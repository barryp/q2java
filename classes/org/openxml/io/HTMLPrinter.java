package org.openxml.io;

/**
 * org/openxml/io/HTMLPrinter.java
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
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.dom.*;
import org.openxml.dom.html.*;


/**
 * Prints HTML elements, documents and document fragments to an output stream
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
 * <P>
 * It is possible to print XML documents using this printer, for example, as
 * the result of XML + XSL processing.
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
public final class HTMLPrinter
	extends Printer
{


	/**
	 * Construct a new HTMLPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written.
	 * The default encoding is UTF-8. Supported printing modes are default,
	 * {@link #PRETTY}, {@link #COMPACT}, {@link #NOWRAP} and {@link #COMPACT} +
	 * {@link #NOWRAP}.
	 *
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public HTMLPrinter( OutputStream output, int mode )
		throws IOException
	{
		this( new XMLStreamWriter( output ), mode, 70, 4 );
	}
	/**
	 * Construct a new HTMLPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written,
	 * and an encoding specified.
	 *
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param encoding The encoding to print in, e.g."ISO8859_1", "UTF8"
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 * @throws UnsupportedEncodingException The encoding is not supported
	 */
	public HTMLPrinter( OutputStream output, String encoding, int mode )
		throws IOException, UnsupportedEncodingException
	{
		this( new XMLStreamWriter( output, encoding ), mode, 70, 4 );
	}
	/**
	 * Construct a new HTMLPrinter. A {@link java.io.Writer} must be supplied
	 * into which the text is written. The default mode is selected, the default
	 * line width is 70 and indentation is 4 spaces.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @throws IOException An error occured with the output stream
	 */
	public HTMLPrinter( Writer writer )
		throws IOException
	{
		this( writer, 0, 70, 4 );
	}
	/**
	 * Construct a new HTMLPrinter. A {@link java.io.Writer} must be supplied
	 * into which the text is written.The default line width is 70 and
	 * indentation is 4 spaces.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public HTMLPrinter( Writer writer, int mode )
		throws IOException
	{
		this( writer, mode, 70, 4 );
	}
	/**
	 * Construct a new HTMLPrinter. A {@link java.io.Writer} must be supplied
	 * into which the text is written.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @param lineWidth The width of each line
	 * @param indentSpaces The number of spaces to use for indentation
	 * @throws IOException An error occured with the output stream
	 */
	public HTMLPrinter( Writer writer, int mode, int lineWidth, int indentSpaces )
		throws IOException
	{
		super( writer, mode, lineWidth, indentSpaces );
	}
	protected String getCharacterRef( char ch )
	{
		return HTMLDTD.fromChar( ch );
	}
	/**
	 * Prints an HTML document. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the output
	 * is to be human readable. It is possible to print XML elements using
	 * this method.
	 *
	 * @param doc HTML document to print
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
	 * Prints an HTML document fragment. It is recommended to print a whole
	 * document or document tree at once rather than an element at a time if
	 * the output is to be human readable. It is possible to print XML elements
	 * using this method.
	 *
	 * @param docFrag HTML document fragment to print
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
	 * Prints an HTML element. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the
	 * output is to be human readable. It is possible to print XML elements
	 * using this method.
	 *
	 * @param elem HTML element to print
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
	 * Prints an HTML document or document fragment according to HTML rules.
	 * This method is called directly by {@link #print(Element)} for any node
	 * that is an HTML document or document fragment.
	 *
	 * @param doc The HTML document or document fragment
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see org.openxml.io.Printer#printDocument
	 */
	protected void printDocument( Node doc, StringBuffer text )
		throws IOException
	{
		Node    child;

		// If this is a full document, print a reference to the DTD.
		// If this is just a fragment, print its contents directly.
		if ( doc instanceof HTMLDocument )
		{
			// Write the DTD specification for HTML 4.0 and then follow with
			// the rest of the document.
			printPart( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\"" );
			breakLine();
			printPart( "  \"http://www.w3.org/TR/REC-html40/strict.dtd\">" );
			breakLine();
		}

		// Simply print contents of document (fragment). Document (fragment)
		// is never part of a larger document, so always break line after it.
		child = doc.getFirstChild();
		while ( child != null )
		{
			printNode( child );
			child = child.getNextSibling();
		}
		breakLine();
	}
	/**
	 * Prints an HTML element according to HTML element rules. This method
	 * is called directly by {@link #print(Element)} for any node that is
	 * an HTML element.
	 * <P>
	 * Elements are printed including attributes and all child nodes. In {@link
	 * #PRETTY} mode elements contained within elements are printed on a separate
	 * line, all other nodes are printed on the same line. The &lt;P&gt; element
	 * is always printed on a separate line in {@link #PRETTY} mode. Spaces are
	 * if the element dictate so or if not in {@link #COMPACT} mode.
	 *
	 * @param elem The HTML element
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
		// strings and is always quoted. Print closing >.
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
		printPart( ">" );
		// If element supports children, print all children (might be zero) and
		// conclude with the closing tag. If element does not support children,
		// printing the empty tag is enough.
		// In PRETTY mode, each element inside this element is printed on a
		// separate line, but text, entity and other nodes are printed on the
		// same line.
		if ( elem.hasChildNodes() || HTMLDTD.supportsChildren( elem ) )
		{
			indent();
			// If element preserve spaces, do so even if in COMPACT mode.
			oldMode = getMode();
			if ( elem instanceof HTMLPreElement ||
				 elem instanceof HTMLScriptElement ||
				 elem instanceof HTMLStyleElement )
				setMode( getMode() & ~COMPACT );
			child = elem.getFirstChild();
			// If an element inside this element is printed on a separate line,
			// the ending tag must also print on a separate line.
			didBreak = false;
			while ( child != null )
			{
				if ( mode( PRETTY ) && child instanceof Element &&
					 child.hasChildNodes() )
				{
					breakLine();
					didBreak = true;
				}
				printNode( child );
				child = child.getNextSibling();
			}
			text.setLength( 0 );
			setMode( oldMode );
			unindent();
			if ( didBreak )
				breakLine();
			text.append( "</" ).append( elem.getTagName() ).append( '>' );
			printPart( text );
			if ( mode( PRETTY ) )
				breakLine();
		}
		else
		// If in PRETTY mode and element is child-less <P>, break line.
		if ( mode( PRETTY ) && elem instanceof HTMLParagraphElement )
			breakLine();
	}
}