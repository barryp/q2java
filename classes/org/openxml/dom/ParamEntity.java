package org.openxml.dom;

/**
 * org/openxml/dom/ParamEntity.java
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


import java.io.*;
import org.w3c.dom.*;


/**
 * Implements a parameter entity.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link NodeImpl#PARAM_ENTITY_NODE}
 * <LI>Node supports childern
 * <LI>Node does not have a value
 * <LI>Node only accessible from {@link org.w3c.dom.DocumentType}
 * </OL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NodeImpl
 */
public class ParamEntity
	extends NodeImpl
	implements Node
{
	
	
	/**
	 * The system identifier of this entity, if specified.
	 */
	private String        _systemId;
	
	
	/**
	 * The public identifier of this entity, if specified.
	 */
	private String        _publicId;
	
	
	/**
	 * Identifies the state of this entity as yet to be parsed, being parsed,
	 * has been parsed, or cannot be found.
	 */
	private short        _state;
	
	
	/**
	 * Holds the internal value of the entity.
	 */
	private String        _internalValue;

	
	/**
	 * Entity has been declared but not parsed. This is the initial state for
	 * an entity after it has been declared in the DTD but before it is used
	 * in the document contents.
	 */
	public static final short    STATE_DECLARED = 0;


	/**
	 * Entity is being parsed. This state designates that the entity is being
	 * parsed right now. State is used to identify circular references.
	 */
	public static final short    STATE_PARSING = 1;
	
	
	/**
	 * Entity has been parsed. This state indicates that entity has been parsed
	 * and it's parsed contents is contained in its child nodes.
	 */
	public static final short    STATE_PARSED = 2;
	
	
	/**
	 * Entity not found. The entity could not be parsed before.
	 */
	public static final short    STATE_NOT_FOUND = 3;


	private ParamEntity( DocumentImpl owner, String name )
	{
		super( owner, name, null, true );
	}
	/**
	 * Constructs an internal parameter entity. Entity value must be provided.
	 * 
	 * @param owner The owner document
	 * @param name The entity name
	 * @param internalValue The unparsed entity value
	 */
	public ParamEntity( DocumentImpl owner, String name, String internalValue )
	{
		super( owner, name, null, true );
		if ( internalValue == null )
			throw new NullPointerException( "Argument 'internalValue' cannot be null." );
		_systemId = null;
		_publicId = null;
		_state = STATE_DECLARED;
		_internalValue = internalValue;
	}
	/**
	 * Constructs an external parameter entity. Entity system identifier must be
	 * provided, public identifier is optional.
	 * 
	 * @param owner The owner document
	 * @param name The entity name
	 * @param systemId The system identifier
	 * @param publicId The public identifier, if specified
	 */
	public ParamEntity( DocumentImpl owner, String name, String systemId, String publicId )
	{
		super( owner, name, null, true );
		if ( systemId == null )
			throw new NullPointerException( "Argument 'systemId' cannot be null." );
		_systemId = systemId;
		_publicId = publicId;
		_state = STATE_DECLARED;
	}
	public final Object clone()
	{
		ParamEntity clone;
		
		clone = new ParamEntity( _ownerDocument, getNodeName() );
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		super.cloneInto( into, deep );
		( (ParamEntity) into )._systemId = this._systemId;
		( (ParamEntity) into )._publicId = this._publicId;
		( (ParamEntity) into )._internalValue = this._internalValue;
		( (ParamEntity) into )._state = this._state;
	}
	public final Node cloneNode( boolean deep )
	{
		ParamEntity clone;
			
		clone = new ParamEntity( _ownerDocument, getNodeName() );
		cloneInto( clone, deep );
		return clone;
	}
	public synchronized boolean equals( Object other )
	{
		ParamEntity    otherX;
		
		// Test for node equality (this covers entity name and all its children)
		// and then test for specific entity qualities.
		if ( super.equals( other ) )
		{
			otherX = (ParamEntity) other;
			// If this entity is internal, both entities must be internal and have
			// equal internal value.
			if ( this.isInternal() )
				return ( otherX.isInternal() &&
						 this.getInternal().equals( otherX.getInternal() ) );
			// External or unparsed: either public id are both null, or public id
			// equals in both (and same for system id).
			return ( ( this._publicId == null && otherX._publicId == null ) ||
					 ( this._publicId != null && this._publicId.equals( otherX._publicId ) ) ) &&
				   ( ( this._systemId == null && otherX._systemId == null ) ||
					 ( this._systemId != null && this._systemId.equals( otherX._systemId ) ) );
		}
		return false;
	}
	public final String getInternal()
	{
		return _internalValue;
	}
	public short getNodeType()
	{
		return PARAM_ENTITY_NODE;
	}
	public String getPublicId()
	{
		return _publicId;
	}
	/**
	 * Returns the parsing state of this entity.
	 * 
	 * @return State of entity
	 */
	public short getState()
	{
		return _state;
	}
	public String getSystemId()
	{
		return _systemId;
	}
	/**
	 * Returns true if entity is an internal entity. An internal entity is one
	 * for which a value has been defined. An external entity is one for which
	 * an external entity has been assigned through either system or public
	 * identifiers.
	 * 
	 * @return True if internal entity
	 */
	public boolean isInternal()
	{
		return ( _internalValue  != null );
	}
	/**
	 * Changes the parsing state of this entity. Note that only some changes
	 * are allowed: from declared to parsing, parsed or not found; from parsing
	 * to parsed or not found; from not found to declared.
	 * 
	 * @param newState New state of entity
	 */
	public void setState( short newState )
	{
		if ( ( _state == STATE_DECLARED && newState == STATE_PARSING ) ||
			 ( _state == STATE_NOT_FOUND && newState == STATE_DECLARED ) )
			_state = newState;
		else
		if ( ( _state == STATE_DECLARED || _state == STATE_PARSING ) &&
			 ( newState == STATE_PARSED || newState == STATE_NOT_FOUND ) )
			_state = newState;
		else
			throw new IllegalStateException( "Cannot switch from state " + _state +
											 " to state " + newState + "." );
	}
	public String toString()
	{
		String    name;
		String    value;
		
		name = getNodeName();
		if ( name.length() > 32 )
			name = name.substring( 0, 32 ) + "..";
		if ( isInternal() )
		{
			value = getInternal();
			if ( value.length() > 64 )
				value = value.substring( 0, 64 ) + "..";
			name = name + "] [" + value;
		}
		else
		{
			if ( getSystemId() != null )
				name = name + "] SYSTEM [" + getSystemId();
			if ( getPublicId() != null )
				name = name + "] PUBLIC [" + getPublicId(); 
		}
		return "PEntity decl: [" + name + "]";
	}
}