package q2java.baseq2;

import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
 
/**
 * Superclass for entities like doors, plats, and trains
 * that push other entities around.
 *
 * @author Barry Pederson
 */ 
 
public abstract class GenericPusher extends GameObject implements ServerFrameListener, FixedObject
	{
	// possible spawn args
	protected float fSpeed;
	protected float fAccel;
	protected float fDecel;
	protected float fMoveDistance;
	
	// stuff only the GenericPusher methods will use
	protected float fCurrentSpeed;
	protected float fNextSpeed;
	protected float fMoveSpeed;
	protected float fDecelDistance;
	protected float fRemainingDistance;	
	protected Point3f fCurrentDest;
	protected Angle3f fCurrentDestAngle;
	protected Vector3f fMoveDir;
	protected Angle3f fMoveAngle;
	protected Vector3f fLinearVelocity;
	protected Angle3f fAngularVelocity;
	
	protected boolean fIsAccelerative;
	protected int fState;
	protected int fEndState;
	protected float fLastFrameTime;

	protected Vector fPositionStack = new Vector();
	protected GameObject fObstacle;
	
	protected final static int STATE_SPAWNED = 0;
	protected final static int STATE_IDLE = 1;
	protected final static int STATE_MOVING_CONSTANT = 2;
	protected final static int STATE_MOVING_ACCELERATED = 3;
	protected final static int STATE_FINALMOVE = 4;
	protected final static int STATE_ROTATING_CONSTANT = 5;

	protected final static int STATE_MOVING_ANGULAR_CONSTANT = 6;
	protected final static int STATE_MOVING_ANGULAR_ACCELERATED = 7;	
	
/**
 * GenericMover constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public GenericPusher(Element spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
		
	fLinearVelocity = new Vector3f();
	fAngularVelocity = new Angle3f();
	fState = STATE_SPAWNED;
	
	// schedule a one-shot call so we can sync speeds
	Game.addServerFrameListener(this, 0, -1);			
	}
/**
 * This method was created by a SmartGuide.
 */
protected void accelerate() 
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
 * Called when the GenericPusher is blocked by another object.
 * @param obj The GameObject that's in the way.
 */
public void block(GameObject obj) 
	{
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
	Game.addServerFrameListener(this, 0, 0);		
			
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
		fLinearVelocity.set(fMoveDir);
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
 * Restore an entity's position off the stack.
 * @return The entity that was restored
 */
protected NativeEntity popPosition() 
	{
	int n = fPositionStack.size();
	NativeEntity ent = (NativeEntity) fPositionStack.elementAt(n - 1);
	if (ent.isPlayer())
		ent.setPlayerDeltaAngles((Angle3f)fPositionStack.elementAt(n - 2));
	ent.setAngles((Angle3f)fPositionStack.elementAt(n - 3));
	ent.setOrigin((Point3f)fPositionStack.elementAt(n - 4));
	fPositionStack.setSize(n - 4);

	return ent;
	}
/**
 * This method was created by a SmartGuide.
 * @return false if blocked, true if no problems.
 */
protected boolean push() 
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

	// save the pusher's original position
	pushPosition(fEntity);
	
	// setup if moving linearly				
	if (isLinearMove)
		{		
		if (fState == STATE_FINALMOVE)
			{
			// snap to final position rather
			// than rely on accumulated individual
			// moves.		
			linearMove = new Vector3f();
			linearMove.sub(fCurrentDest, pusherOrigin);
			pusherOrigin.set(fCurrentDest);
			}
		else
			{	
			linearMove = new Vector3f(fLinearVelocity);
			linearMove.scale(Engine.SECONDS_PER_FRAME);
			MiscUtil.clampEight(linearMove);
			pusherOrigin.add(linearMove);			
			}

		fEntity.setOrigin(pusherOrigin);			
			
		// find the bounding box			
		mins.add(linearMove);
		maxs.add(linearMove);			
		}
		
	// setup if moving angularly		
	if (isAngularMove)
		{		
		Angle3f currentAngle = fEntity.getAngles();
		if (fState == STATE_FINALMOVE)
			{
			// snap to final position rather
			// than rely on accumulated individual
			// moves.		
			angularMove = new Angle3f();
			angularMove.sub(fCurrentDestAngle, pusherOrigin);
			currentAngle.set(fCurrentDestAngle);
			}
		else
			{	
			angularMove = new Angle3f(fAngularVelocity);
			angularMove.scale(Engine.SECONDS_PER_FRAME);		
			currentAngle.add(angularMove);
			}		
			
		fEntity.setAngles(currentAngle);

		// we need this for pushing things later			
		Angle3f org = Q2Recycler.getAngle3f();
		
		org.set(-angularMove.x, -angularMove.y, -angularMove.z);
		forward = new Vector3f();
		right = new Vector3f();
		up = new Vector3f();
		org.getVectors(forward, right, up);
		
		Q2Recycler.put(org);
		}
	
	fEntity.linkEntity();
	
	// see if any solid entities are inside the final position
	NativeEntity[] hits = fEntity.getPotentialPushed(mins, maxs, Engine.MASK_SOLID);				
	for (int i = 0; (hits != null) && (i < hits.length); i++)
		{
		GameObject check = (GameObject) hits[i].getReference();
		
		if (check instanceof FixedObject)
			continue;
			
		boolean isPlayer = check instanceof Player;

		// move this entity
		pushPosition(check.fEntity);

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
			Vector3f org = Q2Recycler.getVector3f();
			Point3f org2 = Q2Recycler.getPoint3f();
			
			org.sub(check.fEntity.getOrigin(), pusherOrigin);
			org2.x = forward.dot(org);
			org2.y = -right.dot(org);
			org2.z = up.dot(org);
			org2.sub(org);
			checkOrigin.add(org2);

			if (isPlayer)
				{
				Angle3f da = check.fEntity.getPlayerDeltaAngles();
				da.y += angularMove.y;
				check.fEntity.setPlayerDeltaAngles(da);
				}

			Q2Recycler.put(org2);
			Q2Recycler.put(org);
			}
			
		check.fEntity.setOrigin(checkOrigin);
		
		// may have pushed them off an edge
//		if (check->groundentity != pusher)
//			check->groundentity = NULL;

		if (testEntityPosition(check.fEntity) == null)
			{
			check.fEntity.linkEntity();
			continue;
			}

		// if it is ok to leave in the old position, do it
		// this is only relevent for riding entities, not pushed
		popPosition();
		if (testEntityPosition(check.fEntity) == null)
			continue;

		// let hte pusher know who's blocking it.
		fObstacle = check;
		
		// move back any entities we already moved
		// go backwards, so if the same entity was pushed
		// twice, it goes back to the original position
		while (fPositionStack.size() > 0)
			popPosition();
			
		return false;
		}
		
/*

//FIXME: is there a better way to handle this?
	// see if anything we moved has touched a trigger
	for (p=pushed_p-1 ; p>=pushed ; p--)
		G_TouchTriggers (p->ent);
*/
	return true;		
	}
