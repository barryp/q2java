package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_armor_shard extends GenericArmor
	{
	
/**
 * No-arg constructor.
 */
public item_armor_shard() 
	{
	}
public item_armor_shard(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get the max value of this armor.
 * @return int
 */
public int getArmorMaxValue()
	{
	return 0;
	}
/**
 * Get the value of this armor.
 * @return int
 */
public int getArmorValue()
	{
	return 2;
	}
/**
 * Get the strength of this armor against energy weapons.
 * @return float
 */
public float getEnergyProtectionFactor()
	{
	return 0.0F;
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
	return "Armor Shard";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/armor/shard/tris.md2";
	}
/**
 * Get the strength of this armor.
 * @return float
 */
public float getProtectionFactor()
	{
	return 0.0F;
	}
}