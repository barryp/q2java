package org.openxml.dom.html;

/**
 * org/openxml/dom/html/HTMLDocumentImpl.java
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


import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.w3c.dom.html.*;
import org.openxml.*;
import org.openxml.dom.*;


/**
 * Implements an HTML document. Provides access to the top level element in the
 * document, its body and title.
 * <P>
 * Several methods create new nodes of all basic types (comment, text, element,
 * etc.). These methods create new nodes but do not place them in the document
 * tree. The nodes may be placed in the document tree using {@link
 * org.w3c.dom.Node#appendChild} or {@link org.w3c.dom.Node#insertBefore}, or
 * they may be placed in some other document tree.
 * <P>
 * Note: &lt;FRAMESET&gt; documents are not supported at the moment, neither
 * are direct document writing ({@link #open}, {@link #write}) and HTTP attribute
 * methods ({@link #getURL}, {@link #getCookie}).
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.html.HTMLDocument
 * @see org.openxml.XMLDocument
 */
public final class HTMLDocumentImpl
	extends DocumentImpl
	implements HTMLDocument
{


	/**
	 * Holds {@link HTMLCollectionImpl} object with live collection of all
	 * anchors in document. This reference is on demand only once.
	 */
	private HTMLCollectionImpl    _anchors;


	/**
	 * Holds {@link HTMLCollectionImpl} object with live collection of all
	 * forms in document. This reference is on demand only once.
	 */
	private HTMLCollectionImpl    _forms;


	/**
	 * Holds {@link HTMLCollectionImpl} object with live collection of all
	 * images in document. This reference is on demand only once.
	 */
	private HTMLCollectionImpl    _images;


	/**
	 * Holds {@link HTMLCollectionImpl} object with live collection of all
	 * links in document. This reference is on demand only once.
	 */
	private HTMLCollectionImpl    _links;


	/**
	 * Holds {@link HTMLCollectionImpl} object with live collection of all
	 * applets in document. This reference is on demand only once.
	 */
	private HTMLCollectionImpl    _applets;


	/**
	 * Holds string writer used by direct manipulation operation ({@link #open}.
	 * {@link #write}, etc) to write new contents into the document and parse
	 * that text into a document tree.
	 */
	private StringWriter        _writer;


	/**
	 * Holds names and classes of HTML element types. When an element with a
	 * particular tag name is created, the matching {@link java.lang.Class}
	 * is used to create the element object. For example, &lt;A&gt; matches
	 * {@link HTMLAnchorElementImpl}. This static table is shared across all
	 * HTML documents, as opposed to the non-static table defined in {@link
	 * org.openxml.dom.DocumentImpl}.
	 *
	 * @see #createElement
	 */
	private static Hashtable        _elementTypesHTML;


	/**
	 * Signature used to locate constructor of HTML element classes. This
	 * static array is shared across all HTML documents.
	 *
	 * @see #createElement
	 */
	private static final Class[]    _elemClassSigHTML =
				new Class[] { HTMLDocumentImpl.class, String.class };


	/**
	 */
	public HTMLDocumentImpl()
	{
		super();
		populateElementTypes();
	}
	protected NodeImpl castNewChild( Node newChild )
		throws DOMException
	{
		// Same method appears in HTMLElementImpl and HTMLDocumentImpl.

		if ( newChild == null )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child reference is null." );
		if ( ! ( newChild instanceof NodeImpl ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child is not a compatible type for this node." );

		// newChild must be HTMLElement, Text, Comment, DocumentFragment or
		// ProcessingInstruction. CDATASection and EntityReference not supported
		// in HTML documents.
		if ( ! ( newChild instanceof HTMLElementImpl ||
				 newChild instanceof Comment ||
				 newChild instanceof Text ||
				 newChild instanceof DocumentFragment ||
				 newChild instanceof ProcessingInstruction ) )
			throw new DOMExceptionImpl( DOMException.HIERARCHY_REQUEST_ERR,
				"Child is not a compatible type for this node." );
		return (NodeImpl) newChild;
	}
	public Object clone()
	{
		HTMLDocumentImpl    clone;

		clone = new HTMLDocumentImpl();
		cloneInto( clone, true );
		return clone;
	}
	public Node cloneNode( boolean deep )
	{
		HTMLDocumentImpl    clone;

		clone = new HTMLDocumentImpl();
		cloneInto( clone, deep );
		return clone;
	}
	public void close()
	{
		// ! NOT IMPLEMENTED, REQUIRES PARSER !
		if ( _writer != null )
		{
			_writer = null;
		}
	}
	public Element createElement( String tagName )
		throws DOMException
	{
		Class        elemClass;
		Constructor    cnst;

		// First, make sure tag name is all upper case, next get the associated
		// element class. If no class is found, generate a generic HTML element.
		// Do so also if an unexpected exception occurs.
		tagName = tagName.toUpperCase();
		elemClass = (Class) _elementTypesHTML.get( tagName );
		if ( elemClass != null )
		{
			// Get the constructor for the element. The signature specifies an
			// owner document and a tag name. Use the constructor to instantiate
			// a new object and return it.
			try
			{
				cnst = elemClass.getConstructor( _elemClassSigHTML );
				return (Element) cnst.newInstance( new Object[] { this, tagName } );
			}
			catch ( Exception except )
			{
				Throwable thrw;

				if ( except instanceof java.lang.reflect.InvocationTargetException )
					thrw = ( (java.lang.reflect.InvocationTargetException) except ).getTargetException();
				else
					thrw = except;
				System.out.println( "Exception " + thrw.getClass().getName() );
				System.out.println( thrw.getMessage() );

				throw new IllegalStateException( "Tag '" + tagName + "' associated with an Element class that failed to construct." );
			}
		}
		return new HTMLElementImpl( this, tagName );
	}
	public HTMLCollection getAnchors()
	{
		// For more information see HTMLCollection#collectionMatch
		if ( _anchors == null )
			_anchors = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.ANCHOR );
		return _anchors;
	}
	public HTMLCollection getApplets()
	{
		// For more information see HTMLCollection#collectionMatch
		if ( _applets == null )
			_applets = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.APPLET );
		return _applets;
	}
	public synchronized HTMLElement getBody()
	{
		Node    html;
		Node    head;
		Node    body;
		Node    child;
		Node    next;

		// Call getDocumentElement() to get the HTML element that is also the
		// top-level element in the document. Get the first element in the
		// document that is called BODY. Work with that.
		html = getDocumentElement();
		head = getHead();
		synchronized ( html )
		{
			body = head.getNextSibling();
			while ( body != null && ! ( body instanceof HTMLBodyElement ) )
				body = body.getNextSibling();
			// If BODY was not found, try looking for FRAMESET instead.
			if ( body == null )
			{
				body = head.getNextSibling();
				while ( body != null && ! ( body instanceof HTMLFrameSetElement ) )
					body = body.getNextSibling();
			}

			// BODY/FRAMESET exists but might not be second element in HTML
			// (after HEAD): make sure it is and return it.
			if ( body != null )
			{
				synchronized ( body )
				{
					child = head.getNextSibling();
					while ( child != null && child != body )
					{
						next = child.getNextSibling();
						body.insertBefore( child, body.getFirstChild() );
						child = next;
					}
				}
				return (HTMLElement) body;
			}

			// BODY does not exist, create a new one, place it in the HTML element
			// right after the HEAD and return it.
			body = new HTMLBodyElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "BODY" );
			html.appendChild( body );
		}
		return (HTMLElement) body;
	}
	public String getCookie()
	{
		// Information not available on server side.
		return null;
	}
	public synchronized Element getDocumentElement()
	{
		Node    html;
		Node    child;
		Node    next;

		// The document element is the top-level HTML element of the HTML
		// document. Only this element should exist at the top level.
		// If the HTML element is found, all other elements that might
		// precede it are placed inside the HTML element.
		html = getFirstChild();
		while ( html != null )
		{
			if ( html instanceof HTMLHtmlElement )
			{
				synchronized ( html )
				{
					child = getFirstChild();
					while ( child != null && child != html )
					{
						next = child.getNextSibling();
						html.appendChild( child );
						child = next;
					}
				}
				return (HTMLElement) html;
			}
			html = html.getNextSibling();
		}

		// HTML element must exist. Create a new element and dump the
		// entire contents of the document into it in the same order as
		// they appear now.
		html = new HTMLHtmlElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "HTML" );
		child = getFirstChild();
		while ( child != null )
		{
			next = child.getNextSibling();
			html.appendChild( child );
			child = next;
		}
		appendChild( html );
		return (HTMLElement) html;
	}
	public String getDomain()
	{
		// Information not available on server side.
		return null;
	}
	public Element getElementById( String elementId )
	{
		return getElementById( elementId, this );
	}
	/**
	 * Recursive method retreives an element by its <code>id</code> attribute.
	 * Called by {@link #getElementById(String)}.
	 *
	 * @param elementId The <code>id</code> value to look for
	 * @return The node in which to look for
	 */
	private Element getElementById( String elementId, Node node )
	{
		Node    child;
		Element    result;

		child = node.getFirstChild();
		while ( child != null )
		{
			if ( child instanceof Element )
			{
				if ( elementId.equals( ( (Element) child ).getAttribute( "id" ) ) )
					return (Element) child;
				result = getElementById( elementId, child );
				if ( result != null )
					return result;
			}
			child = child.getNextSibling();
		}
		return null;
	}
	public NodeList getElementsByName( String elementName )
	{
		return new HTMLElementListImpl( this, "name" );
	}
	public HTMLCollection getForms()
	{
		// For more information see HTMLCollection#collectionMatch
		if ( _forms == null )
			_forms = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.FORM );
		return _forms;
	}
	/**
	 * Obtains the &lt;HEAD&gt; element in the document, creating one if does
	 * not exist before. The &lt;HEAD&gt; element is the first element in the
	 * &lt;HTML&gt; in the document. The &lt;HTML&gt; element is obtained by
	 * calling {@link #getDocumentElement}. If the element does not exist, one
	 * is created.
	 * <P>
	 * Called by {@link #getTitle}, {@link #setTitle}, {@link #getBody} and
	 * {@link #setBody} to assure the document has the &lt;HEAD&gt; element
	 * correctly placed.
	 *
	 * @return The &lt;HEAD&gt; element
	 */
	public synchronized HTMLElement getHead()
	{
		Node    head;
		Node    html;
		Node    child;
		Node    next;

		// Call getDocumentElement() to get the HTML element that is also the
		// top-level element in the document. Get the first element in the
		// document that is called HEAD. Work with that.
		html = getDocumentElement();
		synchronized ( html )
		{
			head = html.getFirstChild();
			while ( head != null && ! ( head instanceof HTMLHeadElement ) )
				head = head.getNextSibling();
			// HEAD exists but might not be first element in HTML: make sure
			// it is and return it.
			if ( head != null )
			{
				synchronized ( head )
				{
					child = html.getFirstChild();
					while ( child != null && child != head )
					{
						next = child.getNextSibling();
						head.insertBefore( child, head.getFirstChild() );
						child = next;
					}
				}
				return (HTMLElement) head;
			}

			// Head does not exist, create a new one, place it at the top of the
			// HTML element and return it.
			head = new HTMLHeadElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "HEAD" );
			html.insertBefore( head, html.getFirstChild() );
		}
		return (HTMLElement) head;
	}
	public HTMLCollection getImages()
	{
		// For more information see HTMLCollection#collectionMatch
		if ( _images == null )
			_images = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.IMAGE );
		return _images;
	}
	public HTMLCollection getLinks()
	{
		// For more information see HTMLCollection#collectionMatch
		if ( _links == null )
			_links = new HTMLCollectionImpl( getBody(), HTMLCollectionImpl.LINK );
		return _links;
	}
	public String getReferrer()
	{
		// Information not available on server side.
		return null;
	}
	public synchronized String getTitle()
	{
		HTMLElement    head;
		Node        title;

		// Get the HEAD element and look for the TITLE element within.
		// When found, make sure the TITLE is a direct child of HEAD,
		// and return the title's text (the Text node contained within).
		head = getHead();
		title = head.getElementsByTagName( "TITLE" ).item( 0 );
		if ( title != null )
		{
			if ( title.getParentNode() != head )
				head.appendChild( title );
			return ( (HTMLTitleElement) title ).getText();
		}
		// No TITLE found, return an empty string.
		return "";
	}
	public String getURL()
	{
		// Information not available on server side.
		return null;
	}
	public void open()
	{
		// When called an in-memory is prepared. The document tree is still
		// accessible the old way, until this writer is closed.
		if ( _writer == null )
			_writer = new StringWriter();
	}
	/**
	 * Called by the constructor to populate the element types list (see {@link
	 * #_elementTypesHTML}). Will be called multiple times but populate the list
	 * only the first time. Replacement for static constructor due to unknown
	 * problem with the static constructor.
	 */
	private static void populateElementTypes()
	{
		if ( _elementTypesHTML != null )
			return;
		_elementTypesHTML = new Hashtable( 63 );
		_elementTypesHTML.put( "A", HTMLAnchorElementImpl.class );
		_elementTypesHTML.put( "APPLET", HTMLAppletElementImpl.class );
		_elementTypesHTML.put( "AREA", HTMLAreaElementImpl.class );
		_elementTypesHTML.put( "BASE", HTMLBaseElementImpl.class );
		_elementTypesHTML.put( "BASEFONT", HTMLBaseFontElementImpl.class );
		_elementTypesHTML.put( "BLOCKQUOTE", HTMLBlockquoteElementImpl.class );
		_elementTypesHTML.put( "BODY", HTMLBodyElementImpl.class );
		_elementTypesHTML.put( "BR", HTMLBRElementImpl.class );
		_elementTypesHTML.put( "BUTTON", HTMLButtonElementImpl.class );
		_elementTypesHTML.put( "DEL", HTMLModElementImpl.class );
		_elementTypesHTML.put( "DIR", HTMLDirectoryElementImpl.class );
		_elementTypesHTML.put( "DIV", HTMLDivElementImpl.class );
		_elementTypesHTML.put( "DL", HTMLDListElementImpl.class );
		_elementTypesHTML.put( "FIELDSET", HTMLFieldSetElementImpl.class );
		_elementTypesHTML.put( "FONT", HTMLFontElementImpl.class );
		_elementTypesHTML.put( "FORM", HTMLFormElementImpl.class );
		_elementTypesHTML.put( "FRAME", HTMLFrameElementImpl.class );
		_elementTypesHTML.put( "FRAMESET", HTMLFrameSetElementImpl.class );
		_elementTypesHTML.put( "HEAD", HTMLHeadElementImpl.class );
		_elementTypesHTML.put( "H1", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "H2", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "H3", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "H4", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "H5", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "H6", HTMLHeadingElementImpl.class );
		_elementTypesHTML.put( "HR", HTMLHRElementImpl.class );
		_elementTypesHTML.put( "HTML", HTMLHtmlElementImpl.class );
		_elementTypesHTML.put( "IFRAME", HTMLIFrameElementImpl.class );
		_elementTypesHTML.put( "IMG", HTMLImageElementImpl.class );
		_elementTypesHTML.put( "INPUT", HTMLInputElementImpl.class );
		_elementTypesHTML.put( "INS", HTMLModElementImpl.class );
		_elementTypesHTML.put( "ISINDEX", HTMLIsIndexElementImpl.class );
		_elementTypesHTML.put( "LABEL", HTMLLabelElementImpl.class );
		_elementTypesHTML.put( "LEGEND", HTMLLegendElementImpl.class );
		_elementTypesHTML.put( "LI", HTMLLIElementImpl.class );
		_elementTypesHTML.put( "LINK", HTMLLinkElementImpl.class );
		_elementTypesHTML.put( "MAP", HTMLMapElementImpl.class );
		_elementTypesHTML.put( "MENU", HTMLMenuElementImpl.class );
		_elementTypesHTML.put( "META", HTMLMetaElementImpl.class );
		_elementTypesHTML.put( "OBJECT", HTMLObjectElementImpl.class );
		_elementTypesHTML.put( "OL", HTMLOListElementImpl.class );
		_elementTypesHTML.put( "OPTGROUP", HTMLOptGroupElementImpl.class );
		_elementTypesHTML.put( "OPTION", HTMLOptionElementImpl.class );
		_elementTypesHTML.put( "P", HTMLParagraphElementImpl.class );
		_elementTypesHTML.put( "PARAM", HTMLParamElementImpl.class );
		_elementTypesHTML.put( "PRE", HTMLPreElementImpl.class );
		_elementTypesHTML.put( "Q", HTMLQuoteElementImpl.class );
		_elementTypesHTML.put( "SCRIPT", HTMLScriptElementImpl.class );
		_elementTypesHTML.put( "SELECT", HTMLSelectElementImpl.class );
		_elementTypesHTML.put( "STYLE", HTMLStyleElementImpl.class );
		_elementTypesHTML.put( "TABLE", HTMLTableElementImpl.class );
		_elementTypesHTML.put( "CAPTION", HTMLTableCaptionElementImpl.class );
		_elementTypesHTML.put( "TD", HTMLTableCellElementImpl.class );
		_elementTypesHTML.put( "COL", HTMLTableColElementImpl.class );
		_elementTypesHTML.put( "COLGROUP", HTMLTableColElementImpl.class );
		_elementTypesHTML.put( "TR", HTMLTableRowElementImpl.class );
		_elementTypesHTML.put( "TBODY", HTMLTableSectionElementImpl.class );
		_elementTypesHTML.put( "THEAD", HTMLTableSectionElementImpl.class );
		_elementTypesHTML.put( "TFOOT", HTMLTableSectionElementImpl.class );
		_elementTypesHTML.put( "TEXTAREA", HTMLTextAreaElementImpl.class );
		_elementTypesHTML.put( "TITLE", HTMLTitleElementImpl.class );
		_elementTypesHTML.put( "UL", HTMLUListElementImpl.class );
	}
	public synchronized void setBody( HTMLElement newBody )
	{
		Node    html;
		Node    body;
		Node    head;
		Node    child;

		synchronized ( newBody )
		{
			// Call getDocumentElement() to get the HTML element that is also the
			// top-level element in the document. Get the first element in the
			// document that is called BODY. Work with that.
			html = getDocumentElement();
			head = getHead();
			synchronized ( html )
			{
				body = this.getElementsByTagName( "BODY" ).item( 0 );
				// BODY exists but might not follow HEAD in HTML. If not,
				// make it so and replce it. Start with the HEAD and make
				// sure the BODY is the first element after the HEAD.
				if ( body != null )
				{
					synchronized ( body )
					{
						child = head;
						while ( child != null )
						{
							if ( child instanceof Element )
							{
								if ( child != body )
									html.insertBefore( newBody, child );
								else
									html.replaceChild( newBody, body );
								return;
							}
							child = child.getNextSibling();
						}
						html.appendChild( newBody );
					}
					return;
				}
				// BODY does not exist, place it in the HTML element
				// right after the HEAD.
				html.appendChild( newBody );
			}
		}
	}
	public void setCookie( String cookie )
	{
		// Information not available on server side.
	}
	public synchronized void setTitle( String newTitle )
	{
		HTMLElement    head;
		Node        title;

		// Get the HEAD element and look for the TITLE element within.
		// When found, make sure the TITLE is a direct child of HEAD,
		// and set the title's text (the Text node contained within).
		head = getHead();
		title = head.getElementsByTagName( "TITLE" ).item( 0 );
		if ( title != null )
		{
			if ( title.getParentNode() != head )
				head.appendChild( title );
			( (HTMLTitleElement) title ).setText( newTitle );
		}
		else
		{
			// No TITLE found, create a new element and place it at the end
			// of the HEAD element.
			title = new HTMLTitleElementImpl( (HTMLDocumentImpl) getOwnerDocument(), "TITLE" );
			head.appendChild( title );
		}
	}
	public void write( String text )
	{
		// Write a string into the in-memory writer.
		if ( _writer != null )
			_writer.write( text );
	}
	public void writeln( String text )
	{
		// Write a line into the in-memory writer.
		if ( _writer != null )
			_writer.write( text + "\n" );
	}
}