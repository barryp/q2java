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
	
/**
 * No-arg constructor.
 */
public GenericArmor() 
	{
	}
public GenericArmor(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	if (GameModule.isDMFlagSet(GameModule.DF_NO_ARMOR))
		{
		dispose();
		throw new InhibitedException("armor items inhibited");
		}		
	}
/**
 * Get the max value of this armor.
 * @return int
 */
public abstract int getArmorMaxValue();
/**
 * Get the value of this armor.
 * @return int
 */
public abstract int getArmorValue();
/**
 * Get the strength of this armor against energy weapons.
 * @return float
 */
public abstract float getEnergyProtectionFactor();
/**
 * Heavier armor has a different sound from light armor.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	if (getEnergyProtectionFactor() > 1)
		return "misc/ar1_pkup.wav";
	else
		return "misc/ar2_pkup.wav";
	}
/**
 * Get the strength of this armor.
 * @return float
 */
public abstract float getProtectionFactor();
/**
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE); // all armor rotates
	}
}