package org.openxml.parser;

/**
 * org/openxml/parser/FatalParseException.java
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
 * Extends {@link ParseException} to define an exception of fatal severity
 * (@link #ERROR_FATAL}. Fatal exceptions are always thrown and often wrap an
 * underlying I/O or runtime exception. That base exception can be obtained
 * by calling {@link #getException}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ParseException
 */
public class FatalParseException
	extends ParseException
{
	
	
	/**
	 * Holds the base exception (usually an I/O exception) for which this
	 * exception was thrown.
	 * 
	 * @serial Reference to base exception
	 */
	private Exception        _baseException;
	
	
	/**
	 * Constructs a new exception based on an existing I/O or runtime exception.
	 * The message is based on the exception's class name and message, and the
	 * base exception can be obtained by calling {@link #getException}.
	 * 
	 * @param source Identifies the source location of the error
	 * @param previous Previous stored exception
	 * @param except The base exception that generated this exception
	 */
	FatalParseException( SourceLocation source, ParseException previous, Exception except )
	{
		super( source, previous, except.getClass().getName() + ": " + except.getMessage() );
		_baseException = except;
	}
	/**
	 * Constructs a exception of fatal severity.
	 * 
	 * @param source Identifies the source location of the error
	 * @param previous Previous stored exception
	 * @param message The reason for this exception
	 */
	FatalParseException( SourceLocation source, ParseException previous, String message )
	{
		super( source, previous, message );
	}
	/**
	 * Returns the base exception. If an I/O or runtime exception has triggered
	 * this exception, the base exception can be obtained with this method.
	 * 
	 * @return The base exception
	 */
	public Exception getException()
	{
		return _baseException;
	}
	public int getLevel()
	{
		return ERROR_FATAL;
	}
}