package barryp.testbot;

/**
 * Australian messages spoken by the testbot.
 *
 *Don't blame me..I got most of them off a webpage.
 */
public class Messages_en_AU extends java.util.ListResourceBundle 
	{
	// reactions to being damaged
	// the {0} parameter is the name of the attacker
	static final String[] gReactStrings = 
		{
		"{0} - your brains are as scarce as rocking-horse manure.",
		"Jeez, {0} is a few stubbies short of a six-pack.",
		"Allright, I''m as mad as a cut snake {0}, watch it!",
		"Feh..{0} couldn''t organise a piss-up in a brewery.",
		"{0} wouldn''t know his arse from his elbow.",
		"You''re rougher than a pig''s breakfast {0}, but I like that!"
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