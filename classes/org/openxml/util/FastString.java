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


package org.openxml.util;


/**
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:07 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see FastStringPool
 */
public class FastString
{

 
    public int length()
    {
	    return _length;
    }
    
    
    public void reset()
    {
        _length = 0;
    }


    public void setLength( int newLength )
    {
        char[]  newChars;
        
        if ( newLength == 0 )
        {
            reset();
            return;
        }
        if ( newLength < 0 )
	        throw new StringIndexOutOfBoundsException( newLength );
	    if ( newLength > _chars.length )
        {
            newChars = new char[ calcSize( newLength ) ];
            System.arraycopy( _chars, 0, newChars, 0, _length );
        }
        while ( _length < newLength )
        {
            _chars[ _length ] = '\0';
            ++_length;
        }
        _length = newLength;
	}

    
    public char charAt( int index )
    {
        if ( index < 0 || index >= _length )
	        throw new StringIndexOutOfBoundsException( index );
	    return _chars[ index ];
    }


    public char[] getChars()
    {
	return _chars;
    }

    
    public void getChars( int srcBegin, int srcEnd, char dst[], int dstBegin )
    {
        if ( srcBegin < 0 || srcBegin >= _length )
            throw new StringIndexOutOfBoundsException( srcBegin );
        if ( srcEnd < 0 || srcEnd > _length )
            throw new StringIndexOutOfBoundsException( srcEnd );
        if ( srcBegin < srcEnd )
            System.arraycopy( _chars, srcBegin, dst, dstBegin, srcEnd - srcBegin );
        else
        if ( srcBegin > srcEnd )
		    throw new StringIndexOutOfBoundsException( "begin > end" );
    }


    public void setCharAt( int index, char ch )
    {
        if ( index < 0 || index >= _length )
            throw new StringIndexOutOfBoundsException( index );
	    _chars[ index ] = ch;
    }


    public FastString append( String string )
    {
        int     length;
        int     newSize;
        char[]  newChars;
        
        if ( string == null ) 
            return this;
        length = string.length();
        newSize = _length + length;
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
        string.getChars( 0, length, _chars, _length );            
	    _length = newSize;
	    return this;
    }
    

    public FastString append( char chars[], int offset, int length )
    {
        int     newSize;
        char[]  newChars;

        newSize = _length + length;
	    if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
	    System.arraycopy( chars, offset, _chars, _length, length );
	    _length = newSize;
	    return this;
    }

    
    public FastString append( FastString string )
    {
        int     newSize;
        char[]  newChars;
        int     length;

        length = string._length;
        newSize = _length + length;
	if ( newSize > _chars.length )
        {
	    newChars = new char[ calcSize( newSize ) ];
	    System.arraycopy( _chars, 0, newChars, 0, _length );
	    _chars = newChars;
	}
	System.arraycopy( string._chars, 0, _chars, _length, length );
	_length = newSize;
        return this;
    }

    
    public FastString append( char ch )
    {
        int     newSize;
        char[]  newChars;
        
        newSize = _length + 1;
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
    	_chars[ _length ] = ch;
        ++_length;
        return this;
    }


    public FastString delete( int start, int end )
    {
        int length;

        if ( start < 0 )
            throw new StringIndexOutOfBoundsException( start );
        if ( end > _length )
            end = _length;
        if ( start > end )
            throw new StringIndexOutOfBoundsException();
        length = end - start;
        if ( length > 0 )
        {
            if ( length < _length )
                System.arraycopy( _chars, start + length, _chars, start, _length - end );
            _length -= length;
        }
        return this;
    }


    public FastString replace( int start, int end, String string )
    {
        int     newSize;
        int     length;
        char[]  newChars;
        
        if ( start < 0 )
            throw new StringIndexOutOfBoundsException( start );
        if ( end > _length )
            end = _length;
        if ( start > end )
            throw new StringIndexOutOfBoundsException();
        length = string.length();
        newSize = _length + length - ( end - start );
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
        System.arraycopy( _chars, end, _chars, start + length, _length - end );
        string.getChars( 0, length, _chars, start );
        _length = newSize;
        return this;
    }


