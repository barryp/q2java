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


import java.util.*;
import org.w3c.dom.*;


/**
 * Implements a collection of nodes that can be accessed by name. Used mostly
 * by {@link DocumentTypeImpl} to hold collections of element, notation and
 * other definitions.
 * <P>
 * The actual collection of objects is held by some owner node in a {@link
 * Dictionary}. This map object provides access to this dictionary in a
 * manner that is consistent with the DOM. This map can be accessed
 * concurrently, so the owner need only create one map per dictionary.
 * <P>
 * Nodes are not maintained in any particular order, so accessing them by index
 * can be expected to be a slow operation. 
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.NamedNodeMap
 */
public final class NamedNodeMapImpl
    implements NamedNodeMap
{

    
    public synchronized Node getNamedItem( String name )
    {
        // Dictionary get by name.
        return (Node) _dictionary.get( name );
    }


    /**
     * ! NOT IMPLEMENTED !
     */
    public synchronized Node getNamedItemNS( String namespaceURI, String localName )
    {
        return getNamedItem( localName );
    }


    public synchronized Node setNamedItem( Node arg )
        throws DOMException
    {
        // Make sure the DTD is not read only and that the added node is of the
        // allowed type (Entity, Notation, AttrDecl, ElementDecl).
        if ( _owner.isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( arg == null || ! ( arg instanceof NodeImpl ) )
            throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR );
        if ( ! ( arg instanceof ElementDeclImpl ||arg instanceof AttlistDecl ||
                 arg instanceof Notation || arg instanceof Entity ) )
            throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR );
        return (Node) _dictionary.put( arg.getNodeName(), arg );
    }
    
    
    public Node setNamedItemNS( Node arg )
        throws DOMException
    {
        return setNamedItem( arg );
    }


    public synchronized Node removeNamedItem( String name )
        throws DOMException
    {
        if ( _owner.isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        return (Node) _dictionary.remove( name );
    }

    
    /**
     * ! NOT IMPLEMENTED !
     */
    public synchronized Node removeNamedItemNS( String namespaceURI, String localName )
    {
        return removeNamedItem( localName );
    }

    
    public synchronized Node item( int index )
    {
        Enumeration    elem;
        
        // Get by index is a long operation, but one that is performed less
        // than get by name. We opt not to use the JDK 1.2 collections to
        // speed it up for the sake of portability.
        elem = _dictionary.elements();
        while ( elem.hasMoreElements() ) {
            if ( index == 0 )
                return (Node) elem.nextElement();
            elem.nextElement();
            -- index;
        }
        return null;
    }

    
    public int getLength()
    {
        return _dictionary.size();
    }
    
    
    /**
     * So we lied about the owner managing the dictionary. But just in case
     * the owner would like to traverse the dictionary list without resorting
     * to the slower indexed method.
     * 
     * @return Enumeration of all elements in the dictionary
     */
    Enumeration elements()
    {
        return _dictionary.elements();
    }
    
  
    /**
     * Constructor required the owner of this dictionary and a reference to the
     * dictionary. Once constructed, the map is ready for use.
     * 
     * @param owner The owner of this dictionary
     * @param dictionary The dictionary managed by that owner
     */
    NamedNodeMapImpl( NodeImpl owner, Dictionary dictionary )
    {
        if ( owner == null || dictionary == null )
            throw new NullPointerException( "Argument 'owner' or 'dictionary' is null." );
        _dictionary = dictionary;
        _owner = owner;
    }
  

    /**
     * Reference to the dictionary accessed through this node map. The dictionary
     * is not held by this object but by the {@link #_owner}.
     */
    private Dictionary        _dictionary;


    /**
     * Reference to the owner of this node list. This is another node which
     * contains {@link #_dictionary} as part of it.
     */
    private NodeImpl        _owner;


}

