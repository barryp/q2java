package org.openxml.io;

/**
 * org/openxml/io/DTDPrinter.java
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
import org.openxml.dom.*;
import org.openxml.DTDDocument;


/**
 * Prints DTD documents and internal subsets to an output stream in the selected
 * encoding. Five printing modes are supported:
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
 * It is recommended that either the {@link DTDPrinter#DTDPrinter(OutputStream,int)}
 * or the {@link DTDPrinter#DTDPrinter(OutputStream,String,int)} constructors be
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
public final class DTDPrinter
	extends Printer
{
	

	/**
	 * Construct a new DTDPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written.
	 * The default encoding is UTF-8.
	 * 
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public DTDPrinter( OutputStream output, int mode )
		throws IOException
	{
		this( new XMLStreamWriter( output ), mode, 70, 4 );
	}
	/**
	 * Construct a new DTDPrinter based on an output stream. A {@link
	 * java.io.OutputStream} must be supplied into which the text is written,
	 * and an encoding specified.
	 * 
	 * @param output The underlying {@link java.io.OutputStream}
	 * @param encoding The encoding to print in, e.g."ISO8859_1", "UTF8", "UTF16"
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 * @throws UnsupportedEncodingException The encoding is not supported
	 */
	public DTDPrinter( OutputStream output, String encoding, int mode )
		throws IOException, UnsupportedEncodingException
	{
		this( new XMLStreamWriter( output, encoding ), mode, 70, 4 );
	}
	/**
	 * Construct a new DTDPrinter. A {@link java.io.Writer} must be supplied into
	 * which the text is written. The default mode is selected, the default line
	 * width is 70 and indentation is 4 spaces.
	 * 
	 * @param write The underlying {@link java.io.Writer}
	 * @throws IOException An error occured with the output stream
	 */
	public DTDPrinter( Writer writer )
		throws IOException
	{
		this( writer, DEFAULT, 70, 4 );
	}
	/**
	 * Construct a new DTDPrinter. A {@link java.io.Writer} must be supplied
	 * into which the text is written.The default line width is 70 and
	 * indentation is 4 spaces.
	 * 
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @throws IOException An error occured with the output stream
	 */
	public DTDPrinter( Writer writer, int mode )
		throws IOException
	{
		this( writer, mode, 70, 4 );
	}
	/**
	 * Construct a new DTDPrinter. A {@link java.io.Writer} must be supplied into
	 * which the text is written.
	 * 
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @param lineWidth The width of each line
	 * @param indentSpaces The number of spaces to use for indentation
	 * @throws IOException An error occured with the output stream
	 */
	public DTDPrinter( Writer writer, int mode, int lineWidth, int indentSpaces )
		throws IOException
	{
		super( writer, mode, lineWidth, indentSpaces );
	}
	/**
	 * Prints a DTD document. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the output
	 * is to be human readable.
	 * 
	 * @param doc DTD document to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public void print( Document doc )
		throws IOException
	{
		printDocument( doc, new StringBuffer() );
		flush();
	}
	/**
	 * Prints a DTD document fragment. It is recommended to print a whole
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
		printDocument( docFrag, new StringBuffer() );
		flush();
	}
	/**
	 * Prints an DTD element. Since DTD's do not contain elements, nothing
	 * happens, no exception is thrown either.
	 * 
	 * @param elem DTD element to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public void print( Element elem )
		throws IOException
	{
	}
	/**
	 * Prints the nodes contents of the DTD. This option only applies to DTD
	 * derived from {@link DTDDocument} when the DTD document has been parsed
	 * into orderly nodes.
	 * 
	 * @param doc The DTD document or document fragment
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected void printContent( DTDDocument dtd, StringBuffer text )
		throws IOException
	{
		Node            child;

		child = dtd.getFirstChild();
		while ( child != null )
		{
			text.setLength( 0 );
			if ( child instanceof ParamEntity )
				printEntity( (ParamEntity) child, text );
			else
			if ( child instanceof Entity )
				printEntity( (Entity) child, text );
			else
			if ( child instanceof Notation )
				printNotation( (Notation) child, text );
			else 
			if ( child instanceof Comment ||
				 child instanceof ProcessingInstruction )
				printNode( child );
			if ( ! mode( COMPACT ) )
				breakLine();
			child = child.getNextSibling();
		}
		breakLine();
	}
	/**
	 * Prints an DTD document or document fragment according to DTD rules
	 * and the printing mode. This method is called directly by {@link
	 * #print(Element)} for any node that is a DTD document or document
	 * fragment.
	 * 
	 * @param doc The DTD document or document fragment
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see org.openxml.io.Printer#printDocument
	 */
	protected void printDocument( Node doc, StringBuffer text )
		throws IOException
	{
		Node        child;
		String        encoding;

		if ( doc instanceof Document )
		{
			// Print document version line followed by the DTD, if one exists.
			text.append( "<?xml version=\"1.0\"" );
			if ( getEncoding() != null )
				text.append( " encoding=\"" ).append( getEncoding() ).append( '\"' );
			printPart( text.append( "?>" ) );
			breakLine();
		}
		text.setLength( 0 );
		printSubset( (DocumentType) doc, text );
	}
	/**
	 * Prints an DTD element. Since DTD's do not contain elements, nothing
	 * happens, no exception is thrown either.
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
	}
	/**
	 * Prints a parameter entity declaration. An empty {@link
	 * java.lang.StringBuffer}, created by the calling method, is passed and
	 * should be used for fast string processing as necessary.
	 * 
	 * @param elem The parameter entity declaration
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	private void printEntity( ParamEntity entity, StringBuffer text )
		throws IOException
	{
		String    value;

		// Print '<!ENTITY %' followed by either the entity value, the system
		// identifier or the public (+ system) identifier.
		text.append( "<!ENTITY % " ).append( entity.getNodeName() );
		printPart( text );
		printSpace();
		if ( entity.isInternal() )
		{
			value = entity.getInternal();
			text.setLength( 0 );
			printPart( appendLiteral( text, value ) );
		}
		else
		{
			value = entity.getPublicId();
			if ( value != null )
			{
				text.setLength( 0 );
				text.append( "PUBLIC " );
				printPart( appendLiteral( text, value ) );
				value = entity.getSystemId();
				if ( value != null )
				{
					text.setLength( 0 );
					printSpace();
					printPart( appendLiteral( text, value ) );
				}
			}
			else
			{
				value = entity.getSystemId();
				if ( value != null )
				{
					text.setLength( 0 );
					text.append( "SYSTEM " );
					printPart( appendLiteral( text, value ) );
				}
			}
		}
		printPart( '>' );
		printBreak();
	}
	/**
	 * Prints a general entity declaration. An empty {@link
	 * java.lang.StringBuffer}, created by the calling method, is passed and
	 * should be used for fast string processing as necessary.
	 * 
	 * @param elem The general entity declaration
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	private void printEntity( Entity entity, StringBuffer text )
		throws IOException
	{
		String    value;
		Node    node;
		int        oldMode;

		// Print '<!ENTITY' followed by either the entity value, the system
		// identifier or the public (+ system) identifier. In the latter two
		// cases, print the optional notation name as well.
		text.append( "<!ENTITY " ).append( entity.getNodeName() );
		printPart( text );
		printSpace();
		if ( entity instanceof EntityImpl && ( (EntityImpl) entity ).isInternal() )
		{
			// If in DTD_CONTENT mode and the entity has been parsed, print the
			// entity as parsed node contents instead of internal value.
			if ( mode( DTD_CONTENT ) && ( (EntityImpl) entity ).getState() == EntityImpl.STATE_PARSED )
			{
				printPart( '"' );
				oldMode = getMode();
				setMode( getMode() & ~PRETTY );
				node = entity.getFirstChild();
				while ( node != null )
				{
					printNode( node );
					node = node.getNextSibling();
				}
				setMode( oldMode );
				printPart( '"' );
			}
			else
			{
				value = ( (EntityImpl) entity ).getInternal();
				text.setLength( 0 );
				printPart( appendLiteral( text, value ) );
			}
		}
		else
		{
			value = entity.getPublicId();
			if ( value != null )
			{
				text.setLength( 0 );
				text.append( "PUBLIC " );
				printPart( appendLiteral( text, value ) );
				value = entity.getSystemId();
				if ( value != null )
				{
					text.setLength( 0 );
					printSpace();
					printPart( appendLiteral( text, value ) );
				}
			}
			else
			{
				value = entity.getSystemId();
				if ( value != null )
				{
					text.setLength( 0 );
					text.append( "SYSTEM " );
					printPart( appendLiteral( text, value ) );
				}
			}
			value = entity.getNotationName();
			if ( value != null )
			{
				text.setLength( 0 );
				text.append( "NDATA " );
				printSpace();
				printPart( appendLiteral( text, value ) );
			}
		}
		printPart( '>' );
		printBreak();
	}
	/**
	 * Prints a notation declaration. An empty {@link java.lang.StringBuffer},
	 * created by the calling method, is passed and should be used for fast
	 * string processing as necessary.
	 * 
	 * @param elem The notation declaration
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	private void printNotation( Notation notation, StringBuffer text )
		throws IOException
	{
		String    value;

		// Print '<!NOTATION' followed by the notation name. The print the
		// public identifier, system identifier or both.
		text.append( "<!NOTATION " ).append( notation.getNodeName() );
		printPart( text );
		printSpace();
		value = notation.getPublicId();
		if ( value != null )
		{
			text.setLength( 0 );
			text.append( "PUBLIC " );
			printPart( appendLiteral( text, value ) );
			value = notation.getSystemId();
			if ( value != null )
			{
				text.setLength( 0 );
				printSpace();
				printPart( appendLiteral( text, value ) );
			}
		}
		else
		{
			value = notation.getSystemId();
			text.setLength( 0 );
			text.append( "SYSTEM " );
			printPart( appendLiteral( text, value ) );
		}
		printPart( '>' );
		printBreak();
	}
	protected void printSubset( DocumentType docType, StringBuffer text )
		throws IOException
	{
		DTDDocument        dtd = null;
		NamedNodeMap    nameMap;
		Dictionary        dict;
		Enumeration        enum;
		int                i;

		if ( mode( DTD_CONTENT ) && docType instanceof DTDDocument &&
			 docType.hasChildNodes() )
			printContent( (DTDDocument) docType, text );
		else
		{
			if ( docType instanceof DTDDocument )
				dtd = (DTDDocument) docType;
			
			if ( dtd != null )
			{
				dict = dtd.getParamEntities();
				if ( dict != null )
				{
					enum = dict.elements();
					while ( enum.hasMoreElements() )
					{
						text.setLength( 0 );
						printEntity( (ParamEntity) enum.nextElement(), text );
						if ( ! mode( COMPACT ) )
							breakLine();
					}
				}
			}
			
			nameMap = docType.getEntities();
			if ( nameMap != null )
			{
				for ( i = 0 ; i < nameMap.getLength() ; ++i )
				{
					text.setLength( 0 );
					printEntity( (Entity) nameMap.item( i ), text );
					if ( ! mode( COMPACT ) )
						breakLine();
				}
			}
			nameMap = docType.getNotations();
			if ( nameMap != null )
			{
				for ( i = 0 ; i < nameMap.getLength() ; ++i )
				{
					text.setLength( 0 );
					printNotation( (Notation) nameMap.item( i ), text );
					if ( ! mode( COMPACT ) )
						breakLine();
				}
			}
		}
	}
}