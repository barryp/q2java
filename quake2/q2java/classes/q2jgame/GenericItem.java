
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
 * A Generic Item lying around in the Quake world.
 *
 * @param spawnArgs args passed from the map.
 * @exception q2java.GameException when there are no more entities available. 
 */
public GenericItem (String[] spawnArgs) throws GameException
	{
	this(spawnArgs, "items/pkup.wav");
	}
/**
 * A Generic Item lying around in the Quake world.
 *
 * @param spawnArgs args passed from the map.
 * @param pickupSound sound to play when the item is picked up.
 * @exception q2java.GameException when there are no more entities available. 
 */
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