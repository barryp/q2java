
package baseq2;

import q2java.*;
import q2jgame.*;

/**
 * Superclass for all entities lying around 
 * in the world waiting to be picked up.
 *
 * @author Barry Pederson
 */
public abstract class GenericItem extends GameObject implements FrameListener
	{
	protected int fPickupSoundIndex;
	protected float fRespawnTime;
	
/**
 * This method was created by a SmartGuide.
 */
public GenericItem() 
	{
	}
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
	fEntity.setRenderFX(NativeEntity.RF_GLOW); // all items glow
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fPickupSoundIndex = Engine.getSoundIndex(pickupSound);
	}
/**
 * Make the item visible again.
 */
public void runFrame(int phase) 
	{
	fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fEntity.setEvent(NativeEntity.EV_ITEM_RESPAWN);
	fEntity.linkEntity();
	}
/**
 * Schedule the item to be respawned.
 * @param delay float
 */
public void setRespawn(float delay) 
	{
	// schedule a one-shot notification
	Game.addFrameListener(this, delay, -1);
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(Player p) 
	{
	// play the pickup sound
	fEntity.sound(NativeEntity.CHAN_ITEM, fPickupSoundIndex, 1, NativeEntity.ATTN_NORM, 0);

	// make the item disappear
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);
	fEntity.linkEntity();
	}
}