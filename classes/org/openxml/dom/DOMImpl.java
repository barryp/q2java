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


/**
 * Sep 23, 1999
 * + Added DOMImplemented outside of Document.
 * + Added DOM 2 support in hasFeature.
 * + Added DOM 2 support with createDocument() and createDocumentType().
 **/


package org.openxml.dom;
    

import org.w3c.dom.*;


/**
 * Provides number of methods for performing operations that are independent
 * of any particular instance of the document object model. This class is
 * unconstructable, the only way to obtain an instance of a DOM implementation
 * is by calling the static method {@link #getDOMImplementation}.
 * 
 * @version $Revision: 1.1 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.DOMImplementation
 */
public class DOMImpl
    implements DOMImplementation
{


    /**
     * Return true if certain feature for specific DOM version supported by
     * this implementation. Currently supports DOM Level 1 and 2, HTML,
     * Traversal and Views.
     * 
     * @param feature Name of feature to check
     * @param version Optional version number
     * @return True if supported
     */
    public final boolean hasFeature( String feature, String version )
    {
        // Versions 1.0 and 2.0 are supported.
        // DOM, HTML DOM and Traversal APIs are supported.
        if ( version == null || version.equals( "1.0" ) ||
             version.equals( "2.0" ) ) {
            if ( feature != null && ( feature.equals( "HTML" ) ||
                                      feature.equals( "Views" ) ||
                                      feature.equals( "Traversal" ) ) )
                return true;
        }
        return false;
    }


    /**
     * Creates an empty document type. The document type has qualified name,
     * public and system identifiers, but no entities or notations.
     *
     * @param qualifiedName The name of the document type to be created
     * @param publicID The document type public identifier
     * @param systemID The document type system identifier
     * @return A new document type
     */
    public final DocumentType createDocumentType( String qualifiedName,
                                                  String publicId, String systemId )
    {
        return createDocumentType( qualifiedName, publicId, systemId, null );
    }


    public DocumentType createDocumentType( String qualifiedName, 
                                            String publicId, String systemId, 
                                            String internalSubset )
    {
        DocumentTypeImpl docType;
        
        NodeImpl.checkName( qualifiedName );
        docType = new DocumentTypeImpl( qualifiedName, publicId, systemId
, internalSubset );
        docType.makeReadOnly();
        return docType;
    }


    /**
     * Create a new document of the specified type with its document element.
     *
     * @param namespaceURI The namespace URI of the document, or null
     * @param qualifiedName The qualified name of the document element
     * @param docType The type of the document, or null
     * @return New document
     * @throws DOMException The document type is already used, or the qualified
     *   name is invalid
     */
    public final Document createDocument( String namespaceURI,
                                          String qualifiedName, DocumentType docType )
        throws DOMException
    {
        NodeImpl.checkName( qualifiedName );
        return new DocumentImpl( namespaceURI, qualifiedName, docType );
    }


    /**
     * Returns an instance of a {@link DOMImplementation} that can be used to
     * perform operations that are not specific to a particular document
     * instance, e.g. to create a new document.
     *
     * @return Reference to a valid DOM implementation
     */
    public static DOMImplementation getDOMImplementation()
    {
        return _instance;
    }


    /**
     * Private constructor assures that an object of this class cannot
     * be created. The only way to obtain an object is by calling {@link
     * #getDOMImplementation}.
     */
    protected DOMImpl()
    {
    }


    /**
     * Holds a reference to the single instance of the DOM implementation.
     * Only one instance is required since this class is multiple entry.

     */
    private static DOMImplementation _instance = new DOMImpl();


}
