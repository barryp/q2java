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


package org.openxml.util;


import org.w3c.dom.*;


/**
 * Implementation of a non-live node iterator. This iterator supports filtering and
 * selective node return, traversing only to next node, and is not live with respect
 * to changes in the document. This iterator can be used in instances that do not
 * require a live node iterator, until tree iterators are formalized by the DOM.
 *
 *
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:07 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class TreeIterator
{


    /**
     * Returns the next node. Nodes are iterated in a depth-first order, returning
     * only nodes that match the <TT>whatToShow</TT> mask. The first call will
     * return first node in the tree. The top node of the tree is never returned.
     * Once the tree has been exhuasted, this method will always return null.
     *
     * @return The next node in the tree, or null
     */
    public Node nextNode()
    {
        while ( _current != null )
        {
            // If the current node has childern, immediately begin traversing the
            // first childern (depth-first). Otherwise, casually skip to the next
            // node after this.
            if ( _current.hasChildNodes() )
                _current = _current.getFirstChild();
            else
            {
                // If this node has no next sibling, the next logical node is the
                // next sibling of this node's parent. Go one level up and look for
                // the next sibling. Repeat as often as necessary until reaching the
                // top of the tree. The tree top is defined by the node that originally
                // servers as its top (_tree) -- when you reach it, iteration is over.
                while ( _current.getNextSibling() == null )
                {
                    _current = _current.getParentNode();
                    if ( _current == _tree || _current == null )
                    {
                        _current = null;
                        return null;
                    }
                }
                _current = _current.getNextSibling();
            }
            // If the current node matches the whatToShow mask, return it.
            // Otherwise, iterate and return the next matching node.
            if ( _current == null ||
                 ( 1 << _current.getNodeType() & _whatToShow ) != 0 )
                break;
        }
        return _current;
    }


    /**
     * Constrctor for a new node iterator.
     *
     * @param tree The top of the node tree to iterate
     * @param whatToShow Bit mask indicating what nodes to return
     */
    public TreeIterator( Node tree, int whatToShow )
    {
        if ( tree == null )
            throw new NullPointerException( "Argument 'tree' is null." );
        _tree = tree;
        _current = _tree;
    }


    /**
     * The top node of the iterated tree. This node is the parent of all traveresed
     * nodes, but is never returned during iteration. No parent or sibling of
     * {@link #_tree} is ever returned.
     */
    private Node     _tree;


    /**
     * The current node. This is the last node returned by {@link #nextNode} and
     * is set to {@link #_tree} on initialization.
     */
    private Node        _current;


    /**
     * Mask of node types. This mask determines which node types will be returned
     * from the iterator. The value of <TT>0xFFFF</TT> will return nodes of all types,
     * and the default value <TT>0x1A</TT> will return elements, text and CDATA
     * sections.
     * <P>
     * The mask is defined as follows. For each node type, its node type code is
     * used as the position of the one in the mask, and all masks are combined with
     * a binary and. Thus, the default value is calculated as:
     * <PRE>
     * = ( 1 << ELEMENT_NODE ) + ( 1 << TEXT_NODE ) + ( 1 << CDATA_SECTION_NODE )
     * = ( 1 << 1 ) + ( 1 << 3 ) + ( 1 << 4 )
     * = 0x02 + 0x08 + 0x10
     * = 0x1A
     * </PRE>
     */
    private int         _whatToShow = 0xFFFF;


}
