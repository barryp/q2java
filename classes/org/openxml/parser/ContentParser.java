package org.openxml.parser;

/**
 * org/openxml/parser/ContentParser.java
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
import org.openxml.DTDDocument;
import org.openxml.dom.*;
import org.openxml.util.*;


/**
 * Extends {@link BaseParser} with layer 2 parsing method that are shared by
 * {@link XMLParser} and {@link HTMLParser}.
 * <P>
 * {@link #readTokenContent} reads and returns a token that applies only to
 * content documents (1/HTML/Entities) and {@link #parseAttributes} is used
 * to parse attributes after the open tag token has been read. {@link
 * #parseAttrEntity} parses an entity reference appearing in an attribute value
 * and {@linnk #parseContentEntity} parses an entity reference appearing in the
 * document content.
 * <P>
 * {@link #_currentNode} references the currently processed node and {@link
 * #_docType} points to the document DTD, if one exists.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see BaseParser
 */
abstract class ContentParser
	extends BaseParser
{

	
	/**
	 * The current parsed element. This is intentionally a node and not an element,
	 * so the document contents can be read even if not encased in an element
	 * (for example, when parsing an entity).
	 * 
	 * @see XMLParser#parseNextNode
	 * @see HTMLParser#parseNextNode
	 */
	protected Node            _currentNode;    
	

	/**
	 * Holds the DTD document for this document or null.
	 */
	protected DTDDocument    _docType;
	

	/**
	 * The '&amp;lt;' entity as text node. This entity is declared by default
	 * for all XML/HTML documents and returned by {@link #getEntityContents}.
	 * The entity is created once when first used by an instance of the parser.
	 */
	private Text            _entityLt;

	
	/**
	 * The '&amp;gt;' entity as text node. This entity is declared by default
	 * for all XML/HTML documents and returned by {@link #getEntityContents}.
	 * The entity is created once when first used by an instance of the parser.
	 */
	private Text            _entityGt;

	
	/**
	 * The '&amp;amp;' entity as text node. This entity is declared by default
	 * for all XML/HTML documents and returned by {@link #getEntityContents}.
	 * The entity is created once when first used by an instance of the parser.
	 */
	private Text            _entityAmp;

	
	/**
	 * The '&amp;apos;' entity as text node. This entity is declared by default
	 * for all XML/HTML documents and returned by {@link #getEntityContents}.
	 * The entity is created once when first used by an instance of the parser.
	 */
	private Text            _entityApos;

	
	/**
	 * The '&quot;lt;' entity as text node. This entity is declared by default
	 * for all XML/HTML documents and returned by {@link #getEntityContents}.
	 * The entity is created once when first used by an instance of the parser.
	 */
	private Text            _entityQuot;

	
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
	protected ContentParser( Reader reader, String sourceURI, short mode, short stopAtSeverity )
	{
		super( reader, sourceURI, mode, stopAtSeverity );
	}
	/**
	 * Find the named entity in the DTD and return its parsed contents. The caller
	 * would then duplicate the entity contents and replace the reference with the
	 * duplicated contents. The return value is the first node of the entity
	 * contents.
	 * <P>
	 * Five entities are defined by default for all documents ('&amp;lt;',
	 * '&amp;gt;', '&amp;amp;', '&amp;apos;' and '&amp;quot;') and always exist,
	 * even if no DTD is specified.
	 * <P>
	 * If no DTD is specified for the document, an error is issued and an {@link
	 * EntityReference} node is returned. If the entity is an unparsed entity, or
	 * an external entity used in an attribute value, an error is issued and null
	 * is returned. If the entity could not be found or parsed successfuly, an
	 * error is issued and null is returned. All errors that occur while parsing
	 * the entity are appended to the current parser's error list.
	 * 
	 * @param name The name of the entity to return
	 * @param inAttr True if entity reference appears inside attribute value
	 * @return The parsed entity's content or null
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final Node getEntityContents( String name, boolean inAttr )
		throws ParseException
	{
		EntityImpl    entity;
		
		// There five entities are implicitly declared for all XML and HTML
		// documents and need not appear in the DTD.
		if ( name.equals( "lt" ) )
		{
			if ( _entityLt == null )
				_entityLt = _document.createTextNode( "<" );
			return _entityLt;
		}
		if ( name.equals( "gt" ) )
		{
			if ( _entityGt == null )
				_entityGt = _document.createTextNode( ">" );
			return _entityGt;
		}
		if ( name.equals( "amp" ) )
		{
			if ( _entityAmp == null )
				_entityAmp = _document.createTextNode( "&" );
			return _entityAmp;
		}
		if ( name.equals( "apos" ) )
		{
			if ( _entityApos == null )
				_entityApos = _document.createTextNode( "'" );
			return _entityApos;
		}
		if ( name.equals( "quot" ) )
		{
			if ( _entityQuot == null )
				_entityQuot = _document.createTextNode( "\"" );
			return _entityQuot;
		}

		if ( _docType != null )
		{
			// Locate the entity in the DTD. If entity could not be parsed, delete
			// it from the DTD. This will create a new empty entity in its place.
			entity = _docType.findEntity( name );
			if ( entity != null )
			{
				if ( entity.isUnparsed() )
				{
					if ( inAttr )
						error( ERROR_WELL_FORMED, "Unparsed general entity '" + _tokenText + "' cannot be used in attribute value." );
					else
						error( ERROR_WELL_FORMED, "Unparsed general entity '" + _tokenText + "' cannot be used in document content." );
				}
				else
				if ( inAttr && ! entity.isInternal() )
					error( ERROR_WELL_FORMED, "External general entity '" + _tokenText + "' cannot be used in attribute value." );
				else
				if ( parseGeneralEntity( entity ) != null )
					return entity.getFirstChild();
			}
			else
				error( ERROR_WELL_FORMED, "General entity '" + name + "' not declared." );
			return _document.createEntityReference( name );
		}
		else
			error( ERROR_WELL_FORMED, "General entity '" + name + "' not declared, DTD missing." );
		return _document.createEntityReference( name );
	}
	/**
	 * Parses an internal general entity into an attribute value. Called by
	 * {@link #parseAttributes} when an entity reference is encountered in the
	 * attribute value. {@link #_tokenText} contains the entity name on entry
	 * and the parsed value on exit.
	 * <P>
	 * The following rules govern how the entity is parsed:
	 * <UL>
	 * <LI>If the entity is external or unparsed general, an error is issued
	 * <LI>If the entity could not be found or could not be parsed, an error is
	 *  issued (note that internal entities are parsed from memory, so an I/O
	 *  exception should not occur)
	 * <LI>If the parsed entity contains text ({@link org.w3c.dom.Text} or
	 *  {@link org.w3c.dom.CDATASection}), it is appended to the attribute value
	 * <LI>If the parsed entity contains comments and processing instructions,
	 *  they are ignored
	 * <LI>If the parsed entity contains anything else, the entity could not be
	 *  used for an attribute value: a well formed error is issued and parsing
	 *  stops
	 * <LI>If the parsed entity contains an entity reference that could not be
	 *  parsed, a second error is issued to report it and parsing stops
	 * </UL>
	 * 
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final void parseAttrEntity()
		throws ParseException
	{
		Node            node;

		// Get the contents of the entity, valid for attribute values.
		node = getEntityContents( _tokenText.toString(), true );
		_tokenText.setLength( 0 );
		while ( node != null )
		{
			// Entity contains text and CDATA sections: append text to attribute
			// value. Entity contains comment and processing instructions: ignore
			// them. Entity reference is stored as is, everything else is ignored.
			if ( node instanceof Text || node instanceof CDATASection )
				_tokenText.append( ( (CharacterData) node ).getData() );
			else
			// 
			if ( node instanceof Comment || node instanceof ProcessingInstruction )
				;
			else
			if ( node instanceof EntityReference )
			{
				// Only came so far if entity could not be resolved by entity
				// parser and assuming that a suiteable error has already been
				// reported. This error is specific to attribute values.
				_tokenText.append( '&' ).append( node.getNodeName() ).append( ';' );
				error( ERROR_WELL_FORMED, "Could not recursively parse entity '" + _tokenText + "' for use in attribute value." );
				return;
			}
			else
			{
				error( ERROR_WELL_FORMED, "Non-textual entity '" + _tokenText + "' cannot be used in attribute value." );
				return;
			}
			node = node.getNextSibling();
		}
	}
	/**
	 * Parses the attribute list of an XML/HTML open tag and stores attribute
	 * values for that element. Returns false if the element is empty (XML only),
	 * true if non-empty (open tag with expected closing tag, or HTML tag).
	 * <P>
	 * On entry the element's tag name and optional whitespaces have been read.
	 * On exit, all attributes have been read including the closing mark '&gt'
	 * (for XML documents, also '/&gt;>). No valid character is held in {@link
	 * #_curChar} on entry or on exit.
	 * <P>
	 * The following rules govern how attributes are parsed and stored:
	 * <UL>
	 * <LI>Attribute name must be a valid name token (see {@link
	 *  BaseParser#isNamePart}).
	 * <LI>Attribute name separated from attribute value by equal sign and optional
	 *  whitespaces on either side. If attribute value is missing, the default value
	 *  or an empty string is assumed.
	 * <LI>Attribute value may be enclosed in single or double quotes. If attribute
	 *  value is not enclosed in quotes, it is terminated with next whitespace or
	 * '&gt' sign. A well-formed error is issued for XML documents if the attribute
	 *  value is not enclosed in quotes.
	 * <LI>Character references and entity references are replaced for the attribute
	 *  value. For XML documents, only internal entities are recognized as valid in
	 *  the attribute value. If an undeclared internal entity is used, an error is
	 *  issued and it's name is stored verbatim.
	 * <LI>For XML documents only, the attribute value is stored as {@link
	 *  org.w3c.dom.Text} and {@link org.w3c.dom.EntityReference} nodes under the
	 *  {@link org.w3c.dom.Attr} parent node.
	 * <LI>If an attribute name is repeated, the first value takes effect and
	 *  an error is issued. The second value is parsed, but not stored.
	 * </UL>
	 * 
	 * @param elem The element being parsed
	 * @return True if an open tag is parsed, false if an empty tag (XML documents
	 *  only)
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 */
	protected final boolean parseAttributes( Element elem, boolean xml )
		throws ParseException, IOException
	{
		char            quote;
		Attr            attr;
		String          attrName;
		FastString      attrValue = null;
//      StringBuffer    attrText = null;
		
		// Whitespaces after tag name have been read, so first attribute can be
		// read immediately, if not the end of the tag.
		readChar();
		while ( _curChar != EOF )
		{
			
			// If next character is '>' or '/>', conclude reading attributes and
			// return true for a full element, false for an empty element. Note
			// that '/>' is only supported in XML documents. Also handle case of
			// unexpected '<'.
			if ( _curChar == '>' )
				return true;
			else
				
			if ( xml && _curChar == '/' )
			{
				if ( readChar() == '>' )
					return false;
				pushBack();
			}
			else
			if ( _curChar == '<' )
			{
				error( ERROR_WELL_FORMED, "Element's attribute list terminated prematurely." );
				pushBack();
				return true;
			}
			else
				
			// An attribute must start with a valid attribute name. Read as much of
			// the name as possible.
			if ( isNamePart( _curChar, true ) )
			{
				// Read the valid attribute name.
				_tokenText.setLength( 1 );
				_tokenText.setCharAt( 0, (char) _curChar );
				while ( isNamePart( readChar(), false ) )
					_tokenText.append( (char) _curChar );
				// Create the named attribute (initially with an empty string value,
				// to make sure it has a specified value and therefore is not
				// confused with a default attribute value). If such an attribute
				// already exists, issue an error but parse it still. Note that an
				// attribute may exist by definition, but it's specified value will
				// be false.
				attrName = _tokenText.toString();
				attr = _document.createAttribute( attrName );
				if ( elem.getAttributeNode( attrName ) != null &&
					 elem.getAttributeNode( attrName ).getSpecified() )
					error( ERROR_WELL_FORMED, "Attribute '" + attrName + "' defined twice for this element." );
				else
					elem.setAttributeNode( attr );
				
				// Consume white space following attribute value. Expect equal sign
				// and more optional whitespace to follow for value. Otherwise,
				// attribute has an empty value.
				while ( isSpace( _curChar ) )
					readChar();
				if ( _curChar == '=' )
				{
					
					while ( isSpace( readChar() ) )
						;
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
						if ( xml )
							error( ERROR_WELL_FORMED, "Attribute value not enclosed with quotes." );
						quote = SPACE;
					}
					// Read the attribute's value until the next separator character
					// (quote, white space, or '>'). The parsed value is accumulated
					// in attrValue. The last text part is accumulated in attrText,
					// made into Text nodes (XML documents only) and stored as an
					// attribute child, interwined with EntityReference nodes.
					if ( attrValue == null )
						attrValue = new FastString( 32 );
					else
						attrValue.setLength( 0 );
/*
					if ( attrText == null )
						attrText = new StringBuffer( 8 );
					else
						attrText.setLength( 0 );
*/                  int startLastText = 0;
					
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
						else
						// When separator is a space, '>' or '/>', finish reading
						// the attribute value and leave the last character unread,
						// so it may be processed by the next iteration of the outer
						// loop, since continue comes next.
						if ( quote == SPACE )
						{
							if ( isSpace( _curChar ) || _curChar == '>' )
								break;
							if ( xml && _curChar == '/' )
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
								pushBack();
								attrValue.append( '/' );
//                              attrText.append( '/' );
							}
						}
						else
						// '<' not allowed in attribute value and means the end of
						// the attribute value and this tag. No excuses. This will
						// terminate the inner loop, then the output loop will
						// conclude processing this element.
						if ( _curChar == '<' )
							break;
						else
						// Found an ampersand, which denotes an entity or character
						// reference. Character reference is returned as text; entity
						// is returned as token. XML attribute will contain entity as
						// EntityReference object.
						if ( _curChar == '&' )
						{
							if ( readTokenEntity() == TOKEN_TEXT )
							{
								attrValue.append( _tokenText );
//                              attrText.append( _tokenText );
							}
							else
							{
/*
								if ( xml )
								{
									if ( attrValue.length() > 0 )
									{
										attr.appendChild( _document.createTextNode( StringUtil.lookup( attrValue, startLastText ) ) );
										startLastText = attrValue.length();
//                                        attrText.setLength( 0 );
									}
									attr.appendChild( _document.createEntityReference( StringUtil.lookup( _tokenText ) ) );
								}
								parseAttrEntity();
*/
								attrValue.append( '&' ).append( _tokenText ).append( ';' );
							}
						}
						else
						if ( isSpace( _curChar ) )
						{
							// All whitespaces are stored as space character (0x20).
							attrValue.append( ' ' );
//                          attrText.append( ' ' );
						}
						else
						{
							// All other characters are added to the attribute value
							// as they are. Next character is read and this inner loop
							// iterates once more.
							attrValue.append( (char) _curChar );
//                          attrText.append( (char) _curChar );
						}
						readChar();
					}
					// If XML attribute value consists of one or more text/entity
					// nodes, add text read so far as another text node. At the
					// least, attribute contains single text node.
//                  if ( xml && ( ! attr.hasChildNodes() || attrText.length() > 0 ) )
//                      attr.appendChild( _document.createTextNode( StringUtil.lookup( attrValue ) );
/*
					if ( xml && ( ! attr.hasChildNodes() || attrValue.length() > startLastText ) )
						attr.appendChild( _document.createTextNode( StringUtil.lookup( attrValue, startLastText ) ) );
*/
					if ( attrValue.length() > 0 )
						attr.setValue( attrValue.toString() );
				}
				// Avoid the readChar() that comes at the end of all conditions.
				continue;
				
			}
			else
				
			// Ignore whitespaces inbetween attributes, shout at junk text.
			if ( ! isSpace( _curChar ) )
				error( ERROR_WELL_FORMED, "Element contains junk text inbetween attributes." );
			readChar();
		}
		error( ERROR_WELL_FORMED, "Element's attribute list terminated prematurely." );
		return true;
	}
	/**
	 * Parses an internal or external general entity into XML/HTML contents. Called
	 * by {@link XMLParser#parseNextNode} and {@link HTMLParser#parseNextNode} when
	 * an entity reference is encountered in the element contents. {@link
	 * #_tokenText} contains the entity name on entry. The entity contents is
	 * duplicated and stored underneath {@link #_currentNode}.
	 * <P>
	 * The following rules govern how the entity is parsed:
	 * <UL>
	 * <LI>If the entity is unparsed general, an error is issued
	 * <LI>If the entity could not be found or could not be parsed, an error is
	 *  issued
	 * <LI>The contents of the parsed entity is duplicated and placed inside the
	 *  document underneath {@link #_currentNode} without affecting
	 *  {@link #_currentNode} itself
	 * <LI>If the parsed entity contains an entity reference, it is parsed
	 *  recursively according to the same rules
	 * </UL>
	 * 
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	protected final void parseContentEntity()
		throws ParseException
	{
		Node            node;
		
		// Get the contents of the entity, valid for attribute values. Store the
		// clones entity content as is underneath the current node.
		node = getEntityContents( _tokenText.toString(), false );
		while ( node != null )
		{
					
			_currentNode.appendChild( node.cloneNode( true ) );
			node = node.getNextSibling();
		}
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
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 * @throws IOException An I/O exception has been encountered when reading
	 *  from the input stream
	 * @see #_tokenText
	 * @see #readTokenMarkup
	 * @see #readTokenEntity
	 */
	protected final int readTokenContent()
		throws ParseException, IOException
	{
		int    token;
		
		// Read a single character and try to ascertain what comes next.
		// If character is less-than, it might be one of several markup tokens.
		// If character is an ampersand, it might be an entity reference.
		// Otherwise, character is plain text.
		readChar();
		if ( _curChar == '<' )
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
//            if ( _curChar == '>' )
//                error( ERROR_WELL_FORMED, "Found the character '>' not within the context of a markup. If this is intentional, please use '&gt;' instead." );
			_tokenText.append( (char) _curChar );
			readChar();
		}
		if ( _curChar != EOF )
			pushBack();
		return TOKEN_TEXT;
	}
}