package q2java.baseq2.spawn;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class item_armor_body extends GenericArmor
	{
	
/**
 * No-arg constructor.
 */
public item_armor_body() 
	{
	}
public item_armor_body(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get the max value of this armor.
 * @return int
 */
public int getArmorMaxValue()
	{
	return 200;
	}
/**
 * Get the value of this armor.
 * @return int
 */
public int getArmorValue()
	{
	return 100;
	}
/**
 * Get the strength of this armor against energy weapons.
 * @return float
 */
public float getEnergyProtectionFactor()
	{
	return 0.6F;
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "i_bodyarmor";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Body Armor";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/armor/body/tris.md2";
	}
/**
 * Get the strength of this armor.
 * @return float
 */
public float getProtectionFactor()
	{
	return 0.8F;
	}
}