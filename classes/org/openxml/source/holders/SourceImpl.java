package org.openxml.source.holders;

/**
 * org/openxml/source/holders/SourceAsynchImpl.java
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
import java.net.*;
import java.beans.*;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLDocument;
import org.openxml.util.Log;
import org.openxml.source.*;


/**
 * Defines the source for an XML document and parses the document. This class
 * supports a bean-like and utility interfaces. An XML source is constructed
 * to describe the location of an XML document, the document type and the
 * default encoding. The document is then parsed and returned, including any
 * errors generated during parsing.
 * <P>
 * The bean-like interface is realized through get/set methods that affect
 * its properties. The document is then retrieved by calling {@link
 * #getDocument}. If a new document is required, the properties can be
 * changed and the document retrieved by calling {@link #forget} first.
 * <P>
 * A source is identified by four parameters. The URI specifies a source that
 * can be a network URL, file URL, resource contained in JAR file, etc. The
 * public identifier is an alternative named that may be mapped to a URI.
 * A default character encoding may be specified and will be used, unless
 * the document source enforces another encoding, e.g. in the content type
 * returned from a Web server, or in the document declaration.
 * <P>
 * A specific document type may be requested and will be returned, unless
 * the document source indicates a different document type, e.g. in the
 * content type returned from a Web server. The document type is specified
 * by requesting an XML document (null, or {@link #DOCUMENT_XML}), an HTML
 * document ({@link #DOCUMENT_HTML}) or an HTML document ({@link
 * #DOCUMENT_HTML}). User XML documents may also be requested by passing
 * a class that extends {@link Document}, see the later for more details.
 * <P>
 * {@link #setReadOnly} may be set to request a read only image of the document.
 * That document may not be edited, but may wholly or partly cloned into an
 * editable document. The default behavior returns a new document instance each
 * time and that document may be modified without affecting previously returned
 * instances. Put otherwise, <TT>getDocument() != getDocument()</TT>.
 * <P>
 * The source is parsed and retrieved by calling {@link #getDocument}. This
 * will return a document of the requested class, unless the source forced
 * a different class to be parsed. If the document cannot be found or parsed
 * completely, null is returned. The error exception may be obtained by calling
 * {@link #getLastException}. The returned exception class may be castable
 * to {@link org.openxml.parser.ParseException}.
 * <P>
 * The source may be parsed asynchronously by calling {@link #asynch}. A new
 * thread will be initiated for parsing the document in the background.
 * {@link #getDocument} will then return that document. Some setter methods
 * will be blocked until {@link #asynch} completes parsing the document.
 * <P>
 * A number of static methods are provided for directly reading documents
 * or known types. These methods simply wrap a source constructor and call
 * to {@link #getDocument}.
 * <P>
 * XML sources benefit from parsed document caching, public identifier mapping,
 * and support for resourc documents that is provided by the holder mechanism.
 * For more information, refer to {@link HolderFinderImpl}. The following URI
 * formats are natively supported by this mechanism:
 * <UL>
 * <LI>Network URLs beginning with <TT>http://</TT> or <TT>ftp://</TT>
 * <LI>File URLs beginning with <TT>file:/</TT> or <TT>file:///</TT>
 * <LI>Files contained in JAR files, as returned by {@link Class#getResource}
 * <LI>Resources retrieved from the default class loader, with the syntax
 *  <TT>res:/<uri></TT> where <TT>uri</TT> is of the form <TT>pkg/doc.xml</TT>
 * <LI>Public identifiers mapped into resources retrieved from the default
 *  class loader or from one of the above URLs
 * </UL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Source
 */
