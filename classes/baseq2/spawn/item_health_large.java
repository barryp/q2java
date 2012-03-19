package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_health_large extends GenericHealth
	{
	
/**
 * No-arg constructor.
 */
public item_health_large() 
	{
	}
public item_health_large(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get how much your health goes up when picking up this item.
 * @return int
 */
public int getHealthValue() 
	{
	return 25;
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/healing/large/tris.md2";
	}
/**
 * Get the name of the sound to play when this item is picked up.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "items/l_health.wav";
	}
}