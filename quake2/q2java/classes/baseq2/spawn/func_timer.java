
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

/**  
 *  func_timer objects wait a bit after being triggered, and
 *  then turn around and trigger other entities.
 *
 *  @author M. van Gangelen &ltmenno@element.nl&gt
 */

public class func_timer extends GameEntity
	{
	private float fWait;
	private float fRandom;
	private float fDelay;
	private float fPauseTime;
	
	private float fNextThink;	
	
public func_timer( String[] spawnArgs ) throws GameException
	{
	super(spawnArgs);

	fWait      = getSpawnArg( "wait",      1f );
	fRandom    = getSpawnArg( "random",    0f );
	fDelay     = getSpawnArg( "delay",     0f );
	fPauseTime = getSpawnArg( "pausetime", 0f );

	//self->use = func_timer_use;
	//self->think = func_timer_think;

	if (fRandom >= fWait)
		{
		fRandom = fWait - Engine.SECONDS_PER_FRAME;
//		PrintManager.dprint( "func_timer at " + getOrigin() + " has random >= wait\n" );
		}

	if ( (fSpawnFlags & 1) != 0 )
		{
		fNextThink = Game.gGameTime + 1 + fPauseTime + fDelay + fWait + (float)(Game.cRandom() * fRandom);
		//self->activator = self;
		}

	//self->svflags = SVF_NOCLIENT;
	}
public void runFrame() 
	{
	if ((fNextThink > 0) && (Game.gGameTime >= fNextThink))
		{
		fNextThink = Game.gGameTime + fWait + (float)(Game.cRandom() * fRandom);
		useTargets();
		}
	}
/**
 * Trigger the timer.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	// if on, turn it off
	if ( fNextThink > 0 )
		{
		fNextThink = 0;
		return;
		}

	// turn it on
	if (fDelay > 0 )
		fNextThink = Game.gGameTime + fDelay;
	else
		runFrame();
	}
}