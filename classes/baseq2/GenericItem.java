
package baseq2;


import javax.vecmath.*;

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
	
	protected int fItemState;
	
	protected final static int STATE_DROPPED = 0;
	protected final static int STATE_NORMAL = 1;
	

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

	fEntity.setMins(-15, -15, -15);
	fEntity.setMaxs(15, 15, 15);
	
	fEntity.setRenderFX(NativeEntity.RF_GLOW); // all items glow
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fPickupSoundIndex = Engine.getSoundIndex(pickupSound);
	
	// schedule a one-shot runFrame() call so we can 
	// drop to the floor
	fItemState = STATE_DROPPED;
	Game.addFrameListener(this, 0, -1);
	}
/**
 * Make the item visible again.
 */
public void runFrame(int phase) 
	{
	switch (fItemState)
		{
		case STATE_DROPPED:
			fItemState = STATE_NORMAL;
			Point3f org = fEntity.getOrigin();
			Point3f dest = new Point3f();
			dest.add(org, new Vector3f(0, 0, -1024));			
			TraceResults tr = Engine.trace(org, fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID);
			
			if (tr.fStartSolid)
				{
//				Engine.dprint("droptofloor: %s startsolid at %s\n", ent->classname, vtos(ent->s.origin));
//				G_FreeEdict (ent);
				return;
				}

			fEntity.setOrigin(tr.fEndPos);
			fEntity.linkEntity();
			fEntity.setGroundEntity(tr.fEntity);
			break;
			
		case STATE_NORMAL:
	fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fEntity.setEvent(NativeEntity.EV_ITEM_RESPAWN);
	fEntity.linkEntity();
			break;
	}
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
 * When a Player touches an item, this method is called.
 * @param p		The Player that touched this item.
 */
public void touch(Player p) 
	{
	// play the pickup sound
	fEntity.sound(NativeEntity.CHAN_ITEM, fPickupSoundIndex, 1, NativeEntity.ATTN_NORM, 0);

	// flash the Player's screen
	p.setFrameAlpha(0.25f);

	// make the item disappear
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);
	fEntity.linkEntity();
	}
}