package org.openxml.x3p.publishers;

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


import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.x3p.*;


/**
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see XMLStreamWriter
 * @see StreamPublisher
 */
public final class HTMLStreamPublisher
	extends StreamPublisher
{


	HTMLStreamPublisher( StreamPublisherTarget target )
		throws IOException
	{
		super( target );
	}
	protected String getCharacterRef( char ch )
	{
		return HTMLEntities.fromChar( ch );
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
	 * if the element dictate so or if not in compact format.
	 *
	 * @param elem The HTML element
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see org.openxml.io.Printer#printElement
	 */
	protected void printElement( Element elem, StringBuffer text,
								 boolean preserveSpace )
		throws IOException
	{
		Attr            attr;
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
		if ( elem.hasChildNodes() || HTMLEntities.supportsChildren( elem.getTagName() ) )
		{
			indent();
			// If an element inside this element is printed on a separate line,
			// the ending tag must also print on a separate line.
			didBreak = false;
			// If element preserve spaces, do so even if in compact format.
			// Scripts and styles are better printed as CDATA sections.
			if ( elem instanceof HTMLScriptElement || elem instanceof HTMLStyleElement )
			{
				preserveSpace = true;
				if ( getFormat().isPretty() )
				{
					breakLine();
					didBreak = true;
				}
				child = elem.getFirstChild();
				while ( child != null )
				{
					if ( child instanceof CharacterData )
						printPart( ( (CharacterData) child ).getData() );
					child = child.getNextSibling();
				}
			}
			else
			{
				// If element preserve spaces, do so even if in compact format.
				if ( elem instanceof HTMLPreElement )
					preserveSpace = true;
				child = elem.getFirstChild();
				while ( child != null )
				{
					if ( getFormat().isPretty() && child instanceof Element &&
						 child.hasChildNodes() )
					{
						breakLine();
						didBreak = true;
					}
					if ( printNode( child, preserveSpace ) )
						didBreak = true;
					child = child.getNextSibling();
				}
			}
			text.setLength( 0 );
			unindent();
			if ( didBreak )
				breakLine();
			text.append( "</" ).append( elem.getTagName() ).append( '>' );
			printPart( text );
			if ( getFormat().isPretty() )
				breakLine();
		}
		else
		// If in PRETTY mode and element is child-less <P>, break line.
		if ( getFormat().isPretty() && elem instanceof HTMLParagraphElement )
			breakLine();
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
	public synchronized void publish( Document doc )
		throws IOException
	{
		// Write the DTD specification for HTML 4.0 and then follow with
		// the rest of the document.
		printPart( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\"" );
		breakLine();
		printPart( "                      \"http://www.w3.org/TR/REC-html40/strict.dtd\">" );
		breakLine();
		publish( (Node) doc );
		flush();
	}
}