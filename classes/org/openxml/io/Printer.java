package org.openxml.io;

/**
 * org/openxml/io/Printer.java
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


/**
 * Implements a base printed for XML/HTML/DTD documents. Derived classes are
 * used by the application through three {@link #print} methods. They implement
 * {@link #printDocument} and {@link #printElement} methods.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XMLStreamWriter
 * @see XMLPrinter
 * @see HTMLPrinter
 * @see DTDPrinter
 * 
 * @deprecated
 *  This package has become obsolete in favor of the <a
 *  href="../x3p/package-summary.html">X3P Publisher and Producer APIs</a>.
 *  This package is temporarily provided for backward compatibility but
 *  will not be included in release 1.1.
 */
public abstract class Printer
{


	/**
	 * This is the default printing mode. If this mode is selected, output will
	 * be printed in a human readable form, but no effort will be made to pretty
	 * print it by indenting elements and breaking them on separate lines.
	 * Long lines will be wrapped around. Selecting any other mode cancels this
	 * mode.
	 */
	public static final int        DEFAULT = 0x00;


	/**
	 * If this mode is selected, output will be pretty printed to make it human
	 * readable. Lines will be broken at element boundaries, comments will come
	 * on separate lines, extra spaces will be used, elements will be indented
	 * and lines will be wrapped. The result would be very readable and correct.
	 * Selecting this mode cancells {@link #NOWRAP} and {@link #COMPACT}.
	 */
	public static final int        PRETTY = 0x01;


	/**
	 * If this mode is selected, the output is printed as compact as possible.
	 * Multiple whitespaces are combined into one (except for elements that must
	 * preserve spaces) and comments are not printed. This mode can be combined
	 * with {@link #NOWRAP}.
	 */
	public static final int        COMPACT = 0x02;


	/**
	 * If this mode is selected, output is not wrapped at the end of the line.
	 * The default is to wrap lines at a predetermined line width for readability
	 * and easier handling in text editors, even if the output is not pretty
	 * printed (see {@link #PRETTY}).
	 */
	public static final int        NOWRAP = 0x04;


	/**
	 * If this mode is selected, only entity and notation declarations are
	 * printed for a DTD document or the DTD portion of an XML document.
	 */
	public static final int        DTD_ENTITIES_ONLY = 0x08;


	/**
	 * If this mode is selected, the XML document is printed standalone with
	 * all DTD declarations included in the document body, rather than linking
	 * to an external DTD.
	 */
	public static final int        DTD_STANDALONE = 0x10;


	/**
	 */
	public static final int        DTD_CONTENT = 0x20;


	/**
	 * Holds the currently accumulating text line. A string buffer is used to
	 * save unnecessary string creation/deletion. This buffer will constantly
	 * be reused by deleting its contents instead of reallocating it.
	 */
	private StringBuffer    _textLine = new StringBuffer( 80 );


	/**
	 * Holds the currently accumulating word that follows {@link #_textLine}.
	 * When the end of the word is identified by a call to {@link #printSpace}
	 * or {@link #printBreak}, this word is added to the accumulated line.
	 * This buffer will constantly be reused by deleting its contents instead
	 * of reallocating it.
	 */
	private StringBuffer    _textWord = new StringBuffer( 20 );


	/**
	 * Counts how many white spaces come between the accumulated line and the
	 * current word. In {@link #COMPACT} mode, unless the element requires that
	 * spaces be preserved, multiple spaces will be folded into one.
	 */
	private int             _whiteSpaces;


	/**
	 * Holds the indentation for the current line that is now accumulating in
	 * memory and will be sent for printing shortly.
	 */
	private int                _thisIndent;


	/**
	 * Holds the indentation for the next line to be printed. After this line is
	 * printed, {@link #_nextIndent} is assigned to {@link #_thisIndent}.
	 */
	private int                _nextIndent;


	/**
	 * Number of spaces in each indentation level. This value is added or removed
	 * from the indentation level when calling {@link #indent} or {@link #unindent}.
	 */
	private int                _indentSpaces = 4;


	/**
	 * The width at which to wrap lines. Lines longer than {@line #_lineWidth}
	 * are wrapped at the last space of wrap point before the line width, or
	 * immediately afterwards.
	 */
	private int                _lineWidth;


	/**
	 * The current writing mode, one of {@link #PRETTY}, {@link #COMPACT},
	 * {@link #NOWRAP} or {@link #COMPACT} + {@link #NOWRAP}.
	 */
	private int                _mode;


	/**
	 * The {@link java.io.Writer} into which XML text is written.
	 */
	private Writer            _writer;


	/**
	 * The line separator, if one has been specified.
	 *
	 * @see #setLineSeparator
	 */
	private String            _lineSeparator;


