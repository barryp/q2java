package org.openxml.parser;

/**
 * org/openxml/parser/BaseParser.java
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


import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import org.w3c.dom.*;
import org.openxml.dom.EntityImpl;
import org.openxml.DOMFactory;
import org.openxml.source.*;
import org.openxml.io.*;
import org.openxml.util.*;


/**
 * Implements layer 0, layer 1 and some layer 2 parsing methods, in addition to
 * error reportingand logging, and mode access.
 * <P>
 *
 * <H3>Layer 0 methods</H3>
 * Layer 0 methods perform low-level reading from the input stream. They support
 * line numbering, line terminator consolidation, and an unlimited pushback buffer.
 * Characters are read sequentially with {@link #readChar}, and pushed back to be
 * re-read with {@link #pushBack}. The last character read and the next character
 * to be pushed back are contained in the variable {@link #_curChar}. The value
 * {@link #EOF} indicates that the end of the input stream.
 * <P>
 * The method {@link #setEncoding} can be used to change the character encoding
 * mid-stream, but is effective only in the input stream is of type {@link
 * XMLStreamReader}. The method {@link #close} closes the parser once all input
 * has been parsed and {@link #isClosed} returns true afterwards.
 * <BR>
 *
 * <H3>Layer 1 methods</H3>
 * Layer 1 methods perform token reading from the input stream. The token code
 * is returned and the token value is contained in {@link #_tokenText}. The value
 * in {@link #_tokenText} is replaced each time one of these methods is called.
 * <P>
 * {@link #readTokenMarkup} reads and returns the markup token that follows the
 * '&lt;' sign; the '&lt;' has already been consumed. {@link #readTokenEntity}
 * reads and returns the general entity reference or character reference that
 * follows the '&amp;' sign; the '&amp;' has already been consumed. {@link
 * #readTokenPERef} reads and returns the parameter entity reference that follows
 * the '%' sign; the '%' sign has already been consumed. If a valid token is not
 * recognized, the sign ('&lt;', '&amp;' or '%') is returned as the token code
 * {@link #TOKEN_TEXT}.
 * <P>
 * The method {@link #readTokenName} reads and returns a valid token name.
 * The characters that consitute a valid token name are defined by {@link
 * #isNamePart}. The method {@link #canReadName} attempts to read and consume
 * the specified name.
 * <P>
 * In addition the following convenience methods are defined: {@link #isSpace}
 * identifies a whitespace character; {@link #isTokenAllSpace} returns true if
 * a token is all whitespace characters; {@link #slicePITokenText} slices a
 * processing instruction into target name and instruction; {@link
 * #readTokenQuoted} reads a quoted (single or double) string and returns it
 * as token text.
 * <BR>
 *
 * <H3>Layer 2 methods</H3>
 * Several layer 2 methods that are common to {@link ContentParser} and {@link
 * DTDParser} are also defined. {@link #parseGeneralEntity} parses a general
 * entity using an new parser instance. {@link #parseDocumentDecl} parses
 * the document declaration found in both XML documents, external subsets and
 * external entities.
 * <BR>
 *
 * <H3>Error reporting methods</H3>
 * The method {@link #getLastException} returns the last exception issued.
 * Expections are stored in a LIFO order and can be retrieved by calling
 * {@link ParseException#getPrevious} recursively on each exception.
 * <P>
 * An error is issued by calling one of the {@link #error} methods, either
 * storing the exception or throwing a {@link ParseException}, depending on the
 * severity level.
 * <P>
 * The error methods are defined as an {@link ErrorSinkHandler} interface, allowing
 * the definition of an external error sink. Typically the document parser serves
 * as an error sink for entity parsers (see {@link #setErrorSink}).
 * <BR>
 *
 * <H3>Mode methods</H3>
 * The method {@link #isMode} identifies which processing mode is in effect.
 * The processing mode is controlled by the constructor.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.openxml.io.Parser
 * @see ParseException
 * @see XMLStreamReader
 * @see ErrorSinkHandler
 */
