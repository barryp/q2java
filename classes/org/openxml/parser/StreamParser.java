/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.openxml.org/license/
 *
 * THIS SOFTWARE AND DOCUMENTATION IS PROVIDED ON AN "AS IS" BASIS
 * WITHOUT WARRANTY OF ANY KIND EITHER EXPRESSED OR IMPLIED,
 * INCLUDING AND WITHOUT LIMITATION, WARRANTIES THAT THE SOFTWARE
 * AND DOCUMENTATION IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGING. SEE THE LICENSE FOR THE
 * SPECIFIC LANGUAGE GOVERNING RIGHTS AND LIMITATIONS UNDER THE
 * LICENSE.
 *
 * The Initial Developer of this code under the License is
 * OpenXML.org. Portions created by OpenXML.org and/or Assaf Arkin
 * are Copyright (C) 1998, 1999 OpenXML.org. All Rights Reserved.
 */


/**
 * Dec 26, 1999
 * + Added readTokenNameCur() and isNamePartFirst().
 */


package org.openxml.parser;


import java.io.*;
import java.net.*;
import java.util.Locale;
import org.xml.sax.*;
import org.openxml.util.Resources;
import org.openxml.util.FastString;



/**
 * Implements layers 0 and 1 of the parser architecture, reading
 * characters and tokens from the input stream. In addition implements
 * support for reading the stream location and for reporting errors.
 * <P>
 * This parser is created by a derived class. The {@link #init} method
 * must be called before the parser is used or re-used (the parser is
 * re-entrant).
 * <P>
 * The input stream is consumed through the {@link #readChar} and {@link
 * #pushBack} methods that work on the value of the state variable {@link
 * #_curChar}. This single variable is constantly reused by all the higher
 * level methods to denote the last character read, so be careful in how
 * you use it.
 * <P>
 * Tokens are read by a variaty of low level methods and stored in the
 * state variable {@link #_tokenText}. Once again, this signle variable
 * is constantly reused by all the higher level methods to contain the
 * last read token, so be careful in how you use it. This variable is of
 * type {@link FastString}, an optimized version of StringBuffer that can
 * be reused perpetually by calling {@link FastString#setLength} with zero.
 * <P>
 * Generally methods should document the expected states of {@link #_curChar}
 * and {@link #_tokenText} on entry and on exit and whether they change them.
 * To make the parser efficient, some methods will leave either variable with
 * an undertermined value, others will levae these variables with the next
 * value to be processed. It is important to pay attention to details.
 * <P>
 * The {@link #Locator} interface is implemented to return indication of
 * the parser's location in the input stream (identifier, line number, etc).
 * <P>
 * All the error reporting methods are implemented at this layer. Errors
 * are generated from a resource file (see {@link Resources} where the
 * actual error message is held. The error message is selected by some
 * generic name, either by calling ({@link #message}) or by formatting 
 * argument values directly into the message ({@link #format}).
 * <P>
 * Errors are generally reported at one of four levels:
 * <UL>
 * <LI>Warnings rarely cause the parser to stop and are reported through
 *  {@link #warning}
 * <LI>Validity  errors that are not mere warnings might cause the
 *  parser to stop and are reported through {@link #error} with the
 *  level {@link #VALIDITY}
 * <LI>Well formed errors might cause the parser to stop and are reported
 *  through {@link #error} with the level {@link #WELL_FORMED}
 * <LI>Fatal errors always cause the parser to stop and are reported through
 *  {@link #fatalError}
 * </UL>
 * An error handler can be associated with this parser. If the error handler
 * is {@link ErrorReport}, the extended error features will be used. If no
 * error handler is used, warnings are ignored, errors and fatal errors
 * will throw an exception and stop the parser.
*
 *
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:07 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see Parser
 * @see ErrorReport
 * @see Resources
 * @see FastString
 */
