package q2java.baseq2.spawn;

import java.util.Vector;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class trigger_relay extends Trigger implements FrameListener
	{
	protected float fDelay;
	
public trigger_relay(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
		
	fDelay = GameUtil.getSpawnArg(spawnArgs, "delay", 0.0F);		
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
}