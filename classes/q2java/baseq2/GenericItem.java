package q2java.baseq2;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;

/**
 * Superclass for all entities lying around 
 * in the world waiting to be picked up.
 *
 * @author Barry Pederson
 */
public abstract class GenericItem extends GameObject implements FrameListener
	{
	public final static int DROP_TIMEOUT = 30; // default number of seconds before dropped items disappear
	public final static int DEFAULT_RESPAWN = 30; // most items come back after this many seconds.
	
	private   int      fPickupSoundIndex; // privately used by the touch method
	protected boolean  fIsDropped;        // privately used by the pick method
	protected float    fDropTimeout;      // game time that a dropped item should reconsider it's fate

	// fields to help prevent droppers from touching too soon
	protected Player   fDropper;          // Player dropping this item - if any.
	protected float    fDropTime;         // time the item was dropped.

	protected int fItemState;	
	protected final static int STATE_SPAWN   = 0; // item was just spawned, needs to snap to ground
	protected final static int STATE_NORMAL  = 1; // just sitting there, waiting to be touched
	protected final static int STATE_FALLING = 2; // was dropped, is falling to ground
	protected final static int STATE_AVOID_TOUCH = 3;  // avoid touching the dropper for a bit
	protected final static int STATE_DROP_TIMEOUT = 4; // waiting to go POOF!
	
	static final float STOP_EPSILON = 0.1f;
	
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
public GenericItem(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	setupEntity();
	fEntity.linkEntity();

	// precache pickup sound
	fPickupSoundIndex = Engine.getSoundIndex(getPickupSound());
	
	//precache icon
	Engine.getImageIndex(getIconName());
	
	// schedule a one-shot runFrame() call so we can 
	// drop to the floor
	fItemState = STATE_SPAWN;
	Game.addFrameListener(this, 0, -1);
	}
/** 
 * limit velocity components
 */
 
protected void checkVelocity()
	{
	Vector3f v = fEntity.getVelocity();
	float maxv = BaseQ2.gMaxVelocity;
	
	if (v.x >  maxv)
		v.x =  maxv;
	else if (v.x < -maxv)
		v.x = -maxv;

	if (v.y >  maxv)
		v.y =  maxv;
	else if (v.y < -maxv)
		v.y = -maxv;

	if (v.z >  maxv)
		v.z =  maxv;
	else if (v.z < -maxv)
		v.z = -maxv;

	fEntity.setVelocity(v);
	}
/**
 * Alter the item's velocity based on something it's hit
 *
 * @param normal vector normal of the surface we hit?
 * @param overbounce ???
 * @return integer flags indicating what we hit?
 */ 
protected int clipVelocity(Vector3f normal, float overbounce)
	{
	float	 backoff;
	int		 i, blocked;
	Vector3f v;
	
	blocked = 0;
	if (normal.z > 0)
		blocked |= 1;		// floor
	if (normal.z == 0)
		blocked |= 2;		// step
	
	v  = fEntity.getVelocity();
	backoff = v.dot(normal) * overbounce;

	v.x -= normal.x * backoff;
	v.y -= normal.y * backoff;
	v.z -= normal.z * backoff;

	if (v.x > -STOP_EPSILON && v.x < STOP_EPSILON)
		v.x = 0;
	if (v.y > -STOP_EPSILON && v.y < STOP_EPSILON)
		v.y = 0;
	if (v.z > -STOP_EPSILON && v.z < STOP_EPSILON)
		v.z = 0;

	fEntity.setVelocity(v);

	return blocked;
	}
public void dispose() 
	{
	super.dispose();
	Game.removeFrameListener(this);
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
	// remember who dropped this
	fDropper = dropper;

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
	fDropTime = Game.getGameTime();

	if (timeout > 0)
		fDropTimeout = fDropTime + timeout;
	else
		fDropTimeout = 0;		

	// stick it where we want it, and set it's velocity
	fEntity.setOrigin(spot);	
	fEntity.setVelocity(velocity);	
	fEntity.linkEntity();

	// start continuous calls to runFrame to animate the item falling
	fItemState = STATE_FALLING;
	Game.addFrameListener(this, 0, 0);	
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
	return true;
	}
/**
 * Can a given player touch this item.
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTouchable(Player p) 
	{
	// allow the touch if the player is not the one who
	// dropped it, or it's been more than 1 second since it was dropped
	return ((p != fDropper) || (Game.getGameTime() > (fDropTime + 1)));
	}
/**
 * Handle dropping items to the floor, respawning, destroying and bouncing
 */
public void runFrame(int phase) 
	{
	// only deal with middle-phase calls.
	if (phase != Game.FRAME_MIDDLE)
		return;
		
	TraceResults tr;
	
	switch (fItemState)
		{
		case STATE_SPAWN:  // was just spawned, needs to snap to ground
			fItemState = STATE_NORMAL;
			Point3f org = fEntity.getOrigin();
			Point3f dest = new Point3f(org);
			dest.add(new Vector3f(0, 0, -1024));			
			tr = Engine.trace(org, fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID);
			
			if (tr.fStartSolid)
				{
//				Engine.dprint("droptofloor: %s startsolid at %s\n", ent->classname, vtos(ent->s.origin));
//				G_FreeEdict (ent);
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
			break;
			
		case STATE_DROP_TIMEOUT: //do something with ourselves
			dropTimeout();
			break;
			
		case STATE_NORMAL: // handle respawn
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
			ent.setEvent(NativeEntity.EV_ITEM_RESPAWN);
			ent.linkEntity();
			break;

		case STATE_AVOID_TOUCH:
			// forget about who dropped us
			fDropper = null;
			
			// select a new state, based on whether
			// there's a timeout or not.
			if (fDropTimeout > 0)
				{
				fItemState = STATE_DROP_TIMEOUT;
				// schedule a one-shot call for when we want to go poof!
				Game.addFrameListener(this, fDropTimeout - Game.getGameTime(), -1);
				}
			else
				{
				// sit around indefinitely
				fItemState = STATE_NORMAL;
				Game.removeFrameListener(this); // shut off frame events
				}
			break;		
			
			
		case STATE_FALLING: // was dropped, is falling to ground			
			checkVelocity();
			applyGravity();

			tr = fEntity.traceMove(Engine.MASK_SOLID, 1.0F);
			if (tr.fFraction == 1)
				return;		// moved the entire distance without hitting anything

			// 'scuse me while I kiss the sky...
			if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
				{
				// fell out of the world
				dropTimeout(); // normal items will dispose, CTF Techs will reposition
				return;
				}

//			if (tr.fFraction < 0.01f)
			if ( (clipVelocity(tr.fPlaneNormal, 1f) & 1) != 0 )
				{
				// came to a stop

				// link it to whatever it came to a rest on, especially 
				// important if it happens to be something like an 
				// elevator, so that it rises and falls properly
				fEntity.setGroundEntity(tr.fEntity);

				// avoid touching the dropper for at least a second after dropping				
				Game.addFrameListener(this, (fDropTime + 1) - Game.getGameTime(), -1);
				fItemState = STATE_AVOID_TOUCH;
				return;		
				}
			break;
		}
	}
/**
 * Cause the item to timeout after a given delay. Used to simulate
 * a timeout for an item that wasn't actually dropped, such as a CTF
 * tech powerup that's created at the beginning of a level.
 *
 * @param delay number of seconds before the item should timeout
 */
protected void setDropTimeout(float delay) 
	{
	fItemState = STATE_DROP_TIMEOUT;
	Game.addFrameListener(this, delay, -1);
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
	Game.removeFrameListener(this);
	fItemState = STATE_NORMAL;
		
	// play the pickup sound
	fEntity.sound(NativeEntity.CHAN_ITEM, fPickupSoundIndex, 1, NativeEntity.ATTN_NORM, 0);

	// Notify the player what they picked up
	p.notifyPickup(getItemName(), getIconName());
	
	// make the item disappear from the world
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
	fEntity.linkEntity();
	}
}