package barryp.xmllink;

import org.xml.sax.*;

import q2java.*;
import q2java.core.*;

/**
 * A chat queued for later processing by the main thread.
 */
class xml_removelocale implements XMLJob
	{
	private String fLocale;
	
/**
 * What to do when run by the main thread.
 */
public void run(GameModule parent) 
	{
	if (fLocale != null)
		Game.removeLocaleListener(parent, fLocale);
	}
/**
 * Configure a Chat Job.
 * @param value java.lang.String
 */
public void setParams(AttributeList attrs, String value) 
	{
	fLocale = attrs.getValue("locale");
	}
}