package q2java.baseq2;

import q2java.core.*;

/**
 * Subclass of GenericItem that holds ammo, 
 * superclass for GenericAmmo and GenericWeapon.
 */
public abstract class AmmoHolder extends GenericItem 
	{
	private int fAmmoCount;	
	
/**
 * no-arg constructor.
 */
public AmmoHolder() 
	{
	super();
	}
/**
 * Constructor for map entities.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public AmmoHolder(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	}
/**
 * Get the class that represents the type of ammo-box 
 * this weapon uses.
 * Default implementation makes a guess based on the name of the ammo.
 * @return java.lang.Class may be null a weapon doesn't use ammo.
 */
public Class getAmmoBoxClass()
	{
	String ammo = getAmmoName();
	if (ammo == null)
		return null;

	try
		{
		return Game.lookupClass(".spawn.ammo_" + ammo);
		}
	catch (ClassNotFoundException cnfe)
		{
		cnfe.printStackTrace();
		return null;
		}
	}
/**
 * Get how much ammo this weapon is carrying with it.
 * @return int.
 */
public int getAmmoCount() 
	{
	return fAmmoCount;
	}
/**
 * Get the name of the type of ammo this class carries/uses.
 * @return Name of kind of ammo, may be null if a weapon
 *   weapon doesn't use ammo.
 */
public abstract String getAmmoName(); 
/**
 * Set how much ammo this weapon is carrying with it.
 */
public void setAmmoCount(int value) 
	{
	fAmmoCount = value;
	}
}