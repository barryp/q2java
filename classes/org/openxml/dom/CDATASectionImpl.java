package org.openxml.dom;

/**
 * org/openxml/dom/CDATASectionImpl.java
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
 * CDATA sections are used to escape blocks of text containing characters that
 * would otherwise be regarded as markup. It is fully implemented by the {@link
 * org.w3c.dom.Text} node type.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#COMMENT_NODE}
 * <LI>Node does not support childern
 * <LI>Node name is always "#comment"
 * </OL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.CDATASection
 * @see TextImpl
 */
final class CDATASectionImpl
	extends TextImpl
	implements CDATASection
{
	
	
	/**
	 * Hidden constructor.
	 * 
	 * @param owner The owner of this document
	 * @param value Initial value or empty string
	 */
	CDATASectionImpl( DocumentImpl owner, String value )
	{
		super( owner, "#cdata-section", value );
	}
	public final Object clone()
	{
		CDATASectionImpl    clone;
		
		clone = new CDATASectionImpl(_ownerDocument, getNodeValue() );
		cloneInto( clone, true );
		return clone;
	}
	public final Node cloneNode( boolean deep )
	{
		CDATASectionImpl    clone;
			
		clone = new CDATASectionImpl( _ownerDocument, getNodeValue() );
		cloneInto( clone, deep );
		return clone;
	}
	public short getNodeType()
	{
		return this.CDATA_SECTION_NODE;
	}
	public String toString()
	{
		String    value;
		
		value = getData();
		if ( value.length() > 64 )
			value = value.substring( 0, 64 ) + "..";
		value = value.replace( '\n', '|' );
		return "CDATA node: [" + value + "]";
	}
}