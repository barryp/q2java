package barryp.xmllink;

import org.xml.sax.*;

import q2java.*;
import q2java.core.*;

/**
 * A chat queued for later processing by the main thread.
 */
class xml_chat implements XMLJob
	{
	private String fValue;
	
/**
 * What to do when run by the main thread.
 */
public void run(GameModule parent) 
	{
	Game.bprint(Engine.PRINT_CHAT, fValue + "\n");	
	}
/**
 * Configure a Chat Job.
 * @param value java.lang.String
 */
public void setParams(AttributeList attrs, String value) 
	{
	fValue = value;
	}
}