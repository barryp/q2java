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


public final class XMLSAXParser
    extends ContentParser
    implements Parser
{
    
    
    public XMLSAXParser()
    {
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
    }

    
    public synchronized void parse( InputSource input )
        throws SAXException, IOException
    {
        int             token;
	boolean         standalone = false;
        
        init( input );
        try
        {
            // Start the document.
            _documentHandler.startDocument();

            // Parse version number, encoding information and standalone flag from
            // '<?xml' PI at beginning of document.
            token = readTokenContent();
            if ( token == TOKEN_PI && _tokenText.length() > 3 &&
                 _tokenText.startsWith( "xml" ) )
            {
                standalone = parseDocumentDecl( true );
                token = readTokenContent();
            }

            // Any number of comments, PI and whitespace may appear before the
            // document type declaration, so process only these tokens and
            // presently ignore them.
            while ( token == TOKEN_COMMENT || token == TOKEN_PI ||
                    ( token == TOKEN_TEXT && isTokenSpace() ) ) {
		if ( token == TOKEN_TEXT )
		    _documentHandler.ignorableWhitespace( _tokenText.getChars(), 0, _tokenText.length() );
		else
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
                if ( ! ( token == TOKEN_TEXT && isTokenSpace() ) )
                    error( WELL_FORMED, message( "Parser056" ) );
		else
		    _documentHandler.ignorableWhitespace( _tokenText.getChars(), 0, _tokenText.length() );
		token = readTokenContent();
            }

            // Read the root element and all the document content contained in it.
            if ( token == TOKEN_OPEN_TAG )
            {
		while ( parseNextNode( token ) )
		    token = readTokenContent();
                while ( getElementState() != null )
                {
		    _documentHandler.endElement( getElementState().getTagName() );
                    error( WELL_FORMED, format( "Parser046", getElementState().getTagName(),
						new Integer( getElementState().getStartLine() ) ) );
                    leaveElementState();
                }
		token = readTokenContent();
            }
            else
                if ( isWarning() )
                    warning( message( "Parser057" ) );
	    if ( token != TOKEN_EOF )
		token = readTokenContent();
            
            // Any number of comments, PI and whitespace may appear after the
            // root element, so process only these tokens.
            while ( token != TOKEN_OPEN_TAG && token != TOKEN_EOF )
            {
                if ( token == TOKEN_COMMENT || token == TOKEN_PI )
                    parseNextNode( token );
                else
                if ( ! ( token == TOKEN_TEXT && isTokenSpace() ) )
                    error( WELL_FORMED, message( "Parser056" ) );
		token = readTokenContent();
            }

            _documentHandler.endDocument();
        }

        // Catch parsing exception and rethrow them. This catch clause is only
        // necessary because of the second, more general catch clause that catches
        // other forms of exceptions: I/O exceptions, runtime exceptions, or even
        // node not supporting children. Finally close the parser and return the
        // node.
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
            close();
        }
    }
    

    protected boolean isHtml()
    {
        return false;
    }

    
    /**
     * Parser the external DTD subset.
     */
    protected final void parseDTDSubset( boolean standalone )
        throws SAXException
    {
        String          publicId = null;
        String          systemId = null;
	String          rootElement = "";
/*
        Source  source;
        Holder  holder;
*/
        // Consume white spaces up to the root element name. If not found,
        // this DTD is invalid.
	skipSpace();
        if ( readTokenNameCur() )
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
	    rootElement = _tokenText.toString();

            // Read the SYSTEM identifier or the PUBLIC and system identifer.
            // The identifiers much be quoted, if they are not they are still
            // read, but an error is reported.
	    skipSpace();
            pushBack();
            if ( readTokenName() )
	    {
		if ( _tokenText.equalsUpper( "SYSTEM" ) )
		{
		    skipSpace();
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
		    skipSpace();
		    pushBack();
		    if ( readTokenQuoted() )
		    {
			publicId = _tokenText.toString();
			skipSpace();
			pushBack();
			if ( readTokenQuoted() )
			    systemId = _tokenText.toString();
			else
			{
			    if ( readTokenName() )
				systemId = _tokenText.toString();
			    error( WELL_FORMED, message( "Parser039" ) );
			}
		    }
		    else
		    {
			if ( readTokenName() )
			    publicId = _tokenText.toString();
			error( WELL_FORMED, message( "Parser038" ) );
		    }
                }
            }

            // If an external subset has been specified, process it as well using
            // the existing or a new DTD document. This assure that the external
            // subset appears to be declared after the internal subset. Report well
            // formed errors if the external subset could not be found.
            if ( systemId != null || publicId != null )
            {
		if ( publicId != null && systemId == null )
		    ; // This is an XML error, public implies system
		if ( _documentHandler instanceof DocumentHandlerEx )
		    ( (DocumentHandlerEx) _documentHandler ).setDocumentType( rootElement, publicId, systemId );
            }
        }
        else
        {
	    pushBack();
            error( WELL_FORMED, message( "Parser040" ) );
	}

        // Consume anything else up to the closing '>'.
        while ( readChar() != EOF && _curChar != '>' )
            ;
        if ( _curChar == EOF )
            error( WELL_FORMED, message( "Parser042" ) );
    }
    

    /**
     * Parses the next node based on the supplied token.
     * <P>
     * The return value indicates if the root element has been closed with
     * a closing tag or reached end of file (false), or should parsing continue
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
    protected boolean  parseNextNode( int token )
        throws SAXException
    {
        String          tagName;

        if ( token == TOKEN_ENTITY_REF )
        {
            token = TOKEN_TEXT;
	    if ( _tokenText.equals( "lt" ) )
	    {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, '<' );
	    }
	    else
	    if ( _tokenText.equals( "gt" ) )
	    {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, '>' );
	    }
	    else
	    if ( _tokenText.equals( "quot" ) )
	    {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, '"' );
	    }
	    else
	    if ( _tokenText.equals( "apos" ) )
	    {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, '\'' );
	    }
	    else
	    if ( _tokenText.equals( "amp" ) )
	    {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, '&' );
	    }
	    else
            {
                _tokenText.insert( 0, '&' ).append( ';' );
                error( WELL_FORMED, format( "Parser045", _tokenText ) );
            }
        }
	
        if ( token == TOKEN_TEXT )
	    {
	    if ( _documentHandler instanceof DocumentHandlerEx )
		( (DocumentHandlerEx) _documentHandler ).characters( _tokenText.toString() );
	    else
		_documentHandler.characters( _tokenText.getChars(), 0, _tokenText.length() );
	    _tokenText.setLength( 0 );
	    return true;
	}

	// If token is EOF, return false indicating we're done with current
	// element. EOF will be returned for as long as necessary until all open
	// elements have been closed in this manner. This check is done here and
	// not at the method's start due to the above Text node creation code.
	if ( token == TOKEN_EOF )
	    return false;

	// Token is an opening tag. The tag name has been parsed, attribute values
	// should be read next.
	if ( token == TOKEN_OPEN_TAG )
        {
	    // _tokenText is guranteed to contain the element's tag name.
	    // The call to parseAttributes will call the element start event.
	    // If the element is an empty tag (openTag returns false), then
	    // the element end event is also called.
	    tagName = _tokenText.toString();
	    
	    // Attributes are read next based on the document type. The tag is
	    // identified as an open tag not based on readAttribute(), but based
	    // on its capacity to support children (e.g. <BR> is empty), or the
	    // support of an optional closing tag (e.g. <P>).
	    // If this is an open tag, read the contents of the element until
	    // the closing tag (or end of file) is reached. The closing tag
	    // (or end of file) is identified by parseNextNode() returning false.
	    if ( parseAttributes( tagName ) )
		enterElementState( tagName, getLineNumber(), false /* isElementContent( tagName ) */ );
	    else
		_documentHandler.endElement( tagName );
	}
	else

        // Token is a closing tag. This can happen in three cases: the ending of
        // the currently open tag, the ending of some other tag (could be), and
        // the ending of an empty tag. The latter case happens, e.g. when '<P>'
        // if followed by '</P>'. This segment returns false if the current
        // open element is closed, but true if something else happens.
        if ( token == TOKEN_CLOSE_TAG )
        {
	    ElementState    closeState;
	    boolean         closed = false;
        
	    tagName = _tokenText.toString();
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
			error( WELL_FORMED, format( "Parser046", getElementState().getTagName(),
						    new Integer( getElementState().getStartLine() ), tagName ) );
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
			return false;
		    }
		}
	    }
	    return true;
	}
	else

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

        // Token is a CDATA section and should be stored. If this is the
        // home document handler, create a CDATA section element. Otherwise,
        // pass the contents as text.
        if ( token == TOKEN_CDATA )
        {
	    if ( _documentHandler instanceof DocumentHandlerEx )
		( (DocumentHandlerEx) _documentHandler ).cdataSection( _tokenText.toString() );
	    else
		_documentHandler.characters( _nodeText.getCharArray(), 0, _nodeText.length() ); 
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
        // Token is an entity reference and should be parsed or stored.
        if ( token == TOKEN_ENTITY_REF )
	{
	    if ( _documentHandler instanceof DocumentHandlerEx )
		( (DocumentHandlerEx) _documentHandler ).entityReference( _tokenText.toString() );
	}
	else

        // TOKEN_SECTION is not TOKEN_CDATA. It is translated into plain text and
        // an error is issued.
        // TOKEN_DTD is also translated into plain text and an error is issued.
        if ( token == TOKEN_SECTION || token == TOKEN_DTD )
	{
	    if ( isWarning() )
		warning( message( "Parser043" ) );
	    token = TOKEN_TEXT;
	    if ( token == TOKEN_SECTION )
		_tokenText.insert( 0, "<![" );
	    else
		_tokenText.insert( 0, "<!" );
	}
        return true;
    }


}



