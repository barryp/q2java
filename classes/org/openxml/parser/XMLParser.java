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


import java.io.IOException;
import java.util.Locale;

import org.xml.sax.*;
import org.w3c.dom.Document;

import org.openxml.dom.SAXBuilder;


public class XMLParser
    implements Parser
{
    
    
    public XMLParser()
    {
	_parser = new XMLSAXParser();
	_builder = new SAXBuilder();
	_errorReport = new ErrorReportImpl( ErrorReport.STOP_AT_WELL_FORMED,
					    ErrorReport.REPORT_ALL_ERRORS );
	_parser.setErrorHandler( _errorReport );
	_parser.setDocumentHandler( _builder );
    }


    public void setErrorHandler( ErrorHandler handler )
    {
	_parser.setErrorHandler( handler );
	if ( handler instanceof ErrorReport )
	    _errorReport = (ErrorReport) handler;
	else
	    _errorReport = null;
    }


    public void setDocumentHandler( DocumentHandler handler )
    {
	_parser.setDocumentHandler( handler );
    }


    public void setDTDHandler( DTDHandler handler )
    {
	_parser.setDTDHandler( handler );
    }


    public void setLocale( Locale locale )
       throws SAXException
    {
	_parser.setLocale( locale );
    }


    public void setEntityResolver( EntityResolver resolver )
    {
	_parser.setEntityResolver( resolver );
    }


    public synchronized void parse( String input )
       throws SAXException, IOException
    {
	_parser.parse( input );
    }


    public synchronized void parse( InputSource input )
       throws SAXException, IOException
    {
	_parser.parse( input );
    }


    public Document getDocument()
    {
	return _builder.getDocument();
    }


    public ErrorReport getErrorReport()
    {
	return _errorReport;
    }


    private SAXBuilder _builder;


    private Parser    _parser;


    private ErrorReport _errorReport;


}
