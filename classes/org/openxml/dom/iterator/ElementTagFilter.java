package org.openxml.dom.iterator;

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


import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.fi.*;


/**
 * Implements a filter for elements with the specified tag name, When
 * this filter is used with a node iterator, the iterator will only
 * return elements with a matching tag name, or all if the pattern "*"
 * is used.
 * <P>
 * A node filter can be shared by multiple iterators. When a filters
 * is reused, it is better to retrieve a copy of the filter from
 * a static list. This mechanism is supported by calling the static
 * method {@link #lookup} and should be used for named filters that
 * are routinely reused.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NodeFilter
 * @see NodeIterator
 */
public final class ElementTagFilter
	implements NodeFilter
{


	/**
	 * The element tag name to filter by. If this value is null, all
	 * elements are returned; it is set to null when the pattern "*"
	 * is used.
	 */
	private String  _tagName;
	
	
	/**
	 * List of all filters created in the past.
	 */
	private static Hashtable    _filters;


	/**
	 * Constructs an element filter with the specified tag name. Only
	 * elements with the given name will be accepted and returned.
	 * Private constructor is only accessible from lookup method.
	 *
	 * @param nodeName The tag name to filter by
	 */
	private ElementTagFilter( String tagName )
	{
		if ( tagName == null )
			throw new NullPointerException( "Argument 'tagName' is null." );
		if ( tagName.equals( "*" ) )
			_tagName = null;
		else
			_tagName = tagName;
	}
	public boolean acceptNode( Node node )
	{
		return ( node.getNodeType() == Node.ELEMENT_NODE &&
				 ( _tagName == null || node.getNodeName().equals( _tagName ) ) );
	}
	/**
	 * Returns an element filter for the specified tag name. This method
	 * should be used for recurring filter names as only one filter is
	 * held for each filtered name.
	 *
	 * @return The element tag name
	 */
	public static NodeFilter lookup( String tagName )
	{
		NodeFilter  filter = null;

		if ( _filters == null )
			_filters = new Hashtable();
		else
			filter = (NodeFilter) _filters.get( tagName );
		if ( filter == null )
		{
			filter = new ElementTagFilter( tagName );
			_filters.put( tagName, filter );
		}
		return filter;
	}
}