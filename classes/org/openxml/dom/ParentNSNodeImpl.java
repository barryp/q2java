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


/**
 * Dec 23, 1999
 * + Implements namespace support for Element and Attribute by
 *   overriding ParentNodeImpl.
 */
package org.openxml.dom;
    

import java.util.*;
import org.w3c.dom.*;



/**
 * Implements a node that has child nodes and supports namespaces.
 * Only {@link AttrImpl} and {@link ElementImpl} require support for
 * both child nodes and namespace handling, and only these two
 * extend this class.
 * <P>
 * This class is abstract. All derived classes must extend {@link
 * #getNodeType}. In addition, derived classes might wish to extend {@link
 * NodeImpl#cloneNode(boolean)}, {@link #equals} and {@link #toString()}
 * and other methods as necessary. Many methods cannot be extended.
 * <P>
 * This class is optimized for searching either by qualified name, namespace
 * URI or local name. The prefix is not stored and must be calculated each
 * time.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see ParentNodeImpl
 */
public abstract class ParentNSNodeImpl
    extends ParentNodeImpl
    implements Node, Cloneable
{
    
  
    /**
     * Returns the namespace URI associated with this node, or null if
     * unspecified. This is the namespace URI given at creation time.
     *
     * @return The namespace URI of this node, null if unspecified
     */
    public final String getNamespaceURI()
    {
	return _namespaceURI;
    }


    /**
     * Returns the namespace prefix of this node, or null if unspecified.
     *
     * @return The namespace prefix of this node, null if unspecified
     */
    public synchronized final String getPrefix()
    {
	int index;

	// If no namespace support or no prefix, return null.
	if ( _localName == _nodeName )
	    return null;

	// Look for the namespace separator and return the prefix part of
	// the name.
	index = _nodeName.indexOf( _nsSeparator );
	if  ( index < 0 )
	    return null;
	else
	    return _nodeName.substring( 0, index - 1 );
    }


    /**
     * Sets the namespace prefix of this node. This will affect {@link
     * #getNodeName}, which holds he qualified name of the node.
     *
     * @param newPrefix The new prefix for the qualified name, or null
     */
    public synchronized final void setPrefix( String newPrefix )
    {
	int index;

	// If no namespace support, do nothing.
	if ( _namespaceURI == null )
	    return;

	// If the new prefix is null, simply remove the old one.
	if ( newPrefix == null )
	{
	    _nodeName = _localName;
	    return;
	}

	// If the new prefix is not null, it must not contain a colon
	// separator.
        index = newPrefix.indexOf( _nsSeparator );
	if ( index >= 0 )
	    throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );

	// Set the node name to consist of the new prefix, separator and
	// local name.
	_nodeName = newPrefix + _nsSeparator + _localName;
    }


    /**
     * Returns the local part of the qualified name of this node.
     * If the prefix was unspecified, the returned value is equal to the
     * node name.
     *
     * @return The local part of the qualified name
     */
    public synchronized final String getLocalName()
    {
        return _localName;
    }


    protected synchronized void cloneInto( NodeImpl into, boolean deep )
    {
        Node    child;

	super.cloneInto( into, deep );
	( (ParentNSNodeImpl) into )._namespaceURI = _namespaceURI;
	( (ParentNSNodeImpl) into )._localName = _localName;
    }


    public synchronized boolean equals( Object other )
    {
        ParentNSNodeImpl otherX;
        boolean          equal;

        // If both objects are the same, return true. If one is null, or they
        // do not belong to the same class, return false. Equality is not
        // tested across different DOMs or different ClassLoaders.
        if ( this == other )
            return true;
        if ( other == null || ! ( other instanceof ParentNodeImpl ) ||
             other.getClass() != getClass() )
            return false;
	if ( ! super.equals( other ) )
	    return false;
        synchronized ( other )
        {
            otherX = (ParentNSNodeImpl) other;
            // Test for children, matching each children and also the
            // exact sequence of the children. This test is recursive.
	    equal = ( ( this._namespaceURI == otherX._namespaceURI ) ||
		      ( this._namespaceURI != null && this._namespaceURI.equals( otherX._namespaceURI ) ) ) &&
		    ( ( this._localName == otherX._localName ) ||
		      ( this._localName != null && this._localName.equals( otherX._localName ) ) );
        }
        return equal;
    }


    /**
     * Hidden constructor creates a new node. Used for creating nodes that
     * are namespace aware.
     * <P>
     * The namespace URI is optional, the qualifiedName is checked to be a
     * valid name with optional prefix and local name parts. If the namespace
     * rules are violated, an exception is thrown.
     *
     * @param owner Document owner of this node, or null
     * @param namespaceURI The namespace URI
     * @param qualifiedName The qualified node name
     * @param value Initial value of node or null
     * @throws org.w3c.dom.DOMException <TT>INVALID_CHARACTER_ERR</TT>
     *  Node name cannot contain whitespaces or non-printable characters
     *  and may use ':' only in compliance with namespace rules
     */
    protected ParentNSNodeImpl( DocumentImpl owner, String namespaceURI,
				String qualifiedName, String value )
        throws DOMException
    {
	super( owner, qualifiedName, value );

	int index;

	_namespaceURI = namespaceURI;
	// Check the qualified name for namespace use. If the separator
	// appears as the first or last character, remove it. If the separator
	// appears more than once, throw an exception. If the separator is the
	// only character, throw an exception.
	if ( _namespaceURI != null )
	{
	    index = _nodeName.indexOf( _nsSeparator );
	    if ( index >= 0 )
	    {
		if ( _nodeName.length() == 1 )
		    throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
		if ( index == 0 )
		{
		    _nodeName = _nodeName.substring( 1 );
		    _localName = _nodeName;
		    index = _nodeName.indexOf( _nsSeparator );
		    if ( index >=0 )
			throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
		}
		else
		if ( index == _nodeName.length() - 1 )
		{
		    _nodeName = _nodeName.substring( 0, index - 1 );
		    _localName = _nodeName;
		}
		else
		{
		    _localName = _nodeName.substring( index + 1 );
		    index = _nodeName.indexOf( _nsSeparator, index + 1 );
		    if ( index >= 0 )
			throw new DOMExceptionImpl( DOMException.INVALID_CHARACTER_ERR );
		}
	    }
	    else
		_localName = _nodeName;
	}
	else
	    _localName = _nodeName;
    }


    /**
     * The namespace URI of this node, or null if unspecified. This is not a
     * computed value that is the result of a namespace lookup, it is merely
     * the namespace URI given at creation time. If this value is not null,
     * this node supports namespaces.
     */
    private String _namespaceURI;


    /**
     * The local part of the name if the node was created with namespace
     * support. Will be equal to {@link #_nodeName} if prefix was not
     * specified. Never null.
     */
    private String _localName;


    /**
     * Identifies the namespace separator distinguishing the prefix and local
     * name parts.
     */
    private static final char _nsSeparator = ':';


}













