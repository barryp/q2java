
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

abstract class GenericHealth extends GenericItem
	{
	private int fHealthValue;
	private boolean fOverrideMax;
	
public GenericHealth(String[] spawnArgs, String modelName, String pickupSound, int healthValue, boolean overrideMax) throws GameException
	{
	super(spawnArgs, pickupSound);
	
	if (Game.isDMFlagSet(Game.DF_NO_HEALTH))
		{
		freeEntity();
		throw new GameException("health items inhibited");
		}
	
	fHealthValue = healthValue;
	fOverrideMax = overrideMax;
	setModel(modelName);
	linkEntity();		
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(Player p) 
	{
	if (p.heal(fHealthValue, fOverrideMax))
		{
		super.touch(p);
	
		// bring the health back in 30 seconds
		setRespawn(30);
		}
	}
}