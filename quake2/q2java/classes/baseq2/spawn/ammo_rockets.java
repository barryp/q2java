
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class ammo_rockets extends GenericAmmo
	{
	
public ammo_rockets(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "rockets", 5, "models/items/ammo/rockets/medium/tris.md2");
	}
}