package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Bundle of rockets waiting to be picked up
 *
 * @author Barry Pederson
 */

public class ammo_rockets extends GenericAmmo
	{
	
public ammo_rockets() throws GameException
	{
	super(5);
	}
public ammo_rockets(Element spawnArgs) throws GameException
	{
	super(spawnArgs, 5);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "a_rockets";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Rockets";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/rockets/medium/tris.md2";
	}
}