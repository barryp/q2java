package q2java.baseq2.spawn;

/**
 * Trigger that can be activated only once when a 
 * a player touches its invisible field.
 *
 * @author Barry Pederson
 */ 
public class trigger_once extends trigger_multiple 
	{
	
/**
 * trigger_once constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public trigger_once(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs, false);
	}
}