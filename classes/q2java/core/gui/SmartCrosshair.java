package q2java.core.gui;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.gui.*;

/**
 * Manage HUD Crosshair icons that light up when a particular 
 * entity is in the line of fire.
 *
 * @author Barry Pederson
 */
 
public class SmartCrosshair extends Tracker
	{
	// max range at which the target is detected
	protected float fRange = 8192; // start off with default value large enough to cover entire maps
	
	// temporary objects
	private Point3f fTempPoint = new Point3f();
	private Vector3f fTempVector = new Vector3f();
	
/**
 * This method was created in VisualAge.
 * @param owner q2java.NativeEntity
 * @param boundStat int
 */
public SmartCrosshair(NativeEntity owner, int boundStat) 
	{
	super(new AnimatedCrosshair(owner, boundStat));
	}
/**
 * Get how far away the target can be before the crosshair stops responding.
 * @param f float
 */
public float getRange() 
	{
	return fRange;
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	AnimatedCrosshair.precacheImages();
	}
/**
 * Update the Crosshair.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	// trace straight ahead 
	Point3f start = fOwnerEntity.getOrigin();
	start.add(fOwnerEntity.getPlayerViewOffset());

	Angle3f ang = fOwnerEntity.getPlayerViewAngles();
	ang.getVectors(fTempVector, null, null);

	fTempPoint.scaleAdd(fRange, fTempVector, start);

	TraceResults tr = Engine.trace(start, fTempPoint, fOwnerEntity, Engine.MASK_SHOT|Engine.CONTENTS_SLIME|Engine.CONTENTS_LAVA);

	// if the trace hit the entity we're looking for, update the crosshair
	if (tr.fEntity == fTargetEntity)
		fIndicator.setValue(Game.getGameTime());
	else
		fIndicator.setValue(Float.NaN);
	}
/**
 * Set how far away the target can be before the crosshair stops responding.
 * @param f float
 */
public void setRange(float f) 
	{
	fRange = f;
	}
}