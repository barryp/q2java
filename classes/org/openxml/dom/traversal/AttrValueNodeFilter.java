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


package org.openxml.dom.traversal;


import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;


/**
 * Implements an attribute value filter. When this filter is used with a
 * node iterator, the iterator will only return elements which have this
 * attribute and can match the attribute's value.
 * <P>
 * A node filter can be shared by multiple iterators. When a filters
 * is reused, it is better to retrieve a copy of the filter from
 * a static list. This mechanism is supported by calling the static
 * method {@link #lookup} and should be used for named filters that
 * are routinely reused.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:05 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see NodeFilter
 * @see NodeIterator
 */
public final class AttrValueNodeFilter
    implements NodeFilter
{


    public short acceptNode( Node node )
    {
        String  value;
        
        if ( node.getNodeType() == Node.ELEMENT_NODE )
        {
            value = ( (Element) node ).getAttribute( _attrName );
            if ( value != null & value.equals( _attrValue ) )
		return FILTER_ACCEPT;
        }
        return FILTER_SKIP;
    }


    /**
     * Returns a node filter for the specified attribute name and value.
     * This method should be used for recurring filter as only one filter
     * is held for each filtered name.
     *
     * @param attrName The filtered attribute's name
     * @param attrValue The filtered attribute's value
     * @return A suitable node filter
     */
    public static NodeFilter lookup( String attrName, String attrValue )
    {
        NodeFilter  filter = null;
        String      key;

        key = attrName + "\0" + attrValue;
        if ( _filters == null )
            _filters = new Hashtable();
        else
            filter = (NodeFilter) _filters.get( key );
        if ( filter == null )
        {
            filter = new AttrValueNodeFilter( attrName, attrValue );
            _filters.put( key, filter );
        }
        return filter;
    }


    /**
     * Constructs an attribute value filter for the specified attribute
     * name and value. Private constructor is only accessible from
     * lookup method.
     *
     * @param attrName The filtered attribute's name
     * @param attrValue The filtered attribute's value
     */
    private AttrValueNodeFilter( String attrName, String attrValue )
    {
        if ( attrName == null || attrValue == null )
            throw new NullPointerException( "Argument 'attrName' or 'attrValue' is null." );
        _attrName = attrName;
        _attrValue = attrValue;
    }


    /**
     * The attribute to filter by. That attribute must be matched by its
     * value.
     */
    private String  _attrName;
    
    
    /**
     * The attribute value to filter by.
     */
    private String  _attrValue;

    
    /**
     * List of all filters created in the past.
     */
    private static Hashtable    _filters;


}
