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
import org.openxml.dom.ext.*;
import org.openxml.x3p.*;


/**
 * Prints XML elements, documents and document fragments to an output stream
 * in the selected encoding. Five printing modes are supported:
 * <UL>
 * <LI>The default mode: lines are wrapped at the specified width and indented
 *  to the element level; multiple spaces are always printed; elements are not
 *  broken on separate line, so the output is quote compact.
 * <LI>Pretty format: lines are wrapped at the specified width and
 *  indented to the element level; multiple spaces are always printed; compound
 *  elements are printed on separate lines with indentation; the result takes
 *  out more space but is easy to read and edit by humans.
 * <LI>Compact format: lines are wrapped but not indented; multiple spaces
 *  are printed as one unless required by specific elements; comments are not
 *  printed; redundant space is saved in this mode.
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
 * @version $Revision: 1.2 $ $Date: 1999/04/12 06:12:32 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Node
 * @see XMLStreamWriter
 * @see StreamPublisher
 */
public final class XMLStreamPublisher
	extends StreamPublisher
{


	XMLStreamPublisher( StreamPublisherTarget target )
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
			case '\'':
				return "apos";
			case '&':
				return "amp";
		}
		return null;
	}
	/**
	 * Prints an XML element according to XML element rules. This method
	 * is called directly by {@link #print(Element)} for any node that is
	 * an XML element.
	 * <P>
	 * Elements are printed including attributes and all child nodes.
	 * In pretty format elements contained within elements will be printed
	 * on a separate line, unless a text or entity node dictate otherwise.
	 * Spaces are preserved if the element dictate so or if not in compact
	 * format.
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
			text.append( "=\"" );
			if ( attr.getValue() != null )
				text.append( encode( attr.getValue(), false ) );
			text.append( '\"' );
			printPart( text );
		}
		// If element does not have children, print closing '/>'.
		// If element had children, print closing '>', print all children
		// and conclude with printing closing tag.
		if ( elem.hasChildNodes() )
		{
			printPart( ">" );
			indent();
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
			text.setLength( 0 );
			unindent();
			text.append( "</" ).append( elem.getTagName() ).append( '>' );
			printPart( text );
		}
		else
			printPart( "/>" );
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
		StringBuffer    text;
		DocumentType    docType;
		DocumentTypeEx  docTypeEx;
		int             index;
		String          internalDTD = null;
		String          publicId = null;
		String          systemId = null;
		String			encoding = null;  // (BBP)


		text = new StringBuffer( 40 );
		// Print document version line followed by the DTD, if one exists.
		text.append( "<?xml version=\"1.0\"" );

		encoding = getEncoding();			
		if ( encoding != null )
			{
			// make sure we use "UTF-8" instead of "UTF8" for encoding
			if (encoding.equals("UTF8"))
				encoding = "UTF-8";
				
			text.append( " encoding=\"" ).append( encoding ).append( '\"' );
			}
			
		if ( getFormat().isStandalone() )
			text.append( " standalone=\"yes\"" );
		printPart( text.append( "?>" ) );
		breakLine();

		// Print !DOCTYPE keyword, DTD name and all entities defined for this
		// document, each on a separate line or all on the same line depending
		// on the mode.
		docType = ( (Document) doc ).getDoctype();
		if ( docType != null && docType instanceof DocumentTypeEx )
		{
			docTypeEx = (DocumentTypeEx) docType;
			publicId = docTypeEx.getPublicId();
			systemId = docTypeEx.getSystemId();
			internalDTD = docTypeEx.internalAsText();
		}
		if ( getFormat().getInternalDTD() != null )
			internalDTD = getFormat().getInternalDTD();
		if ( getFormat().getDTDPublicId() != null || getFormat().getDTDSystemId() != null )
		{
			publicId = getFormat().getDTDPublicId();
			systemId = getFormat().getDTDSystemId();
		}
			
		if ( publicId != null || systemId != null || internalDTD != null )
		{
			text.setLength( 0 );
			text.append( "<!DOCTYPE " ).append( docType.getNodeName() );
			if ( ! getFormat().isStandalone() )
			{
				if ( publicId != null )
				{
					text.append( " PUBLIC " );
					appendLiteral( text, publicId );
					text.append( " " );
					appendLiteral( text, systemId );
				}
				else
				if ( systemId != null )
				{
					text.append( " SYSTEM " );
					appendLiteral( text, systemId );
				}
			}
			if ( internalDTD != null )
			{
				printPart( text.append( " [" ) );
				indent();
				if ( ! getFormat().isCompact() )
					breakLine();
				printPart( internalDTD );
				unindent();
				if ( ! getFormat().isCompact() )
					breakLine();
				printPart( "]>" );
			}
			else
				printPart( text.append( '>' ) );
			breakLine();
		}
		publish( (Node) doc );
		flush();
	}
}