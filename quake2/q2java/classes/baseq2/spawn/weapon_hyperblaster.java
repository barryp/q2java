
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class weapon_hyperblaster extends GenericWeapon
	{
	
public weapon_hyperblaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Hyperblaster", "hyperblaster", 
		"cells", 50, "models/weapons/g_hyperb/tris.md2");
	}
}