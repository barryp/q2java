
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class info_player_start extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-single";
	
public info_player_start(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}