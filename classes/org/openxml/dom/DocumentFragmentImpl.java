package org.openxml.dom;

/**
 * org/openxml/dom/DocumentFragmentImpl.java
 * 
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 * 
 * The Initial Developer of this code under the License is Assaf Arkin.
 * Portions created by Assaf Arkin are Copyright (C) 1998, 1999.
 * All Rights Reserved.
 */


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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.DocumentFragment
 * @see NodeImpl
 */
final class DocumentFragmentImpl
	extends NodeImpl
	implements DocumentFragment
{
	
	
	/**
	 * Constructor requires only document owner.
	 */
	DocumentFragmentImpl( DocumentImpl owner )
	{
		super( owner, "#document-fragment", null, false );
	}
	public final Object clone()
	{
		TextImpl    clone;
		
		clone = new TextImpl( _ownerDocument, getNodeValue() );
		cloneInto( clone, true );
		return clone;
	}
	public final Node cloneNode( boolean deep )
	{
		TextImpl    clone;
			
		clone = new TextImpl( _ownerDocument, getNodeValue() );
		cloneInto( clone, deep );
		return clone;
	}
	public short getNodeType()
	{
		return DOCUMENT_FRAGMENT_NODE;
	}
	public final void setNodeValue( String value )
	{
		throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
			"This node type does not support values." );
	}
	protected boolean supportsChildern()
	{
		return true;
	}
	public String toString()
	{
		return "Document Fragment (" + getChildCount() + " nodes)";
	}
}