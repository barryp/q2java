
package baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Rotating doors that swing on a hinge of some sort.
 * 
 */
public class func_door_rotating extends Door 
	{
	// angular movement parameters
	protected Angle3f fClosedAngle;
	protected Angle3f fOpenedAngle;
	
/**
 * func_door_rotating constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public func_door_rotating(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
					
	fEntity.setAngles(0, 0, 0);

	fMoveDistance = getSpawnArg("distance", 90);	
	fClosedAngle = new Angle3f();
	
	// set the axis of rotation
	fOpenedAngle = new Angle3f();
	if ((fSpawnFlags & DOOR_X_AXIS) != 0)
		fOpenedAngle.z = 1;
	else if ((fSpawnFlags & DOOR_Y_AXIS) != 0)
		fOpenedAngle.x = 1;
	else // Z_AXIS
		fOpenedAngle.y = 1;	

	// check for reverse rotation
	if ((fSpawnFlags & DOOR_REVERSE) != 0)
		fOpenedAngle.negate();		
		
	fOpenedAngle.scale(fMoveDistance);

	if ((fSpawnFlags & DOOR_START_OPEN) != 0)
		{
		fDoorStateInitial = STATE_DOOR_OPENED;
		fEntity.setAngles(fOpenedAngle);
		setPortals(true);
		}
	else		
		{
		fDoorStateInitial = STATE_DOOR_CLOSED;
		setPortals(false);
		}
		
	if (fDoorState != STATE_DOOR_SPAWNTRIGGER)
		fDoorState = fDoorStateInitial;
				
	if ((fSpawnFlags & 16) != 0)
		fEntity.setEffects(NativeEntity.EF_ANIM_ALL);
		
	fEntity.linkEntity();		
	}
/**
 * Move the door to its closed position.
 */
protected void moveClose() 
	{
	rotateTo(fClosedAngle);
	}
/**
 * Move the door to its opened position.
 */
protected void moveOpen() 
	{
	rotateTo(fOpenedAngle);
	}
}