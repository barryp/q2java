
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class trigger_relay extends GameEntity
	{
	private String fMessage;
	private float fDelay;
	
	private float fNextThink;
	
public trigger_relay(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	fDelay = getSpawnArg("delay", 0.0F);
	fMessage = getSpawnArg("message", null);
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	if ((fNextThink > 0) && (Game.gGameTime >= fNextThink))
		{
		fNextThink = 0;
		useTargets();
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	if (fDelay <= 0)
		useTargets();
	else
		fNextThink = (float)(Game.gGameTime + fDelay);		
	}
}