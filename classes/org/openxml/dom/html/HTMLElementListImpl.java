package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLElementListImpl.java
 * 
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 * 
 * The Initial Developer of this code under the License is Assaf Arkin.
 * Portions created by Assaf Arkin are Copyright (C) 1998, 1999.
 * All Rights Reserved.
 */


import java.util.*;
import org.w3c.dom.*;


/**
 * Implements a list of HTML elements extracted based on their name attribute.
 * The constructor recieves the root element and name. It then obtains all the
 * elements contained within that element that match with their <code>name</code>
 * attribute. The list is then accessible through the {@link #item} method.
 * <P>
 * The resulting list is a snapshot of the searched element at the time this
 * list was created. Updates to the document tree are not reflected in the list.
 * The list is implemented as a fast collection and access to elements in the
 * list is very rapid.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.NodeList
 * @see HTMLElementImpl
 * @see HTMLDocumentImpl
 */
final class HTMLElementListImpl
	implements NodeList
{
	
		
	/**
	 * Holds a list of all the matching elements. This list is accessed by
	 * {@link #item} and {@link #getLength}. Note that this list is not live,
	 * updates to the node tree are not reflected in this list.
	 */
	private Vector            _elements;
	
	
	/**
	  * Constructor receieves an element and <code>name</code> attribute value and
	  * extracts all the* sub elements based on that. After construction this object
	  * is ready for element retrieval.
	 * 
	 * @param element The root element from which to extract all sub elements
	  * @param name The <code>name</code> attribute to match
	 */
	 HTMLElementListImpl( Node element, String name )
	{
		if ( name == null )
			 throw new NullPointerException( "Argument 'name' is null." );
		_elements = new Vector();
		 addElements( element, name );
	}
	/**
	 * Add a single element to the list of elements.
	 * 
	 * @param newElem The element to add
	 */
	void addElement( Element newElem )
	{
		_elements.addElement( newElem );
	}
	/**
	 * Add all the elements contained in the root element and matching the
	  * <code>name</code> attribute. Each element is added by calling {@link
	  * #addElement} and the method is recursed on all sub-elements.
	 * 
	 * @param element The root element from which to extract all sub elements
	  * @param name The <code>name</code> attribute to match
	 */
	void addElements( Node element, String name )
	{
		Node    node;
		
		// Traverse all the child nodes of this element. Each node that is an
		 // element is added to the list if its name attribute matches and this
		// method is recursed on that element.
		node = element.getFirstChild();
		while ( node != null )
		{
			if ( node instanceof Element )
			{
				if ( ( (Element) node ).getAttribute( "name" ).equals( name ) )
					addElement( (Element) node );
				addElements( node, name );
			}
			node = node.getNextSibling();
		}
	}
	public int getLength()
	{
		return _elements.size();
	}
	public Node item( int index )
	{
		return (Node) _elements.elementAt( index );
	}
}