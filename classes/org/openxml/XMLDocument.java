package org.openxml;

/**
 * org/openxml/XMLDocument.java
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
import org.openxml.dom.*;


/**
 * Base class for user XML document. In addition to several API extensions, user
 * XML documents can be used to map XML documents directly into application data
 * structures, with the aid of user elements ({@link XMLElement}).
 * <P>
 * {@link XMLDocument} extends the DOM {@link org.w3c.dom.Document} with the
 * following methods:
 * <UL>
 * <LI>{@link #makeReadOnly} renders a full document read-only preventing any
 *  changes to it's documet contents
 * <LI>{@link #registerElement} associates tag names with user element classes
 *  that derive from {@link XMLElement}
 * <LI>{@link #useElementFactory} invokes an external element factory to create
 *  application elements
 * </UL>
 * <P>
 * Documents of specific type can be created safely by passing the document class
 * to {@link DOMFactory#createDocument}. They are supported by the built in XML
 * parser, printer and processor. User documents are also supported by {@link
 * org.openxml.source.Source} through use of the <TT>docClass</TT> property.
 * <P>
 * A user document derived from {@link XMLDocument} must be declared public and
 * the constructor must be public. User elements should be registered in the
 * constructor, and read-only status should be obeyed by calling {@link
 * XMLDocument#isReadOnly}.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Document
 * @see XMLElement
 * @see XMLCollection
 * @see XMLElementFactory
 * @deprecated Alternative API will be introduced in OpenXML 1.1
 */
public class XMLDocument
	extends DocumentImpl
	implements Document
{



	/**
	 * Default constructor.
	 */
	public XMLDocument()
	{
		super();
//        this.registerElement( "MyTag", MyElement.class );
	}
	public Object clone()
	{
		XMLDocument clone;
		
		clone = new XMLDocument();
		cloneInto( clone, true );
		return clone;
	}
	public Node cloneNode( boolean deep )
	{
		XMLDocument clone;
			
		clone = new XMLDocument();
		cloneInto( clone, deep );
		return clone;
	}
}