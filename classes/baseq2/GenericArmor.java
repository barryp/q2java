
package baseq2;

import q2java.*;
import q2jgame.*;

/**
 * Superclass for all armor entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericArmor extends GenericItem
	{
	protected int fCount;
	protected int fMaxCount;
	protected float fProtection;
	protected float fEnergyProtection;
	protected int fIcon;
	
public GenericArmor(String[] spawnArgs, String modelName, String pickupSound, String icon, int count, int maxCount, float protection, float energyProtection) throws GameException
	{
	super(spawnArgs, pickupSound);
	
	if (GameModule.isDMFlagSet(GameModule.DF_NO_ARMOR))
		{
		dispose();
		throw new InhibitedException("armor items inhibited");
		}	
	
	fEntity.setEffects(NativeEntity.EF_ROTATE); // all armor rotates
	fEntity.setModel(modelName);
	fEntity.linkEntity();
	
	fCount = count;
	fMaxCount = maxCount;
	fProtection = protection;
	fEnergyProtection = energyProtection;
	fIcon = Engine.getImageIndex(icon);
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