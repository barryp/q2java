
package q2jgame.spawn;

import q2java.*;

public class weapon_supershotgun extends GenericWeapon
	{
	// all shotgun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {29, 42, 57, 0};
	private static int[] FIRE_FRAMES = new int[] {7, 0};		
	
public weapon_supershotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.SuperShotgun", "super shotgun", 
		"shells", 10, "models/weapons/g_shotg2/tris.md2");
	}
}