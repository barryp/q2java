package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class ammo_cells extends GenericAmmo
	{
	
public ammo_cells() throws GameException
	{
	super(50);
	}
public ammo_cells(Element spawnArgs) throws GameException
	{
	super(spawnArgs, 50);
	}
/**
 * Get the name of this item's icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "a_cells";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Cells";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/ammo/cells/medium/tris.md2";
	}
}