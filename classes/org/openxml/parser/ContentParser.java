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
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.openxml.dom.*;
import org.openxml.util.FastString;


/**
 * Implements layers 3 of the parser architecture, the handling of XML/HTML
 * document content. This level also implements the SAX parser interface
 * for setting document handler, etc.
 * <P>
 * This parser is created by a derived class. The {@link #init} method
 * must be called before the parser is used or re-used (the parser is
 * re-entrant).
 * <P>
 *
 *
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:57:06 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see Parser
 * @see StreamParser
 * @see TokenParser
 * @see FastString
 */
abstract class ContentParser
    extends TokenParser
    implements Parser
{

    
    /**
     * Protected constructor only accessible from derived class.
     */
    protected ContentParser()
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
        _nodeText = new FastString();
	setDocumentHandler( _documentHandler );
    }

    
    public final void setDTDHandler( DTDHandler handler )
    {
        // Freely change DTD handler in the middle of everything.
        // Rest of parser must be able to deal with that gracefully.
        _dtdHandler = handler;
    }


    public final void setDocumentHandler( DocumentHandler handler )
    {
        // Freely change document handler in the middle of everything.
        // Rest of parser must be able to deal with that gracefully.
	// Since we cannot have a null reference, a default do nothing
	// handler is used if no handler is specified.
	if ( handler == null )
	    _documentHandler = _emptyHandler;
	else
	    _documentHandler = handler;
    }

    
    public final void setEntityResolver( EntityResolver resolver )
    {
        // Freely entity resolver in the middle of everything.
        // Rest of parser must be able to deal with that gracefully.
        _entityResolver = resolver;
    }


    public synchronized final void parse( String systemId )
        throws SAXException, IOException
    {
        InputSource     input = null;
        EntityResolver  resolver;
        
        if ( systemId == null )
            throw new NullPointerException( format( "Error001", "systemId" ) );
        // Special handling required, the resolver might be null or changed
        // while we read it. The default resolver is currently the parser
        // itself.
        input = resolveEntity( null, systemId );
        if ( input == null || ( input.getByteStream() == null &&
                                input.getCharacterStream() == null ) )
            fatalError( new SAXException( format( "Parser047", systemId ) ) );
        parse( input );
    }
    

    public final InputSource resolveEntity( String publicId, String systemId )
        throws IOException, SAXException
    {
        InputSource source;

        if ( _entityResolver != null ) {
            source = _entityResolver.resolveEntity( publicId, systemId );
            if ( source != null )
                return source;
        }
        return super.resolveEntity( publicId, systemId );
    }


    /**
     * Reads and returns a single content token. The token's code is returned and
     * the token's text is available in {@link #_tokenText}. No valid character is
     * held in {@link #_curChar} on entry or on exit. Returned token is suitable
     * for document contents in either XML or HTML document or external entity.
     * {@link #TOKEN_EOF} indicates the end of the input stream.
     * <P>
     * Plain text is returned as a {@link #TOKEN_TEXT} token. Text that is part of
     * the element contents may be returned as multiple tokens, or text tokens
     * interwined with entity reference tokens (of type {@link #TOKEN_ENTITY_REF}).
     * <P>
     * For specific information about token processing, see {@link #readTokenMarkup}
     * and {@link #readTokenEntity}.
     * 
     * @return The token code
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     * @see #_tokenText
     * @see #readTokenMarkup
     * @see #readTokenEntity
     */
    protected final int readTokenContent()
        throws SAXException
    {
        int    token;
        
        // Read a single character and try to ascertain what comes next.
        // If character is less-than, it might be one of several markup tokens.
        // If character is an ampersand, it might be an entity reference.
        // Otherwise, character is plain text.
        if ( readChar() == '<' )
            return readTokenMarkup(); 
        else
        if ( _curChar == '&' )
            return readTokenEntity();
        else
        if ( _curChar == EOF )
            return TOKEN_EOF;
        
        // Read as much plain text as possible and accumulate it in _tokenText.
        // Return textual token.
        _tokenText.setLength( 0 );
        while ( _curChar != EOF && _curChar != '<' && _curChar != '&' )
        {
            if ( _curChar == '>' && isWarning() )
                warning( message( "Parser029" ) );
            _tokenText.append( (char) _curChar );
            readChar();
        }
        if ( _curChar != EOF )
            pushBack();
        return TOKEN_TEXT;
    }
    

    /**
     * Parses an internal or external general entity into XML/HTML contents.
     * Called when an entity reference is encountered in the element contents.
     * {@link #_tokenText} contains the entity name on entry. Events are
     * fired to store the entity's value in the document contents.
     * <P>
     * If the entity is unparsed general, or not found, an error is issued.
     * 
     * <LI>If the entity could not be found or could not be parsed, an error
     *  is issued
     * <LI>The contents of the parsed entity is duplicated and placed in the
     *  document either through node cloning or SAX events
     * <LI>If the parsed entity contains an entity reference, it is parsed
     *  recursively according to the same rules
     * </UL>
     * 
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     */
    protected final void parseContentEntity()
        throws SAXException
    {
        String          name;
        EntityImpl      entity;
        Node            node;
        DocumentHandler handler;
        
        // Special handling required, the handler might be null or changed
        // while we use it. No point in proceeding if null.
        handler = _documentHandler;
        if ( handler == null )
            return;
        
        name = _tokenText.toString();
        if ( name.equals( "lt" ) )
            handler.characters( _entityLt, 0, 1 );
        else
        if ( name.equals( "gt" ) )
            handler.characters( _entityGt, 0, 1 );
        else
        if ( name.equals( "amp" ) )
            handler.characters( _entityAmp, 0, 1 );
        else
        if ( name.equals( "apos" ) )
            handler.characters( _entityApos, 0, 1 );
        else
        if ( name.equals( "quot" ) )
            handler.characters( _entityQuot, 0, 1 );
        else
        if ( _docType != null )
        {
            // Locate the entity in the DTD. If entity could not be parsed,
            // delete it from the DTD. This will create a new empty entity
            // in its place.
            entity = _docType.findEntity( name );
            if ( entity != null )
            {
                if ( entity.isUnparsed() )
                    error( WELL_FORMED, format( "Parser033", name ) );
                else
                if ( parseGeneralEntity( entity ) != null )
                {
                    // Get the contents of the entity, valid for attribute values.
                    // Store the clones entity content as is underneath the current node.
                    node = entity.getFirstChild();
                    while ( node != null )
                    {
                        // handleNode( node );
                        node = node.getNextSibling();
                    }
                }
                return;
            }
        }
        error( WELL_FORMED, format( "Parser030", name ) );
    }
    
    
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
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     */
    protected final EntityImpl parseGeneralEntity( EntityImpl entity )
        throws SAXException
    {
/*
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
                    parser = new XMLParser( this, reader, getSystemId() + " Entity: " + entity.getNodeName() );
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
            catch ( SAXException except )
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
*/
        return null;
    }
    

    /**
     * Parses the attribute list of an XML/HTML open tag and calls the proper
     * events for this element using the attribute list. If this element is
     * empty (XML only), returns true and calls both element start and end
     * events. If this is an opening tag (all tags for HTML), returns false

     * <P>
     * On entry the element's tag name and optional whitespaces have been read.
     * On exit, all attributes have been read including the closing mark.
     * No valid character is held in {@link #_curChar} on entry or on exit.
     * 
     * @param tagName Name of element being parsed
     * @param xml True if parsing an XML document, false for HTML
     * @return True if an open tag is parsed, false if an empty tag or any
     *  HTML tag
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     */
    protected boolean parseAttributes( String tagName )
        throws SAXException
    {
        char                quote;
        int                 ch;
        String              attrName;
        
        // Whitespaces after tag name have been read, so first attribute can
        // be read immediately, if not the end of the tag.
        _attrList.clear();
        readChar();
        while ( _curChar != EOF )
        {
            // If next character is '>' or '/>', conclude reading attribute list
            // and return false for an empty element. Note that '/>' is not really
            // supported in XML documents, so a warning is issued. Also handle case
            // of unexpected '<'.
            if ( _curChar == '>' )
            {
		_documentHandler.startElement( tagName, _attrList );
                return true;
            }
            else
            if ( _curChar == '/' )
            {
                if ( readChar() == '>' )
                {
                    // '/>' is not recognized as an empty element in HTML
                    // documents. HTML documents defines empty elements in
                    // the DTD. A warning is ussed, but the '/>' is still
                    // processed.
                    if ( isHtml() && isWarning() )
                        warning( message( "Parser001" ) );
		    _documentHandler.startElement( tagName, _attrList );
                    return false;
                }
                pushBack();
            }
            else
            if ( _curChar == '<' )
            {
                error( WELL_FORMED, message( "Parser002" ) );
                pushBack();
		_documentHandler.startElement( tagName, _attrList );
                return true;
            }
            else
                
            // An attribute must start with a valid attribute name. Read as
            // much of the name as possible.
            if ( isNamePartFirst( _curChar ) )
            {
                // Read the valid attribute name. If the attribute already
                // appears in the list, issue an error indicating that.
                // Note that in HTML the attribute name is all lower case.
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, (char) _curChar );
                while ( isNamePart( readChar() ) )
                    _tokenText.append( (char) _curChar );
		if ( isHtml() )
                    attrName = _tokenText.toLowerCase().toString();
                else
                    attrName = _tokenText.toString();

                // If the attribute already appears for this element,
                // report an error. The second value will not override
                // the first one.
                if ( _attrList.getValue( attrName ) != null )
                    error( WELL_FORMED, format( "Parser003", attrName ) );
                
                // Consume white space following attribute value. Expect equal sign
                // and more optional whitespace to follow for value. Otherwise,
                // attribute has an empty value.
                while ( isSpace( _curChar ) )
                    readChar();
                if ( _curChar == '=' )
                {
		    skipSpace();
                    // First character after '=' determines if the attribute's
                    // value is quoted. It can be a single quote, a double quote,
                    // or nothing (not legal in XML, but still accepted).
                    if ( _curChar == '\'' || _curChar == '\"' )
                    {
                        quote = (char) _curChar;
                        readChar();
                    }
                    else
                    {
                        if ( ! isHtml() )
                            warning( message( "Parser005" ) );
                        quote = SPACE;
                    }
                    
                    // Read the attribute's value until the next separator character
                    // (quote, white space, or '>'). The parsed value is accumulated
                    // in attrValue.
                    _attrValue.setLength( 0 );

                    while ( _curChar != EOF )
                    {
                        // Separator found (quote or space). Terminate this while
                        // loop. Call readChar() since the outer loop will not have
                        // a change to do it, with the continue that comes next.
                        if ( _curChar == quote )
                        {
                            readChar();
                            break;
                        }
                        // When separator is a space, '>' or '/>', finish reading
                        // the attribute value and leave the last character unread,
                        // so it may be processed by the next iteration of the outer
                        // loop, since continue comes next.
                        if ( quote == SPACE )
                        {
                            if ( isSpace( _curChar ) || _curChar == '>' || _curChar == '<' )
                                break;
                            if ( _curChar == '/' )
                            {
                                // If only '/' found, push back the last character
                                // and add '/' to the attribute value. If '/>' found,
                                // push '>', prepare '/' to be read next, and terminate
                                // the inner loop. '>' will be processed by the outer
                                // loop in the next iteration.
                                if ( readChar() == '>' )
                                {
                                    pushBack();
                                    _curChar = '/';
                                    break;
                                }
                                _attrValue.append( '/' );
                            }
                        }

                        // Found an ampersand, which denotes an entity or character
                        // reference. Character reference is returned as text; entity is
                        // returned as token. If the entity could not be parsed, an
                        // error has been issued and alternative textual contents
                        // returned.
                        if ( _curChar == '&' )
                        {
                            // HTML document, only character entities are supported,
                            // get the character value from the entity name and append
                            // it to the attribute value.
                            if ( isHtml() )
                            {
				if ( readTokenEntity( true ) == TOKEN_TEXT )
				    _attrValue.append( _tokenText );
				else {
				    ch = HTMLdtd.charFromName( _tokenText.toString() );
				    if ( ch == -1 )
					{
					    warning( format( "Parser055", _tokenText.toString() ) );
					    _attrValue.append( '&' ).append( _tokenText ).append( ';' );
					}
				    else
					_attrValue.append( (char) ch );
				}
                            }
                            // XML document, only internal entities are supported by
                            // taking their text and reparsing it. Special rules are
                            // observerd.
                            else
			    {
				if ( readTokenEntity( false ) != TOKEN_TEXT )
				    ;
                            }
                        }
                        else
                        {
                            // By definition '<' not allowed inside XML attribute
                            // value, but we'll just report and error and proceed.
                            if ( ! isHtml() && _curChar == '<' )
                                error( WELL_FORMED, message( "Parser048" ) );
                            _attrValue.append( (char) _curChar );
                        }
                        readChar();
                    }
                    // Define the new attribute with the specified value.
                    _attrList.addAttribute( attrName, "CDATA", _attrValue.toString() );
                }
                else
                {
                    // The attribute appears to have no value. In XML this is a
                    // well formed error, in HTML a plain warning. In either case
                    // the attribute is defined to have an empty value.
                    if ( ! isHtml() )
                        error( WELL_FORMED, format( "Parser004", attrName ) );                    
                    else
                    if ( isWarning() )
                        warning( format( "Parser004", attrName ) );
                    _attrList.addAttribute( attrName, "CDATA", "" );
                }
                // Avoid the readChar() that comes at the end of all conditions.
                continue;
                
            }
            else
            // Ignore whitespaces inbetween attributes. Everything else is junk
            // text, probably an indication of attribute values not being properly
            // parser. So consume it and report it.
            if ( ! isSpace( _curChar ) )
            {
                _tokenText.setLength( 1 );
                _tokenText.setCharAt( 0, (char) _curChar );
                readChar();
                while ( _curChar != EOF && _curChar != '<' && _curChar != '>' &&
                        _curChar != '/' && ! isSpace( _curChar ) )
                {
                    _tokenText.append( (char) _curChar );
                    readChar();
                }
                error( WELL_FORMED, format( "Parser006", _tokenText.toString() ) );
            }
                
            readChar();
        }
        
        error( WELL_FORMED, message( "Parser007" ) );
	_documentHandler.startElement( tagName, _attrList );
        return true;
    }
    
    
    /**
     * Parses an internal general entity into the attribute value. Called by
     * {@link #parseAttributes} when an entity reference is encountered in the
     * attribute value. {@link #_tokenText} contains the entity name on entry
     * and the parsed value on exit.
     * <P>
     * If the entity is external, unparsed general or not found, an error is
     * issued and nothing is placed in the attribute value.
     * 
     * @throws SAXException A parsing error has been encountered, and based on
     *  it severity, an exception is thrown to terminate parsing
     */
    protected final void parseAttrEntity( String name )
        throws SAXException
    {
        EntityImpl  entity;
        String      text;

        // There five entities are implicitly declared for all XML and HTML
        // documents and need not appear in the DTD.
        if ( name.equals( "lt" ) )
            _tokenText.append( _entityLt[ 0 ] );
        else
        if ( name.equals( "gt" ) )
            _tokenText.append( _entityGt[ 0 ] );
        else
        if ( name.equals( "amp" ) )
            _tokenText.append( _entityAmp[ 0 ] );
        else
        if ( name.equals( "apos" ) )
            _tokenText.append( _entityApos[ 0 ] );
        else
        if ( name.equals( "quot" ) )
            _tokenText.append( _entityQuot[ 0 ] );
        else
        if ( _docType != null )
        {
            // Locate the entity in the DTD. If entity could not be parsed,
            // delete it from the DTD. This will create a new empty entity
            // in its place.
            entity = _docType.findEntity( name );
            if ( entity != null )
            {
                if ( entity.isUnparsed() )
                    error( WELL_FORMED, format( "Parser032", name ) );
                else
                if ( ! entity.isInternal() )
                    error( WELL_FORMED, format( "Parser031", name ) );
                else
                {
                    text = entity.getInternal();
                    for ( int i = 0 ; i < text.length() ; ++i )
                    {
                        if ( text.charAt( i ) == '<' )
                        {
                            error( WELL_FORMED, message( "Parser048" ) );
                        }
                        else
                        if ( text.charAt( i ) == '&' )
                        {
                        }
                        else
                            _tokenText.append( (char) text.charAt( i ) );
                    }
                }
                return;
            }
            else
                error( WELL_FORMED, format( "Parser030", name ) );
        }
        else {
            error( WELL_FORMED, format( "Parser030", name ) );
	}
    }    

    
    protected final boolean isTokenSpace()
    {
        char[]          chars;
        int             length;
        int             i;

        length = _tokenText.length();
        if ( length == 0 )
            return true;
        chars = _tokenText.getCharArray();
        for ( i = 0 ; i < length ; ++i )
	    if ( chars[ i ] != SPACE && chars[ i ] != LF && chars[ i ] != CR && chars[ i ] != 0x09 )
		return false;
        return true;        
    }

    
    protected final void enterElementState( String tagName, int lineNumber, boolean elementContent )
    {
	ElementState[] newList;

        if ( _elementStateCount == _elementStateList.length )
	{
	    newList = new ElementState[ _elementStateCount + 8 ];
	    System.arraycopy( _elementStateList, 0, newList, 0, _elementStateCount );
	    _elementStateList = newList;
	}
	
	if ( _elementStateList[ _elementStateCount ] == null )
	    _elementStateList[ _elementStateCount ] = new ElementState( tagName, lineNumber, elementContent );
	else
	    _elementStateList[ _elementStateCount ].assign( tagName, lineNumber, elementContent );
	++ _elementStateCount;
    }


    protected final ElementState getElementState()
    {
	if ( _elementStateCount == 0 )
	    return null;
	else
	    return _elementStateList[ _elementStateCount - 1 ];
    }


    protected final ElementState leaveElementState()
    {
	if ( _elementStateCount == 1 )
	{
	    _elementStateCount = 0;
	    return null;
	}
	else
	if ( _elementStateCount == 0 )
	    return null;
	else
	{
	    -- _elementStateCount;
	    return _elementStateList[ _elementStateCount - 1 ];
	}
    }


    protected final ElementState getPreviousState( ElementState state )
    {
	int index;

	index = _elementStateCount - 1;
	while ( index > 0 )
	{
	    if ( _elementStateList[ index ] == state )
		return _elementStateList[ index - 1 ];
	    -- index;
	}
	return null;
    }
    
    
    protected abstract boolean isHtml();
    
    
    /**
     * The '&amp;lt;' entity as text node. This entity is declared by default
     * for all XML/HTML documents.
     */
    private static final char[] _entityLt = new char[] { '<' };

    
    /**
     * The '&amp;gt;' entity as text node. This entity is declared by default
     * for all XML/HTML documents.
     */
    private static final char[] _entityGt = new char[] { '>' };

    
    /**
     * The '&amp;amp;' entity as text node. This entity is declared by default
     * for all XML/HTML documents.
     */
    private static final char[] _entityAmp = new char[] { '&' };

    
    /**
     * The '&amp;apos;' entity as text node. This entity is declared by default
     * for all XML documents.
     */
    private static final char[] _entityApos = new char[] { '\'' };

    
    /**
     * The '&quot;lt;' entity as text node. This entity is declared by default
     * for all XML/HTML documents.
     */
    private static final char[] _entityQuot = new char[] { '\"' };
    
    
    /**
     * Holds the DTD document for this document or null.
     */
    protected DocumentTypeImpl  _docType;
    

    /**
     * Holds the contents of text node collected between elements and other
     * non-textual nodes. Consists of all plain text and resolved entities.
     * <P>
     * This is a state variable. Methods that use it directly or indirectly
     * must be alert to its present state and usage and must reset it as
     * needed. General value on entry/exit is specified in the method comment.
     */
    protected FastString        _nodeText;


    private ElementState[]    _elementStateList = new ElementState[ 8 ];
    private int               _elementStateCount = 0;


    private AttrListImpl       _attrList =  new AttrListImpl();
    private FastString         _attrValue = new FastString( 32 );


    
    /**
     * Reference to document handler. Used to build the document contents
     * based on the SAX document handler. Can be null and can change
     * mid-parsing.
     */
    protected DocumentHandler   _documentHandler = _emptyHandler;
    

    /**
     * Reference to DTD handler. Used to build the document contents based
     * on the SAX DTD handler. Can be null and can change mid-parsing.
     */
    protected DTDHandler       _dtdHandler;


    /**
     * Reference to external entity resolver. Used to locate external
     * entitys based on the SAX entity resolver. Can be null and can change
     * mid-parsing.
     */
    protected EntityResolver    _entityResolver;


    /**
     * An empty handler does nothing when SAX events are thrown at it.
     * This is a requirement to get the parser working when no handler
     * is assocaited with it.
     */
    private static DocumentHandler _emptyHandler = new HandlerBase();


}


