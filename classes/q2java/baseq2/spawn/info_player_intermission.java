package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class info_player_intermission extends GenericSpawnpoint 
	{
	public final static String REGISTRY_KEY = "spawn-intermission";
	
public info_player_intermission(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}