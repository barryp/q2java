
package q2jgame;

import q2java.*;

public class GenericArmor extends GenericItem
	{
	
public GenericArmor(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setEffects(EF_ROTATE); // all armor rotates
	}
}