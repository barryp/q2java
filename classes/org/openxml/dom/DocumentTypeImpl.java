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
 * - No longer extends DocumentImpl, now extends NodeImpl. Removed support
 *   for child nodes.
 * + Support for setting system and public identifier in constructor and
 *   retrieving with method calls.
 * - Removed redundant standalone attribute.
 * + Fixed clone() and equals().
 */

package org.openxml.dom;


import java.util.*;
import org.w3c.dom.*;


/**
 * Each document {@link Document#getDoctype()} attribute whose value is either
 * null or an object that represents the document type definition (DTD).
 * HTML documents do not have a DTD. At the moment, this object provides
 * the list of entities that are defined for the document and little else.
 * Access is provided through {@link NamedNodeMap} collections.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link Node#DOCUMENT_TYPE_NODE}
 * <LI>Does not support childern
 * <LI>Node does not have a value
 * <LI>Node does not have parents or siblings and is only accessible from
 *  {@link org.w3c.dom.Document}
 * </OL>
 * <P>
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see DocumentType
 * @see NodeImpl
 * @see NamedNodeMap
 */
public class DocumentTypeImpl
    extends NodeImpl
    implements DocumentType
{
    

    public short getNodeType()
    {
        return DOCUMENT_TYPE_NODE;
    }
    
    
    public String getName()
    {
        return getNodeName();
    }

        
    public synchronized NamedNodeMap getEntities()
    {
        if ( _entities == null )
            _entities = new NamedNodeMapImpl( this, new Hashtable() );
        return _entities;
    }
    
    
    public synchronized NamedNodeMap getNotations()
    {
        if ( _notations == null )
            _notations = new NamedNodeMapImpl( this, new Hashtable() );
        return _notations;
    }
    
        
    public String getPublicId()
    {
        return _publicId;
    }
    
    
    public String getSystemId()
    {
        return _systemId;
    }


    public String getInternalSubset()
    {
        return _internalSubset;
    }
    
    /**
     * Creates a new external general entity declaration and returns it.
     * 
     * @param name The notation name
     * @param systemId The system identifier
     * @param publicId The public identifier
     * @return Returns a new entity node
     */
    public EntityImpl createEntity( String name, String systemId,
                                    String publicId )
    {
        EntityImpl    entity;
        
        NodeImpl.checkName( name );
        entity = new EntityImpl( name, systemId, publicId );
        return entity;
    }


    /**
     * Creates a new unparsed general entity declaration and returns it.
     * 
     * @param name The notation name
     * @param systemId The system identifier
     * @param publicId The public identifier
     * @param notation The notation
     * @return Returns a new entity node
     */
    public EntityImpl createEntity( String name, String systemId,
                                    String publicId, String notation )
    {
        EntityImpl    entity;
        
        NodeImpl.checkName( name );
        NodeImpl.checkName( notation );
        entity = new EntityImpl( name, systemId, publicId, notation );
        return entity;
    }
    
    
    /**
     * Creates a new internal entity declaration and returns it. The entity
     * value is given after parameter entity and character reference
     * substitution.
     * 
     * @param name The notation name
     * @param value The entity value
     * @return Returns a new entity node
     */
    public EntityImpl createEntity( String name, String value )
    {
        EntityImpl    entity;
        
        NodeImpl.checkName( name );
        entity = new EntityImpl( name, value );
        return entity;
    }

        
    /**
     * Declares a new general entity declaration. If a general entity with the
     * same name is already declared, it remains intact and the existing
     * entity is returned. If no such entity exists, the entity is declared
     * and returned.
     * 
     * @param general The general entity to declare
     * @return The declared entity
     */
    public synchronized EntityImpl declareEntity( EntityImpl general )
    {
        EntityImpl    old;
        
        isReadOnly();
        if ( _entities == null ) {
            //            _entities = new Hashtable();
            _entities = new NamedNodeMapImpl( this, new Hashtable() );
            old = null;
        } else
            old = (EntityImpl) _entities.getNamedItem( general.getNodeName() );
        //            old = (EntityImpl) _entities.get( general.getNodeName() );
        if ( old == null ) {
            _entities.setNamedItem( general );
            //            _entities.put( general.getNodeName(), general );
            return general;
        } else
            return old;
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
     * @param systemId The system identifier
     * @param publicId The public identifier
     * @return Returns a new notation node
     */
    public Notation createNotation( String name, String systemId,
                                    String publicId )
    {
        Notation    notation;
        
        NodeImpl.checkName( name );
        notation = new NotationImpl( name, systemId, publicId );
        return notation;
    }
    
    
    /**
     * Declares a new notation. If a notation with the same name is already
     * declared, it remains intact and the existing notation is returned.
     * If no such notation exists, the notation is declared and returned.
     * 
     * @param notation The notation to declare
     * @return The declared notation
     */
    public synchronized Notation declareNotation( Notation notation )
    {
        Notation    old;
        
        isReadOnly();
        if ( _notations == null ) {
            //            _notations = new Hashtable();
            _notations = new NamedNodeMapImpl( this, new Hashtable() );
            old = null;
        } else
            //            old = (Notation) _notations.get( notation.getNodeName() );
            old = (Notation) _notations.getNamedItem( notation.getNodeName() );
        if ( old == null )  {
            //            _notations.put( notation.getNodeName(), notation );
            _notations.setNamedItem( notation );
            return notation;
        } else
            return old;
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
        // return (Notation) _notations.get( name );
        return (Notation) _notations.getNamedItem( name );
    }
    
    
    public synchronized boolean equals( Object other )
    {
        DocumentTypeImpl    otherX;
        boolean             equal;
        
        // Use Node's equals method to perform the first tests of equality.
        // If these tests do not pass, return false.
        if ( ! super.equals( other ) )
            return false;
        
        // Very simple equality test: are the document types equal.
        // There's nothing else about the document to compare.
        synchronized ( other ) {
            otherX = (DocumentTypeImpl) other;
            equal = ( ( _systemId == null && otherX._systemId == null ) ||
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
        }
        return equal;
    }

    
    public Object clone()
    {
        DocumentTypeImpl    clone;
        
        clone = new DocumentTypeImpl( getName(), getSystemId(), getPublicId(), getInternalSubset() );
        cloneInto( clone, true );
        return clone;
    }

    
    public Node cloneNode( boolean deep )
    {
        DocumentTypeImpl    clone;
        
        clone = new DocumentTypeImpl( getName(), getSystemId(), getPublicId(), getInternalSubset() );
        cloneInto( clone, deep );
        return clone;
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
    
    
    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        Hashtable   dictionary;
        Enumeration enum;
        Node        node;
        
        // Use the parent to clone the object. If the clone is shallow,
        // the cloned will contain reference to the same node maps. If the
        // clone is deep, these node maps must be duplicated.
        super.cloneInto( into, deep );
        
        if ( deep ) {
            // Repeat this for each node map. Create a new dictionary, get an
            // enumeration of the elements in the node map, one by one clone
            // each element and place it in the new dictionary. Create a new
            // node map with that new dictionary and associate it with the
            // clone.
            if ( _entities != null ) {
                dictionary = new Hashtable();
                enum = _entities.elements();
                while ( enum.hasMoreElements() ) {
                    node = ( (Node) enum.nextElement() ).cloneNode( deep );
                    dictionary.put( node.getNodeName(), node );
                }
                ( (DocumentTypeImpl) into )._entities = new NamedNodeMapImpl( into, dictionary );
            }
            
            // Repeat after me...
            if ( _notations != null ) {
                dictionary = new Hashtable();
                enum = _notations.elements();
                while ( enum.hasMoreElements() ) {
                    node = ( (Node) enum.nextElement() ).cloneNode( deep );
                    dictionary.put( node.getNodeName(), node );
                }
                ( (DocumentTypeImpl) into )._notations = new NamedNodeMapImpl( into, dictionary );
            }
        } else {
            ( (DocumentTypeImpl) into )._entities = _entities;
            ( (DocumentTypeImpl) into )._notations = _notations;
        }
    }
    

    public DocumentTypeImpl( String qualifiedName, String systemId, String publicId,
                             String internalSubset )
    {
        super( null, qualifiedName, null );
        _systemId = systemId;
        _publicId = publicId;
        _internalSubset = internalSubset;
    }


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
     * The internal subset.
     */
    private String            _internalSubset;
    

}

















