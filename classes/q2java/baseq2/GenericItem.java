package q2java.baseq2;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Superclass for all entities lying around 
 * in the world waiting to be picked up.
 *
 * @author Barry Pederson
 */
public abstract class GenericItem extends GameObject implements DropHelper.Notify
	{
	public final static int DROP_TIMEOUT = 30; // default number of seconds before dropped items disappear
	public final static int DEFAULT_RESPAWN = 30; // most items come back after this many seconds.
	
	private   int      fPickupSoundIndex; // privately used by the touch method
	private   boolean  fIsDroppable = true;  // is this item droppable?
	protected boolean  fIsDropped;        // privately used by the pick method
	
	// helper classes that control the timed behavior of the item
	protected DropHelper  fDropHelper;		  // helper object actually animating the drop
	protected TimeoutHelper fTimeoutHelper;
	
	// fields to help prevent droppers from touching too soon
	protected Player   fDropper;          // Player dropping this item - if any.

	private final class SpawnHelper implements ServerFrameListener
		{
		private boolean fIsRespawn;
		public SpawnHelper(boolean respawn, float delay)
			{
			fIsRespawn = respawn;
			
			// schedule a one-shot call
			Game.addServerFrameListener(this, delay, -1);
			}

		public void runFrame(int phase)
			{
			// this is what we do on our one-shot
			if (fIsRespawn)
				respawn();
			else
				spawn();
			}
		}

	private final class NoTouch implements ServerFrameListener
		{
		public NoTouch()
			{
			// ask to be called back one time, one second from now
			Game.addServerFrameListener(this, 1 ,-1);
			}
			
		public void runFrame(int phase)
			{
			// forget who dropped us
			fDropper = null;
			}
		}

	private final class TimeoutHelper implements ServerFrameListener
		{
		public TimeoutHelper(float delay)
			{
			Game.addServerFrameListener(this, delay, -1);	
			}

		public void setDropTimeout(float delay) 
			{
			Game.addServerFrameListener(this, delay, -1);	
			}
			
		public void dispose()
			{
			Game.removeServerFrameListener(this);
			}
			
		public void runFrame(int phase)
			{
			dropTimeout();
			}
		}
	
/**
 * This method was created by a SmartGuide.
 */
public GenericItem() 
	{
	//Set up the sound in case we get dropped
	fPickupSoundIndex = Engine.getSoundIndex(getPickupSound());

	//precache icon
	Engine.getImageIndex(getIconName());		
	}
/**
 * A Generic Item lying around in the Quake world.
 *
 * @param spawnArgs args passed from the map.
 * @exception q2java.GameException when there are no more entities available. 
 */
public GenericItem(Element spawnArgs) throws GameException
	{
	super(spawnArgs);

	setupEntity();
	fEntity.linkEntity();

	// precache pickup sound
	fPickupSoundIndex = Engine.getSoundIndex(getPickupSound());
	
	//precache icon
	Engine.getImageIndex(getIconName());
	
	// Create a helper object to handle placing the object into the world
	new SpawnHelper(false, 0);
	}
public void dispose() 
	{
	if (fTimeoutHelper != null)
		{
		fTimeoutHelper.dispose();
		fTimeoutHelper = null;
		}

	if (fDropHelper != null)
		{
		fDropHelper.dispose();
		fDropHelper = null;
		}

	super.dispose();		
	}
/**
 * Drops the item on the ground
 *
 * @param spot point that the item reappears in the world
 * @param direction direction direction the item initially moves, may be null if you don't care.
 * @param speed speed the item initially moves
 * @param timeout number of seconds before the item should do something with itself,
 *   use zero for no action.  An item will finish falling to the ground before
 *   it considers what to do, so very small values might not work as accurately.
 */
public void drop(Point3f spot, Angle3f direction, float speed, float timeout)
	{
	Vector3f forward = new Vector3f();

	// if a direction was specified, convert it and the speed 
	// into a velocity vector
	if (direction != null)
		{
		direction.getVectors(forward, null, null);
		forward.scale(speed);
		}
		
	// setup the native entity how we like it
	setupEntity();

	// actually drop the dang thing.
	drop0(spot, forward, timeout);
	}
/**
 * Drops the item on the ground
 *
 * @param dropper the Player tossing the item, so we can avoid colliding with it.
 * @param timeout number of seconds before the item should do something with itself,
 *   use zero for no action.  An item will finish falling to the ground before
 *   it considers what to do, so very small values might not work as accurately.
 */
public void drop(Player dropper, float timeout)
	{
	// remember who dropped this, and create a helper to forget in a second or so
	fDropper = dropper;
	new NoTouch();
	
	// setup the entity how we like it
	setupEntity();

	// figure out a starting spot somewhat offset from the player
	Vector3f offset = new Vector3f(24, 0, -16);
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Angle3f direction;
	if (dropper.isDead())
		// toss in a random direction
		direction = new Angle3f(0, GameUtil.randomFloat() * 360, 0);
	else
		// toss where the player is looking
		direction = dropper.fEntity.getPlayerViewAngles();
	
	direction.getVectors(forward, right, null);
	Point3f origin = dropper.projectSource(offset, forward, right);
	
	// make sure the starting spot isn't on the other side of a wall or something
	TraceResults tr = Engine.trace(dropper.fEntity.getOrigin(), 
		fEntity.getMins(), fEntity.getMaxs(), origin, dropper.fEntity, Engine.CONTENTS_SOLID);

	// fling it at a decent speed, with an upward kick
	forward.scale(100);
	forward.z = 300;

	// actually toss the dang thing, starting at where our trace ended up
	drop0(tr.fEndPos, forward, timeout);
	}
/**
 * Really drops the item on the ground
 *
 * @param spot point that the item reappears in the world
 * @param velocity vector indicating direction and speed item initially moves
 * @param timeout number of seconds before the item should do something with itself,
 *   use zero for no action.  An item will finish falling to the ground before
 *   it considers what to do, so very small values might not work as accurately.
 */
private void drop0(Point3f spot, Vector3f velocity, float timeout)
	{
	fIsDropped = true;

	// stick it where we want it, and set it's velocity
	fEntity.setOrigin(spot);	
	fEntity.setVelocity(velocity);	
	fEntity.linkEntity();

	// start continuous calls to runFrame to animate the item falling
	fDropHelper = new DropHelper();
	fDropHelper.drop(this, this, null, 0);

	// create another helper object if necessary that will
	// cause something to happen when the item times out
	if (timeout > 0)
		setDropTimeout(timeout);
	}
/**
 * This method was created in VisualAge.
 * @param ok boolean
 */
public void dropFinished(boolean ok) 
	{
	fDropHelper = null;
	
	if (!ok)
		// fell out of the world
		dropTimeout(); // normal items will dispose, CTF Techs will reposition
	}
/**
 * What to do if we've dropped, and nobody's 
 * picked us up in the specified time.  Default is to
 * just quietly go away.  Subclasses like the CTF Tech
 * might want to override this to do something different
 * like reposition to a different spot on the map.
 */
protected void dropTimeout() 
	{
	dispose();
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public abstract String getIconName();
/**
 * Get the name that Id used for item (usually just class name minus package).
 * In some cases this will have to be overidden.
 *
 * @author Peter Donald
 */
public String getIdName()
	{
	String clsName = getClass().toString();
	int lastDot = clsName.lastIndexOf(".");
	
	if( lastDot == -1 ) 
		return clsName;
	else 
		return clsName.substring(lastDot+1);
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public abstract String getItemName();
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public abstract String getModelName();
/**
 * Get the name of the sound to play when this item is picked up.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "items/pkup.wav";
	}
/**
 * Time it takes this item to respawn after being picked up.
 * @return float - number of seconds to get respawned in.
 */
public float getRespawnTime()
	{
	return DEFAULT_RESPAWN;
	}
/**
 * Indicate if an item is willing to be dropped - default is true, 
 * but things like the hand-blaster and grapple hook can override 
 * this and return false.
 * @return boolean
 */
public boolean isDroppable() 
	{
	return fIsDroppable;
	}
/**
 * Can a given player touch this item.
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTouchable(Player p) 
	{
	// allow the touch if the player is not the one we remember as 
	// having dropped it (the item forgets after a second, at which
	// point the original dropper can pick it back up again)
	return (p != fDropper);
	}
/**
 * This method was created in VisualAge.
 */
public void respawn() 
	{
	NativeEntity ent = fEntity;
		
	if (fGroup != null)
		{
		// entity is member of a group, pick a member at random
		int selection = (GameUtil.randomInt() & 0x0fff) % fGroup.size();
		ent = ((GameObject)fGroup.elementAt(selection)).fEntity;
		}

	// make the hidden entity visible again
	ent.setSVFlags(ent.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
	ent.setSolid(NativeEntity.SOLID_TRIGGER);
	Game.getSoundSupport().fireTempEvent(ent, NativeEntity.EV_ITEM_RESPAWN);
	ent.linkEntity();
	}
/**
 * Set whether this item is droppable.
 * @param b boolean
 */
public void setDroppable(boolean b) 
	{
	fIsDroppable = b;
	}
/**
 * Cause the item to call its dropTimeout() method after a delay.
 * @param delay float
 */
public void setDropTimeout(float delay) 
	{
	if (fTimeoutHelper == null)
		fTimeoutHelper = new TimeoutHelper(delay);
	else
		fTimeoutHelper.setDropTimeout(delay);
	}
/**
 * Schedule the item to be respawned.
 * @param delay float
 */
public void setRespawn(float delay) 
	{
	// schedule a one-shot notification
	new SpawnHelper(true, delay);
	}
/**
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	// Make sure the item has a NativeEntity
	if ((fEntity == null) || fEntity.isPlayer())
		{
		try
			{
			fEntity = new NativeEntity();
			}
		catch (GameException ge)
			{
			}
		}
		
	// make sure the entity points back to this Java object
	fEntity.setReference(this);

	// set the various values and flags
	fEntity.setMins(-15, -15, -15);
	fEntity.setMaxs(15, 15, 15);
	fEntity.setRenderFX(NativeEntity.RF_GLOW); // all items glow
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fEntity.setModel(getModelName());
	fEntity.setClipmask(Engine.MASK_SHOT);
	fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
	}
/**
 * Handle the initial spawning of an item into the map, basically
 * snapping it to the ground.
 */
public void spawn() 
	{
	Point3f org = fEntity.getOrigin();
	Point3f dest = new Point3f(org);
	dest.add(new Vector3f(0, 0, -1024));			
	TraceResults tr = Engine.trace(org, fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID);
			
	if (tr.fStartSolid)
		{
//		Engine.dprint("droptofloor: %s startsolid at %s\n", ent->classname, vtos(ent->s.origin));
//		G_FreeEdict (ent);
		return;
		}

	fEntity.setOrigin(tr.fEndPos);
			
	if (fGroup != null)
		{
		// make the item disappear
		fEntity.setSolid(NativeEntity.SOLID_NOT);
		fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);

		// cause the group master to respawn right away
		if (!isGroupSlave())
			setRespawn(0);
		}
			
	fEntity.linkEntity();
	fEntity.setGroundEntity(tr.fEntity);
	}
/**
 * See if the player wants to take this item.
 * @param p a Player object
 */
public void touch(Player p) 
	{
	// check if we want this player touching us
	if (!isTouchable(p))
		return;
		
	if (fIsDropped)
		{
		// see if the player will take this particular item
		if (p.addItem(this))
			{
			// hide the item
			touchFinish(p, this);
			// make it permanently go away from the world
			fEntity.freeEntity();
			fEntity = null;
			}
		}
	else
		{
		// it's a map-spawned entity
		// see if the player will take a new item of the same class
		try
			{
			GenericItem item = (GenericItem) getClass().newInstance();
			if (p.addItem(item))
				{
				// hide the original item
				touchFinish(p, item);
				// make it reappear later
				setRespawn(getRespawnTime());
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	}
/**
 * Called if item was actually taken.
 * @param p	The Player that took this item.
 * @param itemTaken The object given to the player, may be this object or a copy.
 */
protected void touchFinish(Player p, GenericItem itemTaken) 
	{
	// cancel any timeouts or animations
	if (fDropHelper != null)
		{
		fDropHelper.dispose();
		fDropHelper = null;

		
		}
		
	// play the pickup sound
	Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_ITEM, fPickupSoundIndex, 1, NativeEntity.ATTN_NORM, 0);

	// Notify the player what they picked up
	p.notifyPickup(getItemName(), getIconName());
	
	// make the item disappear from the world
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
	fEntity.linkEntity();
	}
}