package baseq2;

import q2java.*;

/**
 * Superclass for all ammo entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericAmmo extends AmmoHolder
	{
	
public GenericAmmo(String[] spawnArgs, int count) throws GameException
	{
	super(spawnArgs);
	setAmmoCount(count);
	}
public GenericAmmo(int count) throws GameException
	{
	super();
	setAmmoCount(count);
	}
/**
 * Get the class that represents the type of ammo-box used to hold this ammo.
 * @return java.lang.Class 
 */
public Class getAmmoBoxClass()
	{
	// override with a much-simpler implementation
	return getClass();
	}
/**
 * Get the name of the type of ammo this class carries.
 * @return Name of kind of ammo.
 */
public String getAmmoName()
	{
	return getItemName();
	}
/**
 * All ammo shares the same pickup sound.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "misc/am_pkup.wav";
	}
}