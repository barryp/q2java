package org.openxml.dom;

/**
 * org/openxml/dom/DocumentImpl.java
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


import java.lang.reflect.*;
import java.util.*;
import org.w3c.dom.*;
import org.openxml.*;
import org.openxml.dom.iterator.ElementTagFilter;


/**
 * Implements an XML document, and also derived to implement an HTML document.
 * Provides access to the top level element in the document ({@link
 * #getDocumentElement}), to the DTD if one exists ({@link #getDoctype},
 * and to all node operations.
 * <P>
 * Several methods create new nodes of all basic types (comment, text, element,
 * etc.). These methods create new nodes but do not place them in the document
 * tree. The nodes may be placed in the document tree using {@link
 * org.w3c.dom.Node#appendChild} or {@link org.w3c.dom.Node#insertBefore}, or
 * they may be placed in some other document tree.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#DOCUMENT_NODE}
 * <LI>Node supports childern
 * <LI>Node name is always "#document"
 * <LI>Node does not have a value
 * <LI>Node may not be added to other nodes
 * </OL>
 * This class contains some extensions beyond the DOM API definition. For a list
 * of all extensions, see {@link org.openxml.XMLDocument}.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.Document
 * @see NodeImpl
 * @see org.w3c.dom.DOMImplementation
 */
