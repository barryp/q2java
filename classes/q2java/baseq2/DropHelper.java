package q2java.baseq2;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Helper class to cause other objects to drop the the ground.
 * Probably should have called it "Tosser", but the english guys would
 * probably get too good a laugh out of that.
 *
 * @author Barry Pederson
 */
public class DropHelper implements ServerFrameListener
	{
	protected NativeEntity fEntity;
	protected Notify fOwner;
	protected boolean fInWater;
	
	protected Vector3f fGravity = new Vector3f(0, 0, -1); // objects tend to fall down
	
	static final float STOP_EPSILON = 0.1f;

	// interface for objects that want to be called back when the Dropper
	// is finished moving the entity
	public interface Notify
		{
		public void dropFinished(boolean ok);
		}
	
/**
 * Alter an object's entity's velocity based on gravity
 */ 
protected void applyGravity()
	{
	Vector3f v = fEntity.getVelocity();
	float f = BaseQ2.gGravity * Engine.SECONDS_PER_FRAME;

	// simulate the drag of water
	if (fInWater)
		f *= 0.3F;
		
	// we could have used the Tuple3f scaleAdd() here too.
	v.x += fGravity.x * f;
	v.y += fGravity.y * f;
	v.z += fGravity.z * f;
	
	fEntity.setVelocity(v);
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
/**
 * Shut down the dropper.
 */
public void dispose() 
	{
	Game.removeServerFrameListener(this);
	fOwner = null;
	fEntity = null;
	}
/**
 * Drops the item on the ground
 *
 * @param owner Droppable object (if any) that should be called back when the entity
 *   is finished dropping (may be null if you don't care to be called back).
 * @param obj A GameObject we're dropping - drop will grab the NativeEntity and current Gravity from it.
 * @param direction direction direction the item initially moves, may be null if you don't care.
 * @param speed speed the item initially moves if a direction is specified
 */
public void drop(Notify owner, GameObject obj, Angle3f direction, float speed)
	{	
	fGravity.set(obj.getGravity());
	
	drop(owner, obj.fEntity, direction, speed);
	}
/**
 * Drops the item on the ground
 *
 * @param owner Droppable object (if any) that should be called back when the entity
 *   is finished dropping (may be null if you don't care to be called back).
 * @param ent NativeEntity that should be moved through the world.
 * @param direction direction direction the item initially moves, may be null if you don't care.
 * @param speed speed the item initially moves if a direction is specified
 */
public void drop(Notify owner, NativeEntity ent, Angle3f direction, float speed)
	{
	fOwner = owner;
	fEntity = ent;
	
	// if a direction was specified, convert it and the speed 
	// into a velocity vector
	if (direction != null)
		{
		Vector3f velocity = Q2Recycler.getVector3f();
		direction.getVectors(velocity, null, null);		
		velocity.scale(speed);		
		fEntity.setVelocity(velocity);
		Q2Recycler.put(velocity);
		}

	// slow down if starting in water
	fInWater = (Engine.getPointContents(fEntity.getOrigin()) & Engine.MASK_WATER) != 0;
	if (fInWater)
		{
		Vector3f velocity = fEntity.getVelocity();
		velocity.scale(0.3F);
		fEntity.setVelocity(velocity);
		}
		
	// start continuous calls to runFrame to animate the item falling
	Game.addServerFrameListener(this, 0, 0);	
	}
/**
 * Handle dropping items to the floor
 */
public void runFrame(int phase) 
	{
	if (!fEntity.isValid())
		{
		dispose();
		return;
		}
		
	checkVelocity();
	applyGravity();

	int contentMask = Engine.MASK_SHOT;

	// if we're not in water now, also check for hitting a water surface
	if (!fInWater)
		contentMask |= Engine.MASK_WATER;
		
	TraceResults tr = fEntity.traceMove(contentMask, 1.0F);
	if (tr.fFraction == 1)
		return;		// moved the entire distance without hitting anything

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		// fell out of the world
		if (fOwner != null)
			fOwner.dropFinished(false);
		dispose();
		return;
		}

	// see if we hit water
	if (!fInWater && ((tr.fContents & Engine.MASK_WATER) != 0))
		{
		fInWater = true;
		int	color = Engine.SPLASH_UNKNOWN;

		// figure out what color the splash should be
		if ((tr.fContents & Engine.CONTENTS_WATER) != 0)
			{
			if (tr.fSurfaceName.equals("*brwater"))
				color = Engine.SPLASH_BROWN_WATER;
			else
				color = Engine.SPLASH_BLUE_WATER;
			}
		else if ((tr.fContents & Engine.CONTENTS_SLIME) != 0)
			color = Engine.SPLASH_SLIME;
		else if ((tr.fContents & Engine.CONTENTS_LAVA) != 0)
			color = Engine.SPLASH_LAVA;

		// cause a splash to appear
		if (color != Engine.SPLASH_UNKNOWN)
			{
			Engine.writeByte(Engine.SVC_TEMP_ENTITY);
			Engine.writeByte(Engine.TE_SPLASH);
			Engine.writeByte(8);
			Engine.writePosition(tr.fEndPos);
			Engine.writeDir(tr.fPlaneNormal);
			Engine.writeByte(color);
			Engine.multicast(tr.fEndPos, Engine.MULTICAST_PVS);
			}

		// slow down the fall because it hit water
		Vector3f v = fEntity.getVelocity();
		v.scale(0.3F);
		fEntity.setVelocity(v);

		// play a splash sound
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_in.wav"), 1, NativeEntity.ATTN_NORM, 0);
		return;
		}
		
	if ((clipVelocity(tr.fPlaneNormal, 1f) & 1) != 0)
		{
		// came to a stop
		
		// link it to whatever it came to a rest on, especially 
		// important if it happens to be something like an 
		// elevator, so that it rises and falls properly
		fEntity.setGroundEntity(tr.fEntity);

		if (fOwner != null)
			fOwner.dropFinished(true);
		dispose();
		return;		
		}
	}
}