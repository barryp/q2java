package org.openxml.x3p.publishers;

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


import java.io.*;
import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.html.*;import org.openxml.util.*;


/**
 * Utility class for accessing information specific to HTML documents.
 * The HTML DTD is expressed as three utility function groups. Two methods
 * allow for checking whether an element requires an open tag on printing
 * ({@link #supportsChildren}) or on parsing ({@link #optionalClosingTag}).
 * <P>
 * Two other methods translate character references from name to value and
 * from value to name. A small entities resource is loaded into memory the
 * first time any of these methods is called for fast and efficient access.
 * <P>
 * The default HTML DTD (HTML 4.0 strict) is also accessible as a single
 * read-only instance ({@link #getDTD}), although obtaining the full DTD
 * can incur a slight delay the first time this method is called.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class HTMLEntities
{


	/**
	 * Table of reverse character reference mapping. Character codes are held
	 * as single-character strings, mapped to their reference name.
	 */
	private static Hashtable        _byChar;


	/**
	 * Table of entity name to value mapping. Entities are held as strings,
	 * character references as <TT>Character</TT> objects.
	 */
	private static Hashtable        _byName;


	/**
	 * Locates the HTML entities file that is loaded upon initialization.
	 * This file is a resource loaded with the default class loader.
	 */
	private static final String     ENTITIES_RESOURCE = "HTMLEntities.res";


	/**
	 * Returns the value of an HTML character reference by its name. If the
	 * reference is not found or was not defined as a character reference,
	 * returns EOF (-1).
	 *
	 * @param name Name of character reference
	 * @return Character code or EOF (-1)
	 */
	public static int charFromName( String name )
	{
		Object    value;

		initialize();
		value = _byName.get( name );
		if ( value != null && value instanceof Character )
			return ( (Character) value ).charValue();
		else
			return -1;
	}
	/**
	 * Defines a new character reference. The reference's name and value are
	 * supplied. Nothing happens if the character reference is already defined.
	 * <P>
	 * Unlike internal entities, character references are a string to single
	 * character mapping. They are used to map non-ASCII characters both on
	 * parsing and printing, primarily for HTML documents. '&lt;amp;' is an
	 * example of a character reference.
	 *
	 * @param name The entity's name
	 * @param value The entity's value
	 */
	private static void defineEntity( String name, char value )
	{
		if ( name == null )
			throw new NullPointerException( Resources.format( "Error001", "name" ) );
		if ( _byName.get( name ) == null )
		{
			_byName.put( name, new Character( value ) );
			_byChar.put( String.valueOf( value ), name );
		}
	}
	/**
	 * Returns the name of an HTML character reference based on its character
	 * value. Only valid for entities defined from character references. If no
	 * such character value was defined, return null.
	 *
	 * @param value Character value of entity
	 * @return Entity's name or null
	 */
	public static String fromChar( char value )
	{
		String    name;

		initialize();
		name = (String) _byChar.get( String.valueOf( value ) );
		if ( name == null )
			return null;
		else
			return name;
	}
	/**
	 * Initialize upon first access. Will load all the HTML character references
	 * into a list that is accessible by name or character value and is optimized
	 * for character substitution. This method may be called any number of times
	 * but will execute only once.
	 */
	private static void initialize()
	{
		InputStream     is = null;
		BufferedReader  reader = null;
		int             index;
		String          name;
		String          value;
		int             code;
		String          line;

		// Make sure not to initialize twice.
		if ( _byName != null )
			return;
		try
		{
			_byName = new Hashtable();
			_byChar = new Hashtable();
			is = HTMLEntities.class.getResourceAsStream( ENTITIES_RESOURCE );
			if ( is == null )
				throw new RuntimeException( Resources.format( "Error004", ENTITIES_RESOURCE ) );
			reader = new BufferedReader( new InputStreamReader( is ) );
			line = reader.readLine();
			while ( line != null )
			{
				if ( line.length() == 0 || line.charAt( 0 ) == '#' )
				{
					line = reader.readLine();
					continue;
				}
				index = line.indexOf( ' ' );
				if ( index > 1 )
				{
					name = line.substring( 0, index );
					++index;
					if ( index < line.length() )
					{
						value = line.substring( index );
						index = value.indexOf( ' ' );
						if ( index > 0 )
							value = value.substring( 0, index );
						code = Integer.parseInt( value );
						defineEntity( name, (char) code );
					}
				}
				line = reader.readLine();
			}
			is.close();
		}
		catch ( Exception except )
		{
			throw new RuntimeException( Resources.format( "Error005",
				ENTITIES_RESOURCE, except.getClass().getName() ) );
		}
		finally
		{
			if ( is != null )
			{
				try
				{
					is.close();
				}
				catch ( Exception except )
				{
				}
			}
		}
	}
	/**
	 * Returns true if element has an optional closing tag. Some HTML elements
	 * do not require a closing tag and one is assumed by default. This test is
	 * done only when parsing, so it checks for valid HTML elements.
	 *
	 * @param elem The element to check
	 * @return True if HTML element does not require closing tag
	 */
	public static boolean optionalClosingTag( String tagName )
	{
		// HTMLParagraphElement, HTMLLIElement, HTMLHtmlElement
		// HTMLHeadElement, HTMLBodyElement
		return ( tagName.equals( "P" ) || tagName.equals( "LI" ) ||
				 tagName.equals( "HTML" ) || tagName.equals( "HEAD" ) ||
				 tagName.equals( "BODY" ) );
	}
	/**
	 * Returns true if element supports children. Some HTML elements are empty
	 * by default and makes no sense to write the closing tag. They are
	 * identifier by tag name, allowing XML documents to be printed as HTML.
	 *
	 * @param elem The element to check
	 * @return True if HTML element with this tag name supports children
	 */
	public static boolean supportsChildren( String tagName )
	{
		// HTMLAreaElement, HTMLBaseElement, HTMLBaseFontElement
		// HTMLBRElement, HTMLHRElement, HTMLImageElement
		// HTMLInputElement, HTMLIsIndexElement, HTMLLIElement
		// HTMLLinkElement, HTMLMetaElement, HTMLParamElement
		return ( ! ( tagName.equals( "AREA" ) || tagName.equals( "BASE" ) ||
					 tagName.equals( "BASEFONT" ) || tagName.equals( "BR" ) ||
					 tagName.equals( "HR" ) || tagName.equals( "IMG" ) ||
					 tagName.equals( "INPUT" ) || tagName.equals( "ISINDEX" ) ||
					 tagName.equals( "LINK" ) || tagName.equals( "META" ) ||
					 tagName.equals( "PARAM" ) ) );
	}
}