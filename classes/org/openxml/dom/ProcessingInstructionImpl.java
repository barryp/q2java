package org.openxml.dom;

/**
 * org/openxml/dom/ProcessingInstructionImpl.java
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


import org.w3c.dom.*;


/**
 * Implements a processing instruction. The target and data of the processing
 * instruction are mapped into the name and value of the node.
 * <P>
 * Notes:
 * <OL>
 * <LI>Node type is {@link org.w3c.dom.Node#PROCESSING_INSTRUCTION_NODE}
 * <LI>Node does not support childern
 * </OL>
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2000/01/02 02:32:58 $
 * @author <a href="mailto:arkin@trendline.co.il">Assaf Arkin</a>
 * @see org.w3c.dom.ProcessingInstruction
 * @see NodeImpl
 */
public final class ProcessingInstructionImpl
	extends NodeImpl
	implements ProcessingInstruction
{
	
	
	/**
	 * Hidden constructor.
	 * 
	 * @param owner The owner of this document
	 * @param target The processing instruction target
	 * @param target The processing instruction data
	 */
	public ProcessingInstructionImpl( DocumentImpl owner, String target, String data )
	{
		super( owner, target, data, true );
	}
	public final Object clone()
	{
		ProcessingInstructionImpl   clone;
		
		clone = new ProcessingInstructionImpl( _ownerDocument, getNodeName(), getNodeValue() );
		cloneInto( clone, true );
		return clone;
	}
	public final Node cloneNode( boolean deep )
	{
		ProcessingInstructionImpl   clone;
			
		clone = new ProcessingInstructionImpl( _ownerDocument, getNodeName(), getNodeValue() );
		cloneInto( clone, deep );
		return clone;
	}
	public String getData()
	{
		// Same as calling getNodeValue().
		return getNodeValue();
	}
	public short getNodeType()
	{
		return this.PROCESSING_INSTRUCTION_NODE;
	}
	public String getTarget()
	{
		// Same as calling getNodeName().
		return getNodeName();
	}
	public void setData( String data )
		throws DOMException
	{
		// Same as calling setNodeValue().
		setNodeValue( data );
	}
	protected final boolean supportsChildern()
	{
		return false;
	}
	public String toString()
	{
		String    target;
		String    data;
		
		target = getTarget();
		if ( target.length() > 32 )
			target = target.substring( 0, 32 ) + "..";
		data = getData();
		if ( data.length() > 32 )
			data = data.substring( 0, 32 ) + "..";
		return "PI node: [" + target + "] [" + data + "]";
	}
}