package org.openxml.beans;

import java.util.*;


public interface ProcessListener
	extends EventListener 
{
	
	
	public void process( ProcessEvent event );
}