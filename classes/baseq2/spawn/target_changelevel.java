
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class target_changelevel extends GameObject
	{
	
public target_changelevel(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	GameModule.setNextMap(getSpawnArg("map", null));
	}
}