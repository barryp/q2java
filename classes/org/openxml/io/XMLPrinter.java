package org.openxml.io;

/**
 * org/openxml/io/XMLPrinter.java
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
import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.DTDDocument;
import org.openxml.dom.*;


/**
 * Prints XML elements, documents and document fragments to an output stream
 * in the selected encoding. Five printing modes are supported:
 * <UL>
 * <LI>The default mode: lines are wrapped at the specified width and indented
 *  to the element level; multiple spaces are always printed; elements are not
 *  broken on separate line, so the output is quote compact.
 * <LI>{@link #PRETTY} mode: lines are wrapped at the specified width and
 *  indented to the element level; multiple spaces are always printed; compound
 *  elements are printed on separate lines with indentation; the result takes
 *  out more space but is easy to read and edit by humans.
 * <LI>{@link #COMPACT} mode: lines are wrapped but not indented; multiple spaces
 *  are printed as one unless required by specific elements; comments are not
 *  printed; redundant space is saved in this mode.
 * <LI>{@link #NOWRAP} mode: lines are not wrapped unless required by specific
 *  elements; the result is often a single long line.
 * <LI>{@link #COMPACT} + {@link #NOWRAP} mode: the result of this mode is
 *  the shortest possible output that is also valid and fidel to the document
 *  contents; the output is quite unreadable to the point of being obfuscated.
 * </UL>
 * The constructor determines the printing mode, the width of each line (for
 * wrapping long lines) and the indentation of each element level. The line
 * width defaults to 70 characters and the indentation to 4 spaces.
 * <P>
 * Supported encodings are all the ASCII mappings ("ASCII", "ISO8859_1", "Cp1252",
 * etc), UTF-8 ("UTF8") and Unicode ("Unicode"). The default is UTF-8. Unicode
 * output is preceded with the 0xFFFE byte order identifier and bytes are in
 * network order.
 * <P>
 * It is recommended that either the {@link XMLPrinter#XMLPrinter(OutputStream,int)}
 * or the {@link XMLPrinter#XMLPrinter(OutputStream,String,int)} constructors be
 * used to provide accurate and fast output.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see XMLStreamWriter
 * @see Printer
 * 
 * @deprecated
 *  This package has become obsolete in favor of the <a
 *  href="../x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
 *  This package is temporarily provided for backward compatibility but
 *  will not be included in release 1.1.
 */
