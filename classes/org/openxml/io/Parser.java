package org.openxml.io;

/**
 * org/openxml/io/Parser.java
 * 
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING ORi DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 * 
 * The Initial Developer of this code under the License is Assaf Arkin.
 * Portions created by Assaf Arkin are Copyright (C) 1998, 1999.
 * All Rights Reserved.
 */


import org.w3c.dom.*;
import org.openxml.parser.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * 
 * @deprecated
 *  This package has become obsolete in favor of the <a
 *  href="../x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
 *  This package is temporarily provided for backward compatibility but
 *  will not be included in release 1.1.
 */
public interface Parser
	extends SourceLocation
{


	/**
	 * Mode flag true if processing instructions should be stored in the document
	 * tree. If false, processing instructions are parsed but not stored in the
	 * document tree. If processing instructions are not stored, they cannot be
	 * acted upon.
	 */
	public static final short    MODE_STORE_PI = 0x01;

	
	/**
	 * Mode flag true if comments should be stored in the document tree. If false,
	 * comments are parsed but not stored in the document tree. Some applications
	 * benefit the space saving, others favor parsing and printing documents for
	 * readability.
	 */
	public static final short    MODE_STORE_COMMENT = 0x02;
	
	
	/**
	 * Mode flag true if CDATA sections should be stored in the document tree as
	 * such. If false, CDATA sections are converted to plain text and stored as
	 * such. When false, the input and output documents will not look identical
	 * but be identical in contents.
	 */
	public static final short    MODE_STORE_CDATA = 0x04;
	
	
	/**
	 * Mode flag true if content entity references should be parsed into the
	 * document tree. If false, entity references are stored as entity reference
	 * nodes.
	 */
	public static final short    MODE_PARSE_ENTITY = 0x08;
	
	
	/**
	 */
	public static final short    MODE_VALIDATE = 0x10;

	
	/**
	 * Mode flag true if identified elements (<TT>ID</TT> attribute) are to be
	 * bookmarked. When this mode is selected, <TT>ID</TT> attributes are checked
	 * for uniqueness.
	 */
	public static final short    MODE_BOOKMARKS = 0x20;
	
	
	/**
	 * Mode flag true if DTD declarations should be stored in the DTD document
	 * tree as nodes. If false, DTD declarations are added to the DTD, but not
	 * under the document tree; the input and output documents will not look
	 * identical but be identical in contents.If true, DTD declarations, comments
	 * and PI may be stored under the document tree, preserving the original
	 * document order.
	 */
	public static final short    MODE_STORE_DTD = 0x40;
	
	
	public static final short    MODE_PRESERVE_WS = 0x80;
	
	
	/**
	 */
	public static final short    MODE_XML_PARSER =
			MODE_STORE_PI | MODE_PARSE_ENTITY | MODE_STORE_PI | MODE_STORE_COMMENT;

	
	/**
	 */
	public static final short    MODE_FIDEL_XML_PARSER =
			MODE_XML_PARSER | MODE_STORE_DTD | MODE_STORE_CDATA | MODE_STORE_COMMENT;

	
	/**
	 */
	public static final short    MODE_VALIDATE_XML_PARSER =
			MODE_XML_PARSER | MODE_VALIDATE;

	
	/**
	 */
	public static final short    MODE_HTML_PARSER =
			MODE_STORE_PI | MODE_PARSE_ENTITY  | MODE_STORE_COMMENT;

	
	/**
	 */
	public static final short    MODE_FIDEL_HTML_PARSER =
			MODE_HTML_PARSER | MODE_STORE_COMMENT;

	
	/**
	 */
	public static final short    MODE_VALIDATE_HTML_PARSER =
			MODE_HTML_PARSER | MODE_VALIDATE;

	
	/**
	 */
	public static final short    MODE_DTD_PARSER =
			MODE_PARSE_ENTITY;

	
	/**
	 */
	public static final short    MODE_FIDEL_DTD_PARSER =
			MODE_DTD_PARSER | MODE_STORE_DTD | MODE_STORE_PI | MODE_STORE_COMMENT;

	
	/**
	 * Severity level for fatal errors. Once a fatal error has been encountered
	 * (e.g. I/O exception), parsing is immediately terminated. Fatal errors are
	 * generally I/O exceptions and runtime exceptions.
	 */
	public static final short    ERROR_FATAL = 0;

	
	/**
	 * Severity level for well-formed errors. Well formed errors may be corrected
	 * by the parser, but indicate a document that is faulty and cannot be properly
	 * processed. Well-formed errors are issued both by validating and non-validating
	 * parsers.
	 */
	public static final short    ERROR_WELL_FORMED = 1;

	
	/**
	 * Severity level for validity constraint errors. Validity constraint errors
	 * may be corrected by the parser, but indicate a document that does not
	 * conform to the DTD. Validity constraint errors are only issued by a
	 * validating parser and are very common in HTML documents.
	 */
	public static final short    ERROR_VALIDITY = 2;

	
	/**
	 * Passed to constructor to indicate the parser should only stop when a
	 * fatal parsing error has been issued, throwing an {@link FatalParseException}.
	 */
	public static final short    STOP_SEVERITY_FATAL = ERROR_FATAL;

	
	/**
	 * Passed to constructor to indicate the parser should stop when the first
	 * well formed parsing error has been issued, throwing a {@link
	 * WellFormedException}. This level also implies {@link #STOP_SEVERITY_FATAL}.
	 */
	public static final short    STOP_SEVERITY_WELL_FORMED = ERROR_WELL_FORMED;

	
	/**
	 * Passed to constructor to indicate the parser should stop when the first
	 * validity parsing error has been issued, throwing a {@link ValidityException}.
	 * This level also implies {@link #STOP_SEVERITY_WELL_FORMED}.
	 */
	public static final short    STOP_SEVERITY_VALIDITY = ERROR_VALIDITY;


	/**
	 * Returns the last exception generated by the parser. That exception was
	 * either thrown (depending on its severity level), or stored for later
	 * retrieval. It is possible that multiple exceptions have been issued
	 * (e.g. in response to well formed errors), but were stored and not thrown.
	 * Previous exceptions can be obtained from the last exception by calling
	 * {@link ParseException#getPrevious} recursively.
	 * 
	 * @return Returns the last parse exception issued or null
	 */
	public ParseException getLastException();
	/**
	 * Parses the open document and returns its contents as a new document object
	 * (document type derived from parser type). After the document has been parsed,
	 * the parser is closed and the node is returned. The parsing behavior depends
	 * in much on mode selected in the constructor.
	 * <P>
	 * Depending on the parsing modes, some parsing errors might cause an exception
	 * to occur, others will be stored and later accessible with the {@link
	 * #getLastException} method. I/O exceptions and runtime exceptions will
	 * terminate parsing immediately by throwing a {@link FatalParseException}.
	 * 
	 * @return The parsed XML/HTML/DTD document
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	public abstract Document parseDocument()
		throws ParseException;
	/**
	 * Parses the open document and places its contents underneath the specified
	 * node. <TT>node</TT> must be a node that supports children (e.g. element or
	 * document fragment). After the document has been parsed, the parser is closed
	 * and the node is returned.
	 * <P>
	 * Depending on the parsing modes, some parsing errors might cause an exception
	 * to occur, others will be stored and later accessible with the {@link
	 * #getLastException} method. I/O exceptions and runtime exceptions will
	 * terminate parsing immediately by throwing a {@link FatalParseException}.
	 * 
	 * @param node A child-supporting node underneath which to place the parsed
	 *  content
	 * @return The node argument
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	public abstract Node parseNode( Node node )
		throws ParseException;
}