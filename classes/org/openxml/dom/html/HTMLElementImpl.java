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


package org.openxml.dom.html;


import org.openxml.dom.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;



/**
 * Implements an HTML-specific element, an {@link org.w3c.dom.Element} that
 * will only appear inside HTML documents. This element extends {@link
 * org.openxml.dom.ElementImpl} by adding methods for directly manipulating
 * HTML-specific attributes. All HTML elements gain access to the
 * <code>id</code>,  <code>title</code>, <code>lang</code>, <code>dir</code>
 * and <code>class</code> attributes. Other elements add their own specific
 * attributes.
 * <P>
 * Note that some support is provided by {@link org.openxml.dom.NodeImpl}
 * directly: translating all tag names to upper case and all attribute names
 * to lower case.
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:04 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLElement
 * @see org.openxml.dom.ElementImpl
 */
public class HTMLElementImpl
    extends ElementImpl
    implements HTMLElement
{


    public String getId()
    {
        return getAttribute( "id" );
    }
    
    
    public void setId( String id )
    {
        setAttribute( "id", id );
    }
    
    
    public String getTitle()
    {
        return getAttribute( "title" );
    }
    
    
    public void setTitle( String title )
    {
        setAttribute( "title", title );
    }
    
    
    public String getLang()
    {
        return getAttribute( "lang" );
    }
    
    
    public void setLang( String lang )
    {
        setAttribute( "lang", lang );
    }
    
    
    public String getDir()
    {
        return getAttribute( "dir" );
    }
    
    
    public void setDir( String dir )
    {
        setAttribute( "dir", dir );
    }

    
    public String getClassName()
    {
        return getAttribute( "class" );
    }

    
    public void setClassName( String className )
    {
        setAttribute( "class", className );
    }
    
    
    /**
     * Convenience method used to translate an attribute value into an integer
     * value. Returns the integer value or zero if the attribute is not a
     * valid numeric string.
     * 
     * @param value The value of the attribute
     * @return The integer value, or zero if not a valid numeric string
     */
    int getInteger( String value )
    {
        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException except )
        {
            return 0;
        }
    }
    
    
    /**
     * Convenience method used to translate an attribute value into a boolean
     * value. If the attribute has an associated value (even an empty string),
     * it is set and true is returned. If the attribute does not exist, false
     * is returend.
     * 
     * @param value The value of the attribute
     * @return True or false depending on whether the attribute has been set
     */
    boolean getBinary( String name )
    {
        return ( getAttributeNode( name ) != null );
    }
    
    
    /**
     * Convenience method used to set a boolean attribute. If the value is true,
     * the attribute is set to an empty string. If the value is false, the attribute
     * is removed. HTML 4.0 understands empty strings as set attributes.
     * 
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    void setAttribute( String name, boolean value )
    {
        if ( value )
            setAttribute( name, name );
        else
            removeAttribute( name );
    }
    
    
    public Attr getAttributeNode( String attrName )
    {
	return super.getAttributeNodeNS( null, attrName.toLowerCase() );
    }


    public Attr getAttributeNodeNS( String namespaceURI,
				    String localName )
    {
	if ( namespaceURI != null && namespaceURI.length() > 0 )
	    return super.getAttributeNodeNS( namespaceURI, localName );
	else
	    return super.getAttributeNodeNS( null, localName.toLowerCase() );
    }
    
    
    public String getAttribute( String attrName )
    {
	return super.getAttributeNS( null, attrName.toLowerCase() );
    }


    public String getAttributeNS( String namespaceURI,
				  String localName )
    {
	if ( namespaceURI != null && namespaceURI.length() > 0 )
	    return super.getAttributeNS( namespaceURI, localName );
	else
	    return super.getAttributeNS( null, localName.toLowerCase() );
    }


    public final NodeList getElementsByTagName( String tagName )
    {
	return super.getElementsByTagNameNS( null, tagName.toUpperCase() );
    }


    public final NodeList getElementsByTagNameNS( String namespaceURI,
					          String localName )
    {
	if ( namespaceURI != null && namespaceURI.length() > 0 )
	    return super.getElementsByTagNameNS( namespaceURI, localName.toUpperCase() );
	else
	    return super.getElementsByTagNameNS( null, localName.toUpperCase() );
    } 


    /**
     * Convenience method used to capitalize a one-off attribute value before it
     * is returned. For example, the align values "LEFT" and "left" will both
     * return as "Left".
     * 
     * @param value The value of the attribute
     * @return The capitalized value
     */
    String capitalize( String value )
    {
        char[]    chars;
        int        i;
        
        // Convert string to charactares. Convert the first one to upper case,
        // the other characters to lower case, and return the converted string.
        chars = value.toCharArray();
        if ( chars.length > 0 )
        {
            chars[ 0 ] = Character.toUpperCase( chars[ 0 ] );
            for ( i = 1 ; i < chars.length ; ++i )
                chars[ i ] = Character.toLowerCase( chars[ i ] );
            return String.valueOf( chars );
        }
        return value;
    }
    

    /**
     * Convenience method used to capitalize a one-off attribute value before it
     * is returned. For example, the align values "LEFT" and "left" will both
     * return as "Left".
     * 
     * @param name The name of the attribute
     * @return The capitalized value
     */
    String getCapitalized( String name )
    {
        String    value;
        char[]    chars;
        int        i;
        
        value = getAttribute( name );
        if ( value != null )
        {
            // Convert string to charactares. Convert the first one to upper case,
            // the other characters to lower case, and return the converted string.
            chars = value.toCharArray();
            if ( chars.length > 0 )
            {
                chars[ 0 ] = Character.toUpperCase( chars[ 0 ] );
                for ( i = 1 ; i < chars.length ; ++i )
                    chars[ i ] = Character.toLowerCase( chars[ i ] );
                return String.valueOf( chars );
            }
        }
        return value;
    }

    
    /**
     * Convenience method returns the form in which this form element is contained.
     * This method is exposed for form elements through the DOM API, but other
     * elements have no access to it through the API.
     */
    public HTMLFormElement getForm()
    {
        Node    parent;
        
        parent = getParentNode(); 
        while ( parent != null )
        {
            if ( parent instanceof HTMLFormElement )
                return (HTMLFormElement) parent;
            parent = parent.getParentNode();
        }
        return null;
    }

        
    protected NodeImpl castNewChild( Node newChild )
        throws DOMException
    {
        // Same method appears in HTMLElementImpl and HTMLDocumentImpl.
        if ( newChild == null )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                "Child reference is null." );
        if ( ! ( newChild instanceof NodeImpl ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
					"Child is not a compatible type for this node." );

        // newChild must be HTMLElement, Text, Comment, DocumentFragment or
        // ProcessingInstruction. CDATASection and EntityReference not supported
        // in HTML documents.
        if ( ! ( newChild instanceof ElementImpl ||
                 newChild instanceof Comment ||
                 newChild instanceof Text ||
                 newChild instanceof DocumentFragment ||
                 newChild instanceof ProcessingInstruction ) )
            throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
                "Child is not a compatible type for this node." );
        return (NodeImpl) newChild;
    }


    public final Object clone()
    {
        ElementImpl clone;
        
	clone = (ElementImpl) getOwnerDocument().createElement( getNodeName() );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        ElementImpl clone;
            
	clone = (ElementImpl) getOwnerDocument().createElement( getNodeName() );
        cloneInto( clone, deep );
        return clone;
    }

    
    /**
     * Constructor required owner document and element tag name. Will be called
     * by the constructor of specific element types but with a known tag name.
     * Assures that the owner document is an HTML element.
     * 
     * @param owner The owner HTML document
     * @param tagName The element's tag name
     */
    HTMLElementImpl( HTMLDocumentImpl owner, String tagName )
    {
        super( owner, null, tagName.toUpperCase() );
    }


    /*
    static class NodeListWrapper
	implements NodeList
    {


	private NodeIterator _iterator;


	private int          _index = 0;


	NodeListWrapper( NodeIterator iterator )
	{
	    _iterator = iterator;
	}


	public synchronized Node item( int index )
	{
	    Node    node;
	    
	    if ( index < 0 )
		return null;
	    node = null;
	    if ( index == _index ) {
		previousNode();
		node = nextNode();
	    } else {
		while ( _index < index ) {
		    node = nextNode();
		    ++_index;
		}
		while ( _index > index ) {
		    node = previousNode();
		    --_index;
		}
	    }
	    return node;
	}
    
    
	public synchronized int getLength()
	{
	    int count;

	    while ( previousNode() != null );
	    for ( count = 0 ; nextNode() != null ; ++count )
		; // Empty statement
	    _index = count;
	    return count;
	}


    }
    */
    
    
}

