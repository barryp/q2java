
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_armor_body extends GenericArmor
	{
	
public item_armor_body(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/armor/body/tris.md2", 
		"misc/ar1_pkup.wav", "i_bodyarmor",
		100, 200, 0.8F, 0.6F);
	}
}