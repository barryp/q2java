package org.openxml.source;

/**
 * org/openxml/source/XCatalog.java
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


/**
 * Interface into an XCatalog data structure suitable for mapping. XCatalog
 * defines mapping from public identifiers to system identifiers and remapping
 * of system identifiers. This interface defines access to an XCatalog for the
 * purpose of mapping, but does not specify how such catalog should be defined.
 * <P>
 * The following rules govern mapping:
 * <UL>
 * <LI>If public identifier is given, public identifier is mapped into a suitable
 *  system identifier
 * <LI>That system identifier might be subject to further remapping
 * <LI>If public identifier not given or could not be mapped, the supplied
 *  system identifier is used instead
 * <LI>That system identifier might be subject to further remapping
 * </UL>
 * An XCatalog is assumed to be thread safe for mapping requests, and may be
 * used as a primary catalog, a delegate catalog or an extending catalog at
 * any given time or all at once.
 * <P>
 * This catalog supports the XCatalog proposal draft 0.2 posted to the xml-dev
 * mailing list by  <a href="mailto:cowan@locke.ccil.org">John Cowan</a>. The
 * XCatalog DTD can be found at <a href="http://www.ccil.org/~cowan/XML/XCatalog.html">
 * http://www.ccil.org/~cowan/XML/XCatalog.html</a>.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public interface XCatalog
{
	
	
	/**
	 * Called to map a public identifier into a URI. The catalog is first searched
	 * for a possible mapping for the public identifier, then delegates and extending
	 * catalogs are searched. That mapped URI might further be remapped to a
	 * different URI (see {@link #remapURI}).
	 * 
	 * @param publicId The public identifier to map, or null
	 * @return The mapped URI, or null
	 */
	public String mapPublicId( String publicId );
	/**
	 * Called to map the given source into a URI. If a public identifier is specified,
	 * the catalog is first searched for a possible mapping for it. If such a mapping
	 * is found, the mapped URI is returned. That mapped URI might further be remapped
	 * to a different URI. If not public identifier is specified, or the public
	 * identifier cannot be mapped, the system identifier is use instead. The system
	 * identifier is subject to remapping.
	 * 
	 * @param publicId The public identifier to map, or null
	 * @param systemId The system identifier to map, or null
	 * @return The mapped URI, or null
	 */
	public String mapSource( String publicId, String systemId );
	/**
	 * Called to remap a system identifier (URI). Searches this catalog and all
	 * extended catalogs for a suitable remap entry. If such an entry specifies
	 * the exact URI, the mapped-to URI is returned. If no such entry is found,
	 * the original URI is returned.
	 * 
	 * @param uri The URI to remap
	 * @return The original or remapped URI
	 */
	public String remapURI( String uri );
}