package q2java.gui;

import q2java.*;

/**
 * Manage a group of icons that implement a bargraph
 * indicator on the HUD.
 *
 * @author Barry Pederson
 */
 
public class BarGraph extends FloatIndicator
	{
	// icons for bargraph
	private static int gIcon[] = new int[11];
	
	protected float fMinValue;
	protected float fMaxValue;
	protected float fScale;	
	
/**
 * Create a BarGraph widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param boundStat which playerStat to set to alter the icon displayed by the tracker
 */
public BarGraph(NativeEntity owner, int boundStat) 
	{
	super(owner, boundStat);
	}
/**
 * Figure out which icon represents a given value.
 * @param f float may be Float.NaN to force an empty reading.
 */
protected int getIcon(float f) 
	{
	if (Float.isNaN(f))
		return gIcon[0];
	else
		{		
		// check for boundary cases
		if (fScale <= 0)
			{
			if (f <= fMinValue)
				return gIcon[0];
			else if (f >= fMaxValue)
				return gIcon[10];
			}
		else
			{
			if (f >= fMinValue)
				return gIcon[0];
			else if (f <= fMaxValue)
				return gIcon[10];		
			}

		// if not a boundary case, figure out which icon to use
		return gIcon[(int)Math.floor(((fMinValue - getValue())/fScale) * 10)];
		}
	}
/**
 * Get the current maximum value displayed by the bargraph.
 * @return float
 */
public float getMaxValue() 
	{
	return fMaxValue;
	}
/**
 * Get the current minimum value displayed by the bargraph.
 * @return float
 */
public float getMinValue() 
	{
	return fMinValue;
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	gIcon[0]  = Engine.getImageIndex("q2j_bargraph_00");
	gIcon[1]  = Engine.getImageIndex("q2j_bargraph_01");
	gIcon[2]  = Engine.getImageIndex("q2j_bargraph_02");
	gIcon[3]  = Engine.getImageIndex("q2j_bargraph_03");
	gIcon[4]  = Engine.getImageIndex("q2j_bargraph_04");
	gIcon[5]  = Engine.getImageIndex("q2j_bargraph_05");	
	gIcon[6]  = Engine.getImageIndex("q2j_bargraph_06");	
	gIcon[7]  = Engine.getImageIndex("q2j_bargraph_07");
	gIcon[8]  = Engine.getImageIndex("q2j_bargraph_08");
	gIcon[9]  = Engine.getImageIndex("q2j_bargraph_09");	
	gIcon[10] = Engine.getImageIndex("q2j_bargraph_10");	
	}
/**
 * Set the Maximum value of the bargraph - all segments should be lit
 * when this value is reached.
 * @param f float
 */
public void setMaxValue(float f) 
	{
	fMaxValue = f;
	fScale = fMinValue - fMaxValue;

	// force a rethink of the display
	setValue(getValue());
	}
/**
 * Set the Minimum value of the bargraph - it 
 * should turn off when this value is reached.
 * @param f float
 */
public void setMinValue(float f) 
	{
	fMinValue = f;
	fScale = fMinValue - fMaxValue;

	// force a rethink of the display
	setValue(getValue());	
	}
}