/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 *
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 *
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


package org.openxml.dom;


import java.util.Vector;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.openxml.parser.DocumentHandlerEx;


/**
 * This is a SAX document handler that is used to build an XML document.
 * It can build a document from any SAX parser, but is specifically tuned
 * for working with the OpenXML XML parser.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 */
public class SAXBuilder
    implements DocumentHandler, DTDHandler, DocumentHandlerEx
{
    
    
    public void startDocument()
        throws SAXException
    {
        if ( ! _done )
            throw new SAXException( "State error: startDocument fired twice on one builder." );
        _document = null;
        _done = false;
    }


    public void endDocument()
        throws SAXException
    {
        if ( _document == null )
            throw new SAXException( "State error: document never started or missing document element." );
        if ( _current != null )
            throw new SAXException( "State error: document ended before end of document element." );
        _current = null;
        _docType = null;
        _done = true;
    }

    
    public void startElement( String tagName, AttributeList attrList )
        throws SAXException
    {
        ElementImpl elem;
        String      namespaceURI = null;
        int         i;
        
        if ( tagName == null )
            throw new SAXException( "Argument 'tagName' is null." );
        
        // If this is the root element, this is the time to create a new document,
        // because only know we know the document element name and namespace URI.
        if ( _document == null ) {
            // Get the namespace URI from the optional attribute of the root element.
            if ( _namespaceSupport )
                namespaceURI = getNamespaceURI( tagName, attrList );
            // Create the document with that namespace URI and document element.
            // No need to create the element explicitly.
            // DocType is made read-only before use in document and reference is
            // made null to prevent further use.
            if ( _docType != null )
                _docType.makeReadOnly();
            _document = createDocument( namespaceURI, tagName, _docType );
            _docType = null;
            elem = (ElementImpl) _document.getDocumentElement();
            _current = elem;
            if ( _current == null )
                throw new SAXException( "State error: Document.getDocumentElement returns null." );
            
            if ( _preRootNodes != null ) {
                for ( i = _preRootNodes.size() ; i-- > 0 ; )
                    _document.insertBefore( (Node) _preRootNodes.elementAt( i ), elem );
                _preRootNodes = null;
            }
            
        } else {
            // This is a state error, indicates that document has been parsed in full,
            // or that there are two root elements.
            if ( _current == null )
                throw new SAXException( "State error: startElement called after end of document element." );
            if ( _namespaceSupport )
                namespaceURI = getNamespaceURI( tagName, attrList );
            elem = (ElementImpl) _document.createElementNS( namespaceURI, tagName );
            _current.appendNewChild( elem );
            _current = elem;
        }
        
        // Add the attributes (specified and not-specified) to this element.
        if ( attrList != null ) {
            for ( i = 0 ; i < attrList.getLength() ; ++ i ) {
                if ( _namespaceSupport )
                    namespaceURI = getNamespaceURI( attrList.getName( i ), attrList );
                else
                    namespaceURI = null;
                elem.addNewAttribute( namespaceURI, attrList.getName( i ), attrList.getValue( i ) );
            }
        }
        /* Used for fancy error reporting, where a DOM object would know
         * which line of code it originated from. Currently not supported.
         if ( _locatorInDOM && _locator != null )
         elem.setLocator( new LocatorImpl( _locator ) );
        */
    }

    
    public void endElement( String tagName )
        throws SAXException
    {
        
        if ( _current == null )
            throw new SAXException( "State error: endElement called with no current node." );
        if ( ! _current.getNodeName().equals( tagName ) )
            throw new SAXException( "State error: mismatch in closing tag name " + tagName );
        
        // Move up to the parent element. When you reach the top (closing the root element).
        // the parent is document and current is null.
        if ( _current.getParentNode() == _current.getOwnerDocument() )
            _current = null;
        else
            _current = (ElementImpl) _current.getParentNode();
    }

    
    public void setDocumentLocator( Locator locator )
    {
        _locator = locator;
    }


    public void characters( String text )
        throws SAXException
    {
        if ( _current == null )
            throw new SAXException( "State error: character data found outside of root element." );
        _current.appendNewChild( new TextImpl( _document, text ) );
    }

    
    public void characters( char[] text, int start, int length )
        throws SAXException
    {
        if ( _current == null )
            throw new SAXException( "State error: character data found outside of root element." );
        _current.appendNewChild( new TextImpl( _document, new String( text, start, length ) ) );
    }
    
    
    public void ignorableWhitespace( char[] text, int start, int length )
        throws SAXException
    {
        Node    node;
        
        if ( ! _ignoreWhitespace )
            _current.appendNewChild( new TextImpl( _document, new String( text, start, length ) ) );
     }
    
    
    public void processingInstruction( String target, String instruction )
        throws SAXException
    {
        Node    node;
        
        // Processing instruction may appear before the document element (in fact, before the
        // document has been created, or after the document element has been closed.
        if ( _current == null && _document == null ) {
            if ( _preRootNodes == null )
                _preRootNodes = new Vector();
            _preRootNodes.addElement( new ProcessingInstructionImpl( null, target, instruction ) );
        } else if ( _current == null && _document != null )
            _document.appendNewChild( new ProcessingInstructionImpl( _document, target, instruction ) );
        else
            _current.appendNewChild( new ProcessingInstructionImpl( _document, target, instruction ) );
    }
    
    
    public void comment( String text )
        throws SAXException
    {
        Node    node;
        
        // Processing instruction may appear before the document element (in fact, before the
        // document has been created, or after the document element has been closed.
        if ( _current == null && _document == null ) {
            if ( _preRootNodes == null )
                _preRootNodes = new Vector();
            _preRootNodes.addElement( new CommentImpl( null, text ) );
        } else {
            if ( _current == null )
                _document.appendNewChild( new CommentImpl( _document, text ) );
            else
                _current.appendNewChild( new CommentImpl( _document, text ) );
        }
    }

    
    public void cdataSection( String text )
        throws SAXException
    {
        Node    node;
        
        if ( _current == null )
            throw new SAXException( "State error: character data found outside of root element." );
        _current.appendNewChild( new CDATASectionImpl( _document, text ) );
    }

    
    public void entityReference( String text )
        throws SAXException
    {
        Node    node;
        
        if ( _current == null )
            throw new SAXException( "State error: character data found outside of root element." );
        _current.appendNewChild( new EntityReferenceImpl( _document, text ) );
    }

    
    public void setDocumentType( String qualifiedName, String publicId, String systemId )
        throws SAXException
    {
        if ( _docType != null )
            throw new SAXException( "State error: document type declared twice for document." );
        _docType = new DocumentTypeImpl( qualifiedName, systemId, publicId, null );
    }


    public void notationDecl( String name, String publicId, String systemId )
        throws SAXException
    {
        if ( _docType == null )
            throw new SAXException( "State error: document type not declared for document, can't add notation." );
        _docType.declareNotation( _docType.createNotation( name, systemId, publicId ) );
    }


    public void unparsedEntityDecl( String name, String publicId,
                                    String systemId, String notation )
        throws SAXException
    {
        if ( _docType == null )
            throw new SAXException( "State error: document type not declared for document, can't add entity." );
        _docType.declareEntity( _docType.createEntity( name, systemId, publicId, notation ) );
    }

        
    public Document getDocument()
    {
        return _document;
    }


    protected String getNamespaceURI( String qualifiedName, AttributeList attrList )
    {
        String      attrName;
        String      namespaceURI;
        ElementImpl elem;
        int         index;
        
        // First, let's see if there is a prefix.
        index = qualifiedName.indexOf( ":" );
        if ( index > 0 )
            attrName = "xmlns:" + qualifiedName.substring( 0, index );
        else
            attrName = "xmlns";
        
        // Now, search for namespace association in the attribute list.
        if ( attrList != null ) {
            namespaceURI = attrList.getValue( attrName );
            if ( namespaceURI != null && namespaceURI.length() > 0 )
                return namespaceURI;
        }
        
        // Now, search for namespace associated up the tree.
        elem = _current;
        while ( elem != null ){
            namespaceURI = elem.getAttribute( attrName );
            if ( namespaceURI != null && namespaceURI.length() > 0 )
                return namespaceURI;
            if ( elem.getParentNode() == null || ! ( elem.getParentNode() instanceof Element ) )
                elem = null;
            else
                elem = (ElementImpl) elem.getParentNode();
        }
        return null;
    }


    protected DocumentImpl createDocument( String namespaceURI, String tagName, DocumentType docType )
    {
        return new DocumentImpl( namespaceURI, tagName, docType );
    }


    public SAXBuilder( boolean ignoreWhitespace )
    {
        _ignoreWhitespace = ignoreWhitespace;
    }


    public SAXBuilder()
    {
    }
    
    
    /**
     * The document that is being built.
     */
    protected DocumentImpl      _document;
    
    
    /**
     * The current node in the document into which elements, text and
     * other nodes will be inserted. This starts as the document iself
     * and reflects each element that is currently being parsed.
     */
    protected ElementImpl      _current;
    
    /**
     * A reference to the current locator, this is generally the parser
     * itself. The locator is used to locate errors and identify the
     * source locations of elements.
     */
    private Locator         _locator;


    /**
     * Applies only to whitespace appearing between element tags in element content,
     * as per the SAX definition, and true by default.
     */
    private boolean         _ignoreWhitespace = true;


    /**
     * Indicates whether finished building a document. If so, can start building
     * another document. Must be initially true to get the first document processed.
     */
    private boolean         _done = true;


    /**
     * The document type definition to be associated with this document.
     */
    private DocumentTypeImpl _docType;


    /**    
     * The document is only created the same time as the document element, however, certain
     * nodes may precede the document element (comment and PI), and they are accumulated
     * in this vector.
     */
    protected Vector         _preRootNodes;


    private boolean          _namespaceSupport;


}
