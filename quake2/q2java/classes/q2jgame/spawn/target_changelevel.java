
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class target_changelevel extends GameEntity
	{
	
public target_changelevel(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	Game.setNextMap(getSpawnArg("map", null));
	}
}