// XMLFilterImpl.java - base SAX2 filter implementation.
// Written by David Megginson, sax@megginson.com
// NO WARRANTY!  This class is in the Public Domain.

// $Id: XMLFilterImpl.java,v 1.1 2000/04/04 23:57:09 barryp Exp $

package org.xml.sax.helpers;

import java.io.IOException;

import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;


/**
 * Base class for deriving an XML filter.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This class is designed to sit between an XMLReader and
 * the client application's event handlers.  By default, it
 * does nothing but pass requests up to the reader and events
 * on to the handlers unmodified, but subclasses can override
 * specific methods to modify the event stream or the configuration
 * requests as they pass through.</p>
 *
 * @since SAX 2.0
 * @author David Megginson, 
 *         <a href="mailto:sax@megginson.com">sax@megginson.com</a>
 * @version 2.0beta
 * @see org.xml.sax.XMLFilter
 * @see org.xml.sax.XMLReader
 * @see org.xml.sax.EntityResolver
 * @see org.xml.sax.DTDHandler
 * @see org.xml.sax.ContentHandler
 * @see org.xml.sax.ErrorHandler
 */
public class XMLFilterImpl
    implements XMLFilter, EntityResolver, DTDHandler,
               ContentHandler, ErrorHandler
{


    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Construct an empty XML filter, with no parent.
     *
     * <p>This filter will have no parent: you must assign a parent
     * before you start a parse or do any configuration with
     * setFeature or setProperty.</p>
     *
     * @see org.xml.sax.XMLReader#setFeature
     * @see org.xml.sax.XMLReader#setProperty
     */
    public XMLFilterImpl ()
    {
	super();
    }


    /**
     * Construct an XML filter with the specified parent.
     *
     * @see #setParent
     * @see #getParent
     */
    public XMLFilterImpl (XMLReader parent)
    {
        super();
	setParent(parent);
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.XMLFilter.
    ////////////////////////////////////////////////////////////////////


    /**
     * Set the parent reader.
     *
     * <p>This is the XMLReader from which this filter will obtain
     * its events and to which it will pass its configuration
     * requests.  The parent may itself be another filter.</p>
     *
     * <p>If there is no parent reader set, any attempt to parse
     * or to set or get a feature or property will fail.</p>
     *
     * @param parent The parent XML reader.
     * @exception java.lang.NullPointerException If the parent is null.
     * @see #getParent
     */
    public void setParent (XMLReader parent)
    {
	if (parent == null) {
	    throw new NullPointerException("Null parent");
	}
	this.parent = parent;
    }


    /**
     * Get the parent reader.
     *
     * @return The parent reader, or null if none is set.
     * @see #setParent
     */
    public XMLReader getParent ()
    {
	return parent;
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.XMLReader.
    ////////////////////////////////////////////////////////////////////


    /**
     * Set the state of a feature.
     *
     * <p>This will always fail if the parent is null.</p>
     *
     * @param name The feature name.
     * @param state The requested feature state.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but 
     *            cannot set the requested value.
     * @see org.xml.sax.XMLReader#setFeature
     */
    public void setFeature (String name, boolean state)
	throws SAXNotRecognizedException, SAXNotSupportedException
    {
	if (parent != null) {
	    parent.setFeature(name, state);
	} else {
	    throw new SAXNotRecognizedException("Feature: " + name);
	}
    }


    /**
     * Look up the state of a feature.
     *
     * <p>This will always fail if the parent is null.</p>
     *
     * @param name The feature name.
     * @return The current state of the feature.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the feature name but 
     *            cannot determine its state at this time.
     * @see org.xml.sax.XMLReader#getFeature
     */
    public boolean getFeature (String name)
	throws SAXNotRecognizedException, SAXNotSupportedException
    {
	if (parent != null) {
	    return parent.getFeature(name);
	} else {
	    throw new SAXNotRecognizedException("Feature: " + name);
	}
    }


    /**
     * Set the value of a property.
     *
     * <p>This will always fail if the parent is null.</p>
     *
     * @param name The property name.
     * @param state The requested property value.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the property name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but 
     *            cannot set the requested value.
     * @see org.xml.sax.XMLReader#setProperty
     */
    public void setProperty (String name, Object value)
	throws SAXNotRecognizedException, SAXNotSupportedException
    {
	if (parent != null) {
	    parent.setProperty(name, value);
	} else {
	    throw new SAXNotRecognizedException("Property: " + name);
	}
    }


    /**
     * Look up the value of a property.
     *
     * @param name The property name.
     * @return The current value of the property.
     * @exception org.xml.sax.SAXNotRecognizedException When the
     *            XMLReader does not recognize the feature name.
     * @exception org.xml.sax.SAXNotSupportedException When the
     *            XMLReader recognizes the property name but 
     *            cannot determine its value at this time.
     * @see org.xml.sax.XMLReader#setFeature
     */
    public Object getProperty (String name)
	throws SAXNotRecognizedException, SAXNotSupportedException
    {
	if (parent != null) {
	    return parent.getProperty(name);
	} else {
	    throw new SAXNotRecognizedException("Property: " + name);
	}
    }


    /**
     * Set the entity resolver.
     *
     * @param resolver The new entity resolver.
     * @exception java.lang.NullPointerException If the resolver
     *            is null.
     * @see org.xml.sax.XMLReader#setEntityResolver
     */
    public void setEntityResolver (EntityResolver resolver)
    {
	if (resolver == null) {
	    throw new NullPointerException("Null entity resolver");
	} else {
	    entityResolver = resolver;
	}
    }


    /**
     * Get the current entity resolver.
     *
     * @return The current entity resolver, or null if none was set.
     * @see org.xml.sax.XMLReader#getEntityResolver
     */
    public EntityResolver getEntityResolver ()
    {
	return entityResolver;
    }


    /**
     * Set the DTD event handler.
     *
     * @param resolver The new DTD handler.
     * @exception java.lang.NullPointerException If the handler
     *            is null.
     * @see org.xml.sax.XMLReader#setDTDHandler
     */
    public void setDTDHandler (DTDHandler handler)
    {
	if (handler == null) {
	    throw new NullPointerException("Null DTD handler");
	} else {
	    dtdHandler = handler;
	}
    }


    /**
     * Get the current DTD event handler.
     *
     * @return The current DTD handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getDTDHandler
     */
    public DTDHandler getDTDHandler ()
    {
	return dtdHandler;
    }


    /**
     * Set the content event handler.
     *
     * @param resolver The new content handler.
     * @exception java.lang.NullPointerException If the handler
     *            is null.
     * @see org.xml.sax.XMLReader#setContentHandler
     */
    public void setContentHandler (ContentHandler handler)
    {
	if (handler == null) {
	    throw new NullPointerException("Null content handler");
	} else {
	    contentHandler = handler;
	}
    }


    /**
     * Get the content event handler.
     *
     * @return The current content handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getContentHandler
     */
    public ContentHandler getContentHandler ()
    {
	return contentHandler;
    }


    /**
     * Set the error event handler.
     *
     * @param handle The new error handler.
     * @exception java.lang.NullPointerException If the handler
     *            is null.
     * @see org.xml.sax.XMLReader#setErrorHandler
     */
    public void setErrorHandler (ErrorHandler handler)
    {
	if (handler == null) {
	    throw new NullPointerException("Null error handler");
	} else {
	    errorHandler = handler;
	}
    }


    /**
     * Get the current error event handler.
     *
     * @return The current error handler, or null if none was set.
     * @see org.xml.sax.XMLReader#getErrorHandler
     */
    public ErrorHandler getErrorHandler ()
    {
	return errorHandler;
    }


    /**
     * Parse a document.
     *
     * @param input The input source for the document entity.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse (InputSource input)
	throws SAXException, IOException
    {
	setupParse();
	parent.parse(input);
    }


    /**
     * Parse a document.
     *
     * @param systemId The system identifier as a fully-qualified URI.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse (String systemId)
	throws SAXException, IOException
    {
	setupParse();
	parent.parse(systemId);
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.EntityResolver.
    ////////////////////////////////////////////////////////////////////


    /**
     * Filter an external entity resolution.
     *
     * @param publicId The entity's public identifier, or null.
     * @param systemId The entity's system identifier.
     * @return A new InputSource or null for the default.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @exception java.io.IOException The client may throw an
     *            I/O-related exception while obtaining the
     *            new InputSource.
     * @see org.xml.sax.EntityResolver#resolveEntity
     */
    public InputSource resolveEntity (String publicId, String systemId)
	throws SAXException, IOException
    {
	if (entityResolver != null) {
	    return entityResolver.resolveEntity(publicId, systemId);
	} else {
	    return null;
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.DTDHandler.
    ////////////////////////////////////////////////////////////////////

    
    /**
     * Filter a notation declaration event.
     *
     * @param name The notation name.
     * @param publicId The notation's public identifier, or null.
     * @param systemId The notation's system identifier, or null.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.DTDHandler#notationDecl
     */
    public void notationDecl (String name, String publicId, String systemId)
	throws SAXException
    {
	if (dtdHandler != null) {
	    dtdHandler.notationDecl(name, publicId, systemId);
	}
    }

    
    /**
     * Filter an unparsed entity declaration event.
     *
     * @param name The entity name.
     * @param publicId The entity's public identifier, or null.
     * @param systemId The entity's system identifier, or null.
     * @param notationName The name of the associated notation.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.DTDHandler#unparsedEntityDecl
     */
    public void unparsedEntityDecl (String name, String publicId,
				    String systemId, String notationName)
	throws SAXException
    {
	if (dtdHandler != null) {
	    dtdHandler.unparsedEntityDecl(name, publicId, systemId,
					  notationName);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.ContentHandler.
    ////////////////////////////////////////////////////////////////////


    /**
     * Filter a new document locator event.
     *
     * @param locator The document locator.
     * @see org.xml.sax.ContentHandler#setDocumentLocator
     */
    public void setDocumentLocator (Locator locator)
    {
	this.locator = locator;
	if (contentHandler != null) {
	    contentHandler.setDocumentLocator(locator);
	}
    }


    /**
     * Filter a start document event.
     *
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument ()
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.startDocument();
	}
    }


    /**
     * Filter an end document event.
     *
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument ()
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.endDocument();
	}
    }


    /**
     * Filter a start Namespace prefix mapping event.
     *
     * @param prefix The Namespace prefix.
     * @param uri The Namespace URI.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#startPrefixMapping
     */
    public void startPrefixMapping (String prefix, String uri)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.startPrefixMapping(prefix, uri);
	}
    }


    /**
     * Filter an end Namespace prefix mapping event.
     *
     * @param prefix The Namespace prefix.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#endPrefixMapping
     */
    public void endPrefixMapping (String prefix)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.endPrefixMapping(prefix);
	}
    }


    /**
     * Filter a start element event.
     *
     * @param uri The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param rawName The element's raw XML 1.0 name, or the empty
     *        string.
     * @param atts The element's attributes.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement (String uri, String localName, String rawName,
			      Attributes atts)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.startElement(uri, localName, rawName, atts);
	}
    }


    /**
     * Filter an end element event.
     *
     * @param uri The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param rawName The element's raw XML 1.0 name, or the empty
     *        string.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement (String uri, String localName, String rawName)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.endElement(uri, localName, rawName);
	}
    }


    /**
     * Filter a character data event.
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.characters(ch, start, length);
	}
    }


    /**
     * Filter an ignorable whitespace event.
     *
     * @param ch An array of characters.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#ignorableWhitespace
     */
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.ignorableWhitespace(ch, start, length);
	}
    }


    /**
     * Filter a processing instruction event.
     *
     * @param target The processing instruction target.
     * @param data The text following the target.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#processingInstruction
     */
    public void processingInstruction (String target, String data)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.processingInstruction(target, data);
	}
    }


    /**
     * Filter a skipped entity event.
     *
     * @param name The name of the skipped entity.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ContentHandler#skippedEntity
     */
    public void skippedEntity (String name)
	throws SAXException
    {
	if (contentHandler != null) {
	    contentHandler.skippedEntity(name);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.ErrorHandler.
    ////////////////////////////////////////////////////////////////////


    /**
     * Filter a warning event.
     *
     * @param e The nwarning as an exception.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ErrorHandler#warning
     */
    public void warning (SAXParseException e)
	throws SAXException
    {
	if (errorHandler != null) {
	    errorHandler.warning(e);
	}
    }


    /**
     * Filter an error event.
     *
     * @param e The error as an exception.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ErrorHandler#error
     */
    public void error (SAXParseException e)
	throws SAXException
    {
	if (errorHandler != null) {
	    errorHandler.error(e);
	}
    }


    /**
     * Filter a fatal error event.
     *
     * @param e The error as an exception.
     * @exception org.xml.sax.SAXException The client may throw
     *            an exception during processing.
     * @see org.xml.sax.ErrorHandler#fatalError
     */
    public void fatalError (SAXParseException e)
	throws SAXException
    {
	if (errorHandler != null) {
	    errorHandler.fatalError(e);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Set up before a parse.
     *
     * <p>Before every parse, check whether the parent is
     * non-null, and re-register the filter for all of the 
     * events.</p>
     */
    private void setupParse ()
    {
	if (parent == null) {
	    throw new NullPointerException("No parent for filter");
	}
	parent.setEntityResolver(this);
	parent.setDTDHandler(this);
	parent.setContentHandler(this);
	parent.setErrorHandler(this);
    }



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private XMLReader parent = null;
    private Locator locator = null;
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;

}

// end of XMLFilterImpl.java
