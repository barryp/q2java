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
 * CDATA sections are used to escape blocks of text containing characters that
 * would otherwise be regarded as markup. It is fully implemented by the {@link
 * org.w3c.dom.Text} node type.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#COMMENT_NODE}
 * <LI>Node does not support childern
 * <LI>Node name is always "#comment"
 * </OL>
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.CDATASection
 * @see TextImpl
 */
final class CDATASectionImpl
    extends TextImpl
    implements CDATASection
{
    
    
    public short getNodeType()
    {
        return this.CDATA_SECTION_NODE;
    }
    
    
    public final Object clone()
    {
        CDATASectionImpl    clone;
        
        clone = new CDATASectionImpl(_ownerDocument, getNodeValue() );
        cloneInto( clone, true );
        return clone;
    }

    
    public final Node cloneNode( boolean deep )
    {
        CDATASectionImpl    clone;
            
        clone = new CDATASectionImpl( _ownerDocument, getNodeValue() );
        cloneInto( clone, deep );
        return clone;
    }

    
    public String toString()
    {
        String    value;
        
        value = getData();
        if ( value.length() > 64 )
            value = value.substring( 0, 64 ) + "..";
        value = value.replace( '\n', '|' );
        return "CDATA node: [" + value + "]";
    }

    
    /**
     * Hidden constructor.
     * 
     * @param owner The owner of this document
     * @param value Initial value or empty string
     */
    CDATASectionImpl( DocumentImpl owner, String value )
    {
        super( owner, "#cdata-section", value );
    }
    
    
}
