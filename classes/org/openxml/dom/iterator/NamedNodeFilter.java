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
 * Implements a filter for nodes with the specified name, When this filter is used
 * with a node iterator, the iterator will only return nodes with a matching name.
 * This filter was designed to retrieve elements with a specific tag name, by
 * combinding it with a <TT>whatToShow</TT> value of {@link NodeEx#TW_ELEMENT}.
 * It can also be used to retrieve processing instructions with a specific target
 * name or entity references, by selecting a different <TT>whatToShow</TT> value.
 * <P>
 * A node filter can be shared by multiple iterators. When a filters is reused,
 * it is better to retrieve a copy of the filter from a static list. This mechanism
 * is supported by calling the static method {@link #lookup} and should
 * be used for named filters that are routinely reused.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see NodeFilter
 * @see NodeIterator
 */
public final class NamedNodeFilter
	implements NodeFilter
{


	/**
	 * The node name to filter by,
	 */
	private String  _nodeName;


	/**
	 * List of all filters created in the past.
	 */
	private static Hashtable    _filters;


	/**
	 * Constructs a node filter with the specified name. Only nodes with
	 * the given name will be accepted and returned.
	 * Private constructor is only accessible from lookup method.
	 *
	 * @param nodeName The name to filter by
	 */
	private NamedNodeFilter( String nodeName )
	{
		if ( nodeName == null )
			throw new NullPointerException( "Argument 'nodeName' is null." );
		_nodeName = nodeName;
	}
	public boolean acceptNode( Node node )
	{
		return ( node.getNodeName().equals( _nodeName ) );
	}
	/**
	 * Returns a node filter for the specified node name. This method should be used
]    * for recurring filter names as only one filter is held for each filtered name.
	 *
	 * @return The named node filter
	 */
	public static NodeFilter lookup( String nodeName )
	{
		NodeFilter  filter = null;

		// Table created on demand. If filter exists, return it, otherwise create
		// a new filter, store it in the table and then return it.
		if ( _filters == null )
			_filters = new Hashtable();
		else
			filter = (NodeFilter) _filters.get( nodeName );
		if ( filter == null )
		{
			filter = new NamedNodeFilter( nodeName );
			_filters.put( nodeName, filter );
		}
		return filter;
	}
}