	/**
	 * Construct a new Printer. A {@link java.io.Writer} must be supplied into
	 * which the text is written. Supported printing modes are default, {@link
	 * #PRETTY}, {@link #COMPACT}, {@link #NOWRAP} and {@link #COMPACT} +
	 * {@link #NOWRAP}.
	 *
	 * @param write The underlying {@link java.io.Writer}
	 * @param mode The writing mode
	 * @param lineWidth The width of each line
	 * @param indentSpaces The number of spaces to use for indentation
	 */
	public Printer( Writer writer, int mode, int lineWidth, int indentSpaces )
	{
		_writer = writer;
		_mode = mode;
		// Mode can be default (zero), PRETTY, COMPACT, NOWRAP or COMPACT + NOWRAP.
		// If PRETTY is selected, it cancells COMPACT and NOWRAP.
		if ( mode( PRETTY ) )
			_mode = ( _mode & ~( COMPACT | NOWRAP ) );
		if ( lineWidth <= 0 )
			throw new IllegalStateException( "Argument 'lineWidth' is negative." );
		if ( lineWidth < 40 )
			lineWidth = 40;
		_lineWidth = lineWidth;
		if ( indentSpaces <= 0 )
			throw new IllegalStateException( "Argument 'indentSpaces' is negative." );
		_indentSpaces = indentSpaces;
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
	public final void close()
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
	/**
	 * Flush all completed output lines. This method is equivalent to calling
	 * {@link java.io.Writer#flush} on the output stream or writer directly.
	 * Lines that have been completed are immediately sent to the output stream,
	 * which is buffered. The current line is not affected.
	 */
	public final void flush()
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
		if ( _textLine.length() > 0 )
		{
			// Make sure the indentation does not blow us away, then print it.
			if ( _thisIndent > 40 )
				_thisIndent = 40;
			while ( _thisIndent > 0 )
			{
				_writer.write( ' ' );
				-- _thisIndent;
			}
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
				if ( _lineSeparator == null )
					_writer.write( 0x0A );
				else
					_writer.write( _lineSeparator );
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
	protected String getCharacterRef( char ch )
	{
		return null;
	}
	/**
	 * Returns the output character encoding, if known. The output character
	 * encoding is only accessible if it has been specified in the constructor.
	 * If the encoding is unknown or cannot be determined, null is returned.
	 *
	 * @return The character encoding
	 */
	protected final String getEncoding()
	{
		if ( _writer instanceof XMLStreamWriter )
			return ( (XMLStreamWriter) _writer ).getEncoding();
		return null;
	}
	/**
	 * Returns the current mode. This method is used to hold the existing mode
	 * when temporarily switching to a different mode. To determine if a specific
	 * mode is in effect, use the {@link #mode(int)} method.
	 *
	 * @return The current mode
	 */
	protected final int getMode()
	{
		return _mode;
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
	 * methods. A space is added by {@link #printSpace} and in {@link #COMPACT}
	 * mode, multiple spaces only count as one. A break is defined by {@link
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
	 * Increment the indentation for the next line by {@link #_indentSpaces}.
	 * Indentation is not supported in the {@link #COMPACT} and {@link #NOWRAP}
	 * modes.
	 */
	protected void indent()
	{
		indent( + _indentSpaces );
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
		if ( ! mode( COMPACT ) && ! mode( NOWRAP ) )
		{
			_nextIndent += amount;
			if ( _nextIndent < 0 )
				_nextIndent = 0;
			if ( amount < 0 &&
				 (_textLine.length() + _whiteSpaces + _textWord.length() )== 0 )
				_thisIndent = _nextIndent;
		}
	}
	/**
	 * Returns true if the specified printing mode is in effect. For example,
	 * <TT>mode( PRETTY )</TT> will return true if pretty printing mode
	 * has been selected.
	 *
	 * @param mode The mode(s) to check
	 * @return True if the mode(s) are in effect
	 */
	protected final boolean mode( int mode )
	{
		return ( _mode & mode ) != 0;
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
	public abstract void print( Document doc )
		throws IOException;
	/**
	 * Prints a document fragment. It is recommended to print a whole
	 * document or document tree at once rather than an element at a time
	 * if the output is to be human readable.
	 *
	 * @param docFrag The XML/HTML document fragment to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	public abstract void print( DocumentFragment docFrag )
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
	public abstract void print( Element elem )
		throws IOException;
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
	 * Prints an XML/HTML/DTD document or document fragment according to
	 * XML/HTML/DTD document rules. This method is called directly by {@link
	 * #print(Element)} for any node that is a document or document fragment.
	 *
	 * @param doc The XML/HTML/DTD document or document fragment
	 * @param text Empty {@link java.lang.StringBuffer} used for fast text
	 *  processing
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected abstract void printDocument( Node doc, StringBuffer text )
		throws IOException;
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
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 */
	protected abstract void printElement( Element elem, StringBuffer text )
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
	 * <LI>Comments are printed only if not in {@link #COMPACT} mode, and in
	 *  {@link #PRETTY} mode are printed on a separate line
	 * <LI>Entity references are printed directly
	 * <LI>Processing instructions are running, spaces are preserved
	 * <LI>Text is running, spaces are preserved when not in {@link #COMPACT}
	 *  mode, or when the including element dictates so
	 * <LI>Element is printed by {@link #printElement} method in derived class
	 * <LI>Document and DTD are printed by {@link #printDocument} method in
	 *  derived class
	 * </UL>
	 *
	 * @param name The XML/HTML node, element, document fragment or document to print
	 * @throws IOException An I/O exception occured while writing to the
	 *  underlying output stream
	 * @see #printElement
	 * @see #printDocument
	 */
	protected final void printNode( Node node )
		throws IOException
	{
		StringBuffer    text;
		String            value;
		int                index;
		Node            child;

		text = new StringBuffer( 100 );
		switch ( node.getNodeType() )
		{
		case Node.CDATA_SECTION_NODE:
			// CDATA section is always printed on a separate line. The contents
			// is not encoded, but is verified not to contain a ']]>' sequence.
			// Spaces are preserved.
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
			breakLine();
			break;

		case Node.COMMENT_NODE:
			// Comments are not printed in COMPACT mode. In PRETTY mode, they are
			// printed on a separate line(s). The contents is not encoded, but
			// is verified not to contain a '--' sequence. Spaces are preserved.
			if ( ! mode( COMPACT ) )
			{
				if ( mode( PRETTY ) )
					breakLine();
				indent();
				value = node.getNodeValue();
				index = value.indexOf( "--" );
				if ( index >= 0 )
					text.append( "<!--" ).append( value.substring( 0, index ) ).append( "-->" );
				else
					text.append( "<!--" ).append( value ).append( "-->" );
				printWords( text, true );
				unindent();
				if ( mode( PRETTY ) )
					breakLine();
			}
			break;

		case Node.ENTITY_REFERENCE_NODE:
			// Entity reference print directly in text, do not break or pause.
			text.append( '&' ).append( node.getNodeName() ).append( ';' );
			printPart( text );
			break;

		case Node.PROCESSING_INSTRUCTION_NODE:
			// Processing instructions are printed as part of the text and are
			// verified not to contain the '?>' sequence. Spaces are preserved.
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
			break;

		case Node.TEXT_NODE:
			// Text prints directly, preserving space depending on mode.
			printWords( encode( node.getNodeValue(), ! mode( COMPACT ) ),
						! mode( COMPACT ) );
			break;

		case Node.DOCUMENT_NODE:
		case Node.DOCUMENT_FRAGMENT_NODE:
			printDocument( node, text );
			break;

		case Node.ELEMENT_NODE:
			printElement( (Element) node, text );
			break;
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
	 * In {@link #COMPACT} mode multiple calls will result in a single space
	 * being written. Lines may be wrapped at space boundaries so defined.
	 */
	protected final void printSpace()
	{
		// This is a boundary point between two words.
		printBreak();
		// Add a second space only if not in COMPACT mode.
		if ( _whiteSpaces == 0 || ! mode( COMPACT ) )
			++ _whiteSpaces;
	}
	/**
	 * Writes the textual string as a sequence of words. Words are separated by
	 * spaces and the line may be wrapped at word boundary if not in {@link
	 * #NOWRAP} mode. Multiple whitespaces are converted to a single space, unless
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
		StringTokenizer    tokenizer;
		String            token;

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
	 * Writes the textual string as a sequence of words. Words are separated by
	 * spaces and the line may be wrapped at word boundary if not in {@link
	 * #NOWRAP} mode. Multiple whitespaces are converted to a single space, unless
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
	 * Change the default line separator. The default line separator is the
	 * new-line characater (0x0A). This is the acceptable line separator for
	 * sending documents over the Internet, and most implementations should
	 * use it. When passing documents to other applications or storing them
	 * locally, it might be useful to specify the line separator. The line
	 * separator suitable for the current machine can be obtained with
	 * <TT>System.getProperty( "line.separator" )</TT>. On Windows/DOS systems
	 * this will return NL-CR (0x0A, 0x0D).
	 *
	 * @param lineSeparator The new line separator as a string
	 */
	public final void setLineSeparator( String lineSeparator )
	{
		_lineSeparator = lineSeparator;
	}
	/**
	 * Sets the current mode. This method is used to temporarily switch to a
	 * different mode and back.
	 *
	 * @param newMode The new mode value
	 */
	protected final void setMode( int newMode )
	{
		_mode = newMode;
	}
	/**
	 * Decrement the indentation for the next line by {@link #_indentSpaces}.
	 * Indentation is not supported in the {@link #COMPACT} and {@link #NOWRAP}
	 * modes.
	 */
	protected void unindent()
	{
		indent( - _indentSpaces );
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
		if ( ! mode( NOWRAP ) && _thisIndent + _textLine.length() +
							 _whiteSpaces + _textWord.length() > _lineWidth )
			flushLine();
	}
}