package org.openxml.x3p;

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
import org.openxml.x3p.publishers.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class PublisherFactory
{
	
	
	private static PublisherFactoryImpl _singleton;


	static
	{
		_singleton = new PublisherFactoryImpl();
		_singleton.registerPublisher( new StreamPublisherCreator() );
	}

	
	public static PublisherCreator asCreator()
	{
		return _singleton;
	}
	public synchronized static Publisher createPublisher( File output, StreamFormat format )
		throws IOException
	{
		return _singleton.createPublisher( new StreamPublisherTarget( output, format ) );
	}
	public synchronized static Publisher createPublisher( OutputStream output, StreamFormat format )
		throws IOException
	{
		return _singleton.createPublisher( new StreamPublisherTarget( output, format ) );
	}
	public synchronized static Publisher createPublisher( Writer output, StreamFormat format )
		throws IOException
	{
		return _singleton.createPublisher( new StreamPublisherTarget( output, format ) );
	}
	public synchronized static Publisher createPublisher( PublisherTarget target )
		throws PublisherTargetNotSupportedException, IOException
	{
		Publisher   publisher;
		
		publisher = _singleton.createPublisher( target );
		if ( publisher == null )
			throw new PublisherTargetNotSupportedException( Resources.format( "Publisher000", target ) );
		return publisher;
	}
	public static synchronized boolean isSupported( PublisherTarget target )
	{
		return _singleton.isSupported( target );
	}
	public static synchronized Class[] listTargets()
	{
		return _singleton.listTargets();
	}
	public synchronized static void registerPublisher( PublisherCreator creator )
	{
		if ( creator != _singleton )
			_singleton.registerPublisher( creator );
	}
}