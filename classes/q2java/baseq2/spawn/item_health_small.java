package q2java.baseq2.spawn;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class item_health_small extends GenericHealth
	{
	
/**
 * No-arg constructor.
 */
public item_health_small() 
	{
	}
public item_health_small(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get how much your health goes up when picking up this item.
 * @return int
 */
public int getHealthValue() 
	{
	return 2;
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/healing/stimpack/tris.md2";
	}
/**
 * Get the name of the sound to play when this item is picked up.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "items/s_health.wav";
	}
/**
 * Can this item boost you past your max?
 * @return boolean
 */
public boolean isOverridingMax() 
	{
	return true;
	}
}