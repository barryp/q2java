
package q2jgame;

/**
 * This class was generated by a SmartGuide.
 * 
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