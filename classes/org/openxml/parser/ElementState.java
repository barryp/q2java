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


class ElementState
{
    
    
    ElementState( String tagName, int startLine, boolean elementContent )
    {
        _tagName = tagName;
        _elementContent = elementContent;
        _startLine = startLine;
    }


    void assign( String tagName, int startLine, boolean elementContent )
    {
        _tagName = tagName;
        _elementContent = elementContent;
        _startLine = startLine;
    }
    
    
    public String getTagName()
    {
        return _tagName;
    }
    
    
    // HTML
    public boolean isFirstInContent()
    {
        return _firstInContent;
    }
    
    
    // HTML
    public void notFirstInContent()
    {
        _firstInContent = false;
    }
    
    
    public boolean isElementContent()
    {
        return _elementContent;
    }
    

    public boolean isPreserveSpaces()
    {
        return _preserveSpaces;
    }
   

    public ElementState getPrevious()
    {
        return _previousState;
    }
    
    
    public int getStartLine()
    {
        return _startLine;
    }

    
    private String          _tagName;
    
    
    private boolean         _elementContent;
    
    
    private boolean         _preserveSpaces;
    
    
    private ElementState    _previousState;
    
    
    private int             _startLine;
    
    
    private boolean         _firstInContent;
    
    
}
