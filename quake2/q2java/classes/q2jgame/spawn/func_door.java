
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_door extends GenericPusher
	{	
	private boolean fNeedTrigger;
	
	private final static int STATE_CLOSE = 1;
	private final static int STATE_OPEN = 2;		
			
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
							
	// setup for opening and closing
	fStartOrigin = getOrigin();
	Vec3 moveDir = getMoveDir();

	Vec3 absMoveDir = (new Vec3(moveDir)).abs();
	Vec3 size = getSize();
	
	fMoveDistance = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - lip;
	fEndOrigin = fStartOrigin.vectorMA(fMoveDistance, moveDir);

	if ((fSpawnFlags & DOOR_START_OPEN) == 0)
		{
		setPortals(false);
		}
	else		
		{
		// if it starts open, switch the positions
		Vec3 temp = fStartOrigin;
		fStartOrigin = fEndOrigin;
		fEndOrigin = temp;
		setOrigin(fStartOrigin);
		setPortals(true);
		}

		
	int effect = 0;
	if ((fSpawnFlags & 16) != 0)
		effect |= EF_ANIM_ALL;
	if ((fSpawnFlags & 64) != 0)
		effect |= EF_ANIM_ALLFAST;
	if (effect != 0)
		setEffects(effect);
	linkEntity();		

	fNeedTrigger = true;
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
protected void hitBottom() 
	{
	setPortals(false);
	}
/**
 * This method was created by a SmartGuide.
 */
protected void hitTop() 
	{
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
	if (fNeedTrigger)
		{
		spawnDoorTrigger();
		fNeedTrigger = false;
		}
		
	super.think();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	if (open())
		setPortals(true);
	}
}