
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class weapon_machinegun extends GenericWeapon
	{
	
public weapon_machinegun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Machinegun", "machinegun", 
		"bullets", 50, "models/weapons/g_machn/tris.md2");
	}
}