
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_door extends GameEntity
	{
	private int fState;
	private Vec3 fCurrentPos;
	private Vec3 fClosedPos;
	private Vec3 fOpenPos;
	private Vec3 fMoveDir;
	private float fCloseTime;
	private float fOpenAmount;
	private int fLip; //?
	
	private final static int STATE_CLOSED = 0;
	private final static int STATE_OPENING = 1;
	private final static int STATE_OPENED = 2;
	private final static int STATE_CLOSING = 3;
	
public func_door(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	setSolid(SOLID_BSP);
	
	String s = getSpawnArg("model");
	if (s != null)
		setModel(s);
		
	s = getSpawnArg("lip");
	if (s != null)
		fLip = Integer.parseInt(s);
	else 
		fLip = 8;	
					
	// setup for opening and closing
	fClosedPos = getOrigin();
	fCurrentPos = getOrigin();
	fOpenAmount = 0;
	setMoveDir();
	Vec3 absMoveDir = (new Vec3(fMoveDir)).abs();
	Vec3 size = getSize();
	
	float dist = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - fLip;
	fMoveDir.scale(dist);
	fOpenPos = (new Vec3(fClosedPos)).add(fMoveDir);	

	// create a trigger for this door
	new DoorTrigger(this);

	linkEntity();		
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
 */
private void setMoveDir() 
	{
	Vec3 angles = getAngles();

	// door goes up	
	if (angles.equals(0, -1, 0))
		{
		fMoveDir = new Vec3(0, 0, 1);
		return;
		}

	// door goes down
	if (angles.equals(0, -2, 0))
		{
		fMoveDir = new Vec3(0, 0, -1);
		return;
		}

	// some other direction?		
	angles.angleVectors(fMoveDir, null, null);
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
public void touch(GenericCharacter touchedBy) 
	{
	if ((fState == STATE_CLOSED) || (fState == STATE_CLOSING))
		fState = STATE_OPENING;
	}
}