
package q2jgame.spawn;

import q2java.*;

public class weapon_supershotgun extends GenericWeapon
	{
	
public weapon_supershotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.SuperShotgun", "super shotgun", 
		"shells", 10, "models/weapons/g_shotg2/tris.md2");
	}
}