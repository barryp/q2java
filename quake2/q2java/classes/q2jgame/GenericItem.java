
package q2jgame;

import q2java.*;
/**
 * Superclass for all entities lying around 
 * in the world waiting to be picked up.
 *
 * @author Barry Pederson
 */
public abstract class GenericItem extends GameEntity
	{
	private int fPickupSoundIndex;
	private float fRespawnTime;
	
/**
 * This method was created by a SmartGuide.
 */
public GenericItem (String[] spawnArgs) throws GameException
	{
	this(spawnArgs, "items/pkup.wav");
	}
public GenericItem(String[] spawnArgs, String pickupSound) throws GameException
	{
	super(spawnArgs);
	setRenderFX(RF_GLOW); // all items glow
	setSolid(SOLID_TRIGGER);
	fPickupSoundIndex = Engine.soundIndex(pickupSound);
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	if ((fRespawnTime > 0) && (Game.gGameTime > fRespawnTime))
		{
		setSVFlags(getSVFlags() & ~SVF_NOCLIENT);
		setSolid(SOLID_TRIGGER);
		setEvent(EV_ITEM_RESPAWN);
		linkEntity();
		fRespawnTime = 0;
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param delay float
 */
public void setRespawn(float delay) 
	{
	fRespawnTime = (float)(Game.gGameTime + delay);
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(Player p) 
	{
	// play the pickup sound
	sound(CHAN_ITEM, fPickupSoundIndex, 1, ATTN_NORM, 0);

	// make the item disappear
	setSolid(SOLID_NOT);
	setSVFlags(SVF_NOCLIENT);
	linkEntity();
	}
}