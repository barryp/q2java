package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class info_player_intermission extends GenericSpawnpoint 
	{
	public final static String REGISTRY_KEY = "spawn-intermission";
	
public info_player_intermission(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}