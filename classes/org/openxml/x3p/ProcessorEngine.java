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
import org.w3c.dom.fi.*;


/**
 * Interface defines a processor engine. The engine is activated and its life
 * cycle controlled by the processor. The engine indicates which nodes it wishes
 * to process and provides a single method to process each of these nodes.
 * <P>
 * An engine is created from a {@link ProcessorEngineCreator} with the
 * controlling process context. The engine is then queried to determine
 * what nodes should be passed to it, returning a bit mask of node types.
 * The {@link #process} method is called for each of the nodes in a
 * depth-first traversal order. Once the engine is no longer required,
 * it's {@link #destroy} method is called.
 *
 * <P>
 * The {@link #process} method may return one of four valid values:
 * <UL>
 * <LI>The <TT>source</TT> node unaltered
 * <LI>The <TT>source</TT> node after it or its children has been modified
 * <LI>A replacement node which should be placed in the document tree instead of
 *  the <TT>source</TT> node
 * <LI>Null to indicate that the <TT>source</TT> node should be removed from
 *  the document tree
 * </UL>
 * </P>
 * The engine should not attempt to modify nodes that are not the <TT>source</TT>
 * node passed to it or a child node of the <TT>source</TT> node. If the engine
 * requires to make modification higher up in the tree, it should find some other
 * way to do so (e.g. to perform such processing higher up the tree).
 * <P>
 * Note that {@link #process} may be called for nodes that the engine is not
 * interested in, and then engine should behave accordingly by returning each
 * node as is.
 * <P>
 * The engine need not be thread-safe. An instance of the engine will only be
 * called to process on behalf of a single processor / process context, and in
 * sequential order.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Processor
 * @see ProcessorEngineCreator
 */
public interface ProcessorEngine
{


	/**
	 * Process default nodes. Engine wishes to process element and textual nodes.
	 */
	public static final int   PROCESS_DEFAULT = 0x3A;       // PROCESS_ELEMENT + PROCESS_TEXT +
															// PROCESS_CDATA + PROCESS_ENTITYREF


	/**
	 * Process only the top node in the tree.
	 */
	public static final int   PROCESS_TOP = 0;


	/**
	 * Process all nodes. Engine wishes to process all nodes in the tree..
	 */
	public static final int   PROCESS_ALL = 0xFFFF;


	/**
	 * Process elements. Engine wishes to process only element nodes.
	 */
	 public static final int   PROCESS_ELEMENT = 0x0002;    // = 1 << 1 (ELEMENT_NODE)


	/**
	 * Process text. Engine wishes to process only text node (Text and CDATASection).
	 */
	public static final int   PROCESS_TEXT = 0x0018;        // = 1 << 3 (TEXT_NODE)
															// + 1 << 4 (CDATA_SECTION_NODE)


	/**
	 * Process entity references. Engine wishes to process only entity references.
	 */
	public static final int   PROCESS_ENTITYREF = 0x0020;   // = 1 << 5 (ENTITY_REFERENCE_NODE)


	/**
	 * Process PIs. Engine wishes to process only processing instruction nodes.
	 */
	public static final int   PROCESS_PI = 0x0080;          // = 1 << 7 (PROCESSING_INSTRUCTION_NODE)


	/**
	 * Process comments. Engine wishes to process only comment nodes.
	 */
	public static final int   PROCESS_COMMENT = 0x0100;     //  1 << 4 (COMMENT_NODE)


	/**
	 * Called once to tell the engine it is no longer needed. After this call,
	 * the engine instance will not be used anymore in this context.
	 *
	 * @param ctx The process context
	 */
	public void destroy( ProcessContext ctx );
	/**
	 * Process the node. This method is called for each node the engine is
	 * interested in processing (and even one it's not). The engine may return the
	 * same <TT>source</TT> node, a replacement node or null. If the engine returns
	 * null, the <TT>source</TT> node is removed from the document tree. If the
	 * engine returns a replacement node, that node replaces the original
	 * <TT>source</TT> node in the document tree and processing resumes at the
	 * replacement node. The engine should not modify the document tree above the
	 * <TT>source</TT> node.
	 *
	 * @param ctx The process context
	 * @param source The node to process
	 * @return The source node intact or processed, or a replacement node
	 * @throws ProcessorException Indicates that an error occured during
	 *  processing
	 */
	public Node process( ProcessContext ctx, Node source )
		throws ProcessorException;
	/**
	 * The engine tells the processor which nodes it is interested in processing
	 * by returning a bit mask of node types. If the return value is -1 the
	 * engine indicates it is interested in processing all nodes. If the return
	 * value is 0, the engine indicates it is only interested in processing the
	 * top node of the tree.
	 *
	 * @return The node type mask or zero
	 */
	public int whatToProcess();
}