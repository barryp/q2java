package baseq2;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Abstract class to handle both sliding and rotating doors
 *
 * @author Barry Pederson
 */ 

public abstract class Door extends GenericPusher 
	{	
	// spawn parameters
	protected float fWait;
	protected float fDmg;
	protected int fMaxHealth;
	protected String fMessage;

	//optional trigger field
	protected NativeEntity fTriggerEntity;

	// track the state of the door
	protected int fDoorState;
	protected int fDoorStateInitial;
	protected int fHealth;
	
	// door sounds if any
	protected int fSoundStart;
	protected int fSoundMiddle;
	protected int fSoundEnd;
		
	// door state constants		
	protected final static int STATE_DOOR_SPAWNTRIGGER		= 0;
	protected final static int STATE_DOOR_CLOSING 		= 1;
	protected final static int STATE_DOOR_CLOSED 			= 2;
	protected final static int STATE_DOOR_OPENING 		= 3;
	protected final static int STATE_DOOR_OPENED 			= 4;	
	protected final static int STATE_DOOR_OPENWAIT 		= 5;	
	
	// spawn flags		
	protected final static int DOOR_START_OPEN	= 1;
	protected final static int DOOR_REVERSE		= 2;
	protected final static int DOOR_CRUSHER		= 4;
	protected final static int DOOR_NOMONSTER	= 8;
	protected final static int DOOR_TOGGLE		= 32;
	protected final static int DOOR_X_AXIS		= 64;
	protected final static int DOOR_Y_AXIS		= 128;
	
/**
 * Setup things common to both linear and rotating doors.
 */
public Door(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);

	fSpeed = getSpawnArg("speed", 100.0F);
	fAccel = getSpawnArg("accel", fSpeed);
	fDecel = getSpawnArg("decel", fSpeed);	
	fWait = getSpawnArg("wait", 3.0F);	
	fDmg = getSpawnArg("dmg", 2.0F);
	fHealth = fMaxHealth = getSpawnArg("health", 0);
	fMessage = getSpawnArg("message", null);
	
	// setup door sounds
	if (getSpawnArg("sounds", 0) != 1)
		{
		fSoundStart = Engine.getSoundIndex("doors/dr1_strt.wav");
		fSoundMiddle = Engine.getSoundIndex("doors/dr1_mid.wav");
		fSoundEnd = Engine.getSoundIndex("doors/dr1_end.wav");
		}
							
	if ((fHealth == 0) && (fTargetGroup== null))
		{	
		fDoorState = STATE_DOOR_SPAWNTRIGGER;
		
		// schedule a one-shot notification
		// so we can create an area trigger
		// after everything has been spawned
		Game.addFrameListener(this, 0, -1);
		}
	else
		fDoorState = STATE_DOOR_CLOSED;		
	}
/**
 * Close the door.
 */
public void close() 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_OPENING:
		case STATE_DOOR_OPENWAIT:
		case STATE_DOOR_OPENED:
			fDoorState = STATE_DOOR_CLOSING;
			moveClose();
			if (!isGroupSlave())
				{
				if (fSoundStart != 0)
					fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundStart, 1, NativeEntity.ATTN_STATIC, 0);
				fEntity.setSound(fSoundMiddle);					
				}
			break;			
		}
	}
/**
 * Handle damaging a door, which in some cases opens it.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent) 
	{
	super.damage(inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent, null);

	if (fMaxHealth != 0)
		{
		fHealth -= damage;
		if (fHealth < 0)
			{
			fHealth = fMaxHealth;
			open();
			}
		}
	}
/**
 * Move the door to its closed position.
 */
protected abstract void moveClose( );
/**
 * Called by GenericPusher when the door is finished moving.
 */
public void moveFinished() 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_OPENING:
			if ((fWait <= 0) || ((fSpawnFlags & DOOR_TOGGLE) != 0))
				fDoorState = STATE_DOOR_OPENED;
			else				
				{
				fDoorState = STATE_DOOR_OPENWAIT;

				// schedule a one-time notification fWait seconds from now
				Game.addFrameListener(this, fWait, -1);
				}
			break;			

		case STATE_DOOR_CLOSING:
			fDoorState = STATE_DOOR_CLOSED;
			setPortals(false);
			break;			
		}
		
	if (!isGroupSlave())
		{
		if (fSoundEnd != 0)
			fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundEnd, 1, NativeEntity.ATTN_STATIC, 0);
		fEntity.setSound(0);			
		}		
	}
/**
 * Move the door to its opened position.
 */
protected abstract void moveOpen(); 
/**
 * Open the door.
 */
public void open() 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_CLOSED:
			setPortals(true);
			// fall through to next case
		case STATE_DOOR_CLOSING:
			fDoorState = STATE_DOOR_OPENING;
			moveOpen();
			if (!isGroupSlave())
				{
				if (fSoundStart != 0)
					fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundStart, 1, NativeEntity.ATTN_STATIC, 0);
				fEntity.setSound(fSoundMiddle);
				}
			break;	
			
		case STATE_DOOR_OPENWAIT:
			// reschedule the notification that's outstanding
			Game.addFrameListener(this, fWait, -1);
			break;				
		}
	}
/**
 * Think about the door's state and what to do next.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_SPAWNTRIGGER:
			spawnDoorTrigger();
			fDoorState = fDoorStateInitial;
			super.runFrame(phase); // the generic pusher needs to initialize some stuff too.		
			break;
				
		case STATE_DOOR_OPENWAIT:
			close();
			break;
			
		default:
			super.runFrame(phase);	
		}		
	}
/**
 * Spawn an areatrigger to cause the door (and its groupmates)
 * to open when somebody steps near it.
 */
protected void spawnDoorTrigger() 
	{
	if (isGroupSlave())
		return;		// only the team leader spawns a trigger

	Tuple3f mins = fEntity.getAbsMins();
	Tuple3f maxs = fEntity.getAbsMaxs();

	if (fGroup != null)	
		{
		for (int i = 1; i < fGroup.size(); i++)
			{
			NativeEntity ge = ((GameObject) fGroup.elementAt(i)).fEntity;
			MiscUtil.addPointToBounds(ge.getAbsMins(), mins, maxs);
			MiscUtil.addPointToBounds(ge.getAbsMaxs(), mins, maxs);
			}
		}	
		
	// expand 
	mins.x -= 60;
	mins.y -= 60;
	maxs.x += 60;
	maxs.y += 60;

	try
		{
		fTriggerEntity = new NativeEntity();
		fTriggerEntity.setReference(this);
	
		fTriggerEntity.setMins(mins);
		fTriggerEntity.setMaxs(maxs);
	
		fTriggerEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fTriggerEntity.linkEntity();
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}			
	}
/**
 * React to a touch if we have an area trigger.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	if (fTriggerEntity != null)
		use(touchedBy);
	}
/**
 * Use the door by opening it.
 * @param touchedBy The player who is causing the door to open (may be null).
 */
public void use(Player touchedBy) 
	{
	if (isGroupSlave())
		return;

	if (((fSpawnFlags & DOOR_TOGGLE) != 0) && (fDoorState == STATE_DOOR_OPENED))
		{
		close();
		// if this is the group master, then also close slaves
		if (fGroup != null)
			{
			for (int i = 1; i < fGroup.size(); i++)
				((Door)fGroup.elementAt(i)).close();
			}
		}
	else
		{
		open();
		// if this is the group master, then also open slaves
		if (fGroup != null)
			{
			for (int i = 1; i < fGroup.size(); i++)
				((Door)fGroup.elementAt(i)).open();
			}
		}	
	}
}