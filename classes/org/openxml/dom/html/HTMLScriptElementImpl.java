package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLScriptElementImpl.java
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


import org.openxml.dom.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLScriptElement
 * @see ElementImpl
 */
public final class HTMLScriptElementImpl
	extends HTMLElementImpl
	implements HTMLScriptElement
{
	
	
	  /**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLScriptElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "SCRIPT" );
	}
	   public String getCharset()
	{
		return getAttribute( "charset" );
	}
	public boolean getDefer()
	{
		return getAttribute( "defer" ) != null;
	}
	   public String getEvent()
	{
		return getAttribute( "event" );
	}
	   public String getHtmlFor()
	{
		return getAttribute( "for" );
	}
	   public String getSrc()
	{
		return getAttribute( "src" );
	}
	public String getText()
	{
		Node    child;
		String    text;
		
		// Find the Text nodes contained within this element and return their
		// concatenated value. Required to go around comments, entities, etc.
		child = getFirstChild();
		text = "";
		while ( child != null )
		{
			if ( child instanceof Text )
				text = text + ( (Text) child ).getData();
			child = child.getNextSibling();
		}
		return text;
	}
	public String getType()
	{
		return getAttribute( "type" );
	}
	public void setCharset( String charset )
	{
		setAttribute( "charset", charset );
	}
	public void setDefer( boolean defer )
	{
		setAttribute( "defer", defer ? "" : null );
	}
	public void setEvent( String event )
	{
		setAttribute( "event", event );
	}
	public void setHtmlFor( String htmlFor )
	{
		setAttribute( "for", htmlFor );
	}
	public void setSrc( String src )
	{
		setAttribute( "src", src );
	}
	public void setText( String text )
	{
		Node    child;
		Node    next;
		
		// Delete all the nodes and replace them with a single Text node.
		// This is the only approach that can handle comments and other nodes.
		child = getFirstChild();
		while ( child != null )
		{
			next = child.getNextSibling();
			removeChild( child );
			child = next;
		}
		insertBefore( getOwnerDocument().createTextNode( text ), getFirstChild() );
	}
	public void setType( String type )
	{
		setAttribute( "type", type );
	}
}