
package q2jgame.spawn;

import q2java.*;

public class weapon_shotgun extends GenericWeapon
	{
	
public weapon_shotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Shotgun", "shotgun", 
		"shells", 10, "models/weapons/g_shotg/tris.md2");
	}
}