/**
 * Save an entity's position on the stack.
 * @param ent q2java.NativeEntity
 */
protected void pushPosition(NativeEntity ent) 
	{
	fPositionStack.addElement(ent.getOrigin());
	fPositionStack.addElement(ent.getAngles());
	fPositionStack.addElement(ent.getPlayerDeltaAngles());
	fPositionStack.addElement(ent);
	}
/**
 * This method was created by a SmartGuide.
 * @param dest q2java.Angle3f
 */
protected void rotateTo(Angle3f dest) 
	{
	if (dest.equals(fCurrentDestAngle))
		return;

	// start getting continuous frame notifications
	Game.addServerFrameListener(this, 0, 0);		
			
	fCurrentDestAngle = dest;		
	fAngularVelocity.set(0,0,0);
	fMoveAngle = new Angle3f(dest);
	fMoveAngle.sub(fEntity.getAngles());
	fRemainingDistance = Math.abs(fMoveAngle.x + fMoveAngle.y + fMoveAngle.z);
	fMoveAngle.scale(1 / fRemainingDistance);
	fCurrentSpeed = 0;		

	if ((fSpeed * Engine.SECONDS_PER_FRAME) > fRemainingDistance)
		{
		setupFinalAngularMove();
		fState = STATE_FINALMOVE;
		return;
		}

	if ((fSpeed == fAccel) && (fSpeed == fDecel))
		{
		fAngularVelocity = new Angle3f(fMoveAngle);
		fAngularVelocity.scale(fSpeed);		

		float frames = (float) Math.floor((fRemainingDistance / fSpeed) / Engine.SECONDS_PER_FRAME);
		fRemainingDistance -= frames * fSpeed * Engine.SECONDS_PER_FRAME;
		fLastFrameTime = (float)(Game.getGameTime() + (frames * Engine.SECONDS_PER_FRAME));
		fState = STATE_MOVING_ANGULAR_CONSTANT;
		}
	else
		{
		// accelerative
		fCurrentSpeed = 0;
		fState = STATE_MOVING_ANGULAR_ACCELERATED;
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame(int phase) 
	{
	// if not a team captain, so movement will be handled elsewhere	
	if (isGroupSlave())
		return;
		
	// clear the position stack
	fPositionStack.setSize(0);
	fObstacle = null;

	try
		{
		// try to push the group Master
		boolean pushOK = push();
			
			
		// make sure all team slaves can move before commiting
		// any moves or calling any think functions
		// if the move is blocked, all moved objects will be backed out
		if (pushOK && (fGroup != null))
			{
			for (int i = 1; pushOK && (i < fGroup.size()); i++)
				{
				GenericPusher slave  = (GenericPusher) fGroup.elementAt(i);
				pushOK = slave.push();
				}
			}

		if (!pushOK)
			{
			if (fObstacle != null)
				{
				block(fObstacle);
				fObstacle = null;
				}
			return;
			}
		}
	catch (Exception e)
		{
		System.out.println("Exception in : " + this);
		e.printStackTrace();	
		}
	
	// clear the position stack
	fPositionStack.setSize(0);

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
 * Activate or deactivate areaportals associated with this object.
 * @param isOpen boolean
 */
protected void setPortals(boolean state) 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)		
		{
		Object obj = fTargets.elementAt(i);
		if (obj instanceof q2java.baseq2.spawn.func_areaportal)
			((q2java.baseq2.spawn.func_areaportal) obj).setPortal(state);
		}
	}
/**
 * This method was created by a SmartGuide.
 */
protected void setupAcceleratedMove() 
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
protected void setupFinalAngularMove() 
	{
	fAngularVelocity.set(fMoveAngle);
	fAngularVelocity.scale(fRemainingDistance / Engine.SECONDS_PER_FRAME);
	}	
/**
 * This method was created by a SmartGuide.
 */
protected void setupFinalMove() 
	{
	fLinearVelocity.set(fMoveDir);
	fLinearVelocity.scale(fRemainingDistance / Engine.SECONDS_PER_FRAME);
	}	
/**
 * This method was created by a SmartGuide.
 * @param avelocity q2java.Angle3f
 */
protected void startRotating(Angle3f avelocity) 
	{
	fState = STATE_ROTATING_CONSTANT;
	fAngularVelocity = new Angle3f(avelocity);

	// start getting continuous frame notifications
	Game.addServerFrameListener(this, 0, 0);		
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
protected void syncGroupSpeed() 
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
 * Check if an entity intersects with something else.
 * @return Entity intersected with.
 * @param ent Entity being checked.
 */
protected static GameObject testEntityPosition(NativeEntity ent) 
	{
	int mask = ent.getClipmask();
	if (mask == 0)
		mask = Engine.MASK_SOLID;
		
	Point3f origin = ent.getOrigin();
	TraceResults tr = Engine.trace(origin, ent.getMins(), ent.getMaxs(), origin, ent, mask);
	if (tr.fStartSolid)
		return BaseQ2.gWorld;
		
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
		case STATE_ROTATING_CONSTANT:
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
			
		case STATE_MOVING_ANGULAR_ACCELERATED:
			fRemainingDistance -= fCurrentSpeed;
			if (fCurrentSpeed == 0)
				setupAcceleratedMove();
			accelerate();
			if (fRemainingDistance <= fCurrentSpeed)
				{
				setupFinalAngularMove();
				fState = STATE_FINALMOVE;
				break;
				}		
			fAngularVelocity.set(fMoveDir);
			fAngularVelocity.scale(fCurrentSpeed * 10);			
			break;
			
		case STATE_MOVING_CONSTANT:
		case STATE_MOVING_ANGULAR_CONSTANT:
			if (Game.getGameTime() < fLastFrameTime)
				return;
				
			if (fRemainingDistance > 0)
				{
				if (fState == STATE_MOVING_CONSTANT)
					setupFinalMove();
				else
					setupFinalAngularMove();
				fState = STATE_FINALMOVE;
				break;
				}
			// fall through to STATE_FINALMOVE
									
		case STATE_FINALMOVE:
			fLinearVelocity.set(0, 0, 0);
			fAngularVelocity.set(0, 0, 0);
			fState = STATE_IDLE;	
			
			// turn off frame notifications
			Game.removeServerFrameListener(this);		
			
			moveFinished();
			break;						
		}		
	}
}