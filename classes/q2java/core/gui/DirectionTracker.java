package q2java.gui;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Extend a DirectionIndicator to be an active component that
 * follows a given target on its own.
 *
 * @author Barry Pederson
 */
 
public class DirectionTracker extends Tracker
	{
	private Angle3f fTempAngle = new Angle3f(); // angle to reuse each frame
	
/**
 * Create a Tracker widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param boundStat which playerStat to set to alter the icon displayed by the tracker
 */
public DirectionTracker(NativeEntity owner, int boundStat) 
	{
	super(new DirectionIndicator(owner, boundStat));
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	// have the dumb widget handle caching the images
	DirectionIndicator.precacheImages();
	}
/**
 * Update the Tracker.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	// get direction to target in quake angles
	fTempAngle.set(fOwnerEntity.getOrigin(), fTargetEntity.getOrigin());

	// update the tracker with the difference between angles
	fIndicator.setValue(fTempAngle.y - fOwnerEntity.getPlayerViewAngles().y);
	}
}