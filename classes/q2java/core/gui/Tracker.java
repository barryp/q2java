package q2java.core.gui;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.gui.*;

/**
 * Base class for active objects that track entities in the 
 * quake world and update some indicator on the HUD by themselves.
 *
 * @author Barry Pederson
 */
 
public abstract class Tracker implements CrossLevel, ServerFrameListener
	{
	protected FloatIndicator fIndicator;
	protected NativeEntity fOwnerEntity;
	protected NativeEntity fTargetEntity;
	private boolean fIsRunning;
	
/**
 * Create a Tracker widget the works with a given HUD indicator.
 * @param fi a HUD indicator widget
 */
public Tracker(FloatIndicator fi)
	{
	fIndicator = fi;
	fOwnerEntity = fi.getOwner();
	}
/**
 * Disassociate the tracker from the game.
 */
public void dispose() 
	{
	if (fIsRunning)
		Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);	
	}
/**
 * Get which entity we're tracking.
 * @return q2java.NativeEntity
 */
public NativeEntity getTarget() 
	{
	return fTargetEntity;
	}
/**
 * Is this Tracker widget visible?
 * @return boolean
 */
public boolean isVisible() 
	{
	return fIndicator.isVisible();
	}
/**
 * Update the Tracker.
 * @param phase int
 */
public abstract void runFrame(int phase); 
/**
 * Set which entity in the world to track.
 * @param target Entity you want to track, null value disables tracker.
 */
public void setTarget(NativeEntity target) 
	{		
	fTargetEntity = target;
	
	if (target == null)
		fIndicator.setValue(Float.NaN);
		
	updateRunState();
	}
/**
 * Override parent setVisible() to also turn frames off and on.
 * @param b boolean
 */
public void setVisible(boolean visible) 
	{
	fIndicator.setVisible(visible);

	updateRunState();
	}
/**
 * Turn frames off and on as needed.
 * @return 
 */
private void updateRunState() 
	{
	// should frames be turned on?
	if (!fIsRunning && (fTargetEntity != null) && fIndicator.isVisible())
		{
		Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
		fIsRunning = true;
		}

	// should frames be turned off?
	if (fIsRunning && ((fTargetEntity == null) || !fIndicator.isVisible()))
		{
		Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
		fIsRunning = false;
		}
	}
}