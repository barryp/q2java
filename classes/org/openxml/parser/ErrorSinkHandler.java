package org.openxml.parser;

/**
 * org/openxml/parser/ErrorSinkHandler.java
 * 
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING ORi DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 * 
 * The Initial Developer of this code under the License is Assaf Arkin.
 * Portions created by Assaf Arkin are Copyright (C) 1998, 1999.
 * All Rights Reserved.
 */


import org.openxml.io.Parser;


/**
 * Defines an interface for reporting errors to an external parser. When an entity
 * is parsed, errors must still be directed to the document parser. This is done
 * by specifying the document parser as the error sink for the entity parser.
 * The {@link #error} methods of the document parser are then called for each error
 * issued by the entity parser.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see SourceLocation
 * @see ParseException
 * @see org.openxml.io.Parser
 */
public interface ErrorSinkHandler
{
	
	
	/**
	 * Generates an exception of type {@link FatalParseException} to encapsulate
	 * an underlying I/O or runtime exception and throws that exception immediately.
	 * 
	 * @param except The underlying exception
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	public void error( Exception except )
		throws ParseException;
	/**
	 * Issues an error by the parser. The error is encapsulated in one of three
	 * exception classes, depending on it's severity:
	 * <UL>
	 * <LI>{@link FatalParseException} for {@link Parser#ERROR_FATAL} severity
	 * <LI>{@link WellFormedException} for {@link Parser#ERROR_WELL_FORMED}
	 *  severity
	 * <LI>{@link ValidityException} for {@link Parser#ERROR_VALIDITY} severity
	 * </UL>
	 * <P>
	 * The exception is then either thrown or stored. Exceptions of fatal severity
	 * are always thrown. Exceptions of well formed severity are thrown if the
	 * severity level has been set to {@link Parser#STOP_SEVERITY_WELL_FORMED}.
	 * Exceptions of validity are thrown in the severity level has been set to
	 * either {@link Parser#STOP_SEVERITY_VALIDITY} or to {@link
	 * Parser#STOP_SEVERITY_WELL_FORMED}.
	 * <P>
	 * Exceptions that are not thrown are stored and may be retrieved by calling
	 * {@link Parser#getLastException} to retrieve the last stored exceptions. Previous
	 * exceptions are retrieved by recursively calling {@link
	 * ParseException#getPrevious}. The error location is obtained by calling
	 * one of the {@link SourceLocation} methods on each exception.
	 * <P>
	 * This mechanism has been devised to allow the parser to fully read invalid
	 * and not well-formed documents into memory and still report errors back to
	 * the applications.
	 * 
	 * @param severity The severity of the exception
	 * @param message A descriptive message
	 * @throws ParseException A parsing error has been encountered, and based on
	 *  it severity, an exception is thrown to terminate parsing
	 */
	public void error( short severity, String message )
		throws ParseException;
}