
package q2jgame;

import q2java.*;

public class item_armor_combat extends GenericArmor
	{
	
public item_armor_combat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/armor/combat/tris.md2");
	linkEntity();
	}
}