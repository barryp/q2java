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
import org.xml.sax.*;


public final class HTMLSAXParser
    extends ContentParser
    implements Parser
{
    
    
    public HTMLSAXParser()
    {
	setErrorHandler( new ErrorReportImpl( ErrorReport.STOP_AT_NO_ERROR,
					      ErrorReport.REPORT_ALL_ERRORS ) );
    }
    
    
    public HTMLSAXParser( ErrorHandler errorHandler )
    {
	setErrorHandler( errorHandler );
    }


    /**
     * Initializes the parser to parse a new document. Must be called
     * before any parsing occurs. See {@link StreamParser#init}.
     *
     * @param input The input source to parse
     * @throws SAXException The input source cannot be used
     * @throws IOException An error occurs accessing the input stream
     */
    protected void init( InputSource input )
        throws SAXException, IOException
    {
        super.init( input );
	if ( getErrorHandler() == null )
	    setErrorHandler( new ErrorReportImpl() );
    }

    
    public synchronized void parse( InputSource input )
        throws SAXException, IOException
    {
        int             token;
        
        init( input );
        try
        {
            // Start the document, consume all the white space appearing
            // at the top of the document. (Illegal, but happens)
            token = readTokenContent();
            while ( token == TOKEN_TEXT && isTokenSpace() )
                token = readTokenContent();

            // If document type definition found, process it, otherwise,
            // resume with document content processing. '<!DOCTYPE' is
            // followed by top-level element name, and external DTD
            // identifiers.
            if ( token == TOKEN_DTD )
            {
                if ( _tokenText.equals( "DOCTYPE" ) )
                {
                    parseDTDSubset();
                    token = readTokenContent();
                }
                else
                if ( _tokenText.equalsUpper( "DOCTYPE" ) )
                {
                    if ( isWarning() )
                        warning( message( "Parser034" ) );
                    parseDTDSubset();
                    token = readTokenContent();
                }
                else
                {
                    error( WELL_FORMED, format( "Parser035", _tokenText.toString() ) );
                    while ( readChar() != EOF && _curChar != '>' )
                        ;
                }
            }

            // Consume all the white space appearing after the DOCTYPE.
            while ( token == TOKEN_TEXT && isTokenSpace() )
                token = readTokenContent();

	    // Start parsing the document.
            _documentHandler.startDocument();

            while ( parseNextNode( token ) )
                token = readTokenContent();
            while ( getElementState() != null )
            {
		_documentHandler.endElement( getElementState().getTagName() );
                if ( ! HTMLdtd.isOptionalClosing( getElementState().getTagName() ) )
                    error( WELL_FORMED, format( "Parser046", getElementState().getTagName(),
						new Integer( getElementState().getStartLine() ) ) );
                leaveElementState();
            }
	    _documentHandler.endDocument();
        }

        // Catch parsing exception and rethrow them. This catch clause is only
        // necessary because of the second, more general catch clause that
        // catches runtime exceptions.
        catch ( SAXException except )
        {
            throw except;
        }
        catch ( Exception except )
        {
            fatalError( except );
        }
        finally
        {
            // Close the input stream.
            close();
        }
    }
    

    
    /**
     * Parser the external DTD subset.
     */
    protected final void parseDTDSubset()
        throws SAXException
    {
        String  publicId = null;
        String  systemId = null;
/*
        Source  source;
        Holder  holder;
*/
        // Consume white spaces up to the root element name. If not found,
        // this DTD is invalid.
        while ( isSpace( readChar() ) )
            ;
        pushBack();
        if ( readTokenName() )
        {
            // If root element is PUBLIC or SYSTEM, this is clearly a mistake,
            // so it is reported and the token is pushed back to be re-read.
            // If the root element is not HTML (even if it's html), this is
            // an error and reported as such.
            if ( _tokenText.equalsUpper( "PUBLIC" ) || _tokenText.equalsUpper( "SYSTEM" ) )
            {
                pushBackToken();
                error( WELL_FORMED, message( "Parser041" ) );
            }
            else
            if ( ! _tokenText.equalsUpper( "HTML" ) )
                error( WELL_FORMED, message( "Parser036" ) );

            // Read the SYSTEM identifier or the PUBLIC and system identifer.
            // The identifiers much be quoted, if they are not they are still
            // read, but an error is reported.
            while ( isSpace( readChar() ) )
                ;
            pushBack();
            readTokenName();
            if ( _tokenText.equalsUpper( "SYSTEM" ) )
            {
                while ( isSpace( readChar() ) )
                    ;
                pushBack();
                if ( readTokenQuoted() )
                    systemId = _tokenText.toString();
                else
                {
                    if ( readTokenName() )
                        systemId = _tokenText.toString();
                    error( WELL_FORMED, message( "Parser037" ) );
                }
            }
            else
            if ( _tokenText.equalsUpper( "PUBLIC" ) )
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
                    {
                        if ( readTokenName() )
                            systemId = _tokenText.toString();
                    }
                }
                else
                {
                    if ( readTokenName() )
                        publicId = _tokenText.toString();
                    error( WELL_FORMED, message( "Parser038" ) );
                }
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
/*
                source = DOMFactory.newSource();
                source.setURI( systemId );
                source.setPublicId( publicId );
                source.setDocClass( Source.DOCUMENT_DTD );
                holder = DOMFactory.getHolderFinder().findHolder( source, false );
                if ( holder != null )
                    _docType = (DTDDocument) holder.newInstance();
                else
                    error( ERROR_WELL_FORMED, "Could not locate external DTD subset." );
*/
            }
        }
        else
            error( WELL_FORMED, message( "Parser040" ) );

        // Consume anything else up to the closing '>'.
        while ( readChar() != EOF && _curChar != '>' )
            ;
        if ( _curChar == EOF )
            error( WELL_FORMED, message( "Parser042" ) );
    }
    

    /**
     * Parses the next node based on the supplied token.
     * <P>
     * The return value indicates if the root element has been closed
     * or the document's end reached (false), or should parsing continue
     * (true).
     *
     * @param token The last token read with {@link #readTokenContent}
     * @return True if continue parsing, false if current element has been closed
     *  or reached end of file
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     * @see #parseAttributes
     * @see #readTokenContent
     */
    protected boolean parseNextNode( int token )
        throws SAXException
    {
        int             i;
        char            ch;
        String          tagName;
        boolean         openTag;


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
            if ( isWarning() )
                warning( message( "Parser043" ) );
            token = TOKEN_TEXT;
            _tokenText.insert( 0, "<![" );
        }
        // TOKEN_DTD is also translated into plain text and an error is issued.
        if ( token == TOKEN_DTD )
        {
            if ( isWarning() )
                warning( message( "Parser043" ) );
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
            i = HTMLdtd.charFromName( _tokenText.toString() );
            if ( i == -1 )
            {
                _tokenText.insert( 0, '&' ).append( ';' );
                error( WELL_FORMED, format( "Parser045", _tokenText ) );
            }
            else
            {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, (char) i );
            }
        }

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
                _nodeText.append( ch );
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
		if ( getElementState() != null && getElementState().isElementContent() )
		{
		    if ( isNodeTextSpace() )
			_documentHandler.ignorableWhitespace( _nodeText.getCharArray(), 0, _nodeText.length() );
		    else
			error( WELL_FORMED, format( "Parser011", getElementState().getTagName() ) );
		}
		else
		{
		    if ( isNodeTextSpace() )
			_documentHandler.ignorableWhitespace( _nodeText.getCharArray(), 0, _nodeText.length() );
		    else {
			fixHtmlTree( null );
			_documentHandler.characters( _nodeText.getCharArray(), 0, _nodeText.length() );
		    }
		}
                _nodeText.setLength( 0 );
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
                tagName = slicePITokenText();
                _documentHandler.processingInstruction( tagName, _tokenText.toString() );
            }
            else

            // Token is a comment. If we reached this point, the mode in effect
            // commands storing comments in the document tree. Comment contents is
            // assured not to contain any '--'.
            if ( token == TOKEN_COMMENT )
            {
                if ( _documentHandler instanceof DocumentHandlerEx )
                    ( (DocumentHandlerEx) _documentHandler ).comment( _tokenText.toString() );
            }
            else

            // Token is an opening tag. The tag name has been parsed, attribute values

            // should be read next.
            if ( token == TOKEN_OPEN_TAG )
            {
                // _tokenText is guranteed to contain the element's tag name.
                // The call to parseAttributes will call the element start event.
                // If the element is an empty tag (openTag returns false), then
                // the element end event is also called.
                tagName = _tokenText.toUpperCase().toString();
                fixHtmlTree( tagName );
		// preClose( handler, tagName );
                
                // Attributes are read next based on the document type. The tag is
                // identified as an open tag not based on readAttribute(), but based
                // on its capacity to support children (e.g. <BR> is empty), or the
                // support of an optional closing tag (e.g. <P>).
                parseAttributes( tagName );

                // Three HTML elements are parsed differently. Their contents is read
                // as is, without markup or character reference parsing, and while
                // preserving whitespaces. Everything between the opening and closing
                // tag is consumed and used as the element's text contents.
                if ( tagName.equals( "SCRIPT" ) || tagName.equals( "STYLE" ) )
                {
                    _nodeText.setLength( 0 );
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
                                if ( readTokenName() && _tokenText.equalsUpper( tagName ) )
                                    break;
                                else
                                    pushBackToken();
                                _curChar = '/';
                            }
                            pushBack();
                            _curChar = '<';
                        }
                        _nodeText.append( (char) _curChar );
                        readChar();
                    }
                    // Strip LF that comes immediately after opening tag or
                    // immediately before closing tag. And only these two.
                    if ( _nodeText.length() > 0 && _nodeText.charAt( 0 ) == LF )
                        _nodeText.delete( 0, 1 );
                    if ( _nodeText.length() > 0 && _nodeText.charAt( _nodeText.length() - 1 ) == LF )
                        _nodeText.setLength( _nodeText.length() - 1 );
		    _documentHandler.characters( _nodeText.getCharArray(), 0, _nodeText.length() );
		    _nodeText.setLength( 0 );
                    
                    // Consume all characters until the closing tag's '>' is found.
                    // If not found, issue an error. This complies with how other
                    // elements are treated (see readTokenMarkup()).
                    readChar();
                    while ( _curChar != EOF && _curChar != '>' && _curChar != '<' )
                        readChar();
                    if ( _curChar == '<' )
                        pushBack();
                    if ( _curChar != '>' )
                        error( WELL_FORMED, message( "Parser002" ) );
                    _documentHandler.endElement( tagName );
                    return true;
                }
                else
                {
                    // If this is an open tag, read the contents of the element until
                    // the closing tag (or end of file) is reached. The closing tag
                    // (or end of file) is identified by parseNextNode() returning false.
                    if ( HTMLdtd.isEmptyTag( tagName ) )
                        _documentHandler.endElement( tagName );
                    else
			enterElementState( tagName, getLineNumber(), HTMLdtd.isElementContent( tagName ) );
		    // HTMLdtd.isPreserveSpace( tagName ) || getElementState().isPreserveSpace() );
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
                tagName = _tokenText.toUpperCase().toString();
                if ( HTMLdtd.isEmptyTag( tagName ) && isWarning() )
                    warning( format( "Parser010", tagName ) );
                else
		{
		    ElementState    closeState;
        
		    closeState = getElementState();

		    // Current node may not be an element, which defies the whole point of
		    // checking the end tag. This only happens at the top level document or
		    // document fragment, so stop processing immediately.
		    if ( closeState == null )
		    {
			 error( WELL_FORMED, format( "Parser008", tagName ) );
			 return false;
		    }

		    while ( closeState != null )
		    {
			// Bingo! Closing tag closes the currently open tag. Return false,
			// which will land us right in the middle of the parseNextNode()
			// loop in the open tag token scenario of the including element.
			if ( tagName.equals( closeState.getTagName() ) )
			{
			    while ( closeState != getElementState() )
			    {
				if ( ! HTMLdtd.isOptionalClosing( getElementState().getTagName() ) )
				    error( WELL_FORMED, format( "Parser046", getElementState().getTagName(),
								new Integer( getElementState().getStartLine() ),
								tagName ) );
				_documentHandler.endElement( getElementState().getTagName() );
				leaveElementState();
			    }
			    _documentHandler.endElement( tagName );
			    if ( leaveElementState() == null )
				return false;
			    closeState = null;
			}
			else
			{
			    closeState = getPreviousState( closeState );
			    if ( closeState == null )
			    {
				error( WELL_FORMED, format( "Parser008", tagName ) );
				return true;
			    }
			}
		    }

		}
            }
        }
        return true;
    }


    
    
    protected final void fixHtmlTree( String tagName )
        throws SAXException
    {
        // If we are not in any state, return. This is most probably an error.
        if ( getElementState() == null )
	{
            // No tag specified or tag is not HTML, create the HTML element.
            // No tag specified or tag is not BODY/HEAD, create the BODY element.
            if ( tagName == null || ! tagName.equals( "HTML" ) )
            {
                _documentHandler.startElement( "HTML", new AttrListImpl() );
		enterElementState( "HTML", getLineNumber(), true );
                if ( tagName == null || ! ( tagName.equals( "BODY" ) ||
					    tagName.equals( "HEAD" ) ||
					    tagName.equals( "FRAMESET" ) ) )
                {
                    _documentHandler.startElement( "BODY", new AttrListImpl() );
		    enterElementState( "BODY", getLineNumber(), false );
                }
            }
        }
        else
	{
	    // If this opening tag implicitly closes an already open element
	    // (e.g. DD and DT) then close the other element now.
	    while ( tagName != null && HTMLdtd.isClosing( tagName, getElementState().getTagName() ) )
	    {
		_documentHandler.endElement( getElementState().getTagName() );
		leaveElementState();
	    }

	    // If inside the HTML element and HTML is the top element, then
	    // if this tag is neither a BODY nor a HEAD, create a BODY in which
	    // to contain this element.
	    if ( getElementState().getTagName().equals( "HTML" ) &&
		 getPreviousState( getElementState() ) == null &&
		 ( tagName != null && ! tagName.equals( "BODY" ) && ! tagName.equals( "HEAD" ) &&
		   ! tagName.equals( "FRAMESET" ) && 
		   ! tagName.equals( "SCRIPT" ) && ! tagName.equals( "STYLE" ) ) )
	    {
		_documentHandler.startElement( "BODY", new AttrListImpl() ) ;
		enterElementState( "BODY", getLineNumber(), false );
	    }
        
        }
    }

    
    /**
     * Called to handle text appearing in the document. Uses the current
     * state to determine how to process this text.
     * 
     * NOT FULLY IMPLEMENTED
     */
    protected final void handleText( boolean first, boolean last )
        throws SAXException
    {
        char[]          chars;
        int             length;
        int             to;
        int             from;
        
        length = _nodeText.length();
        if ( length == 0 )
            return;
        chars = _nodeText.getCharArray();
        
        if ( getElementState() != null && getElementState().isElementContent() )
        {
            if ( ! isNodeTextSpace() )
            {
                _documentHandler.characters( chars, 0, length );
                error( WELL_FORMED, format( "Parser011", getElementState().getTagName() ) );
            }
            else
                _documentHandler.ignorableWhitespace( chars, 0, length );
        }
        else        
	if ( getElementState() != null ) // && getElementState().isPreserveSpaces() )
        {
            if ( first && chars[ 0 ] == LF )
                from = 1;
            else
                from = 0;
            if ( last && chars[ length - 1 ] == LF )
                to = length - 1;
            else
                to = length;
            _documentHandler.characters( chars, from, to - from );
        }
        else
        {
            from = 0;
            to = 0;
            while ( from < length )
            {
                if ( isSpace( chars[ from ] ) )
                {
                    if ( from + 1 < length &&
                         ! isSpace( chars[ from + 1 ] ) )
                    {
                        chars[ to ] = SPACE;
                        ++ to;
                    }
                }
                else
                {
                    chars[ to ] = chars[ from ];
                    ++ to;
                }
                ++from;
            }
            length = to;
            if ( length > 0 )
            {
                if ( first && isSpace( chars[ 0 ] ) )
                    from = 1;
                else
                    from = 0;
                if ( last && isSpace( chars[ length - 1 ] ) )
                    to = length - 1;
                else
                    to = length;
                _documentHandler.characters( chars, from, to - from );
            }
        }
        _nodeText.setLength( 0 );
    }
    

    protected final boolean isNodeTextSpace()
    {
        char[]          chars;
        int             length;
        int             i;

        length = _nodeText.length();
        if ( length == 0 )
            return true;
        chars = _nodeText.getCharArray();
        for ( i = 0 ; i < length ; ++i )
	    if ( chars[ i ] != SPACE && chars[ i ] != LF && chars[ i ] != CR && chars[ i ] != 0x09 )
		return false;
        return true;        
    }


    protected final boolean isHtml()
    {
	return true;
    }


}
