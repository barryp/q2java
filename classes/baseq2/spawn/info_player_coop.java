package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class info_player_coop extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-coop";
	
public info_player_coop(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}