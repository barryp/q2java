package org.openxml.dom;

/**
 * org/openxml/dom/DocumentTypeImpl.java
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


import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.html.HTMLDocument;
import org.openxml.*;
import org.openxml.dom.ext.*;


/**
 * Each document {@link org.w3c.dom.Document#getDoctype()} attribute whose
 * value is either null or an object that represents the document type definition
 * (DTD). HTML documents do not have a DTD. At the moment, this object provides
 * the list of entities that are defined for the document and little else.
 * Access is provided through {@link org.w3c.dom.NamedNodeMap} collections.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#DOCUMENT_TYPE_NODE}
 * <LI>Does not support childern
 * <LI>Node does not have a value
 * <LI>Node does not have parents or siblings and is only accessible from
 *  {@link org.w3c.dom.Document}
 * </OL>
 * <P>
 * The internal implementation also provides access to other elements that are
 * part of the DTD, so full XML documents can be generated and parsed. These
 * extensions are beyond the DOM API and are covered in an extended API.
 * For more information see {@link org.openxml.DTDDocument}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.DocumentType
 * @see org.openxml.DTDDocument
 * @see NodeImpl
 * @see org.w3c.dom.NamedNodeMap
 */
public class DocumentTypeImpl
	extends DocumentImpl
	implements DocumentType, DocumentTypeEx
{
	

	/**
	 * Named node map provides access to an underlying hashtable that holds
	 * all the entities related with this DTD.
	 */
	private NamedNodeMapImpl    _entities;
	

	/**
	 * Named node map provides access to an underlying hashtable that holds
	 * all the notations related with this DTD.
	 */
	private NamedNodeMapImpl    _notations;
	
	
	/**
	 * The system identifier of this entity, if specified.
	 */
	private String            _systemId;
	
	
	/**
	 * The public identifier of this entity, if specified.
	 */
	private String            _publicId;
	
	
	/**
	 * True if the document type has been declared as standalone. Allows the
	 * document to be delivered with its DTD and no external subset.
	 */
	private boolean            _standalone;

	
	/**
	 * Named node map provides access to an underlying hashtable that holds
	 * all the parameter entities related with this DTD.
	 */
	private Hashtable        _params;

	
	/**
	 * Named node map provides access to an underlying hashtable that holds
	 * all the element definitions related with this DTD.
	 */
//    private Hashtable        _elements;


	/**
	 * Named node map provides access to an underlying hashtable that holds
	 * all the attribute definitions related with this DTD.
	 */
//    private Hashtable        _attributes;


	public DocumentTypeImpl( String systemId )
	{
		super( null );
		_ownerDocument = this;
		_standalone = true;
		_systemId = systemId;
	}
	public DocumentTypeImpl( Document owner, String rootElement,
							 boolean standalone, String systemId, String publicId )
	{
		super( rootElement );
		_standalone = standalone;
		_systemId = systemId;
		_publicId = publicId;
	}
	protected NodeImpl castNewChild( Node newChild )
		throws DOMException
	{
		NodeImpl    result;

		// New children can either be an entity, a notation, an element or an
		// attribute definition.
		if ( newChild == null )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child reference is null." );
		if ( ! ( newChild instanceof NodeImpl ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child is not a compatible type for this node." );

		if ( ! ( newChild instanceof EntityImpl || newChild instanceof ParamEntity ||
				 newChild instanceof Notation || newChild instanceof ElementDeclImpl ||
				 newChild instanceof AttlistDecl || newChild instanceof Text ||
				 newChild instanceof Comment || newChild instanceof ProcessingInstruction ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child is not a compatible type for this node." );
		return (NodeImpl) newChild;
	}
	public Object clone()
	{
		DocumentTypeImpl    clone;
		
		clone = new DocumentTypeImpl( _systemId );
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		Hashtable   dictionary;
		Enumeration enum;
		Node        node;
		
		// Use the parent to clone the object. If the clone is shallow, the cloned
		// will contain reference to the same node maps. If the clone is deep,
		// these node maps must be duplicated.
		super.cloneInto( into, deep );
		( (DocumentTypeImpl) into )._standalone = _standalone;
		( (DocumentTypeImpl) into )._systemId = _systemId;
		( (DocumentTypeImpl) into )._publicId = _publicId;
							
		if ( deep )
		{
			// Repeat this for each node map. Create a new dictionary, get an
			// enumeration of the elements in the node map, one by one clone each
			// element and place it in the new dictionary. Create a new node map
			// with that new dictionary and associate it with the clone.
			if ( _entities != null )
			{
				dictionary = new Hashtable();
				enum = _entities.elements();
				while ( enum.hasMoreElements() )
				{
					node = ( (Node) enum.nextElement() ).cloneNode( deep );
					dictionary.put( node.getNodeName(), node );
				}
				( (DocumentTypeImpl) into )._entities = new NamedNodeMapImpl( into, dictionary );
			}

			// Repeat after me...
			if ( _notations != null )
			{
				dictionary = new Hashtable();
				enum = _notations.elements();
				while ( enum.hasMoreElements() )
				{
					node = ( (Node) enum.nextElement() ).cloneNode( deep );
					dictionary.put( node.getNodeName(), node );
				}
				( (DocumentTypeImpl) into )._notations = new NamedNodeMapImpl( into, dictionary );
			}

			// Repeat after me...
			if ( _params != null )
			{
				dictionary = new Hashtable();
				enum = _params.elements();
				while ( enum.hasMoreElements() )
				{
					node = ( (Node) enum.nextElement() ).cloneNode( deep );
					dictionary.put( node.getNodeName(), node );
				}
				( (DocumentTypeImpl) into )._params = dictionary;
			}
		}
		else
		{
			( (DocumentTypeImpl) into )._entities = _entities;
			( (DocumentTypeImpl) into )._notations = _notations;
			( (DocumentTypeImpl) into )._params = _params;
		}
	}
	public Node cloneNode( boolean deep )
	{
		DocumentTypeImpl    clone;
			
		clone = new DocumentTypeImpl( _systemId );
		cloneInto( clone, deep );
		return clone;
	}
	/**
	 * Creates a new internal entity declaration and returns it. The entity value
	 * is given after parameter entity and character reference substitution.
	 * 
	 * @param name The notation name
	 * @param value The entity value
	 * @return Returns a new entity node
	 */
	public EntityImpl createEntity( String name, String value )
	{
		EntityImpl    entity;
		
		entity = new EntityImpl( this, name, value );
		return entity;
	}
	/**
	 * Creates a new external general entity declaration and returns it.
	 * 
	 * @param name The notation name
	 * @param systemID The system identifier
	 * @param publicID The public identifier
	 * @return Returns a new entity node
	 */
	public EntityImpl createEntity( String name, String systemID, String publicID )
	{
		EntityImpl    entity;
		
		entity = new EntityImpl( this, name, systemID, publicID );
		return entity;
	}
	/**
	 * Creates a new unparsed general entity declaration and returns it.
	 * 
	 * @param name The notation name
	 * @param systemID The system identifier
	 * @param publicID The public identifier
	 * @param notation The notation
	 * @return Returns a new entity node
	 */
	public EntityImpl createEntity( String name, String systemID, String publicID, String notation )
	{
		EntityImpl    entity;
		
		entity = new EntityImpl( this, name, systemID, publicID, notation );
		return entity;
	}
/*
	public EntityImpl deleteEntity( String name )
	{
		return (EntityImpl) _entities.removeNamedItem( name );
	}
*/    
	
	/**
	 * Creates a new notation and returns it. Notation must have either or both
	 * system and public identifiers.
	 * 
	 * @param name The notation name
	 * @param systemID The system identifier
	 * @param publicID The public identifier
	 * @return Returns a new notation node
	 */
	public Notation createNotation( String name, String systemID, String publicID )
	{
		Notation    notation;
		
		notation = new NotationImpl( this, name, systemID, publicID );
		return notation;
	}
	/**
	 * Creates a new internal parameter and returns it. The entity value is given
	 * after character reference substitution.
	 * 
	 * @param name The notation name
	 * @param value The entity value
	 * @return Returns a new entity node
	 */
	public ParamEntity createParamEntity( String name, String value )
	{
		ParamEntity    entity;
		
		entity = new ParamEntity( this, name, value );
		return entity;
	}
	/**
	 * Creates a new external parameter entity and returns it.
	 * 
	 * @param name The notation name
	 * @param systemID The system identifier
	 * @param publicID The public identifier
	 * @return Returns a new entity node
	 */
	public ParamEntity createParamEntity( String name, String systemID, String publicID )
	{
		ParamEntity    entity;
		
		entity = new ParamEntity( this, name, systemID, publicID );
		return entity;
	}
	/**
	 * Declares a new general entity declaration. If a general entity with the same
	 * name is already declared, it remains intact and the existing entity is
	 * returned. If no such entity exists, the entity is declared and returned.
	 * 
	 * @param general The general entity to declare
	 * @return The declared entity
	 */
	public EntityImpl declareEntity( EntityImpl general )
	{
		EntityImpl    old;

		isReadOnly();
		if ( _entities == null )
		{
//            _entities = new Hashtable();
			_entities = new NamedNodeMapImpl( this, new Hashtable() );
			old = null;
		}
		else
			old = (EntityImpl) _entities.getNamedItem( general.getNodeName() );
//            old = (EntityImpl) _entities.get( general.getNodeName() );
		if ( old == null )
		{
			_entities.setNamedItem( general );
//            _entities.put( general.getNodeName(), general );
			return general;
		}
		else
			return old;
	}
	/**
	 * Declares a new notation. If a notation with the same name is already
	 * declared, it remains intact and the existing notation is returned.
	 * If no such notation exists, the notation is declared and returned.
	 * 
	 * @param notation The notation to declare
	 * @return The declared notation
	 */
	public Notation declareNotation( Notation notation )
	{
		Notation    old;

		isReadOnly();
		if ( _notations == null )
		{
//            _notations = new Hashtable();
			_notations = new NamedNodeMapImpl( this, new Hashtable() );
			old = null;
		}
		else
//            old = (Notation) _notations.get( notation.getNodeName() );
			old = (Notation) _notations.getNamedItem( notation.getNodeName() );
		if ( old == null )
		{
//            _notations.put( notation.getNodeName(), notation );
			_notations.setNamedItem( notation );
			return notation;
		}
		else
			return old;
	}
	/**
	 * Declares a new parameter entity. If a parameter entity with the same name
	 * is already declared, it remains intact and the existing entity is returned.
	 * If no such entity exists, the entity is declared and returned.
	 * 
	 * @param general The parameter entity to declare
	 * @return The declared entity
	 */
	public ParamEntity declareParamEntity( ParamEntity param )
	{
		ParamEntity    old;

		isReadOnly();
		if ( _params == null )
		{
			_params = new Hashtable();
			old = null;
		}
		else
			old = (ParamEntity) _params.get( param.getNodeName() );
		if ( old == null )
		{
			_params.put( param.getNodeName(), param );
			return param;
		}
		else
			return old;
	}
/*
	public Element createElement( String tagName )
		throws DOMException
	{
		throw new DOMExceptionImpl( DOMException.NOT_SUPPORTED_ERR );
	}
*/    

	public synchronized boolean equals( Object other )
	{
		DocumentTypeImpl    otherX;
		boolean                equal;

		// Use Node's equals method to perform the first tests of equality.
		// If these tests do not pass, return false.
		if ( ! super.equals( other ) )
			return false;

		// Very simple equality test: are the document types equal.
		// There's nothing else about the document to compare.
		synchronized ( other )
		{
			otherX = (DocumentTypeImpl) other;
			equal = ( _standalone == otherX._standalone ) &&
					( ( _systemId == null && otherX._systemId == null ) ||
					  ( _systemId != null && otherX._systemId != null &&
						_systemId.equals( otherX._systemId ) ) &&
					( ( _publicId == null && otherX._publicId == null ) ||
					  ( _publicId != null && otherX._publicId != null &&
						_publicId.equals( otherX._publicId ) ) ) );
			if ( equal )
				equal = ( ( _entities == null && otherX._entities == null ) ||
						  ( _entities != null && otherX._entities != null &&
							_entities.equals( otherX._entities ) ) );
			if ( equal )
				equal = ( ( _notations == null && otherX._notations == null ) ||
						  ( _notations != null && otherX._notations != null &&
							_notations.equals( otherX._notations ) ) );
			if ( equal )
				equal = ( ( _params == null && otherX._params == null ) ||
						  ( _params != null && otherX._params != null &&
							_params.equals( otherX._params ) ) );
		}
		return equal;
	}
	/**
	 * Returns the named general entity declaration if one has been declared.
	 * 
	 * @param name The entity name
	 * @return The named general entity
	 */
	public EntityImpl findEntity( String name )
	{
		if ( _entities == null )
			return null;
//        return (EntityImpl) _entities.get( name );
		return (EntityImpl) _entities.getNamedItem( name );
	}
	/**
	 * Returns the named notation if one has been declared.
	 * 
	 * @param name The notation name
	 * @return The named notation
	 */
	public Notation findNotation( String name )
	{
		if ( _notations == null )
			return null;
//        return (Notation) _notations.get( name );
		return (Notation) _notations.getNamedItem( name );
	}
	/**
	 * Returns the named parameter entity if one has been declared.
	 * 
	 * @param name The entity name
	 * @return The named parameter entity
	 */
	public ParamEntity findParamEntity( String name )
	{
		if ( _params == null )
			return null;
		return (ParamEntity) _params.get( name );
	}
	public Element getDocumentElement()
	{
		return null;
	}
	public NamedNodeMap getEntities()
	{
		return _entities;
	}
	public String getName()
	{
		return getNodeName();
	}
	public short getNodeType()
	{
		return DOCUMENT_TYPE_NODE;
	}
	public NamedNodeMap getNotations()
	{
		return _notations;
	}
	/**
	 * Returns a dictionary of all the parameter entitites declared in this
	 * DTD. If no PEs were declared, null is returned.
	 * 
	 * @return Dictionary of param entities, or null
	 */
	public Dictionary getParamEntities()
	{
		return _params;
	}
	public String getPublicId()
	{
		return _publicId;
	}
	public String getSystemId()
	{
		return _systemId;
	}
	public String internalAsText()
	{
		return null;
	}
	public boolean isStandalone()
	{
		return _standalone;
	}
	public String toString()
	{
		String    name;
		
		name = getName();
		if ( name.length() > 32 )
			name = name.substring( 0, 32 ) + "..";
		name = name.replace( '\n', '|' );
		return "Doctype { " + name + " }";
	}
}