package q2java.baseq2.spawn;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class ammo_shells extends GenericAmmo
	{
	
public ammo_shells() throws GameException
	{
	super(10);
	}
public ammo_shells(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, 10);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "a_shells";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Shells";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/shells/medium/tris.md2";
	}
}