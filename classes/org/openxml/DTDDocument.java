package org.openxml;

/**
 * org/openxml/DTDDocument.java
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

import java.util.*;
import org.w3c.dom.*;
import org.openxml.dom.*;


/**
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 */
public final class DTDDocument
	extends DocumentTypeImpl
	implements Document, DocumentType
{


	public DTDDocument()
	{
		super( null );
	}
	public DTDDocument( Document owner, String rootElement, boolean standalone,
						String systemId, String publicId )
	{
		super( owner, rootElement, standalone, systemId, publicId );
	}
	public Object clone()
	{
		DTDDocument clone;
		
		clone = new DTDDocument();
		cloneInto( clone, true );
		return clone;
	}
	public Node cloneNode( boolean deep )
	{
		DTDDocument clone;
			
		clone = new DTDDocument();
		cloneInto( clone, deep );
		return clone;
	}
}