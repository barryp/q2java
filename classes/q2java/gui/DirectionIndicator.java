package q2java.gui;

import q2java.*;

/**
 * Manage a group of icons that implement a direction
 * indicator on the HUD.
 *
 * @author Barry Pederson
 */
 
public class DirectionIndicator extends FloatIndicator
	{
	// icons for tracker
	private static int gTrackerIcon0;
	private static int gTrackerIcon1;
	private static int gTrackerIcon2;
	private static int gTrackerIcon3;
	private static int gTrackerIcon4;
	private static int gTrackerIcon5;
	private static int gTrackerIcon6;
	
/**
 * Create a DirectionIndicator widget and bind it to a particular player and stat.
 * @param owner q2java.NativeEntity
 * @param boundStat which playerStat to set to alter the icon displayed by the tracker
 */
public DirectionIndicator(NativeEntity owner, int boundStat) 
	{
	super(owner, boundStat);
	}
/**
 * Get an icon to represent a given direction.
 * @return index of icon
 * @param angle Quake direction in degrees, 0 is straight ahead, 
 *   positive values are counter-clockwise, negative is clockwise.
 */
protected int getIcon(float angle) 
	{
	if (Float.isNaN(angle))
		return gTrackerIcon0;
		
	// normalize to range 180..-180
	while (angle > 180)
		angle -= 360;
	while (angle < -180)
		angle += 360;

	// return the icon closest to representing the direction we want
	if (angle > 150)
		return gTrackerIcon4;
	else if (angle > 90)
		return gTrackerIcon5;
	else if (angle > 30)
		return gTrackerIcon6;
	else if (angle > -30)
		return gTrackerIcon1;
	else if (angle > -90)
		return gTrackerIcon2;
	else if (angle > -150)
		return gTrackerIcon3;
	else
		return gTrackerIcon4;
	}
/**
 * Load images into server. Needs to be done at beginning
 * of each level.
 */
public static void precacheImages() 
	{
	gTrackerIcon0 = Engine.getImageIndex("q2j_dirind6_0");
	gTrackerIcon1 = Engine.getImageIndex("q2j_dirind6_1");
	gTrackerIcon2 = Engine.getImageIndex("q2j_dirind6_2");
	gTrackerIcon3 = Engine.getImageIndex("q2j_dirind6_3");
	gTrackerIcon4 = Engine.getImageIndex("q2j_dirind6_4");
	gTrackerIcon5 = Engine.getImageIndex("q2j_dirind6_5");	
	gTrackerIcon6 = Engine.getImageIndex("q2j_dirind6_6");	
	}
}