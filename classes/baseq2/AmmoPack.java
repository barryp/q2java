
package baseq2;

/**
 * Helper class to represent ammo a player is carrying.
 */
 
import q2java.*;
 
public class AmmoPack implements java.io.Serializable
	{
	int fAmount;
	int fMaxAmount;
	String fIconName;
	
/**
 * This method was created by a SmartGuide.
 * @param count int
 * @param icon int
 */
public AmmoPack (int maxCount, String iconName) 
	{
	fMaxAmount = maxCount;
	fIconName = iconName;
	}
}