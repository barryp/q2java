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


import java.util.*;

/**
 * The process context is used to pass parameters to the processor, retrieve
 * results and reserve context for engines. The context is created and maintained
 * by the processor. The application can set parameters in the context and read
 * processing results, and engines can save contexts and exchange information
 * through it. It is recommended that context objects be named in distinct
 * namespaces (e.g. "xml.stylesheet.url").
 * <P>
 * The activator should belong to the environment which created this processor,
 * allowing engines to interact with execution environment. For example, the
 * Servlet or EJB bean underwhich this processor is running. There is no
 * guarantee that the activator will be any useful object.
 * <P>
 * Each context is also associated with a locale. Engines should use this locale
 * for generating textual information, processing dates, sorting, and other
 * locale-sensitive operations. Initially, the system default locale is used.
 *
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Processor
 * @see ProcessorEngine
 */
public interface ProcessContext
{


	/**
	 * Returns an enumeration of all the object names. The objects must have been
	 * placed before with {@link #setObject} and may be retrieved with {@link
	 * #getObject}.
	 *
	 * @return An enumeration of object names
	 */
	public Enumeration enumerate();
	/**
	 * Returns the object that activated this processor. The activator provides
	 * access to the activating environment, e.g. a Servlet or an EJB bean.
	 *
	 * @return The activator object
	 */
	public Object getActivator();
	/**
	 * Returns the locale for processing.
	 * 
	 * @return The locale for processing
	 */
	public Locale getLocale();
	/**
	 * Returns the named object. The object must have been placed before with
	 * {@link #setObject}.
	 *
	 * @param name The object name
	 * @return The object, or null
	 */
	public Object getObject( String name );
	/**
	 * Sets the locale for processing.
	 * 
	 * @param newLocale The new locale
	 */
	public void setLocale( Locale newLocale );
	/**
	 * Associates the named value with an object. The object may be retrieved
	 * later with {@link #getObject}. If <TT>object</TT> is null, the object
	 * is removed.
	 *
	 * @param name The object name
	 * @param object The object, or null
	 */
	public void setObject( String name, Object object );
}