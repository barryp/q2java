package q2java.baseq2.spawn;

import java.util.Vector;
import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**  
 *  func_timer objects wait a bit after being triggered, and
 *  then turn around and trigger other entities.
 *
 * @author Barry Pederson (I think)
 */

public class func_timer implements GameTarget, FrameListener
	{
	private float fWait;
	private float fRandom;
	private float fDelay;
	private float fPauseTime;	
	
	private boolean fIsOn;
	
	private Vector fTargets;	
	
public func_timer(String[] spawnArgs) throws GameException
	{
	BaseQ2.checkInhibited(spawnArgs);
		
	fWait      = GameUtil.getSpawnArg(spawnArgs, "wait",      1f );
	fRandom    = GameUtil.getSpawnArg(spawnArgs, "random",    0f );
	fDelay     = GameUtil.getSpawnArg(spawnArgs, "delay",     0f );
	fPauseTime = GameUtil.getSpawnArg(spawnArgs, "pausetime", 0f );

	if (fRandom >= fWait)
		fRandom = fWait - Engine.SECONDS_PER_FRAME;

	if ((GameUtil.getSpawnArg(spawnArgs, "spawnflags", 0) & 1) != 0)
		{
		Game.addFrameListener(this, 1 + fPauseTime + fDelay + fWait + (float)GameUtil.cRandom() * fRandom, -1);
		fIsOn = true;
		}

	String s = GameUtil.getSpawnArg(spawnArgs, "target", null);
	if (s != null)
		fTargets = Game.getLevelRegistryList("target-" + s);
		
	s = GameUtil.getSpawnArg(spawnArgs, "targetname", null);
	if (s != null)
		Game.addLevelRegistry("target-" + s, this);
	}
public void runFrame(int phase) 
	{
	useTargets();
	
	// schedule another call
	Game.addFrameListener(this, fWait + (float)GameUtil.cRandom() * fRandom, -1);
	}
/**
 * Trigger the timer.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	if (fIsOn)
		// turn it off
		Game.removeFrameListener(this);
	else
		{
		// turn it on
		if (fDelay > 0 )
			Game.addFrameListener(this, fDelay, -1);
		else
			runFrame(0);
		}
		
	// flip the state			
	fIsOn = !fIsOn;
	}
/**
 * This method was created by a SmartGuide.
 */
public void useTargets() 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)
		((GameTarget) fTargets.elementAt(i)).use(null);
	}
}