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
//	protected int fIcon;
	
public GenericArmor(String[] spawnArgs, int count, int maxCount, float protection, float energyProtection) throws GameException
	{
	super(spawnArgs);
	
	if (GameModule.isDMFlagSet(GameModule.DF_NO_ARMOR))
		{
		dispose();
		throw new InhibitedException("armor items inhibited");
		}	
	
	fEntity.setEffects(NativeEntity.EF_ROTATE); // all armor rotates
	fEntity.setModel(getModelName());
	fEntity.linkEntity();
	
	fCount = count;
	fMaxCount = maxCount;
	fProtection = protection;
	fEnergyProtection = energyProtection;
	}
/**
 * Heavier armor has a different sound from light armor.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	if (fEnergyProtection > 1)
		return "misc/ar1_pkup.wav";
	else
		return "misc/ar2_pkup.wav";
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericCharacter
 */
public void touch(Player p) 
	{
	if (p.addArmor(fCount, fMaxCount, fProtection, fEnergyProtection, Engine.getImageIndex(getIconName())))
		{
		super.touch(p);	
		// bring the weapon back in 30 seconds
		setRespawn(30);	
		}	
	}
}