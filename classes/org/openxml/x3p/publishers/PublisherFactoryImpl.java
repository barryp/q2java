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
import org.openxml.x3p.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class PublisherFactoryImpl
	implements PublisherCreator
{
	
	private static PublisherCreator[]   _creators;

	
	public synchronized Publisher createPublisher( PublisherTarget target )
		throws IOException
	{
		Publisher   publisher = null;
		int         i;

		if ( _creators != null )
		{
			for ( i = 0 ; i < _creators.length ; ++i )
			{
				if ( _creators[ i ].isSupported( target ) )
				{
					publisher = _creators[ i ].createPublisher( target );
					if ( publisher != null )
						return publisher;
				}
			}
		}
		return null;
	}
	public synchronized boolean isSupported( PublisherTarget target )
	{
		PublisherCreator    creator;
		int                 i;

		if ( _creators == null )
			return false;
		for ( i = 0 ; i < _creators.length ; ++i )
		{
			creator = _creators[ i ];
			if ( creator.isSupported( target ) )
				return true;
		}
		return false;
	}
	public synchronized Class[] listTargets()
	{
		Class[]             allTargets = null;
		Class[]             targets;
		Class[]             newTargets;
		PublisherCreator    creator;
		int                 i;

		if ( _creators == null )
			return null;
		for ( i = 0 ; i < _creators.length ; ++i )
		{
			creator = _creators[ i ];
			targets = creator.listTargets();
			if ( targets != null && targets.length > 0 )
			{
				if ( allTargets == null )
				{
					allTargets = new Class[ targets.length ];
					System.arraycopy( targets, 0, allTargets, 0, targets.length );
				}
				else
				{
					newTargets = new Class[ allTargets.length + targets.length ];
					System.arraycopy( allTargets, 0, newTargets, 0, allTargets.length );
					System.arraycopy( targets, 0, newTargets, allTargets.length, targets.length );
					allTargets = newTargets;
				}
			}
		}
		return allTargets;
	}
	public synchronized void registerPublisher( PublisherCreator creator )
	{
		PublisherCreator[]  newArray;
		int                 i;

		if ( _creators == null )
		{
			_creators = new PublisherCreator[ 1 ];
			_creators[ 0 ] = creator;
			return;
		}
		for ( i = 0 ; i < _creators.length ; ++i )
			if ( creator == _creators[ 0 ] )
				return;
		newArray = new PublisherCreator[ _creators.length ];
		System.arraycopy( _creators, 0, newArray, 0, _creators.length );
		newArray[ _creators.length ] = creator;
		_creators = newArray;
	}
}