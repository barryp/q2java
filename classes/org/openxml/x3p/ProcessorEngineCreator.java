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


import org.w3c.dom.*;


/**
 * Interface for creating a new processor engine. Engines are not registered
 * with processors directly, only through creators. This approach allows the
 * processor to create the same engine over and over and the engine to be of
 * multiple or single instance.
 * <P>
 * The creator is called by a {@link Processor} on behalf of a process context
 * to create a registered engine. The creator can decide which engine to create
 * based on the context information, and should initialize the engine as necessary.
 * The creator may create a new instance for the engine, or continually return the
 * same engine instance.
 *
 *
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see Processor
 * @see ProcessorEngine
 */
public interface ProcessorEngineCreator
{


	/**
	 * Creates and returns a new processor engine of the specified type.
	 * The processor context may be used to initialize the engine. If the
	 * engine cannot be instantiated, this method may return null.
	 *
	 * @param ctx The process context
	 * @return New engine or null
	 */
	public ProcessorEngine createEngine( ProcessContext ctx );
}