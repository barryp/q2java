package org.openxml.util;

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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see FastString
 */
public class FastStringPool
{
	
	
	/**
	 * List of prime numbers beginning with 3 and ending around 3,000,000.
	 * These primes are used to resize the pool to a larger size.
	 */
	private static final int[]          _primes = 
	{
		3, 7, 11, 13, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293,
		353, 431, 521, 631, 761, 919, 1103, 1327, 1597, 1931, 2333, 2801,
		3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591, 17519,
		21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523,
		108631, 130363, 156437, 187751, 225307, 270371, 324449, 389357,
		467237, 560689, 672827, 807403, 968897, 1162687, 1395263, 1674319,
		2009191, 2411033, 2893249
	};
	
	
	/**
	 * The initial size of the shared string pool. The string pool is fixed
	 * to this size, so it better last it a long time.
	 * <P>
	 * If a new value is selected for the pool size, it must observe the
	 * following rules: the value must be a prime number, or the hasing will
	 * not be optimized, and the value must be sufficiently large so there
	 * is little need to traverse list of nodes in each entry,
	 */
	private static final int            INITIAL_POOL_SIZE = 4861;
	

	/**
	 * This is the empty string.
	 */
	private static final String         EMPTY_STRING = "";
	

	/**
	 * Defines the maximum length of a string that will be stored in the
	 * shared pool. Longer strings are probably too rare to benefit from
	 * the pool and so are returned as new strings. Strings within this
	 * length are candidates for pooling.
	 */
	private static final int            MAX_POOL_STRING_LEN = 32;

	
	/**
	 * Prime number used for hashing strings. Hash code uniqueness is
	 * assured by multiplying the previous hash value by this prime number
	 * and then adding the ordinal code of the next character (generally a
	 * value between 0 and 96). See {@link #primeHash} for use.
	 */
	private static final int            STRING_HASH_PRIME = 37;
	
	
	private static final boolean        PHONG_HASH = true;

	
	/**
	 * Implements a shared string pool. Once an string has been lookup,
	 * it will be stored in this pool and subsequent lookups will retrieve
	 * it from the pool. The pool is implemented as a very minimalistic
	 * hashtable with only insertion and lookup capabilities.
	 * <P>
	 * The original size of this table is 4861 elements. The table can
	 * hold more than that many strings, but only that many strings can
	 * be held so each lookup involves a single node access
	 */
	private StringPoolEntry[]           _pool;

	
	private int                         _firstEntryCount;
	

	private int                         _secondEntryCount;
	
	
	/**
	 * Defines the maximum length of a string that will be stored in the
	 * shared pool. Longer strings are probably too rare to benefit from
	 * the pool and so are returned as new strings. Strings within this
	 * length are candidates for pooling.
	 */
	private int                         _maxPoolStringLength = MAX_POOL_STRING_LEN;
	
	
	/**
	 * If true the Phong hashing algorithm is used. If false, the prime
	 * hashing algorithm is used.
	 */
	private boolean                     _phongHash = false;


