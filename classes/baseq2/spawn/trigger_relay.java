
package baseq2.spawn;

import java.util.Vector;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class trigger_relay implements FrameListener, GameTarget
	{
	private String fMessage;
	private float fDelay;
	private Vector fTargets;
	
public trigger_relay(String[] spawnArgs) throws GameException
	{
	baseq2.GameModule.checkInhibited(spawnArgs);
	
	fDelay = Game.getSpawnArg(spawnArgs, "delay", 0.0F);
	
	String s = Game.getSpawnArg(spawnArgs, "target", null);
	if (s != null)
		fTargets = Game.getLevelRegistryList("target-" + s);
		
	s = Game.getSpawnArg(spawnArgs, "targetname", null);
	if (s != null)
		Game.addLevelRegistry("target-" + s, this);	
	}
/**
 * Do whatever the relay is supposed to do.
 */
public void runFrame(int phase) 
	{
	useTargets();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	if (fDelay <= 0)
		useTargets();
	else
		// ask to have runFrame() called one time in a little bit.
		Game.addFrameListener(this, fDelay, -1);
	}
/**
 * This method was created by a SmartGuide.
 */
public void useTargets() 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)
		((GameTarget) fTargets.elementAt(i)).use(null);
	}
}