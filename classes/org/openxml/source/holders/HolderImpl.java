package org.openxml.source.holders;

/**
 * org/openxml/source/holders/HolderImpl.java
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


import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.openxml.DOMFactory;
import org.openxml.util.Log;
import org.openxml.dom.DocumentImpl;
import org.openxml.io.Parser;
import org.openxml.io.XMLStreamReader;
import org.openxml.parser.XMLParser;
import org.openxml.source.*;


/**
 * Default implementation of a document holder. Holder classes are used to
 * provide access to documents through various protocols and storages locations
 * (HTTP, file, JAR, etc), and to support document caching.
 * <P>
 * The default implementation recieves a textual URI and uses that for
 * constructing a URL to retrieve the document source. Caching of the document
 * source is not supported by the default implementation. However, the {@link
 * #getReadOnly} and {@link #newInstance} methods are fully implemented and
 * provide full support if {@link #canCache} and {@link #getReader} are properly
 * implemented. Derived classes are encouraged to use these methods.
 * <P>
 * The document source is typically supplied using an {@link Source} which
 * specifies the system and public identifier, default encoding and requested
 * document class. That information is generally conveyed to the holder through
 * the constructor and the {@link #_uri}, {@link #_encoding} and {@link
 * #_docClass} member variables. {@link #_document} holds the read-only or
 * single instance of the document and {@link #_lastException} holds the last
 * exception as a result of parsing the document.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Holder
 */
class HolderImpl
	extends Holder
{


	/**
	 * Holds the source URI. The interpretation of the URI is up to the source.
	 */
	protected String        _uri;


	/**
	 * Holds the encoding selected for reading the source.
	 */
	protected String        _encoding;


	/**
	 * Holds the document loaded for this source. This is the read-only image
	 * that should be returned by {@link #getReadOnly}.
	 */
	protected Document        _document;


	/**
	 * Holds the document class. This class is passed to the parser which will
	 * return a document derived from this class. For more information see the
	 * <TT>docClass</TT> property in {@link Source}.
	 */
	protected Class            _docClass;


	/**
	 * Holds the last exception issued when parsing the document.
	 */
	protected IOException    _lastException;



	/**
	 * Default constructor accepts source URI, default encoding and document
	 * class. The URI is placed in {@link #_uri}, the encoding is placed in
	 * {@link #_encoding} and the document class in {@link #_docClass}.
	 * If the source cannot be accessed, the constructor may choose to throw
	 * an {@link IOException}.
	 *
	 * @param uri The source URI
	 * @param encoding The language encoding, or null
	 * @param docClass Specific document class or null
	 * @throws IOException The source cannot be accessed
	 */
	protected HolderImpl( String uri, String encoding, Class docClass )
		throws IOException
	{
		if ( uri == null )
			throw new NullPointerException( "Argument 'uri' is null." );
		_uri = uri;
		_encoding = encoding;
		_docClass = docClass;
	}
	public boolean canCache()
	{
		return false;
	}
	public String getEncoding()
	{
		return ( _encoding == null ? "UTF8" : _encoding );
	}
	public IOException getLastException()
	{
		return _lastException;
	}
	public Reader getReader()
		throws IOException
	{
		return getReader( new URL( _uri ).openStream() );
	}
	protected Reader getReader( InputStream is )
		throws IOException
	{
		return new java.io.BufferedReader( new java.io.InputStreamReader( is/*, getEncoding()*/ ) );
	}
	public Document getReadOnly()
	{
		if ( _document == null )
			_document = parseSource();
		if ( canCache() && _document instanceof DocumentImpl )
			( (DocumentImpl) _document ).makeReadOnly();
		return _document;
	}
	public String getURI()
	{
		return _uri;
	}
	public boolean hasModified()
	{
		return true;
	}
	public Document newInstance()
	{
		Document    instance;

		instance = getReadOnly();
		if ( canCache() && instance != null )
			instance = (Document) instance.cloneNode( true );
		return instance;
	}
	/**
	 * Convenience method to parse a document. This method parses a source based
	 * on its type ((@link #_docClass}), using the holder's reader ((@link
	 * #getReader}). The default parsing mode for the document is used with no
	 * validation and stop on fatal errors. If a fatal error is encountered while,
	 * an {@link IOException} is thrown. Otherwise, the parsed document is
	 * returned and other parsing errors are lost.
	 *
	 * @return The parsed document
	 */
	protected Document parseSource()
	{
		Parser   parser;

		try
		{
			parser = DOMFactory.createParser( new java.io.BufferedReader( getReader() ), getURI(), _docClass );
		}
		catch ( IOException except )
		{
			_lastException = except;
			return null;
		}
		try
		{
			if ( _docClass != null && parser instanceof XMLParser )
				_document = ( (XMLParser) parser ).parseDocument( null, _docClass );
			else
				_document = parser.parseDocument();
		}
		catch ( IOException except )
		{
		}
		_lastException = parser.getLastException();
		return _document;
	}
}