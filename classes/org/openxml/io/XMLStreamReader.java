package org.openxml.io;

/**
 * org/openxml/io/XMLStreamReader.java
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


/**
 * Optimized writer for XML documents is a single class combination of both
 * {@link java.io.OutputStreamWriter} and {@link java.io.BufferedWriter}.
 * {@link java.io.OutputStreamWriter} is necessary for character encoding on
 * the output stream, whereas {@link java.io.BufferedWriter} is necessary for
 * optimized writing.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see java.io.Writer
 * 
 * @deprecated
 *  This package has become obsolete in favor of the <a
 *  href="../x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
 *  This package is temporarily provided for backward compatibility but
 *  will not be included in release 1.1.
 */
public final class XMLStreamReader
	extends InputStreamReader
{
	
	
	


	public static final int        EOF = -1;


	/**
	 * The stream buffer used to hold output and flush it periodically. The buffer
	 * is useful to speed up output operations, replacing multiple I/O calls with
	 * a single buffered call.
	 */
	private char[]                _buffer;
	
	
	/**
	 * The index of the next character to write into the buffer. This starts at
	 * zero, climbs up to {@link #BUFFER_SIZE}, when the buffer must be flushed.
	 */
	private int                    _index;
	
	
	private int                    _length;
	
	
	private int                    _pushBack = EOF;
	

	/**
	 * Size of buffer is fixed and usually sufficient. The intent is to have a
	 * buffer that is large enough to speed performance, yet not too large to
	 * affect memory consumption when multiple threads are printing XML documents.
	 */
	private static final int    BUFFER_SIZE = 2048;


	/**
	 * Construct a new writer based on an open output stream. The constructor
	 * should be called for a full document.
	 * 
	 * @param output The output stream
	 * @throws IOException An error occured with the input stream
	 */
	public XMLStreamReader( InputStream input )
		throws IOException
	{
		super( input, "UTF8" );
		_buffer = new char[ BUFFER_SIZE ];
		_index = 0;
		_length = 0;
	}
	/**
	 * Construct a new writer based on an open output stream and the specified
	 * encoding. The constructor should be called for a full document. Encoding
	 * may be any valid and installed character encoding, such as "ASCII", "UTF8",
	 * "Unicode", etc.
	 * 
	 * @param output The output stream
	 * @param encoding The preferred character encoding
	 * @throws UnsupportedEncodingException The encoding is not supported
	 * @throws IOException An error occured with the input stream
	 */
	public XMLStreamReader( InputStream input, String encoding )
		throws IOException, UnsupportedEncodingException
	{
		super( input, ( encoding != null ? encoding : "UTF8" ) );
		_buffer = new char[ BUFFER_SIZE ];
		_index = 0;
		_length = 0;
	}
	public XMLStreamReader changeEncoding( String encoding )
	{
		return this;
	}
	public synchronized void close()
		throws IOException
	{
		synchronized ( lock )
		{
			super.close();
			_buffer = null;
		}
	}
	public void mark( int readAheadLimit )
		throws IOException
	{
		throw new IOException( "Mark not supported for this reader." );
	}
	public boolean markSupported()
	{
		return false;
	}
	public synchronized int read()
		throws IOException
	{
		int    ch;
		
		synchronized ( lock )
		{
			if ( _pushBack != EOF )
			{
				ch = _pushBack;
				_pushBack = EOF;
			}
			else
				ch = super.read();
		}
		return ch;
	}
	public synchronized String readLine()
		throws IOException
	{
		StringBuffer    line;
		int                ch;
		
		synchronized ( lock )
		{
			ch = read();
			if ( ch == EOF )
				return null;
			line = new StringBuffer( 40 );
			while ( ch != EOF )
			{
				if ( ch == 0x0A )
					break;
				if ( ch == 0x0D )
				{
					ch = read();
					if ( ch != 0x0A )
						_pushBack = ch;
					break;
				}
				line.append( (char) ch );
				ch = read();
			}
			return line.toString();
		}
	}
	public boolean ready()
		throws IOException
	{
		synchronized ( lock )
		{
			// True if there are any characters waiting in the buffer. Otherwise,
			// don't read buffer now, just report ready state of stream.
			if ( _index < _length )
				return true;
			return super.ready();
		}
	}
	public void reset()
		throws IOException
	{
		throw new IOException( "Reset not supported for this reader." );
	}
	public long skip( long n )
		throws IOException
	{
		long    remain;
		
		synchronized ( lock )
		{
			remain = n - ( _length - _index );
			while ( remain > 0 )
			{
				
				_index = 0;
				_length = 0;
				if ( read() == EOF )
					;
			}
			if ( remain <= 0 )
			{
				_index += n;
				if ( _index == _length )
				{
					_index = 0;
					_length = 0;
				}
				return n;
			}
			else
			{
				_index = 0;
				_length = 0;
				return ( n - remain );
			}
		}
	}
}