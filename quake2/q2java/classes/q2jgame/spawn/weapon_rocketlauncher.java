
package q2jgame.spawn;

import q2java.*;

public class weapon_rocketlauncher extends GenericWeapon
	{
	
public weapon_rocketlauncher(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.RocketLauncher", "rocket launcher",
		"rockets", 10, "models/weapons/g_rocket/tris.md2");
	}
}