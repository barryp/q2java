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


import java.util.*;
import org.openxml.x3p.processors.*;
import org.openxml.util.*;


/**
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public abstract class ProcessorFactory
{


	private static PIEngineRegistry     _piRegistry = new PIEngineRegistry();


	private static Hashtable            _creators = new Hashtable();


	static
	{
		ChainProcessorCreator       defProcessor;
		ProcessorEngineCreator[]    defFactories;

		registerEngine( "SSI", new SSIEngineCreator() );
		registerEngine( "PI", _piRegistry );
		defFactories = new ProcessorEngineCreator[ 2 ];
		defFactories[ 1 ] = new SSIEngineCreator();
		defFactories[ 0 ] = _piRegistry;
		registerEngine( "default", new ChainProcessorCreator( defFactories ) );
	}


	/**
	 * Creates and returns a default system-wide processor.
	 *
	 * @param activator The activator of this processor
	 */
	public static Processor createProcessor( Object activator )
		throws ProcessorException
	{
		return createProcessor( activator, "default" );
	}
	/**
	 * Creates and returns a processor that uses the named engine or sequence of
	 * engines. <TT>engines</TT> specifies a generic engine name, or a sequence
	 * of generic engine names, as previously registered with this factory.
	 * For example,
	 * <PRE>
	 * createProcessor( this, "Medical:XSL" );
	 * </PRE>
	 * returns a processor that will activate the "Medical" engine followed by the
	 * "XSL" engine. "Medical" might byitself activate a sequence of engines.
	 *
	 * @param engines Colon separated list of engines
	 * @param activator The activator of this processor
	 */
	public static Processor createProcessor( Object activator, String engines )
		throws ProcessorException
	{
		StringTokenizer             tokenizer;
		String                      name;
		ProcessorEngineCreator[]    creators;
		int                         index;

		// Use an array of factories to pass to ChainProcessor. For each engine name,
		// attempt to obtain the engine's creator from the factories registry and
		// add it to the array. Engines will be executed in the same order in which
		// that are recorded in the array.
		tokenizer = new StringTokenizer( engines, ";:" );
		creators = new ProcessorEngineCreator[ tokenizer.countTokens() ];
		for ( index = 0 ; tokenizer.hasMoreTokens() ; ++index )
		{
			name = tokenizer.nextToken();
			creators[ index ] = (ProcessorEngineCreator) _creators.get( name );
			if ( creators[ index ] == null )
				throw new ProcessorException( Resources.format( "Processor001", name ) );
		}
		// Construct and retain a chain processor with the given list of factories.
		return new ChainProcessor( activator, creators );

	}
	/**
	 * Returns the engine creator registered with the generic name. The creator has
	 * been previously registered with {@link #registerEngine}.
	 *
	 * @param name The generic engine name
	 * @return The engine's creator, or null
	 */
	public static ProcessorEngineCreator findEngineCreator( String name )
	{
		return (ProcessorEngineCreator) _creators.get( name );
	}
	/**
	 * Registers a processor engine creator under the generic name. The engine will
	 * be used when {@link #createProcessor} is called with the given engine name.
	 * The creator can also be retrieved with {@link #findEngineCreator}.
	 *
	 * @param name The generic engine name
	 * @param creator The engine's creator
	 */
	public static void registerEngine( String name, ProcessorEngineCreator creator )
	{
		if ( name == null )
			throw new NullPointerException( Resources.format( "Error001", "creator" ) );
		if ( creator == null )
			_creators.remove( name );
		else
			_creators.put( name, creator );
	}
	public static void registerPIEngine( String targetName, ProcessorEngineCreator creator )
	{
		_piRegistry.registerPIEngine( targetName, creator );
	}
}