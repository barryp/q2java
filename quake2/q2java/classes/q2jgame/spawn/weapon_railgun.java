
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class weapon_railgun extends GenericWeapon
	{
	
public weapon_railgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Railgun", "railgun", 
		"slugs", 10, "models/weapons/g_rail/tris.md2");
	}
}