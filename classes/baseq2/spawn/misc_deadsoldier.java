
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class misc_deadsoldier extends GameObject
	{
	
public misc_deadsoldier(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	if (baseq2.GameModule.gIsDeathmatch)
		{
		dispose();
		throw new InhibitedException("misc_deadsoldier inhibited in deathmatch");
		}
		
	fEntity.setModel("models/deadbods/dude/tris.md2");
	
	// Defaults to frame 0
	if ((fSpawnFlags & 2) != 0)
		fEntity.setFrame(1);
	else if ((fSpawnFlags & 4) != 0)
		fEntity.setFrame(2);
	else if ((fSpawnFlags & 8) != 0)
		fEntity.setFrame(3);
	else if ((fSpawnFlags & 16) != 0)
		fEntity.setFrame(4);
	else if ((fSpawnFlags & 32) != 0)
		fEntity.setFrame(5);
	
	if (fEntity != null)
		fEntity.linkEntity();
	}
}