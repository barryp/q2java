
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class light extends GameObject
	{
	
public light(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	int style = getSpawnArg("style", 0);
/*		
	if (style > 0)
		Engine.debugLog("Styled Light: " + this + "\n");		
		
	if (fTargetName != null)
		Engine.debugLog("Targeted Light: " + this + "\n");		
*/		
/*		
	// no targeted lights in deathmatch, because they cause global messages
	// ---FIXME--- ? this "if" statement looks whacked...but that's what id wrote
	// I would think it should be: if ((fTargetName != null) && Game.fIsDeathmatch)
	//
	if ((fTargetName == null)|| Game.fIsDeathmatch)
		{
		freeEntity();
		throw new GameException("no targeted lights in deathmatch");
		}

	if (style >= 32)
		{
		self->use = light_use;
		if (self->spawnflags & START_OFF)
			Engine.configString(Engine.CS_LIGHTS+style, "a");
		else
			Engine.configString(Engine.CS_LIGHTS+style, "m");
		}	
*/		
	}
}