
package q2jgame;

import q2java.*;

public class GenericHealth extends GenericItem
	{
	public int fHealthValue;
	
public GenericHealth(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(GenericCharacter mob) 
	{
	super.touch(mob);
	
	mob.heal(fHealthValue);
	freeEntity();
	}
}