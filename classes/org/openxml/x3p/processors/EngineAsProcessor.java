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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public abstract class EngineAsProcessor
	extends ProcessContextImpl
	implements Processor, ProcessorEngine
{


	public EngineAsProcessor( Object activator )
	{
		super( activator );
	}
	public synchronized void destroy( ProcessContext ctx )
	{
	}
	public ProcessContext getContext()
	{
		return this;
	}
	public abstract Node process( ProcessContext ctx, Node tree )
		throws ProcessorException;
	public synchronized Document process( Document source )
		throws ProcessorException
	{
		return (Document) process( (Node) source );
	}
	public synchronized Node process( Node source )
		throws ProcessorException
	{
		TreeIterator    iterator;
		Node            node;
		Node            result;
		Node            next;

		if ( whatToProcess() == this.PROCESS_TOP )
			return (Document) process( getContext(), source );
		else
		{
			iterator = new TreeIterator( source, whatToProcess() );
			node = iterator.nextNode();
			while ( node != null )
			{
				result = process( node );
				next = iterator.nextNode();
				if ( result == null )
					node.getParentNode().removeChild( node );
				else
				if ( result != node )
					node.getParentNode().replaceChild( result, node );
				node = next;
			}
		}
		return source;
	}
	public abstract int whatToProcess();
}