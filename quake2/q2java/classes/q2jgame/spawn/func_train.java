
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_train extends GenericPusher 
	{
	// spawn parameters
	private float fWait;
	private int fDmg;
		
	// track the state of the train
	private int fTrainState;
	private float fNextTrainThink;	
	private GameEntity fTrainTarget;
	private Vec3 fTrainDestination;
	
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

	setAngles(0, 0, 0);
	setSolid(SOLID_BSP);
	
	String s = getSpawnArg("model", null);
	if (s != null)
		setModel(s);

	fSpeed = getSpawnArg("speed", 100);
	fAccel = getSpawnArg("accel", fSpeed);
	fDecel = getSpawnArg("decel", fSpeed);	
	fDmg = getSpawnArg("dmg", 100);
	if ((fSpawnFlags & TRAIN_BLOCK_STOPS) != 0)
		fDmg = 0;

	String noise = getSpawnArg("noise", null);		
	if (noise != null)
		fSoundMiddle = Engine.soundIndex(noise);
		
	linkEntity();	
	
	if (fTargets == null)
		{
		PrintManager.dprint("func_train without a target at " + getAbsMins() + "\n");
		fTrainState = STATE_TRAIN_STOPPED;
		fNextTrainThink = 0;
		}
	else
		{
		fTrainState = STATE_TRAIN_SPAWNED;
		fNextTrainThink = (float)(Game.gGameTime + Engine.SECONDS_PER_FRAME);
		}		
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
//			PrintManager.dprint("train_next: no next target\n");
			fTrainState = STATE_TRAIN_STOPPED;
			fNextTrainThink = 0;
			return;
			}

		fTargets = fTrainTarget.fTargets;

		// check for a teleport path_corner
		if ((fTrainTarget.fSpawnFlags & 1) == 0)
			break;  // out if while(true) loop
		else				
			{
			if (!first)
				{
				PrintManager.dprint("connected teleport path_corners, see " + fTrainTarget.getClass().getName() + " at " + fTrainTarget.getOrigin() + "\n");
				return;
				}
			first = false;
			setOrigin(fTrainTarget.getOrigin().subtract(getMins()));
//			VectorCopy (self->s.origin, self->s.old_origin);
			linkEntity();
			}				
		}
		
	fWait = fTrainTarget.getSpawnArg("wait", 0.0F);
	fTrainDestination = fTrainTarget.getOrigin();
	fTrainDestination.subtract(getMins());
		
	moveTo(fTrainDestination);	
	fTrainState = STATE_TRAIN_MOVING;
	fNextTrainThink = 0;
	
	if (!isGroupSlave())
		setSound(fSoundMiddle);
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
		fNextTrainThink = (float)(Game.gGameTime + fWait);
		setSound(0);
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
	fNextTrainThink = 0;
	}
/**
 * This method was created by a SmartGuide.
 */
public void think() 
	{
	if ((fNextTrainThink > 0) && (Game.gGameTime >= fNextTrainThink))
		{
		switch (fTrainState)
			{
			case STATE_TRAIN_SPAWNED:
				fTrainTarget = getRandomTarget();
				setOrigin(fTrainTarget.getOrigin().subtract(getMins()));
				linkEntity();
				fTargets = fTrainTarget.fTargets;
				
				if ((fTargetGroup == null) || ((fSpawnFlags & TRAIN_START_ON) != 0))
					go();
				else
					{
					fTrainState = STATE_TRAIN_STOPPED;
					fNextTrainThink = 0;
					}					
				break;
				
			case STATE_TRAIN_WAITING:
				go();
				break;
			}
		}

	super.think();
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