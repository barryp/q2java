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
import org.openxml.x3p.*;
import org.openxml.util.*;


/**
 * Implements a base printed for XML/HTML/DTD documents. Derived classes are
 * used by the application through three {@link #print} methods. They implement
 * {@link #printDocument} and {@link #printElement} methods.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:03 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see StreamPublisher
 * @see XMLStreamWriter
 * @see XMLStreamPublisher
 * @see HTMLStreamPublisher
 */
public abstract class StreamPublisher
	implements Publisher
{


	/**
	 * Holds the currently accumulating text line. A string buffer is used to
	 * save unnecessary string creation/deletion. This buffer will constantly
	 * be reused by deleting its contents instead of reallocating it.
	 */
	private StringBuffer        _textLine = new StringBuffer( 80 );


	/**
	 * Holds the currently accumulating word that follows {@link #_textLine}.
	 * When the end of the word is identified by a call to {@link #printSpace}
	 * or {@link #printBreak}, this word is added to the accumulated line.
	 * This buffer will constantly be reused by deleting its contents instead
	 * of reallocating it.
	 */
	private StringBuffer        _textWord = new StringBuffer( 20 );


	/**
	 * Counts how many white spaces come between the accumulated line and the
	 * current word. In compact format, unless the element requires that
	 * spaces be preserved, multiple spaces will be folded into one.
	 */
	private int                 _whiteSpaces;


	/**
	 * Holds the indentation for the current line that is now accumulating in
	 * memory and will be sent for printing shortly.
	 */
	private int                 _thisIndent;


	/**
	 * Holds the indentation for the next line to be printed. After this line is
	 * printed, {@link #_nextIndent} is assigned to {@link #_thisIndent}.
	 */
	private int                 _nextIndent;


	/**
	 * The Writer into which XML text is written.
	 */
	private Writer              _writer;


	private StreamFormat        _format;


	/**
	 * Construct a new Printer. A Writer must be supplied into which the
	 * text is written.
	 *
	 * @param write The underlying {@link Writer}
	 * @param mode The writing mode
	 * @param lineWidth The width of each line
	 * @param indentSpaces The number of spaces to use for indentation
	 */
	protected StreamPublisher( StreamPublisherTarget target )
		throws IOException
	{
		OutputStream    os;

		if ( target == null )
			throw new NullPointerException( Resources.format( "Error001", "target" ) );
		_format = target.getFormat();
		os = target.getOutputStream();
		if ( os != null )
			_writer = new StreamWriter( os, getFormat().getEncoding() );
		else
			_writer = new BufferedWriter( target.getWriter() );
		if ( _writer == null )
			throw new IllegalArgumentException( Resources.format( "Error003", "target" ) );
	}
	/**
	 * Appends a literal to the contents of the text buffer. The literal is
	 * enclosed with double quotes, and so quotes and other non-printable
	 * characters in the literal are printed as character references.
	 * On exit, the literal has been appended to <TT>text</TT>.
	 *
	 * @param text Buffer containing existing text
	 * @param literal The literal to append to the buffer
	 * @return The text buffer with the literal appended to it
	 */
	protected final StringBuffer appendLiteral( StringBuffer text, String literal )
	{
		StringBuffer    result;
		int                i;

		text.append( '"' );
		for( i = 0 ; i < literal.length() ; ++i )
		{
			if ( literal.charAt( i ) == '"' )
				text.append( "&#22;" );
			else
			if ( literal.charAt( i ) > 0x7F )
				text.append( "&#" ).append( String.valueOf( literal.charAt( i ) ) ).append( ';' );
			else
				text.append( literal.charAt( i ) );
		}
		text.append( '"' );
		return text;
	}
	/**
	 * Breaks the current line and starts with a new line. Everything accumulated
	 * up to now is printed and a new line is started with empty text.
	 *
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void breakLine()
		throws IOException
	{
		while ( _whiteSpaces > 0 )
		{
			_textLine.append( ' ' );
			-- _whiteSpaces;
		}
		_textLine.append( _textWord );
		_textWord.setLength( 0 );
		flushLine();
	}
	/**
	 * Close the output stream assocaited with this printer. Any subsequent calls
	 * to one of the print methods will throw an exception.
	 */
	public synchronized final void close()
	{
		try
		{
			_writer.close();
		}
		catch ( IOException except )
		{
		}
	}
	/**
	 * Encodes a textual string so it may be used as an XML text or attribute
	 * value. Encoding translates the source string according to the following
	 * rules:
	 * <UL>
	 * <LI>Non-printable characters are ignored
	 * <LI>All whitespace characters (0x09, 0x0A, 0x0D and 0x20) are translated
	 *  into a space (0x20)
	 * <LI>Non-ASCII characters are translated into Unicode entities
	 * <LI>Special XML/HTML codes are translated into XML entities (e.g. &amp;lt;, &amp;gt;)
	 *  see {@link #getCharacterRef}
	 * <LI>All other characters remain intact
	 * <UL>
	 * The whitespace translation rule is in effect for most elements, however,
	 * some elements may wish to preserve whitespace values (e.g. &lt;PRE&gt;)
	 * and should indicate so by setting <TT>preserveSpace</TT> to true.
	 * <P>
	 * The {@link #encode} method is declared public and static and may be used
	 * by other libraries for encoding textual string into value XML/HTML strings.
	 *
	 * @param text The textual string to encode
	 * @param preserveSpace True is encoding must preserve spaces
	 * @return The encoded textual string
	 */
	protected final String encode( String text, boolean preserveSpace )
	{
		StringBuffer    result;
		int                i;
		char            ch;
		String            charRef;

		result = new StringBuffer( text.length() );
		for ( i = 0 ; i < text.length() ; ++i )
		{
			ch = text.charAt( i );
			switch ( ch )
			{
			// All whitespace characters come out as a space, unless space is
			// preserved by this specific element.
			case 0x20:
			case 0x0D:
			case 0x0A:
			case 0x09:
				if ( preserveSpace )
					result.append( ch );
				else
					result.append( ' ' );
				break;
			// All non-printable characters and characters above 0x80 are represented
			// as Unicode entities. All other characters are presented verbatim.
			// Consult the list of character references first.
			default:
				charRef = getCharacterRef( ch );
				if ( charRef == null )
				{
					if ( ch < 0x20 || ch >= 0x7F )
						result.append( "&#" ).append( Integer.toString( ch ) ).append( ';' );
					else
						result.append( ch );
				}
				else
					result.append( '&' ).append( charRef ).append( ';' );
			}
		}
		return result.toString();
	}
	protected final void flush()
		throws IOException
	{

		_writer.flush();
	}
	/**
	 * Prints the current line as accumulated in {@link #_textLine}. The current
	 * word is not printed but rather carried on to the next line. This is true
	 * when called by {@link #wrapLine}; {@link #breakLine} will add the current
	 * word to the accumulated line, so it will print as well.
	 * <P>
	 * The indentation is taken from {@link #_thisIndent} and may not exceed 40.
	 * The next line will use the indentation in {@link #_nextIndent}.
	 *
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void flushLine()
		throws IOException
	{
		int     indent;

		if ( _textLine.length() > 0 )
		{
			// Make sure the indentation does not blow us away, then print it.
			indent = _thisIndent;
			if ( indent > 40 )
				indent = 40;
			while ( indent -- > 0 )
				_writer.write( ' ' );
			_thisIndent = _nextIndent;

			try
			{
				// Print the line. If there are any spaces at the end, print them as
				// well, so they will be read when the document is parsed. The end
				// of line character is equivalent to one of these spaces, so print
				// one less than _whiteSpaces.
				_writer.write( _textLine.toString() );
				while ( _whiteSpaces > 1 )
				{
					_writer.write( ' ' );
					-- _whiteSpaces;
				}
				if ( getFormat().getLineSeparator() == null )
					_writer.write( 0x0A );
				else
					_writer.write( getFormat().getLineSeparator() );
			}
			finally
			{
				// Reset in preparation for the next line.
				_whiteSpaces = 0;
				_textLine.setLength( 0 );
			}
		}
	}
	/**
	 * Returns a character reference name from the character value. If the value
	 * is not recognized, returns null. For example, '&amp;nbsp;' is the character
	 * reference name for the character 0xA0.
	 *
	 * @param ch Character value
	 * @return Character reference name of null
	 */
	protected abstract String getCharacterRef( char ch );
	/**
	 * Returns the output character encoding, if known. The output character
	 * encoding is only accessible if it has been specified in the constructor.
	 * If the encoding is unknown or cannot be determined, null is returned.
	 *
	 * @return The character encoding
	 */
	protected final String getEncoding()
	{
		if ( _writer instanceof StreamWriter )
			return ( (StreamWriter) _writer ).getEncoding();
		return _format.getEncoding();
	}
	protected final StreamFormat getFormat()
	{
		return _format;
	}
	/**
	 * <H3>IMPLEMENTATION NOTES:</H3>
	 *
	 * The logic is quite complicated and very tightly coupled between three
	 * functions, so follow closely.
	 * <P>
	 * The currently written line is made up of everything that has been
	 * accumulated in {@link #_textLine} so far. After that come any number
	 * of spaces, as defined by {@link #_whiteSpaces} and another word, as
	 * held by {@link #_textWord}.
	 * <P>
	 * New text parts are added to the last word. At some point the line might
	 * grow too long and must be wrapped. When this happens, the line is broken
	 * between {@link #_textLine} and {@link #_textWord}, and {@link #_textWord}
	 * becomes the beginning of a new line.
	 * <P>
	 * When a space (or break point) is added after the last word, since the
	 * line has not been wrapped, the last word becomes part of the line by
	 * adding it to {@link #_textLine}. {@link #_whiteSpaces} starts recording
	 * again how many spaces come between the existing line and the new word
	 * which starts building up anew in {@link #_textWord}.
	 * <P>
	 * The word is build up gradually by calling one of the {@link #printPart}
	 * methods. A space is added by {@link #printSpace} and in compact format ,
	 * multiple spaces only count as one. A break is defined by {@link
	 * #printBreak}. A break separates two words at a point that might serve
	 * to break up the line but is not a space, for example, between the end
	 * of one element and the beginning of the next element.
	 * <P>
	 * The line can be broken forcefully and everything in {@link #_textLine}
	 * and {@link #_textWord} will print out by calling {@link #breakLine}.
	 * To write multiple words contained in a single string and support line
	 * wrapping, use {@link #printWords}. If spaces should not server as
	 * wrapping points (e.g. for attribute values), use {@link #printPart}.
	 * <P>
	 * {@link #wrapLine} is called to check if a line should be wrapped at its
	 * present point and {@link #flushLine} is called to do the wrapping, dumping
	 * the existing line and starting anew.
	 * <P>
	 * {@link #indent} and {@link #unindent} are used to control the indentation
	 * level of new lines. The indentation level is changed in {@link #_nextIndent}.
	 * If the current line is empty, or when a new line begin, the indentation is
	 * taken from {@link #_nextIndent} and used in {@link #_thisIndent}. The two
	 * variables allow the indentation level for the next line to change while
	 * the current line has not even printed yet and is only accumulating in
	 * memory. {@link #_thisIndent} is specified in spaces and cannot exceed 40.
	 */


	/**
	 * Returns the writer into which the document will be printed.
	 *
	 * @return The undelying writer
	 */
	protected final Writer getWriter()
	{
		return _writer;
	}
	/**
	 * Increment the indentation for the next line. Indentation is not
	 * supported in the compact format.
	 */
	protected void indent()
	{
		indent( + getFormat().getIndentSpaces() );
	}
	/**
	 * Change the indentation for the next line by the given amount (may be
	 * negative or positive). This change will affect the next line to be
	 * written, not the one being written right now.
	 *
	 * @param amount Amount to add to the indentation
	 */
	protected final void indent( int amount )
	{
		if ( ! getFormat().isCompact() )
		{
			_nextIndent += amount;
			if ( _nextIndent < 0 )
				_nextIndent = 0;
			if ( amount < 0 &&
				 (_textLine.length() + _whiteSpaces + _textWord.length() ) == 0 )
				_thisIndent = _nextIndent;
		}
	}
	/**
	 * Defines the boundary between two words that may serve as wrap point.
	 * For example, a line may be wrapped between the end of one element
	 * and that start of another one, even if there is no space between.
	 * In fact, {@link #printSpace} is based on {@link #printBreak}.
	 */
	protected final void printBreak()
	{
		// This is where the existing word is broken from the next one, either by
		// a space or a line wrapping point. The line is not wrapped at this point,
		// it has an opportunity before. All the spaces after the existing line and
		// the accumulated word are added to the line. The space count is reset
		// and a new word is started.
		if ( _textWord.length() > 0 )
		{
			while ( _whiteSpaces > 0 )
			{
				_textLine.append( ' ' );
				-- _whiteSpaces;
			}
			_textLine.append( _textWord );
			_textWord.setLength( 0 );
		}
	}
	/**
	 * Prints an XML/HTML element according to XML/HTML element rules.
	 * This  method is called directly by {@link #print(Element)} for any node
	 * that is an XML/HTML element. For specific printing rules, see {@link
	 * XMLPrinter#printElement} or {@link HTMLPrinter#printElement}.
	 * <P>
	 * An empty {@link java.lang.StringBuffer}, created by the calling method,
	 * is passed and should be used for fast string processing as necessary.
	 *
	 * @param elem The XML/HTML element to print
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @param preserveSpace
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected abstract void printElement( Element elem, StringBuffer text,
										  boolean preserveSpace )
		throws IOException;
	/**
	 * Prints an XML/HTML/DTD node according to XML/HTML/DTD node rules.
	 * Depending on the derived class, this method uses {@link #printElement}
	 * and {@link #printDocument} to provide formatting specific for XML, HTML
	 * or DTD documents.
	 * <P>
	 * The node and its children are formatted as follows:
	 * <UL>
	 * <LI>CDATA section is always printed on a separate line, spaces are
	 *  preserved.
	 * <LI>Comments are printed only if not in compact format, and in pretty
	 *  format are printed on a separate line
	 * <LI>Entity references are printed directly
	 * <LI>Processing instructions are running, spaces are preserved
	 * <LI>Text is running, spaces are preserved when not in compact format,
	 *  or when the including element dictates so
	 * <LI>Element is printed by {@link #printElement} method in derived class
	 * <LI>Document and DTD are printed by {@link #printDocument} method in
	 *  derived class
	 * </UL>
	 *
	 * @param name The XML/HTML node, element, document fragment or document to print
	 * @param preserveSpace True if this node and possibly its descendants should
	 *  preserve spaces
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see #printElement
	 * @see #printDocument
	 */
	protected final boolean printNode( Node node, boolean preserveSpace )
		throws IOException
	{
		StringBuffer    text;
		String          value;
		int             index;
		Node            child;

		text = new StringBuffer( 100 );
		switch ( node.getNodeType() )
		{
			case Node.CDATA_SECTION_NODE:
			{
				// CDATA section are printed on a separate line in pretty format.
				// The contents is not encoded, but is verified not to contain a
				// ']]>' sequence. Spaces are always preserved.
				if ( getFormat().isPretty() )
					breakLine();
				indent();
				value = node.getNodeValue();
				index = value.indexOf( "]]>" );
				if ( index >= 0 )
					text.append( "<![CDATA[" ).append( value.substring( 0, index ) ).append( "]]>" );
				else
					text.append( "<![CDATA[" ).append( value ).append( "]]>" );
				printWords( text, true );
				unindent();
				if ( getFormat().isPretty() )
					breakLine();
				return getFormat().isPretty();
			}

			case Node.COMMENT_NODE:
			{
				// Comments are not printed in compact format. In pretty format,
				// they are printed on a separate line(s). The contents is not
				// encoded, but is verified not to contain a '--' sequence.
				// Spaces are preserved in pretty mode.
				if ( ! getFormat().isCompact() )
				{
					if ( getFormat().isPretty() )
						breakLine();
					indent();
					value = node.getNodeValue();
					index = value.indexOf( "--" );
					if ( index >= 0 )
						text.append( "<!--" ).append( value.substring( 0, index ) ).append( "-->" );
					else
						text.append( "<!--" ).append( value ).append( "-->" );
					printWords( text, getFormat().isPretty() );
					unindent();
					if ( getFormat().isPretty() )
						breakLine();
					return getFormat().isPretty();
				}
				return false;
			}

			case Node.ENTITY_REFERENCE_NODE:
			{
				// Entity reference print directly in text, do not break or pause.
				text.append( '&' ).append( node.getNodeName() ).append( ';' );
				printPart( text );
				return false;
			}

			case Node.PROCESSING_INSTRUCTION_NODE:
			{
				// Processing instructions are printed as part of the text and are
				// verified not to contain the '?>' sequence. Spaces are always preserved.
				if ( getFormat().isPretty() )
					breakLine();
				indent();
				value = node.getNodeName();
				index = value.indexOf( "?>" );
				if ( index >= 0 )
					text.append( "<?" ).append( value.substring( 0, index ) );
				else
					text.append( "<?" ).append( value );
				text.append( ' ' );
				value = node.getNodeValue();
				index = value.indexOf( "?>" );
				if ( index >= 0 )
					text.append( value.substring( 0, index ) ).append( "?>" );
				else
					text.append( value ).append( "?>" );
				printWords( text, true );
				unindent();
				if ( getFormat().isPretty() )
					breakLine();
				return getFormat().isPretty();
			}

			case Node.TEXT_NODE:
			{
				// Text prints directly, preserving space depending on format.
				printWords( encode( node.getNodeValue(), preserveSpace ), preserveSpace );
				return false;
			}

			case Node.ELEMENT_NODE:
			{
				printElement( (Element) node, text, preserveSpace );
				return false;
			}

			default:
			{
				// Simply print contents of node (document, fragment, etc.).
				child = node.getFirstChild();
				while ( child != null )
				{
					printNode( child, preserveSpace );
					child = child.getNextSibling();
				}
				return false;
			}

		}
	}
	/**
	 * Writes part of a line. Used directly or by calling {@link #printWords}.
	 * Lines may be wrapped but only at space boundaries defined with {@link
	 * #printSpace} or {@link #printBreak}.
	 *
	 * @param ch A single character
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void printPart( char ch )
		throws IOException
	{
		// Extend the current word and check if the line should be wrapped at
		// this point.
		_textWord.append( ch );
		wrapLine();
	}
	/**
	 * Writes part of a line. Used directly or by calling {@link #printWords}.
	 * Lines may be wrapped but only at space boundaries defined with {@link
	 * #printSpace} or {@link #printBreak}.
	 *
	 * @param text Textual string
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void printPart( String text )
		throws IOException
	{
		// Extend the current word and check if the line should be wrapped at
		// this point.
		_textWord.append( text );
		wrapLine();
	}
	/**
	 * Writes part of a line. Used directly or by calling {@link #printWords}.
	 * Lines may be wrapped but only at space boundaries defined with {@link
	 * #printSpace} or {@link #printBreak}.
	 *
	 * @param text Textual string
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void printPart( StringBuffer text )
		throws IOException
	{
		// Extend the current word and check if the line should be wrapped at
		// this point.
		_textWord.append( text );
		wrapLine();
	}
	/**
	 * Writes a single space. Used directly or by calling {@link #printWords}.
	 * In compact formt multiple calls will result in a single space being
	 * written. Lines may be wrapped at space boundaries so defined.
	 */
	protected final void printSpace()
	{
		// This is a boundary point between two words.
		printBreak();
		// Add a second space only if not in compact format.
		if ( _whiteSpaces == 0 || ! getFormat().isCompact() )
			++ _whiteSpaces;
	}
	/**
	 * Writes the textual string as a sequence of words. Words are separated
	 * by spaces and the line may be wrapped at word boundary wrap width is
	 * specified. Multiple whitespaces are converted to a single space, unless
	 * <TT>preserveSpace</TT> is true, in which case spaces are printed as
	 * they are, including tabs and line ending CR or NL.
	 *
	 * @param text The text to write
	 * @param preserveSpace True is spaces should be preserved
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void printWords( String text, boolean preserveSpace )
		throws IOException
	{
		StringTokenizer tokenizer;
		String          token;

		if ( preserveSpace )
		{
			// Preserving spaces: tokenizer will return token as well as spaces.
			// If token is a tab, print the exact tab character (wrapping support
			// is weak in this case); if token is a CR/NL, break to a new line;
			// otherwise, print space or token as is.
			tokenizer = new StringTokenizer( text, " \f\t\n\r", true );
			while ( tokenizer.hasMoreTokens() )
			{
				token = tokenizer.nextToken();
				switch ( token.charAt( 0 ) )
				{
				case 0x09:
					printPart( (char) 0x09 );
					break;
				case 0x0A:
				case 0x0D:
					breakLine();
					break;
				case 0x20:
					printSpace();
					break;
				default:
					printPart( token );
					break;
				}
			}
		}
		else
		{
			// Not preserving spaces: tokenizer will return tokens, print each
			// token separated by a single space.
			tokenizer = new StringTokenizer( text.trim(), " \f\t\n\r", false );
			if ( tokenizer.hasMoreTokens() )
			{
				token = tokenizer.nextToken();
				printPart( token );
			}
			while ( tokenizer.hasMoreTokens() )
			{
				token = tokenizer.nextToken();
				printSpace();
				printPart( token );
			}
		}
	}
	/**
	 * Writes the textual string as a sequence of words. Words are separated
	 * by spaces and the line may be wrapped at word boundary wrap width is
	 * specified. Multiple whitespaces are converted to a single space, unless
	 * <TT>preserveSpace</TT> is true, in which case spaces are printed as
	 * they are, including tabs and line ending CR or NL.
	 *
	 * @param text The text to write
	 * @param preserveSpace True is spaces should be preserved
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void printWords( StringBuffer text, boolean spaces )
		throws IOException
	{
		printWords( text.toString(), spaces );
	}
	/**
	 * Prints a document. It is recommended to print a whole document or
	 * document tree at once rather than an element at a time if the output
	 * is to be human readable.
	 *
	 * @param doc The XML/HTML document to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public abstract void publish( Document doc )
		throws IOException;
	/**
	 * Prints a single element. It is recommended to print a whole document
	 * or document tree at once rather than an element at a time if the output
	 * is to be human readable.
	 *
	 * @param elem The XML/HTML element to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public synchronized void publish( Node node )
		throws IOException
	{
		printNode( node, getFormat().isPreserveSpace() );
		breakLine();
		flush();
	}
	/**
	 * Decrement the indentation for the next line. Indentation is not
	 * supported in the compact format.
	 */
	protected void unindent()
	{
		indent( - getFormat().getIndentSpaces() );
	}
	/**
	 * Called by the {@link #printPart} methods to check whether the line should
	 * be wrapped at this point. If the line accumulated so far is too long, it is
	 * broken at the last break point (or space) and the current word is carried
	 * on to the next line. If the line consists only of the current word, there
	 * is no wrapping.
	 *
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected final void wrapLine()
		throws IOException
	{
		if ( ( getFormat().isPretty() && getFormat().getLineWrap() > 0 ) &&
			 _thisIndent + _textLine.length() +
			 _whiteSpaces + _textWord.length() > getFormat().getLineWrap() )
			flushLine();
	}
}