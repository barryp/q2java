
package baseq2;

import q2java.*;
import q2jgame.*;

/**
 * Superclass for all health entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericHealth extends GenericItem
	{
	protected int fHealthValue;
	protected boolean fOverrideMax;
	
public GenericHealth(String[] spawnArgs, String modelName, String pickupSound, int healthValue, boolean overrideMax) throws GameException
	{
	super(spawnArgs, pickupSound);
	
	if (GameModule.isDMFlagSet(GameModule.DF_NO_HEALTH))
		{
		dispose();
		throw new InhibitedException("health items inhibited");
		}
	
	fHealthValue = healthValue;
	fOverrideMax = overrideMax;
	fEntity.setModel(modelName);
	fEntity.linkEntity();		
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