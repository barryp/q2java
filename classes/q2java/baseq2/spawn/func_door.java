package baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Doors that slide open (like in a grocery store)
 *
 * @author Barry Pederson
 */

public class func_door extends Door 
	{	
	// linear movement parameters
	protected Point3f fClosedOrigin;
	protected Point3f fOpenedOrigin;
	
public func_door(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	float lip = getSpawnArg("lip", 8.0F);
							
	// setup for opening and closing
	fClosedOrigin = fEntity.getOrigin();
	Vector3f moveDir = MiscUtil.calcMoveDir(fEntity.getAngles());
	fEntity.setAngles(0, 0, 0);

	Vector3f absMoveDir = new Vector3f(moveDir);
	absMoveDir.absolute();
	Tuple3f size = fEntity.getSize();
	
	fMoveDistance = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - lip;
	fOpenedOrigin = new Point3f();
	fOpenedOrigin.scaleAdd(fMoveDistance, moveDir, fClosedOrigin);
	
	if ((fSpawnFlags & DOOR_START_OPEN) == 0)
		setPortals(false);
	else		
		{
		fEntity.setOrigin(fOpenedOrigin);
		Point3f temp = fOpenedOrigin;
		fOpenedOrigin = fClosedOrigin;
		fClosedOrigin = temp;
		setPortals(true);
		}
		
	fDoorStateInitial = STATE_DOOR_CLOSED;
	if (fDoorState != STATE_DOOR_SPAWNTRIGGER)
		fDoorState = fDoorStateInitial;
			
	int effect = 0;
	if ((fSpawnFlags & 16) != 0)
		effect |= NativeEntity.EF_ANIM_ALL;
	if ((fSpawnFlags & 64) != 0)
		effect |= NativeEntity.EF_ANIM_ALLFAST;
	if (effect != 0)
		fEntity.setEffects(effect);
		
	fEntity.linkEntity();		
	}
/**
 * Move the door to its closed position.
 */
protected void moveClose() 
	{
	moveTo(fClosedOrigin);
	}
/**
 * Move the door to its opened position.
 */
protected void moveOpen() 
	{
	moveTo(fOpenedOrigin);
	}
}