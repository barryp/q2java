package org.openxml.source;

/**
 * org/openxml/source/Source.java
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


import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.html.HTMLDocument;


/**
 * Defines the source for an XML document. This interface describes the
 * location of an XML document, the document type and the default encoding.
 * It is used to retrieve an in-memory document tree and check for any
 * errors in the retrieval process. It is not specified how the retrieval
 * is implemented, parsing is assumed.
 * <P>
 * A source is identified by four parameters. The URI specifies a source
 * that can be a network URL, file URL, resource contained in JAR file, etc.
 * The public identifier is an alternative named that may be mapped to a URI.
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
 * The source is retrieved by calling {@link #getDocument}. This will return
 * a document of the requested class, unless the source forced a different class
 * to be parsed. If the document cannot be retrieved completely, null is returned.
 * The error exception may be obtained by calling {@link #getLastException}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public abstract class Source
{
	
	
	/**
	 * Request a XML document from the source. The returned document will be
	 * of type {@link Document}, unless otherwise specified by the source.
	 */
	public static final Class   DOCUMENT_XML = Document.class;

	
	/**
	 * Request a HTML document from the source. The returned document will be
	 * of type {@link HTMLDocument}, unless otherwise specified by the source.
	 */
	public static final Class   DOCUMENT_HTML = HTMLDocument.class;
	
	
	/**
	 * Request a DTD document from the source. The returned document will be
	 * of type {@link DocumentType}, unless otherwise specified by the source.
	 */
	public static final Class   DOCUMENT_DTD = DocumentType.class;

	
	/**
	 * Forget about the and be prepared to retrieve from a different source.
	 * The URI or public identifier may be changed, but to retrieve a document
	 * from the new source, this method must be called first.
	 */
	public abstract void forget();
	/**
	 * Returns the document class. This is the class requested for the parsed
	 * document.
	 * 
	 * @return The document class
	 */
	public abstract Class getDocClass();
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
	 */
	public abstract Document getDocument();
	/**
	 * Returns the source encoding. This encoding was used to parse the source.
	 * 
	 * @return The document encoding, or null
	 */
	public abstract String getEncoding();
	/**
	 * Returns the last exception generated by an attempt to parse the source.
	 * This exception may be due to inability to access the source (@link
	 * Exception}) or a result of parsing the source ({@link 
	 * org.openxml.parser.ParseException }).
	 * 
	 * @return The last parsing exception
	 */
	public abstract Exception getLastException();
	/**
	 * Return the public identifier for this document. Some documents are located
	 * by a public identifier that is not a valid URI.
	 * 
	 * @return The document public identifer, or null
	 */
	public abstract String getPublicId();
	/**
	 * Returns the URI for this document.
	 * 
	 * @return The document URI, or null
	 */
	public abstract String getURI();
	/**
	 * Sets the requested document class. The source will be parsed and returned
	 * in a document of this class, unless otherwise specified, e.g. by the
	 * content type returned from a Web server. The default document type is
	 * {@link Document}; {@link HTMLDocument}, {@link DocumentType} or any
	 * class derived from {@link Document} may be used.
	 * 
	 * @param className The requested document class
	 */
	public abstract void setDocClass( Class docClass );
	/**
	 * Sets the default source encoding. This encoding is used to parse the
	 * source, unless a different encoding is specified, e.g. by the content
	 * type returned from a Web server, or in the document declaration.
	 * If no encoding is specified, the default "UTF8" is assumed.
	 * 
	 * @param encoding The default document encoding, or null
	 */
	public abstract void setEncoding( String encoding );
	/**
	 * Sets the public identifier for this document. Some documents are located
	 * by a public identifier that is not a valid URI but might be translated
	 * into one. If the public identifier is not recognized, the document URI
	 * must be used. Changing the public identifier will not return a new
	 * document until after {@link #forget} is called.
	 * 
	 * @param publicId The document public identifer, or null
	 */
	public abstract void setPublicId( String publicId );
	/**
	 * If true, requests a read-only instance of the document that may be
	 * read, clones, but not modified. If false (default), a new instance of
	 * the document will be returned each time and this instance may be
	 * modified without affecting other instances.
	 * 
	 * @param readOnly True if read-only instance is required
	 */
	public abstract void setReadOnly( boolean readOnly );
	/**
	 * Sets the URI for this document. Most document are identified by a URI
	 * that might be a network URL, a file URL, or some other regcognized
	 * identifier. Changing the URI will not return a new document until
	 * after {@link #forget} is called.
	 * 
	 * @param uri The document URI, or null
	 */
	public abstract void setURI( String uri );
}