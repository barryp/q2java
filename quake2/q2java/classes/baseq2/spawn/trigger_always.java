
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class trigger_always extends GameEntity
	{
	private String fMessage;
	private float fDelay;
	
	private float fNextThink;
	
public trigger_always(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	fNextThink = (float)(Game.gGameTime + getSpawnArg("delay", 0.2F));
	fMessage = getSpawnArg("message", null);
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	if ((fNextThink > 0) && (Game.gGameTime >= fNextThink))
		{
		useTargets();
		freeEntity();
		}
	}
}