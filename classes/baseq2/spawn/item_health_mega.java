package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_health_mega extends GenericHealth
	{
	protected Player fOwner;
	
/**
 * No-arg constructor.
 */
public item_health_mega() 
	{
	}
public item_health_mega(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get how much your health goes up when picking up this item.
 * @return int
 */
public int getHealthValue() 
	{
	return 100;
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/mega_h/tris.md2";
	}
/**
 * Get the name of the sound to play when this item is picked up.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "items/m_health.wav";
	}
/**
 * Can this item boost you past your max?
 * @return boolean
 */
public boolean isOverridingMax() 
	{
	return true;
	}
/**
 * Decrease the player's health as the item wears out
 */
public void runFrame(int phase) 
	{
	if (fOwner == null)
		{
		// one of the super classes must have called for this.
		super.runFrame(phase);
		return;
		}

	if (fOwner.getHealth() > fOwner.getHealthMax())		
		{
		fOwner.heal(-1, true);
		return;
		}
		
/*
	if (!(self->spawnflags & DROPPED_ITEM) && (deathmatch->value))
		SetRespawn (self, 20);
	else
		G_FreeEdict (self);			
*/		

	// detach from player and respawn in 20 seconds.
	fOwner = null;		
	setRespawn(20);		
	}
/**
 * React to being touched by boosting player health 
 * and then slowly lowering it down to the maxLevel.
 * @param mob q2jgame.GenericMobile
 */
public void touch(Player p) 
	{	
	super.touch(p);
	
	fOwner = p;
	Game.addFrameListener(this, 5, 1); 
	// wait 5 seconds, then start decreasing health at one-second intervals
	}
}