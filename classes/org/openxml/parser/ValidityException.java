package org.openxml.parser;

/**
 * org/openxml/parser/ParseException.java
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
 * Extends {@link ParseException} to define an exception of validity severity
 * (@link #ERROR_VALIDITY}. Validity exceptions are thrown only by validating
 * parsers and are recoverable.
 * <P>
 * Validity exceptions are thrown when the document does not conform to the DTD.
 * The parser might easily recover from and bypass a validity exception, and
 * so these exceptions are usually stored and extracted at the end. Hand built
 * HTML documents are rarely valid.
 * <P>
 * The parser will thrown an exception for the first validity error issued,
 * if the <TT>stopAtSeverity</TT> level is {@link #STOP_SEVERITY_VALIDITY}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ParseException
 */
public final class ValidityException
	extends ParseException
{
	
	
	/**
	 * Constructs a exception of validity severity.
	 * 
	 * @param source Identifies the source location of the error
	 * @param previous Previous stored exception
	 * @param message The reason for this exception
	 */
	ValidityException( SourceLocation source, ParseException previous, String message )
	{
		super( source, previous, message );
	}
	public int getLevel()
	{
		return ERROR_VALIDITY;
	}
}