package q2java.baseq2.spawn;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class item_armor_combat extends GenericArmor
	{
	
/**
 * No-arg constructor.
 */
public item_armor_combat() 
	{
	}
public item_armor_combat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get the max value of this armor.
 * @return int
 */
public int getArmorMaxValue()
	{
	return 100;
	}
/**
 * Get the value of this armor.
 * @return int
 */
public int getArmorValue()
	{
	return 50;
	}
/**
 * Get the strength of this armor against energy weapons.
 * @return float
 */
public float getEnergyProtectionFactor()
	{
	return 0.3F;
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
/**
 * Get the strength of this armor.
 * @return float
 */
public float getProtectionFactor()
	{
	return 0.6F;
	}
}