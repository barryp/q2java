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


import org.w3c.dom.*;


/**
 * An execption that occured while processing a document or a node.
 * This exception class adds a reference to the node or node tree being
 * processed and the processor responsible for the exception, for easier
 * tracking of processor logic.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Processor
 */
public class ProcessorException
	extends Exception
{


	/**
	 * The node at which this exception was raised.
	 */
	private transient Node            _node;


	/**
	 * The processor engine that raised this exception.
	 */
	private transient ProcessorEngine _engine;


	/**
	 * Constructor for general processor exception not associated with any engine.
	 *
	 * @param message A descriptive exception message
	 */
	public ProcessorException( String message )
	{
		super( message );
	}
	/**
	 * Constructor for an exception raised when processing the specified node
	 * by the specified engine.
	 *
	 * @param engine The processor engine that raised this exception.
	 * @param node The node at which this exception was raised
	 * @param message A descriptive exception message
	 */
	public ProcessorException( ProcessorEngine engine, Node node, String message )
	{
		super( message );
		_engine = engine;
		_node = node;
	}
	/**
	 * Constructor for an exception raised when processing the specified node
	 * by the processor.
	 *
	 * @param node The node at which this exception was raised
	 * @param message A descriptive exception message
	 */
	public ProcessorException( Node node, String message )
	{
		super( message );
		_node = node;
	}
	/**
	 * Returns the processor engine that raised this exception. If the exception
	 * was raised by an engine, this method will return the responsible engine.
	 * If the exception was raised by the processor, this method will return null.
	 *
	 * @return The processor engine that raised this exception
	 */
	public ProcessorEngine getEngine()
	{
		return _engine;
	}
	/**
	 * Returns the node at which this exception was raised. This may be a node
	 * the could not be processed, the tree top node, or the document being
	 * processed.
	 *
	 * @return The node at which the exception was raised
	 */
	public Node getNode()
	{
		return _node;
	}
}