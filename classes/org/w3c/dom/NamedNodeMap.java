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
 * Objects implementing the <code>NamedNodeMap</code> interface are used to 
 * represent collections of nodes that can be accessed by name. Note that 
 * <code>NamedNodeMap</code> does not inherit from <code>NodeList</code>; 
 * <code>NamedNodeMaps</code> are not maintained in any particular order. 
 * Objects contained in an object implementing <code>NamedNodeMap</code> may 
 * also be accessed by an ordinal index, but this is simply to allow 
 * convenient enumeration of the contents of a <code>NamedNodeMap</code>, and 
 * does not imply that the DOM specifies an order to these Nodes. 
 */
public interface NamedNodeMap {
    /**
     * Retrieves a node specified by name.
     * @param name The <code>nodeName</code> of a node to retrieve.
     * @return A <code>Node</code> (of any type) with the specified 
     *   <code>nodeName</code>, or <code>null</code> if it does not identify 
     *   any node in the map.
     */
    public Node         getNamedItem(String name);
    /**
     * Adds a node using its <code>nodeName</code> attribute.
     * <br>As the <code>nodeName</code> attribute is used to derive the name 
     * which the node must be stored under, multiple nodes of certain types 
     * (those that have a "special" string value) cannot be stored as the 
     * names would clash. This is seen as preferable to allowing nodes to be 
     * aliased.
     * @param arg A node to store in a named node map. The node will later be 
     *   accessible using the value of the <code>nodeName</code> attribute of 
     *   the node. If a node with that name is already present in the map, it 
     *   is replaced	by the new one.
     * @return If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created the 
     *   <code>NamedNodeMap</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this 
     *   <code>NamedNodeMap</code> is readonly.
     *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     */
    public Node         setNamedItem(Node arg)
                                     throws DOMException;
    /**
     * Removes a node specified by name.
     * @param name The <code>nodeName</code> of the node to remove. When this 
     *   <code>NamedNodeMap</code> contains the attributes attached to an 
     *   element, as returned by the attributes attribute of the 
     *   <code>Node</code> interface, if the removed attribute is known to 
     *   have a default value, an attribute immediately appears containing the
     *    default value as well as the corresponding namespace URI, local 
     *   name, and prefix when applicable.
     * @return The node removed from the map if a node with such a name exists.
     * @exception DOMException
     *   NOT_FOUND_ERR: Raised if there is no node named <code>name</code> in 
     *   the map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this 
     *   <code>NamedNodeMap</code> is readonly.
     */
    public Node         removeNamedItem(String name)
                                        throws DOMException;
    /**
     * Returns the <code>index</code>th item in the map. If <code>index</code> 
     * is greater than or equal to the number of nodes in the map, this 
     * returns <code>null</code>.
     * @param index Index into the map.
     * @return The node at the <code>index</code>th position in the 
     *   <code>NamedNodeMap</code>, or <code>null</code> if that is not a 
     *   valid index.
     */
    public Node         item(int index);
    /**
     * The number of nodes in the map. The range of valid child node indices 
     * is 0 to <code>length-1</code> inclusive. 
     */
    public int          getLength();
    /**
     * Retrieves a node specified by local name and namespace URI. HTML-only 
     * DOM implementations do not need to implement this method.
     * @since DOM Level 2
     * @param namespaceURI The namespace URI of the node to retrieve.
     * @param localName The local name of the node to retrieve.
     * @return A <code>Node</code> (of any type) with the specified local name 
     *   and namespace URI, or <code>null</code> if they do not identify any 
     *   node in the map.
     */
    public Node         getNamedItemNS(String namespaceURI, 
                                       String localName);
    /**
     * Adds a node using its <code>namespaceURI</code> and 
     * <code>localName</code>. HTML-only DOM implementations do not need to 
     * implement this method.
     * @since DOM Level 2
     * @param arg A node to store in a named node map. The node will later be 
     *   accessible using the value of the <code>namespaceURI</code> and 
     *   <code>localName</code> attribute of the node. If a node with those 
     *   namespace URI and local name is already present in the map, it is 
     *   replaced by the new one.
     * @return If the new <code>Node</code> replaces an existing node the 
     *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
     *   is returned.
     * @exception DOMException
     *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
     *   different document than the one that created the 
     *   <code>NamedNodeMap</code>.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this 
     *   <code>NamedNodeMap</code> is readonly.
     *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
     *   <code>Attr</code> that is already an attribute of another 
     *   <code>Element</code> object. The DOM user must explicitly clone 
     *   <code>Attr</code> nodes to re-use them in other elements.
     */
    public Node         setNamedItemNS(Node arg)
                                       throws DOMException;
    /**
     * Removes a node specified by local name and namespace URI. HTML-only DOM 
     * implementations do not need to implement this method.
     * @since DOM Level 2
     * @param namespaceURI The namespace URI of the node to remove.
     * @param localName The local name of the node to remove. When this 
     *   <code>NamedNodeMap</code> contains the attributes attached to an 
     *   element, as returned by the attributes attribute of the 
     *   <code>Node</code> interface, if the removed attribute is known to 
     *   have a default value, an attribute immediately appears containing 
     *   the default value as well as the corresponding namespace URI, local 
     *   name, and prefix.
     * @return The node removed from the map if a node with such a local name 
     *   and namespace URI exists.
     * @exception DOMException
     *   NOT_FOUND_ERR: Raised if there is no node named <code>name</code> in 
     *   the map.
     *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this 
     *   <code>NamedNodeMap</code> is readonly.
     */
    public Node         removeNamedItemNS(String namespaceURI, 
                                          String localName)
                                          throws DOMException;
}

