
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_door extends GameEntity
	{
	// possible spawn args
	private int fLip; 
	private int fSpeed;
	private int fAccel;
	private int fDecel;
	private int fWait;
	private int fDmg;
	
	private int fState;
	private Vec3 fCurrentPos;
	private Vec3 fClosedPos;
	private Vec3 fOpenPos;
	private Vec3 fMoveDir;
	private float fCloseTime;
	private float fOpenAmount;
	
	private final static int STATE_CLOSED	= 0;
	private final static int STATE_OPENING	= 1;
	private final static int STATE_OPENED	= 2;
	private final static int STATE_CLOSING	= 3;
	
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
	fLip = getSpawnArg("lip", 8);
	fDmg = getSpawnArg("dmg", 2);
					
	// setup for opening and closing
	fClosedPos = getOrigin();
	fCurrentPos = getOrigin();
	fOpenAmount = 0;
	fMoveDir = getMoveDir();

	Vec3 absMoveDir = (new Vec3(fMoveDir)).abs();
	Vec3 size = getSize();
	
	float dist = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - fLip;
	fMoveDir.scale(dist);
	fOpenPos = (new Vec3(fClosedPos)).add(fMoveDir);	

	// if it starts open, switch the positions
	if ((fSpawnFlags & DOOR_START_OPEN) != 0)
		{
		Vec3 temp = fClosedPos;
		fClosedPos = fOpenPos;
		fOpenPos = temp;
		setOrigin(fOpenPos);
		}

	int effect = 0;
	if ((fSpawnFlags & 16) != 0)
		effect |= EF_ANIM_ALL;
	if ((fSpawnFlags & 64) != 0)
		effect |= EF_ANIM_ALLFAST;
	if (effect != 0)
		setEffects(effect);

	// create a trigger for this door
	new DoorTrigger(this);

	linkEntity();		
	}
/**
 * Get the direction the door should move.
 * When the entity is spawned, the "angles"
 * indicates the direction the door should move,
 * but the format is a little bizarre, and the
 * value doesn't actually indicate the angles
 * for the entity, so we -have- to clear it,
 * once we get a copy, otherwise the doors
 * will appear on the maps in all sorts of odd positions.
 * @return a Vec3 pointing in the direction the door opens.
 */
private Vec3 getMoveDir() 
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
public void runFrame() 
	{
//		ent->moveinfo.sound_middle = gi.soundindex  ("doors/dr1_mid.wav");

	switch (fState)
		{
		case STATE_CLOSED: break;
		case STATE_OPENED:
			if (Game.fGameTime >= fCloseTime)		
				fState = STATE_CLOSING;
			break;
		
		case STATE_OPENING:
			// are we just starting to open?
			if (fOpenAmount == 0)
				{
				setPortals(true);
				sound(CHAN_NO_PHS_ADD+CHAN_VOICE, Engine.soundIndex("doors/dr1_strt.wav"), 1, ATTN_STATIC, 0);				
				}
				
			fOpenAmount += 0.1;
			if (fOpenAmount < 1)
				setOrigin(fClosedPos.vectorMA(fOpenAmount, fMoveDir));
			else				
				{
				setOrigin(fOpenPos);
				fState = STATE_OPENED;
				fOpenAmount = 1.0F;
				fCloseTime = (float)(Game.fGameTime + 5);  // close the door in 5 seconds
				}
			break;
			
		case STATE_CLOSING:
			fOpenAmount -= 0.1;
			if (fOpenAmount > 0)
				setOrigin(fClosedPos.vectorMA(fOpenAmount, fMoveDir));
			else
				{
				setOrigin(fClosedPos);				
				fState = STATE_CLOSED;
				fOpenAmount = 0;
				setPortals(false);
				sound(CHAN_NO_PHS_ADD+CHAN_VOICE, Engine.soundIndex("doors/dr1_end.wav"), 1, ATTN_STATIC, 0);				
				}
			break;			
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param isOpen boolean
 */
private void setPortals(boolean state) 
	{
	Enumeration enum = enumerateTargets();
	while (enum.hasMoreElements())
		{
		Object obj = enum.nextElement();
		if (obj instanceof func_areaportal)
			((func_areaportal) obj).setPortal(state);
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	if ((fState == STATE_CLOSED) || (fState == STATE_CLOSING))
		fState = STATE_OPENING;
	}
}