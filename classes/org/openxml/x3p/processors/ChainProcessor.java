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


import org.w3c.dom.*;
import org.openxml.x3p.*;
import org.openxml.util.*;


/**
 * Implementation of a chain processor and engine. The chain processor executes
 * a number of engines in a predetermined order. It can be used either as a
 * processor, or as an engine controlled by a larger processor. It may be called
 * for any number of documents and will always execute the same sequence of
 * engines on these documents.
 * <P>
 * The chain processor is created by a {@link ChainProcessorCreator} and inherits
 * the sequence of engines from it. Changes to the engine list do not affect the
 * processor after it has been created. Engines as specified by means of their
 * factories.
 * <P>
 * Given a list of engine factories, in the specified order each engine will
 * be constructed, passed the nodes it is interested in, and destroyed. Each
 * engine will process the result of the previous engine.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Processor
 * @see ChainProcessorCreator
 */
public class ChainProcessor
	extends ProcessContextImpl
	implements Processor, ProcessorEngine
{


	/**
	 * Processor engine factories in the same order in which the engines must
	 * be activated.
	 */
	private ProcessorEngineCreator[]    _factories;


	/**
	 * Constructor is only accessible from {@link ChainProcessorCreator}.
	 * <TT>activator</TT> is only required if destined to run as a processor.
	 * <TT>factories</TT> is required even if the creator list is empty.
	 *
	 * @return The activator object
	 * @param factories An array of processor engine factories, the order of
	 *  factories determines the order of engine activation
	 */
	public ChainProcessor( Object activator, ProcessorEngineCreator[] factories )
	{
		super( activator );
		if ( factories == null )
			throw new NullPointerException( Resources.format( "Error001", "factories" ) );
		_factories = factories;
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
		ProcessorEngine engine;
		TreeIterator    iterator;
		Node            node;
		Node            next;
		Node            result;
		int             index;

		// whatToProcess returns PROCESS_TOP, so this node is expected to
		// be the top of the node tree, although we have no way of checking it.

		// Iterate through engines in the specified order. In each iteration
		// create the engine from its creator, process the node tree using it,
		// and destory it before proceeding to the next engine.
		for ( index = 0 ; index < _factories.length ; ++index )
		{
			engine = _factories[ index ].createEngine( ctx );
			if ( Log.DEBUG )
				Log.debug( "ChainProcessor.process: Created processor engine [" + engine + "]" );
			// If the engine is interested in the top node, process the node
			// and move on to the next engine. If the return value is null, we
			// cannot continue, so might as well stop here.
			if ( engine.whatToProcess() == 0 )
			{
				result = engine.process( ctx, source );
				if ( result == null )
				{
					// Null has been returned, just return null (asking that
					// the node be removed from the document tree).
					engine.destroy( ctx );
					return null;
				}
				else
				if ( result != source )
					// Different node has been returned. This node will be
					// returned and will replace the original in the document
					// tree. But first, proceed with processing the result node.
					source = result;
			}
			else
			// If the engine is interested in any other node, iterate down the
			// node tree and process each node in sequence using the engine.
			// Requests to remove a processed node from the tree or replace it
			// are handled directly.
			{

				iterator = new TreeIterator( source, engine.whatToProcess() );
				node = iterator.nextNode();
				while ( node != null )
				{
					next = iterator.nextNode();
					result = engine.process( ctx, node );
					if ( result == null )
					{
						// Null returned and the node is removed from the
						// document tree.
						node.getParentNode().removeChild( node );
					}
					else
					if ( result != node )
					{
						// A replacement node is returne and placed in the
						// document tree instead of the source node. That
						// node will be re-processed next.
						next = result;
						node.getParentNode().replaceChild( result, node );
					}
					node = next;
				}
			}
			// Destroy the engine before proceeding to the next engine. No point
			// in dragging this one along.
			engine.destroy( ctx );
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
		int     i;
		String  desc;

		if ( _factories.length == 0 )
			return Resources.message( "Processor009" );
		desc = "ChainProcessor [" + _factories[ 0 ].toString();
		for ( i = 0 ; i < _factories.length ; ++i )
			desc = desc + ", " + _factories[ i ].toString();
		return desc + "]";
	}
	public int whatToProcess()
	{
		// This engine is only interested in the top node of the tree.
		// It will then iterate the tree and pass each node to the engines,
		return this.PROCESS_TOP;
	}
}