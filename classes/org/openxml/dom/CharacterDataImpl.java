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
 * Sep 23, 1999
 * - Removed supportChilds(), no longer necessary.
 */


package org.openxml.dom;


import org.w3c.dom.*;


/**
 * Abstract data class has methods for interacting directly with data contained
 * in it. Derived classes {@link org.w3c.dom.Text}, {@link org.w3c.dom.Comment}
 * and {@link org.w3c.dom.CDATASection} provide full implementation of this class.
 * <P>
 * The initial data is guaranteed to be a zero length string, not null. Setting
 * the data to null will always return an empty string.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node does not support childern
 * </OL>
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.CharacterData
 * @see NodeImpl
 */
abstract class CharacterDataImpl
    extends NodeImpl
    implements CharacterData
{


    public final String getData()
    {
        // Same as calling getNodeValue().
        return getNodeValue();
    }
    
    
    public final void setData( String value )
        throws DOMException
    {
        setNodeValue( value == null ? "" : value );
    }
    
    
    public final int getLength()
    {
        return getNodeValue().length();
    }
    
    
    public final synchronized String substringData( int start, int count )
        throws DOMException
    {
        // Make sure that start and start + count are not outside the range of
        // the data.
        if ( start < 0 || start >= getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "'start' out of data size." );
        if ( start + count > getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "'start + count' out of data size." );
        // Cut the data and return.
        return getNodeValue().substring( start, count );
    }
    
    
    public final synchronized void appendData( String value )
    {
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( value == null )
            value = "";
        setNodeValue( getNodeValue() + value );
    }
    
    
    public final synchronized void insertData( int offset,  String value )
        throws DOMException
    {
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( value == null )
            value = "";
        // Make sure that offest is not outside the range of the data.
        if ( offset < 0 || offset > getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "'start' out of data size." );
        // If offset is zero, prepend value to data. If offest is size
        // of data, append value to data.
        if ( offset == 0 )
            setNodeValue( value + getNodeValue() );
        else
        if ( offset == getLength() )
            setNodeValue( getNodeValue() + value );
        // Cut data in the middle and combine all three parts.
        else
            setNodeValue( getNodeValue().substring( 0, offset ) + value
                + getNodeValue().substring( offset ) );
    }
    
    
    public final synchronized void deleteData( int offset, int count )
        throws DOMException
    {
        // Make sure that offest and offset + count are not outside
        // the range of the data.
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( offset < 0 || offset >= getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "'start' out of data size." );
        if ( offset + count > getLength() )
            throw new DOMExceptionImpl( DOMException.INDEX_SIZE_ERR, "'start + count' out of data size." );
        if ( count == 0 )
            return;
        
        // If offest + count reach end of data, it's easier to cut end of data.
        // If offest is zero, it's easier to cut beginning of data.
        if ( offset + count == getLength() )
            setNodeValue( getNodeValue().substring( 0, offset ) );
        else
        if ( offset == 0 )
            setNodeValue( getNodeValue().substring( count ) );
        // Cut data in the middle and combine the two parts.
        else
            setNodeValue( getNodeValue().substring( 0, offset ) +
                          getNodeValue().substring( offset + count ) );
    }
    
    
    public final synchronized void replaceData( int offset, int count, String value )
        throws DOMException
    {
        if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
        if ( value == null )
            value = "";
        // Cheap implementation performs deletion and insertion. Both methods
        // are synchronized, but replace() must also be synchronized to prevent
        // mid-call changes.
        deleteData( offset, count );
        insertData( offset, value );
    }
    
    
    /**
     * Constructor for derived classes.
     * 
     * @param owner The owner of this document
     * @param name The name of this node type
     * @param value Initial value or empty string
     */
    CharacterDataImpl( DocumentImpl owner, String name, String value )
    {
        super( owner, name, value );
    }


}

