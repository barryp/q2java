package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class info_player_coop extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-coop";
	
public info_player_coop(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}