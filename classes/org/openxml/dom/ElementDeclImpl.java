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
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see NodeImpl
 */
public class ElementDeclImpl
    extends NodeImpl
    implements Node
{
    
    
    public short getNodeType()
    {
        return ELEMENT_DECL_NODE;
    }
    
    
    public String getName()
    {
        return getNodeName();
    }
    
    
    public synchronized boolean equals( Object other )
    {
/*        EntityImpl    otherX;
        
        // Test for node equality (this covers entity name and all its children)
        // and then test for specific entity qualities.
        if ( super.equals( other ) )
        {
            otherX = (EntityImpl) other;
            return ( this._publicId.equals( otherX._publicId ) &&
                     this._systemId.equals( otherX._systemId ) &&
                     this._notation.equals( otherX._notation ) );
        }
*/        return false;
    }
    
    
    /**
     * Returns true if element is empty. An empty element cannot contain any
     * children and must be specified with an empty tag, or an opening tag
     * immediately followed by a closing tag.
     * 
     * @return True if element is empty
     */
    public boolean isEmpty()
    {
        return _empty;
    }
    
    
    /**
     * Returns true if element may contain any child elements and character data
     * in any order.
     * 
     * @return True if element supports any contents
     */
    public boolean isAny()
    {
        return _any;
    }
    
    
    /**
     * Returns true if element contains mixed contents. Mixed contents includes
     * both elements and character data in any particular order. This option
     * implies that both {@link #isEmpty} and {@link #isAny} return false.
     * If all three are false, then contents is subject to exact order.
     * 
     * @return True if element contains mixed contents
     */
    public boolean isMixed()
    {
        return _mixed;
    }

    
    /**
     * Returns true if the opening tag is optional. Even if the opening tag is
     * missing from the document for this element, the document is still valid.
     * This option only relates to HTML document, and implies that {@link
     * #requiresClosingTag} is also true.
     * 
     * @return True if opening tag is optional
     */
    public boolean requiresOpeningTag()
    {
        return _optionalOpen;
    }

    
    /**
     * Returns true if the closing tag is optional. Even if the closing tag is
     * missing from the document for this element, the document is still valid.
     * This option only relates to HTML document.
     * 
     * @return True if closing tag is optional
     */
    public boolean requiresClosingTag()
    {
        return _optionalClose;
    }

    
    public final Object clone()
    {
        ElementDeclImpl clone;
        
        clone = new ElementDeclImpl( _ownerDocument, getNodeName() );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        ElementDeclImpl clone;
            
        clone = new ElementDeclImpl( _ownerDocument, getNodeName() );
        cloneInto( clone, deep );
        return clone;
    }

    
    /**
     * Constructor requires owner document, element name and its definition.
     * 
     * @param owner The owner document
     * @param name The element name
     * @param definition The element definition
     */
    ElementDeclImpl( DocumentImpl owner, String name, String definition )
    {
        this( owner, name, definition, false, false );
    }
    
    
    /**
     * Constructor requires owner document, element name and its definition.
     * The flags for optional opening and closing tag are supported only for
     * HTML documents.
     * 
     * @param owner The owner document
     * @param name The element name
     * @param definition The element definition
     * @param optionalOpen The opening tag is optional
     * @param optionalClose The closing tag is optional
     */
    ElementDeclImpl( DocumentImpl owner, String name, String definition,
                 boolean optionalOpen, boolean optionalClose )
    {
        super( owner, name, null );
        _optionalOpen = optionalOpen;
        if ( _optionalOpen )
            _optionalClose = true;
        else
            _optionalClose = optionalClose;
        if ( definition.equals( "EMPTY" ) )
            _empty = true;
        else
        if ( definition.equals( "ANY" ) )
            _any = true;
        else
        { 
        }
    }
    
    
    private ElementDeclImpl( DocumentImpl owner, String name )
    {
        super( owner, name, null );
    }

        
    /**
     * Indicates that the opening tag is optional: even if missing from the
     * document, the document is still valid. Applies only to HTML documents.
     * Also implies that {@link #_optionalClose} is true.
     */
    private boolean        _optionalOpen;
    
    
    /**
     * Indicates that the closing tag is optional: even if missing from the
     * document, the document is still valid. Applies only to HTML documents.
     */
    private boolean        _optionalClose;
    
    
    /**
     * Indicates that the element is empty.
     */
    private boolean        _empty;
    

    /**
     * Indicates that the element can contain any child elements of any type
     * and any order.
     */
    private boolean        _any;


    /**
     * Indicates that the element contains mixed contents. Mixed contents
     * includes both elements and character data in any particular order.
     */
    private boolean        _mixed;


}
