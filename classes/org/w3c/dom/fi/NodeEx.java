package org.w3c.dom.fi;

import org.w3c.dom.Node;


public interface NodeEx
	extends Node
{


   /**
	 * Iterator should return only elements and text nodes.
	 */
	public static final int   TW_DEFAULT = 0x1A;        // TW_ELEMENT + TW_TEXT


	/**
	 * Iterator should return all nodes.
	 */
	public static final int   TW_ALL = 0xFFFF;


	/**
	 * Iterator should return element nodes.
	 */
	 public static final int   TW_ELEMENT = 0x0002;     // = 1 << 1 (ELEMENT_NODE)


	/**
	 * Iterator should return text and CDATA section nodes.
	 */
	public static final int   TW_TEXT = 0x0018;         // = 1 << 3 (TEXT_NODE)
														// + 1 << 4 (CDATA_SECTION_NODE)


	/**
	 * Iterator should return entity reference nodes.
	 */
	public static final int   TW_ENTITYREF = 0x0020;    // = 1 << 5 (ENTITY_REFERENCE_NODE)


	/**
	 * Iterator should return processing instruction nodes.
	 */
	public static final int   TW_PI = 0x0080;           // = 1 << 7 (PROCESSING_INSTRUCTION_NODE)


	/**
	 * Iterator should return text nodes.
	 */
	public static final int   TW_COMMENT = 0x0100;      //  1 << 4 (COMMENT_NODE)

 
	public NodeIterator createTreeIterator( int whatToShow, NodeFilter nodeFilter );
}