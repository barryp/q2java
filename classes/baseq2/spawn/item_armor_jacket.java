package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_armor_jacket extends GenericArmor
	{
	
public item_armor_jacket(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 25, 50, 0.3F, 0.0F);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "i_jacketarmor";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Jacket Armor";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/armor/jacket/tris.md2";
	}
}