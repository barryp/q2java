package q2java.core;

/**
 * An Exception class that indicates a particular entity
 * shouldn't appear on the map (because of deathmatch flags or
 * skill level usually)
 *
 * @author Barry Pederson
 */
public class InhibitedException extends q2java.GameException 
	{
	
/**
 * InhibitedException constructor comment.
 * @param msg java.lang.String
 */
public InhibitedException(String msg) {
	super(msg);
}
}