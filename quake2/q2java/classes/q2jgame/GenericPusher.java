
package q2jgame;

import q2java.*;
import java.util.Enumeration;
 
/**
 * Superclass for entities like doors, plats, and trains
 * that push other entities around.
 *
 * @author Barry Pederson
 */ 
 
public abstract class GenericPusher extends GameEntity 
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
	private Vec3 fCurrentDest;
	private Vec3 fMoveDir;
	private Vec3 fLinearVelocity;
	private Vec3 fAngularVelocity;
	
	private boolean fIsAccelerative;
	private int fState;
	private int fEndState;
	private float fNextThink;
	
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
		
	fLinearVelocity = new Vec3();
	fAngularVelocity = new Vec3();
	fState = STATE_SPAWNED;
	fNextThink = (float) Game.gGameTime;
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
protected Vec3 getMoveDir() 
	{
	Vec3 angles = getAngles();
	setAngles(0, 0, 0);
		
	// door goes up	
	if (angles.equals(0, -1, 0))
		return new Vec3(0, 0, 1);

	// door goes down
	if (angles.equals(0, -2, 0))
		return new Vec3(0, 0, -1);

	// some other direction?	
	Vec3 result = new Vec3();	
	angles.angleVectors(result, null, null);
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
protected void moveTo(Vec3 dest) 
	{
	if (dest.equals(fCurrentDest))
		return;
		
	fCurrentDest = dest;		
	fLinearVelocity.clear();
	fMoveDir = new Vec3(dest);
	fMoveDir.subtract(getOrigin());
	fRemainingDistance = fMoveDir.normalizeLength();
	fCurrentSpeed = 0;		
		
	if ((fSpeed * Engine.SECONDS_PER_FRAME) > fRemainingDistance)
		{
		setupFinalMove();
		fState = STATE_FINALMOVE;
		return;
		}

	if ((fSpeed == fAccel) && (fSpeed == fDecel))
		{
		fLinearVelocity = new Vec3(fMoveDir);
		fLinearVelocity.scale(fSpeed);		
		float frames = (float) Math.floor((fRemainingDistance / fSpeed) / Engine.SECONDS_PER_FRAME);
		fRemainingDistance -= frames * fSpeed * Engine.SECONDS_PER_FRAME;
		fNextThink = (float)(Game.gGameTime + (frames * Engine.SECONDS_PER_FRAME));
		fState = STATE_MOVING_CONSTANT;
		}
	else
		{
		// accelerative
		fCurrentSpeed = 0;
		fNextThink = (float)(Game.gGameTime + Engine.SECONDS_PER_FRAME);
		fState = STATE_MOVING_ACCELERATED;
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return false if blocked, true if no problems.
 */
private boolean push() 
	{
	boolean isAngularMove = !fAngularVelocity.equals(0,0,0);
	boolean isLinearMove = !fLinearVelocity.equals(0,0,0);
	
	if (!isAngularMove && !isLinearMove)
		return true;
		
	Vec3 linearMove = null;
	Vec3 angularMove = null; 
	Vec3 forward = null;
	Vec3 right = null;
	Vec3 up = null;
	Vec3 mins = getAbsMins();
	Vec3 maxs = getAbsMaxs();			
	Vec3 pusherOrigin = getOrigin();

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
		linearMove = new Vec3(fLinearVelocity).scale(Engine.SECONDS_PER_FRAME).clampEight();
		setOrigin(pusherOrigin.add(linearMove));

		// find the bounding box			
		mins.add(linearMove);
		maxs.add(linearMove);			
		}
		
	// setup if moving angularly		
	if (isAngularMove)
		{		
		angularMove = new Vec3(fAngularVelocity).scale(Engine.SECONDS_PER_FRAME);		
		setAngles(getAngles().add(angularMove));
		
		// we need this for pushing things later			
		Vec3 org = new Vec3(-angularMove.x, -angularMove.y, -angularMove.z);
		forward = new Vec3();
		right = new Vec3();
		up = new Vec3();
		org.angleVectors(forward, right, up);			
		}
	
	linkEntity();
	
	// see if any solid entities are inside the final position
	NativeEntity[] hits = getPotentialPushed(mins, maxs);				
	for (int i = 0; (hits != null) && (i < hits.length); i++)
		{
		GameEntity check = (GameEntity) hits[i];
		
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

		Vec3 checkOrigin = check.getOrigin();
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
			Vec3 org = check.getOrigin().subtract(pusherOrigin);
			Vec3 org2 = new Vec3();
			org2.x = Vec3.dotProduct(org, forward);
			org2.y = -Vec3.dotProduct(org, right);
			org2.z = Vec3.dotProduct(org, up);
			org2.subtract(org);
			checkOrigin.add(org2);
			}
			
		check.setOrigin(checkOrigin);			
		check.linkEntity();
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
public void runFrame() 
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
	fLinearVelocity.set(fMoveDir).scale(fRemainingDistance / Engine.SECONDS_PER_FRAME);
	}	
/**
 * Halt the object.  Useful for when trains are turned off.
 */
protected void stopMoving() 
	{
	fLinearVelocity.clear();
	fAngularVelocity.clear();
	fState = STATE_IDLE;
	fNextThink = 0;
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
private static NativeEntity testEntityPosition(NativeEntity ent) 
	{
	Vec3 origin = ent.getOrigin();
	TraceResults tr = Engine.trace(origin, ent.getMins(), ent.getMaxs(), origin, ent, Engine.MASK_SOLID);
	if (tr.fStartSolid)
		return Game.gWorld;
		
	return null;		
	}
/**
 * This method was created by a SmartGuide.
 */
protected void think() 
	{
	if ((fNextThink <= 0) || (Game.gGameTime < fNextThink))
		return;

	switch (fState)
		{
		case STATE_SPAWNED:
			syncGroupSpeed();
			fState = STATE_IDLE;
		case STATE_IDLE:
			fNextThink = 0;
			break;
			
		case STATE_MOVING_CONSTANT:
			if (fRemainingDistance > 0)
				{
				setupFinalMove();
				fState = STATE_FINALMOVE;
				break;
				}
				
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
			fLinearVelocity.set(fMoveDir).scale(fCurrentSpeed * 10);			
			fNextThink += Engine.SECONDS_PER_FRAME;
			break;				
			
		case STATE_FINALMOVE:
			fLinearVelocity.clear();
			fAngularVelocity.clear();
			fNextThink = 0;
			fState = STATE_IDLE;			
			moveFinished();
			break;						
		}		
	}
}