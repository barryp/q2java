package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.baseq2.*;

public class trigger_always extends Trigger implements ServerFrameListener
	{
	private String fMessage;
	private float fDelay;
	
public trigger_always(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	fMessage = GameUtil.getSpawnArg(spawnArgs, "message", null);
	
	// schedule a one-shot runFrame() call
	Game.addServerFrameListener(this, GameUtil.getSpawnArg(spawnArgs, "delay", 0.2F), -1);
	}
/**
 * Do whatever the trigger is supposed to do, and go away.
 */
public void runFrame(int phase) 
	{
	useTargets();
	}
}