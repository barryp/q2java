package org.openxml.x3p.publishers;

import java.io.*;
import org.openxml.x3p.*;


public final class StreamPublisherTarget
	implements PublisherTarget
{
	

	private OutputStream    _stream;
	
	
	private Writer          _writer;
	
	
	private File            _file;
	
	
	private StreamFormat    _format;
	
	
	public StreamPublisherTarget( File file, StreamFormat format )
	{
		_file = file;
		_format = format;
	}
	public StreamPublisherTarget( OutputStream stream, StreamFormat format )
	{
		_stream = stream;
		_format = format;
	}
	public StreamPublisherTarget( Writer writer, StreamFormat format )
	{
		_writer = writer;
		_format = format;
	}
	public StreamFormat getFormat()
	{
		return _format;
	}
	public OutputStream getOutputStream()
		throws IOException
	{
		if ( _file != null )
		{
			_stream = new FileOutputStream( _file );
			_file = null;
		}
		return _stream;
	}
	public Writer getWriter()
	{
		return _writer;
	}
	StreamPublisherTarget useStreamFormat( StreamFormat format )
	{
		_format = format;
		return this;
	}
}