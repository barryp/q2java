package org.openxml.source.holders;

/**
 * org/openxml/source/holders/XCatalogImpl.java
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


import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.*;
import org.openxml.util.Log;
import org.openxml.source.*;


/**
 * Implementation of an XCatalog that also servers as a holder factory. This
 * object is constructed from an XCatalog document and presents a memory image
 * of that document suitable for mapping under the {@link XCatalog} interface.
 * It might also be registered as a mapping holder factory using the {@link
 * HolderFactory} interface.
 * <P>
 * An XCatalog does not care what role it takes part in. It may be used as a
 * holder factory, a main XCatalog, a delegate catalog, an extending catalog,
 * or all four. It may be used in all these fashions at any given time or all
 * at once and from multiple threads.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XCatalog
 * @see XCatalogFactory
 * @see HolderFactory
 */
final class XCatalogImpl
	extends HolderFactoryImpl
	implements XCatalog
{
	
	
	/**
	 * The URI of the document from which this catalog was loaded.
	 */
	private String      _uri;

	
	/**
	 * Holds all public id to URI mapping defined for this catalog. This reference
	 * is null until the first mapping is placed in it.
	 */
	private Hashtable   _maps;

	
	/**
	 * Holds all URI to URI re-mapping defined for this catalog. This reference
	 * is null until the first rr-mapping is placed in it.
	 */
	private Hashtable   _remaps;

	
	/**
	 * Holds all extending catalogs defined for this catalog. Extending catalogs
	 * are objects of type {@link XCatalog}. This reference is null until the first
	 * extending catalog is placed in it.
	 */
	private Vector      _extends;
	
	
	/**
	 * Holds all delegates defined by this catalog. Delegates are objects of
	 * type {@link XCatalogDelegate}. This reference is null until the first
	 * delagate is placed in it.
	 */
	private Vector      _delegates;
	

	/**
	 * Construct for dummy catalog.
	 * 
	 * @see XCatalogFactory
	 */
	XCatalogImpl()
	{
		_uri = "Dummy catalog";
	}
	/**
	 * Construct a new catalog from the given document. The document is assumed
	 * to be a valid XCatalog document, or else this catalog will not map
	 * properly. This constructor will fail if the document could not be found
	 * or could not be parsed successfully (well formed and validity errors are
	 * ignored).
	 * 
	 * @param uri The document URI
	 * @throws IOException Document could not be found or accessed
	 */
	XCatalogImpl( String uri )
		throws IOException
	{
		Source      source;
		Document    doc;

		_uri = uri;
		source = new SourceImpl( uri, null, null, null );
		doc = source.getDocument();
		if ( doc == null )
		{
			if ( source.getLastException() != null )
				throw new IOException( "Could not load the document [" + uri + "] into memory - parser exception." );
			else
				throw new IOException( "Could not load the document [" + uri + "] into memory - document not found." );
		}
		else
		if ( source.getLastException() != null )
		{
			Log.error( "XCatalog: Encountered parsing errors while loading [" + uri + "]" );
			Log.error( source.getLastException() );
		}
		loadCatalog( doc, uri );
	}
	/**
	 * Construct a new catalog from the given document. The document is assumed
	 * to be a valid XCatalog document, or else this catalog will not map
	 * properly. This constructor will always succeed. If the document URI is
	 * known, it should be supplied and will be used as the base URL.
	 * 
	 * @param dom The XCatalog document
	 * @param uri The document URI, or null
	 */
	XCatalogImpl( Document doc, String uri )
	{
		_uri = uri;
		loadCatalog( doc, uri );
	}
	/**
	 * Retrieves attribute from element regardless of case. XML attributes should
	 * be case sensitive, but in a user created document this cannot be counted
	 * on. This method is used to retrieve the named element, regardless of its
	 * case.
	 * 
	 * @param elem The element to look at
	 * @attrName The attribute to look for
	 * @return The named attribute, or null
	 */
	private String getAttribute( Element elem, String attrName )
	{
		NamedNodeMap    map;
		int             i;
		Node            node;
		
		map = elem.getAttributes();
		for ( i = 0 ; i < map.getLength() ; ++i )
		{
			node = map.item( i );
			if ( node.getNodeName().equalsIgnoreCase( attrName ) )
				return node.getNodeValue();
		}
		return null;
	}
	/**
	 * Called to load the catalog from an XCatalog document. The document is
	 * assumed to be in a valid XCatalog format, however, since many documents
	 * will be hand edited, minor errors are forgiven. This method always
	 * succeeds.
	 * <P>
	 * If the document URI is known, this method will attempt to use it as the
	 * base URL for all references appearing in the document.
	 * <P>
	 * For debugging purposes note that this method is very forgiving and does
	 * not report validity errors in the document, however, it does report
	 * mappings created based on the document. The reported mapping should be
	 * compared to the document contents.
	 * 
	 * @param doc The document holding the XCatalog
	 * @param uri The document URI, or null
	 * @see #getAttribute(Element,String)
	 * @see #makeURI(String,URL)
	 */
	private void loadCatalog( Document doc, String uri )
	{
		Element     elem;
		Node        node;
		String      id;
		String      href;
		XCatalog    catalog;
		URL         baseURL = null;

		// Try using the supplied URI to form the base URL. If it doesn't work,
		// log the error but do nothing else.
		if ( uri != null )
		{
			try
			{
				baseURL = new URL( uri );
			}
			catch ( MalformedURLException except )
			{
				Log.debug( "XCatalog.loadCatalog: Document [" + uri + "]: URI cannot be used as base URL" );
			}
		}
		
		// Get the document element. In a valid document this would be the <XCatalog>
		// element and this method should process its child elements. The first node
		// under <XCatalog> is assumed to be a textual description of the catalog
		// which is logged, but otherwise, unused.
		// In an invalid document, start working on the document contents itself,
		// disregarding the root element.
		elem = doc.getDocumentElement();
		if ( elem != null && elem.getTagName().equals( "xcatalog" ) )
		{
			node = elem.getFirstChild();
			if ( elem.getAttribute( "title" ) != null )
				Log.debug( "XCatalog.loadCatalog: Catalog [" + uri + "] described as: [" + elem.getAttribute( "title" ) +"]" );
		}
		else
		{
			Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: Root element [xcatalog] not found" );
			node = doc.getFirstChild();
		}

		// Itertate through all the nodes looking for elements of specific types.
		// Anything else, be it junk whitespace or unknown element, is shamelessly
		// ignored. Note that element names are matched case insensitive, to help
		// free spirited documents.
		while ( node != null )
		{
			if ( node.getNodeType() == Node.ELEMENT_NODE )
			{
				elem = (Element) node;
				// <BASE> element identifies an alternative base URL for all
				// references that follow this tag. Provided that the HREF
				// attribute is a valid URL.
				if ( elem.getTagName().equals( "base" ) )
				{
					id = getAttribute( elem, "href" );
					if ( id != null )
					{
						try
						{
							baseURL = new URL( id );
							Log.debug( "XCatalog.loadCatalog: New base URL [" + baseURL + "]" );
						}
						catch ( MalformedURLException except )
						{
							Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: Malformed base URL [" + id + "] -- ignoring rest of document" );
							return;
						}
					}
					else
						Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: <base> element missing required attribute" );
				}
				else
				// <MAP> element describes a mapping between a public identifier
				// and a system identifier. Store that one to one mapping in the
				// hashtable.
				if ( elem.getTagName().equals( "map" ) )
				{
					id = getAttribute( elem, "public" );
					href = makeURI( getAttribute( elem, "href" ), baseURL );
					if ( id != null && href != null )
					{
						if ( _maps == null )
							_maps = new Hashtable();
						Log.debug( "XCatalog.loadCatalog: Added mapping from [" + id + "] to [" + href + "]" );
						_maps.put( id, href );
					}
					else
						Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: <map> element missing required attribute" );
				}
				else
				// <REMAP> element describes a remapping of a system identifier
				// to an alternative identifier. Store that one remapping in the
				// hashtable.
				if ( elem.getTagName().equals( "remap" ) )
				{
					id = getAttribute( elem, "system" );
					href = makeURI( getAttribute( elem, "href" ), baseURL );
					if ( id != null && href != null )
					{
						if ( _remaps == null )
							_remaps = new Hashtable();
						Log.debug( "XCatalog.loadCatalog: Added re-mapping from  [" + id + "] to [" + href + "]" );
						_remaps.put( id, href );
					}
					else
						Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: <remap> element missing required attribute" );
				}
				else
				// <DELEGATE> elements describes a mapping between a public
				// identifier prefix and the delegate XCatalog that will handle
				// all such prefixes. The delegate XCatalog is loaded from the
				// factory and an entry is added to the vector.
				if ( elem.getTagName().equals( "delegate" ) )
				{
					id = getAttribute( elem, "public" );
					href = makeURI( getAttribute( elem, "href" ), baseURL );
					if ( id != null && href != null )
					{
						if ( _delegates == null )
							_delegates = new Vector();
						Log.debug( "XCatalog.loadCatalog: Added delegate [" + href + "] for prefixes [" + id + "]" );
						catalog = (XCatalog) XCatalogFactory.findCatalog( href );
						if ( catalog != null )
							_delegates.addElement( new XCatalogDelegate( id, catalog ) );
					}
					else
						Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: <delegate> element missing required attribute" );
				}
				else
				// <EXTEND> element describes an additional XCatalog that should
				// be used for mapping when this one is exhuasted. The extending
				// XCatalog is loaded from the factory and an entry is added to
				// the vector.
				if ( elem.getTagName().equals( "extend" ) )
				{
					href = makeURI( getAttribute( elem, "href" ), baseURL );
					if ( href != null )
					{
						if ( _extends == null )
							_extends = new Vector();
						Log.debug( "XCatalog.loadCatalog: Added extending catalog [" + href + "]" );
						catalog = XCatalogFactory.findCatalog( href );
						if ( catalog != null )
							_extends.addElement( catalog );
					}
					else
						Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: <extend> element missing required attribute" );
				}
				else
					Log.error( "XCatalog.loadCatalog: Document [" + uri + "]: Element [" + elem.getTagName() + "] not recognized -- ignored" );
			}
			node = node.getNextSibling();
		}
		Log.debug( "XCatalog.loadCatalog: Document [" + uri + "]: Loaded catalog into memory" );
	}
	/**
	 * Returns a URI based on the supplied URI and base URL. Used to return
	 * absolute URLs from relative href attributes and the last known base
	 * URL (from &lt;Base> element or document URI). This method will return
	 * <TT>uri</TT> if either parameter is false. If <TT>uri</TT> is a self
	 * sufficient URL, it will be returned. Otherwise, <TT>baseURL</TT> will
	 * be used as the base URL.
	 * 
	 * @param uri The URI or null
	 * @param baseURL The base URL or null
	 * @return Possibly an absolute URI
	 */
	private String makeURI( String uri, URL baseURL )
	{
		URL     url;
		
		if ( uri != null )
		{
			try
			{
				url = new URL( uri );
				return url.toString();
			}
			catch ( MalformedURLException e1 )
			{
				if ( baseURL != null )
				{
					try
					{
						url = new URL( baseURL, uri );
						return url.toString();
					}
					catch ( MalformedURLException e2 )
					{
					}
				}
			}
		}
		return uri;
	}
	public String mapPublicId( String publicId )
	{
		String  uri;

		if ( publicId != null )
		{
			uri = mapPublicId2( publicId );
			if ( uri != null )
				return remapURI( uri );
		}
		return null;
	}
	/**
	 * Called to map a public identifier into a system identifier (URI).
	 * Searches this catalog, its delegates and all extended catalogs for a
	 * suitable map entry. If such an entry specifies the exact public
	 * identifier, the mapped-to URI is returned. If no such entry is found,
	 * null is returned.
	 * 
	 * @param publicId The public identifier to map
	 * @return The mapped URI, or null
	 */
	private String mapPublicId2( String publicId )
	{
		int                 i;
		String              uri;
		XCatalogDelegate    deleg;

		// If there is a mapping list, use it to remap the public id to a URI.
		// If a mapping is found, the search terminates and the mapped URI is
		// returned.
		if ( _maps != null )
		{
			uri = (String) _maps.get( publicId );
			if ( uri != null )
			{
				Log.debug( "XCatalog.mapPublicId: Public id [" + publicId + "]: Mapped by catalog [" + _uri + "] to [" + uri + "]" );
				return uri;
			}
		}
		
		// Next the delegates are searched. If a delagate specifies a prefix of the
		// public id, use that deligate to try and locate the public id.
		if ( _delegates != null )
			for ( i = 0 ; i < _delegates.size() ; ++i )
			{
				deleg = (XCatalogDelegate) _delegates.elementAt( i );
				if ( deleg.doesMatch( publicId ) )
				{
					Log.debug( "XCatalog.mapPublicId: Delegating search of [" + publicId + "] to catalog [" + deleg.getCatalog() + "]" );
					uri = deleg.getCatalog().mapPublicId( publicId );
					if ( uri != null )
						return uri;
				}
			}
		
		// Next the extended catalogs are searched. If an extended catalog can map
		// the public id, the search is terminated and the mapped URI is returned.
		// The search is conducted in a depth-first order.
		if ( _extends != null )
		{
			for ( i = 0 ; i < _extends.size() ; ++i )
			{
				uri = ( (XCatalog) _extends.elementAt( i ) ).mapPublicId( publicId );
				if ( uri != null )
					return uri;
			}
			Log.debug( "XCatalog.mapPublicId: Public id [" + publicId + "]: Not matched in extended catalogs" );
		}
		else
			Log.debug( "XCatalog.mapPublicId: Public id [" + publicId + "]: Not matched and no extended catalogs" );

		// Public id not mapped, return null URI.
		return null;
	}
	public String mapSource( String publicId, String systemId )
	{
		String  uri;

		if ( publicId != null )
		{
			uri = mapPublicId2( publicId );
			if ( uri != null )
				return remapURI( uri );
		}
		if ( systemId != null )
			return remapURI( systemId );
		return null;
	}
	public Holder newHolder( Source source )
	{
		String  uri;
		
		// Attempt to map the source.
		uri = mapSource( source.getPublicId(), source.getURI() );
		// URI might be a mapping of the public identifier, or it might be the
		// unmapped system identifier. In the latter case, calling findHolder()
		// again will enter an infinite loop as URI is mapped into itself.
		// Avoid that possibility.
		if ( uri != null && ! uri.equals( source.getURI() ) )
		{
			// Re-locate the holder but now with a new URI
			source = new SourceImpl( uri, null, source.getEncoding(), source.getDocClass() );
			return findHolder( source );
		}
		// No luck, no mapping, keep on with next holder factory.
		return null;
	}
	public String remapURI( String uri )
	{
		String  remapped;
		int     i;

		// If there is a remapping list, use it to remap the URI to an alternative
		// URI. If a remapping is found, the search terminates and the remapped URI
		// is returned.
		if ( _remaps != null )
		{
			remapped = (String) _remaps.get( uri );
			if ( remapped != null )
			{
				Log.debug( "XCatalog.remapURI: URI [" + uri + "]: Remapped by catalog [" + _uri + "] to [" + remapped + "]" );
				return remapped;
			}
		}

		// Next the extended catalogs are searched. If an extended catalog can remap
		// the URI, the search is terminated and the remapped URI is returned.
		// The search is conducted in a depth-first order.
		if ( _extends != null )
		{
			for ( i = 0 ; i < _extends.size() ; ++i )
			{
				remapped = ( (XCatalog) _extends.elementAt( i ) ).remapURI( uri );
				if ( uri != remapped )
				{
					Log.debug( "XCatalog.remapURI: URI [" + uri + "]: Remapped by extended catalog to [" + remapped + "]" );
					return remapped;
				}
			}
		}
		
		// URI not remapped, but return it.
		return uri;
	}
}