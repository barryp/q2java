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
import org.w3c.dom.*;
import org.openxml.x3p.*;


/**
 * This class is not implemented yet, only provided as a test case.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class SSIEngine
	implements ProcessorEngine
{


	public SSIEngine( ProcessContext ctx )
	{
	}
	public void destroy( ProcessContext ctx )
	{
	}
	public synchronized Node process( ProcessContext ctx, Node source )
		throws ProcessorException
	{
		Object  obj;
		String  value;
		String  name;
		int     index;

		if ( source.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE )
		{
			if ( source.getNodeName().equals( "ssi:echo" ) )
			{
				value = source.getNodeValue();
				if ( value.equals( "DATE_GMT" ) )
					value = new Date().toGMTString();
				else
				if ( value.equals( "DATE_LOCAL" ) )
					value = new Date().toLocaleString();
				else
				{
					obj = ctx.getObject( value );
					if ( obj == null )
						value = "<unknown>";
					else
						value = obj.toString();
				}
				return source.getOwnerDocument().createTextNode( value );
			}
			else
			if ( source.getNodeName().equals( "ssi:set" ) )
			{
				value = source.getNodeValue();
				if ( value.startsWith( "var=\"" ) )
				{
					value = value.substring( 5 );
					index = value.indexOf( "\"" );
					if ( index >= 0 )
					{
						name = value.substring( 0, index );
						++index;
						while ( index < value.length() &&
								value.charAt( index ) == ' ' )
							++index;
						if ( index < value.length() )
						{
							value = value.substring( index );
							if ( value.startsWith( "value=\"" ) )
							{
								value = value.substring( 7 );
								index = value.indexOf( "\"" );
								if ( index > 0 )
									value = value.substring( 0, index );
								ctx.setObject( name, value );
							}
						}
					}
				}
				return null;
			}
			else
			if ( source.getNodeName().equals( "ssi:include" ) )
			{
				value = source.getNodeValue();
				if ( value.startsWith( "file=\"" ) )
				{
					value = value.substring( 6 );
					index = value.indexOf( "\"" );
					if ( index >= 0 )
						value = value.substring( 0, index );

				}
				else
				if ( value.startsWith( "text=\"" ) )
				{
					value = value.substring( 6 );
					index = value.indexOf( "\"" );
					if ( index >= 0 )
						value = value.substring( 0, index );

				}
				return null;
			}
		}
		return source;
	}
	public String toString()
	{
		return "SSIEngine";
	}
	public int whatToProcess()
	{
		return PROCESS_PI;
	}
}