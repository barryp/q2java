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


import org.xml.sax.Locator;


/**
 * Immutable locator. A locator identifies a location (usually of an
 * error) in the source document, including the document's system and
 * public identifier, the line number and the column inside that list.
 * Both line and column number are 1-based.
 * <P>
 * This object is immutable, it cannot be changed. It is safe to pass
 * the locator to other methods and share it as a constant locator.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:57:07 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.xml.sax.Locator
 */
class LocatorImpl
    implements Locator
{


    /**
     * Constructs a new locator with the given information. Once the
     * locator has been fixed, the location held in it cannot be changed.
     * 
     * @param systemId The document system identifier, if known
     * @param publicId The document public identifier, if known
     * @param lineNumber The document line number (1-based, -1 if unkown)
     * @param columnNumber The column number in the line (1-based,
     *  -1 if unkown)
     */
    public LocatorImpl( String systemId, String publicId, int lineNumber, int columnNumber )
    {
        _systemId = systemId;
        _publicId = publicId;
        _lineNumber = lineNumber;
        _columnNumber = columnNumber;
    }


    /**
     * Copy constructor creates a new locator from an existing one.
     * Changes to the original locator are not reflected in the
     * copied locator.
     * 
     * @param locator The locator to duplicate
     */
    public LocatorImpl( Locator locator )
    {
        _systemId = locator.getSystemId();
        _publicId = locator.getPublicId();
        _lineNumber = locator.getLineNumber();
        _columnNumber = locator.getColumnNumber();
    }


    /**
     * Returns the source document public identifier.
     * Returns null if the public identifier is unknown.
     * 
     * @return Public identifier
     */
    public String getPublicId()
    {
        return _publicId;
    }


    /**
     * Returns the source document system identifier.
     * Returns null if the system identifier is unknown.
     * 
     * @return System identifier
     */
    public String getSystemId()
    {
       return _systemId;
    }


    /**
     * Returns location's line number in the document (1-based).
     * Returns -1 if the line number is unknown.
     * 
     * @return Line number
     */
    public int getLineNumber()
    {
        return _lineNumber;
    }


    /**
     * Returns location's column number in the line (1-based).
     * Returns -1 if the column number is unknown.
     * 
     * @return Column number
     */
    public int getColumnNumber()
    {
        return _columnNumber;
    }

    
    /**
     * The source document system identifier (if known).
     */
    private String      _systemId;
    
    
    /**
     * The source document public identifier (if known).
     */
    private String      _publicId;
    
    
    /**
     * The line number location (1-based).
     */
    private int         _lineNumber;
    
    
    /**
     * The column number in the line location (1-based).
     */
    private int         _columnNumber;
    

}