public class XMLPrinter
	extends Printer
{


	/**
	 * Construct a new XMLPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written.
	 * The default encoding is UTF-8.
	 *
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public XMLPrinter( OutputStream output, int mode )
		throws IOException
	{
		this( new XMLStreamWriter( output ), mode, 70, 4 );
	}
	/**
	 * Construct a new XMLPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written,
	 * and an encoding specified.
	 *
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param encoding The encoding to print in, e.g."ISO8859_1", "UTF8", "UTF16"
	 * @param mode The writing mode
	 * @throws UnsupportedEncodingException The encoding is not supported
	 * @throws IOException An error occured with the output stream
	 */
	public XMLPrinter( OutputStream output, String encoding, int mode )
		throws IOException, UnsupportedEncodingException
	{
		this( new XMLStreamWriter( output, encoding ), mode, 70, 4 );
	}
	/**
	 * Construct a new XMLPrinter. A {@link java.io.Writer} must be supplied into
	 * which the text is written. The default mode is selected, the default line
	 * width is 70 and indentation is 4 spaces.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @throws IOException An error occured with the output stream
	 */
	public XMLPrinter( Writer writer )
		throws IOException
	{
		this( writer, DEFAULT, 70, 4 );
	}
	/**
	 * Construct a new XMLPrinter. A {@link java.io.Writer} must be supplied
	 * into which the text is written.The default line width is 70 and
	 * indentation is 4 spaces.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public XMLPrinter( Writer writer, int mode )
		throws IOException
	{
		this( writer, mode, 70, 4 );
	}
	/**
	 * Construct a new XMLPrinter. A {@link java.io.Writer} must be supplied into
	 * which the text is written.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @param lineWidth The width of each line
	 * @param indentSpaces The number of spaces to use for indentation
	 * @throws IOException An error occured with the output stream
	 */
	public XMLPrinter( Writer writer, int mode, int lineWidth, int indentSpaces )
		throws IOException
	{
		super( writer, mode, lineWidth, indentSpaces );
	}
	protected String getCharacterRef( char ch )
	{
		switch ( ch )
		{
			// Encode special XML characters into the equivalent character references.
			// These five are defined by default for all XML documents.
			case '<':
				return "&lt;";
			case '>':
				return "&gt;";
			case '"':
				return "&quot;";
			case '\'':
				return "&apos;";
			case '&':
				return "&amp;";
		}
		return null;
	}
	/**
	 * Prints an XML document. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the output
	 * is to be human readable.
	 *
	 * @param doc XML document to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public void print( Document doc )
		throws IOException
	{
		printNode( doc );
		flush();
	}
	/**
	 * Prints an XML document fragment. It is recommended to print a whole
	 * document or document tree at once rather than an element at a time if
	 * the output is to be human readable.
	 *
	 * @param docFrag XML document fragment to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public void print( DocumentFragment docFrag )
		throws IOException
	{
		printNode( docFrag );
		flush();
	}
	/**
	 * Prints an XML element. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the
	 * output is to be human readable.
	 *
	 * @param elem XML element to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public void print( Element elem )
		throws IOException
	{
		printNode( elem );
		flush();
	}
	/**
	 * Prints an XML document or document fragment according to XML rules.
	 * This method is called directly by {@link #print(Element)} for any node
	 * that is an XML document or document fragment.
	 * <P>
	 * The XML document includs the DTD definition and is always well formed.
	 * XML elements are printed according to XML formatting rules, see
	 * {@link #printElement}.
	 *
	 * @param doc The XML document or document fragment
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see org.openxml.io.Printer#printDocument
	 */
	protected void printDocument( Node doc, StringBuffer text )
		throws IOException
	{
		Node            child;
		DocumentType    docType;
		DTDPrinter        dtdPrinter;

		// If this is a full document, print the XML version, and a reference
		// to the DTD or the DTD, depending on the mode. If this is just a
		// fragment, print its contents directly.
		if ( doc instanceof Document )
		{
			// Print document version line followed by the DTD, if one exists.
			text.append( "<?xml version=\"1.0\"" );
			if ( getEncoding() != null )
				text.append( " encoding=\"" ).append( getEncoding() ).append( '\"' );
			if ( mode( DTD_STANDALONE ) )
				text.append( " standalone=\"yes\"" );
			printPart( text.append( "?>" ) );

			// Print !DOCTYPE keyword, DTD name and all entities defined for this
			// document, each on a separate line or all on the same line depending
			// on the mode.
			docType = ( (Document) doc ).getDoctype();
			if ( docType != null )
			{
				text.setLength( 0 );
				text.append( "<!DOCTYPE " ).append( docType.getNodeName() );
				if ( docType instanceof DTDDocument && ! mode( DTD_STANDALONE ) )
				{
					if ( ( (DTDDocument) docType ).getPublicId() != null )
					{
						text.append( " PUBLIC " );
						appendLiteral( text, ( (DTDDocument) docType ).getPublicId() );
						text.append( " " );
						appendLiteral( text, ( (DTDDocument) docType ).getSystemId() );
					}
					else
					if ( ( (DTDDocument) docType ).getSystemId() != null )
					{
						text.append( " SYSTEM " );
						appendLiteral( text, ( (DTDDocument) docType ).getSystemId() );
					}
				}

				if ( true )
				{
					printPart( text.append( " [" ) );
					indent();
					if ( ! mode( COMPACT ) )
						breakLine();
					text.setLength( 0 );
					dtdPrinter = new DTDPrinter( getWriter(), getMode() );
					dtdPrinter.printSubset( docType, text );
					unindent();
					if ( ! mode( COMPACT ) )
						breakLine();
					printPart( "]>" );
				}
				else
					printPart( text.append( '>' ) );
			}
		}

		// Simply print contents of document fragment. Document fragment is
		// never part of a larger document, so always break line after it.
		breakLine();
		child = doc.getFirstChild();
		while ( child != null )
		{
			printNode( child );
			child = child.getNextSibling();
		}
		breakLine();
	}
	/**
	 * Prints an XML element according to XML element rules. This method
	 * is called directly by {@link #print(Element)} for any node that is
	 * an XML element.
	 * <P>
	 * Elements are printed including attributes and all child nodes.
	 * In {@link #PRETTY} mode elements contained within elements will be
	 * printed on a separate line, unless a text or entity node dictate
	 * otherwise. Spaces are preserved if the element dictate so or if not
	 * in {@link #COMPACT} mode.
	 *
	 * @param elem The XML element
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see org.openxml.io.Printer#printElement
	 */
	protected void printElement( Element elem, StringBuffer text )
		throws IOException
	{
		Attr            attr;
		int             oldMode;
		boolean         canBreak;
		boolean         didBreak;
		Node            child;
		NamedNodeMap    attrMap;
		int             i;

		// Element is printed in three parts: the opening tag with its
		// attributes, the contained children nodes (if any), and the
		// closing tag (if element contains children).

		// Print opening '<' and tag name. For each attribute print a space
		// before that attribute, the attribute name and its value.
		// The attribute value is printed only if not null, including empty
		// strings and is always quoted. The closing > is not printed yet.
		text.append( "<" ).append( elem.getTagName() );
		printPart( text );
		attrMap = elem.getAttributes();
		for ( i = 0 ; i < attrMap.getLength() ; ++i )
		{
			attr = (Attr) attrMap.item( i );
			printSpace();
			text.setLength( 0 );
			text.append( attr.getName() );
			if ( attr.getValue() != null )
			{
				text.append( "=\"" );
				text.append( encode( attr.getValue(), true ) );
				text.append( '\"' );
			}
			printPart( text );
		}
		// If element does not have children, print closing '/>'.
		// If element had children, print closing '>', print all children
		// and conclude with printing closing tag.
		if ( elem.hasChildNodes() )
		{
			printPart( ">" );
			indent();
			// Preserve spaces for XML element even if in COMPACT mode.
			oldMode = getMode();
			setMode( getMode() & ~COMPACT );
			child = elem.getFirstChild();
			// Can break between two elements, but not between element and
			// any other node. canBreak is maintained true as long as the
			// element only contains elements and facilitates a line break
			// between the elements in PRETTY mode.
			canBreak = true;
			// didBreak indicates that a line break was done between this
			// element and at least on of its children and if the last child
			// is an element, a second line break should take place.
			didBreak = false;
			while ( child != null )
			{
				if ( child instanceof Element )
				{
					if ( mode( PRETTY ) && canBreak )
					{
						breakLine();
						didBreak = true;
					}
					canBreak = true;
				}
				else
					canBreak = false;
				printNode( child );
				child = child.getNextSibling();
			}
			if ( canBreak && didBreak )
				breakLine();
			text.setLength( 0 );
			setMode( oldMode );
			unindent();
			text.append( "</" ).append( elem.getTagName() ).append( '>' );
			printPart( text );
		}
		else
			printPart( "/>" );
	}
}