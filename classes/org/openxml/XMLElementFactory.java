package org.openxml;

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


import org.w3c.dom.*;


/**
 * Defines an element factory for constructing new elements. An application
 * document may elect to use this factory to create user elements derived
 * from the class {@link XMLElement}. This is an alternative to the simple
 * tag name to class mapping that is supported by {@link XMLDocument}.
 * <P>
 * The {@link #createElement} will be called to create any element and may
 * behave in one of three manners:
 * <UL>
 * <LI>Create and return a new element from a class that extends {@link
 *  XMLElement}
 * <LI>Return null and an element will be created from {@link XMLElement}
 * <LI>Throw an exception to indicate that elements of this type are not
 *  supported in this document (this behavior is highly discouraged)
 * </UL>
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see XMLDocument#useElementFactory
 * @see XMLElement
 * @deprecated Alternative API will be introduced in OpenXML 1.1
 */
public interface XMLElementFactory
{


	/**
	 * Called to create an element with the specified tag name. Returned element
	 * is of class derived from {@link XMLElement}. If null is returned, an
	 * element of type {@link XMLElement} will be created.
	 * <P>
	 * When creating a new element, the parameters <TT>owner</TT> and
	 * <TT>tagName</TT> must be passed as is to the {@link XMLElement}
	 * constructor.
	 *
	 * @param owner The owner document
	 * @param tagName The element tag name
	 * @return New element or null
	 */
	public XMLElement createElement( XMLDocument owner, String tagName );
}