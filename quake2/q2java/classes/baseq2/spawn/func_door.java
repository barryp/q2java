
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_door extends GenericPusher implements AreaTriggerUser
	{	
	// spawn parameters
	private float fWait;
	private float fDmg;
	private int fMaxHealth;
	private String fMessage;

	// private movement parameters
	private Vec3 fClosedOrigin;
	private Vec3 fOpenedOrigin;

	// track the state of the door
	private int fDoorState;
	private int fDoorStateInitial;
	private float fNextDoorThink;
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
	
	setSolid(SOLID_BSP);
	String s = getSpawnArg("model", null);
	if (s != null)
		setModel(s);

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
		fSoundStart = Engine.soundIndex("doors/dr1_strt.wav");
		fSoundMiddle = Engine.soundIndex("doors/dr1_mid.wav");
		fSoundEnd = Engine.soundIndex("doors/dr1_end.wav");
		}
							
	// setup for opening and closing
	fClosedOrigin = getOrigin();
	Vec3 moveDir = getMoveDir();

	Vec3 absMoveDir = (new Vec3(moveDir)).abs();
	Vec3 size = getSize();
	
	fMoveDistance = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - lip;
	fOpenedOrigin = fClosedOrigin.vectorMA(fMoveDistance, moveDir);

	if ((fSpawnFlags & DOOR_START_OPEN) != 0)
		{
		fDoorStateInitial = fDoorState = STATE_DOOR_OPENED;
		setOrigin(fOpenedOrigin);
		setPortals(true);
		}
	else		
		{
		fDoorStateInitial = fDoorState = STATE_DOOR_CLOSED;
		setPortals(false);
		}
		
	int effect = 0;
	if ((fSpawnFlags & 16) != 0)
		effect |= EF_ANIM_ALL;
	if ((fSpawnFlags & 64) != 0)
		effect |= EF_ANIM_ALLFAST;
	if (effect != 0)
		setEffects(effect);
	linkEntity();		

	if ((fHealth == 0) && (fTargetGroup== null))
		{		
		fDoorState = STATE_DOOR_SPAWNTRIGGER;
		fNextDoorThink = (float)(Game.gGameTime + Engine.SECONDS_PER_FRAME);
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void areaTrigger(Player touchedBy) 
	{
	use(null);
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
					sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundStart, 1, ATTN_STATIC, 0);
				setSound(fSoundMiddle);					
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
public void damage(GameEntity inflictor, GameEntity attacker, 
	Vec3 dir, Vec3 point, Vec3 normal, 
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
				fNextDoorThink = (float)(Game.gGameTime + fWait);
				fDoorState = STATE_DOOR_OPENWAIT;
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
			sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundEnd, 1, ATTN_STATIC, 0);
		setSound(0);			
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
					sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundStart, 1, ATTN_STATIC, 0);
				setSound(fSoundMiddle);
				}
			break;	
			
		case STATE_DOOR_OPENWAIT:
			fNextDoorThink = (float)(Game.gGameTime + fWait);	
			break;				
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param isOpen boolean
 */
private void setPortals(boolean state) 
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
private void spawnDoorTrigger() 
	{
	if (isGroupSlave())
		return;		// only the team leader spawns a trigger

	Vec3 mins = getAbsMins();
	Vec3 maxs = getAbsMaxs();

	if (fGroup != null)	
		{
		for (int i = 1; i < fGroup.size(); i++)
			{
			GameEntity ge = (GameEntity) fGroup.elementAt(i);
			Vec3.addPointToBounds(ge.getAbsMins(), mins, maxs);
			Vec3.addPointToBounds(ge.getAbsMaxs(), mins, maxs);
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
 */
public void think() 
	{
	if ((fNextDoorThink > 0) && (Game.gGameTime >= fNextDoorThink))
		{
		switch (fDoorState)
			{
			case STATE_DOOR_SPAWNTRIGGER:
				spawnDoorTrigger();
				fDoorState = fDoorStateInitial;
				fNextDoorThink = 0;
				break;
				
			case STATE_DOOR_OPENWAIT:
				fNextDoorThink = 0;
				close();
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
	open();
	
	// if this is the group master, then also trigger the slaves
	if (!isGroupSlave() && (fGroup != null))
		{
		for (int i = 1; i < fGroup.size(); i++)
			((func_door)fGroup.elementAt(i)).use(touchedBy);
		}
	}
}