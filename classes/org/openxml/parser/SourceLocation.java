package org.openxml.parser;

/**
 * org/openxml/parser/SourceLocation.java
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


/**
 * Identifies a source document and a location in that source. This interface
 * is used to locate an error or to track progress through the source.
 * All methods are thread-safe.
 * <P>
 * {@link #getSourceURI} is called to obtain the URI identification of the
 * source. {@link #getSourcePosition} returns the current position in that
 * source, whereas {@link #getLineNumber} returns that position as a line
 * number (beginning at one).
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public interface SourceLocation
{

	
	/**
	 * Returns the current line number in the source. This method is called to
	 * locate an error, or track progress through the source. It is thread-safe.
	 * 
	 * @return The line number in the source (1..n)
	 */
	public int getLineNumber();
	/**
	 * Returns the current character position in the source. This method is called
	 * to track progress through the source. It is thread-safe.
	 * 
	 * @return The character position in the source (0..n)
	 */
	public int getSourcePosition();
	/**
	 * Returns the source URI. This method is called to locate an error, or track
	 * progress through the source. It is thread-safe. It returns the URI that
	 * identifies the source, or null if unknown.
	 * 
	 * @return The source URI or null
	 */
	public String getSourceURI();
}