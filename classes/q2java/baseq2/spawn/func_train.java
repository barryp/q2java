package q2java.baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * func_train - objects that move around following
 *  preset path
 *
 * @author Barry Pederson
 */

public class func_train extends GenericPusher 
	{
	// spawn parameters
	private float fWait;
	private int fDmg;
	private float fTouchDebounceTime;
		
	// track the state of the train
	private int fTrainState;
	private GameObject fTrainTarget;
	private Point3f fTrainDestination;
	
	// train sounds
	private int fSoundMiddle;
	
	// train state constants		
	private final static int STATE_TRAIN_SPAWNED	= 0;
	private final static int STATE_TRAIN_STOPPED	= 1;
	private final static int STATE_TRAIN_MOVING	= 2;
	private final static int STATE_TRAIN_WAITING	= 3;
	
	// train spawn flags
	private final static int TRAIN_START_ON		= 1;
	private final static int TRAIN_TOGGLE		= 2;
	private final static int TRAIN_BLOCK_STOPS	= 4;	
	
public func_train(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	fEntity.setAngles(0, 0, 0);
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);

	fSpeed = getSpawnArg("speed", 100.0F);
	fAccel = getSpawnArg("accel", fSpeed);
	fDecel = getSpawnArg("decel", fSpeed);	
	fDmg = getSpawnArg("dmg", 100);
	if ((fSpawnFlags & TRAIN_BLOCK_STOPS) != 0)
		fDmg = 0;

	String noise = getSpawnArg("noise", null);		
	if (noise != null)
		fSoundMiddle = Engine.getSoundIndex(noise);
		
	fEntity.linkEntity();	
	
	if (fTargets == null)
		{
		Game.dprint("func_train without a target at " + fEntity.getAbsMins() + "\n");
		fTrainState = STATE_TRAIN_STOPPED;
		}
	else
		{
		fTrainState = STATE_TRAIN_SPAWNED;
		Game.addFrameListener(this, 0, -1);
		}		
	}
/**
 * Called when the GenericPusher is blocked by another object.
 * @param obj The GameObject that's in the way.
 */
public void block(GameObject obj) 
	{
	Vector3f origin = new Vector3f();
	
	if (!(obj instanceof Player))
		{
		// give it a chance to go away on it's own terms (like gibs)
		obj.damage(this, this, origin, obj.fEntity.getOrigin(), origin, 100000, 1, 0, 0, "crush");
		// if it's still there, nuke it
		if (obj.fEntity != null)
			obj.becomeExplosion(Engine.TE_EXPLOSION1);
		return;		
		}
	
	float time = Game.getGameTime();
	if (time < fTouchDebounceTime)
		return;

	if (fDmg == 0)
		return;
		
	fTouchDebounceTime = time + 0.5F;	
	obj.damage(this, this, origin, obj.fEntity.getOrigin(), origin, (int)fDmg, 1, 0, 0, "crush");
	}
/**
 * This method was created by a SmartGuide.
 */
public void go() 
	{
	if (fTrainState == STATE_TRAIN_MOVING)
		return;
		
	boolean first = true;

	while (true)
		{
		fTrainTarget = getRandomTarget();
		if (fTargets == null)
			{
//			Game.dprint("train_next: no next target\n");
			fTrainState = STATE_TRAIN_STOPPED;
			return;
			}

		fTargets = fTrainTarget.getTargets();

		// check for a teleport path_corner
		if ((fTrainTarget.getSpawnFlags() & 1) == 0)
			break;  // out if while(true) loop
		else				
			{
			if (!first)
				{
				Game.dprint("connected teleport path_corners, see " + fTrainTarget.getClass().getName() + " at " + fTrainTarget.fEntity.getOrigin() + "\n");
				return;
				}
			first = false;
			Point3f p = fTrainTarget.fEntity.getOrigin();
			p.sub(fEntity.getMins());
			fEntity.setOrigin(p);
//			VectorCopy (self->s.origin, self->s.old_origin);
			fEntity.linkEntity();
			}				
		}
		
	fWait = fTrainTarget.getSpawnArg("wait", 0.0F);
	fTrainDestination = fTrainTarget.fEntity.getOrigin();
	fTrainDestination.sub(fEntity.getMins());
		
	moveTo(fTrainDestination);	
	fTrainState = STATE_TRAIN_MOVING;
	
	if (!isGroupSlave())
		fEntity.setSound(fSoundMiddle);
	}
/**
 * This method was created by a SmartGuide.
 */
protected void moveFinished() 
	{
	fTrainState = STATE_TRAIN_STOPPED;

	if (fWait <= 0)
		go();
	else		
		{
		fTrainState = STATE_TRAIN_WAITING;
		Game.addFrameListener(this, fWait, -1);
		fEntity.setSound(0);
		}	
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame(int phase) 
	{
	switch (fTrainState)
		{
		case STATE_TRAIN_SPAWNED:
			fTrainTarget = getRandomTarget();
			Point3f p = fTrainTarget.fEntity.getOrigin();
			p.sub(fEntity.getMins());
			fEntity.setOrigin(p);
			fEntity.linkEntity();
			fTargets = fTrainTarget.getTargets();
				
			if ((fTargetGroup == null) || ((fSpawnFlags & TRAIN_START_ON) != 0))
				go();
			else
				fTrainState = STATE_TRAIN_STOPPED;
			break;
				
		case STATE_TRAIN_WAITING:
			go();
			break;
			
		default:
			super.runFrame(phase);
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void stop() 
	{
	switch (fTrainState)
		{
		case STATE_TRAIN_MOVING:
			stopMoving();
			break;				
		}

	fTrainState = STATE_TRAIN_STOPPED;
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	go();
	}
}