package q2java.core.gui;

import q2java.NativeEntity;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.gui.*;

/**
 * Runs a count down timer on the players hud.
 * @author Brian Haskin
 */
public class IconCountdownTimer extends IconIntegerDisplay implements ServerFrameListener
	{
	boolean fIsRunning;
	
/**
 * Create a Timer and bind it to a player.
 * @param q2java.NativeEntity - Owner
 * @param int iconStat - PlayerStat for Icon
 * @param int iconIndex - Index of icon image
 * @param int timeStat - PlayerStat for time
 * @param int time - Starting time (seconds)
 */
public IconCountdownTimer(NativeEntity owner, int iconStat, int iconIndex, int timeStat, int time)
	{
	super(owner, iconStat, iconIndex, timeStat, time);
	}
/**
 * get whether the timer is running or not.
 * @return boolean
 */
public boolean isRunning()
	{
	return fIsRunning;
	}
/**
 * do all our processing for a frame.
 */
public void runFrame(int Phase)
	{
	int time = getValue() - 1;
	if (time < 0)
		{
		// disable and hide the timer
		setRunning(false);
		setVisible(false);
		}

	// update the displayed value
	setValue(time);
	}
/**
 * Start/stop timer.
 */
public void setRunning(boolean b)	
	{
	if (b == fIsRunning)
		return;  // no change in state
		
	fIsRunning = b;
	
	if (fIsRunning)
		Game.addServerFrameListener(this, 1, 1);
	else
		Game.removeServerFrameListener(this);
	}
}