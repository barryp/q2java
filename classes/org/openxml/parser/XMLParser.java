package org.openxml.parser;

/**
 * org/openxml/parser/XMLParser.java
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
import java.net.URL;
import java.util.*;
import org.w3c.dom.*;
import org.openxml.DOMFactory;
import org.openxml.XMLDocument;
import org.openxml.DTDDocument;
import org.openxml.source.*;
import org.openxml.dom.*;
import org.openxml.io.*;
import org.openxml.util.*;


/**
 * Implements a parser for XML documents, document fragments, nodes and external
 * entities. The XML document is created with {@link DOMFactory}, but a specific
 * class type may be requested with {@link #parseDocument(DTDDocument,Class)}.
 * <P>
 * Stuff to do:
 * <UL>
 * <LI>Support MODE_PRESERVE_WS and xml:space to preserve whitespaces at the
 *  document and element level respectively
 * <LI>Striping of redundent whitespaces between markup
 * <LI>Use document URI as base for relative entity URI
 * </UL>
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ContentParser
 * @see ParseException
 */
public class XMLParser
	extends ContentParser
{


	/**
	 * Holds the contents of text node collected between elements and other
	 * non-textual nodes. Consists of all plain text and resolved entities.
	 *
	 * @see #parseNextNode
	 */
	private FastString          _nodeText;


	/**
	 * Holds an orphan closing tag, one for which an opening tag could not be
	 * matched. Only a single orphan closing tag is supported.
	 */
	private String              _orphanClosingTag;


	/**
	 * Parser constructor. Constructor will operate in the default mode of
	 * {@link #MODE_XML_PARSER} with {@link #STOP_SEVERITY_FATAL}.
	 *
	 * @param reader Any {@link java.io.Reader} from which entity text can be read
	 * @param sourceURI URI of entity source
	 */
	public XMLParser( Reader reader, String sourceURI )
	{
		this( reader, sourceURI, MODE_XML_PARSER, STOP_SEVERITY_FATAL );
	}
	/**
	 * Parser constructor. Requires source text in the form of a {@link
	 * java.io.Reader} object and as an identifier. The parsing mode consists of a
	 * combination of <TT>MODE_..</TT> flags. The constructor specifies the error
	 * severity level at which to stop parsing, either {@link #STOP_SEVERITY_FATAL},
	 * {@link #STOP_SEVERITY_VALIDITY} or {@link #STOP_SEVERITY_WELL_FORMED}.
	 *
	 * @param reader Any {@link java.io.Reader} from which entity text can be read
	 * @param sourceURI URI of entity source
	 * @param mode The parsing mode in effect
	 * @param stopAtSeverity Severity level at which to stop parsing
	 */
	public XMLParser( Reader reader, String sourceURI, short mode, short stopAtSeverity )
	{
		super( reader, sourceURI, mode, stopAtSeverity );
	}
	/**
	 * Constructor for entity parser. Requires a parent parser to be specified and
	 * will use that parser's document, dtd, error sink and mode (assuming that
	 * {@link #MODE_PARSE_ENTITY} is in effect). The severity level is set to
	 * {@link #STOP_SEVERITY_FATAL}.
	 *
	 * @param owner The parser which invoked this parser
	 * @param reader Any {@link java.io.Reader} from which entity text can be read
	 * @param sourceURI URI of entity source
	 */
	public XMLParser( BaseParser owner, Reader reader, String sourceURI )
	{
		super( reader, sourceURI, owner.getMode(), STOP_SEVERITY_FATAL );
		_document = owner._document;
		if ( owner instanceof ContentParser )
			_docType = ( (ContentParser) owner )._docType;
		setErrorSink( owner );
	}
	/**
	 * Called to process the closing tag. This method is isolated from {@link
	 * #parseNextNode} and called on two occassions: when the closing tag is met,
	 * and when an orphan closing tag has been identified. In the first instance,
	 * it is called with <TT>keepQuite</TT> false, issuing an error if an orphan
	 * closing tag is met. In the second instance, the orphan closing tag is
	 * processed and so <TT>keepQuite</TT> is true.
	 *
	 * @param name The tag name
	 * @param keepQuite True if orphan closing tag should not issue an error
	 * @return True if closing tag matches current element in {@link #_currentNode}
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final boolean closingTag( String name, boolean keepQuite )
		throws ParseException
	{
		Element elem;
		Node    node;

		// Current node may not be an element, which defies the whole point of
		// checking the end tag. This only happens at the top level document or
		// document fragment, so stop processing immediately.
		if ( ! ( _currentNode instanceof Element ) )
			return false;

		// Bingo! Closing tag closes the currently open tag. Return false,
		// which will land us right in the middle of the parseNextNode()
		// loop in the open tag token scenario of the including element.
		if ( ( (Element) _currentNode ).getTagName().equals( name ) )
			return false;

		// Desparately, keep the orphan closing tag, it might belong
		// someplace (e.g. '<P><FONT><B>text</P></B></FONT>').
		_orphanClosingTag = name;
		if ( ! keepQuite )
			error( ERROR_WELL_FORMED, "Closing tag '" + name + "' does not match with opening tag'" +
										( (Element) _currentNode ).getTagName() + "'." );
		return true;
	}
	/**
	 * Strips whitespaces immediately after the opening tag and immediately before
	 * the closing tag. Also loses whitespaces between elements. This operation is
	 * done once an element has been fully parsed.
	 *
	 * @param elem The element to work on
	 */
	private void loseWhitespace( Element elem )
	{
		Node    child;
		String    text;
		int        i;

//        elem.normalize();

		// Get the first node, if a text node, strip leading whitespaces.
		// Since spaces were not preserved, all multiple whitespaces have been
		// turned into a single space character that is removed. If the text
		// node is emptied, it is removed for the sake of normalization.
		child = elem.getFirstChild();
		if ( child != null && child instanceof Text )
		{
			text = ( (Text) child ).getData();
			if ( text.length() == 1 && text.charAt( 0 ) == SPACE )
				elem.removeChild( child );
			else
			if ( text.length() > 0 && text.charAt( 0 ) == SPACE )
				( (Text) child ).deleteData( 0, 1 );
		}

		// Get the last node, if a text node (first and last might be the same node),
		// strip trailing whitespaces. If the first-last node is all whitespaces,
		// it does not exist by now. If the text node is emptied, it is removed for
		// the sake of normalization.
		child = elem.getLastChild();
		if ( child != null && child instanceof Text )
		{
			text = ( (Text) child ).getData();
			if ( text.length() == 1 && text.charAt( 0 ) == SPACE )
				elem.removeChild( child );
			else
			if ( text.length() > 0 && text.charAt( text.length() - 1 ) == SPACE )
				( (Text) child ).deleteData( text.length() - 1, 1 );
		}
	}
	public Document parseDocument()
		throws ParseException
	{
		return parseDocument( null, null );
	}
	/**
	 * Parses and returns a new XML document. The input stream is assumed to
	 * contain a valid document. The parsing behavior depends in much on mode
	 * selected in the constructor. A default DTD is specified and if the document
	 * specifies no internal or external DTD, this DTD is used for entity resolving
	 * and for validation.
	 * <P>
	 * Depending on the parsing modes, some parsing errors might cause an exception
	 * to occur, others will be stored and later accessible with the {@link
	 * #getLastException} method. I/O exceptions and runtime exceptions will
	 * terminate parsing immediately by throwing a {@link FatalParseException}.
	 *
	 * @param dtd The default DTD to use, if one not specified in the document,
	 *  or null
	 * @return The parsed XML document
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @param dtd The default DTD to use, if one not specified in the document
	 * @see DTDDocument
	 */
	public Document parseDocument( DTDDocument dtd )
		throws ParseException
	{
		return parseDocument( dtd );
	}
	/**
	 * Parses and returns a new XML document. The input stream is assumed to
	 * contain a valid document. The parsing behavior depends in much on mode
	 * selected in the constructor. A default DTD is specified and if the document
	 * specifies no internal or external DTD, this DTD is used for entity resolving
	 * and for validation.
	 * <P>
	 * Depending on the parsing modes, some parsing errors might cause an exception
	 * to occur, others will be stored and later accessible with the {@link
	 * #getLastException} method. I/O exceptions and runtime exceptions will
	 * terminate parsing immediately by throwing a {@link FatalParseException}.
	 * <P>
	 * If not null, <TT>docClass</TT> specifies the class for the created XML
	 * document. That class must extend {@link XMLDocument}.
	 *
	 * @param dtd The default DTD to use, if one not specified in the document,
	 *  or null
	 * @param xmlClass The class for the document object, or null
	 * @return The parsed XML document
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @see DTDDocument
	 * @see XMLDocument
	 */
	public Document parseDocument( DTDDocument dtd, Class docClass )
		throws ParseException
	{
		int            token;
		boolean        standalone;

		// Just to be on the safe side.
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );

		_docType = dtd;
		standalone = false;
		try
		{
			if ( docClass != null )
			{
				if ( docClass != Document.class && ! Document.class.isAssignableFrom( docClass ) )
					throw new IllegalArgumentException( "Argument 'docClass' must extend Document." );
				_document = DOMFactory.createDocument( docClass );
			}
			else
				_document = DOMFactory.createDocument( null );
			_currentNode = _document;

			// Parse version number, encoding information and standalone flag from
			// '<?xml' PI at beginning of document.
			token = readTokenContent();
// ! JDK 1.2 !
//            if ( token == TOKEN_PI && _tokenText.length() > 3 &&
//                 _tokenText.substring( 0, 3 ).equals( "xml" ) )
// ! JDK 1.1 !
			if ( token == TOKEN_PI && _tokenText.length() > 3 &&
				 _tokenText.startsWith( "xml" ) )
			{
				standalone = parseDocumentDecl( true );
				token = readTokenContent();
			}

			// Any number of comments, PI and whitespace may appear before the
			// document type declaration, so process only these tokens.
			while ( token == TOKEN_COMMENT || token == TOKEN_PI ||
					( token == TOKEN_TEXT && isTokenAllSpace() ) )
			{
				if ( token != TOKEN_TEXT )
					parseNextNode( token );
				token = readTokenContent();
			}

			// If document type definition found, process it, otherwise, resume
			// with document content processing. '<!DOCTYPE' is followed by top-
			// level element name, optional external DTD id, and optional markup
			// declaration, both processed using a DTDParser.
			if ( token == TOKEN_DTD && _tokenText.equals( "DOCTYPE" ) )
			{
				parseDTDSubset( standalone );
				token = readTokenContent();
			}

			// Any number of comments, PI and whitespace may appear before the
			// root element, so process only these tokens.
			while ( token != TOKEN_OPEN_TAG && token != TOKEN_EOF )
			{
				if ( token == TOKEN_COMMENT || token == TOKEN_PI )
					parseNextNode( token );
				else
				if ( ! ( token == TOKEN_TEXT && isTokenAllSpace() ) )
					error( ERROR_WELL_FORMED, "Only comments and processing instructions allowed outside of root element." );
				token = readTokenContent();
			}

			// Read the root element and all the document content contained in it.
			if ( token == TOKEN_OPEN_TAG )
			{
				 while ( parseNextNode( token ) )
					token = readTokenContent();
			}
			// Any number of comments, PI and whitespace may appear after the
			// root element, so process only these tokens.
			while ( token != TOKEN_OPEN_TAG && token != TOKEN_EOF )
			{
				if ( token == TOKEN_COMMENT || token == TOKEN_PI )
					parseNextNode( token );
				else
				if ( ! ( token == TOKEN_TEXT && isTokenAllSpace() ) )
					error( ERROR_WELL_FORMED, "Only comments and processing instructions allowed outside of root element." );
				token = readTokenContent();
			}
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Finally close the parser and return the
		// node.
		catch ( ParseException except )
		{
			throw except;
		}
		catch ( Exception except )
		{
			error( except );
		}
		finally
		{
			close();
		}
		return _document;
	}
	/**
	 * Parser the internal and external DTD subsets. A new {@link DTDDocument}
	 * is created, the internal subset is parsed into it, followed by the
	 * external subset. Errors produced when parsing the DTD are directed to
	 * this parser. If there is no internal subset, the external subset is
	 * optinally cached in memory. Public identifiers may be converted to URIs,
	 * as per the installed {@link HolderFinder}.
	 * <P>
	 * This method is called after '&lt;!DOCTYPE' has been consumed and returns
	 * after the terminating '&gt;' has been read. The standalone flag is passed
	 * from the '&lt;xml' PI processing.
	 *
	 * @param standalone The standalone flag
	 */
	protected final void parseDTDSubset( boolean standalone )
		throws ParseException, IOException
	{
		String              rootElementName;
		String              publicId = null;
		String              systemId = null;
		Source              source;
		Holder              holder;
		Reader              reader;
		DTDParser           parser;
		DocumentTypeImpl    docType;

		// If a DTD has been specified, forget about it. A new DTD will be created
		// from the internal and external subset.
		_docType = null;
		// Consume white spaces up to the root element name. If not found, this DTD
		// is invalid.
		while ( isSpace( readChar() ) )
			;
		pushBack();
		if ( readTokenName() )
		{
			rootElementName = _tokenText.toString();

			// Consume optional external identifier and hold the system and
			// public identifiers in systemId and publicId. If the next
			// character is a '[', push it back. It will be processed in
			// the next stage (the internal subset).
			while ( isSpace( readChar() ) )
				;
			if ( _curChar == 'S' && canReadName( "YSTEM" ) )
			{
				while ( isSpace( readChar() ) )
					;
				pushBack();
				if ( readTokenQuoted() )
					systemId = _tokenText.toString();
				else
					error( ERROR_WELL_FORMED, "Document type definition missing quoted system identifier." );
			}
			else
			if ( _curChar == 'P' && canReadName( "UBLIC" ) )
			{
				while ( isSpace( readChar() ) )
					;
				pushBack();
				if ( readTokenQuoted() )
				{
					publicId = _tokenText.toString();
					while ( isSpace( readChar() ) )
						;
					pushBack();
					if ( readTokenQuoted() )
						systemId = _tokenText.toString();
					else
						error( ERROR_WELL_FORMED, "Document type definition missing quoted system identifier." );
				}
				else
					error( ERROR_WELL_FORMED, "Document type definition missing quoted public identifier." );
			}
			else
				pushBack();

			if ( ( systemId != null || publicId != null ) && standalone )
			{
				error( ERROR_VALIDITY, "Document declared as standalone, but uses external DTD subset." );
				standalone = false;
			}

			// Process white spaces between the external identifier and the internal
			// subset. If next character read is '[', process the internal subset.
			// The internal subset requires a new DTD into which the internal and
			// external subsets will be declared.
			while ( isSpace( readChar() ) )
				;
			if ( _curChar == '[' )
			{
				_docType = new DTDDocument( _document, rootElementName, standalone,
											systemId, publicId );
				parser = new DTDParser( this, getReader(), getSourceURI() + " (internal subset)" );
				parser.parseInternalSubset( _docType );
				advanceLineNumber( parser.getLineNumber() );
			}
			else
				pushBack();

			// If an external subset has been specified, process it as well. Make
			// sure document was not declared as standalone. If internal subset
			// exists, append the external subset after it; else, can reuse a
			// cached DTD document.
			if ( systemId != null || publicId != null )
			{
				// Create a source to describe the entity and request a DTD
				// document. From that source request a suitable holder: if the
				// public identifier can be used, it probably will be. Given
				// that a holder could be granted, get the entity reader from
				// it and proceed to parse the entity contents.
				source = DOMFactory.newSource();
				source.setURI( systemId );
				source.setPublicId( publicId );
				source.setDocClass( Source.DOCUMENT_DTD );
				holder = DOMFactory.getHolderFinder().findHolder( source, false );
				if ( holder != null )
				{
					// If there is no internal subset, makes sense to get a
					// cached copy of the DTD; otherwise, or if the cached
					// one fails, parse it directly.
					if ( _docType == null )
					{
						_docType = (DTDDocument) holder.newInstance();
						if ( _docType == null )
							error( ERROR_VALIDITY, "Could not access external DTD subset '" + source + "'." );
					}
					else
					{
						try
						{
							reader = holder.getReader();
							if ( reader != null )
							{
								parser = new DTDParser( this, reader, source.toString() );
								parser.parseExternalSubset( _docType );
							}
							else
								error( ERROR_VALIDITY, "Could not access external DTD subset '" + source + "'." );
						}
						catch ( IOException except )
						{
							error( ERROR_VALIDITY, "Could not access external DTD subset '" + source + "'. IOException: " + except.getMessage() );
						}
					}
				}
				else
				{
					error( ERROR_VALIDITY, "Could not locate external DTD subset." );
				}
				if ( _docType == null )
					_docType = new DTDDocument( _document, rootElementName, standalone,
												systemId, publicId );
			}
			if ( _docType != null && _document instanceof DocumentImpl )
				( (DocumentImpl) _document ).assignDoctype( _docType );
		}
		else
			error( ERROR_WELL_FORMED, "Document type definition missing root element type and was ignored." );

		// Consume anything else up to the closing '>'.
		while ( readChar() != EOF && _curChar != '>' )
			;
		if ( _curChar == EOF )
			error( ERROR_WELL_FORMED, "Document type definition terminated at end of document." );
	}
	/**
	 * Parses the external/internal entity and places its contents underneath the
	 * entity node. The document might be an internal or external entity (set by
	 * <TT>internal</TT>). After the document has been parsed, the parser is
	 * closed and the entity is returned as read-only.
	 * <P>
	 * Depending on the parsing modes, some parsing errors might cause an exception
	 * to occur, others will be stored and later accessible with the {@link
	 * #getLastException} method. I/O exceptions and runtime exceptions will
	 * terminate parsing immediately by throwing a {@link FatalParseException}.
	 *
	 * @param entity The entity to parse
	 * @param internal True if an internal entity
	 * @return The entity node
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	public final Entity parseEntity( EntityImpl entity, boolean internal )
		throws ParseException
	{
		int    token;

		// Just to be on the safe side.
		if ( entity == null )
			throw new NullPointerException( "Argument 'entity' is null." );
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );
		_document = entity.getOwnerDocument();

		// Read tokens, parse them into nodes and place these nodes in the node.
		// At the end of the parsing process, some text might remain in _nodeText,
		// so store it as a text node.
		try
		{
			_currentNode = entity;
			token = readTokenContent();
			// If an external entity, parse the version procession instruction
			// that might appear at the beginning. This might result in a version
			// error or changing of the input encoding.
// ! JDK 1.2 !
//            if ( ! internal && token == TOKEN_PI && _tokenText.length() > 3 &&
//                 _tokenText.substring( 0, 3 ).equals( "xml" ) )
// ! JDK 1.1 !
			if ( ! internal && token == TOKEN_PI && _tokenText.length() > 3 &&
				 _tokenText.startsWith( "xml" ) )
			{
				parseDocumentDecl( false );
				token = readTokenContent();
			}
			while ( parseNextNode( token ) )
				token = readTokenContent();
			if ( _nodeText != null && _nodeText.length() > 0 )
			{
				_currentNode.appendChild( _document.createTextNode( _nodeText.toString() ) );
				_nodeText = null;
			}
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Finally close the parser and return the
		// node.
		catch ( ParseException except )
		{
			throw except;
		}
		catch ( Exception except )
		{
			error( except );
		}
		finally
		{
			close();
			entity.makeReadOnly();
		}
		return entity;
	}
	/**
	 * Parses the next node based on the supplied token. This method is called
	 * with a read token, parses a node and appends it to {@link #_currentNode}.
	 * If plain text is read, it is accumulated and later on converted into a
	 * {@link org.w3c.dom.Text}. If the node is an element, the element
	 * is created and it's full contents read (recursively).
	 * <P>
	 * The return value indicates if the current element (in {@link #_currentNode})
	 * has been closed with a closing tag (false), or should parsing continue at
	 * the same level (true). False is also returned if the end of file has been
	 * reached.
	 * <P>
	 * The following rules govern how tokens are translated into nodes:
	 * <UL>
	 * <LI>CDATA sections are stored as {@link org.w3c.dom.CDATASection} if in
	 *  mode {@link #MODE_STORE_CDATA}, converted to plain text otherwise
	 * <LI>Comments are stored as {@link org.w3c.dom.Comment} if in mode {@link
	 *  #MODE_STORE_COMMENT}, ignored otherwise
	 * <LI>Processing instructions are stored as {@link
	 *  org.w3c.dom.ProcessingInstruction} if in mode {@link #MODE_STORE_PI},
	 *  ignored otherwise
	 * <LI>Entity references are parsed if in mode {@link #MODE_PARSE_ENTITY},
	 *  otherwise they are stored as {@link org.w3c.dom.EntityReference} nodes
	 * <LI>Attributes are read according to the rules set forth in {@link
	 *  #parseAttributes}
	 * <LI>A single orphan closing tag is supported (while issuing a well formed
	 *  error); a closing orphan tag is one that is misplaced relative to the open
	 *  tag, e.g. '&ltP>&ltFONT>&ltB>text&lt/P>&lt/B>&lt/FONT>'
	 * <LI>White spaces inside elements are preserved, white spaces between
	 *  elements are lost
	 * <LI>If not in mode {@link #MODE_PRESERVE_WS} or requested in the "xml:space"
	 *  attribute, whitespaces are converted to space (0x20) and trailing/leading
	 *  whitespaces are lost in element content
	 * </UL>
	 * The proper way to use this method is:
	 * <PRE>
	 * _currentNode = ...;
	 * token = readTokenContent();
	 * while ( parseNextNode( token ) )
	 *     token = readTokenContent();
	 * </PRE>
	 *
	 * @param token The last token read with {@link #readTokenContent}
	 * @return True if continue parsing, false if current element has been closed
	 *  or reached end of file
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 * @see #parseAttributes
	 * @see #readTokenContent
	 * @see #_currentNode
	 * @see #_orphanClosingTag
	 */
	protected final boolean parseNextNode( int token )
		throws ParseException, IOException
	{
		Node        node;
		String      tokenText;
		int         i;
		char        ch;
		String      name;
		boolean     openTag;
		Element     elem;

		// The rules are evaluated in three sections:
		// Section 1: Translate some tokens into text tokens, storing them as
		//   character data in the document, and ignore other tokens.
		// Section 2: All text is accumulated in _nodeText.
		// Section 3: If token is not text, the contents of _nodeText is stored
		//   as a text node; the token is then processed and stored as the
		//   respectable node; EOF is also processed here to assure that any
		//   _nodeText remains are stored as such.


		// Section 1:
		// TOKEN_SECTION is not TOKEN_CDATA. It is translated into plain text and
		// an error is issued.
		if ( token == TOKEN_SECTION )
		{
			error( ERROR_WELL_FORMED, "The sequence '<![' encountered for no good reason." );
			token = TOKEN_TEXT;
			_tokenText.insert( 0, "<![" );
		}
		// TOKEN_DTD is also translated into plain text and an error is issued.
		if ( token == TOKEN_DTD )
		{
			error( ERROR_WELL_FORMED, "The sequence '<!' encountered for no good reason." );
			token = TOKEN_TEXT;
			_tokenText.insert( 0, "<!" );
		}

		// If not in MODE_STORE_CDATA mode, CDATA is converted to and stored as
		// text. In MODE_STORE_CDATA mode it will be stored as a CDATASection
		// node (see section 3).
		if ( token == TOKEN_CDATA && ! isMode( MODE_STORE_CDATA ) )
			token = TOKEN_TEXT;
		// If token is a processing instruction or a comment and mode does not
		// require node to be stored in document tree, exit now. This will save
		// some overhead with storing text nodes before non-text nodes.
		if ( ( token == TOKEN_PI && ! isMode( MODE_STORE_PI ) ) ||
			 ( token == TOKEN_COMMENT && ! isMode( MODE_STORE_COMMENT ) ) )
			return true;

		// Section 2:
		// Now processing text from _tokenText. Several text tokens may follow so
		// rather than creating a Text node at this point, the text is accumulated
		// in _nodeText. When any other token is met (including TOKEN_EOF), the
		// contents of _nodeText is turned into a Text node and stores as such.
		if ( token == TOKEN_TEXT )
		{
			if ( _nodeText == null )
				_nodeText = new FastString( _tokenText );
			else
				_nodeText.append( _tokenText );
		}
		// Section 3:
		else
		{
			// If we reached this point, the token is either going to create a node
			// into the document tree or close the current element. All text
			// accumualted in _nodeText so far is added to the current element as a
			// Text node before commencing with per-token behavior.
			if ( _nodeText != null && _nodeText.length() > 0 )
			{
				int k; int l; char[] a;
				l = _nodeText.length();
				a = _nodeText.getCharArray();
				for ( k = 0 ; k < l ; ++k )
					if ( ! isSpace( a[ k ] ) )
						break;
				if ( k < l )
				{
					node = _document.createTextNode( _nodeText.toString() );
					_currentNode.appendChild( node );
				}
				_nodeText.setLength( 0 );
			}

			// If token is EOF, return false indicating we're done with current
			// element. EOF will be returned for as long as necessary until all
			// open elements have been closed in this manner. This check is done
			// here and not at the method's start due to the above section 2.
			if ( token == TOKEN_EOF )
				return false;

			// Token is a processing instruction and should be stored.
			if ( token == TOKEN_PI )
			{
				name = slicePITokenText();
				node = _document.createProcessingInstruction( name, _tokenText.toString() );
				_currentNode.appendChild( node );
			}
			else
			// Token is a comment and should be stored.
			if ( token == TOKEN_COMMENT )
			{
				node = _document.createComment(  _tokenText.toString() );
				_currentNode.appendChild( node );
			}
			else
			// Token is a CDATA section and should be stored.
			if ( token == TOKEN_CDATA )
			{
				node = _document.createCDATASection( _tokenText.toString() );
				_currentNode.appendChild( node );
			}
			else
			// Token is an entity reference and should be parsed or stored.
			if ( token == TOKEN_ENTITY_REF )
			{
				if ( isMode( MODE_PARSE_ENTITY ) )
					parseContentEntity();
				else
				{
					node = _document.createEntityReference( _tokenText.toString() );
					_currentNode.appendChild( node );
				}
			}
			else

			// Token is an opening tag. The tag name has been parsed, attribute
			// values should be read next.
			if ( token == TOKEN_OPEN_TAG )
			{
				// _tokenText is guranteed to contain the element's tag name.
				// openTag value is returned based on the tag type (opening/empty).
				elem = _document.createElement( _tokenText.toString() );
				_currentNode.appendChild( elem );
				openTag = parseAttributes( elem, true );
				// If this is an open tag, read the contents of the element until
				// the closing tag (or end of file) is reached. The closing tag
				// (or end of file) is identified by parseNextNode() returning false.
				if ( openTag )
				{
					_currentNode = elem;
					token = readTokenContent();
					while ( parseNextNode( token ) )
						token = readTokenContent();
//                    loseWhitespace( (Element) _currentNode );
					_currentNode = _currentNode.getParentNode();

					// If there is an orphan closing tag, now will be a good time
					// to see if it matches, or dump it. Do so cleveraly by using
					// the close clause with a second iteration.
					if ( _orphanClosingTag != null )
						return closingTag( _orphanClosingTag, true );
				}
			}
			else

			// Token is a closing tag. This can happen in three cases: the ending of
			// the currently open tag, the ending of some other tag (could be), and
			// the ending of an empty tag. The latter case happens, e.g. when '<P>'
			// if followed by '</P>'. This segment returns false if the current
			// open element is closed, but true if something else happens.
			if ( token == TOKEN_CLOSE_TAG )
			{
				if ( _tokenText.length() == 0 )
				{
					error( ERROR_WELL_FORMED, "Encountered closing tag with not tag name." );
					return true;
				}
				return closingTag( _tokenText.toString(), false );
			}
		}
		return true;
	}
	public final Node parseNode( Node node )
		throws ParseException
	{
		int    token;

		// Just to be on the safe side.
		if ( node == null )
			throw new NullPointerException( "Argument 'node' is null." );
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );
		_document = node.getOwnerDocument();

		// Read tokens, parse them into nodes and place these nodes in the node.
		// At the end of the parsing process, some text might remain in _nodeText,
		// so store it as a text node.
		try
		{
			_currentNode = node;
			token = readTokenContent();
			 while ( parseNextNode( token ) )
				token = readTokenContent();
			if ( _nodeText != null && _nodeText.length() > 0 )
			{
				_currentNode.appendChild( _document.createTextNode( _nodeText.toString() ) );
				_nodeText = null;
			}
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Finally close the parser and return the
		// node.
		catch ( ParseException except )
		{
			throw except;
		}
		catch ( Exception except )
		{
			error( except );
		}
		finally
		{
			close();
		}
		return node;
	}
}