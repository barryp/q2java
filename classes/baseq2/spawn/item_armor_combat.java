package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_armor_combat extends GenericArmor
	{
	
public item_armor_combat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 50, 100, 0.6F, 0.3F);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "i_combatarmor";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Combat Armor";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/armor/combat/tris.md2";
	}
}