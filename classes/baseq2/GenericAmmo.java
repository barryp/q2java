
package baseq2;

import q2java.*;

/**
 * Superclass for all ammo entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericAmmo extends GenericItem
	{
	String fAmmoType;
	int fCount;
	
public GenericAmmo(String[] spawnArgs, String ammoType, int count, String modelName) throws GameException
	{
	super(spawnArgs, "misc/am_pkup.wav");
	fEntity.setModel(modelName);
	fAmmoType = ammoType;
	fCount = count;
	fEntity.linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void touch(Player p) 
	{	
	if (p.addAmmo(fAmmoType, fCount))
		{		
		super.touch(p);

		// bring the ammo back in 30 seconds
		setRespawn(30);	
		}
	}
}