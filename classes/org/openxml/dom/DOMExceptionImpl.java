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


package org.openxml.dom;


import org.w3c.dom.*;


/**
 * Implements {@link org.w3c.dom.DOMException} for throwing a {@link
 * java.lang.RuntimeException}. Message specified as one of several predefined
 * error codes plus an optional description string. {@link #getMessage} returns
 * textual description of error code.
 * <P>
 * See {@link org.w3c.dom.DOMException} for list of supported error codes.
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.DOMException
 */
public final class DOMExceptionImpl
    extends DOMException
{
    
    
    /**
     * Create new {@link org.w3c.dom.DOMException} with specified code.
     * Message will contain textual description of code.
     * 
     * @param code {@link org.w3c.dom.DOMException} error code
     */
    public DOMExceptionImpl( short code )
    {
        this( code, null );
    }


    /**
     * Create new {@link org.w3c.dom.DOMException} with specified code and
     * message. Message will contain textual description of code and optional
     * message text.
     * 
     * @param code {@link org.w3c.dom.DOMException} error code
     * @param message Optional message text
     */
    public DOMExceptionImpl( short code, String message )
    {
        super( code, makeMessage( code, message ) );
    }
    

    public String toString()
    {
        return "DOMException(" + super.code + "): " + getMessage();
    }
    

    /**
     * Construct message based on textual description of message code and
     * message text. Static method is used by constructor.
     * 
     * @param code {@link org.w3c.dom.DOMException} error code
     * @param message Optional message text
     * @return Description of error code containing optional message text
     */
    private static String makeMessage( short code, String message )
    {
        String    codeText;
        
        switch ( code )
        {
        case INDEX_SIZE_ERR:
            codeText = "Index or size is negative or greater than the allowed value.";
            break;
	case DOMSTRING_SIZE_ERR:
	    codeText = "The specified range of text does not fit into a DOMString.";
	    break;
        case HIERARCHY_REQUEST_ERR:
            codeText = "Node is inserted somewhere it doesn't belong.";
            break;
        case WRONG_DOCUMENT_ERR:
            codeText = "Node is used in a different document than the one that created it (that doesn't support it).";
            break;
        case INVALID_CHARACTER_ERR:
            codeText = "An invalid character is specified, such as in a name.";
            break;
        case NO_DATA_ALLOWED_ERR:
            codeText = "Node does not support data.";
            break;
        case NO_MODIFICATION_ALLOWED_ERR:
            codeText = "Attempt made to modify an pbject where modification not allowed.";
            break;
        case NOT_FOUND_ERR:
            codeText = "Attempt to reference a node in a context where it does not exist.";
            break;
        case NOT_SUPPORTED_ERR:
            codeText = "Implementation does not support the type of object requested.";
            break;
        case INUSE_ATTRIBUTE_ERR:
            codeText = "An attempt to add an attribute that is already inuse elsewhere.";
            break;
        default:
            codeText = "Exception reason is unspecified.";
            break;
        }
        codeText = "DOMException: " +  codeText + " (code " + code + ")";
        if ( message != null && message.length() > 0 )
            codeText = codeText + "\n" + message;
        return codeText;
    }
    
    
}




















