/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 */

package org.w3c.dom;

/**
 * The <code>Text</code> interface inherits from <code>CharacterData</code> 
 * and represents the textual content (termed character  data in XML) of an 
 * <code>Element</code> or <code>Attr</code>.  If there is no markup inside 
 * an element's content, the text is contained in a single object 
 * implementing the <code>Text</code> interface that is the only child of the 
 * element. If there is markup, it is parsed into the information items 
 * (elements,  comments, etc.) and <code>Text</code>  nodes that form the 
 * list of children of the element.
 * <p>When a document is first made available via the DOM, there is  only one 
 * <code>Text</code> node for each block of text. Users may create  adjacent 
 * <code>Text</code> nodes that represent the  contents of a given element 
 * without any intervening markup, but should be aware that there is no way 
 * to represent the separations between these nodes in XML or HTML, so they 
 * will not (in general) persist between DOM editing sessions. The 
 * <code>normalize()</code> method on <code>Element</code> merges any such 
 * adjacent <code>Text</code> objects into a single node for each block of 
 * text.
 */
public interface Text extends CharacterData {
    /**
     * Breaks this node into two  nodes at the specified <code>offset</code>, 
     * keeping both in the tree as siblings. This node then only contains all 
     * the content up to the <code>offset</code> point. And a new node of the 
     * same nodeType, which is inserted as the next sibling of this node, 
     * contains all the content at and after the <code>offset</code> point. 
     * When the <code>offset</code> is equal to the length of this node, the 
     * new node has no data.
     * @param offset The 16-bit unit offset at which to split, starting from 
     *   p0.
     * @return The new <code>Text</code> node.
     * @exception DOMException
     *   INDEX_SIZE_ERR: Raised if the specified offset is negative or 
     *   greater than the number of 16-bit units in <code>data</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     */
    public Text         splitText(int offset)
                                  throws DOMException;
}
