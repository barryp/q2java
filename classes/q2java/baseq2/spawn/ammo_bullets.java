package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class ammo_bullets extends GenericAmmo
	{
	
public ammo_bullets() throws GameException
	{
	super(50);
	}
public ammo_bullets(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 50);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "a_bullets";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Bullets";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/bullets/medium/tris.md2";
	}
}