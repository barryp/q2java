
package baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class func_door extends GenericPusher 
	{	
	// spawn parameters
	private float fWait;
	private float fDmg;
	private int fMaxHealth;
	private String fMessage;

	// private movement parameters
	private Point3f fClosedOrigin;
	private Point3f fOpenedOrigin;

	// track the state of the door
	private int fDoorState;
	private int fDoorStateInitial;
	private int fHealth;
	
	// door sounds if any
	private int fSoundStart;
	private int fSoundMiddle;
	private int fSoundEnd;
		
	// door state constants		
	private final static int STATE_DOOR_SPAWNTRIGGER = 0;
	private final static int STATE_DOOR_CLOSING = 1;
	private final static int STATE_DOOR_CLOSED = 2;
	private final static int STATE_DOOR_OPENING = 3;
	private final static int STATE_DOOR_OPENED = 4;	
	private final static int STATE_DOOR_OPENWAIT = 5;	
	
	// spawn flags		
	private final static int DOOR_START_OPEN		= 1;
	private final static int DOOR_REVERSE		= 2;
	private final static int DOOR_CRUSHER		= 4;
	private final static int DOOR_NOMONSTER		= 8;
	private final static int DOOR_TOGGLE			= 32;
	private final static int DOOR_X_AXIS			= 64;
	private final static int DOOR_Y_AXIS			= 128;
	
public func_door(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);

	fSpeed = getSpawnArg("speed", 100);
	fAccel = getSpawnArg("accel", fSpeed);
	fDecel = getSpawnArg("decel", fSpeed);	
	fWait = getSpawnArg("wait", 3);	
	fDmg = getSpawnArg("dmg", 2);
	int lip = getSpawnArg("lip", 8);
	fHealth = fMaxHealth = getSpawnArg("health", 0);
	fMessage = getSpawnArg("message", null);
	
	// setup door sounds
	if (getSpawnArg("sounds", 0) != 1)
		{
		fSoundStart = Engine.getSoundIndex("doors/dr1_strt.wav");
		fSoundMiddle = Engine.getSoundIndex("doors/dr1_mid.wav");
		fSoundEnd = Engine.getSoundIndex("doors/dr1_end.wav");
		}
							
	// setup for opening and closing
	fClosedOrigin = fEntity.getOrigin();
	Vector3f moveDir = getMoveDir();

	Vector3f absMoveDir = new Vector3f(moveDir);
	absMoveDir.absolute();
	Tuple3f size = fEntity.getSize();
	
	fMoveDistance = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - lip;
	fOpenedOrigin = new Point3f();
	fOpenedOrigin.scaleAdd(fMoveDistance, moveDir, fClosedOrigin);
	
	if ((fSpawnFlags & DOOR_START_OPEN) != 0)
		{
		fDoorStateInitial = fDoorState = STATE_DOOR_OPENED;
		fEntity.setOrigin(fOpenedOrigin);
		setPortals(true);
		}
	else		
		{
		fDoorStateInitial = fDoorState = STATE_DOOR_CLOSED;
		setPortals(false);
		}
		
	int effect = 0;
	if ((fSpawnFlags & 16) != 0)
		effect |= NativeEntity.EF_ANIM_ALL;
	if ((fSpawnFlags & 64) != 0)
		effect |= NativeEntity.EF_ANIM_ALLFAST;
	if (effect != 0)
		fEntity.setEffects(effect);
	fEntity.linkEntity();		

	if ((fHealth == 0) && (fTargetGroup== null))
		{		
		fDoorState = STATE_DOOR_SPAWNTRIGGER;
		
		// schedule a one-shot notification
		// so we can create an area trigger
		// after everything has been spawned
		Game.addFrameListener(this, 0, -1);
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void close() 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_OPENING:
		case STATE_DOOR_OPENWAIT:
		case STATE_DOOR_OPENED:
			fDoorState = STATE_DOOR_CLOSING;
			moveTo(fClosedOrigin);
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
 * This method was created by a SmartGuide.
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
	super.damage(inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent);
	
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
 * This method was created by a SmartGuide.
 */
public void moveFinished() 
	{
	switch (fDoorState)
		{
		case STATE_DOOR_OPENING:
			if (fWait <= 0)
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
 * This method was created by a SmartGuide.
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
			moveTo(fOpenedOrigin);
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
 * This method was created by a SmartGuide.
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
 * This method was created by a SmartGuide.
 * @param isOpen boolean
 */
protected void setPortals(boolean state) 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)		
		{
		Object obj = fTargets.elementAt(i);
		if (obj instanceof func_areaportal)
			((func_areaportal) obj).setPortal(state);
		}
	}
/**
 * This method was created by a SmartGuide.
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
		new AreaTrigger(this, mins, maxs);
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}			
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	open();
	
	// if this is the group master, then also trigger the slaves
	if (!isGroupSlave() && (fGroup != null))
		{
		for (int i = 1; i < fGroup.size(); i++)
			((func_door)fGroup.elementAt(i)).use(touchedBy);
		}
	}
}