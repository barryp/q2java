package q2java.baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Rotating doors that swing on a hinge of some sort.
 * 
 * @author Barry Pederson
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

	fMoveDistance = getSpawnArg("distance", 90.0F);	
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

	if ((fSpawnFlags & DOOR_START_OPEN) == 0)
		setPortals(false);
	else
		{
		fEntity.setAngles(fOpenedAngle);
		Angle3f temp = fOpenedAngle;
		fOpenedAngle = fClosedAngle;
		fClosedAngle = temp;
		
		setPortals(true);
		}
		
	fDoorStateInitial = STATE_DOOR_CLOSED;		
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