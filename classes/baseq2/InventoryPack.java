
package baseq2;

/**
 * Helper class to represent inventory a player is carrying.
 */
 
import q2java.*;
 
public class InventoryPack implements java.io.Serializable
	{
	int fAmount;
	int fMaxAmount;
	String fIconName;
	Object fItem;
	
/**
 * This method was created by a SmartGuide.
 */
public InventoryPack ( ) {
}
/**
 * This method was created by a SmartGuide.
 * @param maxAmount int
 * @param iconName java.lang.String
 */
public InventoryPack (int maxAmount, String iconName) 
	{
	fMaxAmount = maxAmount;
	fIconName = iconName;
	}
/**
 * This method was created by a SmartGuide.
 * @param obj java.lang.Object
 */
public InventoryPack (Object obj) 
	{
	fAmount = 1;
	fItem = obj;
	}
}