
package q2jgame;

import q2java.*;

public class GenericWeapon extends GenericItem
	{
	
public GenericWeapon(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setEffects(EF_ROTATE); // all weapons rotate
	}
}