
package q2jgame.spawn;

import q2java.*;

public class weapon_hyperblaster extends GenericWeapon
	{
	// all hyperblaster objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {0};
	private static int[] FIRE_FRAMES = new int[] {6, 7, 8, 9, 10, 11, 0};		
	
public weapon_hyperblaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Hyperblaster", "hyperblaster", 
		"cells", 50, "models/weapons/g_hyperb/tris.md2");
	}
}