package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

public class item_health_mega extends GenericHealth
  implements ServerFrameListener, PlayerStateListener
	{
	protected Player fOwner;
	
/**
 * No-arg constructor.
 */
public item_health_mega() 
	{
	}
public item_health_mega(Element spawnArgs) throws GameException
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
 * Watch for Player disconnect or level change.
 */
public void playerStateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())	
		{
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_INVALID:
		case PlayerStateEvent.STATE_SUSPENDEDSTART:
			fOwner.removePlayerStateListener(this);
			fOwner = null;
		
			Game.removeServerFrameListener(this);
			setRespawn(20);
			break;
		}	
	}
/**
 * Decrease the player's health as the item wears out
 */
public void runFrame(int phase) 
	{
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
	fOwner.removePlayerStateListener(this);
	fOwner = null;
	
	Game.removeServerFrameListener(this);
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
		
	// wait 5 seconds, then start decreasing health at one-second intervals
	Game.addServerFrameListener(this, 5, 1);
	
	fOwner = p;
	fOwner.addPlayerStateListener(this);
	}
}