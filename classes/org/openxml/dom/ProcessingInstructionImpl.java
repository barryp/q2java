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


package org.openxml.dom;


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
 * @version $Revision: 1.2 $ $Date: 2000/04/04 23:49:23 $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 * @see org.w3c.dom.ProcessingInstruction
 * @see NodeImpl
 */
public final class ProcessingInstructionImpl
    extends NodeImpl
    implements ProcessingInstruction
{
    
    
    public short getNodeType()
    {
        return this.PROCESSING_INSTRUCTION_NODE;
    }
    
    
    public String getTarget()
    {
        // Same as calling getNodeName().
        return getNodeName();
    }

    
    public String getData()
    {
        // Same as calling getNodeValue().
        return getNodeValue();
    }
    
    
    public void setData( String data )
        throws DOMException
    {
        // Same as calling setNodeValue().
        setNodeValue( data );
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

    
    /**
     * Hidden constructor.
     * 
     * @param owner The owner of this document
     * @param target The processing instruction target
     * @param target The processing instruction data
     */
    public ProcessingInstructionImpl( DocumentImpl owner, String target, String data )
    {
        super( owner, target, data );
    }
    
    
}
