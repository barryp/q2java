
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

abstract class GenericHealth extends GenericItem
	{
	private int fHealthValue;
	
public GenericHealth(String[] spawnArgs, String modelName, String pickupSound, int healthValue) throws GameException
	{
	super(spawnArgs, pickupSound);
	fHealthValue = healthValue;
	setModel(modelName);
	linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(GenericCharacter mob) 
	{
	super.touch(mob);
	
	mob.heal(fHealthValue);
	}
}