public class DocumentImpl
	extends NodeImpl
	implements Document, DOMImplementation
{
	
	
	/**
	 * The document type definition of this document (per DOM API). Only available
	 * if document was created with a DTD and not available for HTML documents.
	 */
	private DocumentTypeImpl        _docType;

	
	/**
	 * Holds a reference to the locking thread. When unlocked, this reference is
	 * null. This object is used both to designate a lock, identify the locking
	 * thread, and perform a {@link Object#wait}.
	 * 
	 * @see #lock
	 * @see #unlock
	 */
	private Thread                    _lockThread;
	
	
	/**
	 * Implement counting on locks, allowing {@link #lock} to be called a
	 * multiple number of times, and {@link #unlock} to still retain the lock
	 * on the outer calls.
	 * 
	 * @see #lock
	 * @see #unlock
	 */
	private int                        _lockCount;

	
	/**
	 * Holds names and classes of application element types. When an element
	 * with a particular tag name is created, the matching {@link java.lang.Class}
	 * is used to create the element object. This reference is null unless an
	 * element has been defined.
	 */
	private Hashtable                _elementTypes;
	
	
	/**
	 * Factory used to create new elements. The document may be associated with
	 * a factory that will get a first chance to create XML elements.
	 */
	private XMLElementFactory        _elementFactory;
	
	
	/**
	 * Signature used to locate constructor of application element class.
	 * This static array is shared across all XML documents and only allows
	 * for application defined elements based on {@link org.openxml.XMLElement}.
	 * 
	 * @see #createElement
	 */
	private static final Class[]    _elemClassSig =
				new Class[] { org.openxml.XMLDocument.class, String.class };


	public DocumentImpl()
	{
		super( null, "#document", null, false );
		_ownerDocument = this;
	}
	protected DocumentImpl( String rootElement )
	{
		super( null, ( rootElement != null ? rootElement : "" ),
			   null, ( rootElement != null ) );
		_ownerDocument = this;
	}
	/**
	 * Called to acquire access to a possible locked resource. If the resource
	 * was locked by the current thread, the method will return immediately.
	 * If the resource was locked by some other thread, the method will block
	 * until the resource is unlocked, or the block has timed out.
	 * 
	 * @see #lock
	 * @see #unlock
	 */
	public void acquire( long lockTimeout )
		throws RuntimeException
	{
		long    start;
		long    timeout;
		
		synchronized ( this )
		{
			try
			{
				// If the application is not locked by the current thread,
				// wait until the application is unlocked, then proceed.
				if ( _lockThread != Thread.currentThread() && _lockThread != null )
				{
					// Remember when started waiting for lock to implement
					// timeout. Repeat until lock is released by other thread,
					// or until timeout has expired.
					start = System.currentTimeMillis();
					while ( _lockThread != null &&
							lockTimeout > System.currentTimeMillis() - start )
					{
						// If the locking thread is dead, release the lock
						// immediately, otherwise, wait for it to be released.
						if ( ! _lockThread.isAlive() )
							_lockThread = null;
						else
							wait( lockTimeout - System.currentTimeMillis() - start );
					}
					// Timeout, throw an exception.
					if ( _lockThread != null )
						throw new RuntimeException( "Timeout waiting for lock to be released." );
				}
			}
			catch ( InterruptedException except )
			{
				// Thread interrupted, throw an exception.
				throw new RuntimeException( "Timeout waiting for lock to be released." );
			}
		}
	}
	public void assignDoctype( DocumentTypeImpl docType )
	{
		if ( docType == null )
			throw new NullPointerException( "Argument 'docType' is null." );
		if ( _docType != null )
			throw new IllegalStateException( "Document type already assigned to this document." );
		_docType = docType;
	}
	public Object clone()
	{
		DocumentImpl    clone;
		
		clone = new DocumentImpl();
		cloneInto( clone, true );
		return clone;
	}
	protected synchronized void cloneInto( NodeImpl into, boolean deep )
	{
		DocumentTypeImpl    docType;
		
		// Use the Node cloning method. The DOM object does not need cloning,
		// but the document type (which contains entities, notations, etc)
		// must be cloned.
		super.cloneInto( into, deep );
		if ( deep )
		{
			if ( _docType != null )
				( (DocumentImpl) into )._docType = (DocumentTypeImpl) _docType.clone();
			// If application elements are defined, clone the list as well.
			if ( _elementTypes != null )
				( (DocumentImpl) into )._elementTypes = (Hashtable) _elementTypes.clone();
		}
		else
		{
			( (DocumentImpl) into )._docType = _docType;
			( (DocumentImpl) into )._elementTypes = _elementTypes;
		}
		( (DocumentImpl) into )._elementFactory = _elementFactory;
	}
	public Node cloneNode( boolean deep )
	{
		DocumentImpl    clone;
			
		clone = new DocumentImpl();
		cloneInto( clone, deep );
		return clone;
	}
	public final Attr createAttribute( String name )
		throws DOMException
	{
		return new AttrImpl( this, name, "" );
	}
	/**
	 * Creates an attribute with the default value specified in the DTD.
	 * This method is not defined in the DOM but is used by the parser.
	 * 
	 * @param name The name of the attribute
	 * @param defValue The default value of the attribute
	 */
	public final Attr createAttribute( String name, String defValue )
		throws DOMException
	{
		return new AttrImpl( this, name, defValue == null ? "" : defValue );
	}
	public final CDATASection createCDATASection( String data )
		throws DOMException
	{
		return new CDATASectionImpl( this, data );
	}
	public final Comment createComment( String data )
	{
		return new CommentImpl( this, data );
	}
	public final DocumentFragment createDocumentFragment()
	{
		return new DocumentFragmentImpl( this );
	}
	public Element createElement( String tagName )
		throws DOMException
	{
		Class        elemClass;
		Constructor    cnst;
		Element        elem;

		if ( _elementFactory != null )
		{
			elem = _elementFactory.createElement( (XMLDocument) this, tagName );
			if ( elem != null )
				return elem;
		}
		
		// If this tag was registered as an application type, create an element
		// object of the specified class and return it. Otherwise, create the
		// default Element. The constructor signature is defined statically to
		// save space.
		if ( _elementTypes != null )
		{
			elemClass = (Class) _elementTypes.get( tagName );
			if ( elemClass != null )
			{
				try
				{
					cnst = elemClass.getConstructor( _elemClassSig );
					return (Element) cnst.newInstance( new Object[] { this, tagName } );
				}
				catch ( Exception except )
				{
					throw new IllegalStateException( "Tag '" + tagName + "' associated with an Element class that failed to construct." );
				}
			}
		}
		
		return new XMLElement( this, tagName );
	}
	public final EntityReference createEntityReference( String name )
		throws DOMException
	{
		return new EntityReferenceImpl( this, name );
	}
	public final ProcessingInstruction
		createProcessingInstruction( String target, String data )
		throws DOMException
	{
		return new ProcessingInstructionImpl( this, target, data );
	}
	public final Text createTextNode( String data )
	{
		return new TextImpl( this, data );
	}
	public synchronized boolean equals( Object other )
	{
		DocumentImpl    otherX;

		// Use Node's equals method to perform the first tests of equality.
		// If these tests do not pass, return false.
		if ( ! super.equals( other ) )
			return false;

		// Very simple equality test: are the document types equal.
		// There's nothing else about the document to compare.
		synchronized ( other )
		{
			otherX = (DocumentImpl) other;
			return ( ( _docType == null && otherX._docType == null ) ||
					 ( _docType != null && otherX._docType != null &&
					   _docType.equals( otherX._docType ) ) );
		}
	}
	public final DocumentType getDoctype()
	{
		return _docType;
	}
	public Element getDocumentElement()
	{
		Node    child;
		
		// Returns the top-level element in this document. Might not exist, or
		// might be the first of many.
		child = getFirstChild();
		while ( child != null )
		{
			if ( child instanceof Element )
				return (Element) child;
			child = child.getNextSibling();
		}
		return null;
	}
	/**
	 * Creates a document type with the specified name (the name follows the
	 * <TT>!DOCTYPE</TT> entity) and associates it with the document.
	 * If the document is HTML or already has a DTD associated with it, throws
	 * an exception. This method is not defined in the DOM but is used by the
	 * parser.
	 * 
	 * @param name The name of the document type
	 * @param name The public identifier, if exists
	 * @param name The system identifier, if exists
	 * @throws org.w3c.dom.DOMException <T>NOT_SUPPORTED_ERR</TT>
	 *  Document is HTML or already has a DTD
	 */
/*    public final DocumentType createDocumentType( String name, String publicId,
												  String systemId )
		throws DOMException
	{
		if ( isHtmlDoc() || _docType != null )
			throw new DOMExceptionImpl( DOMException.NOT_SUPPORTED_ERR );
		_docType = new DocumentTypeImpl( this, name );
		return _docType;
	}
*/
	
	public final NodeList getElementsByTagName( String tagName )
	{
		return (NodeList) createTreeIterator( TW_ELEMENT, ElementTagFilter.lookup( tagName ) );
	}
	public final DOMImplementation getImplementation()
	{
		return this;
	}
	public short getNodeType()
	{
		return DOCUMENT_NODE;
	}
	/**
	 * Return true if certain feature for specific DOM version supported by this
	 * implementation.
	 * 
	 * @param feature Name of feature to check
	 * @param version Optional version number
	 * @return True if supported
	 */
	public boolean hasFeature( String feature, String version )
	{
		// Versions higher than 1.0 not supported yet.
		if ( version != null )
		{
			if ( version.indexOf( "1.0" ) < 0 &&
				 version.indexOf( "Level 1" ) < 0 )
				return false;
		}
		// Supports XML and HTML.
		if ( feature != null )
		{
			if ( feature.equalsIgnoreCase( "XML" ) ||
				 feature.equalsIgnoreCase( "HTML" ) )
				return true;
		}
		return false;
	}
	/**
	* Implements a lock mechanism. The lock mechanism differs from synchronization
	* in that it supports multiple function calls, while still preserving the lock
	* granted to a specific thread. Caution is required as other threads may obtain
	* access without first acquiring lock. All implementations of the lock mechanism
	* should use {@link #lock}, {@link #unlock} and {@link #acquire} to assure true
	* synchronization.
	* <P>
	* Calling {@link #lock} obtains a lock to the current running thread. If the
	* lock has already been granted to some other thread, this call will block
	* until the lock is released or until the lock has timed out.
	* <P>
	* Calling {@link #unlock} releases the lock from the current thread, if still
	* held by the current thread. A thread may call {@link #lock} any number of
	* times and must call {@link #unlock} that number of times to release the
	* lock. The thread may call {@link #unlock} an additional number of times if
	* necessary by the implementation.
	* <P>
	* If a thread requires access to a resource that might be lock, it should
	* acquire it by calling {@link #acquire}. This method will block until the 
	* resource is unlocked by some other thread. If the resource is unlocked,
	* or locked by the current thread, {@link #acquire} will return immediately.
	*/
   
   /**
	 * Obtains a lock, preventing other threads from gaining access to the locked
	 * resource. The lock is retained across multiple calls, until {@link #unlock}
	 * is called. Attempts to call {@link #acquire} from another thread will be
	 * blocked.
	 * <P>
	 * If the resource is already locked by another thread, the method will
	 * block until the lock is released or until the block has timedout.
	 * <P>
	 * {@link #lock} may be called any number of times, and {@link #unlock} must
	 * be called that number of times to release the lock. 
	 * 
	 * @see #unlock
	 */
	public void lock()
		throws RuntimeException
	{
		synchronized ( this )
		{
			// If current thread is locking, increase the lock count.
			// Otherwise, wait for lock to be released, acquire the lock and
			// increase the lock count. If timeout has reached or thread has
			// been interrupted, acquire() will throw an exception.
			if ( _lockThread == Thread.currentThread() )
				++ _lockCount;
			else
			{
				acquire( Long.MAX_VALUE );
				_lockThread = Thread.currentThread();
				++ _lockCount;
			}
		}
	}
	/**
	 * Register an application-defined element type. The element's tag name and
	 * class are specified. When a so named element is created with {@link
	 * #createElement}, an object of the specified class is created and returned.
	 * This allows applications to define classes for specific element types.
	 * 
	 * @param tagName The name of the element tag
	 * @param elementClass Class derived from {@link org.openxml.XMLElement},
	 *  used to construct the element
	 */
	public void registerElement( String tagName, Class elementClass )
	{
		if ( tagName == null || tagName.length() == 0 )
			throw new NullPointerException( "Argument 'tagName' is null or an empty string." );
		if ( elementClass == null || elementClass.isAssignableFrom( Element.class ) )
			throw new IllegalArgumentException( "Argument 'elementClass' is null or does not extend Element." );
		if ( _elementTypes == null )
			_elementTypes = new Hashtable();
		_elementTypes.put( tagName, elementClass );
	}
	public final void setNodeValue( String value )
	{
		throw new DOMExceptionImpl( DOMException.NO_DATA_ALLOWED_ERR,
			"This node type does not support values." );
	}
	protected final boolean supportsChildern()
	{
		return true;
	}
	public String toString()
	{
		return "Document: ";
	}
	/**
	 * Releases a lock, so other thread may gain access to the resource. {@link
	 * #unlock} must be called as many times as {@link #lock} was called to
	 * release the lock. {@link #unlock} may be called an additional number of
	 * times, if so required by the implementation (e.g. to assure that a lock
	 * is released at the end of a thread).
	 * 
	 * @see #lock
	 */
	public void unlock()
	{
		// If the current thread is locking, decrease the lock count.
		// If the lock count is zero, release the lock, otherwise unlock()
		// must be called again to release lock.
		if ( _lockThread == Thread.currentThread() )
		{
			synchronized( this )
			{
				-- _lockCount;
				if ( _lockCount == 0 )
				{
					_lockThread = null;
					notify();
				}
			}
		}
	}
	/**
	 * Sets the element factory for this document. The element factory is given
	 * a first chance to construct new elements based on the tag name.
	 * 
	 * @param elementFactory The new element factory
	 */
	public void useElementFactory( XMLElementFactory elementFactory )
	{
		if ( elementFactory != this )
			_elementFactory = elementFactory;
	}
}