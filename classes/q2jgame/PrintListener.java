package q2jgame;

/**
 * Interface for objects that want to be notified of print events.
 * 
 */
public interface PrintListener 
	{
	
/**
 * Print a broadcast message.
 * @param flags int
 * @param msg java.lang.String
 */
public void bprint(int flags, String msg);
/**
 * Print a message that came from outside the game (currently just from the JavaVM).
 * @param s java.lang.String
 */
public void consoleOutput(String s);
/**
 * Print a debugging message.
 * @param msg java.lang.String
 */
public void dprint(String msg);
}