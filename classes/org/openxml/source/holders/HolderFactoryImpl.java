package org.openxml.source.holders;

/**
 * org/openxml/source/holders/HolderFactoryImpl.java
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


import org.openxml.source.*;


/**
 * Super class for all holder factory implementations in this package.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:59 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see HolderFactory
 */
public abstract class HolderFactoryImpl
	implements HolderFactory
{
	
	
	/**
	 * Called to look up an alternative holder for this source. If the source
	 * has been mapped to a different source (e.g. resources and catalogs),
	 * this method will lookup and return an holder based on the new mapping.
	 * 
	 * @param source The new source
	 * @return Holder for that source
	 */
	protected final Holder findHolder( Source source )
	{
		return HolderFinderImpl.getHolderFinder().findHolder( source );
	}
}