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


import java.io.*;
import org.w3c.dom.*;


/**
 * Interface for a document publisher. Publishers are obtained from
 * {@link PublisherFactory}. They are created with a specific output
 * target and format and can only be used with that combination.
 * <P>
 * When called with a document, {@link #publish(Document)} will publish
 * the entire document contents including document type information.
 * When called with a node, {@link #publish(Node)} will publish the
 * node contents and its childs, and can be used to publish portions of
 * a larger document.
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:33:02 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see PublisherFactory
 */
public interface Publisher
{


	/**
	 * Called to close the output target. It is recommended to call this
	 * method after publishing concludes, so as not to consume open
	 * resources held by the target. This method has no affect on
	 * garbage collection of publisher objects.
	 */
	public void close();
	/**
	 * Called to publish the entire document including the document
	 * declaration and DTD.
	 * 
	 * @param doc The document to publish
	 * @throws IOException An exception occured while publishing the
	 *  document
	 */
	public void publish( Document doc )
		throws IOException;
	/**
	 * Called to publish the node and its children. When called with
	 * a document, this method will not print the document declaration
	 * or DTD.
	 * 
	 * @param node The node to publish
	 * @throws IOException An exception occured while publishing the
	 *  node
	 */
	public void publish( Node node )
		throws IOException;
}