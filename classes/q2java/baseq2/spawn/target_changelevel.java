package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Cause level changes in single-player mode
 *
 * @author Barry Pederson
 */

public class target_changelevel extends Trigger
	{
	protected String fMap;
	
public target_changelevel(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fMap = GameUtil.getSpawnArg(spawnArgs, "map", null);
	}
/**
 * Switch to next map if not in DM mode
 * @param usedBy baseq2.Player
 */
public void use(Player usedBy) 
	{	
	if (!BaseQ2.gIsDeathmatch)
		Engine.addCommandString("gamemap \"" + fMap + "\"\n");	
	}
}