public abstract class BaseParser
	implements Parser, ErrorSinkHandler
{



	/**
	 * Indicates that end of file (or the input stream) has been reached and no
	 * more character are availble. This character is invalid. It is legal to
	 * push is back, though.
	 */
	protected static final int        EOF = -1;


	/**
	 * Line feed. Character code 0x0A.
	 */
	protected static final char        LF = (char) 0x0A;


	/**
	 * Carriage return. Character code 0x0D.
	 */
	protected static final char        CR = (char) 0x0D;


	/**
	 * Space. Character code 0x20.
	 */
	protected static final char        SPACE = (char) 0x20;


	/**
	 * End of input. This token indicates that the end of the input stream (for
	 * this entity) has been reached. Reading past this token will always return
	 * this token.
	 */
	protected static final short    TOKEN_EOF = -1;


	/**
	 * Textual token. {@link #_tokenText} contains the plain text. This token
	 * is generally used to construct a {@link org.w3c.dom.Text} node when
	 * appearing in the content.
	 */
	protected static final short    TOKEN_TEXT = 0;


	/**
	 * Entity reference token. {@link #_tokenText} contains the entity name.
	 * This token is generally used to construct a {@link
	 * org.w3c.dom.EntityReference} node.
	 */
	protected static final short    TOKEN_ENTITY_REF = 1;


	/**
	 * Open tag token. {@link #_tokenText} contains the tag name. This token
	 * is generally used to construct a {@link org.w3c.dom.Element} node.
	 * Only the tag name is read, the attributes and terminating '>' should
	 * be read separately (see {@link ContentParser#parseAttributes}).
	 */
	protected static final short    TOKEN_OPEN_TAG = 2;


	/**
	 * Close tag token. {@link #_tokenText} contains the tag name. This token
	 * is generally used to construct a {@link org.w3c.dom.Element} node.
	 * The entire closing tag has been consumed.
	 */
	protected static final short    TOKEN_CLOSE_TAG = 3;


	/**
	 * Comment token. {@link #_tokenText} contains the comment text (if in mode
	 * {@link #MODE_STORE_COMMENT}). This token is generally used to construct
	 * a {@link org.w3c.dom.Comment} node.
	 */
	protected static final short    TOKEN_COMMENT = 4;


	/**
	 * Processing instruction token. {@link #_tokenText} contains the processing
	 * instruction (if in mode {@link #MODE_STORE_PI}). This token is generally
	 * used to construct a {@link org.w3c.dom.ProcessingInstruction} node.
	 */
	protected static final short    TOKEN_PI = 5;


	/**
	 * CDATA section token. {@link #_tokenText} contains the CDATA contents.
	 * This token is generally used to construct a {@link
	 * org.w3c.dom.CDATASection} node.
	 */
	protected static final short    TOKEN_CDATA = 6;


	/**
	 * DTD token. {@link #_tokenText} contains the DTD entity type (whatever comes
	 * after '<!'). This token is used to construct a number of DTD nodes for
	 * entity, element, attribute and document type declarations.
	 */
	protected static final short    TOKEN_DTD = 7;


	/**
	 * DTD section token. Indicates a '<![' that is followed by 'INCLUDE' or
	 * 'IGNORE'. If followed by 'CDATA', a {@link #TOKEN_CDATA} would have been
	 * returned. This token is valid only in the external DTD subset.
	 */
	protected static final short    TOKEN_SECTION = 8;


	/**
	 * DTD section token end. Indicates a ']]>' that terminates an 'INCLUDE' or
	 * 'IGNORE' section. The 'CDATA' terminator is always consumed for the {@link
	 * #TOKEN_CDATA} token. This token is valid only in the external DTD.
	 */
	protected static final short    TOKEN_SECTION_END = 9;


	/**
	 * Parameter entity reference. {@link #_tokenText} contains the entity name.
	 * This token is valid only in the DTD.
	 */
	protected static final short    TOKEN_PE_REF = 10;


	/**
	 * Holds the last character read by {@link #readChar}, or the next character
	 * to be pushed back (see {@link #pushBack}). Set to {@link #EOF} if end
	 * of input stream has been reached.
	 *
	 * @see #readChar
	 */
	protected int                   _curChar;


	/**
	 * Holds the contents of the last token read by one of the token reading
	 * methods. Set to {@link #TOKEN_EOF} if end of input stream has been reached.
	 * Many methods read, modify and possibly return values in {@link #_tokenText},
	 * so its value should not be assumed to remain constant between method calls.
	 * <P>
	 * A {@link StringBuffer} is allocated and constantly reused by resetting
	 * its length to zero. In some instances, it is replaced with an alternative
	 * {@link StringBuffer} object. IT IS IMPORTANT that no other variable will
	 * reference this {@link StringBuffer}.
	 *
	 * @see ContentParser#readTokenContent
	 */
	protected FastString            _tokenText = new FastString();


	/**
	 * The XML/HTML/DTD document being processed. When parsing an entity, this
	 * variable points to the document to which the entity belongs.
	 */
	protected Document              _document;


	/**
	 * Push back buffer used by {@link #pushBack} and {@link #readChar}.
	 *
	 * @see #pushBack
	 */
	private char[]                  _pushBackBuffer = new char[ 64 ];


	/**
	 * Push back buffer index used by {@link #pushBack} and {@link #readChar}.
	 * This is the index of the next character to place in the push back buffer,
	 * or one after the  first character to read from the buffer.
	 *
	 * @see #pushBack
	 */
	private int                     _pushBackIndex;


	/**
	 * The stream reader from which contents is obtained. The reader might be,
	 * but is not guaranteed to be {@link XMLStreamReader}.
	 */
	private Reader                  _reader;


	/**
	 * The current parsing mode, a combination of flags beginning with\
	 * <TT>MODE_..</TT>.
	 */
	private short                   _mode;


	/**
	 * The current line number in the input stream.
	 */
	private int                     _lineNumber;


	/**
	 * The current character position in the input stream.
	 */
	private int                     _position;


	/**
	 * URI that identifies the entity source.
	 */
	private String                  _sourceURI;


	/**
	 * True when document has been fully parsed and closed.
	 */
	private boolean                 _closed = false;


	/**
	 * Holds the last exception issued (whether stored or thrown).
	 *
	 * @see #error
	 */
	private ParseException          _lastException;


	/**
	 * Indicates the severity level at which to throw an exception rather
	 * than store it. Effective values are {@link #STOP_SEVERITY_FATAL},
	 * {@link #STOP_SEVERITY_VALIDITY} and {@link #STOP_SEVERITY_WELL_FORMED}.
	 *
	 * @see #error
	 */
	private short                   _stopAtSeverity;


	/**
	 * Reference to external error sink. Used primarily by external entity
	 * parsers that report errors back to the document entity parser.
	 */
	private ErrorSinkHandler        _errorSink;
	
	
	/**
	 * Parser constructor. Requires source text in the form of a {@link
	 * java.io.Reader} object and textual identifier. The parsing mode consists of
	 * a combination of <TT>MODE_..</TT> flags. The constructor specifies the error
	 * severity level at which to stop parsing, either {@link #STOP_SEVERITY_FATAL},
	 * {@link #STOP_SEVERITY_VALIDITY} or {@link #STOP_SEVERITY_WELL_FORMED}.
	 *
	 * @param reader Any {@link java.io.Reader} from which entity text can be read
	 * @param sourceURI URI of entity source
	 * @param mode The parsing mode in effect
	 * @param stopAtSeverity Severity level at which to stop parsing
	 */
	protected BaseParser( Reader reader, String sourceURI, short mode, short stopAtSeverity )
	{
		_reader = reader;
		_mode = mode;
		_stopAtSeverity = stopAtSeverity;
		_sourceURI = sourceURI;
	}
	/**
	 * Advances the line number count by the specified increment. Used when a
	 * different parses chews through the same input stream, obscuring the line
	 * number count. Specifically, when {@link DTDParser} is used to read the
	 * internal subset in an XML document.
	 *
	 * @param increment The line number increment
	 */
	protected final void advanceLineNumber( int increment )
	{
		_lineNumber = _lineNumber + increment;
	}
	/**
	 * Returns true if the specified name can be read and consumes it all.
	 * Attempts to read the name in its entirety. If the name is read, it is
	 * consumed from the input stream and the method returns true. If the name
	 * is not read, the input stream is not affected and the method return false.
	 *
	 * @return True if the name was read and consumed in its entirety
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final boolean canReadName( String name )
		throws IOException
	{
		int    i;

		for ( i = 0 ; i < name.length() ; ++i )
		{
			if ( readChar() != name.charAt( i ) )
			{
				pushBack();
				while ( i > 0 )
				{
					-- i;
					pushBack( name.charAt( i ) );
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * Closes the input stream.
	 */
	protected final void close()
	{
		try
		{
			_closed = true;
			_reader.close();
		}
		catch ( IOException except )
		{
		}
	}
	public final void error( Exception except )
		throws ParseException
	{
		if ( _errorSink != null )
			_errorSink.error( except );
		else
		{
			_lastException = new FatalParseException( this, _lastException, except );
			if ( Log.DEBUG )
			{
				Log.info( "Parser.error: Fatal at document [" + getSourceURI() + "] line " + getLineNumber() );
				Log.info( "Parser.error: Fatal: " + except.getClass().getName() + ": " + except.getMessage() );
			}
			throw _lastException;
		}
	}
	public final void error( short severity, String message )
		throws ParseException
	{
		if ( _errorSink != null )
			_errorSink.error( severity, message );
		else
		if ( severity == ERROR_VALIDITY )
		{
			_lastException = new ValidityException( this,  _lastException, message );
			if ( Log.DEBUG )
			{
				Log.info( "Parser.error: Validity at document [" + getSourceURI() + "] line " + getLineNumber() );
				Log.info( "Parser.error: Validity: " + message );
			}
			if ( _stopAtSeverity == STOP_SEVERITY_VALIDITY )
				throw _lastException;
		}
		else
		if ( severity == ERROR_WELL_FORMED )
		{
			_lastException = new WellFormedException( this, _lastException, message );
			if ( Log.DEBUG )
			{
				Log.info( "Parser.error: Well-formed at document [" + getSourceURI() + "] line " + getLineNumber() );
				Log.info( "Parser.error: Well-formed: " + message );
			}
			if ( _stopAtSeverity == this.STOP_SEVERITY_VALIDITY ||
				 _stopAtSeverity == this.STOP_SEVERITY_WELL_FORMED )
				throw _lastException;
		}
		else
		{
			_lastException = new FatalParseException( this, _lastException, message );
			if ( Log.DEBUG )
			{
				Log.info( "Parser.error: Fatal at document [" + getSourceURI() + "] line " + getLineNumber() );
				Log.info( "Parser.error: Fatal: " + message );
			}
			throw _lastException;
		}
	}
	/***********************************************************************
	 Error reporting and logging methods
	 ***********************************************************************/


	public ParseException getLastException()
	{
		return _lastException;
	}
	public final int getLineNumber()
	{
		return _lineNumber + 1;
	}
	/**
	 * Returns the current parsing mode.
	 *
	 * @return The current parsing mode
	 */
	protected final short getMode()
	{
		return _mode;
	}
	/**
	 * Returns the reader used for accessing the underlying input stream.
	 *
	 * @return Reader used by this parser
	 */
	protected final Reader getReader()
	{
		return _reader;
	}
	public final int getSourcePosition()
	{
		return _position;
	}
	public final String getSourceURI()
	{
		return _sourceURI;
	}
	/**
	 * Returns true if the document has been fully parsed and the parsed has been
	 * closed. Parsing operations should be rejected when the parser is closed.
	 * The parser is closed by calling the {@link #close} method.
	 *
	 * @return True if parser has been closed
	 */
	protected final boolean isClosed()
	{
		return _closed;
	}
	/***********************************************************************
	 Constructor and mode access methods
	 ***********************************************************************/


	/**
	 * Returns true if the specified parsing mode is in effect. For example,
	 * <TT>mode( MODE_STORE_COMMENT )</TT> will return true if comment storing
	 * mode has been selected. A combination of <TT>MODE_..</TT> flags can be
	 * used with this method.
	 *
	 * @param mode The mode(s) to check
	 * @return True if the mode(s) are in effect
	 */
	protected final boolean isMode( short mode )
	{
		return ( _mode & mode ) != 0;
	}
	/**
	 * Returns true if character is part of a valid name. The <TT>first</TT> flag
	 * indicates whether character is expected to be the first or any other in the
	 * name.
	 * <P>
	 * Valid names are defined as consisting of any letter, underscore or colon,
	 * followed by zero or more letters and digits, underscores, hyphens, colons
	 * and periods. Unlike other languages, letters and digits can be specified
	 * in all Unicode supported languages.
	 *
	 * @param ch The character to test
	 * @param first True if first letter in the name
	 * @return True if character is valid in name context
	 */
	protected final boolean isNamePart( int ch, boolean first )
	{
		if ( first )
			return ( Character.isLetter( (char) ch ) || ch == '_' || ch == ':' );
		else
			return ( Character.isLetterOrDigit( (char) ch ) ||
					 ch == '_' || ch == ':' || ch == '-' || ch == '.' );
	}
	/**
	 * Returns true if character is a whitespace. Space (0x20), tab (0x09), line
	 * feed (0x0A), carriage return (0x0D) and form feed (0x0C) are defined as
	 * whitespaces.
	 *
	 * @param ch The character to check
	 * @return True if character is a whitespace
	 */
	protected final boolean isSpace( int ch )
	{
		return ( ch == SPACE || ch == LF || ch == CR || ch == 0x09 || ch == 0x0C );
	}
	/**
	 * Returns true if the token is all whitespace. The token is contained in
	 * {@link #_tokenText} and all its characters must be whitespace as defined
	 * by {@link #isSpace}.
	 *
	 * @return True if {@link #_tokenText} is all whitespace
	 */
	protected final boolean isTokenAllSpace()
	{
		int i;
		int ch;

		for ( i = 0 ; i < _tokenText.length() ; ++i )
		{
			ch = _tokenText.charAt( i );
			if ( ! ( ch == SPACE || ch == LF || ch == CR || ch == 0x09 || ch == 0x0C ) )
				return false;
		}
		return true;
	}
	/**
	 * Parses the document declaration for XML documents and external entities,
	 * returning the standalone status and changing the character encoding (if
	 * necessary). Errors are issued if the document declaration is invalid.
	 * <P>
	 * The document declaration is contained in a processing instruction that
	 * appears at the very beginning of the document or entity and begins with
	 * 'xml' (case sensitive). The processing instruction's full text is expected
	 * in the variable {@link #_tokenText} on entry.
	 * <P>
	 * The declaration for XML documents contains a version number, optional
	 * character encoding and optional standalone status. The default standalone
	 * status is false. The declaration for external entities and external subsets
	 * contains an optional version number, and mandatory character encoding.
	 * <P>
	 * Currently only XML version "1.0" is supported. The current character
	 * encoding is changed by calling {@link #setEncoding}.
	 *
	 * @param XMLDecl True if expecting XML document declaration, false if expecting
	 *  external entity/subset declaration
	 * @return True if XML document is standalone
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final boolean parseDocumentDecl( boolean XMLDecl )
		throws ParseException
	{
		StringTokenizer tokenizer;
		String          text;
		String          version;
		String          encoding;
		boolean         standalone;
		char            quote;

		// XMLDecl := '<?xml' S VersionInfo S EncodingDecl? (S SDDecl)? S? '?>'
		// TextDecl := '<?xml' (S VersionInfo)? S EncodingDecl S? '?>'
		// VersionInfo := 'version' Eq Qt [a-zA-Z0-9_.:-]+ Qt
		// EncodingDecl := 'encoding' Eq Qt [A-Za-z][A-Za-z0-9._-]* Qt
		// SDDecl := 'standalone' Eq Qt 'yes' | 'no' Qt

		standalone = false;
		version = null;
		encoding = null;

		// Use a tokenizer to skip spaces between tokens. Note that some spaces
		// are optional (around quotes), and some never come to place (the space
		// before the terminating '?>'). Immediately skip the first token ('xml').
		tokenizer = new StringTokenizer( _tokenText.toString(), " \n\r\f\t" );
		if ( tokenizer.nextToken() != null && tokenizer.hasMoreTokens() )
		{
			text = tokenizer.nextToken();

			// If next token is 'version', read the version identifier.
			// At the end of the process the next token that is not part of the
			// version identifier is contained in the variable text.
			if ( text.startsWith( "version" ) )
			{
				if ( text.length() > 7 && text.charAt( 7 ) == '=' )
				{
					if ( text.length() > 8 )
						version = text.substring( 8 );
					else
						version = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
				}
				else
				{
					text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					if ( text != null && text.charAt( 0 ) == '=' )
					{
						if ( text.length() > 1 )
							version = text.substring( 1 );
						else
							version = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					}
				}
				text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
			}

			// If next token is 'encoding', read the encoding identifier.
			// At the end of the process the next token that is not part of the
			// encoding identifier is contained in the variable text.
			if ( text != null && text.startsWith( "encoding" ) )
			{
				if ( text.length() > 8 && text.charAt( 8 ) == '=' )
				{
					if ( text.length() > 9 )
						encoding = text.substring( 9 );
					else
						encoding = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
				}
				else
				{
					text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					if ( text != null && text.charAt( 0 ) == '=' )
					{
						if ( text.length() > 1 )
							encoding = text.substring( 1 );
						else
							encoding = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					}
				}
				text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
			}

			// If next token is 'standalone', read the standalone status.
			// At the end of the process the next token that is not part of
			// the standalone status is contained in the variable text.
			// This only applies to XML documents.
			if ( XMLDecl && text != null && text.startsWith( "standalone" ) )
			{
				if ( text.length() > 10 && text.charAt( 10 ) == '=' )
				{
					if ( text.length() > 11 )
						text = text.substring( 11 );
					else
						text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
				}
				else
				{
					text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					if ( text != null && text.charAt( 0 ) == '=' )
					{
						if ( text.length() > 1 )
							text = text.substring( 1 );
						else
							text = ( tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null );
					}
				}

				// Standalone value is either quoted 'yes' or 'no', but might just
				// be null or none of the above.
				if ( text != null )
				{
					text = text.toLowerCase();
					if ( text.startsWith( "\"yes\"" ) || text.startsWith( "\'yes\'" ) )
						standalone = true;
					else
					if ( text.startsWith( "\"no\"" ) || text.startsWith( "\'no\'" ) )
						standalone = false;
					else
						text = null;
				}
				if ( text == null )
					error( ERROR_WELL_FORMED, "Missing 'yes'|'no' value for standalone document declaration." );
			}

		}

		// XML document is missing 'version', while external entity is missing
		// 'encoding': issue an error. Only version 1.0 XML supported by this DOM.
		if ( XMLDecl && version == null )
			error( ERROR_WELL_FORMED, "XML declaration missing mandatory version information." );
		else
		if ( ! XMLDecl && encoding == null )
			error( ERROR_WELL_FORMED, "External entity declaration missing mandatory encoding information." );
		if ( version != null && ! version.equals( "\'1.0\'" ) && ! version.equals( "\"1.0\"" ) )
			error( ERROR_WELL_FORMED, "Only documents tagged XML version 1.0 are properly recognized by this parser." );

		// If encoding specified, change the encoding of the input stream.
		if ( encoding != null )
		{
			quote = encoding.charAt( 0 );
			if ( encoding.length() < 3 || ( quote != '\'' && quote != '\"' ) ||
				 encoding.charAt( encoding.length() -1 ) != quote )
				error( ERROR_WELL_FORMED, "Encoding identifier not properly quoted." );
			else
			{
				encoding = encoding.substring( 1, encoding.length() - 2 );
				setEncoding( encoding );
			}
		}

		// Return standalone flag only valid for XML document declaration.
		return standalone;
	}
	/***********************************************************************
	 Layer 2 methods: general entity and document declaration parsing
	 ***********************************************************************/


	/**
	 * Parses the general entity, returning the entity as parsed. An existing
	 * {@link org.openxml.dom.EntityImpl} is passed to the method. On exit,
	 * the same entity (parsed) is returned, or null to indicate that the entity
	 * could not be parsed.
	 * <P>
	 * The following rules govern how the entity is parsed:
	 * <UL>
	 * <LI>If the entity's state is {@link EntityImpl#STATE_PARSED}, then the
	 *  entity has been parsed before, and is returned.
	 * <LI>If the entity's state is {@link EntityImpl#STATE_NOT_FOUND}, then
	 *  the entity could not be found, and null is returned. There is no need
	 *  to issue an error again.
	 * <LI>If the entity's state is {@link EntityImpl#STATE_PARSING}, then the
	 *  entity is being parsed: this is a circular reference, an error is issued
	 *  and null is returned.
	 * <LI>If the entity's state is {@link EntityImpl#STATE_DECLARED}, then the
	 *  entity is being parsed. For an external entity, the entity source is being
	 *  located using {@link HolderFinder}. If the entity source could
	 *  not be found or could not be opened, the entity state is set to {@link
	 *  EntityImpl#STATE_NOT_FOUND}, an error is issued and null returned.
	 *  For an internal entity, the entity source is created from it's value.
	 * <LI>If the entity's state is {@link EntityImpl#STATE_DECLARED} and the
	 *  entity source could be located, an {@link XMLParser} is created and used
	 *  to parse the entity. If no fatal errors are encountered when parsing,
	 *  the entity is returned. Well formed errors are treated as if generated
	 *  by the current parser.
	 * <LI>If the entity's state is {@link EntityImpl#STATE_DECLARED} and a fatal
	 *  error was issued while parsing the entity with an {@link XMLParser}, then
	 *  a fatal error is issued and an exception raised.
	 * </UL>
	 * Well formed and validity errors issued by the external entity parser are
	 * reported directly to this parser and treated by the error mode of this
	 * entity. Fatal and I/O exceptions will terminate parsing with a fatal error.
	 *
	 * @param entity The entity to parse
	 * @return The entity if parsed, null if could not be parsed
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final EntityImpl parseGeneralEntity( EntityImpl entity )
		throws ParseException
	{
		Holder      holder;
		Source      source;
		XMLParser   parser;
		Reader      reader = null;

		switch ( entity.getState() )
		{
		// Entity has been parsed before and apparently successfuly, so just
		// return it.
		case EntityImpl.STATE_PARSED:
			return entity;

		// Entity could not be found. Meaning that an attempt to parse the entity
		// was taken in the past, but the entity source could not be found. Do not
		// report an error again, just return null.
		case EntityImpl.STATE_NOT_FOUND:
			return null;

		// Entity is now being parsed. This can only imply one thing: a circular
		// reference. Returning null indicates that Entity could not be parsed.
		case EntityImpl.STATE_PARSING:
			error( ERROR_WELL_FORMED, "Entity '" + entity.getNodeName() + "' contains circular reference." );
			return null;

		// Entity has not been parsed, yet. Attempt to locate the entity source,
		// parse it into the entity and return the parsed entity. Change the
		// entity's state to one of the above flags.
		case EntityImpl.STATE_DECLARED:
			// If entity is internal, create a parser for it's value. If entity
			// is external, get it's URL and open a connection to that URL.
			// If source could not be found or opened, entity becomes not found,
			// an error is issued and null returned.
			try
			{
				if ( entity.isInternal() )
				{
					reader = new StringReader( entity.getInternal() );
					parser = new XMLParser( this, reader, getSourceURI() + " Entity: " + entity.getNodeName() );
				}
				else
				{
					// Create a source to describe the entity and request an
					// XML document. From that source request a suitable holder:
					// if the public identifier can be used, it probably will.
					// Given that a holder could be granted, get the entity
					// reader from it and proceed to parse the entity contents.
					source = DOMFactory.newSource();
					source.setURI( entity.getSystemId() );
					source.setPublicId( entity.getPublicId() );
					source.setDocClass( Source.DOCUMENT_XML );
					holder = DOMFactory.getHolderFinder().findHolder( source, false );
					if ( holder != null )
						reader = holder.getReader();
					if ( reader == null || holder == null )
					{
						entity.setState( EntityImpl.STATE_NOT_FOUND );
						error( ERROR_WELL_FORMED, "External entity '" + entity.getNodeName() + "' could not be found." );
						return null;
					}
					parser = new XMLParser( this, reader, source.toString() );
				}

				// Switch the entity state to parsing, to detect circular references.
				// Parse the entity and switch it to parsed. If a fatal (or IO) error
				// is encountered when parsing, the exception will change the state
				// to not found. Since this parser is the error sink for the external
				// entity parser, all errors are reported directly to this parser.
				entity.setState( EntityImpl.STATE_PARSING );
				parser.parseEntity( entity, entity.isInternal() );
				entity.setState( EntityImpl.STATE_PARSED );
			}
			catch ( ParseException except )
			{
				entity.setState( EntityImpl.STATE_NOT_FOUND );
				return null;
			}
			catch ( IOException except )
			{
				entity.setState( EntityImpl.STATE_NOT_FOUND );
				error( ERROR_WELL_FORMED, "External entity '" + entity.getNodeName() + "' could not be found. Reason:" +
										  except.getMessage() );
				return null;
			}
			return entity;

		default:
			// Entity should never be in an undetermined state.
			throw new IllegalStateException( "Entity in illegal state." );
		}
	}
	/**
	 * Push back the last character read into {@link #_curChar}. The pushed back
	 * character will be returned when {@link #readChar} is called next. Any number
	 * of characters can be pushed back. The push back buffer is a LIFO stack,
	 * so text should be pushed back in reverse order. It is not an error to push
	 * back the value {@link #EOF}.
	 */
	protected final void pushBack()
	{
		pushBack( _curChar );
	}
	/**
	 * Push back a single character. The pushed back character will be returned
	 * when {@link #readChar} is called next. Any number of characters can be pushed
	 * back. The push back buffer is a LIFO stack, so text should be pushed back in
	 * reverse order. It is not an error to push back the value {@link #EOF}.
	 *
	 * @param ch The character to push back
	 */
	protected final void pushBack( int ch )
	{
		if ( ch != EOF )
		{
			if ( _pushBackIndex == _pushBackBuffer.length )
			{
				char[]    newBuffer;

				newBuffer = new char[ _pushBackIndex * 2 ];
				System.arraycopy( newBuffer, _pushBackIndex, _pushBackBuffer, 0, _pushBackIndex );
				_pushBackBuffer = newBuffer;
			}
			_pushBackBuffer[ _pushBackIndex ] = (char) ch;
			++ _pushBackIndex;
		}
	}
	/***********************************************************************
	 Layer 0 methods: reading from input stream
	 ***********************************************************************/


	/**
	 * Reads and returns a single character from the input stream. If characters
	 * were pushed back they are returned in the same order they were pushed (LIFO).
	 * If end of input stream has been reached, {@link #EOF} is returned. The
	 * returned character is also available in the {@link #_curChar} variable.
	 * <P>
	 * Line breaks (LF, CR and CR+LF) are returned as a single line feed (0x0A)
	 * character.
	 *
	 * @return A single character read from the input stream, also available in
	 *  {@link #_curChar}
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final int readChar()
		throws IOException
	{
		// If character available in push back buffer, read last character
		// stored in push back buffer and return it.
		if ( _pushBackIndex > 0 )
			_curChar = _pushBackBuffer[ -- _pushBackIndex ];
		else
		{
			// Read a single character from the input stream and return it. LF, CR
			// and CR+LF are converted to LF.
			_curChar = _reader.read();
			if ( _curChar != EOF )
				++ _position;
			// If found either LF, CR or CR + LF, return a single LF character.
			if ( _curChar == LF || _curChar == CR )
			{
				if ( _curChar == CR )
				{
					_curChar = _reader.read();
					if ( _curChar != EOF )
						++ _position;
					if ( _curChar != LF )
						pushBack();
				}
				_curChar = LF;
				++_lineNumber;
			}
		}
		return _curChar;
	}
	/**
	 * Reads general entity reference token or character reference. Returns
	 * the token code {@link #TOKEN_ENTITY_REF} and the entity name in {@link
	 * #_tokenText}. The preceding '&amp;' has been consumed prior to calling this
	 * method, and the trailing ';' is consumed by this method. No valid character
	 * is held in {@link #_curChar} on entry or exit.
	 * <P>
	 * If no valid entity name is found, the token code {@link #TOKEN_TEXT} is
	 * returned, with '&amp;' contained in {@link #_tokenText} and the input
	 * stream is not affected.
	 * <P>
	 * A '#' sign indicates a character reference (either decimal or hexadecimal)
	 * which is read and stored in {@link #_tokenText}, and the token code {@link
	 * #TOKEN_TEXT} is returned. If the character reference value is invalid,
	 * the token code {@link #TOKEN_TEXT} is returned, with '&amp;' contained in
	 * {@link #_tokenText} and the input stream is not affected.
	 * <P>
	 * If the entity reference or character reference is not terminated with a ';',
	 * a well-formed error is issued, but the entity reference is still regarded
	 * valid.
	 *
	 * @return Token code either {@link #TOKEN_ENTITY_REF} or {@link #TOKEN_TEXT}
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final int readTokenEntity()
		throws ParseException, IOException
	{
		int        code;
		int        x;

		// Read character immediately following '&' and decide what to do next
		// based on that character.
		_tokenText.setLength( 0 );
		readChar();

		// Case of '&#' this is a character reference. Value is given in decimal,
		// or if 'x|X' follows, in hexadeciaml. If value is clearly invalid,
		// return '&#' or '&#x' as plain text.
		if ( _curChar == '#' )
		{
			x = readChar();
			if ( x == 'x' || x == 'X' )
			{
				// '&#x' is followed by any number of hexadecimal digits that form
				// a character code. The terminating ';' is expected, but not
				// required to terminate this character reference.
				if ( Character.digit( (char) readChar(), 16 ) >= 0 )
				{
					code = 0;
					while ( Character.digit( (char) _curChar, 16 ) >= 0 )
					{
						code = ( code << 4 ) + Character.digit( (char) _curChar, 16 );
						readChar();
					}
					if ( _curChar != ';' )
					{
						pushBack();
						error( ERROR_WELL_FORMED, "Character reference terminated permaturely." );
					}
					_tokenText.append( (char) code );
					return TOKEN_TEXT;
				}

				// '&#x' that is not followed by digits: '&' is returned in
				// _tokenText, '#x' is pushed back to the input stream.
				else
				{
					pushBack();
					pushBack( x );
					pushBack( '#' );
					_tokenText.append( "&" );
					return TOKEN_TEXT;
				}
			}
			else

			// '&#' is followed by any number of digits that form a character code.
			// The terminating ';' is expected, but not required to terminate this
			// character reference.
			if ( _curChar >= '0' && _curChar <= '9' )
			{
				code = 0;
				while ( _curChar >= '0' && _curChar <= '9' )
				{
					code = ( code * 10 ) + Character.digit( (char) _curChar, 10 );
					readChar();
				}
				if ( _curChar != ';' )
				{
					pushBack();
					error( ERROR_WELL_FORMED, "Character reference terminated permaturely." );
				}
				_tokenText.append( (char) code );
				return TOKEN_TEXT;
			}

			// '&#' that is not followed by digits: '&' is returned in _tokenText,
			// '#' is pushed back to the input stream.
			else
			{
				pushBack();
				pushBack( '#' );
				_tokenText.append( "&" );
				return TOKEN_TEXT;
			}
		}

		// Not a character reference, than probably an entity reference.
		// Get the entity name. If name is missing, return '&' as plain text.
		// The terminating ';' is expected, but not required to terminate this
		// entity reference.
		pushBack();
		if ( ! readTokenName() )
		{
			_tokenText.append( '&' );
			return TOKEN_TEXT;
		}
		readChar();
		if ( _curChar != ';' )
		{
			pushBack();
			error( ERROR_WELL_FORMED, "Entity reference terminated permaturely." );
		}

		// Return tokan for entity reference and it's name.
		return TOKEN_ENTITY_REF;
	}
	/***********************************************************************
	 Layer 1 methods: token reading methods
	 ***********************************************************************/


	/**
	 * Reads markup token. Returns the token code and the token value in {@link
	 * #_tokenText}. The preceding '&lt;' has been consumed prior to calling this
	 * method. No valid character is held in {@link #_curChar} on entry or exit.
	 * <P>
	 * The following rules govern how tokens are parsed and which code is returned:
	 * <UL>
	 * <LI>{@link #TOKEN_OPEN_TAG} returned for opening tag. Opening tag is '&lt;'
	 *  immediately followed by valid tag name (returned as token text) and
	 *  optional whitespace. Attributes and terminating '&gt;' are not read by
	 *  this method. A whitespace between the '&lt;' and tag name is not allowed
	 * <BR>
	 * <LI>{@link #TOKEN_CLOSE_TAG} returned for closing tag. Closing tag is '&lt;/'
	 *  followed by valid tag name (returned as token text) and '&gt'. All text
	 *  following the tag name until the terminating '&gt;' is ignored; a whitespace
	 *  between the '&lt;' and tag name is not allowed; an empty tag name will be
	 *  returned.
	 * <BR>
	 * <LI>{@link #TOKEN_COMMENT} returned for comment. Comment is terminated with
	 *  '&lt;!--' and '--&gt;'. All text inbetween is consumed, and returned as
	 *  token text if in mode {@link #MODE_STORE_COMMENT}.
	 * <BR>
	 * <LI>{@link #TOKEN_CDATA} returned for CDATA section. Section starts with
	 *  '&lt;![CDATA[' and ends with ']]>'. All text inbetween is consumed and
	 *  returned as token text.
	 * <BR>
	 * <LI>{@link #TOKEN_PI} returned for processing instruction. Processing
	 *  instruction is terminated with '&lt;?' and '?&gt;'. All text inbetween is
	 *  consumed, and returned as token text if in mode {@link #MODE_STORE_PI}.
	 * <BR>
	 * <LI>{@link #TOKEN_DTD} returned for DTD declaration. DTD declaration starts
	 *  with '&lt;!' immediately followed by a token name (returned as token text).
	 *  All other declaration contents is not read by this method. A whitespace
	 *  between the '&lt;!' and the token name is not allowed, and the token name
	 *  is all uppercase letters.
	 * <BR>
	 * <LI>{@link #TOKEN_SECTION} returned for DTD conditional section. Conditional
	 *  section begins with '&lt;![' and is not a CDATA section. Only the '&lt;!['
	 *  sequence is read and consumed by this method.
	 * </UL>
	 * If no markup is found, the token code {@link #TOKEN_TEXT} is returned, with
	 * '&lt;' contained in {@link #_tokenText} and the input stream is not affected.
	 *
	 * @return The markup token code of {@link #TOKEN_TEXT}
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final int readTokenMarkup()
		throws ParseException, IOException
	{
		// Read character immediately following '<' and decide what to do next
		// based on that character.
		_tokenText.setLength( 0 );
		readChar();

		// Case of '<?' signifies a processing instruction. The contents of the
		// processing instruction is read literally until the terminating '?>'.
		if ( _curChar == '?' )
		{
			readChar();
			while ( _curChar != EOF )
			{
				// If not '?', accumulate the character read. If '?' and not
				// followed by '>', accumulate the '?' and use the following
				// character in the next iteration. If '?>', return token code.
				if ( _curChar == '?' )
				{
					if ( readChar() == '>' )
						return TOKEN_PI;
					_tokenText.append( '?' );
				}
				else
				{
					_tokenText.append( (char) _curChar );
					readChar();
				}
			}
			if ( _curChar == EOF )
				error( ERROR_WELL_FORMED, "Processing instruction terminated permaturely at end of file." );
			return TOKEN_PI;
		}
		else

		// Case of '<!', this is either a comment, a CDATA section, or a DTD
		// element definition. Detemine which is which first. If none of the
		// above, push '!' back and return '<' as plain text.
		if ( _curChar == '!' )
		{

			readChar();
			// '<!-' is possibly a comment, if a second hyphen follows. Consume the
			// entire comment until the terminating '-->' and return it. The contents
			// of the comment is accumulated only if in mode MODE_STORE_COMMENT.
			// If this is not a comment, push back everything and return less-than
			// as plain text.
			if ( _curChar == '-' )
			{
				if ( readChar() != '-' )
				{
					// Push back the last non-hyphen character and the hyphen.
					// Return the plain text '<'.
					pushBack();
					pushBack( '-' );
					_tokenText.append( '<' );
					return TOKEN_TEXT;
				}

				readChar();
				while ( _curChar != EOF )
				{
					// If encoutering an hyphen read the next character and if
					// also an hypen, this is probably the end of the comment.
					// If not, just add the first hyphen and continue processing
					// the rest.
					if ( _curChar == '-' )
					{
						if ( readChar() == '-' )
						{
							if ( readChar() == '>' )
								return TOKEN_COMMENT;
							else
								pushBack();
							_curChar = '-';
						}
						if ( isMode( MODE_STORE_COMMENT ) )
							_tokenText.append( '-' );
					}
					else
					{
						if ( isMode( MODE_STORE_COMMENT ) )
							_tokenText.append( (char) _curChar );
						readChar();
					}
				}
				if ( _curChar == EOF )
					error( ERROR_WELL_FORMED, "Comment terminated permaturely at end of file." );
				return TOKEN_COMMENT;
			}
			else

			// '<![CDATA[' begins a CDATA section. If 'CDATA[' cannot be read,
			// all is returned back to the push-back buffer and less-than is
			// returned as plain text. All characters up to terminating ']]>'
			// are consumed.
			if ( _curChar == '[' )
			{
				if ( canReadName( "CDATA[" ) )
				{
					readChar();
					while ( _curChar != EOF )
					{
						// If not ']', accumulate the character read. If ']' and
						// not followed by another ']', accumulate the first ']'
						// and use the second one in the next iteration. If ']]'
						// and not followed by '>', accumulate both ']]' and use
						// '>' in the next iteration. If ']]>', return token code.
						if ( _curChar == ']' )
						{
							if ( readChar() == ']' )
							{
								if ( readChar() == '>' )
									return TOKEN_CDATA;
								_tokenText.append( ']' );
							}
							_tokenText.append( ']' );
						}
						else
						{
							_tokenText.append( (char) _curChar );
							readChar();
						}
					}
					if ( _curChar == EOF )
						error( ERROR_WELL_FORMED, "CDATA section terminated permaturely at end of file." );
					return TOKEN_CDATA;
				}
				else
				{
					// Return the token indicating this is possibly an IGNORE/INCLUDE
					// section. This value is used by the DTD parser. XML and HTML
					// parser will rather convert it to plain text.
					return TOKEN_SECTION;
				}
			}
			else

			// Not comment and not CDATA section, this is a DTD definition.
			// Read the DTD entity type (the uppercase name that comes after the
			// '<!') and return it.
			while ( _curChar >= 'A' && _curChar <= 'Z' )
			{
				_tokenText.append( (char) _curChar );
				readChar();
			}
			pushBack();
			return TOKEN_DTD;

		}
		else

		// Case of '</' is a closing tag. A closing tag ends with the closest
		// '>' or '<' (invalid, but still processed). Anything inbetween is
		// consumed. The first valid characters form the tag name, anything
		// else is ignored.
		if ( _curChar == '/' )
		{
			readTokenName();
			readChar();
			while ( _curChar != EOF && _curChar != '<' && _curChar != '>' )
				readChar();
			if ( _curChar == EOF )
				error( ERROR_WELL_FORMED, "Closing tag terminated permaturely at end of file." );
			if ( _curChar == '<' )
			{
				pushBack();
				error( ERROR_WELL_FORMED, "Closing tag terminated permaturely." );
			}
			return TOKEN_CLOSE_TAG;
		}

		// Nothing else identified, this is an opening tag. The first valid
		// characters form the tag name. Subsequent whitespaces are consumed.
		// The attribute or terminating '>' will be read some other time.
		pushBack();
		if ( readTokenName() )
		{
			while ( isSpace( readChar() ) )
				;
			pushBack();
			return TOKEN_OPEN_TAG;
		}

		// If '<' not followed by valid tag name, return plain text.
		_tokenText.append( '<' );
		return TOKEN_TEXT;
	}
	/**
	 * Reads a valid token name and places it in {@link #_tokenText}. If a valid
	 * name can be read, it is placed in {@link #_tokenText} and true is returned,
	 * otherwise false is returned and the input stream is not affected.
	 * <P>
	 * A valid token name is defined as consisting of any letter, underscore or
	 * colon, followed by zero or more letters and digits, underscores, hyphens,
	 * colons and periods. Unlike other languages, letters and digits can be
	 * specified in all Unicode supported languages.
	 * <P>
	 * {@link #_curChar} does not contain a valid value on either entry or exit
	 * from this method.
	 *
	 * @return True if valid name of at least one character found
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final boolean readTokenName()
		throws IOException
	{
		_tokenText.setLength( 0 );
		if ( isNamePart( readChar(), true ) )
		{
			_tokenText.append( (char) _curChar );
			while ( isNamePart( readChar(), false ) )
				_tokenText.append( (char) _curChar );
			pushBack();
			return true;
		}
		pushBack();
		return false;
	}
	/**
	 * Reads parameter entity reference token. Returns the token code {@link
	 * #TOKEN_PE_REF} and the entity name in {@link #_tokenText}. The preceding
	 * '%' has been consumed prior to calling this method, and the trailing ';'
	 * is consumed by this method. No valid character is held in {@link #_curChar}
	 * on entry or exit.
	 * <P>
	 * If no valid entity name is found, the token code {@link #TOKEN_TEXT} is
	 * returned, with '%' contained in {@link #_tokenText} and the input stream
	 * is not affected.
	 * <P>
	 * If the entity reference is not terminated with a ';', a well-formed error
	 * is issued, but the entity reference is still regarded valid.
	 *
	 * @return Token code either {@link #TOKEN_PE_REF} or {@link #TOKEN_TEXT}
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final int readTokenPERef()
		throws ParseException, IOException
	{
		// Get the entity name. If name is missing, return '%' as plain text.
		// The terminating ';' is expected, but not required to terminate this
		// entity reference.
		_tokenText.setLength( 0 );
		if ( ! readTokenName() )
		{
			_tokenText.append( '%' );
			return TOKEN_TEXT;
		}
		readChar();
		if ( _curChar != ';' )
		{
			pushBack();
			error( ERROR_WELL_FORMED, "Parameter entity reference terminated permaturely." );
		}
		// Return tokan for entity reference and it's name.
		return TOKEN_PE_REF;
	}
	/**
	 * Reads the quoted identifier token. Quotes can be either single or double,
	 * but both opening and closing quotes must be identical. Everything inbetween
	 * the quotes is stored in {@link #_tokenText}. Returns true if a quoted value
	 * was found (i.e. opening quote followed on the input stream).
	 *
	 * @return True if quoted value was found
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final boolean readTokenQuoted()
		throws ParseException, IOException
	{
		int                quote;
		StringBuffer    text;
		String            name;
		Node            node;

		_tokenText.setLength( 0 );
		quote = readChar();
		// Quote can be either single or double, opening and closing quotes must
		// be identical, everything else is unacceptable.
		if ( quote == '\'' || quote == '\"' )
		{
			text = new StringBuffer( 32 );
			readChar();
			while ( _curChar != quote && _curChar != EOF )
			{
				_tokenText.append( (char) _curChar );
				readChar();
			}
			if ( _curChar == EOF )
				error( ERROR_WELL_FORMED, "Quoted identifier terminated prematurely." );
			return true;
		}
		pushBack();
		return false;
	}
	/**
	 * Changes the encoding of the input stream. This is only effective if the
	 * input stream is of type {@link XMLStreamReader} and will do nothing
	 * otherwise. Nothing happens if the encoding is not recognized.
	 */
	protected final void setEncoding( String encoding )
	{
		if ( _reader instanceof XMLStreamReader )
			_reader = ( (XMLStreamReader) _reader ).changeEncoding( encoding );
	}
	/**
	 * Associates this parser with an error sink in another parser. When an entity
	 * is parsed, errors must still be directed to the document parser. This is
	 * done by specifying the document parser as the error sink for the entity
	 * parser. The {@link #error} methods of the document parser are then called
	 * for each error issued by the entity parser.
	 * <P>
	 * Be careful of circular references, they lead to premature stack death.
	 *
	 * @param errorSink The error sink
	 * @see ErrorSinkHandler
	 */
	public final void setErrorSink( ErrorSinkHandler errorSink )
	{
		_errorSink = errorSink;
	}
	/**
	 * Slices processing instruction text into target and instruction code.
	 * Called with the processing instruction text in {@link #_tokenText},
	 * returning the valid target name, and {@link #_tokenText} truncated to
	 * contain just the instruction code. If no valid target name is found,
	 * an empty name is returned.
	 *
	 * @return The target name
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final String slicePITokenText()
		throws ParseException
	{
		int        i;
		char    ch;
		String    name;

		if ( _tokenText.length() == 0 )
			return "";
		// Count how many characters constitute a valid target name. Valid target
		// name is an XML NAME token followed by '?>' or a whitespace.
		i = 0;
		ch = _tokenText.charAt( 0 );
		if ( isNamePart( ch, true ) )
		{
			i = 1;
			ch = _tokenText.charAt( i );
			while ( i < _tokenText.length() && isNamePart( ch, false ) )
			{
				++ i;
				ch = _tokenText.charAt( i );
			}
		}
		// If target is not only thing in processing instruction or followed by
		// a white space, issue an error.
		if ( i == 0 || ( i < _tokenText.length() && ! isSpace( ch ) ) )
			error( ERROR_WELL_FORMED, "Target name in processing instruction is invalid." );
		// Get target name. Skip all whitespaces following target name, and create
		// new node with that information.
// ! JDK 1.2 !
		// name = _tokenText.substring( 0, i );
// ! JDK 1.1 !
		name = _tokenText.toString( 0, i );
		while ( i < _tokenText.length() && isSpace( ch ) )
		{
			++ i;
			ch = _tokenText.charAt( i );
		}
// ! JDK 1.2 !
		_tokenText.delete( 0, i );
// ! JDK 1.1 !
//        _tokenText = StringUtil.substring( _tokenText, i );
		return name;
	}
}