
package q2jgame;

import q2java.*;
/**
 * Superclass for all armor entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericArmor extends GenericItem
	{
	private int fCount;
	private int fMaxCount;
	private float fProtection;
	private float fEnergyProtection;
	private int fIcon;
	
public GenericArmor(String[] spawnArgs, String modelName, String pickupSound, String icon, int count, int maxCount, float protection, float energyProtection) throws GameException
	{
	super(spawnArgs, pickupSound);
	
	if (Game.isDMFlagSet(Game.DF_NO_ARMOR))
		{
		freeEntity();
		throw new InhibitedException("armor items inhibited");
		}	
	
	setEffects(EF_ROTATE); // all armor rotates
	setModel(modelName);
	linkEntity();
	
	fCount = count;
	fMaxCount = maxCount;
	fProtection = protection;
	fEnergyProtection = energyProtection;
	fIcon = Engine.imageIndex(icon);
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericCharacter
 */
public void touch(Player p) 
	{
	if (p.addArmor(fCount, fMaxCount, fProtection, fEnergyProtection, fIcon))
		{
		super.touch(p);	
		// bring the weapon back in 30 seconds
		setRespawn(30);	
		}	
	}

}