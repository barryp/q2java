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
import org.w3c.dom.fi.*;
import org.openxml.x3p.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class PIProcessor
	extends ProcessContextImpl
	implements Processor, ProcessorEngine
{


	private Hashtable       _engines;


	private Dictionary      _factories;


	public PIProcessor( Object activator, Dictionary factories )
	{
		super( activator );
		if ( factories == null )
			throw new NullPointerException( Resources.format( "Error001", "factories" ) );
		_factories = factories;
	}
	protected ProcessorEngine createEngine( ProcessContext ctx, String targetName )
	{
		ProcessorEngine         engine;
		ProcessorEngineCreator  creator;

		if ( _engines != null )
		{
			engine = (ProcessorEngine) _engines.get( targetName );
			if ( engine != null )
				return engine;
		}
		creator = (ProcessorEngineCreator) _factories.get( targetName );
		if ( creator == null )
			return null;
		engine = creator.createEngine( ctx );
		if ( ( engine.whatToProcess() & PROCESS_PI ) == 0 )
			return null;
		if ( _engines == null )
			_engines = new Hashtable();
		_engines.put( targetName, engine );
		return engine;
	}
	public void destroy( ProcessContext ctx )
	{
	}
	public ProcessContext getContext()
	{
		return this;
	}
	public synchronized Node process( ProcessContext ctx, Node source )
		throws ProcessorException
	{
		Enumeration enum;
		ProcessorEngine engine;
		TreeIterator    iterator;
		Node            node;
		Node            next;
		Node            result;
		int             index;

		while ( source.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE )
		{
			engine = createEngine( ctx, source.getNodeName() );
			if ( engine == null )
				return source;
			if ( Log.DEBUG )
				Log.debug( "PIProcessor.process: Created processor engine [" + engine + "]" );
			result = engine.process( ctx, source );
			engine.destroy( ctx );
			if ( result == null )
				 return null;
			else
			if ( result == source )
				return source;
			source = result;
		}

		iterator = new TreeIterator( source, PROCESS_PI );
		node = iterator.nextNode();
		while ( node != null )
		{
			next = iterator.nextNode();
			engine = createEngine( ctx, node.getNodeName() );
			if ( engine != null )
			{
				if ( Log.DEBUG )
					Log.debug( "PIProcessor.process: Created processor engine [" + engine + "]" );
				result = engine.process( ctx, node );
				if ( result == null )
					// Null returned and the node is removed from the
					// document tree.
					node.getParentNode().removeChild( node );
				else
				if ( result != node )
					// A replacement node is returne and placed in the
					// document tree instead of the source node. That
					// node will be re-processed next.
					node.getParentNode().replaceChild( result, node );
			}
			node = next;
		}

		if ( _engines != null )
		{
			enum = _engines.elements();
			while ( enum.hasMoreElements() )
				( (ProcessorEngine) enum.nextElement() ).destroy( ctx );
			_engines = null;
		}
		return source;
	}
	public synchronized Document process( Document source )
		throws ProcessorException
	{
		// Another way for activating the engine's process method,
		return (Document) process( getContext(), source );
	}
	public synchronized Node process( Node source )
		throws ProcessorException
	{
		// Another way for activating the engine's process method,
		return process( getContext(), source );
	}
	public String toString()
	{
		String      desc;
		int         i;
		Enumeration enum;
		String      target;

		enum = _factories.keys();
		if ( ! enum.hasMoreElements() )
			return Resources.message( "Processor002" );
		target = (String) enum.nextElement();
		desc = "PIProcessor [" + target + ":" + _factories.get( target ).toString();
		while ( enum.hasMoreElements() )
		{
			target = (String) enum.nextElement();
			desc = desc + ", " + target + ":" + _factories.get( target ).toString();
		}
		return desc + "]";
	}
	public int whatToProcess()
	{
		// This engine is only interested in processing instructions.
		return PROCESS_PI;
	}
}