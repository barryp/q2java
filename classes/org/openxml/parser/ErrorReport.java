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


import java.text.*;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * The error report facility is an implementation of the SAX error
 * handler. It is activated by SAX error events, but rather than just
 * throwing an exception, is capable of recording errors in an ordered
 * error list, and returning the errors as formatted messages. It can
 * also be used to report errors from X3P processors.
 * <P>
 * All errors are reported, stored and retrieved as SAX exceptions.
 * Errors reported by a parser are likely to derive from {@link
 * SAXException}, but the application should not assume all
 * errors extend this class.
 * <P>
 * Some applications are interested in stopping at the first error
 * and can request that the parsing/processing throw a SAX exception
 * at the first error (not warning) they encounter. Other applications
 * are interested in recovering from as many errors as possible, with
 * only fatal errors stopping the parsing/processing. The application
 * can then obtain an ordered list of errors including their location
 * and description, for example, for the purpose of printing a log or
 * error list in a GUI component.
 * <P>
 * When used with the OpenXML parser and compatible processors, each
 * error is given a level number. Two parameters are used to control
 * response to errors at different levels.
 * <P>
 * The report level determines which errors are to be recorded and
 * reported back to the application. The three most common values are
 * {@link #REPORT_WITH_WARNING} which reports all errors and warnings,
 * {@link #REPORT_ALL_ERRORS} which reports all errors but no warnings,
 * and {@link #REPORT_NOTHING} which supresses reporting.
 * <P>
 * The stop level determines which error level will stop the
 * parser/processor by throwing a SAX exception. The three most common
 * values are {@link #STOP_AT_NO_ERROR} which will only stop if a fatal
 * error is encountered (fatal errors are non recoverable), {@link
 * #STOP_AT_FIRST_ERROR} which will stop at the first error encountered,
 * and {@link #STOP_AT_CONTENT} which will swallow all parsing
 * errors, but stop at a content, processing or general error.
 * <P>
 * When used with any other parser or SAX event driver processor,
 * all errors are reported at {@link #GENERAL} level, so the
 * {@link #STOP_AT_CONTENT} resolution is meaningless.
 * <P>
 * Once errors have been accumulated, they can be retrieved by the
 * application in a variety of ways. The application should always be
 * interested in the last error recorded, if no exception was thrown.
 * The application can retrieve the error list one by one, either as
 * exceptions, formatted messages, or error level. The application can
 * also request an array of errors (exceptions or formatted messages)
 * at a given level.
 * <P>
 * The formatting of the error message consists of the error location,
 * it's level and it's message. When parsing the location is likely to
 * include the document's name (system identifier), line number and
 * column within the line. When processing the location is likely to
 * include a node or element's location in the document tree.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:06 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ErrorHandler
 * @see SAXException
 */
public interface ErrorReport
    extends ErrorHandler
{
    
    
    /**
     * Report a processing error at the specified level. A textual string
     * is used to identify the error's location. It is reported and
     * possibly thrown based on the error level.
     * 
     * @param errorLevel The error level to report
     * @param location The location at which the error occured
     * @param except The exception to be reported
     * @throws SAXException Will throw the exception if set to stop at
     *  this error level
     */
    public void reportError( int errorLevel, String location, SAXException except )
        throws SAXException;

    
    /**
     * Report a processing error at the specified level. A document tree
     * node is used to identify the error's location in the document.
     * It is reported and possibly thrown based on the error level.
     * 
     * @param errorLevel The error level to report
     * @param node The document tree node at which the error occured
     * @param except The exception to be reported
     * @throws SAXException Will throw the exception if set to stop at
     *  this error level
     */
    public void reportError( int errorLevel, Node node, SAXException except )
        throws SAXException;

    
    /**
     * Report a parse error at the specified level. The parse exception
     * already contains the error's location in the source document.
     * It is reported and possibly thrown based on the error level.
     * 
     * @param errorLevel The error level to report
     * @param except The exception to be reported
     * @throws SAXException Will throw the exception if set to stop at
     *  this error level
     */
    public void reportError( int errorLevel, SAXException except )
        throws SAXException;


    /**
     * Report a fatal exception derived from an exception. The exception
     * is usually an I/O or runtime exception, which is transformed to
     * and reported as a fatal error. This method will always throw an
     * exception.
     * 
     * @param except The exception to be reported as fatal
     * @throws SAXException The exception is reported and thrown as this
     *  SAX exception
     */
    public void fatalError( Exception except )
        throws SAXException;


    /**
     * True if errors in this level are to be reported. This method
     * can be used to determine if this report is interested in an
     * error of a particular level (e.g. warnings are often ignored).
     * 
     * @param erroLevel The error level to be reported
     * @return True if interested in this error level
     */
    public boolean isReporting( int errorLevel );
    
    
    /**
     * Return the last error exception. This is the last error reported
     * to this facility, even if it was not stored; it will not be a
     * warning.
     *  
     * @return The error as exception
     */
    public SAXException getLastException();


    /**
     * Return the last error as formatted message. This is the last error
     * reported to this facility, even if it was not stored; it will not
     * be a warning. The error message is formatted to contain the
     * location of the error and the description of the message. The
     * formatted string is returned in the current locale.
     * 
     * @return The formatted message
     */
    public String getLastMessage();
    
    
    /**
     * Returns a list of error messages. The returned list is in the
     * same order in which the errors were reported. To selectively
     * view errors, set <tt>fromErrorLevel</tt> to a value that is
     * higher than a {@link #WARNING}.
     * 
     * @param fromErrorLevel Return only errors of this or higher
     *  error level
     * @return List of error message (could be empty)
     */
    public String[] listMessages( int fromErrorLevel );

    
    /**
     * Returns a list of error exceptions. The returned list is in the
     * same order in which the errors were reported. To selectively
     * view errors, set <tt>fromErrorLevel</tt> to a value that is
     * higher than a {@link #WARNING}.
     * 
     * @param fromErrorLevel Return only errors of this or higher
     *  error level
     * @return List of errors (could be empty)
     */
    public SAXException[] listExceptions( int fromErrorLevel );


    /**
     * Returns the number of errors contained in this report.
     * It is possible that new errors will be added to this report
     * after this method has returned.
     * 
     * @param Number of errors in report
     */
    public int getCount();
    
    
    /**
     * Returns the error's exception. Each error is associated with a
     * single {@link SAXException} exception, {@link org.xml.sax.SAXParseException}
     * if the error was generated by a parser.
     * 
     * @param index The error message number
     * @return The error exception
     */
    public SAXException getException( int index );
    
    
    /**
     * Returns formatted error message. The error message is formatted
     * to contain the location of the error and the description of the
     * message. The formatted string is returned in the current locale.
     * 
     * @param index The error message number
     * @return The formatted message
     */
    public String getMessage( int index );
    
    
    /**
     * Returns the error level of the numbered error. This method can
     * be used to tell if a given error is fatal, warning or other.
     * 
     * @param index The error message number
     * @return The error level
     */
    public int getErrorLevel( int index );
    
    
    /**
     * Warning. By definition warnings do not convey any error status,
     * they merely point at the potential for an error. Usually warnings
     * are not reported and they cannot be stopped at. 
     */
    public static final int WARNING = 0;
    
    
    /**
     * Well formed document error level. This error level indicates that
     * the source XML document is not well formed (e.g. attribute value
     * not quoted). Well formed errors are very common in HTML files,
     * but should generally not occur in XML files. Generally the parser
     * will report but not stop at well formed errors.
     */
    public static final int WELL_FORMED = 1;
    
    
    /**
     * Document validity error. This error level indicates that the
     * document is not valid according to the specified content model.
     * Validitiy errors are thrown by a validating parser or a validating
     * processor, and usually result in corrective action (such as
     * adding or removing an element). Generally the parser will report
     * but not stop at validity errors.
     */
    public static final int VALIDITY = 2;
    

    /**
     * Document content error. This error level indicates an error in
     * the actual document content, given that the document is properly
     * constructed. A content error is likely to be reported by a
     * document processor (e.g. XSL, XQL) that relies on the content to
     * convey meaningful information.
     */
    public static final int CONTENT = 3;

    
    /**
     * Generic document processing error. This error level indicates an
     * error that occurs while processing the document, but is not
     * related directly to the document content. For example, an XSL
     * processor may report internal errors in this way, while reporting
     * XSL template errors using the content error level.
     */
    public static final int PROCESSING = 4;

    
    /**
     * Generic error. This error level has no special meaning except that
     * errors reported in this level are not considered fatal, and thus,
     * might be corrected and ignored.
     * <P>
     * This is the default error level, if none is specified (e.g. when
     * using this report as a SAX error handler).
     */
    public static final int GENERAL = 5;

    
    /**
     * Fatal error after which processing cannot continue. This error
     * level is the most serve, in that, after a fatal error has occured,
     * processing must stop. Fatal errors are reported and are always
     * thrown. A fatal error can be an IO exception opening a document
     * file, or it can be a runtime exception (e.g. null pointer), or it
     * can be any other exception from which the processor cannot recover
     * and continue processing.
     */
    public static final int FATAL = 6;
    
    
    /**
     * Report all errors and warnings. Using this report level, every
     * error and warning generated will be reported and can later be
     * retrieved from this report.
     */
    public static final int REPORT_WITH_WARNING = WARNING;
    
    
    /**
     * Report all errors but no warnings. Using this report level, every
     * error generated will be reported and can later be retrieved from
     * this report. Warnings are ignored.
     */
    public static final int REPORT_ALL_ERRORS = WELL_FORMED;

    
    /**
     * Ignore all errors, nothing is reported. This report level is
     * generally used in two cases: a) when the application is not
     * interested in reading any errors and assumes all operations will
     * complete successfuly, b) when the application uses {@link
     * #STOP_AT_FIRST_ERROR} but is not interested in warnings. We
     * <b>do not</b> recommend a) as an approach to application design. 
     */
    public static final int REPORT_NOTHING = FATAL;

    
    /**
     * Stop at first well formed error. Will stop when the first well
     * formed error is encountered, but will also stop at all other
     * error levels.
     * <P>
     * This level is used when the document must be correct, or else
     * there is no point in handling it.
     */
    public static final int STOP_AT_WELL_FORMED = WELL_FORMED;


    /**
     * Stop at first validity error. Will stop when the first validity
     * error is encountered, will not stop for well formed errors, but
     * will stop for content and processing errors.
     * <P>
     * This level is used when the document must be valid, or else
     * there is no point in handling it.
     */
    public static final int STOP_AT_VALIDITY = VALIDITY;

    
    /**
     * Stop at first content error. Will stop when the first content
     * error is encountered, will not stop for well formed or validity
     * errors, but will stop for processing errors.
     */
    public static final int STOP_AT_CONTENT = CONTENT;


    /**
     * Stop at first processing error. Will stop when the first processing
     * error is encountered, will not stop for well formed, validity or
     * content errors.
     */
    public static final int STOP_AT_PROCESSING = PROCESSING;
    
    
    /**
     * Stop at fatal error only. The first fatal error will always stop
     * the processing. Using this stop level simply reports but do not
     * stop at lower error levels.
     * <P>
     * This level is used when the application assumes that all errors
     * can be properly corrected, or when the application reports all
     * errors not just the last one.
     */
    public static final int STOP_AT_FATAL = FATAL;


    /**
     * Stop at error encountered. This level is used when the application
     * wishes to assure that there are absolutely no errors, but does not
     * both to recover from them or check the error report.
     * <P>
     * This level is equivalent to {@link #STOP_AT_WELL_FORMED}.
     */
    public static final int STOP_AT_FIRST_ERROR = STOP_AT_WELL_FORMED;

    
    /**
     * Stop at no error (except fatal). This level is used when the
     * application assumes that all errors are recoverable, or prefers
     * to get a full report at the end of processing. Fatal errors
     * will always stop the processing.
     * <P>
     * This level is equivalent to {@link #STOP_AT_FATAL}.
     */
    public static final int STOP_AT_NO_ERROR = STOP_AT_FATAL;

    
}
