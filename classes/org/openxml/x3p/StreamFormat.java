package org.openxml.x3p;

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


import java.util.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public class StreamFormat
{


	static final short   DOC_TYPE_UNKOWN = 0;
	static final short   DOC_TYPE_XML = 1;
	static final short   DOC_TYPE_HTML = 2;
	static final short   DOC_TYPE_XHTML = 3;
	static final short   DOC_TYPE_DTD = 4;


	static final short   DEFAULT_LINE_WRAP = 72;
	static final short   DEFAULT_INDENT_SPACES = 4;
	
	public static final StreamFormat    XML;
	public static final StreamFormat    XML_COMPACT;
	public static final StreamFormat    XML_PRETTY;
	public static final StreamFormat    HTML;
	public static final StreamFormat    HTML_COMPACT;
	public static final StreamFormat    HTML_PRETTY;
	public static final StreamFormat    XHTML;
	public static final StreamFormat    XHTML_COMPACT;
	public static final StreamFormat    XHTML_PRETTY;

	
	static
	{
		XML = new StreamFormat( DOC_TYPE_XML );
		XML_COMPACT = new StreamFormat( XML ).changeToCompact();
		XML_PRETTY = new StreamFormat( XML ).changeToPretty();
		HTML = new StreamFormat( DOC_TYPE_HTML );
		HTML_COMPACT = new StreamFormat( HTML ).changeToCompact();
		HTML_PRETTY = new StreamFormat( HTML ).changeToPretty();
		XHTML = new StreamFormat( DOC_TYPE_XHTML );
		XHTML_COMPACT = new StreamFormat( XHTML ).changeToCompact();
		XHTML_PRETTY = new StreamFormat( XHTML ).changeToPretty();
	}
	

	/**
	 * If this format is selected, output will be pretty printed to make it
	 * human readable. Lines will be broken at element boundaries, comments
	 * will come on separate lines, extra spaces will be used, elements will
	 * be indented and lines will be wrapped. The result would be very
	 * readable and correct.
	 */
	private boolean             _pretty = false;


	/**
	 * If this format is selected, the output is printed as compact as possible.
	 * Multiple whitespaces are combined into one (except for elements that must
	 * preserve spaces) and comments are not printed.
	 */
	private boolean             _compact = false;


	/**
	 * If this format is selected, the XML document is printed as standalone
	 * with all DTD declarations included in the document body, rather than
	 * linking to an external DTD.
	 */
	private boolean             _standalone = false;


	/**
	 * The line separator, if one has been specified.
	 */
	private String              _lineSeparator;


	/**
	 * Number of spaces in each indentation level. This value is added or
	 * removed from the indentation level when calling {@link #indent} and
	 * {@link #unindent}.
	 */
	private short               _indentSpaces = DEFAULT_INDENT_SPACES;


	/**
	 * The width at which to wrap lines. Lines longer than this are wrapped
	 * at the last space of wrap point before the line width, or immediately
	 * afterwards. If zero, lines are not wrapped.
	 */
	private short               _lineWrap = DEFAULT_LINE_WRAP;


	private short               _docType = DOC_TYPE_UNKOWN;


	private String              _encoding = "UTF8";
	
	
	private String              _publicId;


	private String              _systemId;

	
	private String              _internalDTD;

	
	private static Hashtable    _cache = new Hashtable();


	StreamFormat( StreamFormat copyFrom )
	{
		_pretty = copyFrom._pretty;
		_compact = copyFrom._compact;
		_standalone = copyFrom._standalone;
		_lineSeparator = copyFrom._lineSeparator;
		_indentSpaces = copyFrom._indentSpaces;
		_lineWrap = copyFrom._lineWrap;
		_docType = copyFrom._docType;
		_encoding = copyFrom._encoding;
		_internalDTD = copyFrom._internalDTD;
		_publicId = copyFrom._publicId;
		_systemId = copyFrom._systemId;
	}
	private StreamFormat( short docType )
	{
		_docType = docType;
	}
	public StreamFormat changeEncoding( String encoding )
	{
		StreamFormat    changed;
		
		if ( _encoding == encoding ||
			 ( _encoding != null && _encoding.equals( encoding ) ) )
			return this;
		changed = new StreamFormat( this );
		changed._encoding = encoding;
		return changed;
	}
	public StreamFormat changeExternalDTD( String publicId, String systemId )
	{
		StreamFormat    changed;
		
		if ( ( _publicId == publicId && _systemId == systemId ) ||
			 ( _publicId == publicId &&
			   _systemId != null && _systemId.equals( systemId ) ) ||
			 ( _publicId != null && _publicId.equals( publicId ) &&
			   _systemId != null && _systemId.equals( systemId ) ) )
			return this;
		changed = new StreamFormat( this );
		changed._publicId = publicId;
		changed._systemId = systemId;
		return changed;
	}
	public StreamFormat changeIndentSpaces( int indentSpaces )
	{
		StreamFormat    changed;
		
		if ( indentSpaces < 0 )
			indentSpaces = 0;
		else
		if ( indentSpaces > 16 )
			indentSpaces = 16;
		if ( _indentSpaces == indentSpaces )
			return this;
		changed = new StreamFormat( this );
		changed._indentSpaces = (short) indentSpaces;
		return changed;
	}
	public StreamFormat changeInternalDTD( String internalDTD )
	{
		StreamFormat    changed;
		
		if ( _internalDTD == internalDTD ||
			 ( _internalDTD != null && _internalDTD.equals( internalDTD ) ) )
			return this;
		changed = new StreamFormat( this );
		changed._internalDTD = internalDTD;
		return changed;
	}
	public StreamFormat changeLineSeparator( String lineSeparator )
	{
		StreamFormat    changed;
		
		if ( _lineSeparator == lineSeparator ||
			 ( _lineSeparator != null && _lineSeparator.equals( lineSeparator ) ) )
			return this;
		changed = new StreamFormat( this );
		changed._lineSeparator = lineSeparator;
		return changed;
	}
	public StreamFormat changeLineWrap( int lineWrap )
	{
		StreamFormat    changed;
		
		if ( lineWrap < 0 || lineWrap == 0 )
			lineWrap = 0;
		else
		if ( lineWrap < 40 )
			lineWrap = 40;
		if ( _lineWrap == lineWrap )
			return this;
		changed = new StreamFormat( this );
		changed._lineWrap = (short) lineWrap;
		return changed;
	}
	public StreamFormat changeStandalone( boolean standalone )
	{
		StreamFormat    changed;
		
		if ( _standalone == standalone )
			return this;
		changed = new StreamFormat( this );
		changed._standalone = standalone;
		return changed;
	}
	public StreamFormat changeToCompact()
	{
		StreamFormat    changed;
		
		if ( _compact )
			return this;
		changed = new StreamFormat( this );
		changed._compact = true;
		changed._pretty = false;
		return changed;
	}
	public StreamFormat changeToDefault()
	{
		StreamFormat    changed;
		
		if ( ! _compact && ! _pretty )
			return this;
		changed = new StreamFormat( this );
		changed._pretty = false;
		changed._compact = false;
		return changed;
	}
	public StreamFormat changeToHTML()
	{
		StreamFormat    changed;
		
		if ( _docType == DOC_TYPE_HTML )
			return this;
		changed = new StreamFormat( this );
		changed._docType = DOC_TYPE_HTML;
		return changed;
	}
	public StreamFormat changeToPretty()
	{
		StreamFormat    changed;
		
		if ( _pretty )
			return this;
		changed = new StreamFormat( this );
		changed._pretty = true;
		changed._compact = false;
		return changed;
	}
	public StreamFormat changeToXHTML()
	{
		StreamFormat    changed;
		
		if ( _docType == DOC_TYPE_XHTML )
			return this;
		changed = new StreamFormat( this );
		changed._docType = DOC_TYPE_XHTML;
		return changed;
	}
	public StreamFormat changeToXML()
	{
		StreamFormat    changed;
		
		if ( _docType == DOC_TYPE_XML )
			return this;
		changed = new StreamFormat( this );
		changed._docType = DOC_TYPE_XML;
		return changed;
	}
	public String getDTDPublicId()
	{
		return _publicId;
	}
	public String getDTDSystemId()
	{
		return _systemId;
	}
	public String getEncoding()
	{
		return _encoding;
	}
	public short getIndentSpaces()
	{
		return _indentSpaces;
	}
	public String getInternalDTD()
	{
		return _internalDTD;
	}
	public String getLineSeparator()
	{
		return _lineSeparator;
	}
	public short getLineWrap()
	{
		return _lineWrap;
	}
	public boolean isCompact()
	{
		return _compact;
	}
	public boolean isDefault()
	{
		return ( ! _pretty && ! _compact ) ;
	}
	public boolean isHTML()
	{
		return ( _docType == DOC_TYPE_HTML );
	}
	public boolean isPreserveSpace()
	{
		return ! _compact;
	}
	public boolean isPretty()
	{
		return _pretty;
	}
	public boolean isStandalone()
	{
		return _standalone;
	}
	public boolean isXHTML()
	{
		return ( _docType == DOC_TYPE_XHTML );
	}
	public boolean isXML()
	{
		return ( _docType == DOC_TYPE_XML );
	}
}