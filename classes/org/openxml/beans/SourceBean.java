package org.openxml.beans;

/**
 * org/openxml/beans/SourceBean.java
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
import org.w3c.dom.DocumentType;
import org.w3c.dom.html.HTMLDocument;
import org.openxml.source.*;
import org.openxml.source.holders.*;


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
 * a class that extends {@link org.openxml.XMLDocument}, see the later for
 * more details.
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
 * The following URI formats are natively supported by this mechanism:
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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class SourceBean
	extends SourceImpl
{


	/**
	 * Helper object to implement property change events and listeners.
	 */
	protected transient PropertyChangeSupport   _propChange;


	/**
	 * Constructs a new source with no parameters.
	 */
	public SourceBean()
	{
		this( null, null, null, null );
	}
	/**
	 * Constructs a new source with the supplied parameters. All parameters are
	 * optional. For more information, see the related get methods.
	 *
	 * @param uri The source URI, or null
	 */
	public SourceBean( String uri )
	{
		this( uri, null, null, null );
	}
	/**
	 * Constructs a new source with the supplied parameters. All parameters are
	 * optional. For more information, see the related get methods.
	 *
	 * @param uri The source URI, or null
	 * @param docClass Requested document class, or null
	 */
	public SourceBean( String uri, Class docClass )
	{
		this( uri, null, null, docClass );
	}
	/**
	 * Constructs a new source with the supplied parameters. All parameters are
	 * optional. For more information, see the related get methods.
	 *
	 * @param uri The source URI, or null
	 * @param encoding Default encoding, or null
	 */
	public SourceBean( String uri, String encoding )
	{
		this( uri, null, encoding, null );
	}
	/**
	 * Constructs a new source with the supplied parameters. All parameters are
	 * optional. For more information, see the related get methods.
	 *
	 * @param uri The source URI, or null
	 * @param publicId The source public identifier, or null
	 * @param encoding Default encoding, or null
	 */
	public SourceBean( String uri, String publicId, String encoding )
	{
		this( uri, publicId, encoding, null );
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
	public SourceBean( String uri, String publicId, String encoding, Class docClass )
	{
		_uri = uri;
		_publicId = publicId;
		_encoding = encoding;
		_docClass = docClass;
	}
	/**
	 * Assigns a property change listener. This listener will recieve a property
	 * change event whenever a property of this bean has changed, either as a
	 * result of changing the property, or of retrieving a document.
	 * <P>
	 * Property change events are fired for changed on the following properties:
	 * <UL>
	 * <LI>URI -- reflects user changed to the URI property
	 * <LI>PublicID -- reflects user changed to the public identifier property
	 * <LI>Encoding -- reflects user changed to the encoding property, or change
	 *  of encoding in response to source document being retrieved
	 * <LI>DocClass -- reflects user changed to the document class property
	 *  either by name ({@link #setDocClassName}) or class ({@link #setDocClass}),
	 *  or in response to source document being parsed
	 * <LI>ReadOnly -- reflects user changed to the public read-only property
	 * </UL>
	 *
	 * @param listener The listener to assign
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener )
	{
		if ( _propChange == null )
			_propChange= new PropertyChangeSupport( this );
		_propChange.addPropertyChangeListener( listener );
	}
	/**
	 * Returns the document class. This is the class requested for the parsed
	 * document, or if parsed the actual class of the parsed document.
	 *
	 * @return The document class
	 */
	public String getDocClassName()
	{
		if ( _docClass != null )
			return _docClass.getName();
		else
			return null;
	}
	protected void notifyComplete()
	{
	}
	/**
	 * Reads and returns an DTD document from the file. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param file The source file
	 * @param encoding The source encoding, or null
	 * @return An DTD document, or null
	 */
	public static Document readDTD( File file, String encoding )
	{
		Source  source;

		source = new SourceImpl( "file:///" + file, null, encoding, DocumentType.class );
		return source.getDocument();
	}
	/**
	 * Reads and returns an DTD document from the URI. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param uri The source URI
	 * @param encoding The source encoding, or null
	 * @return An DTD document, or null
	 */
	public static Document readDTD( String uri, String encoding )
	{
		Source  source;

		source = new SourceImpl( uri, null, encoding, DocumentType.class );
		return source.getDocument();
	}
	/**
	 * Reads and returns an HTML document from the file. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param file The source file
	 * @param encoding The source encoding, or null
	 * @return An HTML document, or null
	 */
	public static Document readHTML( File file, String encoding )
	{
		Source  source;

		source = new SourceImpl( "file:///" + file, null, encoding, HTMLDocument.class );
		return source.getDocument();
	}
	/**
	 * Reads and returns an HTML document from the URI. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param uri The source URI
	 * @param encoding The source encoding, or null
	 * @return An HTML document, or null
	 */
	public static Document readHTML( String uri, String encoding )
	{
		Source  source;

		source = new SourceImpl( uri, null, encoding, HTMLDocument.class );
		return source.getDocument();
	}
	/**
	 * Reads and returns an XML document from the file. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param file The source file
	 * @param encoding The source encoding, or null
	 * @return An XML document, or null
	 */
	public static Document readXML( File file, String encoding )
	{
		Source  source;

		source = new SourceImpl( "file:///" + file, null, encoding, DOCUMENT_XML );
		return source.getDocument();
	}
	/**
	 * Reads and returns an XML document from the URI. If the source could not
	 * be found or accessed, null is returned. The returned document is of the
	 * specified type, unless type has been modified based on the source.
	 *
	 * @param uri The source URI
	 * @param encoding The source encoding, or null
	 * @return An XML document, or null
	 */
	public static Document readXML( String uri, String encoding )
	{
		Source  source;

		source = new SourceImpl( uri, null, encoding, DOCUMENT_XML );
		return source.getDocument();
	}
	/**
	 * Removes a property change listener. This listener will not longer recieve
	 * property change events for this bean.
	 *
	 * @param listener The listener to remove
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener )
	{
		if ( _propChange != null )
			_propChange .removePropertyChangeListener( listener );
	}
	/**
	 * Sets the requested document class. The source will be parsed and returned
	 * in a document of this class, unless otherwise specified, e.g. by the
	 * content type returned from a Web server. The default document type is
	 * {@link Document}; {@link HTMLDocument}, {@link org.openxml.DTDDocument}
	 * or any class derived from {@link org.openxml.XMLDocument} may be used.
	 *
	 * @param className The requested document class
	 */
	public synchronized void setDocClass( Class docClass )
	{
		Class   old;

		if ( _docClass != docClass )
		{
			old = _docClass;
			_docClass = docClass;
			if ( _propChange != null )
				_propChange.firePropertyChange( "DocClass", old, _docClass );
		}
	}
	/**
	 * Sets the requested document class. The source will be parsed and returned
	 * in a document of this class, unless otherwise specified, e.g. by the
	 * content type returned from a Web server. The default document type is
	 * {@link Document}; {@link HTMLDocument}, {@link org.openxml.DTDDocument}
	 * or any class derived from {@link org.openxml.XMLDocument} may be used.
	 *
	 * @param className The requested document class
	 */
	public synchronized void setDocClassName( String className )
	{
		Class  old;

		old = _docClass;
		try
		{
			_docClass = Class.forName( className );
		}
		catch ( ClassNotFoundException except )
		{
		}
		if ( _docClass != old && _propChange != null )
			_propChange.firePropertyChange( "DocClass", old, _docClass );
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
		String  old;

		if ( _encoding != encoding )
		{
			old = _encoding;
			_encoding = encoding;
			if ( _propChange != null )
				_propChange.firePropertyChange( "Encoding", old, _encoding );
		}
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
		String  old;

		if ( _publicId != publicId )
		{
			old = _publicId;
			_publicId = publicId;
			if ( _propChange != null )
				_propChange.firePropertyChange( "PublicId", old, _publicId );
		}
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
		Boolean  old;

		if ( _readOnly = readOnly )
		{
			old = new Boolean( _readOnly );
			_readOnly = readOnly;
			if ( _propChange != null )
				_propChange.firePropertyChange( "ReadOnly", old, new Boolean( _readOnly ) );
		}
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
		String  old;

		if ( _uri != uri )
		{
			old = _uri;
			_uri = uri;
			if ( _propChange != null )
				_propChange.firePropertyChange( "URI", old, _uri );
		}
	}
}