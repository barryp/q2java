
package q2jgame.spawn;

import q2java.*;

public class weapon_railgun extends GenericWeapon
	{
	// all machinegun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {56, 0};
	private static int[] FIRE_FRAMES = new int[] {4, 0};			
	
public weapon_railgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Railgun", "railgun", 
		"slugs", 10, "models/weapons/g_rail/tris.md2");
	}
}