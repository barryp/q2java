
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class item_armor_combat extends GenericArmor
	{
	
public item_armor_combat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/armor/combat/tris.md2", 
		"misc/ar1_pkup.wav", "i_combatarmor",
		50, 100, 0.6F, 0.3F);
	}
}