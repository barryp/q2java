
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class ammo_bullets extends GenericAmmo
	{
	
public ammo_bullets(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "bullets", 50, "models/items/ammo/bullets/medium/tris.md2");
	}
}