package q2java.core.gui;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.gui.*;

/**
 * Extend a BarGraph to be an active component that
 * shows the range to a given target on its own.
 *
 * @author Barry Pederson
 */
 
public class RangeTracker extends Tracker
	{
	private Vector3f fTempVector = new Vector3f();
	
/**
 * Create a Tracker widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param boundStat which playerStat to set to alter the icon displayed by the tracker
 */
public RangeTracker(NativeEntity owner, int boundStat) 
	{
	super(new BarGraph(owner, boundStat));
	}
/**
 * Get the maximum value of the RangeTracker BarGraph.
 */
public float getMaxValue() 
	{
	return ((BarGraph)fIndicator).getMaxValue();
	}
/**
 * Get the minimum value of the RangeTracker BarGraph.
 */
public float getMinValue() 
	{
	return ((BarGraph)fIndicator).getMinValue();
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	// have the dumb widget handle caching the images	
	BarGraph.precacheImages();
	}
/**
 * Update the Tracker.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	fTempVector.sub(fTargetEntity.getOrigin(), fOwnerEntity.getOrigin());			
	fIndicator.setValue(fTempVector.length());
	}
/**
 * Set the maximum value on the RangeTracker BarGraph.
 * @param f float
 */
public void setMaxValue(float f) 
	{
	((BarGraph)fIndicator).setMaxValue(f);
	}
/**
 * Set the minimum value on the RangeTracker BarGraph.
 * @param f float
 */
public void setMinValue(float f) 
	{
	((BarGraph)fIndicator).setMinValue(f);
	}
}