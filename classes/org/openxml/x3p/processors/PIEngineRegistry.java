package org.openxml.x3p.processors;

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


import java.util.*;
import org.openxml.x3p.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class PIEngineRegistry
	implements ProcessorEngineCreator
{


	private Hashtable       _factories = new Hashtable();


	public ProcessorEngine createEngine( ProcessContext ctx )
	{
		return new PIProcessor( ctx, _factories );
	}
	public void registerPIEngine( String targetName, ProcessorEngineCreator creator )
	{
		_factories.put( targetName, creator );
	}
	public String toString()
	{
		String      desc;
		int         i;
		Enumeration enum;
		String      target;

		enum = _factories.keys();
		if ( ! enum.hasMoreElements() )
			return Resources.message( "Processor003" );
		target = (String) enum.nextElement();
		desc = "PIEngineRegistry [" + target + ":" + _factories.get( target ).toString();
		while ( enum.hasMoreElements() )
		{
			target = (String) enum.nextElement();
			desc = desc + ", " + target + ":" + _factories.get( target ).toString();
		}
		return desc + "]";
	}
}