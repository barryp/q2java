
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class trigger_always extends GameObject implements FrameListener
	{
	private String fMessage;
	private float fDelay;
	
public trigger_always(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	fMessage = getSpawnArg("message", null);
	
	// schedule a one-shot runFrame() call
	Game.addFrameListener(this, getSpawnArg("delay", 0.2F), -1);
	}
/**
 * Do whatever the trigger is supposed to do, and go away.
 */
public void runFrame(int phase) 
	{
	useTargets();
	dispose();
	}
}