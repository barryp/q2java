
package q2jgame.spawn;

import q2java.*;

public class weapon_shotgun extends GenericWeapon
	{
	// all shotgun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {22, 28, 34, 0};
	private static int[] FIRE_FRAMES = new int[] {8, 9, 0};	
	
public weapon_shotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Shotgun", "shotgun", 
		"shells", 10, "models/weapons/g_shotg/tris.md2");
	}
}