
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_health extends GenericHealth
	{
	
public item_health(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 10, false);
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/healing/medium/tris.md2";
	}
/**
 * Get the name of the sound to play when this item is picked up.
 * @return java.lang.String
 */
public String getPickupSound() 
	{
	return "items/n_health.wav";
	}
}