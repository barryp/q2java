
package q2jgame;

import q2java.*;
/**
 * Superclass for all health entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericHealth extends GenericItem
	{
	private int fHealthValue;
	private boolean fOverrideMax;
	
public GenericHealth(String[] spawnArgs, String modelName, String pickupSound, int healthValue, boolean overrideMax) throws GameException
	{
	super(spawnArgs, pickupSound);
	
	if (Game.isDMFlagSet(Game.DF_NO_HEALTH))
		{
		freeEntity();
		throw new InhibitedException("health items inhibited");
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