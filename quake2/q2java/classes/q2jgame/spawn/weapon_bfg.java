
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class weapon_bfg extends GenericWeapon
	{
	
public weapon_bfg(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "q2jgame.PlayerBFG", "bfg10k", 
		"cells", 50, "models/weapons/g_bfg/tris.md2");
		
	freeEntity();
	throw new InhibitedException("BFG inhibited");		
	}
}