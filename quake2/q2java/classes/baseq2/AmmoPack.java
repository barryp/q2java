
package q2jgame;

/**
 * Helper class to represent ammo a player is carrying.
 */
 
import q2java.*;
 
public class AmmoPack 
	{
	int fAmount;
	int fMaxAmount;
	int fIcon;
	
/**
 * This method was created by a SmartGuide.
 * @param count int
 * @param icon int
 */
public AmmoPack (int maxCount, String iconName) 
	{
	fMaxAmount = maxCount;
	fIcon = (iconName == null ? 0 : Engine.imageIndex(iconName));
	}
}