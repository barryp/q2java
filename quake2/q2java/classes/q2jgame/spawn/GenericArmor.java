
package q2jgame.spawn;

import q2java.*;

abstract class GenericArmor extends GenericItem
	{
	
public GenericArmor(String[] spawnArgs, String modelName) throws GameException
	{
	super(spawnArgs);
	setEffects(EF_ROTATE); // all armor rotates
	setModel(modelName);
	linkEntity();
	}
}