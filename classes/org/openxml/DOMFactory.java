package org.openxml;

/**
 * org/openxml/DOMFactory.java
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


import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.html.HTMLDocument;
import org.openxml.dom.*;
import org.openxml.dom.html.HTMLDocumentImpl;
import org.openxml.parser.*;
import org.openxml.io.*;
import org.openxml.source.*;
import org.openxml.source.holders.*;
import org.openxml.util.Log;


/**
 * Factory for XML, HTML and DTD documents, parsers and printers. The factory
 * has methods for creating new documents, parsers and printers. The exact
 * type is determined by the document class, this might be {@link Document}
 * ({@link #DOCUMENT_XML}), {@link HTMLDocument} ({@link #DOCUMENT_HTML}),
 * {@link DocumentType} ({@link #DOCUMENT_DTD}) or a user document derived from
 * {@link XMLDocument}.
 * <P>
 * The default document type is controlled by the <TT>openxml.document.class</TT>
 * propety in the OpenXML properties file ({@link <A HREF="properties.html">
 * openxml.prop</A>}). The parser and printer classes for XML, HTML and DTD
 * documents are also controlled by the property file.
 * <P>
 * The method {@link #createDocument} does not guarantee that it will return
 * {@link Document}, although this is the default behavior. To obtain a
 * {@link Document} either pass its class as argument, or call {@link
 * #createXMLDocument}.
 * <P>
 * A newly created parser is only guaranteed to extend {@link Parser}, even
 * if {@link #DOCUMENT_XML} has been specified as the document type. To create
 * a document from a user class, either use {@link Source}, or the following
 * code:
 * <PRE>
 * Parser    parser;
 *
 * parser = DOMFactory.createParser( reader, sourceURI, docClass );
 * if ( parser instanceof XMLParser )
 *     doc = ( (XMLParser) parser ).parseDocument( null, docClass );
 * else
 *     doc = parser.parseDocument();
 * </PRE>
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Document
 * @see XMLElement
 * @see XMLCollection
 */
public class DOMFactory
{


	/**
	 * XML document class. Can be used to request a new XML document, XML
	 * parser or XML printer. Will produce a document of type {@link
	 * Document}.
	 */
	public static final Class       DOCUMENT_XML = Document.class;


	/**
	 * HTML document class. Can be used to request a new HTML document, HTML
	 * parser or HTML printer. Will produce a document of type {@link
	 * HTMLDocument}.
	 */
	public static final Class       DOCUMENT_HTML = HTMLDocumentImpl.class;


	/**
	 * DTD document class. Can be used to request a new DTD document, DTD
	 * parser or DTD printer. Will produce a document of type {@link
	 * DTDDocument}.
	 */
	public static final Class       DOCUMENT_DTD = DocumentType.class;


	/**
	 * Holds the properties for the factory. This object is created on-demand
	 * when a property is first accessed.
	 */
	private static Properties       _xmlProps;


	/**
	 * Holds a singleton holder finder.
	 */
	private static HolderFinder     _finder;


	/**
	 * The signature for the constructor of a parser class (class that
	 * implemented {@link Parser}). Accepts <TT>reader</TT> and
	 * <TT>docClass</TT>.
	 */
	private static final Class[]    _parserSignature =
		new Class[] { Reader.class, String.class };


	/**
	 * The signature for the constructor of a printer class (class that
	 * implemented {@link Printer}). Accepts <TT>writer</TT> and <TT>mode</TT>.
	 */
	private static final Class[]    _printerSignature =
		new Class[] { Writer.class, int.class };


	/**
	 * Identifies the properties file as a resource. The named resource will
	 * be loaded with the DOMFactory class loader. If the resource exists in
	 * a different package, the full package path must be included, preceded
	 * with '/'.
	 */
	private static final String         RESOURCE_PROPS = "openxml.properties";
	
	
	private static final String         MAIN_CATALOG = "res:/org/openxml/source/dtd-catalog/catalog.xml";


