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


/**
 * Dec 23, 1999
 * + Extends ParentNodeImpl, removed supportChilds().
 */


package org.openxml.dom;


import org.w3c.dom.*;


/**
 * Implements a lightweight or minimal {@link org.w3c.dom.Document}.
 * Primarily used to carry document parts from one document to another,
 * or to hold impartial documents without maintaining any {@link
 * org.w3c.dom.Document} consistency rules on them.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#DOCUMENT_FRAGMENT_NODE}
 * <LI>Node supports childern
 * <LI>Node name is always "#document-fragment"
 * <LI>Node does not have a value
 * <LI>Special rules apply when adding fragment to other nodes (see
 *  {@link org.w3c.dom.Node#appendChild}).
 * </OL>
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.DocumentFragment
 * @see NodeImpl
 */
final class DocumentFragmentImpl
    extends ParentNodeImpl
    implements DocumentFragment
{
    
    
    public short getNodeType()
    {
        return DOCUMENT_FRAGMENT_NODE;
    }
    
        
    public final void setNodeValue( String value )
    {
        throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
            "This node type does not support values." );
    }

    
    public final Object clone()
    {
        NodeImpl    clone;
        
        clone = new DocumentFragmentImpl( _ownerDocument );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        NodeImpl    clone;
            
        clone = new DocumentFragmentImpl( _ownerDocument );
        cloneInto( clone, deep );
        return clone;
    }

    
    public String toString()
    {
        return "Document Fragment (" + getChildCount() + " nodes)";
    }

    
    /**
     * Constructor requires only document owner.
     */
    DocumentFragmentImpl( DocumentImpl owner )
    {
        super( owner, "#document-fragment", null );
    }


}



