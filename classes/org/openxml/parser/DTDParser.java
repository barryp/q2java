package org.openxml.parser;

/**
 * org/openxml/parser/DTDParser.java
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
import org.openxml.DTDDocument;
import org.openxml.DOMFactory;
import org.openxml.source.*;
import org.openxml.dom.*;
import org.openxml.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.openxml.io.Parser
 */
public class DTDParser
	extends BaseParser
{

	/**
	 * Different tokenizing rules for internal and external subsets.
	 */
	private boolean            _internal;


	/**
	 * Current nesting level of 'INCLUDE' conditional sections. Each 'INCLUDE'
	 * section increments this level by one. Note that an 'INCLUDE' section
	 * inside an 'IGNORE' section is counted as an ignore, not an include (see
	 * {@link #_ignoreLevel}). In other words, once {@link #_ignoreLevel} has
	 * been set to one, it will be incremented for each new section, whether
	 * include or ignore.
	 */
	private int                _includeLevel;


	/**
	 * Current nesting level of 'IGNORE' conditional sections. Each 'IGNORE'
	 * section increments this level by one. Note that an 'INCLUDE' section
	 * inside an 'IGNORE' section is counted as an ignore. In other words, if
	 * {@link #_ignoreLevel} is more than one, {@link #_ignoreLevel} must be
	 * decremented before {@link #_includeLevel} when closing a section.
	 */
	private int                _ignoreLevel;


	/**
	 * True if all DTD declarations should be ignored. This flag is only set
	 * if a PE appeared in the markup and could not be parsed into it. In this
	 * case, all following declarations must be ignored or the DTD might turn
	 * out invalid.
	 */
	private boolean            _ignoreAll;


	/**
	 * The document being processed as a DTDDocument (since _document is of
	 * type Document and casting can be annoying).
	 */
	private DTDDocument        _docType;


	public DTDParser( Reader reader, String sourceURI )
	{
		this( reader, sourceURI, MODE_DTD_PARSER, STOP_SEVERITY_FATAL );
	}
	public DTDParser( Reader reader, String sourceURI, short mode, short stopAtSeverity )
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
	public DTDParser( BaseParser owner, Reader reader, String sourceURI )
	{
		super( reader, sourceURI, owner.getMode(), STOP_SEVERITY_FATAL );
		if ( owner instanceof DTDParser )
		{
			_document = ( (DTDParser) owner )._document;
			_docType = ( (DTDParser) owner )._docType;
		}
		setErrorSink( owner );
	}
	private void allowMarkupPE()
		throws IOException
	{
		ParamEntity    pe;
		String        name;
		int            i;

		// Read character, if '%' then read PE and place it in push back buffer
		// enclosed in space, otherwise push back character.
		if ( readChar() == '%' )
		{
			if ( readTokenName() )
			{
				if ( readChar() != ';' )
					pushBack();
				name = _tokenText.toString();
				pe = _docType.findParamEntity( name );
				if ( pe == null )
					error( ERROR_WELL_FORMED, "Parameter entity '" + name + "' not found." );
				else
				if ( pe.isInternal() )
				{
					pushBack( ' ' );
					name = pe.getInternal();
					i = name.length();
					while ( i -- > 0 )
						pushBack( name.charAt( i ) );
					pushBack( ' ' );
				}
				else
					error( ERROR_WELL_FORMED, "Cannot used external parameter entity '" + name + "' inside markup." );
			}
			else
				pushBack( '%' );
		}
		else
			pushBack();
	}
	/**
	 * Returns true if parsed nodes and declarations can be declared and stored
	 * in the DTD. Declaration is prohibited when inside ignore conditional
	 * sections, or after an unparsed PE appared in the DTD.
	 *
	 * @return True if can declare parsed nodes and declarations in DTD
	 */
	private boolean canDeclare()
	{
		return ( ! _ignoreAll && _ignoreLevel == 0 );
	}
	/**
	 * Returns true if parsed nodes and declarations can be stored in the DTD.
	 * Storage is only allowed if declaration is allowed and in mode {@link
	 * #MODE_STORE_DTD}. In this mode declarations, comments and PIs will be
	 * stored under the document tree.
	 *
	 * @return True if can store parsed nodes and declarations in DTD
	 */
	private boolean canStore()
	{
		return ( isMode( MODE_STORE_DTD ) && ! _ignoreAll && _ignoreLevel == 0 );
	}
	protected Attr createAttribute( String attrName )
	{
		throw new IllegalStateException( "Unexpected call to 'createAttribute' for DTDParser." );
	}
	/**
	 * Returns true if parsing the internal DTD subset. External DTD subset, DTD
	 * documents and parameter entities will always return false. Different token
	 * rules apply for internal and external subsets, for example, use of PE
	 * inside markup and conditional sections.
	 *
	 * @return True if parsing internal DTD subset
	 */
	private boolean isInternal()
	{
		return _internal;
	}
	/**
	 * Parses a declaration into the current document. The declaration markup has
	 * been read and the declaration type ('ENTITY', 'ELEMENT', etc) is contained
	 * in {@link #_tokenText}. On exit, the entire declaration has been consumed
	 * and no valid value is contained in {@link #_curChar} or {@link #_tokenText}.
	 * This is also true if an invalid declaration has been parsed.
	 * <P>
	 * If the declaration is valid, a suitable node is created, appended to the
	 * document contents, and declared by calling one of the respective declaration
	 * methods. Note that multiple declarations with the same name may be added to
	 * the document contents, but only the first one is effectively declared.
	 * If {@link #canDeclare} returns false, declarations are ignored (see for more
	 * info).
	 * <P>
	 * The following rules govern how declarations are parsed:
	 * <UL>
	 * <LI>General entity declaration consists of entity name, followed by either
	 *  entity value, system identifier, or public and system identifiers; a node
	 *  of type {@link org.w3c.dom.Entity} is added to the document
	 * <LI>Unparsed entity declaration consists of entity name, followed by either,
	 *  system identifier, or public and system identifiers and notation name;
	 *  if named notation is not defined, an error is issued but the entity is still
	 *  declared; a node of type {@link org.w3c.dom.Entity} is added to the document
	 * <LI>Parameter entity declaration consists of entity name, followed by either
	 *  entity value, system identifier, or public and system identifiers; a node of
	 *  type {@link org.openxml.dom.ParamEntity} is added to the document
	 * <LI>Notation declaration consists of notation name, followed by system
	 *  identifier, or public identifier (with optional system identifier);
	 *  a node of type {@link org.w3c.dom.Notation} is added to the document
	 * <LI>Element declaration
	 * <LI>Attribute list declaration
	 * </UL>
	 *
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	private final void parseDeclaration()
		throws ParseException, IOException
	{
		String    name;
		String    systemId = null;
		String    publicId = null;
		String    notation;
		Node    node = null;
		boolean    param;

		// Read entity declaration. Both general and parameter entities are read
		// by the same code, with handling for internal, external and unparsed.
		// Syntax is as follows:
		// GeneralEntity := '<!ENTITY' S Name S ( EntityValue | ( ExternalID NDataDecl?) ) S? '>'
		// ParamEntity := '<!ENTITY' S '%' S Name S ( EntityValue | ExternalID ) S? '>'
		// EntityValue := Qt ( NotQt | EntityRef | PERef )* Qt
		// ExternalID := ( 'SYSTEM' S Qt Uri Qt ) | ( 'PUBLIC' S Qt PubID Qt S Qt System Qt )
		if ( _tokenText.equals( "ENTITY" ) )
		{
			// Consume whitespace. Look for percentage sign, consume some more
			// whitespace. Read token name, consume yet more white space.
			// Allow token name to be supplied with a parameter entity.
			while ( isSpace( readChar() ) )
				;
			param = ( _curChar == '%' );
			if ( param )
			{
				param = true;
				while ( isSpace( readChar() ) )
					;
			}
			pushBack();
			if ( readTokenName() )
			{
				// Entity name goes into name, white space is consumed.
				// What follows is one of the above: 'SYSTEM' with system
				// identifier, 'PUBLIC' with public identifier and system
				// identifier, or quote with entity value.
				name = _tokenText.toString();
				while ( isSpace( readChar() ) )
					;
				pushBack();

				if ( canReadName( "SYSTEM" ) )
				{
					while ( isSpace( readChar() ) )
						;
					pushBack();
					if ( readTokenQuotedPE() )
						systemId = _tokenText.toString();
					else
						error( ERROR_WELL_FORMED, "Entity declaration missing system identifier." );
				}
				else
				if ( canReadName( "PUBLIC" ) )
				{
					while ( isSpace( readChar() ) )
						;
					pushBack();
					if ( readTokenQuotedPE() )
					{
						publicId = _tokenText.toString();
						while ( isSpace( readChar() ) )
							;
						pushBack();
						if ( readTokenQuotedPE() )
							systemId = _tokenText.toString();
						else
							error( ERROR_WELL_FORMED, "Entity declaration missing system identifier." );
					}
					else
						error( ERROR_WELL_FORMED, "Entity declaration missing public identifier." );
				}
				// Entity declaration is followed by quoted value. If quoted value
				// not found, issue an error, otherwise declare either parameter or
				// general entity.
				else
				{
					// The HTML DTD uses this convention, so swallow the 'CDATA'
					// followed by whitespaces.
					if ( canReadName( "CDATA" ) )
					{
						while ( isSpace( readChar() ) )
							;
						pushBack();
					}
					if ( readTokenQuoted() )
					{
						if ( canDeclare() )
						{
							if ( param )
								node = _docType.createParamEntity( name, _tokenText.toString() );
							else
							{
								parseTokenPE();
								node = _docType.createEntity( name, _tokenText.toString() );
							}
						}
					}
					else
						error( ERROR_WELL_FORMED, "Entity is improperly declared (missing quoted value or external identifiers)." );
				}

				// Proceed if external entity defined properly. If entity is internal,
				// it has been handled before. If entity is external, it is properly
				// defined if the system identifier is known. An external parameter
				// entity is declared as such. An external general entity might have
				// a notation specification, making it an unparsed entity and declared
				// as such.
				if ( systemId != null && canDeclare() )
				{
					if ( param )
						node = _docType.createParamEntity( name, systemId, publicId );
					else
					{
						while ( isSpace( readChar() ) )
							;
						pushBack();
						if ( canReadName( "NDATA" ) )
						{
							while ( isSpace( readChar() ) )
								;
							pushBack();
							if ( readTokenQuotedPE() )
							{
								notation = _tokenText.toString();
								node = _docType.createEntity( name, systemId, publicId, notation );
								if ( _docType.findNotation( notation ) == null )
									error( ERROR_WELL_FORMED, "Unparsed entity declaration refers to an undeclared notation." );
							}
							else
								error( ERROR_WELL_FORMED, "Unparsed entity declaration missing notation identifier." );
						}
						else
							node = _docType.createEntity( name, systemId, publicId );
					}
				}

				// If node is not null, an entity (general or parameter) has been
				// properly defined and is appended to the document contents and
				// declared.
				if ( node != null && canDeclare() )
				{
					if ( canStore() )
						_document.appendChild( node );
					if ( param  )
						_docType.declareParamEntity( (ParamEntity) node );
					else
						_docType.declareEntity( (EntityImpl) node );
				}
			}
			else
				error( ERROR_WELL_FORMED, "Entity declaration missing proper entity name." );
		}
		else

		// Notation := '<!NOTATION' S Name S ( 'SYSTEM' S Qt Uri Qt ) |
		//             ( 'PUBLIC' S PublicId ( S Qt Uri Qt )? ) S? '>'
		// PublicId := Qt ( #x20 | #xD | #xA [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_] )* Qt
		if ( _tokenText.equals( "NOTATION" ) )
		{
			// Consume whitespace, followed by proper notation name and another
			// whitespace. Allow PE reference to provide notation name (legal?)
			while ( isSpace( readChar() ) )
				;
			pushBack();
			allowMarkupPE();
			if ( readTokenName() )
			{
				name = _tokenText.toString();
				while ( isSpace( readChar() ) )
					;
				pushBack();

				// What comes next is either the system or public identifier.
				// One of the two is required. Public identifier may be followed
				// by optional system identifier.
				if ( canReadName( "SYSTEM" ) )
				{
					while ( isSpace( readChar() ) )
						;
					pushBack();
					if ( readTokenQuotedPE() )
						systemId = _tokenText.toString();
					else
						error( ERROR_WELL_FORMED, "Notation declaration missing system identifier." );
				}
				else
				if ( canReadName( "PUBLIC" ) )
				{
					while ( isSpace( readChar() ) )
						;
					pushBack();
					if ( readTokenQuotedPE() )
					{
						publicId = _tokenText.toString();
						while ( isSpace( readChar() ) )
							;
						pushBack();
						if ( readTokenQuotedPE() )
							systemId = _tokenText.toString();
					}
					else
						error( ERROR_WELL_FORMED, "Notation declaration missing public identifier." );
				}

				// If all well, create a new notation node, append it to the
				// DTD node and declare it in the DTD.
				if ( systemId != null || publicId != null )
				{
					node = _docType.createNotation( name, systemId, publicId );
					if ( canDeclare() )
					{
						if ( canStore() )
							_document.appendChild( node );
						_docType.declareNotation( (Notation) node );
					}
				}
			}
			else
				error( ERROR_WELL_FORMED, "Notation declaration missing proper notation name." );
		}
		else

		if ( _tokenText.equals( "ELEMENT" ) )
		{
		}
		else
		if ( _tokenText.equals( "ATTLIST" ) )
		{
		}
		else
			error( ERROR_WELL_FORMED, "Unknown declaration type." );

		// Skip the end of the declaration. The HTML DTD may contain declaration
		// comments, so skip them as well. Skip everything up to and including the
		// terminating '>'.
		skipDeclComment();
		readChar();
		while ( _curChar != EOF && _curChar != '>' )
		{
			skipDeclComment();
			readChar();
		}
		if ( _curChar == EOF )
			error( ERROR_WELL_FORMED, "DTD declaration terminated prematurely." );
	}
	public Document parseDocument()
		throws ParseException
	{
		int    token;

		_docType = DOMFactory.createDTDDocument();
		_document = _docType;
		_internal = false;
		try
		{
			token = readTokenDTD();
// ! JDK 1.2 !
//            if ( token == TOKEN_PI && _tokenText.length() > 3 &&
//                 _tokenText.substring( 0, 3 ).equals( "xml" ) )
// ! JDK 1.1 !
			if ( token == TOKEN_PI && _tokenText.length() > 3 &&
				 _tokenText.startsWith( "xml" ) )
			{
				// Parse version and encoding from text declaration.
				parseDocumentDecl( false );
				token = readTokenDTD();
			}
			 while ( parseNextDecl( token ) )
				token = readTokenDTD();
		}
		// Case of ParseException, throw it out. Case of runtime or other exception,
		// issue a FatalParseException. In either case, close the input stream when
		// done with it.
		catch ( ParseException except )
		{
			throw except;
		}
		catch ( RuntimeException except )
		{
			error( except );
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
	public DTDDocument parseExternalSubset( DTDDocument dtd )
		throws ParseException
	{
		int    token;

		// Just to be on the safe side.
		if ( dtd == null )
			throw new NullPointerException( "Argument 'dtd' is null." );
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );

		_document = dtd;
		_docType = dtd;
		_internal = false;
		try
		{
			token = readTokenDTD();
			 while ( parseNextDecl( token ) )
				token = readTokenDTD();
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Do not close the parser, XML content will
		// follow.
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
		return (DTDDocument) _document;
	}
	public DTDDocument parseInternalSubset( DTDDocument dtd )
		throws ParseException
	{
		int    token;

		// Just to be on the safe side.
		if ( dtd == null )
			throw new NullPointerException( "Argument 'dtd' is null." );
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );

		_document = dtd;
		_docType = dtd;
		_internal = true;
		try
		{
			token = readTokenDTD();
			 while ( parseNextDecl( token ) )
				token = readTokenDTD();
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Do not close the parser, XML content will
		// follow.
		catch ( ParseException except )
		{
			throw except;
		}
		catch ( Exception except )
		{
			error( except );
		}
		return (DTDDocument) _document;
	}
	private boolean parseNextDecl( int token )
		throws ParseException, IOException
	{
		Node            node;
		int             i;
		char            ch;
		String          name = null;
		String          value;
//        boolean         optionalOpen = false;
//        boolean         optionalClose = false;
		boolean         param;
		char            quote;
		StringBuffer    buffer;
		String          systemID;
		String          publicID;
		ParamEntity     pe;

		// If met the end of the file or internal section, return false and stop
		// parsing the document. This is the place to look for open condition
		// sections.
		if ( token == TOKEN_EOF )
		{
			if ( isInternal() && ( _ignoreLevel > 0 || _includeLevel > 0 ) )
				error( ERROR_WELL_FORMED, "Conditional section terminated prematurely at end of file." );
			return false;
		}

		// If met end of conditional section, decrement the section level. If no
		// matching open section, issue an error. Section nesting is counted by
		// _includeLevel and then by _ignoreLevel, see for more information.
		if ( token == TOKEN_SECTION_END )
		{
			if ( _ignoreLevel > 0 )
				-- _ignoreLevel;
			else
			if ( _includeLevel > 0 )
				-- _includeLevel;
			else
				error( ERROR_WELL_FORMED, "Unbalanced ']]>' does not match an open conditional section." );
		}
		else
		// If a PE found inbetween markup, it is parsed as if it makes part of
		// the markup. Note that only internal PEs are allowed in the internal
		// subset. The external PE need only be parsed once as it's declarations
		// are added to this DTD. If it has been parsed before, just skip it.
		// If it was not found, do not parse any other declarations afterwards.
		if ( token == TOKEN_PE_REF )
		{
			name = _tokenText.toString();
			pe = _docType.findParamEntity( name );
			if ( pe == null )
				error( ERROR_WELL_FORMED, "Parameter entity '" + name + "' not found." );
			else
			if ( isInternal() && ! pe.isInternal() )
				error( ERROR_WELL_FORMED, "External parameter entity '" + name + "' cannot be used in internal subset." );
			else
			if ( pe.getState() == ParamEntity.STATE_DECLARED )
			{
				parseParamEntity( pe );
				if ( pe.getState() == ParamEntity.STATE_NOT_FOUND )
					_ignoreAll = true;
			}
		}
		else
		// Token is a comment. Store it in the document, only if the mode in effect
		// commands storing comments in the document tree. Comment contents is
		// assured not to contain any '-->'.
		if ( token == TOKEN_COMMENT )
		{
			if ( canStore() && isMode( MODE_STORE_COMMENT ) )
			{
				node = _document.createComment( _tokenText.toString() );
				_document.appendChild( node );
			}
		}
		else
		// Token is a processing instruction. Store in the document tree, only
		// if the mode in effect commands storing processing instructions.
		// Processing instruction contents is assured not to contain any '?>'.
		if ( token == TOKEN_PI )
		{
			if ( canStore() && isMode( MODE_STORE_PI ) )
			{
				name = slicePITokenText();
				node = _document.createProcessingInstruction( name, _tokenText.toString() );
				_document.appendChild( node );
			}
		}
		else
		// Case of '<[[' requires special handling. If it is a CDATA, an error is
		// issued since CDATA sections do not apply to DTDs. If internal DTD,
		// condition sections are not allowed either.
		if ( token == TOKEN_SECTION || token == TOKEN_CDATA )
		{
			if ( token == TOKEN_CDATA )
				error( ERROR_WELL_FORMED, "CDATA section not supported in DTD." );
			else
			if ( isInternal() )
				error( ERROR_WELL_FORMED, "Conditional sections not supported in internal DTD subset." );
			else
			{
				allowMarkupPE();
				while ( isSpace( readChar() ) )
					allowMarkupPE();
				pushBack();
				readTokenName();
				if ( _tokenText.equals( "IGNORE" ) )
					++ _ignoreLevel;
				else
				{
					// Incremenent the last level to prevent an error being
					// reported on exit.
					if ( ! _tokenText.equals( "INCLUDE" ) )
						error( ERROR_WELL_FORMED, "Conditional section missing 'INCLUDE' or 'IGNORE' directive." );
					if ( _ignoreLevel > 0 )
						++ _ignoreLevel;
					else
						++ _includeLevel;
				}

				while ( isSpace( readChar() ) )
					;
				if ( _curChar != '[' )
				{
					error( ERROR_WELL_FORMED, "Condition section missing opening '['." );
					pushBack();
				}
			}
		}
		else
		// Case of '<!' is a declaration. The declaration is parsed and added to
		// the DTD by the parseDeclaration() method.
		if ( token == TOKEN_DTD )
			parseDeclaration();
		else
		// Case of all other tokens which are not valid in the DTD: TOKEN_OPEN_TAG,
		// TOKEN_CLOSE_TAG, TOKEN_ENTITY_REF or TOKEN_TEXT. All are regarded as
		// text that does not belong here (whitespaces in the DTD are gracefully
		// ignored).
		if ( token == TOKEN_TEXT && isTokenAllSpace() )
			;
		else
			error( ERROR_WELL_FORMED, "Character data not supported in DTD subset." );
		return true;
	}
	public final Node parseNode( Node node )
		throws ParseException
	{
		throw new IllegalStateException( "Node parsing not supported by DTDParser." );
	}
	/**
	 * Parses the parameter entity, returning the entity as parsed. An existing
	 * {@link org.openxml.dom.ParamEntity} is passed to the method. On exit,
	 * the same entity (parsed) is returned, or null to indicate that the entity
	 * could not be parsed.
	 * <P>
	 * The following rules govern how the entity is parsed:
	 * <UL>
	 * <LI>If the entity's state is {@link ParamEntity#STATE_PARSED}, then the
	 *  entity has been parsed before, and is returned.
	 * <LI>If the entity's state is {@link ParamEntity#STATE_NOT_FOUND}, then
	 *  the entity could not be found, and null is returned. There is no need
	 *  to issue an error again.
	 * <LI>If the entity's state is {@link ParamEntity#STATE_PARSING}, then the
	 *  entity is being parsed: this is a circular reference, an error is issued
	 *  and null is returned.
	 * <LI>If the entity's state is {@link ParamEntity#STATE_DECLARED}, then the
	 *  entity is being parsed. For an external entity, the entity source is being
	 *  located using {@link HolderFinder}. If the entity source could
	 *  not be found or could not be opened, the entity state is set to {@link
	 *  ParamEntity#STATE_NOT_FOUND}, an error is issued and null returned.
	 *  For an internal entity, the entity source is created from it's value.
	 * <LI>If the entity's state is {@link ParamEntity#STATE_DECLARED} and the
	 *  entity source could be located, an {@link DTDParser} is created and used
	 *  to parse the entity. If no fatal errors are encountered when parsing,
	 *  the entity is returned. Well formed errors are treated as if generated
	 *  by the current parser.
	 * <LI>If the entity's state is {@link ParamEntity#STATE_DECLARED} and a fatal
	 *  error was issued while parsing the entity with an {@link DTDParser}, then
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
	protected final ParamEntity parseParamEntity( ParamEntity entity )
		throws ParseException
	{
		Holder      holder;
		Source      source;
		DTDParser   parser;
		Reader      reader = null;

		switch ( entity.getState() )
		{
		// Entity has been parsed before and apparently successfuly, so just
		// return it.
		case ParamEntity.STATE_PARSED:
			return entity;

		// Entity could not be found. Meaning that an attempt to parse the entity
		// was taken in the past, but the entity source could not be found. Do not
		// report an error again, just return null.
		case ParamEntity.STATE_NOT_FOUND:
			return null;

		// Entity is now being parsed. This can only imply one thing: a circular
		// reference. Returning null indicates that Entity could not be parsed.
		case ParamEntity.STATE_PARSING:
			error( ERROR_WELL_FORMED, "Parameter entity '" + entity.getNodeName() + "' contains circular reference." );
			return null;

		// Entity has not been parsed, yet. Attempt to locate the entity source,
		// parse it into the entity and return the parsed entity. Change the
		// entity's state to one of the above flags.
		case ParamEntity.STATE_DECLARED:
			// If entity is internal, create a parser for it's value. If entity
			// is external, get it's URL and open a connection to that URL.
			// If source could not be found or opened, entity becomes not found,
			// an error is issued and null returned.
			try
			{
				if ( entity.isInternal() )
				{
					reader = new StringReader( entity.getInternal() );
					parser = new DTDParser( this, reader, getSourceURI() + " Entity: " + entity.getNodeName() );
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
					source.setDocClass( Source.DOCUMENT_DTD );
					holder = DOMFactory.getHolderFinder().findHolder( source, false );
					if ( holder != null )
						reader = holder.getReader();
					if ( reader == null || holder == null )
					{
						entity.setState( ParamEntity.STATE_NOT_FOUND );
						error( ERROR_WELL_FORMED, "External parameter entity '" + entity.getNodeName() + "' could not be found." );
						return null;
					}
					parser = new DTDParser( this, reader, source.toString() );
				}

				// Switch the entity state to parsing, to detect circular references.
				// Parse the entity and switch it to parsed. If a fatal (or IO) error
				// is encountered when parsing, the exception will change the state
				// to not found. Since this parser is the error sink for the external
				// entity parser, all errors are reported directly to this parser.
				entity.setState( ParamEntity.STATE_PARSING );
				parser.parseParamEntity( _docType, entity.isInternal() );
				entity.setState( ParamEntity.STATE_PARSED );
			}
			catch ( ParseException except )
			{
				entity.setState( ParamEntity.STATE_NOT_FOUND );
				return null;
			}
			catch ( IOException except )
			{
				entity.setState( ParamEntity.STATE_NOT_FOUND );
				error( ERROR_WELL_FORMED, "External parameter entity '" + entity.getNodeName() + "' could not be found. Reason:" +
										  except.getMessage() );
				return null;
			}
			return entity;

		default:
			// Entity should never be in an undetermined state.
			throw new IllegalStateException( "Parameter entity in illegal state." );
		}
	}
	public DTDDocument parseParamEntity( DTDDocument dtd, boolean internal )
		throws ParseException
	{
		int    token;

		// Just to be on the safe side.
		if ( dtd == null )
			throw new NullPointerException( "Argument 'dtd' is null." );
		if ( isClosed() )
			throw new IllegalStateException( "Parser has been closed and may not be used anymore." );
		_document = dtd;
		_docType = dtd;
		_internal = false;
		try
		{
			token = readTokenDTD();
// ! JDK 1.2 !
//            if ( internal && token == TOKEN_PI && _tokenText.length() > 3 &&
//                 _tokenText.substring( 0, 3 ).equals( "xml" ) )
// ! JDK 1.1 !
			if ( internal && token == TOKEN_PI && _tokenText.length() > 3 &&
				 _tokenText.startsWith( "xml" ) )
			{
				parseDocumentDecl( false );
				token = readTokenDTD();
			}
			 while ( parseNextDecl( token ) )
				token = readTokenDTD();
		}

		// Catch parsing exception and rethrow them. This catch clause is only
		// necessary because of the second, more general catch clause that catches
		// other forms of exceptions: I/O exceptions, runtime exceptions, or even
		// node not supporting children. Do not close the parser, XML content will
		// follow.
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
		return (DTDDocument) _document;
	}
	private void parseTokenPE()
		throws ParseException
	{
		int            index;
		int            end;
		String        name;
		ParamEntity    pe;

		for ( index = 0 ; index < _tokenText.length() ; ++ index )
		{
			if ( _tokenText.charAt( index ) == '%' )
			{
				end = index + 1;
				if ( end < _tokenText.length() &&
					 isNamePart( _tokenText.charAt( end ), true ) )
				{
					++ end;
					while ( end < _tokenText.length() &&
							isNamePart( _tokenText.charAt( end ), false ) )
						++ end;
// ! JDK 1.2 !
					// name = _tokenText.substring( index + 1, end );
// ! JDK 1.1 !
					name = _tokenText.toString( index + 1, end );
					if ( end < _tokenText.length() && _tokenText.charAt( end ) == ';' )
						++end;
					pe = _docType.findParamEntity( name );
					if ( pe == null )
						error( ERROR_WELL_FORMED, "Parameter entity '" + name + "' not found." );
					else
					if ( ! pe.isInternal() )
						error( ERROR_WELL_FORMED, "Cannot used external parameter entity '" + name + "' inside markup." );
					else
					{
// ! JDK 1.2 !
						 _tokenText.replace( index, end, pe.getInternal() );
//                        _tokenText = StringUtil.replace( _tokenText, index, end, pe.getInternal() );
//                                    new StringBuffer( _tokenText.toString(), 0, index ) ).
//                                        append( pe.getInternal() ).append( _tokenText.toString() end ) );
// ! JDK 1.1 !
						-- index;
					}
				}
			}
		}
	}
/*
	public final void parseAllEntities()
	{
		Enumeration        enum;
		ParamEntity        param;
		NamedNodeMap    map;
		int                i;

		if ( _docType != null )
			synchronized ( docType )
			{
				if ( keepParam && _docType.getParamEntities() != null )
				{
					enum = _docType.getParamEntities();
					while ( enum.hasMoreElements() );
					{
						param = (ParamEntity) enum.nextElement();
						if ( param.getState() == ParamEntity.STATE_DECLARED )
							parseParamEntity( param );
					}
				}
				map = _docType.getEntities();
				if ( map != null )
				{
					for ( i = 0 ; i < map.getLength() ; ++i )
					{
						entity = (Entity) map.item( i );
						if ( entity instanceof EntityImpl &&
							 ( (EntityImpl) entity ).getState() == EntityImpl.STATE_DECLARED )
							parseGeneralEntity( entity );
					}
				}
			}

	}
*/

	private final int readTokenDTD()
		throws ParseException, IOException
	{
		// Read a single character and try to ascertain what comes next.
		// If character is less-than, it might be one of several markup tokens.
		// If character is a percentage, it might be an entity reference.
		// Otherwise, character is plain text.
		readChar();
		if ( _curChar == '<' )
			return readTokenMarkup();
		else
		if ( _curChar == '%' )
			return readTokenPERef();
		else
		if ( _curChar == EOF )
			return TOKEN_EOF;

		// Found a ']'. In the internal subset, this terminates the subset.
		// In the external subset, this might terminate a condition section
		// (must be ']]>'). Otherwise, it's just text.
		if ( _curChar == ']' )
		{
			if ( isInternal() )
			{
				pushBack();
				return TOKEN_EOF;
			}
			else
			{
				if ( readChar() == ']' )
				{
					if ( readChar() == '>' )
						return TOKEN_SECTION_END;
					pushBack();
					_curChar = ']';
				}
				pushBack();
				_curChar = ']';
			}
			_tokenText.setLength( 1 );
			_tokenText.setCharAt( 0, ']' );
			return TOKEN_TEXT;
		}

		// Read as much plain text as possible and accumulate it in _tokenText,
		// return textual token.
		_tokenText.setLength( 0 );
		while ( _curChar != EOF && _curChar != '<' && _curChar != '%' &&
				_curChar != ']' )
		{
			_tokenText.append( (char) _curChar );
			readChar();
		}
		if ( _curChar != EOF )
			pushBack();
		return TOKEN_TEXT;
	}
	private boolean readTokenQuotedPE()
		throws ParseException, IOException
	{
		if ( readTokenQuoted() )
		{
			parseTokenPE();
			return true;
		}
		return false;
	}
	/**
	 * Skips declaration comment. Declaration comments can appear inside the
	 * declaration, terminated with a double hyphen ('--') on both sides.
	 * They are specifically supported in the HTML DTD. The variable {@link
	 * #_curChar} holds no valid value on entry or exit.
	 */
	private void skipDeclComment()
		throws ParseException, IOException
	{
		if ( readChar() == '-' )
		{
			if ( readChar() == '-' )
			{
				while ( readChar() != EOF )
				{
					if ( _curChar == '-' && readChar() == '-' )
						return;
				}
				error( ERROR_WELL_FORMED, "Declaration comment terminated permaturely." );
			}
			pushBack();
			_curChar = '-';
		}
		pushBack();
	}
}