package org.openxml.dom.ext;

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
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public interface DocumentTypeEx
{


	/**
	 * Returns the public identifier associated with the external DTD
	 * subset, if specified.
	 * 
	 *  @return The public identifier, or null
	 */
	public String getPublicId();
	/**
	 * Returns the system identifier associated with the external DTD
	 * subset, if specified.
	 * 
	 * @return The system identifier, or null
	 */
	public String getSystemId();
	/**
	 * Returns the internal DTD subset as textual representation.
	 * If available, this representation should be identical to the
	 * text appearing in the source document.
	 *
	 * @return The internal DTD subset as text, or null
	 */
	public String internalAsText();
	/**
	 * Returns true if the document is standalone, that is, has no
	 * reference to an external DTD subset.
	 * 
	 * @return True if standalone document.
	 */
	public boolean isStandalone();
}