
package q2jgame.spawn;

import q2java.*;

public class item_armor_jacket extends GenericArmor
	{
	
public item_armor_jacket(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/armor/jacket/tris.md2", 
		"misc/ar1_pkup.wav", "i_jacketarmor",
		25, 50, 0.3F, 0.0F);
	}
}