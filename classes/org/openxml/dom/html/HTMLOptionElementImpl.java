package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLOptionElementImpl.java
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
 * @see org.w3c.dom.html.HTMLOptionElement
 * @see ElementImpl
 */
public final class HTMLOptionElementImpl
	extends HTMLElementImpl
	implements HTMLOptionElement
{

	

	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLOptionElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "OPTION" );
	}
	public boolean getDefaultSelected()
	{
		// ! NOT FULLY IMPLEMENTED !
		return ( getAttribute( "default-selected" ) != null );
	}
	public boolean getDisabled()
	{
		return getAttribute( "disabled" ) != null;
	}
	public int getIndex()
	{
		Node        parent;
		NodeList    options;
		int            i;
		
		// Locate the parent SELECT. Note that this OPTION might be inside a
		// OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
		// Everything is possible. If no parent is found, return -1.
		parent = getParentNode();
		while ( parent != null && ! ( parent instanceof HTMLSelectElement ) )
			parent = parent.getParentNode();
		if ( parent != null )
		{
			// Use getElementsByTagName() which creates a snapshot of all the
			// OPTION elements under the SELECT. Access to the returned NodeList
			// is very fast and the snapshot solves many synchronization problems.
			options = ( (HTMLElement) parent ).getElementsByTagName( "OPTION" );
			for ( i = 0 ; i < options.getLength() ; ++i )
				if ( options.item( i ) == this )
					return i;
		}
		return -1;
	}
	  public String getLabel()
	{
		return capitalize( getAttribute( "label" ) );
	}
	  public boolean getSelected()
	{
		return getAttribute( "selected" ) != null;
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
	public String getValue()
	{
		return getAttribute( "value" );
	}
	public void setDefaultSelected( boolean defaultSelected )
	{
		// ! NOT FULLY IMPLEMENTED !
		setAttribute( "default-selected", defaultSelected ? "" : null );
	}
	public void setDisabled( boolean disabled )
	{
		setAttribute( "disabled", disabled ? "" : null );
	}
	public void setIndex( int index )
	{
		Node        parent;
		NodeList    options;
		Node        item;
		
		// Locate the parent SELECT. Note that this OPTION might be inside a
		// OPTGROUP inside the SELECT. Or it might not have a parent SELECT.
		// Everything is possible. If no parent is found, just return.
		parent = getParentNode();
		while ( parent != null && ! ( parent instanceof HTMLSelectElement ) )
			parent = parent.getParentNode();
		if ( parent != null )
		{
			// Use getElementsByTagName() which creates a snapshot of all the
			// OPTION elements under the SELECT. Access to the returned NodeList
			// is very fast and the snapshot solves many synchronization problems.
			// Make sure this OPTION is not replacing itself.
			options = ( (HTMLElement) parent ).getElementsByTagName( "OPTION" );
			if ( options.item( index ) != this )
			{
				// Remove this OPTION from its parent. Place this OPTION right
				// before indexed OPTION underneath it's direct parent (might
				// be an OPTGROUP).
				getParentNode().removeChild( this );
				item = options.item( index );
				item.getParentNode().insertBefore( this, item );
			}
		}
	}
	public void setLabel( String label )
	{
		setAttribute( "label", label );
	}
	  public void setSelected( boolean selected )
	{
		setAttribute( "selected", selected ? "" : null );
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
	public void setValue( String value )
	{
		setAttribute( "value", value );
	}
}