package barryp.testbot;

/**
 * Messages spoken by the testbot.
 * 
 */
public class Messages extends java.util.ListResourceBundle 
	{
	// reactions to being damaged
	// the {0} parameter is the name of the attacker
	static final String[] gReactStrings = 
		{
		"You better watch that shit {0}!",
		"{0} you asshole...you''re pissing me off!",
		"Your ass is grass {0}, and I''m the lawnmower!",
		"Watch out everybody, {0} finally found the safety on his weapon",
		"Knock it off {0}, or I''m gonna come over there and kick your ass"
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