	/**
	 * Creates and returns a new XML/HTML/DTD document. The document type is
	 * based on <TT>docClass</TT>, which dictates whether the document is XML,
	 * HTML or DTD. If <TT>docClass</TT> is null, the class type is read from
	 * the property <TT>openxml.document.class</TT>, and if that property is
	 * missing, the default {@link Document} is used.
	 * <P>
	 * Note that the returned document type may or may not be {@link Document},
	 * but it must extend {@link Document}, and that is also true for non-XML
	 * documents.
	 *
	 * @return A new XML/HTML/DTD document
	 * @see Document
	 */
	public static Document createDocument( Class docClass )
	{
		// Returns the document type which is either docClass (must extend
		// Document), or the class from the properties file, or XMLDocument.
		docClass = getDocClass( docClass );
		// Instantiate a new document, report any error encountered and default
		// to XMLDocument is necessary.
		if ( docClass != null && docClass != Document.class &&
			 docClass != XMLDocument.class )
		{
			try
			{
				return (Document) docClass.newInstance();
			}
			catch ( Exception except )
			{
				Log.error( "DOMFactory.createDocument: Could not create new instance of document class [" + docClass.getName() + "] -- defaulting to Document" );
				Log.error( except );
			}
		}
		return new XMLDocument();
	}
	/**
	 * Creates and returns a new DTD document. The document type is
	 * {@link DTDDocument}.
	 *
	 * @return A new DTD document
	 * @see DTDDocument
	 */
	public static DTDDocument createDTDDocument()
	{
		return (DTDDocument) createDocument( DOCUMENT_DTD );
	}
	/**
	 * Creates and returns a new HTML document. The document type is
	 * {@link HTMLDocument}.
	 *
	 * @return A new XML document
	 * @see HTMLDocument
	 */
	public static HTMLDocument createHTMLDocument()
	{
		return (HTMLDocument) createDocument( DOCUMENT_HTML );
	}
	/**
	 * Creates and returns a new XML parser.
	 *
	 * @param input An input stream to the document source
	 * @param sourceURI The source URI
	 * @return A new parser
	 */
	public static Parser createParser( InputStream stream, String sourceName )
		throws IOException
	{
		return createParser( new BufferedReader( new InputStreamReader( stream ) ), sourceName, DOCUMENT_XML );    }
	/**
	 * Creates and returns a new XML/HTML/DTD parser. The parser type is
	 * determined by the document class provided in <TT>docClass</TT>, which
	 * dictates whether the parser is XML, HTML or DTD. If <TT>docClass</TT> is
	 * null, the same rules that govern {@link #createDocument} apply here.
	 * <P>
	 * The parser is only guaranteed to extend {@link Parser} and will use
	 * {@link #createDocument} to create an instance of the parsed document.
	 * To create a document of a user class, either use {@link Source}, or
	 * the following code:
	 * <PRE>
	 * Parser    parser;
	 *
	 * parser = DOMFactory.createParser( reader, sourceURI, docClass );
	 * if ( parser instanceof XMLParser )
	 *     doc = ( (XMLParser) parser ).parseDocument( null, docClass );
	 * else
	 *     doc = parser.parseDocument();
	 * </PRE>
	 *
	 * @param input An input stream to the document source
	 * @param sourceURI The source URI
	 * @param docClass The requested document type
	 * @return A new parser
	 */
	public static Parser createParser( InputStream stream, String sourceName, Class docClass )
		throws IOException
	{
		return createParser( new BufferedReader( new InputStreamReader( stream ) ), sourceName, docClass );    }
	/**
	 * Creates and returns a new XML parser.
	 *
	 * @param reader A reader to the document source
	 * @param sourceURI The source URI
	 * @return A new parser
	 */
	public static Parser createParser( Reader reader, String sourceName )
		throws IOException
	{
		return createParser( reader, sourceName, DOCUMENT_XML );
	}
	/**
	 * Creates and returns a new XML/HTML/DTD parser. The parser type is
	 * determined by the document class provided in <TT>docClass</TT>, which
	 * dictates whether the parser is XML, HTML or DTD. If <TT>docClass</TT> is
	 * null, the same rules that govern {@link #createDocument} apply here.
	 * <P>
	 * The parser is only guaranteed to extend {@link Parser} and will use
	 * {@link #createDocument} to create an instance of the parsed document.
	 * To create a document of a user class, either use {@link Source}, or
	 * the following code:
	 * <PRE>
	 * Parser    parser;
	 *
	 * parser = DOMFactory.createParser( reader, sourceURI, docClass );
	 * if ( parser instanceof XMLParser )
	 *     doc = ( (XMLParser) parser ).parseDocument( null, docClass );
	 * else
	 *     doc = parser.parseDocument();
	 * </PRE>
	 *
	 * @param reader A reader to the document source
	 * @param sourceURI The source URI
	 * @param docClass The requested document type
	 * @return A new parser
	 */
	public static Parser createParser( Reader reader, String sourceURI, Class docClass )
	{
		String        name;
		Class        parserClass;
		Constructor    cnst;

		// Get the specified document class, or the default document class.
		// Either one is expected as the output of the parser, so work with it.
		// Based on the document type decide which is the default parser and
		// the name of the parser class property.
		docClass = getDocClass( docClass );
		if ( HTMLDocument.class.isAssignableFrom( docClass ) )
		{
			name = "openxml.parser.html.class";
			parserClass = HTMLParser.class;
		}
		else
		if ( DTDDocument.class.isAssignableFrom( docClass ) )
		{
			name = "openxml.parser.dtd.class";
			parserClass = DTDParser.class;
		}
		else
		{
			name = "openxml.parser.xml.class";
			parserClass = XMLParser.class;
		}

		// Given the property name, read its value and if valid, attempt to
		// load the named parser class. Make sure this class is indeed a
		// parser. There is no way to check its ability to produce a document
		// of the requested type: the parser for HTML documents might be rigged
		// to only produce DTD documents. Such is life.
		name = getProperty( name );
		if ( name != null )
		{
			try
			{
				parserClass = Class.forName( name );
				if ( ! Parser.class.isAssignableFrom( parserClass ) )
				{
					parserClass = XMLParser.class;
					Log.error( "DOMFactory.createParser: Parser class [" + name + "] is not a supported parser -- defaulting to XMLParser" );
				}
			}
			catch ( ClassNotFoundException except )
			{
				Log.error( "DOMFactory.createParser: Could not locate parser class [" + name + "] -- defaulting to XMLParser" );
			}
		}

		// Parser class known, find the constructor which accepts a reader and
		// sourceURI. This is the minimalist constructor for a parser and is
		// supported by all three parsers. Using that constructor create a new
		// instance of the parser and return it.
		if ( parserClass != null && parserClass != XMLParser.class )
		{
			try
			{
				cnst = parserClass.getConstructor( _parserSignature );
				if ( cnst != null )
					return (Parser) cnst.newInstance( new Object[] { reader, sourceURI } );
			}
			catch ( Exception except )
			{
				Log.error( "DOMFactory.createParser: Could not create new instance of parser class [" + parserClass.getName() + "] -- defaulting to XMLParser" );
				Log.error( except );
			}
		}
		// Anything fails, or if specifically requested, return the default
		// XML parser.
		return new XMLParser( reader, sourceURI );
	}
	/**
	 * Creates and returns a new XML printer.
	 *
	 * @param output A stream for the document output
	 * @param mode The printing mode
	 * @return A new printer
	 *
	 * @deprecated
	 *  This method has become obsolete in favor of the <a
	 *  href="x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
	 *  This method is temporarily provided for backward compatibility but
	 *  will not be included in release 1.1.
	 */
	public static Printer createPrinter( OutputStream stream, int mode )
		throws IOException
	{
		return createPrinter( new XMLStreamWriter( stream ), mode, DOCUMENT_XML );
	}
	/**
	 * Creates and returns a new XML/HTML/DTD printer. The printer type is
	 * determined by the document class provided in <TT>docClass</TT>, which
	 * dictates whether the printer is XML, HTML or DTD. If <TT>docClass</TT> is
	 * null, the same rules that govern {@link #createDocument} apply here.
	 *
	 * @param output A stream for the document output
	 * @param mode The printing mode
	 * @param docClass The document type
	 * @return A new printer
	 *
	 * @deprecated
	 *  This method has become obsolete in favor of the <a
	 *  href="x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
	 *  This method is temporarily provided for backward compatibility but
	 *  will not be included in release 1.1.
	 */
	public static Printer createPrinter( OutputStream stream, int mode, Class docClass )
		throws IOException
	{
		return createPrinter( new XMLStreamWriter( stream ), mode, docClass );
	}
	/**
	 * Creates and returns a new XML printer.
	 *
	 * @param writer A writer for the document output
	 * @param mode The printing mode
	 * @return A new printer
	 *
	 * @deprecated
	 *  This method has become obsolete in favor of the <a
	 *  href="x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
	 *  This method is temporarily provided for backward compatibility but
	 *  will not be included in release 1.1.
	 */
	public static Printer createPrinter( Writer writer, int mode )
		throws IOException
	{
		return createPrinter( writer, mode, DOCUMENT_XML );
	}
	/**
	 * Creates and returns a new XML/HTML/DTD printer. The printer type is
	 * determined by the document class provided in <TT>docClass</TT>, which
	 * dictates whether the printer is XML, HTML or DTD. If <TT>docClass</TT> is
	 * null, the same rules that govern {@link #createDocument} apply here.
	 *
	 * @param writer A writer for the document output
	 * @param mode The printing mode
	 * @param docClass The document type
	 * @return A new printer
	 *
	 * @deprecated
	 *  This method has become obsolete in favor of the <a
	 *  href="x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
	 *  This method is temporarily provided for backward compatibility but
	 *  will not be included in release 1.1.
	 */
	public static Printer createPrinter( Writer writer, int mode, Class docClass )
		throws IOException
	{
		Class        printerClass;
		String        name;
		Constructor    cnst;

		// Get the specified document class, or the default document class.
		// Either one is expected as the input to the parser, so work with it.
		// Based on the document type decide which is the default printer and
		// the name of the printer class property.
		docClass = getDocClass( docClass );
		if ( HTMLDocument.class.isAssignableFrom( docClass ) )
		{
			name = "openxml.printer.html.class";
			printerClass = HTMLPrinter.class;
		}
		else
		if ( DTDDocument.class.isAssignableFrom( docClass ) )
		{
			name = "openxml.printer.dtd.class";
			printerClass = DTDPrinter.class;
		}
		else
		{
			name = "openxml.printer.xml.class";
			printerClass = XMLPrinter.class;
		}

		// Given the property name, read its value and if valid, attempt to
		// load the named printer class. Make sure this class is indeed a
		// printer. There is no way to check its ability to generate a document
		// of the requested type: the printer for HTML documents might be rigged
		// to only generate DTD documents. Such is life.
		name = getProperty( name );
		if ( name != null )
		{
			try
			{
				printerClass = Class.forName( name );
				if ( ! Printer.class.isAssignableFrom( printerClass ) )
				{
					printerClass = XMLPrinter.class;
					Log.error( "DOMFactory.createPrinter: Printer class [" + name + "] is not a supported printer -- defaulting to XMLPrinter" );
				}
			}
			catch ( ClassNotFoundException except )
			{
				Log.error( "DOMFactory.createPrinter: Could not locate printer class [" + name + "] -- defaulting to XMLPrinter" );
			}
		}

		// Printer class known, find the constructor which accepts a writer and
		// printing mode. This is the minimalist constructor for a printer and
		// is supported by all three printers. Using that constructor create a
		// new instance of the printer and return it.
		if ( printerClass != null && printerClass != XMLPrinter.class )
		{
			try
			{
				cnst = printerClass.getConstructor( _printerSignature );
				return (Printer) cnst.newInstance( new Object[] { writer, new Integer( mode ) } );
			}
			catch ( Exception except )
			{
				Log.error( "DOMFactory.createPrinter: Could not create new instance of printer class [" + printerClass.getName() + "] -- defaulting to XMLPrinter" );
				Log.error( except );
			}
		}
		// Anything fails, or if specifically requested, return the default
		// XML printer.
		return new XMLPrinter( writer, mode );
	}
	/**
	 * Creates and returns a new XML document. The document type is {@link
	 * Document}.
	 *
	 * @return A new XML document
	 * @see Document
	 */
	public static Document createXMLDocument()
	{
		return (Document) createDocument( DOCUMENT_XML );
	}
	/**
	 * Returns the specified document class, or the properties file specified
	 * class, or the default. If the specified document class is not valid, a
	 * runtime exception is thrown. If the specified class is null, the name
	 * is read from the properties file and used as the based class. If that
	 * property is missing or not a valid class, the default document class
	 * ({@link Document}) is used.
	 *
	 * @param docClass The specified document class, or null
	 * @return A valid document class, extending {@link Document}
	 */
	public static Class getDocClass( Class docClass )
	{
		String    prop;

		// If the specified document class is invalid, throw an exception,
		// as we do not want to assume default behavior.
		if ( docClass != null )
		{
			if ( docClass == DocumentType.class || docClass == DTDDocument.class )
				return DTDDocument.class;
			if ( ! Document.class.isAssignableFrom( docClass ) )
				throw new IllegalArgumentException( "Requested document class is not a valid class." );
		}
		// Read the property from the properties file and if not missing,
		// attempt to load the named class. If the named class does not extend
		// document, sadly it must be disposed of.
		prop = getProperty( "openxml.document.class" );
		if ( prop != null )
		{
			try
			{
				docClass = Class.forName( prop );
				if ( ! Document.class.isAssignableFrom( docClass ) )
				{
					docClass = null;
					Log.error( "DOMFactory.getDocClass: Document class [" + prop + "] is not a supported document class -- defaulting to Document" );
				}
			}
			catch ( ClassNotFoundException except )
			{
				Log.error( "DOMFactory.getDocClass: Could not locate document class [" + prop + "] -- defaulting to Document" );
			}
		}
		// The default is Document.
		if ( docClass == null )
			docClass = DOCUMENT_XML;
		return docClass;
	}
	/**
	 * Returns a singleton holder finder. This finder has default holder factories
	 * registered for handling network, file, JAR and CLASSPATH document sources,
	 * and mapping for built-in DTDs. Additional holder factories and Xcatalogs
	 * may be specified in the properties file and are loaded and registered the
	 * first time this method is called.
	 *
	 * @return An holder finder
	 */
	public static HolderFinder getHolderFinder()
	{
		XCatalog        catalog;
		String          prop;
		StringTokenizer tokenizer;

		if ( _finder == null )
		{
			_finder = HolderFinderImpl.getHolderFinder();
			catalog = XCatalogFactory.findCatalog( MAIN_CATALOG );
			if ( catalog != null )
				_finder.registerFactory( XCatalogFactory.asHolderFactory( catalog ) );

			prop = DOMFactory.getProperty( "openxml.holder.factories" );
			if ( prop != null )
			{
				tokenizer = new StringTokenizer( prop, ";" );
				while ( tokenizer.hasMoreTokens() )
				{
					prop = tokenizer.nextToken();
					try
					{
						_finder.registerFactory( (HolderFactory) Class.forName( prop ).newInstance() );
						Log.info( "DOMHolderFactory.<init>: Registered holder factory [" + prop + "]" );
					}
					catch ( Exception except )
					{
						Log.error( "DOMHolderFactory.<init>: Failed to register holder factory [" + prop + "] -- class not found or could not be instantiated" );
					}
				}
			}

			prop = DOMFactory.getProperty( "openxml.holder.catalogs" );
			if ( prop != null )
			{
				tokenizer = new StringTokenizer( prop, ";" );
				while ( tokenizer.hasMoreTokens() )
				{
					prop = tokenizer.nextToken();
					try
					{
						catalog = XCatalogFactory.findCatalog( prop );

						_finder.registerFactory( XCatalogFactory.asHolderFactory( catalog ) );
						Log.info( "DOMHolderFactory.<init>: Registered XCatalog from [" + prop + "]" );
					}
					catch ( Exception except )
					{
						Log.error( "DOMHolderFactory.<init>: Failed to register XCatalog from [" + prop + "] -- catalog not found or could not be loaded" );
					}
				}
			}
		}
		return _finder;
	}
	/**
	 * Returns the properties list from the OpenXML properties file. If this
	 * property list is changed, changes will affect the behavior of the factory
	 * and other OpenXML elements.
	 *
	 * @return The properties list
	 */
	public static Properties getProperties()
	{
		String    className;
		Class    docClass;

		if ( _xmlProps == null )
		{
			_xmlProps = new Properties();
			try
			{
				_xmlProps.load( DOMFactory.class.getResourceAsStream( RESOURCE_PROPS ) );
			}
			catch ( Exception except )
			{
				Log.error( "DOMFactory.getProperties: Failed to load properties from resource [" + RESOURCE_PROPS + "]" );
				Log.error( except );
			}
		}
		return _xmlProps;
	}
	/**
	 * Returns the property from the OpenXML properties file.
	 *
	 * @param name The property name
	 * @return Property value or null
	 */
	public static String getProperty( String name )
	{
		return getProperties().getProperty( name );
	}
	public static Source newSource()
	{
		getHolderFinder();
		return new SourceImpl();
	}
}