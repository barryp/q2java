package baseq2;

import q2java.*;

/**
 * Superclass for all ammo entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericAmmo extends GenericItem
	{
	int fCount;
	
public GenericAmmo(String[] spawnArgs, int count) throws GameException
	{
	super(spawnArgs);
	fEntity.setModel(getModelName());
	fCount = count;
	fEntity.linkEntity();
	}
/**
 * All ammo shares the same pickup sound.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "misc/am_pkup.wav";
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void touch(Player p) 
	{	
	if (p.addAmmo(getItemName(), fCount))
		{		
		super.touch(p);

		// bring the ammo back in 30 seconds
		setRespawn(30);	
		}
	}
}