package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLCollectionImpl.java
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


import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.dom.*;


/**
 * Implements {@link org.w3c.dom.html.HTMLCollection} to traverse any named
 * elements on a {@link org.w3c.dom.html.HTMLDocument}. The elements type to
 * look for is identified in the constructor by code. This collection is not
 * optimized for traversing large trees.
 * <p>
 * The collection has to meet two requirements: it has to be live, and it has
 * to traverse depth first and always return results in that order. As such,
 * using an object container (such as {@link java.util.Vector}) is expensive on
 * insert/remove operations. Instead, the collection has been implemented using
 * three traversing functions. As a result, operations on large documents will
 * result in traversal of the entire document tree and consume a considerable
 * amount of time.
 * <p>
 * Note that synchronization on the traversed document cannot be achieved.
 * The document itself cannot be locked, and locking each traversed node is
 * likely to lead to a dead lock condition. Therefore, there is a chance of the
 * document being changed as results are fetched; in all likelihood, the results
 * might be out dated, but not erroneous.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLCollection
 * @see org.openxml.dom.CollectionImpl
 */
final class HTMLCollectionImpl
	extends CollectionImpl
	implements HTMLCollection
{
	
	/**
	 * Request collection of all anchors in document: &lt;A&gt; elements that
	 * have a <code>name</code> attribute.
	 */
	static final short        ANCHOR = 1;
	
	
	/**
	 * Request collection of all forms in document: &lt;FORM&gt; elements.
	 */
	static final short        FORM = 2;
	
	
	/**
	 * Request collection of all images in document: &lt;IMAGE&gt; elements.
	 */
	static final short        IMAGE = 3;
	
	
	/**
	 * Request collection of all Applets in document: &lt;APPLET&gt; and
	 * &lt;OBJECT&gt; elements (&lt;OBJECT&gt; must contain an Applet).
	 */
	static final short        APPLET = 4;
	
	
	/**
	 * Request collection of all links in document: &lt;A&gt; and &lt;AREA&gt;
	 * elements (must have a <code>href</code> attribute).
	 */
	static final short        LINK = 5;
	
	
	/**
	 * Request collection of all options in selection: &lt;OPTION&gt; elments in
	 * &lt;SELECT&gt; or &lt;OPTGROUP&gt;.
	 */
	static final short        OPTION = 6;
	
	
	/**
	 * Request collection of all rows in table: &lt;TR&gt; elements in table or
	 * table section.
	 */
	static final short        ROW = 7;

	
	/**
	 * Request collection of all form elements: &lt;INPUT&gt;, &lt;BUTTON&gt;,
	 * &lt;SELECT&gt;, &lt;TEXT&gt; and &lt;TEXTAREA&gt; elements inside form
	 * &lt;FORM&gt;.
	 */
	static final short        ELEMENT = 8;
	
	
	/**
	 * Request collection of all areas in map: &lt;AREA&gt; element in &lt;MAP&gt;
	 * (non recursive).
	 */
	static final short        AREA = -1;
	

	/**
	 * Request collection of all table bodies in table: &lt;TBODY&gt; element in
	 * table &lt;TABLE&gt; (non recursive).
	 */
	static final short        TBODY = -2;

	
	/**
	 * Request collection of all cells in row: &lt;TD&gt; elements in &lt;TR&gt;
	 * (non recursive).
	 */
	static final short        CELL = -3;

	
	/**
	 * Indicates what this collection is looking for. Holds one of the enumerated
	 * values and used by {@link #collectionMatch}. Set by the constructor and
	 * determine the collection's use for its life time.
	 */
	private short            _lookingFor;
	
	
	/**
	 * Construct a new collection that retrieves element of the specific type
	 * (<code>lookingFor</code>) from the specific document portion
	 * (<code>topLevel</code>).
	 * 
	 * @param topLevel The element underneath which the collection exists
	 * @param lookingFor Code indicating what elements to look for
	 */
	HTMLCollectionImpl( HTMLElement topLevel, short lookingFor )
	{
		super( topLevel );
		_lookingFor = lookingFor;
	}
	/**
	 * Determines if current element matches based on what we're looking for.
	 * The element is passed along with an optional identifier name. If the
	 * element is the one we're looking for, return true. If the name is also
	 * specified, the name must match the <code>id</code> attribute
	 * (match <code>name</code> first for anchors).
	 * 
	 * @param elem The current element
	 * @param name The identifier name or null
	 * @return The element matches what we're looking for
	 */
	protected boolean collectionMatch( Element elem, String name )
	{
		boolean    match;
		
		synchronized ( elem )
		{
			// Begin with no matching. Depending on what we're looking for,
			// attempt to match based on the element type. This is the quickest
			// way to match involving only a cast. Do the expensive string
			// comparison later on.
			match = false;
			switch ( _lookingFor )
			{
			case ANCHOR:
				// Anchor is an <A> element with a 'name' attribute. Otherwise, it's
				// just a link.
				match = ( elem instanceof HTMLAnchorElement ) &&
						elem.getAttribute( "name" ) != null;
				break;
			case FORM:
				// Any <FORM> element.
				match = ( elem instanceof HTMLFormElement );
				break;
			case IMAGE:
				// Any <IMG> element. <OBJECT> elements with images are not returned.
				match = ( elem instanceof HTMLImageElement );
				break;
			case APPLET:
				// Any <APPLET> element, and any <OBJECT> element which represents an
				// Applet. This is determined by 'codetype' attribute being
				// 'application/java' or 'classid' attribute starting with 'java:'.
				match = ( elem instanceof HTMLAppletElement ) ||
						( elem instanceof HTMLObjectElement &&
						  ( "application/java".equals( elem.getAttribute( "codetype" ) ) ||
							( elem.getAttribute( "classid" ) != null &&
							  elem.getAttribute( "classid" ).startsWith( "java:" ) ) ) );
				break;
			case ELEMENT:
				// All form elements implement HTMLFormControl for easy identification.
				match = ( elem instanceof HTMLFormControl );
				break;
			case LINK:
				// Any <A> element, and any <AREA> elements with an 'href' attribute.
				match = ( ( elem instanceof HTMLAnchorElement ||
							elem instanceof HTMLAreaElement ) &&
						  elem.getAttribute( "href" ) != null );
				break;
			case AREA:
				// Any <AREA> element.
				match = ( elem instanceof HTMLAreaElement );
				break;
			case OPTION:
				// Any <OPTION> element.
				match = ( elem instanceof HTMLOptionElement );
				break;
			case ROW:
				// Any <TR> element.
				match = ( elem instanceof HTMLTableRowElement );
				break;
			case TBODY:
				// Any <TBODY> element (one of three table section types).
				match = ( elem instanceof HTMLTableSectionElement &&
						  elem.getTagName().equals( "tbody" ) );
				break;
			case CELL:
				// Any <TD> element.
				match = ( elem instanceof HTMLTableCellElement );
				break;
			}
		
			// If element type was matched and a name was specified, must also match
			// the name against either the 'id' or the 'name' attribute. The 'name'
			// attribute is relevant only for <A> elements for backward compatibility.
			if ( match && name != null )
			{
				// If an anchor and 'name' attribute matches, return true. Otherwise,
				// try 'id' attribute.
				if ( elem instanceof HTMLAnchorElement &&
					 name.equals( elem.getAttribute( "name" ) ) )
					return true;
				match = name.equals( elem.getAttribute( "id" ) );
			}
		}
		return match;
	}
	/**
	 * Returns true if scanning methods should iterate through the collection.
	 * When looking for elements in the document, recursing is needed to traverse
	 * the full document tree. When looking inside a specific element (e.g. for a
	 * cell inside a row), recursing can lead to erroneous results.
	 * 
	 * @return True if methods should recurse to traverse entire tree
	 */
	protected boolean recurse()
	{
		return _lookingFor > 0;
	}
}