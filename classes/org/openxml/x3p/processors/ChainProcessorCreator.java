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
 * Creator for a chain processor and chain processor engine. The creator provides
 * control of which engines will be activated by the chain processor and in what
 * order. A chain processor is created with a snapshot of the engine sequence.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class ChainProcessorCreator
	implements ProcessorEngineCreator
{


	private Vector      _factories;


	public ChainProcessorCreator( ProcessorEngineCreator[] factories )
	{
		int    i;

		for ( i = 0 ; i < factories.length ; ++i )
			appendEngine( factories[ i ] );
	}
	public ChainProcessorCreator( String factories )
	{
		appendEngines( factories );
	}
	public void appendEngine( ProcessorEngineCreator creator )
	{
		if ( _factories == null )
			_factories = new Vector();
		_factories.addElement( creator );
	}
	public void appendEngines( String factories )
	{
		StringTokenizer         tokenizer;
		String                  name;
		ProcessorEngineCreator  creator;

		tokenizer = new StringTokenizer( factories, ";:" );
		while ( tokenizer.hasMoreTokens() )
		{
			name = tokenizer.nextToken();
			try
			{
				creator = (ProcessorEngineCreator) Class.forName( name ).newInstance();
				appendEngine( creator );
			}
			catch ( Exception except )
			{
				org.openxml.util.Log.error( except );
			}
		}
	}
   /**
	 * Constructs and returns a new chain processor to activate the engines in
	 * the specified order. The processor context is not used in this constructor.
	 *
	 * @param ctx The process context under which this engine will process
	 * @return New chain processor engine
	 */
	public ProcessorEngine createEngine( ProcessContext ctx )
	{
		return (ProcessorEngine) createProcessor( null );
	}
   /**
	 * Constructs and returns a new chain processor to activate the engines in
	 * the specified order.
	 *
	 * @param activator The activator object
	 * @return New chain processor
	 */
	public Processor createProcessor( Object activator )
	{
		ProcessorEngineCreator[]    factories;

		if ( _factories == null )
			factories = new ProcessorEngineCreator[ 0 ];
		else
		{
			factories = new ProcessorEngineCreator[ _factories.size() ];
			_factories.copyInto( factories );
		}
		return new ChainProcessor( activator, factories );
	}
	public void insertEngine( ProcessorEngineCreator creator, int index )
	{
		if ( _factories == null )
			_factories = new Vector();
		_factories.insertElementAt( creator, index );
	}
	public ProcessorEngineCreator item( int index )
	{
		if ( _factories != null || index < 0 || index >= _factories.size() )
			return null;
		else
			return (ProcessorEngineCreator) _factories.elementAt( index );
	}
	public int length()
	{
		if ( _factories != null )
			return _factories.size();
		else
			return 0;
	}
	public void removeEngine( ProcessorEngineCreator creator )
	{
		if ( _factories != null )
			_factories.removeElement( creator );
	}
	public String toString()
	{
		int     i;
		String  desc;

		if ( _factories.isEmpty() )
			return Resources.message( "Processor008" );
		desc = "ChainProcessorCreator [" + _factories.firstElement().toString();
		for ( i = 0 ; i < _factories.size() ; ++i )
			desc = desc + ", " + _factories.elementAt( i ).toString();
		return desc + "]";
	}
}