package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class ammo_slugs extends GenericAmmo
	{
	
public ammo_slugs(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 10);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "a_slugs";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Slugs";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/slugs/medium/tris.md2";
	}
}