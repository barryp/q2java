
package baseq2;

/**
 * Simple class to hold a welcome message to show 
 * new users as they connect.  It should be easy to 
 * make changes to this class and recompile to change the
 * message.  Perhaps a Server command could also be added
 * to change it at runtime.
 * 
 */
public class WelcomeMessage 
	{
	private final static String gMsg = 
		  "Welcome to the Q2Java Sample Game v0.3\n\n"
		+ "The main change in this version has\n"
		+ "been a switch the the javax.vecmath\n"
		+ "library, and a more event-driven\n"
		+ "gameflow where objects have to register\n"
		+ "to be notified of server frames rather\n"
		+ "than having the game just blindly call\n"
		+ "every entity like the C game does.";
	
/**
 * Return the message to display to new players.
 * This particular implementation is pretty simple, but
 * it could be easily replaced with something more dynamic -
 * perhaps reading something from a file, or reporting
 * the current status of the game.
 *
 * @return java.lang.String
 */
public static String getMessage() 
	{
	return gMsg;
	}
}