
package q2jgame;

import q2java.*;

public class misc_deadsoldier extends GameEntity
	{
	
public misc_deadsoldier(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/deadbods/dude/tris.md2");
	
	// Defaults to frame 0
	if ((fSpawnFlags & 2) != 0)
		setFrame(1);
	else if ((fSpawnFlags & 4) != 0)
		setFrame(2);
	else if ((fSpawnFlags & 8) != 0)
		setFrame(3);
	else if ((fSpawnFlags & 16) != 0)
		setFrame(4);
	else if ((fSpawnFlags & 32) != 0)
		setFrame(5);
	
	linkEntity();
	}
}