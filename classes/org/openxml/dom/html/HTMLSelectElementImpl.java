package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLSelectElementImpl.java
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
 * @see org.w3c.dom.html.HTMLSelectElement
 * @see ElementImpl
 */
public final class HTMLSelectElementImpl
	extends HTMLElementImpl
	implements HTMLSelectElement, HTMLFormControl
{
	
	
	private HTMLCollection    _options;
  
  
	/**
	 * Constructor requires owner document.
	 * 
	 * @param owner The owner HTML document
	 */
	public HTMLSelectElementImpl( HTMLDocumentImpl owner, String name )
	{
		super( owner, "SELECT" );
	}
	public void add( HTMLElement element, HTMLElement before )
	{
		insertBefore( element, before );
	}
	public void               blur()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public void               focus()
	{
		// No scripting in server-side DOM. This method is moot.
	}
	public boolean getDisabled()
	{
		return getAttribute( "disabled" ) != null;
	}
	public int getLength()
	{
		return getOptions().getLength();
	}
	  public boolean getMultiple()
	{
		return getAttribute( "multiple" ) != null;
	}
	  public String getName()
	{
		return getAttribute( "name" );
	}
	public HTMLCollection getOptions()
	{
		if ( _options == null )
			_options = new HTMLCollectionImpl( this, HTMLCollectionImpl.OPTION );
		return _options;
	}
	public int getSelectedIndex()
	{
		NodeList    options;
		int            i;
		
		// Use getElementsByTagName() which creates a snapshot of all the
		// OPTION elements under this SELECT. Access to the returned NodeList
		// is very fast and the snapshot solves many synchronization problems.
		// Locate the first selected OPTION and return its index. Note that
		// the OPTION might be under an OPTGROUP.
		options = getElementsByTagName( "OPTION" );
		for ( i = 0 ; i < options.getLength() ; ++i )
			if ( ( (HTMLOptionElement) options.item( i ) ).getSelected() )
				return i;
		return -1;
	}
	public int getSize()
	{
		return toInteger( getAttribute( "size" ) );
	}
	public int getTabIndex()
	{
		return toInteger( getAttribute( "tabindex" ) );
	}
	public String getType()
	{
		return getAttribute( "type" );
	}
	  public String getValue()
	{
		return getAttribute( "value" );
	}
	public void remove( int index )
	{
		NodeList    options;
		Node        removed;
		
		// Use getElementsByTagName() which creates a snapshot of all the
		// OPTION elements under this SELECT. Access to the returned NodeList
		// is very fast and the snapshot solves many synchronization problems.
		// Remove the indexed OPTION from it's parent, this might be this
		// SELECT or an OPTGROUP.
		options = getElementsByTagName( "OPTION" );
		removed = options.item( index );
		if ( removed != null )
			removed.getParentNode().removeChild ( removed );
	}
	public void setDisabled( boolean disabled )
	{
		setAttribute( "disabled", disabled ? "" : null );
	}
	public void setMultiple( boolean multiple )
	{
		setAttribute( "multiple", multiple ? "" : null );
	}
	public void setName( String name )
	{
		setAttribute( "name", name );
	}
	public void setSelectedIndex( int selectedIndex )
	{
		NodeList    options;
		int            i;
		
		// Use getElementsByTagName() which creates a snapshot of all the
		// OPTION elements under this SELECT. Access to the returned NodeList
		// is very fast and the snapshot solves many synchronization problems.
		// Change the select so all OPTIONs are off, except for the
		// selectIndex-th one.
		options = getElementsByTagName( "OPTION" );
		for ( i = 0 ; i < options.getLength() ; ++i )
			( (HTMLOptionElementImpl) options.item( i ) ).setSelected( i == selectedIndex );
	}
	public void setSize( int size )
	{
		setAttribute( "size", String.valueOf( size ) );
	}
	public void setTabIndex( int tabIndex )
	{
		setAttribute( "tabindex", String.valueOf( tabIndex ) );
	}
	public void setValue( String value )
	{
		setAttribute( "value", value );
	}
}