package org.openxml.dom;

/**
 * org/openxml/dom/NotationImpl.java
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
 * Implements a notation. A notation node merely associates the notation's
 * name with its system and/or public identifiers. The notation has no contents.
 * This node is immutable.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#NOTATION_NODE}
 * <LI>Node does not support childern
 * <LI>Node does not have a value
 * <LI>Node only accessible from {@link org.w3c.dom.DocumentType}
 * </OL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Notation
 * @see NodeImpl
 */
public final class NotationImpl
	extends NodeImpl
	implements Notation
{
	
	
	/**
	 * The system identifier of this notation, if specified.
	 */
	private String        _systemID;
	
	
	/**
	 * The public identifier of this notation, if specified.
	 */
	private String        _publicID;

	
	/**
	 * Constructor requires owner document, notation name and all its attributes.
	 * 
	 * @param owner The owner document
	 * @param name The entity name
	 * @param systemID The system identifier, if specified
	 * @param publicID The public identifier, if specified
	 */
	public NotationImpl( DocumentImpl owner, String name, String systemID, String publicID )
	{
		super( owner, name, null, true );
		if ( _systemID == null && _publicID == null )
			throw new IllegalArgumentException( "Both 'systemID' and 'publicID' are missing." );
		_systemID = systemID;
		_publicID = publicID;
	}
	public final Object clone()
	{
		TextImpl    clone;
		
		clone = new TextImpl( _ownerDocument, getNodeValue() );
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		super.cloneInto( into, deep );
		( (NotationImpl) into )._systemID = _systemID;
		( (NotationImpl) into )._publicID = _publicID;
	}
	public final Node cloneNode( boolean deep )
	{
		TextImpl    clone;
			
		clone = new TextImpl( _ownerDocument, getNodeValue() );
		cloneInto( clone, deep );
		return clone;
	}
	public synchronized boolean equals( Object other )
	{
		NotationImpl    otherX;
		boolean            equal;
		
		// Test for node equality (this covers notation name and all its children)
		// and then test for specific notation qualities. Either both public id's
		// are null, or they are not null and equal. Same thing with system id.
		if ( super.equals( other ) )
		{
			otherX = (NotationImpl) other;
			return ( ( this._publicID == null && otherX._publicID == null ) ||
					 ( this._publicID != null && this._publicID.equals( otherX._publicID ) ) ) &&
				   ( ( this._systemID == null && otherX._systemID == null ) ||
					 ( this._systemID != null && this._systemID.equals( otherX._systemID ) ) );
		}
		return false;
	}
	public short getNodeType()
	{
		return NOTATION_NODE;
	}
	public String getPublicId()
	{
		return _publicID;
	}
	public String getSystemId()
	{
		return _systemID;
	}
	public final void setNodeValue( String value )
	{
		throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
			"This node type does not support values." );
	}
	protected final boolean supportsChildern()
	{
		return false;
	}
	public String toString()
	{
		String    name;
		
		name = getNodeName();
		if ( name.length() > 32 )
			name = name.substring( 0, 32 ) + "..";
		if ( getSystemId() != null )
			name = name + "] SYSTEM [" + getSystemId();
		if ( getPublicId() != null )
			name = name + "] PUBLIC [" + getPublicId();
		return "Notation decl: [" + name + "]";
	}
}