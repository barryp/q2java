/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 *
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 *
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


/**
 * Dec 23, 1999
 * - NodeImpl no longer support child nodes, all such functionality has been
 *   moved to ParentNodeImpl.
 * + Added namespace support in interface with getNamespaceURI(),
 *   getPrefix() and getLocalName(), but the actual code is only
 *   implemented in ParentNSNodeImpl.
 * + By definition _ownerDocument is null for a Document and a DocumentType
 *   not used with any document.
 * - Removed all iterator support code and moved to ParentNodeImpl.
 * + Added EmptyNodeList class to support empty node lists on nodes that
 *   have no childs.
 * Dec 25, 1999
 * + Removed read-only capability for speed improvement.
 **/


package org.openxml.dom;
    

import java.util.*;
import org.w3c.dom.*;


/**
 * {@link org.w3c.dom.Node} is the primary class in the DOM, representing a
 * single node in the document tree. All other classes derived and extend
 * their major functionality from this class.
 * <P>
 * This class is abstract. All derived classes must extend {@link
 * #getNodeType}. This class does not support child nodes, all classes
 * supporting child nodes must extend {@link ParentNodeImpl}. In addition,
 * derived classes might wish to extend {@link #cloneNode(boolean)},
 * {@link #equals} and {@link #toString()} and other methods as necessary.
 * Many methods cannot be extended.
 * <P>
 * For speed and consistency, this is a "kitchen sink" class. It implements
 * most of the functionality for all derived class types.
 * <P>
 * {@link NodeImpl} does not support child nodes, such support is available
 * in the extended class {@link ParentNodeImpl}. Namespace support (for
 * element and attribute) is available in {@link ParentNSNodeImpl}.
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 */
public abstract class NodeImpl
    implements Node, Cloneable
{
    
    
    /**
     * Abstract method must be implemented by each node class.
     * 
     * @see org.w3c.dom.Node#getNodeType
     */
    public abstract short getNodeType();
    

    /**
     * Returns the name of the node, set from the constructor. Some derived
     * classes do not have the notion of a name, and will return the same
     * name each time. They should do so by setting the default name (e.g.
     * <TT>"#comment"</TT>) in the constructor. This value is never null.
     * 
     * @see org.w3c.dom.Node#getNodeName
     */
    public String getNodeName()
    {
        return _nodeName;
    }


    public String getNamespaceURI()
    {
	// Only supported in ParentNSNodeImpl.
        return null;
    }


    public String getPrefix()
    {
	// Only supported in ParentNSNodeImpl.
	return null;
    }


    public void setPrefix( String newPrefix )
    {
	// Only supported in ParentNSNodeImpl.
    }


    public String getLocalName()
    {
	// Only supported in ParentNSNodeImpl. Must return the node name.
	return getNodeName();
    }


    /**
     * Returns the value of the node. Depending on the node type, this value
     * is either the node value (e.g. the text in {@link org.w3c.dom.Text}),
     * or always null is node has no notion of a value (e.g. {@link
     * org.w3c.dom.Element}). For complete list of which node types will return
     * what, see {@link #setNodeValue}.
     * 
     * @return Value of node, null if node has no value
     */
    public final String getNodeValue()
    {
        return _nodeValue;
    }
    
    
    /**
     * Changes the value of the node. Not all node types support the notion of
     * a value. If the value is not supported by a particular node type, 
     * it will throw an exception when calling this method. The following
     * table specifies which node types support values:
     * <PRE>
     * Element                Not supported
     * Attr                   Supported
     * Text                   Supported
     * CDATASection           Supported
     * EntityReference        Not supported
     * Entity                 Not supported
     * ProcessingInstruction  Supported
     * Comment                Supported
     * Document               Not supported
     * DocumentType           Not supported
     * DocumentFragment       Not supported
     * Notation               Not supported
     * </PRE>
     * For most node types, if the value is set to null, {@link #getNodeValue}
     * will return an empty string instead.
     * 
     * @param value New value of node
     * @throws org.w3c.dom.DOMExceptionImpl <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
     *  Node is read-only and cannot be modified
     * @throws org.w3c.dom.DOMExceptionImpl <TT>NO_DATA_ALLOWED_ERR</TT>
     *  This node does not support a value
     */
    public void setNodeValue( String value )
    {
        /*
	if ( isReadOnly() )
            throw new DOMExceptionImpl( DOMException.NO_MODIFICATION_ALLOWED_ERR );
	*/
        _nodeValue = value == null ? "" : value;
    }
    

    /**
     * Returns the parent node of this node. Node may not necessarily have a
     * parent node. If node has been created but not added to any other node,
     * it will be parentless. The {@link org.w3c.dom.Document} node is always
     * parentless.
     * 
     * @return Parent node of this node
     */
    public Node getParentNode()
    {
        return _parent;
    }
    
    
    /**
     * Returns a {@link NodeList} object that can be used to traverse this
     * node's children. The node list is live, so every change to this node
     * is reflected in it. If the node does not support children, returns
     * an empty node list.
     * 
     * @return {@link org.w3c.dom.NodeList} on this node
     * @see org.w3c.dom.NodeList
     */
    public NodeList getChildNodes()
    {
	// No children supported by this node.
        return new EmptyNodeList();
    }
    
    
    /**
     * Returns the first child of the node. If node has no children,
     * returns null.
     * 
     * @return First child or null 
     */
    public Node getFirstChild()
    {
	// No children supported by this node.
        return null;
    }
    

    /**
     * Returns the last child of the node. If node has no children, 
     * returns null.
     * 
     * @return Last child or null 
     */
    public Node getLastChild()
    {
	// No children supported by this node.
        return null;
    }
    
    
    /**
     * Returns the previous sibling of this node. If node has no previous
     * siblings, returns null.
     * 
     * @return Previous sibling or null 
     */
    public Node getPreviousSibling()
    {
        return _prevNode;
    }

    
    /**
     * Returns the next sibling of this node. If node has no next siblings,
     * returns null.
     * 
     * @return Next sibling or null 
     */
    public Node getNextSibling()
    {
        return _nextNode;
    }

    
    /**
     * Return attributes of node. Returns null unless node is of type {@link
     * Element}, in which case the returned {@link NamedNodeMap} will provide
     * access to all the element's
     * attributes.
     * 
     * @return Attributes of node or null
     */
    public NamedNodeMap getAttributes()
    {
	// No attributes supported by this node.
        return null;
    }
    
    
    /**
     * Return the document associated with this node. The document is used to
     * create new nodes. If this is a {@link Document}, or a
     * {@link DocumentType} which is not used with any document, will
     * return null.
     *
     * @return The document associated with this node
     */
    public final Document getOwnerDocument()
    {
        return _ownerDocument;
    }

    
    /**
     * Return true if there are any childern to this node. Less intensive than
     * calling {@link #getChildNodes}.
     * 
     * @return True if node has any children
     */
    public  boolean hasChildNodes()
    {
	// No children supported by this node.
        return false;
    }
    

    /**
     * Insert <TT>newChild</TT> as the last child of this parent.
     * <P>
     * If <TT>newChild</TT> is null, <TT>newChild</TT> is not a compatible
     * type, or childern are not supported by this node type, an exception
     * is thrown.
     * <P>
     * <TT>newChild</TT> is removed from its original parent before adding to
     * this parent. If <TT>newChild</TT> is a {@link DocumentFragment}, all
     * its children are inserted one by one into this parent.
     * 
     * @param newChild The new child to add
     * @return The newly inserted child
     * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
     *  Node is read-only and cannot be modified
     * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
     *  Children are not supported by this node type, or <TT>newChild</TT> is
     *  not a compatible type for this node
     */
    public Node appendChild( Node newChild )
    {
        throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
            "No childern supported by this node type." );
    }

    
    /**
     * Insert <TT>newChild</TT> in this parent, before the existing child
     * <TT>refChild</TT>. If <TT>refChild</TT> is null, insert
     * <TT>newChild</TT> as the last child of this parent, akin to calling
     * {@link #appendChild}.
     * <P>
     * If <TT>newChild</TT> is null, <TT>newChild</TT> does not belong to
     * this DOM, <TT>refChild</TT> is not a direct child of this node, or
     * childern are not supported by this node type, an exception is thrown.
     * <P>
     * <TT>newChild</TT> is removed from its original parent before adding
     * to this parent. If <TT>newChild</TT> is a {@link DocumentFragment}, all
     * its children are inserted one by one into this parent.
     * 
     * @param newChild The new child to add
     * @param refChild Insert new child before this child, or insert at the end
     *  if this child is null
     * @return The newly inserted child
     * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
     *  Node is read-only and cannot be modified
     * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
     *  Children are not supported by this node type, or <TT>newChild</TT>
     *  is not a compatible type for this node
     * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
     *  <TT>oldChild</TT> is not null and not a direct child of this node
     */
    public Node insertBefore( Node newChild, Node refChild )
        throws DOMException
    {
        throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
            "No childern supported by this node type." );
    }
    
    
    /**
     * Remove <TT>oldChild</TT> from this parent. If <TT>oldChild</TT> is not
     * a direct child of this parent, or childern are not supported by this
     * node type, an exception is thrown.
     * 
     * @param oldChild The child to remove
     * @return The removed child
     * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
     *  Node is read-only and cannot be modified
     * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
     *  Children are not supported by this node type
     * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
     *  <TT>oldChild</TT> is not a direct child of this node
     */
    public Node removeChild( Node oldChild )
        throws DOMException
    {
        throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
            "No childern supported by this node type." );
    }
    
    
    /**
     * Replace <TT>oldChild</TT> with <TT>newChild</TT>, adding the new child
     * and removing the old one.
     * <P>
     * If <TT>newChild</TT> does not belong to this DOM, <TT>oldChild</TT>
     * is not a direct child of this parent, or childern are not supported
     * by this node type, an exception is thrown.
     * <P>
     * <TT>newChild</TT> is removed from its original parent before adding to
     * this parent. If <TT>newChild</TT> is a {@link DocumentFragment}, all
     * its children are inserted one by one into this parent.
     * 
     * @param newChild The new child to add
     * @param oldChild The old child to take away
     * @return The old child
     * @throws org.w3c.dom.DOMException <TT>NO_MODIFICATION_ALLOWED_ERR</TT>
     *  Node is read-only and cannot be modified
     * @throws org.w3c.dom.DOMException <TT>HIERARCHY_REQUEST_ERR</TT>
     *  Children are not supported by this node type, or <TT>newChild</TT> is
     *  not a compatible type for this node
     * @throws org.w3c.dom.DOMException <TT>NOT_FOUND_ERR</TT>
     *  <TT>oldChild</TT> is not a direct child of this node
     */
    public Node replaceChild( Node newChild, Node oldChild )
        throws DOMException
    {
         throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
             "No childern supported by this node type." );
    }
    
    
    public void normalize()
    {
        // Do nothing in node, only implemented for parent node.
    }


    /**
     * Renders this node read only, preventing it's contents from being
     * modified. Attempts to modify the node's contents will throw an
     * exception. The node's children are also made read-only.
     */
    public final synchronized void makeReadOnly()
    {
        NodeImpl    child;

        return;
	/*
        _readOnly = true;
        // Make all children read-only as well: this allows us to lock a branch
        // but, for example, move it to a different tree.
        child = (NodeImpl) getFirstChild();
        while ( child != null )
        {
            child.makeReadOnly();
            child = (NodeImpl) child.getNextSibling();    
        }
	*/
    }
    
    
    /**
     * Returns true if node is read-only and cannot be modified, or if node
     * belongs to a read-only document.
     * 
     * @return True if node is read-only and cannot be modified
     * @see #makeReadOnly
     */
    public final boolean isReadOnly()
    {
	return false;
	/*
        return _readOnly;
	*/
    }
        

    /**
     * Returns true if the node defines a subtree within which the specified
     * feature is supported, false otherwise.
     *
     * @param feature The package name of the feature set
     * @param version The version number of the feature set (e.g. "2.0" for
     *  Level 2), or any version if null
     * @return True if feature is supported
     */
    public final boolean supports( String feature, String version )
    {
	return DOMImpl.getDOMImplementation().hasFeature( feature, version );
    }

    
    /**
     * Sets the owner document of this node and all its children. When adding
     * a node previously with one document to a new document, it makes sense
     * to switch the owner document. If the new owner is not part of this
     * DOM implementation, an exception will be thrown.
     *
     * @param owner The new owner
     * @throws DOMException The owner is not of this DOM implementation
     */
    protected final synchronized void setOwnerDocument( Document owner )
        throws DOMException
    {
        Node    node;

        if ( owner == null )
            _ownerDocument = null;
        else
        {
            if ( ! ( owner instanceof DocumentImpl ) )
                throw new DOMExceptionImpl( DOMException.WRONG_DOCUMENT_ERR );
            _ownerDocument = (DocumentImpl) owner;
        }
        node = getFirstChild();
        while ( node != null )
        {
            ( (NodeImpl) node ).setOwnerDocument( owner );
            node = node.getNextSibling();
        }
    }


    /**
     * This clone method is called after a new node has been constructed to
     * copy the contents of this node into the new one. It clones in contents
     * but not in context, and guarantees that the cloned node will pass the
     * equality test (see {@link #equals}).
     * <P>
     * <TT>into</TT> must be a valid node of the exact same class as this one.
     * <TT>deep</TT> is true if deep cloning (includes all children nodes) is
     * to be performed. If <TT>deep</TT> is false, the clone might not pass the
     * equality test.
     * <P>
     * Derived classes override and call this method to add per-class variable
     * copying. This method is called by {@link #cloneNode} and the default
     * {@link java.lang.Object#clone} method.
     * <P>
     * Contents cloning duplicates the node's name and value, and its children.
     * It does not duplicate it's context, that is, the node's parent or
     * sibling. Initially a clone node has no parents or siblings. However,
     * the node does belong to the same document, since all nodes must belong
     * to some document. The cloned node is never read-only.
     * 
     * @param into A node into which to duplicate this one
     * @param deep True if deep cloning is required
     */
    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        Node    child;

        // Duplicate node name and value.
        into._nodeName = _nodeName;
        into._nodeValue = _nodeValue;
        into._ownerDocument = _ownerDocument;
    }
    

    /**
     * Returns true if this node and <TTother</TT> are identical by content but
     * not context. Test by content but not context adheres to the following:
     * <UL>
     * <LI>Both nodes must be of the same type, must have the same name and
     *  value (case sensitive) and for elements, the same tag name
     * <LI>For elements, both must have identical attribute list, although
     *  attributes may be specified/unspecified and in different orders
     * <LI>Both nodes must have no children, or must have the exact same
     *  sequence of children; children are tested for equality by calling
     *  {@link #equals} on each pair
     * <LI>However, both nodes may reside in different documents, belong to
     *  different parents, have different siblings, different read-only flags,
     *  etc.
     * <UL>
     * It is guaranteed that the act of cloning a node will return a second
     * node that will pass the test of equality.
     * <p>
     * Note that for large document roots, the equality operation can be very
     * expensive.
     * 
     * @param other The other node to test for equality
     * @return True if both nodes are equal based on their content but not
     *  their context
     */    
    public synchronized boolean equals( Object other )
    {
        NodeImpl    otherX;
        String      value;
        String      valueX;
        boolean     equal;

        // If both objects are the same, return true. If one is null, or they
        // do not belong to the same class, return false. Equality is not
        // tested across different DOMs or different ClassLoaders.
        if ( this == other )
            return true;
        if ( other == null || ! ( other instanceof NodeImpl ) ||
             other.getClass() != getClass() )
            return false;
        synchronized ( other )
        {
            otherX = (NodeImpl) other;
            // Test equality of name and value. Note that values might be null,
            // so equality tests for both being null, or both being equal in
            // value. Temporary variables are used as getNodeValue() might
            // be expensive operation if text substitution is involved.
            equal = this._nodeName.equals( otherX._nodeName );
            if ( equal )
            {
                value = this.getNodeValue();
                valueX = otherX.getNodeValue();
                if ( value != null && valueX != null )
                    equal = value.equals( valueX );
                else
                    equal = ( value == null && valueX == null );
            }
        }
        return equal;
    }

    /**
     * Check the node name one character at a time to assure that no illegal
     * characters are used. Node name must conform to Name token as defined
     * in XML spec, including use of all Unicode letters and digits.
     * Static method used primarly by {@link DocumentImpl} when creating
     * a new node. If the name is invalid, an exception is thrown.
     *
     * @param name The node name
     * @throws DOMException <TT>INVALID_CHARACTER_ERRO</TT> if the node
     *  name is not a valid XML token
     */
     static void checkName( String name )
        throws DOMException
    {
	char ch;
	int  i;

	if ( name.length() == 0 )
	    throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR, "Node name cannot be empty string." );
	
         ch = name.charAt( 0 );
         if ( ! Character.isLetter( ch ) && ch != '_' && ch != ':' )
            throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
         for ( i = 1 ; i < name.length() ; ++i )
         {
            ch = name.charAt( 1 );
            if ( ! Character.isLetterOrDigit( ch ) &&
                 ch != '_' && ch != ':' && ch != '-' && ch != '.' )
                throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
        }
    }

        
    /**
     * Hidden constructor creates a new node. Only one constructor is
     * supported, although cloning is also supported. Owner document must be
     * supplied except for {@link DocumentImpl} and {@link DocumentTypeImpl}.
     * Name must be supplied, either dynamic or static (e.g. "#document#").
     * <P>
     * If <TT>checkName</TT> is true, the supplied named is assumed to be a
     * valid XML name token, one that can contain any Unicode letters and
     * digits, must start with a letter, and may also contain hyphen,
     * underscore, digit or colon.
     * 
     * @param owner Document owner of this node, or null
     * @param name Name of node
     * @param value Initial value of node or null
     * @throws org.w3c.dom.DOMException <TT>INVALID_CHARACTER_ERR</TT>
     *  Node name cannot contain whitespaces or non-printable characters
     */
    protected NodeImpl( DocumentImpl owner, String name, String value )
        throws DOMException
    {
        char    ch;
        int     i;

        if ( name == null )
            throw new NullPointerException( "Argument 'name' is null." );
        _nodeName = name;
        _ownerDocument = owner;
        if ( value != null )
            setNodeValue( value );
    }
    
    
    /**
     * Element declaration node. Not part of the DOM, identifies an element
     * declaration node appearing in the DTD.
     */
    public static final short    ELEMENT_DECL_NODE = 13;

    
    /**
     * Attributes list declaration node. Not part of the DOM, identifies an
     * attributes list declaration node appearing in the DTD..
     */
    public static final short    ATTLIST_DECL_NODE = 14;

    
    /**
     * Parameter entity declaration node. Not part of the DOM, identifies an
     * internal or external parameter entity declaration node appearing in the
     * DTD (see {@link org.openxml.dom.ParamEntity}).
     */
    public static final short    PARAM_ENTITY_NODE = 15;
    
   
    /**
     * This node ia part of a double-linked list that belongs to its parent.
     * This reference identifies the next child in the list. Class access
     * required by derived classes.
     */
    NodeImpl                _nextNode;

    
    /**
     * This node ia part of a double-linked list that belongs to its parent.
     * This reference identifies the previous child in the list. Class access
     * required by derived classes.
     */
    NodeImpl                _prevNode;
    
    
    /**
     * The parent of this node or null if the node has no parent. Class access
     * required by derived classes.
     */
    NodeImpl                    _parent;


    /**
     * The document owner of this node, or the document itself. If the node
     * belongs to any document, this will point to that document. For a
     * document this will be null, for a document type not associated with
     * a document, this will also be null.
     */
    DocumentImpl               _ownerDocument;

    
    /**
     * The name of this node. All nodes have names, some are dynamic (e.g. the
     * tag name of an element), others are static (e.g. "#document").
     */
    String                     _nodeName;
    
    
    /**
     * The value of this node. Not all nodes support values and this might be
     * null for some nodes.
     */
    private String            _nodeValue;
    
    
    /**
     * True if this node is read-only and its contents cannot be modified.
     */
    // private boolean         _readOnly;
   
    
}


/**
 * Implementation of an empty node list, returned when a node list is
 * requested on this node that has no child nodes.
 */
class EmptyNodeList
    implements NodeList
{


    public Node item( int index )
    {
	return null;
    }

    public int getLength()
    {
	return 0;
    }


}

