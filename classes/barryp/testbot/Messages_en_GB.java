package barryp.testbot;

/**
 * British messages spoken by the testbot.
 * 
 * Mostly taken from a humorous English->American dictionary webpage.
 */
public class Messages_en_GB extends java.util.ListResourceBundle 
	{
	// reactions to being damaged
	// the {0} parameter is the name of the attacker
	static final String[] gReactStrings = 
		{
		"You''re a tosser {0}, sod off!",
		"I say {0}! that's terribly rude...I must object!",
		"Right! you don''t want to mess with me {0}, I''m hard!"
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