    public FastString insert( int index, char chars[], int offset, int length )
    {
        int     newSize;
        char[]  newChars;
        
        if ( index < 0  || index > _length )
            throw new StringIndexOutOfBoundsException();
        if ( offset < 0 || offset + length > chars.length )
            throw new StringIndexOutOfBoundsException( offset );
        if ( length < 0 )
            throw new StringIndexOutOfBoundsException( length );
        newSize = _length + length;
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
        System.arraycopy( _chars, index, _chars, index + length, _length - index );
        System.arraycopy( chars, offset, _chars, index, length );
        _length = newSize;
        return this;
    }


    public FastString insert( int index, String string )
    {
        int     length;
        int     newSize;
        char[]  newChars;
        
        if ( string == null )
            return this;
        if ( index < 0 || index > _chars.length )
            throw new StringIndexOutOfBoundsException( index );
        length = string.length();
        newSize = _length + length;
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
        System.arraycopy( _chars, index, _chars, index + length, _length - index );
        string.getChars( 0, length, _chars, index );
        _length = newSize;
        return this;
    }
    
    
    public FastString insert( int offset, char ch )
    {
        int     newSize;
        char[]  newChars;
        
        newSize = _length + 1;
        if ( newSize > _chars.length )
        {
            newChars = new char[ calcSize( newSize ) ];
	        System.arraycopy( _chars, 0, newChars, 0, _length );
	        _chars = newChars;
        }
        System.arraycopy( _chars, offset, _chars, offset + 1, _length - offset );
        _chars[ offset ] = ch;
        _length = newSize;
        return this;
    }

    
    public boolean startsWith( String string )
    {
        int     length;
        
        // Cover all bases. Prefix must be shorter than buffer.
        if ( string == null )
            return false;
        length = string.length();
        if ( _length < length )
            return false;
        while ( length-- > 0 )
            if ( string.charAt( length ) != _chars[ length ] )
                return false;
        return true;
    }

    
    public FastString toUpperCase()
    {
        int i;
        
        for ( i = 0 ; i < _length ; ++i )
            _chars[ i ] = Character.toUpperCase( _chars[ i ] );
        return this;
    }

        
    public FastString toLowerCase()
    {
        int i;
        
        for ( i = 0 ; i < _length ; ++i )
            _chars[ i ] = Character.toLowerCase( _chars[ i ] );
        return this;
    }

        
    public boolean equals( String string )
    {
        int     length;

        if ( string == null )
            return false;
        length = string.length();
        if ( length != _length )
            return false;
        while ( length-- > 0 )
            if ( string.charAt( length ) != _chars[ length ] )
                return false;
        return true;
    }
    
    
    public boolean equalsUpper( String string )
    {
        int     length;

        if ( string == null )
            return false;
        length = string.length();
        if ( length != _length )
            return false;
        while ( length-- > 0 )
            if ( string.charAt( length ) != Character.toUpperCase( _chars[ length ] ) )
                return false;
        return true;
    }

    
    public boolean equals( Object object )
    {
        return equals( object.toString() );
    }


    public String toUniqueString()
    {
	return new String( _chars, 0, _length );
    }
    
    
    public String toString()
    {
        return _pool.lookup( _chars, 0, _length );
    }
    
    
    public String toString( int start, int end )
    {
        return _pool.lookup( _chars, start, end );
    }

    
    private int calcSize( int minimum )
    {
        int newSize;
        
        newSize = ( _chars.length + 1 ) * 2;
        if ( minimum > newSize )
            newSize = minimum;
        minimum = ( minimum + 15 ) & 0xFFFFFFF0;
        return minimum;
    }
    

    public char[] getCharArray()
    {
        return _chars;
    }


    public static FastStringPool getPool()
    {
	return _pool;
    }
    

    public FastString()
    {
	    this( 16 );
    }


    public FastString( int length )
    {
	    _chars = new char[ ( length + 15 ) & 0xFFFFFFF0 ];
    }


    public FastString( String string )
    {
        _length = string.length();
	    _chars = new char[ ( _length + 15 ) & 0xFFFFFFF0 ];
        string.getChars( 0, _length, _chars, 0 );
    }


    public FastString( FastString string )
    {
        _length = string._length;
	    _chars = new char[ ( _length + 15 ) & 0xFFFFFFF0 ];
        System.arraycopy( string._chars, 0, _chars, 0, _length );
    }

    
    private char    _chars[];

    
    private int     _length;

    
    private static final FastStringPool _pool = new FastStringPool();


}
