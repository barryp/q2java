package q2java.gui;

import q2java.*;

/**
 * Display an icon and an integer on the players hud.
 */
public class IconIntegerDisplay
	{
	protected NativeEntity fOwnerEntity;
	private int fIconStat;
	private int fIconIndex;
	private int fIntegerStat;
	private int fValue;
	
	private boolean fIsVisible;
	
/**
 * Create an indicator widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param int iconStat - which playerStat to set to alter the icon displayed
 * @param int icon - index returned by Engine.getImageIndex for this icon
 * @param int integerStat - which playerStat to set to the diplayed value
 * @param int integer - initial value
 */
public IconIntegerDisplay(NativeEntity owner, int iconStat, int icon, int intStat, int integer)
	{
	fOwnerEntity = owner;
	fIconStat = iconStat;
	fIconIndex = icon;
	fIntegerStat = intStat;
	fValue = integer;
	
	fIsVisible = false;
	}
/**
 * Get the index of the icon image for this widget.
 * @return int a value returned by Engine.getImageIndex() for our icon.
 */
protected int getIcon()
	{
	return fIconIndex;
	}
/**
 * Get the entity that owns this indicator.
 * @return q2java.NativeEntity
 */
public NativeEntity getOwner() 
	{
	return fOwnerEntity;
	}
/**
 * Get the value this indicator is displaying.
 * @return int
 */
public int getValue() 
	{
	return fValue;
	}
/**
 * Get whether the indicator is visible or not.
 * @return boolean
 */
public boolean isVisible() 
	{
	return fIsVisible;
	}
/**
 * Set the icon displayed.
 * @param int icon to be displayed
 */
public void setIcon(int i) 
	{
	fIconIndex = i;
	if (fIsVisible)
		fOwnerEntity.setPlayerStat(fIconStat, (short)i);
	}
/**
 * Set the value displayed.
 * @param int value to be displayed
 */
public void setValue(int i) 
	{
	fValue = i;
	if (fIsVisible)
		fOwnerEntity.setPlayerStat(fIntegerStat, (short)i);
	}
/**
 * Set whether the indicator is visible or not.
 * @param b true if visible, false if not.
 */
public void setVisible(boolean b) 
	{
	fIsVisible = b;

	fOwnerEntity.setPlayerStat(fIconStat, (short)(fIsVisible ? fIconIndex : 0));
	fOwnerEntity.setPlayerStat(fIntegerStat, (short)(fIsVisible ? fValue : 0));
	}
}