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
 * + Package name changes to traversal in DOM 2.
 * + Added namespace support in constructor and filter.
 **/


package org.openxml.dom.traversal;


import java.util.*;
import org.openxml.dom.ElementImpl;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;


/**
 * Implements a filter for elements with the specified tag name, When
 * this filter is used with a node iterator, the iterator will only
 * return elements with a matching tag name, or all if the pattern "*"
 * is used.
 * <P>
 * If a namespace URI is provided, the namespace and local name are
 * compared. If the namespace URI is "*", all namespaces are matched
 * and only the local name is compared. If the namespace is not
 * provided, standard lookup occurs.
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
public final class ElementFilter
    implements NodeFilter
{


    public short acceptNode( Node node )
    {
        // We have several combinations of elements to handle:
        // - If namespace URI is null, the element's node name is compared
        // - If the match pattern is "*", all elements are returned.
        // - If ignoring case (HTML), the node name is match case insensitive
        // - If namespace URI is not null, the namespace URI is compared and
        //   the elements' local name
        // - If namespace URI is "*", all namespace URIs are matched
        // - The local name is only compared case sensitive
        if ( node.getNodeType() == Node.ELEMENT_NODE ) {
            if ( _namespaceURI == null ) {
                if ( _localName == MATCH_ALL )
                    return FILTER_ACCEPT;
                if ( ( _ignoreCase && _localName.equalsIgnoreCase( node.getNodeName() ) ||
                       _localName.equals( node.getNodeName() ) ) )
                    return FILTER_ACCEPT;
            } else if ( _namespaceURI == MATCH_ALL ||
                        _namespaceURI.equals( ( (ElementImpl) node ).getNamespaceURI() ) ) {
                if ( _localName == MATCH_ALL || _localName.equals( ( (ElementImpl) node ).getLocalName() ) )
                    return FILTER_ACCEPT;
            }
        }
        return FILTER_SKIP;
    }


    /**
     * Returns an element filter for the specified tag name. This method
     * should be used for recurring filter names as only one filter is
     * held for each filtered name.
     *
     * @return The element tag name
     * @param ignoreCase True if case should be ignored (for HTML)
     */
    public static NodeFilter lookup( String namespaceURI, String localName, boolean ignoreCase )
    {
        NodeFilter  filter = null;
        String      hashName;
        
        // Make sure the tag name is not null. If ignoring case, prefix
        // the tag name with '#', just to make sure we don't mix lower
        // and upper case.
        if ( localName == null )
            throw new NullPointerException( "Argument 'localName' is null" );
        if ( ignoreCase )
            hashName = "~IC~" + localName;
        else
            hashName = localName;
        if ( namespaceURI != null )
            hashName = namespaceURI + "~NS~" + localName;
        
        if ( _filters == null )
            _filters = new Hashtable();
        else
            filter = (NodeFilter) _filters.get( hashName );
        if ( filter == null ) {
            filter = new ElementFilter( namespaceURI, localName, ignoreCase );
            _filters.put( hashName, filter );
        }
        return filter;
    }


    /**
     * Constructs an element filter with the specified tag name. Only
     * elements with the given name will be accepted and returned.
     * Private constructor is only accessible from lookup method.
     *
     * @param namespaceURI The namespace URI, or null
     * @param localName The tag name to filter by
     * @param ignoreCase True if case should be ignored (for HTML)
     */
    private ElementFilter( String namespaceURI, String localName, boolean ignoreCase )
    {
        if ( localName == null )
            throw new NullPointerException( "Argument 'localName' is null." );
        if ( localName.equals( "*" ) )
            _localName = MATCH_ALL;
        else
            _localName = localName;
        _ignoreCase = ignoreCase;
        if ( namespaceURI != null ) {
            if ( namespaceURI.equals( "*" ) )
                _namespaceURI = MATCH_ALL;
            else
                _namespaceURI = namespaceURI;
        }
    }


    /**
     * The element tag name to filter by. If this value equals {@link
     * #MATCH_ALL}, all elements are returned; it is set to {@link
     * #MATCH_ALL} when the pattern "*" is used.
     */
    private String  _localName;


    /**
     * The namespace URI to filter by, or null if not specified.
     * If this value equals {@link #MATCH_ALL}, all namespace URIs
     * are returned; it is set to {@link #MATCH_ALL} when the pattern
     * "*" is used.
     */
    private String  _namespaceURI;
    
    
    /**
     * True if case should be ignored when matching tag name. Used for
     * iterating over HTML documents.
     */
    private boolean _ignoreCase;
    
    
    /**
     * List of all filters created in the past.
     */
    private static Hashtable    _filters;


    /**
     * This pattern can be used for the local name to indicate that all
     * elements should be returned or in the namespace URI to indicate
     * that all namespace URIs must be matched.
     */
    public static final String MATCH_ALL = "*";


}
