
package q2jgame;

import q2java.*;

public class item_armor_jacket extends GenericArmor
	{
	
public item_armor_jacket(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/armor/jacket/tris.md2");
	linkEntity();
	}
}