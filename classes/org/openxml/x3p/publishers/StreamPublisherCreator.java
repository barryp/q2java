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
import java.util.StringTokenizer;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.x3p.*;
import org.openxml.util.Resources;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class StreamPublisherCreator
	implements PublisherCreator, Publisher
{


	private StreamPublisherTarget   _target;


	private StreamPublisher         _lastPublisher;
	
	
	private static final Class[]    TARGETS = new Class[] { StreamPublisherTarget.class };

	public StreamPublisherCreator()
	{
	}
	private StreamPublisherCreator( StreamPublisherTarget target )
	{
		_target = target;
	}
	public synchronized void close()
	{
		if ( _target == null )
			throw new IllegalStateException( Resources.message( "Error006" ) );
		if ( _lastPublisher != null )
			_lastPublisher.close();
	}
	public Publisher createPublisher( PublisherTarget target )
		throws IOException
	{
		StreamPublisherTarget   streamTarget;

		if ( ! ( target instanceof StreamPublisherTarget ) )
			throw new IllegalArgumentException( Resources.format( "Error003", "target" ) );
		streamTarget = (StreamPublisherTarget) target;
		if ( streamTarget.getFormat() != null )
		{
			if ( streamTarget.getFormat().isXML() )
				return new XMLStreamPublisher( streamTarget );
			if ( streamTarget.getFormat().isHTML() )
				return new HTMLStreamPublisher( streamTarget );
			if ( streamTarget.getFormat().isXHTML() )
				return new XHTMLStreamPublisher( streamTarget );
		}
		return new StreamPublisherCreator( streamTarget );
	}
	public boolean isSupported( PublisherTarget target )
	{
		return ( target instanceof StreamPublisherTarget );
	}
	public Class[] listTargets()
	{
		return TARGETS;
	}
	public synchronized void publish( Document doc )
		throws IOException
	{
		if ( _target == null )
			throw new IllegalStateException( Resources.message( "Error006" ) );
		if ( doc instanceof HTMLDocument )
			_lastPublisher = new XHTMLStreamPublisher( _target.useStreamFormat( StreamFormat.XHTML ) );
		else
			_lastPublisher = new XMLStreamPublisher( _target.useStreamFormat( StreamFormat.XML ) );
		_lastPublisher.publish( doc );
	}
	public synchronized void publish( Node node )
		throws IOException
	{
		if ( _target == null )
			throw new IllegalStateException( Resources.message( "Error006" ) );
		if ( node instanceof HTMLElement )
			_lastPublisher = new XHTMLStreamPublisher( _target.useStreamFormat( StreamFormat.XHTML ) );
		else
			_lastPublisher = new XMLStreamPublisher( _target.useStreamFormat( StreamFormat.XML ) );
		_lastPublisher.publish( node );
	}
}