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
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.x3p.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class StringPublisher
	implements Publisher
{
	
	
	private StreamFormat        _format;
	
	
	private StringWriter        _writer;
	
	
	private StreamPublisher     _lastPublisher;
	
	
	public StringPublisher( StreamFormat format )
	{
		_format = format;
	}
	public synchronized void close()
	{
		if ( _lastPublisher != null )
			_lastPublisher.close();
	}
	private StreamPublisher getPublisher( Node node )
		throws IOException
	{
		StreamPublisherTarget   target;
		
		if ( _writer == null )
			_writer = new java.io.StringWriter();
		if ( _lastPublisher == null )
		{
			if ( _format == null )
			{
				if ( node instanceof HTMLDocument || node instanceof HTMLElement )
					_format = StreamFormat.XHTML;
				else
					_format = StreamFormat.XML;
			}
			else
			if ( ! _format.isXML() && ! _format.isHTML() && ! _format.isXHTML() )
			{
				if ( node instanceof HTMLDocument || node instanceof HTMLElement )
					_format = _format.changeToXHTML();
				else
					_format = _format.changeToXML();
			}
				
			target = new StreamPublisherTarget( _writer, _format );
			if ( _format.isXML() )
				_lastPublisher = new XMLStreamPublisher( target );
			else
			if ( _format.isHTML() )
				_lastPublisher = new HTMLStreamPublisher( target );
			else
			if ( _format.isXHTML() )
				_lastPublisher = new XHTMLStreamPublisher( target );
		}
		return _lastPublisher;
	}
	public String getString()
	{
		if ( _writer == null )
			return "";
		else
			return _writer.toString();
	}
	public StringBuffer getStringBuffer()
	{
		if ( _writer == null )
			return new StringBuffer();
		else
			return _writer.getBuffer();
	}
	public synchronized void publish( Document doc )
		throws IOException
	{
		getPublisher( doc ).publish( doc );
	}
	public synchronized void publish( Node node )
		throws IOException
	{
		getPublisher( node ).publish( node );
	}
}