abstract class StreamParser
    implements Locator, EntityResolver
{
    
    
    /**
     * Indicates that end of file (or the input stream) has been reached and no
     * more character are availble. This character is invalid. It is legal to
     * push is back, though.
     */
    protected static final int      EOF = -1;


    /**
     * Line feed. Character code 0x0A.
     */
    protected static final char     LF = (char) 0x0A;


    /**
     * Carriage return. Character code 0x0D.
     */
    protected static final char     CR = (char) 0x0D;


    /**
     * Space. Character code 0x20.
     */
    protected static final char     SPACE = (char) 0x20;


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
     * Error level used for reporting well formed errors.
     */
    protected static final int  WELL_FORMED = ErrorReport.WELL_FORMED;


    /**
     * Error level used for reporting validity errors.
     */
    protected static final int  VALIDITY = ErrorReport.VALIDITY;

    
    /**
     * Holds the last character read by {@link #readChar}, or the next character
     * to be pushed back (see {@link #pushBack}). Set to {@link #EOF} if end
     * of input stream has been reached.
     * <P>
     * This is a state variable. Methods that use it directly or indirectly
     * must be alert to its present value and usage. General value on
     * entry/exit is specified in the method comment.
     */
    protected int               _curChar;


    /**
     * Holds the contents of the last token read by one of the token reading
     * methods. Set to {@link #TOKEN_EOF} if end of input stream has been reached.
     * Many methods read, modify and possibly return values in {@link #_tokenText},
     * so its value should not be assumed to remain constant between method calls.
     * <P>
     * A {@link FastString} is allocated and constantly reused by resetting
     * its length to zero. In some instances, it is replaced with an alternative
     * {@link FastString} object. IT IS IMPORTANT that no other variable will
     * reference this string.
     * <P>
     * This is a state variable. Methods that use it directly or indirectly
     * must be alert to its present state and usage and must reset it as
     * needed. General value on entry/exit is specified in the method comment.
     */
    protected FastString        _tokenText = new FastString();

    
    /**
     * Push back buffer used by {@link #pushBack} and {@link #readChar}.
     *
     * @see #pushBack
     */
    private char[]              _pushBackBuffer = new char[ 64 ];


    /**
     * Push back buffer index used by {@link #pushBack} and {@link #readChar}.
     * This is the index of the next character to place in the push back buffer,
     * or one after the  first character to read from the buffer.
     *
     * @see #pushBack
     */
    private int                 _pushBackIndex;


    /**
     * The stream reader from which contents is obtained. The reader might be,
     * but is not guaranteed to be {@link XMLStreamReader}.
     */
    private Reader              _reader;
    
    
    /**
     * The current line number in the input stream (zero based).
     */
    private int                 _lineNumber;
    
    
    /**
     * The position inside the current line number in the input stream
     * (zero based).
     */
    private int                 _columnNumber;


    /**
     * Created by the constructor and identifies this document's
     * input source. This object is available exclusively to this
     * parser.
     */
    private InputSource         _inputSource;

    
    /**
     * Holds the last exception issued (whether stored or thrown).
     */
    private SAXParseException   _lastException;


    /**
     * Reference to external error handler. Used to report errors based
     * on the SAX error handler, or {@link ErrorReport}. Can be null and
     * can change mid-parsing.
     */
    private ErrorHandler        _errorHandler;




    /**
     * Protected constructor only accessible from derived class.
     */
    protected StreamParser()
    {
    }
    
    
    /**
     * Initializes the parser to parse a new document. Must be called
     * before any parsing occurs and must provide input stream or reader.
     * <P>
     * The input source is expected to provide the public and system
     * identifiers of the document for the purpose of error reporting and
     * relative external entity resolving. No checking is done on their
     * validity.
     * <P>
     * The input source must specify either an input stream or a reader.
     * If neither is specified, an exception is thrown. If a reader is
     * specified, it is used for consuming the source document. If an
     * input stream is specified, it is preferred to the reader and the
     * specified encoding is used to convert it. If the specified
     * encoding is not supported, an exception is thrown. If no encoding
     * is specified, the default UTF-8 is used.
     * <P>
     * Note, the input source is expected to be available to the
     * parser for the entire duration of parsing the document.
     *
     * @param input The input source to parse
     * @throws SAXException The input source cannot be used
     */
    protected void init( InputSource input )
        throws SAXException, IOException
    {
        InputStream is;
        
        if ( input == null )
            throw new NullPointerException( format( "Error001", "input" ) );
        is = input.getByteStream();
        if ( is != null ) {
            try {
                if ( input.getEncoding() == null )
                    _reader = new InputStreamReader( new BufferedInputStream( is ) );
                else
                    _reader = new InputStreamReader( new BufferedInputStream( is ), input.getEncoding() );
                input.setCharacterStream( _reader );
            } catch ( UnsupportedEncodingException except ) {
                fatalError( new SAXException( format( "Parser014", input.getEncoding() ) ) );
            }
        } else if ( input.getCharacterStream() != null )
            _reader = new BufferedReader( input.getCharacterStream() );
        else {
            input = resolveEntity( input.getPublicId(), input.getSystemId() );
            if ( input == null  )
                fatalError( new SAXException( message( "Parser013" ) ) );
            else {
                init( input );
                return;
            }
        }
        
        _inputSource = input;
        _tokenText.setLength( 0 );
        _pushBackIndex = 0;
        _lineNumber = 0;
        _columnNumber = 0;
        _lastException = null;
    }


    
    
    /***********************************************************************
     Layer 1: reading tokens from input stream
     ***********************************************************************/


    /**
     * Reads the quoted identifier token. Quotes can be either single or
     * double, but both opening and closing quotes must be identical.
     * Everything inbetween the quotes is stored in {@link #_tokenText}.
     * Returns true if a quoted value was found (i.e. opening quote
     * followed on the input stream).
     *
     * @return True if quoted value was found
     * @throws SAXException A low-level error has been encountered
     */
    protected final boolean readTokenQuoted()
        throws SAXException
    {
        int             quote;
        
        _tokenText.setLength( 0 );
        quote = readChar();
        // Quote can be either single or double, opening and closing quotes
        // must be identical, everything else is unacceptable.
        if ( quote == '\'' || quote == '\"' ) {
            while ( readChar() != quote && _curChar != EOF )
                _tokenText.append( (char) _curChar );
            if ( _curChar == EOF )
                error( WELL_FORMED, message( "Parser012" ) );
            return true;
        }
        pushBack( quote );
        return false;
    }


    /**
     * Reads a valid token name and places it in {@link #_tokenText}.
     * If a valid name can be read, it is placed in {@link #_tokenText} and
     * true is returned, otherwise false is returned and the input stream
     * is not affected.
     * <P>
     * A valid token name is defined as consisting of any letter, underscore
     * or colon, followed by zero or more letters and digits, underscores,
     * hyphens, colons and periods. Unlike other languages, letters and
     * digits can be specified in all Unicode supported languages.
     * <P>
     * {@link #_curChar} does not contain a valid value on either entry or
     * exit from this method.
     *
     * @return True if valid name of at least one character found
     * @throws SAXException A low-level error has been encountered
     */
    protected final boolean readTokenName()
        throws SAXException
    {
        _tokenText.setLength( 0 );
        if ( isNamePartFirst( readChar() ) ) {
            _tokenText.append( (char) _curChar );
            while ( isNamePart( readChar() ) )
                _tokenText.append( (char) _curChar );
            pushBack();
            return true;
        }
        pushBack();
        return false;
    }


    /**
     * Reads a valid token name and places it in {@link #_tokenText}.
     * If a valid name can be read, it is placed in {@link #_tokenText} and
     * true is returned, otherwise false is returned and the input stream
     * is not affected.
     * <P>
     * A valid token name is defined as consisting of any letter, underscore
     * or colon, followed by zero or more letters and digits, underscores,
     * hyphens, colons and periods. Unlike other languages, letters and
     * digits can be specified in all Unicode supported languages.
     * <P>
     * {@link #_curChar) contains the last character read (first token
     * character) on entry, but does not contain a valid value on exit.
     *
     * @return True if valid name of at least one character found
     * @throws SAXException A low-level error has been encountered
     */
    protected final boolean readTokenNameCur()
        throws SAXException
    {
        _tokenText.setLength( 0 );
        if ( isNamePartFirst( _curChar ) ) {
            _tokenText.append( (char) _curChar );
            while ( isNamePart( readChar() ) )
                _tokenText.append( (char) _curChar );
            pushBack();
            return true;
        }
        pushBack();
        return false;
    }


    /**
     * Returns true if the specified name can be read and consumes it all.
     * Attempts to read the name in its entirety. If the name is read,
     * it is consumed from the input stream and the method returns true.
     * If the name is not read, the input stream is not affected and the
     * method return false.
     *
     * @return True if the name was read and consumed in its entirety
     * @throws SAXException A low-level error has been encountered
     */
    protected final boolean canReadName( String name )
        throws SAXException
    {
        int    i;

        for ( i = 0 ; i < name.length() ; ++i )  {
            if ( readChar() != name.charAt( i ) ) {
                pushBack( _curChar );
                while ( i > 0 ) {
                    -- i;
                    pushBack( name.charAt( i ) );
                }
                return false;
            }
        }
        return true;
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

        for ( i = 0 ; i < _tokenText.length() ; ++i ) {
            ch = _tokenText.charAt( i );
            if ( ! ( ch == SPACE || ch == LF || ch == CR || ch == 0x09 ) )
                return false;
        }
        return true;
    }


    /**
     * Returns true if character is part of a valid name. The <TT>first</TT>
     * flag indicates whether character is expected to be the first or any
     * other in the name.
     * <P>
     * Valid names are defined as consisting of any letter, underscore or
     * colon, followed by zero or more letters and digits, underscores,
     * hyphens, colons and periods. Unlike other languages, letters and
     * digits can be specified in all Unicode supported languages.
     * <P>
     * Test performed on {@link #_curChar}.
     *
     * @param first True if first letter in the name
     * @return True if character is valid in name context
     */
    protected final boolean isNamePart( int ch )
    {
        return ( Character.isLetterOrDigit( (char) ch ) ||
                 ch == '_' || ch == ':' || ch == '-' || ch == '.' );
    }

    protected final boolean isNamePartFirst( int ch )
    {
        return ( Character.isLetter( (char) ch ) || ch == '_' || ch == ':' );
    }


    /**
     * Returns true if character is a whitespace. Space (0x20), tab (0x09),
     * line feed (0x0A), carriage return (0x0D) are defined as whitespaces.
     * Test performed on {@link #_curChar}.
     *
     * @param ch The character to check
     * @return True if character is a whitespace
     */
    protected final boolean isSpace( int ch )
    {
        return ( ch == SPACE || ch == LF || ch == CR || ch == 0x09 );
    }


    protected final void skipSpace()
        throws SAXException
    {
        int ch;
        
        ch = readChar();
        while ( ch == SPACE || ch == LF || ch == CR || ch == 0x09 )
            ch = readChar();
    }
    
    
    /**
     * Pushes back the token contained in {@link #_tokenText}. The value
     * of {@link #_tokenText} is identical on entry and exit. The value
     * of {@link #_curChar} is unaffected.
     */
    protected final void pushBackToken()
    {
        int i;
        
        i = _tokenText.length();
        while ( i-- > 0 )
            pushBack( _tokenText.charAt( i ) );
    }

    


    /***********************************************************************
     Layer 0: reading characters from input stream
     ***********************************************************************/


    /**
     * Reads and returns a single character from the input stream.
     * If characters were pushed back they are returned in the same order
     * they were pushed (LIFO). If end of input stream has been reached,
     * {@link #EOF} is returned. The returned character is also available
     * in the {@link #_curChar} variable.
     * <P>
     * Line breaks (LF, CR and CR+LF) are returned as a single line feed
     * (0x0A) character.
     *
     * @return A single character read from the input stream, also available in
     *  {@link #_curChar}
     * @throws SAXException An I/O exception has been encountered when reading
     *  from the input stream
     */
    protected final int readChar()
        throws SAXException
    {
        // If character available in push back buffer, read last character
        // stored in push back buffer and return it.
        if ( _pushBackIndex > 0 )
            _curChar = _pushBackBuffer[ -- _pushBackIndex ];
        else {
            // Read a single character from the input stream and return it.
            // LF, CR and CR+LF are converted to LF.
            try {
                // Read a single character through a buffer.
                // The buffer is maintained here to improve performance.
                if ( _bufferPos == _bufferSize ) {
                    _bufferPos = 0;
                    _bufferSize = _reader.read( _buffer );
                    if ( _bufferSize <= 0 ) {
                        _bufferSize = 0;
                        _curChar =  EOF;
                    } else {
                        _curChar = _buffer[ _bufferPos ++ ];
                        ++ _columnNumber;
                    }
                } else {
                    _curChar = _buffer[ _bufferPos ++ ];
                    ++ _columnNumber;
                }
                
                // If found either LF, CR or CR + LF, return a single LF character.
                if ( _curChar == LF || _curChar == CR ) {
                    _columnNumber = 0;
                    ++_lineNumber;
                    
                    if ( _curChar == CR ) {
                        
                        if ( _bufferPos == _bufferSize ) {
                            _bufferPos = 0;
                            _bufferSize = _reader.read( _buffer );
                            if ( _bufferSize <= 0 ) {
                                _bufferSize = 0;
                                _curChar =  EOF;
                            } else
                                _curChar = _buffer[ _bufferPos ++ ];
                        } else
                            _curChar = _buffer[ _bufferPos ++ ];
                        
                        if ( _curChar != LF && _curChar != EOF ) {
                            ++ _columnNumber;
                            pushBack( _curChar );
                        }
                        _curChar = LF;
                    }
                }
            } catch ( IOException except ) {
                // If an IO exception occurs, throw a fatal exception
                // based on that. This saves us the need to handle
                // IO exceptions higher up in the parser.
                fatalError( except );
            }
        }
        return _curChar;
    }
    
    
    private char[]  _buffer = new char[ 8192 ];
    int             _bufferSize;
    int             _bufferPos;
    

    /**
     * Push back the last character read into {@link #_curChar}. The pushed
     * back character will be returned when {@link #readChar} is called next.
     * Any number of characters can be pushed back. The push back buffer is
     * a LIFO stack, so text should be pushed back in reverse order. It is
     * not an error to push back the value {@link #EOF}.
     */
    protected final void pushBack()
    {
        pushBack( _curChar );
    }


    /**
     * Push back a single character. The pushed back character will be returned
     * when {@link #readChar} is called next. Any number of characters can be
     * pushed back. The push back buffer is a LIFO stack, so text should be
     * pushed back in reverse order. It is not an error to push back the value
     * {@link #EOF}.
     *
     * @param ch The character to push back
     */
    protected final void pushBack( int ch )
    {
        if ( ch != EOF ) {
            if ( _pushBackIndex == _pushBackBuffer.length ) {
                char[]    newBuffer;
                
                newBuffer = new char[ _pushBackIndex * 2 ];
                System.arraycopy( newBuffer, _pushBackIndex, _pushBackBuffer, 0, _pushBackIndex );
                _pushBackBuffer = newBuffer;
            }
            _pushBackBuffer[ _pushBackIndex ] = (char) ch;
            ++ _pushBackIndex;
        }
    }


    /**
     * Changes the encoding of the input stream. This is only effective if
     * an input stream was specified and not a reader.
     */
    protected final void setEncoding( String encoding )
        throws SAXException
    {
        if ( _inputSource.getByteStream() != null ) {
            if ( ! encoding.equalsIgnoreCase( _inputSource.getEncoding() ) ) {
                _inputSource.setEncoding( encoding );
                try {
                        _reader = new InputStreamReader( new BufferedInputStream( _inputSource.getByteStream() ), encoding );
                } catch ( UnsupportedEncodingException except ) {
                    error( WELL_FORMED, format( "Parser014", encoding ) );
                }
            }
        }
        else
            if ( isWarning() && _inputSource.getEncoding() != null &&
                 ! encoding.equalsIgnoreCase( _inputSource.getEncoding() ) )
                warning( format( "Parser015", _inputSource.getEncoding(), encoding ) );                
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


    /**
     * Closes the input stream. Any read operation following this
     * closing will result in an I/O exception.
     */
    protected final void close()
    {
        try {
            _reader.close();
            _reader = null;
        } catch ( IOException except ) { }
    }


    /**
     * Returns true if the document has been fully parsed and the parsed has
     * been closed. Parsing operations should be rejected when the parser is
     * closed. The parser is closed by calling the {@link #close} method.
     *
     * @return True if parser has been closed
     */
    protected final boolean isClosed()
    {
        return _reader != null;
    }

 
    
    
    /***********************************************************************
     Source location access methods (see Locator interface)
     ***********************************************************************/


    public final int getLineNumber()
    {
        return _lineNumber + 1;
    }


    public final int getColumnNumber()
    {
        return _columnNumber + 1;
    }
    
    
    public final String getSystemId()
    {
        return _inputSource.getSystemId();
    }

    
    public final String getPublicId()
    {
        return _inputSource.getPublicId();
    }
    
    
    public InputSource resolveEntity( String publicId, String systemId )
        throws IOException, SAXException
    {
        URL             url;
        URLConnection   conn;
        InputSource     input;
        
        if ( systemId == null )
            return null;

        url = new URL( systemId );
        conn = url.openConnection();
        input = new InputSource();
        input.setEncoding( conn.getContentEncoding() );
        input.setByteStream( conn.getInputStream() );
        input.setPublicId( publicId );
        input.setSystemId( systemId );
        return input;
    }

    
    

    /***********************************************************************
     Error reporting and logging, and source location methods
     ***********************************************************************/


    /**
     * Returns the last error issued by the parser. This might be any
     * error or fatal exception that was thrown or not thrown by the
     * parser. Warnings are not returned.
     * 
     * @return The last error issued, or null
     */
    public final SAXException getLastException()
    {
        return _lastException;
    }


    /**
     * Associates this parser with an error handler. By default the parser
     * creates and users an {@link ErrorReport} for reporting errors.
     * Some applications may wish to provide their own error handler,
     * by calling this method. If the handler is set to null, all errors
     * encountered in the code will throw an exception and stop the parser.
     *
     * @param handler The new error handler to use, or null
     * @see ErrorHandler
     * @see ErrorReport
     */
    public synchronized final void setErrorHandler( ErrorHandler handler )
    {
        _errorHandler = handler;
    }
    

    /**
     * Returns the error handler associated with this parser (if any).
     * By default this is an {@link ErrorReport}.
     *
     * @return The error handler of this parser, or null
     * @see ErrorHandler
     */
    public synchronized final ErrorHandler getErrorHandler()
    {
        return _errorHandler;
    }
    
    
    /**
     * Set the locale for error messages. If a resource file for this
     * locale exists, messages will be formatted in this locale.
     * 
     * @param locale The locale to use, null for the system default
     */
    public final void setLocale( Locale locale )
    {
    }

    
    /**
     * Returns true if warning should be issued. Use this method to
     * avoid creating warning messages when there is no error handler,
     * or the error handler is not interested in them.
     * 
     * @return True if error handler interested in warnings
     */
    protected final boolean isWarning()
    {
        return ! ( _errorHandler == null ||
                   ( _errorHandler instanceof ErrorReport &&
                     ! ( (ErrorReport) _errorHandler ).isReporting( ErrorReport.WARNING ) ) );
    }

    
    /**
     * Reports a warning. A warning is not an error and is not supposed
     * to be thrown, but the SAX error handler can throw an exception.
     * 
     * @param message The warning message
     * @throws SAXException The error handler might respond by throwing
     *  an exception that will stop the parser
     */
    protected final void warning( String message )
        throws SAXException
    {
        SAXParseException   except;
        
        if ( _errorHandler != null ) {
            if ( _errorHandler instanceof ErrorReport &&
                 ! ( (ErrorReport) _errorHandler ).isReporting( ErrorReport.WARNING ) )
                return;
            except = new SAXParseException( message, this );
            _errorHandler.warning( except );
        }
    }
    
    
    /**
     * Reports a parser error. Errors fall under a number of severity
     * levels. Depending on the level the error handler may or may not
     * throw an exception and stop the parser. Error levels are not
     * supported by SAX.
     * 
     * @param errorLevel The error level
     * @param message The error message
     * @throws SAXException The error handler might respond by throwing
     *  an exception that will stop the parser
     */
    protected final void error( int errorLevel, String message )
        throws SAXException
    {
        _lastException = new SAXParseException( message, this );
        if ( _errorHandler == null )
            throw _lastException;
        else
            if ( _errorHandler instanceof ErrorReport )
                ( (ErrorReport) _errorHandler ).reportError( errorLevel, _lastException );
            else
                _errorHandler.error( _lastException );
    }
    
    
    /**
     * Reports a fatal error. A fatal error will always throw an
     * exception and stop the parser. The exception is a SAX exception
     * which encapsulates the original exception.
     * 
     * @param except The fatal exception
     * @throws SAXException The error handler will respond by throwing
     *  an exception that will stop the parser
     */
    protected final void fatalError( Exception except )
        throws SAXException
    {
        if ( except instanceof SAXParseException )
            _lastException = (SAXParseException) except;
        else if ( except instanceof SAXException )
            _lastException = new SAXParseException( except.getMessage(), this );
        else
            _lastException = new SAXParseException( null, this, except );
        if ( _errorHandler != null )
            _errorHandler.fatalError( _lastException );
        throw _lastException;
    }
    
    
    /**
     * Return a message from the resource file based on the selected
     * locale. This is a convenience method hiding {@link
     * Resources#message} with a locale known to the parser.
     * 
     * @param message The message identifier
     * @return The local-specific message text
     */
    protected final String message( String message )
    {
        return Resources.message( message );
    }


    /**
     * Format a message from the resource file based on the selected
     * locale and given argument. This is a convenience method
     * hiding {@link Resources#format} with a locale known to the
     * parser.
     * 
     * @param message The message identifier
     * @param arg1 The first argument
     * @return The local-specific message text
     */
    protected final String format( String message, Object arg1 )
    {
        return Resources.format( message, arg1 );
    }
    

    /**
     * Format a message from the resource file based on the selected
     * locale and given arguments. This is a convenience method
     * hiding {@link Resources#format} with a locale known to the
     * parser.
     * 
     * @param message The message identifier
     * @param arg1 The first argument
     * @param arg2 The second argument
     * @return The local-specific message text
     */
    protected final String format( String message, Object arg1, Object arg2 )
    {
        return Resources.format( message, arg1, arg2 );
    }
    
    
    /**
     * Format a message from the resource file based on the selected
     * locale and given arguments. This is a convenience method
     * hiding {@link Resources#format} with a locale known to the
     * parser.
     * 
     * @param message The message identifier
     * @param arg1 The first argument
     * @param arg2 The second argument
     * @param arg3 The third argument
     * @return The local-specific message text
     */
    protected final String format( String message, Object arg1, Object arg2, Object arg3 )
    {
        return Resources.format( message, arg1, arg2, arg3 );
    }
    

    
    
}
