
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class weapon_chaingun extends GenericWeapon
	{
	
public weapon_chaingun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.weapon.Chaingun", "chaingun", 
		"bullets", 50, "models/weapons/g_chain/tris.md2");
	}
}