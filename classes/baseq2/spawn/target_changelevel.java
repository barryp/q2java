package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Cause level changes in single-player mode
 *
 * @author Barry Pederson
 */

public class target_changelevel extends Trigger
	{
	protected String fMap;
	
public target_changelevel(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fMap = Game.getSpawnArg(spawnArgs, "map", null);
	}
/**
 * Switch to next map if not in DM mode
 * @param usedBy baseq2.Player
 */
public void use(Player usedBy) 
	{	
	if (!baseq2.GameModule.gIsDeathmatch)
		Engine.addCommandString("gamemap \"" + fMap + "\"\n");	
	}
}