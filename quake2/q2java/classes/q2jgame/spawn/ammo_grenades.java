
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class ammo_grenades extends GenericAmmo
	{
	
public ammo_grenades(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "grenades", 5, "models/items/ammo/grenades/medium/tris.md2");
	freeEntity();
	throw new InhibitedException("grenades inhibited");
	}
}