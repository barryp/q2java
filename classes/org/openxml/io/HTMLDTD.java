package org.openxml.io;

/**
 * org/openxml/io/HTMLDTD.java
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


import java.io.*;
import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.html.*;
import org.openxml.dom.*;
import org.openxml.DOMFactory;
import org.openxml.DTDDocument;
import org.openxml.source.*;


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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * 
 * @deprecated
 *  This package has become obsolete in favor of the <a
 *  href="../x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
 *  This package is temporarily provided for backward compatibility but
 *  will not be included in release 1.1.
 */
public final class HTMLDTD
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
	 * The single instance HTML DTD document.
	 */
	private static DTDDocument        _htmlDTD;

	
	/**
	 * Locates the HTML entities file that is loaded upon initialization.
	 * This file is a resource loaded with the default class loader.
	 */
	private static final String        DTD_RESOURCE = "/org/openxml/source/dtd-catalog/HTMLEntities.res";


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
			throw new NullPointerException( "Argument 'name' is null." );
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
	 * Returns a default DTD for HTML documents. Based on the HTML 4.0 strict DTD.
	 * The returned {@link DTDDocument} is a shared read-only instance that is
	 * loaded once from local dtd files.
	 * 
	 * @return The HTML DTD document
	 */
	public static DTDDocument getDTD()
	{
		Source  source;
		Holder  holder;

		if ( _htmlDTD == null )
		{
			try
			{
				source = DOMFactory.newSource();
				source.setURI( "http://www.w3.org/TR/REC-html40/loose.dtd" );
				source.setPublicId( "-//W3C//DTD HTML 4.0//EN" );
				source.setDocClass( Source.DOCUMENT_DTD );
				holder = DOMFactory.getHolderFinder().findHolder( source );
				if ( holder != null )
					_htmlDTD = (DTDDocument) holder.getReadOnly();
				else
					System.out.println( "HTMLDTD.getDTD: Could not locate holder for HTML DTD." );    
			}
			catch ( Exception except )
			{
				System.out.println( "HTMLDTD.getDTD: Error processing HTML DTD. Exception: " +
									except.getClass().getName() );    
				System.out.println( "Message:" + except.getMessage() );    
			}
		}
		if ( _htmlDTD == null )
			_htmlDTD = new DTDDocument();
		return _htmlDTD;
	}
	/**
	 * Initialize upon first access. Will load all the HTML character references
	 * into a list that is accessible by name or character value and is optimized
	 * for character substitution. This method may be called any number of times
	 * but will execute only once.
	 */
	private static void initialize()
	{
		InputStream        is = null;
		XMLStreamReader    reader = null;
		int                index;
		String            name;
		String            value;
		int                code;
		String            line;

		// Make sure not to initialize twice.
		if ( _byName != null )
			return;
		try
		{
			_byName = new Hashtable();
			_byChar = new Hashtable();
			is = HTMLDTD.class.getResourceAsStream( DTD_RESOURCE );
			if ( is == null )
				throw new RuntimeException( "HTMLDTD: Resource " + DTD_RESOURCE + " could not be found." );
			reader = new XMLStreamReader( is );
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
			throw new RuntimeException( "HTMLDTD: Resource " + DTD_RESOURCE + 
				" failed to load. Reason exception: " + except.getClass().getName() );
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
	public static boolean optionalClosingTag( Element elem )
	{
		return ( elem instanceof HTMLParagraphElement || elem instanceof HTMLLIElement ||
				 elem instanceof HTMLHtmlElement || elem instanceof HTMLHeadElement ||
				 elem instanceof HTMLBodyElement );
	}
	/**
	 * Returns true if element supports children. Some HTML elements are empty
	 * by default and makes no sense to write the closing tag. They are
	 * identifier by tag name, allowing XML documents to be printed as HTML.
	 * 
	 * @param elem The element to check
	 * @return True if HTML element with this tag name supports children
	 */
	public static boolean supportsChildren( Element elem )
	{
		String    tagName;
		
		tagName = elem.getTagName();
		return ( ! ( tagName.equals( "AREA" ) || tagName.equals( "BASE" ) ||
					 tagName.equals( "BASEFONT" ) || tagName.equals( "BR" ) ||
					 tagName.equals( "HR" ) || tagName.equals( "IMG" ) ||
					 tagName.equals( "INPUT" ) || tagName.equals( "ISINDEX" ) ||
					 tagName.equals( "LI" ) || tagName.equals( "LINK" ) ||
					 tagName.equals( "META" ) || tagName.equals( "PARAM" ) ) );
		// HTMLAreaElement, HTMLBaseElement, HTMLBaseFontElement
		// HTMLBRElement, HTMLHRElement, HTMLImageElement
		// HTMLInputElement, HTMLIsIndexElement, HTMLLIElement
		// HTMLLinkElement, HTMLMetaElement, HTMLParamElement
	}
}