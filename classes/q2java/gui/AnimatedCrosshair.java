package q2java.gui;

import q2java.*;

/**
 * Manage an animated HUD crosshair.
 *
 * @author Barry Pederson
 */
 
public class AnimatedCrosshair extends FloatIndicator
	{
	// icons for crosshairs
	private static int gTargetIcon0;
	private static int gTargetIcon1;
	
/**
 * Create an Animated Crosshair.
 * @param owner q2java.NativeEntity
 * @param boundStat int
 */
public AnimatedCrosshair(NativeEntity owner, int boundStat) 
	{
	super(owner, boundStat);
	}
/**
 * Choose an icon for the crosshairs
 * @param f float - the Game clock is a good value to use here.
 */
public int getIcon(float f) 
	{
	if (Float.isNaN(f))
		return 0;
		
	if ((((int)(f * 2)) & 1)  == 0)
		return gTargetIcon0;
	else
		return gTargetIcon1;
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	gTargetIcon0 = Engine.getImageIndex("q2j_crosshair_0");
	gTargetIcon1 = Engine.getImageIndex("q2j_crosshair_1");
	}
}