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
import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.x3p.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class XHTMLStreamPublisher
	extends StreamPublisher
{


	XHTMLStreamPublisher( StreamPublisherTarget target )
		throws IOException
	{
		super( target );
	}
	protected String getCharacterRef( char ch )
	{
		switch ( ch )
		{
			// Encode special XML characters into the equivalent character references.
			// These five are defined by default for all XML documents.
			case '<':
				return "lt";
			case '>':
				return "gt";
			case '"':
				return "quot";
			case '&':
				return "amp";
		}
		return HTMLEntities.fromChar( ch );
	}
	/**
	 * Prints an XML element according to XML element rules. This method
	 * is called directly by {@link #print(Element)} for any node that is
	 * an XML element.
	 * <P>
	 * Elements are printed including attributes and all child nodes.
	 * In pretty format elements contained within elements will be
	 * printed on a separate line, unless a text or entity node dictate
	 * otherwise. Spaces are preserved if the element dictate so or if not
	 * in compact format.
	 *
	 * @param elem The XML element
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
		// strings and is always quoted. Print closing >.
		text.append( "<" ).append( elem.getTagName().toLowerCase() );
		printPart( text );
		attrMap = elem.getAttributes();
		for ( i = 0 ; i < attrMap.getLength() ; ++i )
		{
			attr = (Attr) attrMap.item( i );
			printSpace();
			text.setLength( 0 );
			text.append( attr.getName().toLowerCase() );
			text.append( "=\"" );
			if ( attr.getValue() != null )
				text.append( encode( attr.getValue(), false ) );
			text.append( '\"' );
			printPart( text );
		}

		// If element does not have children, print closing '/>'.
		// If element had children, print closing '>', print all children
		// and conclude with printing closing tag.
		if ( elem.hasChildNodes() || HTMLEntities.supportsChildren( elem.getTagName() ) )
		{
			printPart( ">" );
			indent();
			// If element preserve spaces, do so even if in compact format.
			// Scripts and styles are better printed as CDATA sections.
			if ( elem instanceof HTMLScriptElement || elem instanceof HTMLStyleElement )
			{
				preserveSpace = true;
				printPart( "<![CDATA[" );
				if ( getFormat().isPretty() )
					breakLine();
				child = elem.getFirstChild();
				while ( child != null )
				{
					if ( child instanceof CharacterData )
						printPart( ( (CharacterData) child ).getData() );
					child = child.getNextSibling();
				}
				if ( getFormat().isPretty() )
					breakLine();
				printPart( "]]>" );
			}
			else
			{
				child = elem.getFirstChild();
				// Can break between two elements, but not between element and
				// any other node. canBreak is maintained true as long as the
				// element only contains elements and facilitates a line break
				// between the elements in pretty format.
				canBreak = true;
				// didBreak indicates that a line break was done between this
				// element and at least on of its children and if the last child
				// is an element, a second line break should take place.
				didBreak = false;
				while ( child != null )
				{
					if ( child instanceof Element )
					{
						if ( elem instanceof HTMLPreElement )
						{
							breakLine();
							didBreak = true;
							printNode( child, preserveSpace );
							breakLine();
						}
						else
						if ( getFormat().isPretty() && canBreak )
						{
							breakLine();
							didBreak = true;
							printNode( child, preserveSpace );
							breakLine();
						}
						else
							printNode( child, preserveSpace );
						canBreak = true;
					}
					else
					if ( printNode( child, preserveSpace ) )
					{
						canBreak = true;
						didBreak = true;
					}
					child = child.getNextSibling();
				}
				if ( canBreak && didBreak )
					breakLine();
			}
			text.setLength( 0 );
			unindent();
			text.append( "</" ).append( elem.getTagName().toLowerCase() ).append( '>' );
			printPart( text );
		}
		else
			printPart( " />" );
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
	public synchronized void publish( Document doc )
		throws IOException
	{
		Element html;

		// Write the DTD specification for HTML 4.0 and then follow with
		// the rest of the document.
		printPart( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" );
		breakLine();
		printPart( "                      \"http://www.w3.org/TR/WD-html-in-xml/DTD/xhtml1-strict.dtd\">" );
		breakLine();
		html = (Element) doc.getElementsByTagName( "html" ).item( 0 );
		if ( html == null )
			html = (Element) doc.getElementsByTagName( "HTML" ).item( 0 );
		if ( html != null )
			html.setAttribute( "xmlns", "http://www.w3.org/Profiles/xhtml1-strict" );
		publish( (Node) doc );
		flush();
	}
}