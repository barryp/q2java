
package q2jgame.spawn;

import q2java.*;

public class weapon_chaingun extends GenericWeapon
	{
	// all chaingun objects will share these arrays
	private static int[] PAUSE_FRAMES = new int[] {38, 43, 51, 61, 0};
	private static int[] FIRE_FRAMES = new int[] {5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 0};			
	
public weapon_chaingun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Chaingun", "chaingun", 
		"bullets", 50, "models/weapons/g_chain/tris.md2");
	}
}