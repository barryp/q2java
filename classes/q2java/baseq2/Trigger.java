package q2java.baseq2;

import java.util.Vector;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Superclass for various trigger entities
 */

public abstract class Trigger implements GameTarget
	{
	protected Vector fTargets;
	
public Trigger(Element spawnArgs) throws GameException
	{
	BaseQ2.checkInhibited(spawnArgs);
	
	String s = GameUtil.getSpawnArg(spawnArgs, "target", "id", null);
	if (s != null)
		fTargets = Game.getLevelRegistryList("target-" + s);
		
	s = GameUtil.getSpawnArg(spawnArgs, "targetname", "id", null);
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