public class SourceImpl
	extends Source
	implements Runnable, Serializable
{
	
	
	/**
	 * The URI for this source.
	 * 
	 * @serial
	 */
	protected String                    _uri;

	
	/**
	 * The public identifier for this source.
	 * 
	 * @serial
	 */
	protected String                    _publicId;
	
	
	/**
	 * The encoding for this source.
	 * 
	 * @serial
	 */
	protected String                    _encoding;
	
	
	/**
	 * The document class for this source.
	 * 
	 * @serial
	 */
	protected Class                     _docClass;
	
	
	/**
	 * Holds the document retrieved by {@link #run}, either inside {@link
	 * #getDocument} or after a call to {@link #asynch}.
	 */
	protected transient Document        _document;
	

	/**
	 * Holds the last exception issued when the document was last parsed.
	 */
	protected transient Exception       _lastException;
	
	
	/**
	 * The read-only request flag.
	 * 
	 * @serial
	 */
	protected boolean                   _readOnly;
	
	
	/**
	 * The asynchronous parsing thread, only one allowed at any given time.
	 */
	protected transient Thread          _asyncThread;
	
	
	/**
	 * The holder associated with this source when {@link #run} is first
	 * called or after a call to {@link #forget}.
	 */
	protected transient Holder          _holder;

	
	/**
	 * Constructs a new source with no parameters.
	 */
	public SourceImpl()
	{
		this( null, null, null, null );
	}
	/**
	 * Constructs a new source with the supplied parameters. All parameters are
	 * optional. For more information, see the related get methods.
	 * 
	 * @param uri The source URI, or null
	 * @param publicId The source public identifier, or null
	 * @param encoding Default encoding, or null
	 * @param docClass Requested document class, or null
	 */
	public SourceImpl( String uri, String publicId, String encoding, Class docClass )
	{
		_uri = uri;
		_publicId = publicId;
		_encoding = encoding;
		_docClass = docClass;
	}
	/**
	 * Starts parsing the source asynchronously. This document is useful if
	 * some other initialization operations may be performed as the document
	 * is being parsed (e.g. when fetched from a slow Internet connection).
	 * A new thread is created to parse the source. They document may be
	 * retrieved by calling {@link #getDocument}, either returning the parsed
	 * document or blocking until one is available.
	 */
	public final synchronized void asynch()
	{
		if ( _asyncThread == null )
		{
			_asyncThread = new Thread( this );
			_asyncThread.start();
		}
	}
	/**
	 * Forget about the and be prepared to retrieve from a different source.
	 * The URI or public identifier may be changed, but to retrieve a document
	 * from the new source, this method must be called first.
	 */
	public final synchronized void forget()
	{
		// Since this is synchronized, the run() method is sure to complete
		// before coming here. Forget everything known about that source.
		_document = null;
		_holder = null;
		_lastException = null;
	}
	/**
	 * Returns the document class for DTD documents.
	 * 
	 * @return The document class
	 */
	public static final Class getClassDTD()
	{
		return DOCUMENT_DTD;
	}
	/**
	 * Returns the document class for HTML documents.
	 * 
	 * @return The document class
	 */
	public static final Class getClassHTML()
	{
		return DOCUMENT_HTML;
	}
	/**
	 * Returns the document class for XML documents.
	 * 
	 * @return The document class
	 */
	public static final Class getClassXML()
	{
		return DOCUMENT_XML;
	}
	/**
	 * Returns the document class. This is the class requested for the parsed
	 * document.
	 * 
	 * @return The document class
	 */
	public Class getDocClass()
	{
		return _docClass;
	}
	/**
	 * Returns the document read from this source. If the document could not
	 * be found or parsed, null is returned and the last error could be found
	 * with {@link #getLastException}. The returned document type is based on
	 * the requested document type ({@link Document} by default) or the
	 * the document type specified by the source.
	 * <P>
	 * If in read-only mode, a single instance of a read-only document will be
	 * returned. If not in read-only mode, a new modifiable instance of the
	 * document will be returned each time this method is called.
	 * <P>s
	 * This method may be used after a call to {@link #asynch} to retrieve an
	 * asynchronously parsed document. If {@link #asynch} has not completed,
	 * this method will block until the entire document is available.
	 * 
	 * @return The parsed document, or null
	 */
	public final synchronized Document getDocument()
	{
		Document    doc;
		
		// If the asynch thread is running, block until it returns. If the
		// document could be parsed, there woule be a new document in _document.
		// Otherwise, call run() to parse the document now; if asynch has
		// completed, there would be a document in _document, so don't call
		// run() this time.
/* Used to happen before, but not run() is fully synchronized
		if ( _asyncThread != null )
		{
			try
			{
				_asyncThread.join();
			}
			catch ( InterruptedException except )
			{
			}
		}
		else
*/
		if ( _document == null )
			run();
		// Set _document to null, so next time getDocument() is called,
		// it will be forced to retrieve a new document from the holder.
		// This is required when not in read-only mode to retrieve a new
		// modifiable instance.
		doc = _document;
		_document = null;
		notifyComplete();
		return doc;
	}
	/**
	 * Returns the source encoding. This encoding was used to parse the source.
	 * 
	 * @return The document encoding, or null
	 */
	public String getEncoding()
	{
		return _encoding;
	}
	/**
	 * Returns the last exception generated by an attempt to parse the source.
	 * This exception may be due to inability to access the source (@link
	 * Exception}) or a result of parsing the source ({@link 
	 * org.openxml.parser.ParseException }).
	 * 
	 * @return The last parsing exception
	 */
	public final Exception getLastException()
	{
		return _lastException;
	}
	/**
	 * Return the public identifier for this document. Some documents are located
	 * by a public identifier that is not a valid URI.
	 * 
	 * @return The document public identifer, or null
	 */
	public String getPublicId()
	{
		return _publicId;
	}
	/**
	 * Returns the URI for this document.
	 * 
	 * @return The document URI, or null
	 */
	public String getURI()
	{
		return _uri;
	}
	/**
	 * Called by {@link #run} to nodify that the document has been successfuly
	 * retrieved. The default implementation does nothing, but a bean instance of
	 * this class will fire a completion event.
	 */
	protected void notifyComplete()
	{
	}
	/**
	 * To be called by application only. Since this method is synchronized,
	 * many other methods will be blocked.
	 */
	public final synchronized void run()
	{
		// Synchronized to make sure the source is not change while looking
		// for a suitable holder object. The holder is only required first
		// time or after a forget().
		if ( _holder == null )
			_holder = HolderFinderImpl.getHolderFinder().findHolder( this );
			
		// Depending on the flag, read a read-only or new instance of
		// the document. Set the encoding, document class and last
		// exception accordingly.
		if ( _holder != null && _document == null )
		{
			if ( _readOnly )
				_document = _holder.getReadOnly();
			else
				_document = _holder.newInstance();
			if ( _document != null )
			{
				setDocClass( _document.getClass() );
				if ( _holder.getEncoding() != null )
					setEncoding( _holder.getEncoding() );
				_lastException = _holder.getLastException();
				if ( _lastException != null )
				{
					Log.error( "Source.getDocument/asynch: Error parsing document [" + toString() + "]" );
					Log.error( _lastException );
				}
			}
		}
		else
			Log.error( "Source.getDocument/asynch: Could not locate document [" + toString() + "]" );
			
		// No reason for the notify, but a derived class might find
		// it useful.
		notify();
		_asyncThread = null;
		notifyComplete();
	}
	/**
	 * Sets the requested document class. The source will be parsed and returned
	 * in a document of this class, unless otherwise specified, e.g. by the
	 * content type returned from a Web server.
	 * 
	 * @param className The requested document class
	 */
	public synchronized void setDocClass( Class docClass )
	{
		_docClass = docClass;
	}
	/**
	 * Sets the default source encoding. This encoding is used to parse the
	 * source, unless a different encoding is specified, e.g. by the content
	 * type returned from a Web server, or in the document declaration.
	 * If no encoding is specified, the default "UTF8" is assumed.
	 * 
	 * @param encoding The default document encoding, or null
	 */
	public synchronized void setEncoding( String encoding )
	{
		_encoding = encoding;
	}
	/**
	 * Sets the public identifier for this document. Some documents are located
	 * by a public identifier that is not a valid URI but might be translated
	 * into one. If the public identifier is not recognized, the document URI
	 * must be used. Changing the public identifier will not return a new
	 * document until after {@link #forget} is called.
	 * 
	 * @param publicId The document public identifer, or null
	 */
	public synchronized void setPublicId( String publicId )
	{
		_publicId = publicId;
	}
	/**
	 * If true, requests a read-only instance of the document that may be
	 * read, clones, but not modified. If false (default), a new instance of
	 * the document will be returned each time and this instance may be
	 * modified without affecting other instances.
	 * 
	 * @param readOnly True if read-only instance is required
	 */
	public synchronized void setReadOnly( boolean readOnly )
	{
		_readOnly = readOnly;
	}
	/**
	 * Sets the URI for this document. Most document are identified by a URI
	 * that might be a network URL, a file URL, or some other regcognized
	 * identifier. Changing the URI will not return a new document until
	 * after {@link #forget} is called.
	 * 
	 * @param uri The document URI, or null
	 */
	public synchronized void setURI( String uri )
	{
		_uri = uri;
	}
	public String toString()
	{
		StringBuffer    text;
		
		text = new StringBuffer( "Source:" );
		if ( _uri != null )
			text.append( " URI [" ).append( _uri ).append( "]" );
		if ( _publicId != null )
			text.append( " PublicID [" ).append( _publicId ).append( "]" );
		if ( _encoding != null )
			text.append( " Encoding [" ).append( _encoding ).append( "]" );
		if ( _docClass != null )
			text.append( " DocClassName [" ).append( _docClass.getName() ).append( "]" );
		if ( _holder != null )
			text.append( " Loaded" );
		return text.toString();
	}
}