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


package org.openxml.parser;


import java.text.MessageFormat;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.openxml.dom.*;
import org.openxml.util.Resources;


/**
 * Implements an error report facility which is also a SAX error handler.
 * This implementation is used by parsers to implement error reporting
 * selecting throwing, based on the {@link ErrorReport} interface.
 * This implementation also supports the SAX error handler interface,
 * so it can be used with other parsers as well.
 * <P>
 * If this error report is used with an OpenXML parser, the parser will
 * use the error report facility to extend the SAX error handler with
 * two features. It will skip warning reporting if they are not
 * desired by this error report, and it will report the level of errors.
 * When used with a SAX parser, all errors will be reported at the
 * default {@link #GENERAL} level.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:06 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see ErrorReport
 * @see org.xml.sax.ErrorHandler
 */
public class ErrorReportImpl
    implements ErrorHandler, ErrorReport
{
    
    
    public void warning( SAXParseException except )
        throws SAXException
    {
        reportError( WARNING, except );
    }

    
    public void error( SAXParseException except )
        throws SAXException
    {
        reportError( GENERAL, except );
    }

    
    public void fatalError( SAXParseException except )
        throws SAXException
    {
        reportError( FATAL, except );
    }
    

    public void reportError( int errorLevel, String location, SAXException except )
        throws SAXException
    {
        reportError( errorLevel, location, except );
    }

    
    public void reportError( int errorLevel, Node node, SAXException except )
        throws SAXException
    {
        reportError( errorLevel, node, except );
    }

    
    public void reportError( int errorLevel, SAXException except )
        throws SAXException
    {
        reportError( errorLevel, except, except );
    }

       
    public void fatalError( Exception except )
        throws SAXException
    {
        if ( ! ( except instanceof SAXException ) )
            except = new SAXException( except );
        reportError( FATAL, "", (SAXException) except );
    }

    
    public boolean isReporting( int errorLevel)
    {
        return ( _reportLevel <= errorLevel ||
                 _stopAtLevel <= errorLevel );
    }
    
    
    public int getCount()
    {
        return _errorCount;
    }
    
    
    public int getErrorLevel( int index )
    {
        if ( index < 0 || index >= _errorCount )
            throw new IndexOutOfBoundsException( Resources.message( "Error007" ) );
        return _errorList[ index ]._errorLevel;
    }
    
    
    public SAXException getException( int index )
    {
        if ( index < 0 || index >= _errorCount )
            throw new IndexOutOfBoundsException( Resources.message( "Error007" ) );
        return _errorList[ index ]._except;
    }
    

    public String getMessage( int index )
    {
        if ( index < 0 || index >= _errorCount )
            throw new IndexOutOfBoundsException( Resources.message( "Error007" ) );
        return formatMessage( _errorList[ index ] );
    }
    
    
    public SAXException getLastException()
    {
        return _lastError._except;
    }

    
    public String getLastMessage()
    {
        return formatMessage( _lastError );
    }
    
    
    public String[] listMessages( int fromErrorLevel )
    {
        int         count;
        int         i;
        String[]    messages;
        
        if ( fromErrorLevel < WARNING || fromErrorLevel > STOP_AT_FATAL )
            throw new IllegalArgumentException( Resources.format( "Error008", "fromErrorLevel", "error level" ) );
        count = 0;
        for ( i = 0 ; i < _errorCount ; ++i )
            if ( _errorList[ i ]._errorLevel >= fromErrorLevel )
                ++ count;
        messages = new String[ count ];
        count = 0;
        for ( i = 0 ; i < _errorCount ; ++i )
            if ( _errorList[ i ]._errorLevel >= fromErrorLevel )
            {
                messages[ count ] = formatMessage( _errorList[ i ] );
                ++count;
            }
        return messages;
    }

    
    public SAXException[] listExceptions( int fromErrorLevel )
    {
        int             count;
        int             i;
        SAXException[]  excepts;
        
        if ( fromErrorLevel < WARNING || fromErrorLevel > STOP_AT_FATAL )
            throw new IllegalArgumentException( Resources.format( "Error008", "fromErrorLevel", "error level" ) );
        count = 0;
        for ( i = 0 ; i < _errorCount ; ++i )
            if ( _errorList[ i ]._errorLevel >= fromErrorLevel )
                ++ count;
        excepts = new SAXException[ count ];
        count = 0;
        for ( i = 0 ; i < _errorCount ; ++i )
            if ( _errorList[ i ]._errorLevel >= fromErrorLevel )
            {
                excepts[ count ] = _errorList[ i ]._except;
                ++count;
            }
        return excepts;
    }


    /**
     * Internal error reporting method. This method is called to report an
     * error and based on its level will either record it, throw an
     * exception or plainly ignore it. Warnings never throw an exception,
     * fatal errors always throw an exception.
     * 
     * @param errorLevel The level of the reported error
     * @param location Object that describes location at which error occured
     * @param except The error's exception
     */
    protected void reportError( int errorLevel, Object location, SAXException except )
        throws SAXException
    {
        ErrorReportEntry[]  newList;
        ErrorReportEntry    error = null;
        
        if ( errorLevel < WARNING || errorLevel > STOP_AT_FATAL )
            throw new IllegalArgumentException( Resources.format( "Error008", "errorLevel", "error level" ) );
        if ( _reportLevel > WARNING || _reportLevel <= errorLevel )
        {
            error = new ErrorReportEntry( errorLevel, location, except );
            if ( _reportLevel > WARNING )
                _lastError = error;
        }
        
        if ( _reportLevel <= errorLevel )
        {
            if ( _errorList == null )
            {
                _errorList = new ErrorReportEntry[ 8 ];
                _errorList[ 0 ] = error;
                _errorCount = 1;
            }
            else
            {
                if ( _errorCount == _errorList.length )
                {
                    newList = new ErrorReportEntry[ _errorCount + 8 ];
                    System.arraycopy( _errorList, 0, newList, 0, _errorCount );
                    _errorList = newList;
                }
                _errorList[ _errorCount ] = error;
                ++ _errorCount;
            }
        }
        if ( _stopAtLevel <= errorLevel )
            throw except;
    }
    

    /**
     * Given the error record returns a suitably formatted string.
     * The error location is formatted using {@link #formatLocation}.
     * <P>
     * The following parameters are used in formatting the message:
     * <UL>
     * <LI>{0} Error level (integer)
     * <LI>{1} Location (from {@link #formatLocation})
     * <LI>{2} Error exception class name
     * <LI>{3} Error message
     * </UL>
     * 
     * @param error The error record
     * @return The formatted error message
     */
    protected String formatMessage( ErrorReportEntry error )
    {
        return _messageFormat.format( new Object[] { new Integer( error._errorLevel ),
            formatLocation( error._location ), error._except.getClass().getName(),
            error._except.getMessage() } );
    }

    
    /**
     * Given the location returns a suitably formatted string.
     * If the location is a {@link Locator} or {@link SAXException},
     * the location in the source document is returned in a suitable
     * format; if the location is a DOM node, its location in the
     * document tree is returned; if the location is a string, or a
     * null, it is returned as is.
     * <P>
     * The following parameters are used in formatting the location
     * in the source document:
     * <UL>
     * <LI>{0} Document system identifier
     * <LI>{1} Document public identifier
     * <LI>{2} Line number (1-based)
     * <LI>{3} Column number in line (1-based)
     * </UL>
     * For location in the document tree, starting with the root, each
     * parent node is printed as node name followed by '/', e.g.:
     * <PRE>HTML/HEAD/TITLE/#text</PRE>
     * 
     * @param location The location object
     * @return The formatted error location
     */
    protected String formatLocation( Object location )
    {
        Locator             locator;
        SAXParseException   except;
        Node                node;
        StringBuffer        text;
        
        // No location, return no location string.
        if ( location == null )
            return null;
        // Location is a locator, format the document, line number and
        // other information conveyed in the locator and return that
        // location string.
        if ( location instanceof Locator )
        {
            locator = (Locator) location;
            return _locationFormat.format( new Object[] { locator.getSystemId(), locator.getPublicId(),
                new Integer( locator.getLineNumber() ), new Integer( locator.getColumnNumber() ) } );
        }
        else
        // Location is a SAX parser exception, this works just like the
        // Locator case above.
        if ( location instanceof SAXParseException )
        {
            except = (SAXParseException) location;
            return _locationFormat.format( new Object[] { except.getSystemId(), except.getPublicId(),
                new Integer( except.getLineNumber() ), new Integer( except.getColumnNumber() ) } );
        }
        else
        // Location is a node. If the node has information about its
        // location in the source document, use that, otherwise, print
        // the node's heirarchy in the tree.
        if ( location instanceof Node )
        {
            node = (Node) location;
            // If DOM is OpenXML then do a simple test. Look for the
            // closest part of this node that is an element. If one is
            // found, check whether it has locator information and if so,
            // print the element's location in the source document.
            if ( node instanceof NodeImpl )
            {
                while ( node != null && ! ( node instanceof ElementImpl ) )
                    node = node.getParentNode();
                if ( node != null )
                {
		    /*
                    locator = ( (ElementImpl) node ).getLocator();
                    if ( locator != null )
                        return _locationFormat.format( new Object[] { locator.getSystemId(), locator.getPublicId(),
                            new Integer( locator.getLineNumber() ), new Integer( locator.getColumnNumber() ) } );
		    */
                }
                node = (Node) location;
            }
            // Print the node's location in the document tree by printing
            // the name of this node and each of its parents up to the
            // document itself.
            text = new StringBuffer( 40 );
            text.append( node.getNodeName() );
            node = node.getParentNode();
            while ( node != null && node.getNodeType() != Node.DOCUMENT_NODE )
            {
                text.insert( 0, '/' ).insert( 0, node.getNodeName() );
                node = node.getParentNode();
            }
            return text.toString();
        }
        else
        // Location is just a textual presentation, return it.
        if ( location instanceof String )
            return (String) location;
        return null;
    }
    
    
    /**
     * Create a new error reporter with the report level {@link
     * #REPORT_ALL_ERRORS} and the stop level {@link #STOP_AT_NO_ERROR}.
     * The application uses this to report all errors for the purpose of
     * bug fixing, but assumes that the application can recover and
     * continue in spite of errors.
     */
    public ErrorReportImpl()
    {
        this( STOP_AT_NO_ERROR, REPORT_ALL_ERRORS );
    }

    
    /**
     * Create a new error reporter with the specified report level and
     * stop level. <tt>reportLevel</tt> and <tt>stopAtLevel</tt> are
     * often used in the following combinations:
     * <UL>
     * <LI>{@link #STOP_AT_NO_ERROR}, {@link #REPORT_WITH_WARNING}:
     *   Parsing and locating errors in the document; all errors and
     *   warnings are reported at the end.
     * <LI>{@link #STOP_AT_NO_ERROR}, {@link #REPORT_ALL_ERRORS}:
     *   Working with user interaction; will attempt to report all errors
     *   for purpose of bug fixing, but assumes that the application can
     *   go on until the user stops it.
     * <LI>{@link #STOP_AT_FIRST_ERROR}, {@link #REPORT_NOTHING}:
     *   Working non-interactive: there should be no problem, if there is,
     *   processing stops and the last error is reported.
     * </UL>
     * 
     * @param stopAtLevel The error level at which to stop processing and
     *   throw an exception
     * @param reportLevel The error levels to report
     */
    public ErrorReportImpl( int stopAtLevel, int reportLevel )
    {
        if ( stopAtLevel <= WARNING || stopAtLevel > STOP_AT_FATAL )
            throw new IllegalArgumentException( Resources.format( "Error008", "stopAtLevel", "error level" ) );
        if ( reportLevel < WARNING || reportLevel > STOP_AT_FATAL )
            throw new IllegalArgumentException( Resources.format( "Error008", "reportLevel", "error level" ) );
        _stopAtLevel = stopAtLevel;
        _reportLevel = reportLevel;
    }
    
    
    private int         _stopAtLevel;
    
    
    private int         _reportLevel;
    
    
    private ErrorReportEntry[]  _errorList;
    
    
    private int                 _errorCount;
    
    
    private ErrorReportEntry    _lastError;
    
    
    private MessageFormat       _messageFormat =
        new MessageFormat( Resources.message( "Format001" ) );
//    private MessageFormat       _messageFormat =
//        new MessageFormat( "{1}: {0,choice,0#WARNING|1#WELL FORMED|#2VALIDITY|3#CONTENT|4#PROCESSING|5#GENERAL|6#FATAL}: {3}" );

    
//    private MessageFormat       _locationFormat = new MessageFormat( "{0}:{2}:{3}:" );
    private MessageFormat       _locationFormat =
        new MessageFormat( Resources.message( "Format002" ) );
    

}


class ErrorReportEntry
{
    
    
    ErrorReportEntry( int errorLevel, Object location, SAXException except )
    {
        _errorLevel = errorLevel;
        _location = location;
        _except = except;
    }
    
    
    SAXException    _except;
    
    
    Object          _location;
    
    
    int             _errorLevel;
    
    
}
