package q2java.gui;

import q2java.*;

/**
 * Manage a group of icons that implement a widget that
 * reflects some float value on the HUD.
 *
 * @author Barry Pederson
 */
 
public abstract class FloatIndicator 
	{
	protected NativeEntity fOwnerEntity;
	private int fBoundStat;
	private boolean fIsVisible;
	private float fValue = Float.NaN;
	
/**
 * Create an indicator widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param boundStat which playerStat to set to alter the icon displayed by the tracker
 */
public FloatIndicator(NativeEntity owner, int boundStat) 
	{
	fOwnerEntity = owner;
	fBoundStat = boundStat;
	}
/**
 * Get the index of the icon image which represents this float value.
 * @return int a value returned by Engine.getImageIndex(), possibly 0 if the float value can't be displayed.
 * @param f value to represent, may be Float.NaN for an undefined value.
 */
protected abstract int getIcon(float f);
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
 * @return float
 */
public float getValue() 
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
 * Set the indicator to display a particular value.
 * @param f float may be Float.NaN.
 */
public void setValue(float f) 
	{
	fValue = f;
	if (fIsVisible)
		fOwnerEntity.setPlayerStat(fBoundStat, (short)getIcon(f));
	}
/**
 * Set whether the indicator is visible or not.
 * @param b true if visible, false if not.
 */
public void setVisible(boolean b) 
	{
	fIsVisible = b;

	fOwnerEntity.setPlayerStat(fBoundStat, (short)(fIsVisible ? getIcon(fValue) : 0));	
	}
}