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
 * Implements a notation. A notation node merely associates the notation's
 * name with its system and/or public identifiers. The notation has no
 * contents. This node is immutable.
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
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Notation
 * @see NodeImpl
 */
public final class NotationImpl
    extends NodeImpl
    implements Notation
{
    
    
    public short getNodeType()
    {
        return NOTATION_NODE;
    }
    
    
    public final void setNodeValue( String value )
    {
        throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
            "This node type does not support values." );
    }

    
    public String getPublicId()
    {
        return _publicID;
    }
    
    
    public String getSystemId()
    {
        return _systemID;
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

    
    public final Object clone()
    {
        NotationImpl    clone;
        
        clone = new NotationImpl( getNodeValue(), getSystemId(), getPublicId() );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        NotationImpl    clone;
            
        clone = new NotationImpl( getNodeValue(), getSystemId(), getPublicId() );
        cloneInto( clone, deep );
        return clone;
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

    
    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        super.cloneInto( into, deep );
        ( (NotationImpl) into )._systemID = _systemID;
        ( (NotationImpl) into )._publicID = _publicID;
    }

        
    /**
     * Constructor requires owner document, notation name and all its attributes.
     * 
     * @param owner The owner document
     * @param name The entity name
     * @param systemID The system identifier, if specified
     * @param publicID The public identifier, if specified
     */
    public NotationImpl( String name, String systemID, String publicID )
    {
        super( null, name, null );
        if ( _systemID == null && _publicID == null )
            throw new IllegalArgumentException( "Both 'systemID' and 'publicID' are missing." );
        _systemID = systemID;
        _publicID = publicID;
    }
    
    
    /**
     * The system identifier of this notation, if specified.
     */
    private String        _systemID;
    
    
    /**
     * The public identifier of this notation, if specified.
     */
    private String        _publicID;

    
}
