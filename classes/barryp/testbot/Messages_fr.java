package barryp.testbot;

/**
 * French messages spoken by the testbot.
 * 
 * Taken from a webpage of French slang.
 */
public class Messages_fr extends java.util.ListResourceBundle 
	{
	// reactions to being damaged
	// the {0} parameter is the name of the attacker
	static final String[] gReactStrings = 
		{
		"Je casserai la gueuele à {0}",
		"{0}! Que tu es emmerdant!"		
		};
		
	static final Object[][] gContents = 
		{	
		{"react",         gReactStrings}
		};	
	
/**
 * getContents method comment.
 */
protected java.lang.Object[][] getContents() 
	{
	return gContents;
	}
}