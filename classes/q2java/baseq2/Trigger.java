package q2java.baseq2;

import java.util.Vector;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Superclass for various trigger entities
 */

public abstract class Trigger implements GameTarget
	{
	protected Vector fTargets;
	
public Trigger(String[] spawnArgs) throws GameException
	{
	BaseQ2.checkInhibited(spawnArgs);
	
	String s = GameUtil.getSpawnArg(spawnArgs, "target", null);
	if (s != null)
		fTargets = Game.getLevelRegistryList("target-" + s);
		
	s = GameUtil.getSpawnArg(spawnArgs, "targetname", null);
	if (s != null)
		Game.addLevelRegistry("target-" + s, this);	
	}
/**
 * Activate this trigger.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy)
	{
	}
/**
 * Use the objects targeted by this one.
 */
public void useTargets() 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)
		((GameTarget) fTargets.elementAt(i)).use(null);
	}
}