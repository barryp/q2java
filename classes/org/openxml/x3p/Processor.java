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
 * Instance of a processor. The processor instance brings together the process
 * context, and one or more processor engines. The process context is created by
 * the processor and placed under its control. This interface does not define how
 * the processor finds or determines the order of activation of engines. The
 * processor can be used to process multiple documents serially using the same
 * context and engines.
 * <P>
 * The methods {@link #process(Document)} and {@link #process(Node)} are functionally
 * equivalent and only exist as a convenience.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ProcessContext
 * @see ProcessorFactory
 */
public interface Processor
{


	/**
	 * Returns the processing context associated with this processor instance.
	 * Each processor instance is associated with a single context and the life
	 * cycle of the processor instance is controlled by the processor.
	 *
	 * @return The process context of this processor instance
	 */
	public ProcessContext getContext();
	/**
	 * Called to process a document. If the processor cannot process the document,
	 * it should return the document unaltered. The processor may alter the document,
	 * or return a different document. In the latter case, the application should
	 * discard the source document. Returning null is possible, but highly
	 * discouraged.
	 *
	 * @param source The document to process
	 * @return The source document intact or processed, or a replacement document
	 * @throws ProcessorException An exception has occured and processing
	 *  could not continue
	 */
	public Document process( Document source )
		throws ProcessorException;
	/**
	 * Called to process a node or node tree. If the processor cannot process the
	 * node, it should return the node unaltered. The processor may alter the node,
	 * or return a different node. In the latter case, the application should
	 * discard the source node. Returning null is possible, but highly discouraged.
	 *
	 * @param source The node or node tree to process
	 * @return The source node intact or processed, or a replacement node
	 * @throws ProcessorException An exception has occured and processing
	 *  could not continue
	 */
	public Node process( Node source )
		throws ProcessorException;
}