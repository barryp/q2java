package org.openxml.util;

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


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class Log
{
	
	
	/**
	 * Emergency level 0.
	 */
	public static final short   LEVEL_EMERGENCY = 0;

	
	/**
	 * Alert level 1.
	 */
	public static final short   LEVEL_ALERT = 1;

	
	/**
	 * Critical level 2.
	 */
	public static final short   LEVEL_CRITICAL = 2;

	
	/**
	 * Error level 3.
	 */
	public static final short   LEVEL_ERROR = 3;

	
	/**
	 * Warning level 4.
	 */
	public static final short   LEVEL_WARNING = 4;

	
	/**
	 * Notice level 5.
	 */
	public static final short   LEVEL_NOTICE = 5;

	
	/**
	 * Info level 6.
	 */
	public static final short   LEVEL_INFO = 6;

	
	/**
	 * Debug level 7.
	 */
	public static final short   LEVEL_DEBUG = 7;
	
   
	
	public static boolean  DEBUG = false;
	
	
	private static Writer  _errorWriter;

	
	private static Writer  _outputWriter;
	
	
	private static String  _facility = "OpenXML";
	
	
	private static short   _outputLevel = LEVEL_DEBUG; // WARNING;
	
	
	private static short   _errorLevel = LEVEL_CRITICAL;
	
	
	private static short   _stackTraceLevel = LEVEL_DEBUG; // ERROR;
	
	
	private static boolean _printTime = false;
	
	
	private static boolean _printPrevException = true;
	
	
	static
	{
		_errorWriter = new OutputStreamWriter( System.err );
		_outputWriter = new OutputStreamWriter( System.out );
	}
	
	
	public static void debug( String message )
	{
		print( LEVEL_DEBUG, _facility, message );
		
	}
	public static void debug( Throwable thrw )
	{
		print( LEVEL_DEBUG, _facility, thrw );
		
	}
	public static void error( String message )
	{
		print( LEVEL_ERROR, _facility, message );
		
	}
	public static void error( Throwable thrw )
	{
		print( LEVEL_ERROR, _facility, thrw );
		
	}
	public static void info( String message )
	{
		print( LEVEL_INFO, _facility, message );
		
	}
	public static void info( Throwable thrw )
	{
		print( LEVEL_INFO, _facility, thrw );
		
	}
	private static String levelAsString( short level )
	{
		switch ( level )
		{
			case LEVEL_EMERGENCY:
				return "EMERGENCY";
			case LEVEL_ALERT:
				return "ALERT";
			case LEVEL_CRITICAL:
				return "CRITICAL";
			case LEVEL_ERROR:
				return "error";
			case LEVEL_WARNING:
				return "warning";
			case LEVEL_NOTICE:
				return "notice";
			case LEVEL_INFO:
				return "info";
			case LEVEL_DEBUG:
				return "debug";
			default:
				if ( level < 0 )
					return "EMERGENCY";
				else
				return "debug" + ( level - LEVEL_DEBUG + 1 );
		}
	}
	public static void print( short level, String message )
	{
		print( level, _facility, message );
		
	}
	public static void print( short level, String facility, String message )
	{
		StringBuffer    line;
		int             i;
		char            ch;

		if ( level <= _outputLevel || level <= _errorLevel )
		{
			line = new StringBuffer( 80 );
			if ( _printTime )
				line.append( '[' ).append( new Date().toString() ).append( "] " );
			line.append( '[' ).append( levelAsString( level ) ).append( "] " );
			if ( facility != null && ! facility.equals( _facility ) )
				line.append( '[' ).append( facility ).append( "] " );

			for ( i = 0 ; i < message.length() ; ++i )
			{
				ch = message.charAt( i );
				if ( ( ch >= 0x20 && ch < 0x7F ) || ch == 0x09 )
					line.append( ch );
				else
				if ( ch < 0x10 )
					line.append( "#0" ).append( Integer.toHexString( ch ) );
				else
					line.append( '#' ).append( Integer.toHexString( ch ) );
			}
			writeLine( level, line );
		}
	}
	public static void print( short level, String facility, Throwable thrw )
	{
		StringWriter    stack;
		StringTokenizer tokenizer;
		
		if ( level <= _outputLevel || level <= _errorLevel )
		{
			print( level ,facility, "Exception " + thrw.getClass().getName() +
									": " + thrw.getMessage() );
			if ( level <= _stackTraceLevel )
			{
				stack = new StringWriter();
				thrw.printStackTrace( new PrintWriter( stack ) );
				tokenizer = new StringTokenizer( stack.toString(), "\n" );
				while ( tokenizer.hasMoreTokens() )
					print( level, facility, tokenizer.nextToken() );
			}
		}        
	}
	public static void print( short level, Throwable thrw )
	{
		print( level, _facility, thrw );
		
	}
	private static void writeLine( short level, StringBuffer message )
	{
		try
		{
			if ( level <= _outputLevel )
			{
				_outputWriter.write( message.toString() );
				_outputWriter.write( '\n' );
				_outputWriter.flush();
			}
		}
		catch ( IOException except )
		{
		}
		try
		{
			if ( level <= _errorLevel )
			{
				_errorWriter.write( message.toString() );
				_errorWriter.write( '\n' );
				_outputWriter.flush();
			}
		}
		catch ( IOException except )
		{
		}
	}
}