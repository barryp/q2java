package org.openxml.x3p;

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


import java.io.IOException;


/**
 * Interface for a publisher creator. Creators are registered with the
 * publisher factory and asked to create a publisher for the specified
 * target. A creator should only return a publisher if it supports the
 * requested target, otherwise it must return null.
 * <P>
 * {@link #isSupported} asks the creator if it can create a new publisher
 * for a specific target, and {@link #listTargets} lists the target
 * classes supported by this creator.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Publisher
 * @see PublisherFactory
 */
public interface PublisherCreator
{


	/**
	 * Called to create a new publisher for the specified target. If no
	 * suitable publisher can be created, this method should simply
	 * return null. If a publisher is returned it will be used.
	 * 
	 * @param target The publisher target
	 * @return New publisher, or null
	 */
	public Publisher createPublisher( PublisherTarget target )
		throws IOException;
	/**
	 * Returns true if a publisher can be created for the specified target.
	 * If this method returns true, {@link #createPublisher} should return
	 * such a publisher.
	 * 
	 * @param target The publisher target
	 * @return True is publisher can be created
	 */
	public boolean isSupported( PublisherTarget target );
	/**
	 * Returns a list of supported publisher targets. The list is returned
	 * as an array of classes (non-abstract) extending {@link
	 * PublisherTarget}. Any of these classes can be constructed and
	 * used to invoke this publisher. If the list is empty, null may be
	 * returned.
	 * 
	 * @return Array of target classes
	 */
	public Class[] listTargets();
}