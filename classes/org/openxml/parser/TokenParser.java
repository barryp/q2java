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


package org.openxml.parser;


import java.io.IOException;
import java.util.StringTokenizer;
import org.xml.sax.*;
import org.openxml.util.FastString;


/**
 * Implements layers 2 of the parser architecture, the handling of XML tokens
 * and entity declarations. These methods are a higher level than core token
 * reading (level 1, see {@link StreamParser}) but are not specific to
 * XML/HTML document content (level 3, see {@link ContentParser}).
 * <P>
 * This parser is created by a derived class. The {@link #init} method
 * must be called before the parser is used or re-used (the parser is
 * re-entrant).
 * <P>
 * {@link #readTokenMarkup} is the most useful method for reading a markup
 * token inside an XML or HTML content.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:07 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see Parser
 * @see StreamParser
 * @see FastString
 */
abstract class TokenParser
    extends StreamParser
{
    
    
    /**
     * Protected constructor only accessible from derived class.
     */
    protected TokenParser()
    {
    }

    
    /**
     * Initializes the parser to parse a new document. Must be called
     * before any parsing occurs. See {@link StreamParser#init}.
     *
     * @param input The input source to parse
     * @throws SAXException The input source cannot be used
     */
    protected void init( InputSource input )
        throws SAXException, IOException
    {
        super.init( input );
    }

    
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
     *  this method. A whitespace between the '&lt;' and tag name is reported as
     *  an error; an empty tag name will never be returned.
     * <BR>
     * <LI>{@link #TOKEN_CLOSE_TAG} returned for closing tag. Closing tag is '&lt;/'
     *  followed by valid tag name (returned as token text) and '&gt'. All text
     *  following the tag name until the terminating '&gt;' is ignored; a whitespace
     *  between the '&lt;' and tag name is reported as an error; an empty tag name
     *  will never be returned.
     * <BR>
     * <LI>{@link #TOKEN_COMMENT} returned for comment. Comment is terminated with
     *  '&lt;!--' and '--&gt;'. All text inbetween is consumed, and returned as
     *  token text.
     * <BR>
     * <LI>{@link #TOKEN_CDATA} returned for CDATA section. Section starts with
     *  '&lt;![CDATA[' and ends with ']]>'. All text inbetween is consumed and
     *  returned as token text.
     * <BR>
     * <LI>{@link #TOKEN_PI} returned for processing instruction. Processing
     *  instruction is terminated with '&lt;?' and '?&gt;'. All text inbetween is
     *  consumed, and returned as token text.
     * <BR>
     * <LI>{@link #TOKEN_DTD} returned for DTD declaration. DTD declaration starts
     *  with '&lt;!' immediately followed by a token name (returned as token text).
     *  All other declaration contents is not read by this method. A whitespace
     *  between the '&lt;!' and the token name is not allowed; an empty
     *  declaration name is never returned.
     * <BR>
     * <LI>{@link #TOKEN_SECTION} returned for DTD conditional section. Conditional
     *  section begins with '&lt;![' and is not a CDATA section. Only the '&lt;!['
     *  sequence is read and consumed by this method.
     * </UL>
     * If no markup is found, the token code {@link #TOKEN_TEXT} is returned, with
     * '&lt;' contained in {@link #_tokenText} and the input stream is not affected.
     * An error is reported.
     *
     * @return The markup token code of {@link #TOKEN_TEXT}
     * @throws SAXException A parsing error has been encountered
     */
    protected final int readTokenMarkup()
        throws SAXException
    {
        // Read character immediately following '<' and decide what to do next
        // based on that character. Empty the token text, so it can be used
        // to accumulate non-markup text.
        _tokenText.setLength( 0 );
        readChar();

        // Case of '</' is a closing tag. A closing tag ends with the closest
        // '>' or '<' (invalid, but still processed). Anything inbetween is
        // consumed. The first valid characters form the tag name, anything
        // else is ignored.
        if ( _curChar == '/' )
        {
            if ( isSpace( readChar() ) )
            {
                error( WELL_FORMED, message( "Parser050" ) );
                _tokenText.append( (char) _curChar );
                while ( isSpace( readChar() ) )
		{
                    _tokenText.append( (char) _curChar );
		    readChar();
		}
            }
            if ( readTokenNameCur() )
            {
                readChar();
                while ( _curChar != EOF && _curChar != '<' && _curChar != '>' )
                    readChar();
                if ( _curChar == '<' )
                {
                    pushBack();
                    error( WELL_FORMED, message( "Parser026" ) );
                }
		else
                if ( _curChar == EOF )
                    error( WELL_FORMED, message( "Parser025" ) );
                return TOKEN_CLOSE_TAG;
            }

	    pushBack();
            _tokenText.insert( 0, "</" );
            error( WELL_FORMED, format( "Parser049", "</" ) );
            return TOKEN_TEXT;
        }

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
                error( WELL_FORMED, message( "Parser022" ) );
            return TOKEN_PI;
        }

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
                    // Return the plain text '<' and issue a warning.
                    pushBack();
                    pushBack( '-' );
                    _tokenText.append( '<' );
                    error( WELL_FORMED, format( "Parser049", "<-" ) );
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
                            if ( isSpace( readChar() ) )
                            {
                                if ( isWarning() )
                                    warning( message( "Parser052" ) );
				skipSpace();
                            }
                            if ( _curChar == '>' )
                                return TOKEN_COMMENT;
                            else
                                pushBack();
                            _curChar = '-';
                        }
                        _tokenText.append( '-' );
                    }
                    else
                    {
                        _tokenText.append( (char) _curChar );
                        readChar();
                    }
                }
                if ( _curChar == EOF )
                    error( WELL_FORMED, message( "Parser023" ) );
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
                                pushBack();
                                _curChar = ']';
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
                        error( WELL_FORMED, message( "Parser024" ) );
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
            {
                
                if ( ! ( _curChar >= 'a' && _curChar <= 'z' ) &&
                     ! ( _curChar >= 'A' && _curChar <= 'Z' ) )
                {
                    pushBack();
                    _tokenText.append( "<!" );
                    error( WELL_FORMED, format( "Parser049", "<!" ) );
                    return TOKEN_TEXT;
                }
                // Not comment and not CDATA section, this is a DTD definition.
                // Read the DTD entity type (the uppercase name that comes after the
                // '<!') and return it.
                while ( ( _curChar >= 'a' && _curChar <= 'z' ) ||
                        ( _curChar >= 'A' && _curChar <= 'Z' ) )
                {
                    _tokenText.append( (char) _curChar );
                    readChar();
                }
                pushBack();
                return TOKEN_DTD;
            }

        }
        else

        // Nothing else identified, this is an opening tag. The first valid
        // characters form the tag name. Subsequent whitespaces are consumed.
        // The attribute or terminating '>' will be read some other time.
        if ( isSpace( _curChar ) )
        {
            error( WELL_FORMED, message( "Parser051" ) );
            _tokenText.append( (char) _curChar );
            while ( isSpace( readChar() ) )
                _tokenText.append( (char) _curChar );
        }
        if ( readTokenNameCur() )
        {
	    skipSpace();
            pushBack();
            return TOKEN_OPEN_TAG;
        }

        // If '<' not followed by valid tag name, return plain text.
        //   pushBack();                 //removed RM 01/14/99
        _tokenText.insert( 0, '<' );
        error( WELL_FORMED, format( "Parser049", "<" ) );
        return TOKEN_TEXT;
    }


    /**
     * Reads general entity reference token or character reference. Returns
     * the token code {@link #TOKEN_ENTITY_REF} and the entity name in {@link
     * #_tokenText}. The preceding '&amp;' has been consumed prior to calling
     * this method, and the trailing ';' is consumed by this method. No valid
     * character is held in {@link #_curChar} on entry or exit.
     * <P>
     * If no valid entity name is found, the token code {@link #TOKEN_TEXT} is
     * returned, with '&amp;' contained in {@link #_tokenText} and the input
     * stream is not affected.
     * <P>
     * A '#' sign indicates a character reference (either decimal or hexadecimal)
     * which is read and stored in {@link #_tokenText}, and the token code
     * {@link #TOKEN_TEXT} is returned. If the character reference value is
     * invalid, the token code {@link #TOKEN_TEXT} is returned, with the
     * invalid part contained in {@link #_tokenText} and the input stream is
     * not affected.
     * <P>
     * If the entity reference or character reference is not terminated with
     * a ';', a well-formed error is issued, and the name is returned as
     * textual string instead of an entity reference.
     *
     * @return Token code either {@link #TOKEN_ENTITY_REF} or {@link #TOKEN_TEXT}
     * @throws SAXException A parsing error has been encountered
     */
    protected final int readTokenEntity()
        throws SAXException
    {
	return readTokenEntity( false );
    }

    protected final int readTokenEntity( boolean ignoreError )
        throws SAXException
    {
        int code;
        int x;

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
                        error( WELL_FORMED, message( "Parser027" ) );
                    }
                    _tokenText.append( (char) code );
                    return TOKEN_TEXT;
                }

                // '&#x' that is not followed by digits: '&#x' is returned in
                // _tokenText and an error is reported.
                else
                {
                    pushBack();
                    _tokenText.append( "&#" ).append( (char) x );
                    error( WELL_FORMED, format( "Parser054", "&#X" ) );
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
		    error( WELL_FORMED, message( "Parser027" ) );
                }
                _tokenText.append( (char) code );
                return TOKEN_TEXT;
            }

            // '&#' that is not followed by digits: '&#' is returned in
            // _tokenText and an error is reported.
            else
            {
                pushBack();
                _tokenText.append( "&#" );
                error( WELL_FORMED, format( "Parser054", "&#" ) );
                return TOKEN_TEXT;
            }
        }

        // Not a character reference, than probably an entity reference.
        // Get the entity name. If name is missing, return '&' as plain text.
        // The terminating ';' is expected, but not required to terminate this
        // entity reference.
        if ( ! readTokenNameCur() )
        {
            _tokenText.append( '&' );
	    if ( ignoreError )
		warning( format( "Parser054", "&" ) );
	    else
		error( WELL_FORMED, format( "Parser054", "&" ) );
            return TOKEN_TEXT;
        }
        readChar();
        if ( _curChar != ';' )
        {
            pushBack();
	    if ( ignoreError )
		warning( format( "Parser028", _tokenText.toString() ) );
	    else
		error( WELL_FORMED, format( "Parser028", _tokenText.toString() ) );
            _tokenText.insert( 0, '&' );
            return TOKEN_TEXT;
        }

        // Return tokan for entity reference and it's name.
        return TOKEN_ENTITY_REF;
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
     * @throws SAXException A parsing error has been encountered
     */
    protected final boolean parseDocumentDecl( boolean XMLDecl )
        throws SAXException
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
            // At the end of the process the next token that is not part of
            // the version identifier is contained in the variable text.
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
                    error( WELL_FORMED, message( "Parser016" ) );
            }

        }

        // XML document is missing 'version', while external entity is missing
        // 'encoding': issue an error. Only version 1.0 XML supported by this DOM.
        if ( XMLDecl && version == null )
            error( WELL_FORMED, message( "Parser017" ) );
        else
        if ( ! XMLDecl && encoding == null )
            error( WELL_FORMED, message( "Parser018" ) );
        if ( version != null && ! version.equals( "\'1.0\'" ) && ! version.equals( "\"1.0\"" ) )
            error( WELL_FORMED, message( "Parser019" ) );

        // If encoding specified, change the encoding of the input stream.
        if ( encoding != null )
        {
            quote = encoding.charAt( 0 );
            if ( encoding.length() < 3 || ( quote != '\'' && quote != '\"' ) ||
                 encoding.charAt( encoding.length() -1 ) != quote )
                error( WELL_FORMED, message( "Parser020" ) );
            else
            {
                encoding = encoding.substring( 1, encoding.length() - 1 );
                setEncoding( encoding );
            }
        }

        // Return standalone flag only valid for XML document declaration.
        return standalone;
    }


    /**
     * Slices processing instruction text into target and instruction code.
     * Called with the processing instruction text in {@link #_tokenText},
     * returning the valid target name, and {@link #_tokenText} truncated to
     * contain just the instruction code. If no valid target name is found,
     * an empty name amd empty instruction are returned.
     *
     * {@link #_curChar} is undetermined on entry and exit and is used
     * inside function.
     *
     * @return The target name
     * @throws SAXException A parsing error has been encountered
     */
    protected final String slicePITokenText()
        throws SAXException
    {
        int     i;
        String  name;
	char    ch;

        if ( _tokenText.length() == 0 )
            return "";
        // Count how many characters constitute a valid target name. Valid target
        // name is an XML NAME token followed by '?>' or a whitespace.
        i = 0;
        ch = _tokenText.charAt( 0 );
        if ( isNamePartFirst( ch ) )
        {
            i = 1;
            ch = _tokenText.charAt( i );
            while ( i < _tokenText.length() && isNamePart( ch ) )
            {
                ++ i;
                ch = _tokenText.charAt( i );
            }
        }
        // If target is an empty name, or not followed by a space,
        // issue an error.
        // 
	name = _tokenText.toString( 0, i );
        if ( i == 0 || ( i < _tokenText.length() && ! isSpace( ch ) ) )
        {
            error( WELL_FORMED, message( "Parser021" ) );
            _tokenText.setLength( 0 );
            return name;
        }
        
        // Skip all whitespaces following target name/
        while ( i < _tokenText.length() && isSpace( ch ) )
        {
            ++ i;
            ch = _tokenText.charAt( i );
        }
        _tokenText.delete( 0, i );
        return name;
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
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     * @throws IOException An I/O exception has been encountered when reading
     *  from the input stream
     */
    protected final int readTokenPERef()
        throws SAXException, IOException
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
            error( WELL_FORMED, "Parameter entity reference terminated permaturely." );
        }
        // Return tokan for entity reference and it's name.
        return TOKEN_PE_REF;
    }


}
