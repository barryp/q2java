package org.openxml.beans;

import java.util.*;
import org.w3c.dom.*;


public class ProcessEvent
	extends EventObject
{
	

	private Document    _document;
	
	
	public ProcessEvent( Object source, Document document )
	{
		super( source );
		_document = document;
	}
	public Document getDocument()
	{
		return _document;
	}
}