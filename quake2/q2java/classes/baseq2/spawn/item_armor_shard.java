
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class item_armor_shard extends GenericArmor
	{
	
public item_armor_shard(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/armor/shard/tris.md2", 
		"misc/ar2_pkup.wav", "i_jacketarmor",
		2, 0, 0, 0);
	}
}