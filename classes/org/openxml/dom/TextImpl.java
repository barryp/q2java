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


import org.w3c.dom.*;


/**
 * Implements the textual content (termed  character data) of a {@link
 * org.w3c.dom.Element} or {@link org.w3c.dom.Attr}. If there is no markup
 * inside an element's content, the text is contained in a single object
 * implementing the {@link org.w3c.dom.Text} interface; if there is markup,
 * it is parsed into a list of elements and {@link org.w3c.dom.Text} nodes
 * that form the list of children of the element.
 * <P>
 * Notes:
 * <OLl>
 * <LI>Node type is {@link org.w3c.dom.Node#TEXT_NODE}
 * <LI>Node does not support childern
 * <LI>Node name is always "#text"
 * <LI>One of two nodes that may be added to an attribute
 * </OL>
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:24 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Text
 * @see CharacterDataImpl
 */
class TextImpl
    extends CharacterDataImpl
    implements Text
{
    
    
    public short getNodeType()
    {
        return TEXT_NODE;
    }
    
    
    public final synchronized Text splitText( int offset )
        throws DOMException
    {
        Text    next;

        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( offset < 0 || offset > getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "Offest is negative or greater than text size." );
        // Create new Text node, splice the first section and place it in the
	// new text node, then add it before this node. Splice the second
	// section and place it in the existing node. Note that this Text
	// node might not have a parent.
        next = new TextImpl( _ownerDocument, substringData( 0, offset ) );
        if ( getParentNode() != null )
            getParentNode().insertBefore( next, this );
        setData( substringData( offset, getLength() - offset ) );
        return next;
    }
    
    
    public String toString()
    {
        String    value;

        value = getData();
        if ( value.length() > 64 )
            value = value.substring( 0, 64 ) + "..";
        value = value.replace( '\n', '|' );
        return "Text node: [" + value + "]";
    }

  
    public Object clone()
    {
        TextImpl    clone;
        
        clone = new TextImpl( _ownerDocument, getNodeValue() );
        cloneInto( clone, true );
        return clone;
    }

    
    public Node cloneNode( boolean deep )
    {
        TextImpl    clone;
            
        clone = new TextImpl( _ownerDocument, getNodeValue() );
        cloneInto( clone, deep );
        return clone;
    }

    
    /**
     * Constructor requires only owner document and initial text.
     * 
     * @param owner The owner of this document
     * @param text The initial text
     */
    TextImpl( DocumentImpl owner, String text )
    {
        super( owner, "#text", text );
    }
    

    /**
     * Constructor for {@link CDATASectionImpl}.
     */
    TextImpl( DocumentImpl owner, String name, String text )
    {
        super( owner, name, text );
    }
    
    
}
