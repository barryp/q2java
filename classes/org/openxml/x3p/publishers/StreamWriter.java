package org.openxml.x3p.publishers;

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


import java.io.*;


/**
 * Optimized writer for XML documents is a single class combination of both
 * {@link java.io.OutputStreamWriter} and {@link java.io.BufferedWriter}.
 * {@link java.io.OutputStreamWriter} is necessary for character encoding on
 * the output stream, whereas {@link java.io.BufferedWriter} is necessary for
 * optimized writing.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see java.io.Writer
 */
public final class StreamWriter
	extends    OutputStreamWriter
{


	/**
	 * The stream buffer used to hold output and flush it periodically. The buffer
	 * is useful to speed up output operations, replacing multiple I/O calls with
	 * a single buffered call.
	 */
	private char[]              _buffer;

	
	/**
	 * The index of the next character to write into the buffer. This starts at
	 * zero, climbs up to {@link #BUFFER_SIZE}, when the buffer must be flushed.
	 */
	private int                 _index;

	
	/**
	 * Size of buffer is fixed and usually sufficient. The intent is to have a
	 * buffer that is large enough to speed performance, yet not too large to
	 * affect memory consumption when multiple threads are printing XML documents.
	 */
	private static final int    BUFFER_SIZE = 4096;
	
	
	/**
	 * Construct a new writer based on an open output stream. The constructor
	 * should be called for a full document.
	 *
	 * @param output The output stream
	 * @throws IOException An I/O error occured
	 */
	public StreamWriter( OutputStream output )
		throws IOException
	{
		this( output, "UTF8" );
	}
	/**
	 * Construct a new writer based on an open output stream and the specified
	 * encoding. The constructor should be called for a full document. Encoding
	 * may be any valid and installed character encoding, such as "ASCII", "UTF8",
	 * "Unicode", etc.
	 *
	 * @param output The output stream
	 * @param encoding The preferred character encoding
	 * @throws IOException An I/O error occured
	 * @throws UnsupportedEncodingException The encoding is not supported
	 */
	public StreamWriter( OutputStream output, String encoding )
		throws IOException, UnsupportedEncodingException
	{
		super( output, ( encoding != null ? encoding : "UTF8" ) );
		_buffer = new char[ BUFFER_SIZE ];
		_index = 0;
	}
	public void close()
		throws IOException
	{
		synchronized ( lock )
		{
			flush();
			super.close();
			_buffer = null;
		}
	}
	public void flush()
		throws IOException
	{
		synchronized ( lock )
		{
			if ( _index != 0 )
			{
				super.write( _buffer, 0, _index );
				_index = 0;
			}
			super.flush();
		}
	}
	public void write( char chars[], int offset, int length )
		throws IOException
	{
		int allowed;
		
		synchronized ( lock )
		{
			if ( offset < 0 || length < 0 || offset + length > chars.length )
				throw new IndexOutOfBoundsException( "The supplied index is out of bounds" );
			while ( length > 0 )
			{
				allowed = length;
				if ( allowed > BUFFER_SIZE - _index )
					allowed = BUFFER_SIZE - _index;
				System.arraycopy( chars, offset, _buffer, _index, allowed );
				offset += allowed;
				length -= allowed;
				_index += allowed;
				if ( _index == BUFFER_SIZE )
					flush();
			}
		}
	}
	public void write( int ch )
		throws IOException
	{
		synchronized ( lock )
		{
			if ( _index >= BUFFER_SIZE )
				flush();
			_buffer[ _index++ ] = (char) ch;
		}
	}
	public void write( String str, int offset, int length )
		throws IOException
	{
		write( str.toCharArray(), offset, length );
	}
}