
package q2jgame.spawn;

import q2java.*;

public class weapon_machinegun extends GenericWeapon
	{
	// all machinegun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {23, 45, 0};
	private static int[] FIRE_FRAMES = new int[] {4, 5, 0};			
	
public weapon_machinegun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Machinegun", "machinegun", 
		"bullets", 50, "models/weapons/g_machn/tris.md2");
	}
}