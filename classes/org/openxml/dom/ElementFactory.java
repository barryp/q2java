/**
 * org/openxml/XMLElementFactory.java
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
 * Dec 23, 1999
 * + Moved from org.openxml.XMLElementFactory here to decouple DOM
 *   implementation from OpenXML.
 */


package org.openxml.dom;


import org.w3c.dom.*;


/**
 * Defines an element factory for constructing new elements. An application
 * document may elect to use this factory to create user elements derived
 * from the class {@link ElementImpl}.
 * <P>
 * The {@link #createElement} will be called to create any element and may
 * behave in one of three manners:
 * <UL>
 * <LI>Create and return a new element from a class that extends {@link
 *  ElementImpl}
 * <LI>Return null and a standard element will be created
 * <LI>Throw an exception to indicate that elements of this type are not
 *  supported in this document (this behavior is highly discouraged)
 * </UL>
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see ElementImpl
 * @deprecated Alternative API will be introduced in OpenXML 1.1
 */
public interface ElementFactory
{


    /**
     * Called to create an element with the specified tag name. Returned
     * element is of class derived from {@link Element}. If null is
     * returned, an element of type {@link Element} will be created.
     * <P>
     * When creating a new element, the parameters <TT>owner</TT> and
     * <TT>tagName</TT> must be passed as is to the {@link Element}
     * constructor.
     *
     * @param owner The owner document
     * @param namespaceURI The namespace URI if specified, or null
     * @param qualifiedName The qualified element tag name
     * @return New element or null
     */
    public Element createElement( Document owner, String namespaceURI, String qualifiedName );


}
