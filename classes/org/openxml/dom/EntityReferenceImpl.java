package org.openxml.dom;

/**
 * org/openxml/dom/EntityReferenceImpl.java
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
 * Implements an entity reference. Entity references are read-only when an XML
 * document is parsed, but are modifiable when an XML document is created in
 * memory.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#ENTITY_REFERENCE_NODE}
 * <LI>Node supports childern
 * <LI>Node does not have a value
 * <LI>One of two nodes that may be added to an attribute or an element
 * </OL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.EntityReference
 * @see NodeImpl
 */
final class EntityReferenceImpl
	extends NodeImpl
	implements EntityReference
{
	
	
	/**
	 * Parses an entity reference based on the entities contained in the document.
	 * If an entity with a matching name is found, it is parsed and the textual
	 * value is returned. Otherwise, an empty string is returned.
	 * 
	 * @return Parsed entity reference or empty string
	 */
/*
	synchronized String parseEntity()
	{
		NamedNodeMap    entities;
		Node            node;
		StringBuffer    parsed;
		DocumentType    docType;

		parsed = new StringBuffer();
		// This document supports attribute children as per the XML spec
		docType = getDocument().getDoctype();
		if ( docType != null )
		{
			entities = docType.getEntities();
			node = entities.getNamedItem( getNodeName() );
			if ( node != null )
				synchronized ( node )
				{
					node = node.getFirstChild();
					while ( node != null )
					{
						if ( node instanceof Text )
							parsed.append( ( (Text) node ).getData() );
						else
						if ( node instanceof EntityReference )
							parsed.append( ( (EntityReferenceImpl) node ).parseEntity() );
						node = node.getNextSibling();
					}
				}
		}
		return parsed.toString();
	}
*/
	
	
	/**
	 * Constructor requires only owner document and entity name.
	 * 
	 * @param owner The owner of this document
	 * @param name The entity name
	 */
	EntityReferenceImpl( DocumentImpl owner, String name )
	{
		super( owner, name, null, true );
	}
	public final Object clone()
	{
		EntityReferenceImpl clone;
		
		clone = new EntityReferenceImpl( _ownerDocument, getNodeName() );
		cloneInto( clone, true );
		return clone;
	}
	public final Node cloneNode( boolean deep )
	{
		EntityReferenceImpl clone;
			
		clone = new EntityReferenceImpl( _ownerDocument, getNodeName() );
		cloneInto( clone, deep );
		return clone;
	}
	public short getNodeType()
	{
		return this.ENTITY_REFERENCE_NODE;
	}
	public final void setNodeValue( String value )
	{
		throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
			"This node type does not support values." );
	}
	protected final boolean supportsChildern()
	{
		return true;
	}
	public String toString()
	{
		String    name;
		
		name = getNodeName();
		if ( name.length() > 32 )
			name = name.substring( 0, 32 ) + "..";
		return "Entity ref: [" + name + "]";
	}
}