	public FastStringPool()
	{
		_pool = new StringPoolEntry[ INITIAL_POOL_SIZE ];
	}
	public FastStringPool( int initialPoolSize, int maxPoolStringLength, boolean phongHash )
	{
		if ( initialPoolSize < 64 )
			initialPoolSize = 64;
		_pool = new StringPoolEntry[ primeFromField( initialPoolSize ) ];
		_maxPoolStringLength = maxPoolStringLength;
		_phongHash = phongHash;
	}
	public void enlargePool()
	{
		int                 i;
		int                 newSize;
		StringPoolEntry[]   newPool;
		StringPoolEntry     entry;
		int                 hash;
		
		newSize = _pool.length;
		for ( i = 0 ; i < _primes.length ; ++i )
		{
			newSize = _primes[ i ];
			if ( newSize > _pool.length )
				break;
		}
		if ( newSize > _pool.length )
		{
			newPool = new StringPoolEntry[ newSize ];
			for ( i = 0 ; i < _pool.length ; ++i )
			{
				entry = _pool[ i ];
				while ( entry != null )
				{
					_pool[ i ] = entry._next;
					hash = hash( entry._string ) % newSize;
					entry._next = newPool[ hash ];
					newPool[ hash ] = entry;
					entry = _pool[ i ];
				}
			}
			_pool = newPool;
		}
	}
	public int getFirstEntryCount()
	{
		return _firstEntryCount;
	}
	public int getPoolSize()
	{
		return _pool.length;
	}
	public int getSecondEntryCount()
	{
		return _secondEntryCount;
	}
	protected int hash( String str )
	{
		int     i;
		int     hash;
		
		hash = 1;
		if ( PHONG_HASH )
		{
			for ( i = 0 ; i < str.length() ; ++ i )
				hash = ( 0x63C63CD9 * hash ) + 0x9C39C33D + str.charAt( i );
		}
		else
		{
			for ( i = 0 ; i < str.length() ; ++ i )
				hash = ( hash * STRING_HASH_PRIME ) ^ ( str.charAt( i ) - ' ' );
		}
		return ( hash & 0x7FFFFFFF );
	}
	/**
	 * Returns a string that of equivalent contents to the original string.
	 * As the name implies this method might perform an in-memory lookup
	 * for the purpose of combining all non-unique strings in a shared
	 * pool.
	 * <P>
	 * If the original string is null or empty, the canonical empty string
	 * will be returned. If the original string is longer than a specified
	 * length, it will not be used in the pool as its non-uniqurness is
	 * not guaranteed.
	 * 
	 * @param string The original string
	 * @return An equal string from the shared pool
	 */
	public String lookup( char[] chars, int start, int end )
	{
		int             length;
		int			    hash;
		StringPoolEntry entry;
		length = end - start;
		// Attempt to pass null or an empty string will return the 
		// cannonical empty string.
		if ( chars == null || length <= 0 )
			return EMPTY_STRING;
		if ( length > _maxPoolStringLength )
			return new String( chars, start, length );
		
		// Calculate the hash from the string contents. Round it up to the
		// pool size and use that number as the index into the pool entry.
		hash = 0;
		if ( PHONG_HASH )
		{
			for ( int i = start ; i < end ; ++i )
				hash = ( hash * STRING_HASH_PRIME ) ^ ( chars[ i ] - ' ' );
		}
		else
		{
			for ( int i = start ; i < end ; ++i )
				hash = ( 0x63C63CD9 * hash ) + 0x9C39C33D + chars[ i ];
		}
		hash = ( hash & 0x7FFFFFFF ) % _pool.length;

		// Look for an existing entry in the pool, if one is found return
		// it. This saves the creation of a new String object, at the cost
		// of string lookup. Make sure to update the last accessed time.
		synchronized ( _pool )
		{
			for( entry = _pool[ hash ] ; entry != null ; entry = entry._next )
				if ( match( entry._string, chars, start, end ) )
					return entry._string;
			entry = new StringPoolEntry();
			entry._string = new String( chars, start, length );

			// If the pool already contains an entry for this string, push
			// it in and make sure the next entry is linked from it.
			// Update the statistic counters.
			entry._next =_pool[ hash ];
			_pool[ hash ] = entry;
			if ( entry._next == null )
				++ _firstEntryCount;
			else
				++ _secondEntryCount;
		}
		return entry._string;
	}
	/**
	 * Returns a string that of equivalent contents to the original string.
	 * As the name implies this method might perform an in-memory lookup
	 * for the purpose of combining all non-unique strings in a shared
	 * pool.
	 * <P>
	 * If the original string is null or empty, the canonical empty string
	 * will be returned. If the original string is longer than a specified
	 * length, it will not be used in the pool as its non-uniqurness is
	 * not guaranteed.
	 * 
	 * @param string The original string
	 * @return An equal string from the shared pool
	 */
	public String lookup( String string )
	{
		int			    hash;
		StringPoolEntry entry;

		// Attempt to pass null or an empty string will return the 
		// cannonical empty string.
		if ( string == null || string.length() == 0 )
			return EMPTY_STRING;
		if ( string.length() > _maxPoolStringLength )
			return string;
		// Calculate the hash from the string contents. Round it up to the
		// pool size and use that number as the index into the pool entry.
		hash = hash( string ) % _pool.length;

		// Look for an existing entry in the pool, if one is found return
		// it. This saves the creation of a new String object, at the cost
		// of string lookup. Make sure to update the last accessed time.
		synchronized ( _pool )
		{
			for ( entry = _pool[ hash ] ; entry != null ; entry = entry._next )
				if ( entry._string.equals( string ) )
					return entry._string;
			entry = new StringPoolEntry();
			entry._string = string;

			// If the pool already contains an entry for this string, push
			// it in and make sure the next entry is linked from it.
			// Update the statistic counters.
			entry._next =_pool[ hash ];
			_pool[ hash ] = entry;
			if ( entry._next == null )
				++ _firstEntryCount;
			else
				++ _secondEntryCount;
		}
		return entry._string;
	}
	/**
	 * Returns a string that of equivalent contents to the original string.
	 * As the name implies this method might perform an in-memory lookup
	 * for the purpose of combining all non-unique strings in a shared
	 * pool.
	 * <P>
	 * If the original string is null or empty, the canonical empty string
	 * will be returned. If the original string is longer than a specified
	 * length, it will not be used in the pool as its non-uniqurness is
	 * not guaranteed.
	 * 
	 * @param string The original string
	 * @return An equal string from the shared pool
	 */
	public String lookup( StringBuffer string, int start, int end )
	{
		int             length;
		int			    hash;
		StringPoolEntry entry;
		char[]          chars;

		length = end - start;
		// Attempt to pass null or an empty string will return the 
		// cannonical empty string. Attempt to pass a longer string
		// than the recommended pool size will return a new String
		// not from the pool.
		if ( string == null || length <= 0 )
			return EMPTY_STRING;
		if ( length > _maxPoolStringLength )
		{
			if ( length == string.length() )
				return string.toString();
			else
			{
				chars = new char[ length ];
				string.getChars( start, end, chars, 0 );
				return new String( chars );
			}
		}

		// Turn this buffer into a character array and work with it.
		chars = new char[ length ];
		string.getChars( start, end, chars, 0 );

		// Calculate the hash from the string contents. Round it up to the
		// pool size and use that number as the index into the pool entry.
		hash = 0;
		if ( PHONG_HASH )
		{
			for ( int i = start ; i < end ; ++i )
				hash = ( hash * STRING_HASH_PRIME ) ^ ( chars[ i ] - ' ' );
		}
		else
		{
			for ( int i = start ; i < end ; ++i )
				hash = ( 0x63C63CD9 * hash ) + 0x9C39C33D + chars[ i ];
		}
		hash = ( hash & 0x7FFFFFFF ) % _pool.length;

		// Look for an existing entry in the pool, if one is found return
		// it. This saves the creation of a new String object, at the cost
		// of string lookup. Make sure to update the last accessed time.
		synchronized ( _pool )
		{
			for( entry = _pool[ hash ] ; entry != null ; entry = entry._next )
			{
				int index;
				if ( entry._string.length() == length )
				{
					for ( index = start ; index < end ; ++ index )
						if ( string.charAt( index - start ) != chars[ index ] )
							break;
					if ( index == end )
//                if ( match( entry._string, chars, 0, length ) )
						return entry._string;
				}
			}
			entry = new StringPoolEntry();
			entry._string = new String( chars, 0, length );

			// If the pool already contains an entry for this string, push
			// it in and make sure the next entry is linked from it.
			// Update the statistic counters.
			entry._next =_pool[ hash ];
			_pool[ hash ] = entry;
			if ( entry._next == null )
				++ _firstEntryCount;
			else
				++ _secondEntryCount;
		}
		return entry._string;
	}
	protected boolean match( String str, char[] chars )
	{
		int index;
		int length;
		
		length = chars.length;
		if ( str.length() != length )
			return false;
		for ( index = 0 ; index < length ; ++ index )
			if ( str.charAt( index ) != chars[ index ] )
				return false;
		return true;
	}
	protected boolean match( String str, char[] chars, int start, int end )
	{
		int index;

		if ( str.length() != end - start )
			return false;
		for ( index = start ; index < end ; ++ index )
			if ( str.charAt( index - start ) != chars[ index ] )
				return false;
		return true;
	}
	/**
	 * Returns the largest prime number in this field. This method is used
	 * to turn an arbitrary size (e.g. 4096) into a prime field size (e.g
	 * 4049). The return value is the larger prime number that is less than
	 * or equal to field.
	 * 
	 * @param field The field size
	 * @return prime The prime field size
	 */
	protected static int primeFromField( int field )
	{
		int i;

		i = _primes.length;
		while ( i-- > 0 )
			if ( _primes[ i ] <= field )
				return _primes[ i ];
		return 3;
	}
}