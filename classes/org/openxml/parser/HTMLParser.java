package org.openxml.parser;

/**
 * org/openxml/parser/HTMLParser.java
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
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.DOMFactory;
import org.openxml.DTDDocument;
import org.openxml.XMLBookmarks;
import org.openxml.source.*;
import org.openxml.io.*;
import org.openxml.util.*;


/**
 * Implements a parser for HTML documents and nodes. The HTML document is created
 * with {@link DOMFactory}, loads the DTD document specified, and assures that
 * HTML, HEAD and BODY elements exist in its structure.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ContentParser
 * @see ParseException
 */
public final class HTMLParser
	extends ContentParser
{


	/**
	 * Holds the contents of text node collected between elements and other
	 * non-textual nodes. Consists of all plain text and resolved entities.
	 *
	 * @see #parseNextNode
	 */
	private StringBuffer        _nodeText = new StringBuffer();


	/**
	 * Used together with {@link #_nodeText} to remember if the last character
	 * placed there was a whitespace. Multiple whitespaces are consolidated by
	 * setting and checking this flag.
	 *
	 * @see #parseNextNode
	 */
	private boolean             _nodeTextSpace;


	/**
	 * Holds an orphan closing tag, one for which an opening tag could not be
	 * matched. Only a single orphan closing tag is supported.
	 */
	private String              _orphanClosingTag;


	private XMLBookmarks        _bookmarks;


	/**
	 * Parser constructor. Constructor will operate in the default mode of
	 * {@link #MODE_HTML_PARSER} with {@link #STOP_SEVERITY_FATAL}.
	 *
	 * @param reader Any {@link java.io.Reader} from which entity text can be read
	 * @param sourceURI URI of entity source
	 */
	public HTMLParser( Reader reader, String sourceURI )
	{
		this( reader, sourceURI, MODE_HTML_PARSER, STOP_SEVERITY_FATAL );
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
	public HTMLParser( Reader reader, String sourceURI, short mode, short stopAtSeverity )
	{
		super( reader, sourceURI, mode, stopAtSeverity );
	}
	/**
	 * Called to process the closing tag. This method is isolated from {@link
	 * #parseNextNode} and called on two occassions: when the closing tag is met,
	 * and when an orphan closing tag has been identified. In the first instance,
	 * it is called with <TT>keepQuite</TT> false, issuing an error if an orphan
	 * closing tag is met. In the second instance, the orphan closing tag is
	 * processed and so <TT>keepQuite</TT> is true.
	 *
	 * @param keepQuite True if orphan closing tag should not issue an error
	 * @return True if closing tag matches current element in {@link #_currentNode}
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	private boolean closingTag( boolean keepQuite )
		throws ParseException
	{
		String            name;
		HTMLElement        elem;
		Node            node;

		// Current node may not be an element, which defies the whole point of
		// checking the end tag. This only happens at the top level document,
		// particularly in ill-formed HTML documents.
		if ( ! ( _currentNode instanceof HTMLElement ) )
			return true;

		name = _tokenText.toString();
		elem = (HTMLElement) _currentNode;
		// Bingo! Closing tag closes the currently open tag. Return false,
		// which will land us right in the middle of the parseNextNode()
		// loop in the open tag token scenario of the including element.
		// HTML tags are case insensitive (stores in upper case).
		if ( elem.getTagName().equalsIgnoreCase( name ) )
			return false;
		// Darn! No open tag to close. Maybe there was an empty tag we
		// with an optional closing tag we can close. Look for one.
		// This option only supported for badly formatted HTML documents.
		node = _currentNode.getLastChild();
		while ( node != null )
		{
			if ( node instanceof HTMLElement )
			{
				elem = (HTMLElement) node;
				// If it's the same tag name, this is either the opening tag, or
				// a not-empty tag. If it can be matched, do so, otherwise end
				// the search.
				if ( elem.getTagName().equalsIgnoreCase( name ) )
				{
					if ( ! elem.hasChildNodes() && HTMLDTD.supportsChildren( elem ) )
					{
						// Okay, we found the '<P>' that closes with this '</P>'
						// (just an example). Now we take all the nodes inbetween,
						// cram them into the empty '<P>', and return true (since
						// we did not close the outer iteration).
						while ( elem.getNextSibling() != null )
							elem.appendChild( elem.getNextSibling() );
						loseWhitespace( elem );
						return true;
					}
					else
						break;
				}
			}
			node = node.getPreviousSibling();
		}
		// Desparately, keep the orphan closing tag, it might belong
		// someplace (e.g. '<P><FONT><B>text</P></B></FONT>').
		_orphanClosingTag = name;
		if ( ! keepQuite )
			error( ERROR_WELL_FORMED, "Closing tag '" + name.toUpperCase() + "' does not match with opening tag'" + elem.getTagName() + "'." );
		return true;
	}
	/**
	 * Strips whitespaces immediately after the opening tag and immediately before
	 * the closing tag. This operation is done once an element has been fully parsed.
	 *
	 * @param elem The element to work on
	 */
	private void loseWhitespace( HTMLElement elem )
	{
		Node    child;
		String    text;
		int        i;

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
		int            token;

		// Just to be on the safe side.
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );

		try
		{
			_document = DOMFactory.createHTMLDocument();
			token = readTokenContent();

			// If document type definition found, process it, otherwise, resume
			// with document content processing. '<!DOCTYPE' is followed by top-
			// level element name, and external DTD identifiers.
			if ( token == TOKEN_DTD && _tokenText.equals( "DOCTYPE" ) )
			{
				parseDTDSubset();
				token = readTokenContent();
			}

			_currentNode = _document;
			 while ( parseNextNode( token ) )
				token = readTokenContent();
			if ( _nodeText.length() > 0 )
				_currentNode.appendChild( _document.createTextNode( _nodeText.toString() ) );
			( (HTMLDocument) _document ).getBody();
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
	 * Parser the external DTD subset. A new {@link DTDDocument} is created,
	 * the external subset is optinally cached in memory, and public identifiers
	 * are possibly converted to URIs, as per the installed {@link
	 * HolderFinder}.
	 * <P>
	 * This method is called after '&lt;!DOCTYPE' has been consumed and returns
	 * after the terminating '&gt;' has been read.
	 */
	protected final void parseDTDSubset()
		throws ParseException, IOException
	{
		String  publicId = null;
		String  systemId = null;
		Source  source;
		Holder  holder;

		// Consume white spaces up to the root element name. If not found, this DTD
		// is invalid.
		while ( isSpace( readChar() ) )
			;
		pushBack();
		if ( readTokenName() )
		{
			if ( ! _tokenText.equals( "HTML" ) )
				error( ERROR_WELL_FORMED, "Only 'HTML' supported as root element in HTML document." );

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

			// If an external subset has been specified, process it as well using
			// the existing or a new DTD document. This assure that the external
			// subset appears to be declared after the internal subset. Report well
			// formed errors if the external subset could not be found.
			if ( systemId != null || publicId != null )
			{
				// Create a source to describe the entity and request a DTD
				// document. From that source request a suitable holder: if the
				// public identifier can be used, it probably will be.
				source = DOMFactory.newSource();
				source.setURI( systemId );
				source.setPublicId( publicId );
				source.setDocClass( Source.DOCUMENT_DTD );
				holder = DOMFactory.getHolderFinder().findHolder( source, false );
				if ( holder != null )
					_docType = (DTDDocument) holder.newInstance();
				else
					error( ERROR_WELL_FORMED, "Could not locate external DTD subset." );
			}
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
	 * <LI>CDATA sections are stored as {@link org.w3c.dom.CDATASection} if
	 *  in mode {@link #MODE_STORE_CDATA}, converted to plain text otherwise
	 * <LI>Comments are stored as {@link org.w3c.dom.Comment} if in mode {@link
	 *  #MODE_STORE_COMMENT}, ignored otherwise
	 * <LI>Processing instructions are stored as {@link
	 *  org.w3c.dom.ProcessingInstruction} if in mode {@link #MODE_STORE_PI},
	 *  ignored otherwise
	 * <LI>All whitespaces are converted to space (0x20) and multiple whitespaces
	 *  are consolidated in text, except for a few space preserving elements
	 * <LI>Entity references are stored as text, unresolved references are stored
	 *  as textual presentation, regardless of {@link #MODE_PARSE_ENTITY}
	 * <LI>Attributes are read according to the rules set forth in {@link
	 *  #parseAttributes}
	 * <LI>For the HTML elements &lt;PRE>, &lt;SCRIPT> and &lt;STYLE>,
	 *  all text until the closing tag is consumed and stored as is, without
	 *  parsing markup or character references
	 * <LI>For HTML elements with an optional closing tag, if the closing tag is
	 *  missing, an empty element is stored
	 * <LI>A single orphan closing tag is supported (while issuing a well formed
	 *  error); a closing orphan tag is one that is misplaced relative to the open
	 *  tag, e.g. '&ltP>&ltFONT>&ltB>text&lt/P>&lt/B>&lt/FONT>'
	 * <LI>White space immediately after the opening tag and immediately before
	 *  the closing tag is discarded
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
	protected boolean parseNextNode( int token )
		throws ParseException, IOException
	{
		Node        node;
		String        tokenText;
		int            i;
		char        ch;
		String        name;
		boolean        openTag;
		HTMLElement    elem;
		int            startLine;

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

		// CDATA is read by the parser, but not stored in the document tree, as
		// per the HTML DOM specification, but stored as text.
		if ( token == TOKEN_CDATA )
			token = TOKEN_TEXT;
		// Entity references are translated using an optimized mechanism in
		// HTMLDTD which is more efficient than DTD entities (HTML entities are
		// single character long). What cannot be understood is stored as is.
		if ( token == TOKEN_ENTITY_REF )
		{
			token = TOKEN_TEXT;
			i = HTMLDTD.charFromName( _tokenText.toString() );
			if ( i == -1 )
			{
				_tokenText.insert( 0, '&' ).append( ';' );
				error( ERROR_WELL_FORMED, "Found unresolved entity reference." );
			}
			else
			{
				_tokenText.setLength( 1 );
				_tokenText.setCharAt( 0, (char) i );
			}
		}

		// If token is a processing instruction or a comment and mode does not
		// require node to be stored in document tree, exit now. This will save
		// some overhead with storing text nodes before non-text nodes.
		if ( ( token == TOKEN_PI && ! isMode( MODE_STORE_PI ) ) ||
			 ( token == TOKEN_COMMENT && ! isMode( MODE_STORE_COMMENT ) ) )
			return true;

		// Section 2:
		// Now processing text from _tokenText. Several text tokens may follow so
		// rather than creating a Text node at this point, the text is accumulated
		// in _nodeText. When another token is met (a node, an element or the
		// closing tag), the contents of _nodeText is turned into a Text node and
		// added to the element.
		if ( token == TOKEN_TEXT )
		{
			// Append the text such that multiple whitespaces are converted to
			// a single space. The _nodeTextSpace flag is used for that purpose.
			for ( i = 0 ; i < _tokenText.length() ; ++i )
			{
				ch = _tokenText.charAt( i );
				// Note the use of CR. Although CR is not returned from readChar(),
				// it might be generated by a character reference and is supported
				// as a valid character code.
				if ( isSpace( ch ) )
				{
					if ( ! _nodeTextSpace )
					{
						_nodeTextSpace = true;
						_nodeText.append( SPACE );
					}
				}
				else
				{
					_nodeTextSpace = false;
					_nodeText.append( ch );
				}
			}
		}
		else
		// Section 3:
		{
			// If we reached this point, the token is either going to create a node
			// into the document tree or close the current element. All text
			// accumualted in _nodeText so far is added to the current element as a
			// Text node before commencing with per-token behavior.
			if ( _nodeText.length() > 0 )
			{
				node = _document.createTextNode( _nodeText.toString() );
				_currentNode.appendChild( node );
				_nodeText.setLength( 0 );
				_nodeTextSpace = false;
			}

			// If token is EOF, return false indicating we're done with current
			// element. EOF will be returned for as long as necessary until all open
			// elements have been closed in this manner. This check is done here and
			// not at the method's start due to the above Text node creation code.
			if ( token == TOKEN_EOF )
				return false;

			// Token is a processing instruction. If we reached this point, the
			// mode in effect commands storing processing instructions in the
			// document tree. Processing instruction contents is assured not to
			// contain any '?>'.
			if ( token == TOKEN_PI )
			{
				name = slicePITokenText();
				node = _document.createProcessingInstruction( name, _tokenText.toString() );
				_currentNode.appendChild( node );
			}
			else

			// Token is a comment. If we reached this point, the mode in effect
			// commands storing comments in the document tree. Comment contents is
			// assured not to contain any '--'.
			if ( token == TOKEN_COMMENT )
			{
				node = _document.createComment( _tokenText.toString() );
				_currentNode.appendChild( node );
			}
			else

			// Token is an opening tag. The tag name has been parsed, attribute values
			// should be read next.
			if ( token == TOKEN_OPEN_TAG )
			{
				// _tokenText is guranteed to contain the element's tag name.
				elem = (HTMLElement) _document.createElement( _tokenText.toUpperCase().toString() );
				_currentNode.appendChild( elem );

				// Attributes are read next based on the document type. The tag is
				// identified as an open tag not based on readAttribute(), but based
				// on its capacity to support children (e.g. <BR> is empty), or the
				// support of an optional closing tag (e.g. <P>).
				parseAttributes( elem, false );
				openTag = HTMLDTD.supportsChildren( elem ) && ! HTMLDTD.optionalClosingTag( elem );

				// In mode MODE_BOOKMARKS, if element has an ID attribute,
				// bookmark that identifier and make sure it's unique in the
				// document. XMLBookmarks object is created as needed and
				// setUnique() is called, throwing an exception if not unique.
				if ( isMode( MODE_BOOKMARKS ) )
				{
					name = elem.getId();
					if ( name != null )
					{
						if ( _bookmarks == null )
							_bookmarks = XMLBookmarks.of( _document );
						try
						{
							_bookmarks.setUnique( name, elem );
						}
						catch ( Exception except )
						{
							error( ERROR_WELL_FORMED, "Element identifier (ID attribute) is not unique in document element." );
						}
					}
				}

				// Three HTML elements are parsed differently. Their contents is read
				// as is, without markup or character reference parsing, and while
				// preserving whitespaces. Everything between the opening and closing
				// tag is consumed and used as the element's text contents.
				if ( elem instanceof HTMLPreElement ||
					 elem instanceof HTMLScriptElement ||
					 elem instanceof HTMLStyleElement )
				{
					name = elem.getTagName();
					_tokenText.setLength( 0 );
					readChar();
					while ( _curChar != EOF )
					{
						// If read '<', and read '/', and read the tag's name,
						// terminate the loop. All other characters are consumed.
						if ( _curChar == '<' )
						{
							readChar();
							if ( _curChar == '/' )
							{
								if ( canReadName( name ) )
									break;
								_curChar = '/';
							}
							pushBack();
							_curChar = '<';
						}
						_tokenText.append( (char) _curChar );
					}
					// Strip LF that comes immediately after opening tag or
					// immediately before closing tag. And only these two.
					if ( _tokenText.length() > 0 && _tokenText.charAt( 0 ) == LF )
// ! JDK 1.2 !
						_tokenText.delete( 0, 1 );
// ! JDK 1.1 !
//                        _tokenText = StringUtil.substring( _tokenText, 1 );
					if ( _tokenText.length() > 0 && _tokenText.charAt( _tokenText.length() - 1 ) == LF )
						_tokenText.setLength( _tokenText.length() - 1 );
					elem.appendChild( _document.createTextNode( _tokenText.toString().trim() ) );
					// Consume all characters until the closing tag's '>' is found.
					// If not found, issue an error. This complies with how other
					// elements are treated (see readTokenMarkup()).
					while ( _curChar != EOF && _curChar != '>' && _curChar != '<' )
						readChar();
					if ( _curChar == '<' )
						pushBack();
					if ( _curChar != '>' )
						error( ERROR_WELL_FORMED, "Element '" + name + "' terminated prematurely." );
					return true;
				}

				// If this is an open tag, read the contents of the element until
				// the closing tag (or end of file) is reached. The closing tag
				// (or end of file) is identified by parseNextNode() returning false.
				if ( openTag )
				{
					startLine = getLineNumber();
					_currentNode = elem;
					token = readTokenContent();
					while ( parseNextNode( token ) )
					{
						token = readTokenContent();
						if ( token == TOKEN_EOF )
						{
							error( ERROR_WELL_FORMED, "Element '" + elem.getTagName() + "' started at line " +
															startLine + " terminated prematurely at end of file." );
							break;
						}
					}
					( (Element) _currentNode ).normalize();
					_currentNode = _currentNode.getParentNode();
					loseWhitespace( elem );

					// If there is an orphan closing tag, now will be a good time
					// to see if it matches, or dump it. Do so cleveraly by using
					// the close clause with a second iteration.
					if ( _orphanClosingTag != null )
					{
						_tokenText.setLength( 0 );
						_tokenText.append( _orphanClosingTag );
						return  closingTag( true );
					}
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
				return closingTag( false );
			}
		}
		return true;
	}
	/**
	 * Parses a document fragment. A document fragment by definition does not
	 * contain a header or DTD and is not subject for validation. An empty
	 * document fragment (created from an existing document) must be supplied
	 * and the non-empty fragment is returned.
	 *
	 * @param fragment A {@link DocumentFragment} that is empty and compatible
	 * @return The same {@link DocumentFragment} object
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
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
			if ( _nodeText.length() > 0 )
				_currentNode.appendChild( _document.createTextNode( _nodeText.toString() ) );
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