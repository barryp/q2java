
package baseq2;

import java.util.Enumeration;
import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
 
/**
 * Superclass for entities like doors, plats, and trains
 * that push other entities around.
 *
 * @author Barry Pederson
 */ 
 
public abstract class GenericPusher extends GameObject implements FrameListener
	{
	// possible spawn args
	protected float fSpeed;
	protected float fAccel;
	protected float fDecel;
	protected float fMoveDistance;
	
	// stuff only the GenericPusher methods will use
	private float fCurrentSpeed;
	private float fNextSpeed;
	private float fMoveSpeed;
	private float fDecelDistance;
	private float fRemainingDistance;	
	private Point3f fCurrentDest;
	private Vector3f fMoveDir;
	private Vector3f fLinearVelocity;
	private Angle3f fAngularVelocity;
	
	private boolean fIsAccelerative;
	private int fState;
	private int fEndState;
	private float fLastFrameTime;
	
	private final static int STATE_SPAWNED = 0;
	private final static int STATE_IDLE = 1;
	private final static int STATE_MOVING_CONSTANT = 2;
	private final static int STATE_MOVING_ACCELERATED = 3;
	private final static int STATE_FINALMOVE = 4;
	
/**
 * GenericMover constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public GenericPusher(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
		
	fLinearVelocity = new Vector3f();
	fAngularVelocity = new Angle3f();
	fState = STATE_SPAWNED;
	
	// schedule a one-shot call so we can sync speeds
	Game.addFrameListener(this, 0, -1);			
	}
/**
 * This method was created by a SmartGuide.
 */
private void accelerate() 
	{
	// are we decelerating?
	if (fRemainingDistance <= fDecelDistance)
		{
		if (fRemainingDistance < fDecelDistance)
			{
			if (fNextSpeed != 0)
				{
				fCurrentSpeed = fNextSpeed;
				fNextSpeed = 0;
				return;
				}
			if (fCurrentSpeed > fDecel)
				fCurrentSpeed -= fDecel;
			}
		return;
		}

	// are we at full speed and need to start decelerating during this move?
	if (fCurrentSpeed == fMoveSpeed)
		if ((fRemainingDistance - fCurrentSpeed) < fDecelDistance)
			{			
			float p1_distance = fRemainingDistance - fDecelDistance;
			float p2_distance = (float)(fMoveSpeed * (1.0 - (p1_distance / fMoveSpeed)));
			float distance = p1_distance + p2_distance;
			
			fCurrentSpeed = fMoveSpeed;
			fNextSpeed = fMoveSpeed - fDecel * (p2_distance / distance);
			return;
			}

	// are we accelerating?
	if (fCurrentSpeed < fSpeed)
		{
		float old_speed = fCurrentSpeed;

		// figure simple acceleration up to move_speed
		fCurrentSpeed += fAccel;
		if (fCurrentSpeed > fSpeed)
			fCurrentSpeed = fSpeed;

		// are we accelerating throughout this entire move?
		if ((fRemainingDistance - fCurrentSpeed) >= fDecelDistance)
			return;

		// during this move we will accelrate from current_speed to move_speed
		// and cross over the decel_distance; figure the average speed for the
		// entire move
		float p1_distance = fRemainingDistance - fDecelDistance;
		float p1_speed = (float)((old_speed + fMoveSpeed) / 2.0);
		float p2_distance = (float)(fMoveSpeed * (1.0 - (p1_distance / p1_speed)));
		float distance = p1_distance + p2_distance;
		fCurrentSpeed = (p1_speed * (p1_distance / distance)) + (fMoveSpeed * (p2_distance / distance));
		fNextSpeed = fMoveSpeed - fDecel * (p2_distance / distance);
		return;
		}

	// we are at constant velocity (move_speed)
	return;
	}
/**
 * Get the direction the door or button should move.
 * When the entity is spawned, the "angles"
 * indicates the direction the door should move,
 * but the format is a little bizarre, and the
 * value doesn't actually indicate the angles
 * for the entity, so we -have- to clear it,
 * once we get a copy, otherwise the doors
 * will appear on the maps in all sorts of odd positions.
 * @return a Vec3 pointing in the direction the door opens.
 */
protected Vector3f getMoveDir() 
	{
	Angle3f angles = fEntity.getAngles();
	fEntity.setAngles(0, 0, 0);
		
	// door goes up	
	if (MiscUtil.equals(angles, 0, -1, 0))
		return new Vector3f(0, 0, 1);

	// door goes down
	if (MiscUtil.equals(angles, 0, -2, 0))
		return new Vector3f(0, 0, -1);

	// some other direction?	
	Vector3f result = new Vector3f();	
	angles.getVectors(result, null, null);
	return result;
	}

/**
 * This method was created by a SmartGuide.
 */
protected abstract void moveFinished();
/**
 * This method was created by a SmartGuide.
 * @param dest q2java.Vec3
 * @param newState int
 */
protected void moveTo(Point3f dest) 
	{
	if (dest.equals(fCurrentDest))
		return;

	// start getting continuous frame notifications
	Game.addFrameListener(this, 0, 0);		
			
	fCurrentDest = dest;		
	fLinearVelocity.set(0,0,0);
	fMoveDir = new Vector3f(dest);
	fMoveDir.sub(fEntity.getOrigin());
	fRemainingDistance = fMoveDir.length();
	fMoveDir.scale(1 / fRemainingDistance);
	fCurrentSpeed = 0;		
		
	if ((fSpeed * Engine.SECONDS_PER_FRAME) > fRemainingDistance)
		{
		setupFinalMove();
		fState = STATE_FINALMOVE;
		return;
		}

	if ((fSpeed == fAccel) && (fSpeed == fDecel))
		{
		fLinearVelocity = new Vector3f(fMoveDir);
		fLinearVelocity.scale(fSpeed);		
		float frames = (float) Math.floor((fRemainingDistance / fSpeed) / Engine.SECONDS_PER_FRAME);
		fRemainingDistance -= frames * fSpeed * Engine.SECONDS_PER_FRAME;
		fLastFrameTime = (float)(Game.getGameTime() + (frames * Engine.SECONDS_PER_FRAME));
		fState = STATE_MOVING_CONSTANT;
		}
	else
		{
		// accelerative
		fCurrentSpeed = 0;
		fState = STATE_MOVING_ACCELERATED;
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return false if blocked, true if no problems.
 */
private boolean push() 
	{
	boolean isAngularMove = ! MiscUtil.equals(fAngularVelocity, 0, 0, 0);
	boolean isLinearMove = ! MiscUtil.equals(fLinearVelocity, 0, 0, 0);
	
	if (!isAngularMove && !isLinearMove)
		return true;
		
	Vector3f linearMove = null;
	Angle3f angularMove = null; 
	Vector3f forward = null;
	Vector3f right = null;
	Vector3f up = null;
	Tuple3f mins = fEntity.getAbsMins();
	Tuple3f maxs = fEntity.getAbsMaxs();			
	Point3f pusherOrigin = fEntity.getOrigin();

/*	
// save the pusher's original position
	pushed_p->ent = pusher;
	VectorCopy (pusher->s.origin, pushed_p->origin);
	VectorCopy (pusher->s.angles, pushed_p->angles);
	if (pusher->client)
		pushed_p->deltayaw = pusher->client->ps.pmove.delta_angles[YAW];
	pushed_p++;
*/

	// setup if moving linearly				
	if (isLinearMove)
		{				
		linearMove = new Vector3f(fLinearVelocity);
		linearMove.scale(Engine.SECONDS_PER_FRAME);
		MiscUtil.clampEight(linearMove);
		pusherOrigin.add(linearMove);
		fEntity.setOrigin(pusherOrigin);

		// find the bounding box			
		mins.add(linearMove);
		maxs.add(linearMove);			
		}
		
	// setup if moving angularly		
	if (isAngularMove)
		{		
		angularMove = new Angle3f(fAngularVelocity);
		angularMove.scale(Engine.SECONDS_PER_FRAME);		
		Angle3f currentAngle = fEntity.getAngles();
		currentAngle.add(angularMove);
		fEntity.setAngles(currentAngle);
		
		// we need this for pushing things later			
		Angle3f org = new Angle3f(-angularMove.x, -angularMove.y, -angularMove.z);
		forward = new Vector3f();
		right = new Vector3f();
		up = new Vector3f();
		org.getVectors(forward, right, up);			
		}
	
	fEntity.linkEntity();
	
	// see if any solid entities are inside the final position
	NativeEntity[] hits = fEntity.getPotentialPushed(mins, maxs);				
	for (int i = 0; (hits != null) && (i < hits.length); i++)
		{
		GameObject check = (GameObject) hits[i].getReference();
		
		if (check instanceof GenericPusher)
			continue;
						
		if (check instanceof AreaTrigger)
			continue;			

		boolean isPlayer = check instanceof Player;

		// move this entity
//		pushed_p->ent = check;
//		VectorCopy (check->s.origin, pushed_p->origin);
//		VectorCopy (check->s.angles, pushed_p->angles);
//		pushed_p++;

		Point3f checkOrigin = check.fEntity.getOrigin();
		// try moving the contacted entity 
		if (isLinearMove)
			checkOrigin.add(linearMove);
//		if (check->client)
//			{	// FIXME: doesn't rotate monsters?
//			check->client->ps.pmove.delta_angles[YAW] += amove[YAW];
//			}

		if (isAngularMove)
			{
			// figure movement due to the pusher's amove
			Vector3f org = new Vector3f(check.fEntity.getOrigin());
			org.sub(pusherOrigin);
			Tuple3f org2 = new Tuple3f();
			org2.x = forward.dot(org);
			org2.y = -right.dot(org);
			org2.z = up.dot(org);
			org2.sub(org);
			checkOrigin.add(org2);
			}
			
		check.fEntity.setOrigin(checkOrigin);			
		check.fEntity.linkEntity();
		}
		
/*
		// may have pushed them off an edge
		if (check->groundentity != pusher)
			check->groundentity = NULL;

		block = SV_TestEntityPosition (check);
		if (!block)
			{	// pushed ok
			gi.linkentity (check);
			// impact?
			continue;
			}

		// if it is ok to leave in the old position, do it
		// this is only relevent for riding entities, not pushed
		// FIXME: this doesn't acount for rotation
		VectorSubtract (check->s.origin, move, check->s.origin);
		block = SV_TestEntityPosition (check);
		if (!block)
			{
			pushed_p--;
			continue;
			}
		
		// save off the obstacle so we can call the block function
		obstacle = check;

		// move back any entities we already moved
		// go backwards, so if the same entity was pushed
		// twice, it goes back to the original position
		for (p=pushed_p-1 ; p>=pushed ; p--)
			{
			VectorCopy (p->origin, p->ent->s.origin);
			VectorCopy (p->angles, p->ent->s.angles);
			if (p->ent->client)
				{
				p->ent->client->ps.pmove.delta_angles[YAW] = p->deltayaw;
				}
			gi.linkentity (p->ent);
			}	
		return false;
		}

//FIXME: is there a better way to handle this?
	// see if anything we moved has touched a trigger
	for (p=pushed_p-1 ; p>=pushed ; p--)
		G_TouchTriggers (p->ent);
*/
	return true;		
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame(int phase) 
	{
	// if not a team captain, so movement will be handled elsewhere	
	if (isGroupSlave())
		return;

try
	{
	// try to push the group Master
	if (!push())
		return;
			
			
	// make sure all team slaves can move before commiting
	// any moves or calling any think functions
	// if the move is blocked, all moved objects will be backed out
	if (fGroup != null)
		{
		for (int i = 1; i < fGroup.size(); i++)
			{
			GenericPusher slave  = (GenericPusher) fGroup.elementAt(i);			
			if (!slave.push())
				break;
			}
		}
	}
catch (Exception e)
	{
	System.out.println("Exception in : " + this);
	e.printStackTrace();	
	}
		
/*					
//retry:
	pushed_p = pushed;
	for (part = ent ; part ; part=part->teamchain)
	{
		if (part->velocity[0] || part->velocity[1] || part->velocity[2] ||
			part->avelocity[0] || part->avelocity[1] || part->avelocity[2]
			)
		{	// object is moving
			VectorScale (part->velocity, FRAMETIME, move);
			VectorScale (part->avelocity, FRAMETIME, amove);

			if (!SV_Push (part, move, amove))
				break;	// move was blocked
		}
	}
	if (pushed_p > &pushed[MAX_EDICTS])
		gi.error (ERR_FATAL, "pushed_p > &pushed[MAX_EDICTS], memory corrupted");

	if (part)
	{
		// the move failed, bump all nextthink times and back out moves
		for (mv = ent ; mv ; mv=mv->teamchain)
		{
			if (mv->nextthink > 0)
				mv->nextthink += FRAMETIME;
		}

		// if the pusher has a "blocked" function, call it
		// otherwise, just stay in place until the obstacle is gone
		if (part->blocked)
			part->blocked (part, obstacle);
#if 0
		// if the pushed entity went away and the pusher is still there
		if (!obstacle->inuse && part->inuse)
			goto retry;
#endif
	}
	else
	{
		// the move succeeded, so call all think functions
		for (part = ent ; part ; part=part->teamchain)
		{
			SV_RunThink (part);
		}
	}
*/	

	// call all think functions
	think();
	if (fGroup != null)
		{
		for (int i = 1; i < fGroup.size(); i++)
			((GenericPusher)fGroup.elementAt(i)).think();
		}			
	}
/**
 * This method was created by a SmartGuide.
 */
private void setupAcceleratedMove() 
	{
	float accelDist;
	float decelDist;

	fIsAccelerative = true;
	fMoveSpeed = fSpeed;

	if (fRemainingDistance < fAccel)
		{
		fCurrentSpeed = fRemainingDistance;
		return;
		}

	accelDist = (fSpeed * ((fSpeed / fAccel) + 1) / 2);
	decelDist = (fSpeed * ((fSpeed / fDecel) + 1) / 2);
	
	if ((fRemainingDistance - accelDist - decelDist) < 0)		
		{
		float f = (fAccel + fDecel) / (fAccel * fDecel);
		fMoveSpeed = (float) ((-2 + Math.sqrt(4 - 4 * f * (-2 * fRemainingDistance))) / (2 * f));
		decelDist = (fMoveSpeed * ((fMoveSpeed / fDecel) + 1) / 2);
		}

	fDecelDistance = decelDist;
	}
/**
 * This method was created by a SmartGuide.
 */
private void setupFinalMove() 
	{
	fLinearVelocity.set(fMoveDir);
	fLinearVelocity.scale(fRemainingDistance / Engine.SECONDS_PER_FRAME);
	}	
/**
 * Halt the object.  Useful for when trains are turned off.
 */
protected void stopMoving() 
	{
	fLinearVelocity.set(0, 0, 0);
	fAngularVelocity.set(0, 0, 0);
	fState = STATE_IDLE;
	}
/**
 * Adjust the speed of group members so they finish at the same time.
 */
private void syncGroupSpeed() 
	{
	if ((fGroup == null) || isGroupSlave())
		return;		// only the group master does this

	// find the smallest distance any member of the group will be moving
	float min = Math.abs(fMoveDistance);
	for (int i = 1; i < fGroup.size(); i++)
		{
		float dist = Math.abs(((GenericPusher)fGroup.elementAt(i)).fMoveDistance);
		if (dist < min)
			min = dist;
		}
		
	float time = min / fSpeed;

	// adjust speeds so they will all complete at the same time
	for (int i = 0; i < fGroup.size(); i++)
		{
		GenericPusher gp = (GenericPusher) fGroup.elementAt(i);
		float newspeed = Math.abs(fMoveDistance) / time;
		float ratio = newspeed / gp.fSpeed;

		if (gp.fAccel == gp.fSpeed)
			gp.fAccel = newspeed;
		else			
			gp.fAccel *= ratio;
			
		if (gp.fDecel == gp.fSpeed)
			gp.fDecel = newspeed;
		else			
			gp.fDecel *= ratio;
	
		gp.fSpeed = newspeed;				
		}		
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.NativeEntity
 * @param ent q2java.NativeEntity
 */
private static GameObject testEntityPosition(NativeEntity ent) 
	{
	Point3f origin = ent.getOrigin();
	TraceResults tr = Engine.trace(origin, ent.getMins(), ent.getMaxs(), origin, ent, Engine.MASK_SOLID);
	if (tr.fStartSolid)
		return GameModule.gWorld;
		
	return null;		
	}
/**
 * The actual moving has been handled, now think 
 * about what we're  going to do next frame.
 */
protected void think() 
	{
	switch (fState)
		{
		case STATE_SPAWNED:
			syncGroupSpeed();
			fState = STATE_IDLE;
			break;
			
		case STATE_IDLE:
			break;
			
		case STATE_MOVING_ACCELERATED:
			fRemainingDistance -= fCurrentSpeed;
			if (fCurrentSpeed == 0)
				setupAcceleratedMove();
			accelerate();
			if (fRemainingDistance <= fCurrentSpeed)
				{
				setupFinalMove();
				fState = STATE_FINALMOVE;
				break;
				}		
			fLinearVelocity.set(fMoveDir);
			fLinearVelocity.scale(fCurrentSpeed * 10);			
			break;				
			

		case STATE_MOVING_CONSTANT:
			if (Game.getGameTime() < fLastFrameTime)
				return;
				
			if (fRemainingDistance > 0)
				{
				setupFinalMove();
				fState = STATE_FINALMOVE;
				break;
				}
			// fall through to STATE_FINALMOVE
							
		case STATE_FINALMOVE:
			fLinearVelocity.set(0, 0, 0);
			fAngularVelocity.set(0, 0, 0);
			fState = STATE_IDLE;	
			
			// turn off frame notifications
			Game.removeFrameListener(this);		
			
			moveFinished();
			break;						
		}		
	}
}