package q2java.baseq2.spawn;

import q2java.*;
import q2java.baseq2.*;

/**
 * Ammo Packs that increase your capacity and refill stocks.
 *
 * @author Barry Pederson
 */
public class item_pack extends GenericItem 
	{
	public final static int PACK_QUANTITY = 180; // amount of ammo to give players

	// known ammo types and new max values
	private final static String[] AMMO_TYPES = {"bullets", "shells", "grenades", "rockets", "cells", "slugs" };
	private final static int[] MAX_COUNTS = {300, 200, 100, 100, 300, 100 };
	
/**
 * item_pack constructor comment.
 */
public item_pack() 
	{
	super();
	}
/**
 * item_pack constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public item_pack(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	}
/**
 * Get the name of the icon representing this item.
 */
public String getIconName() 
	{
	return "i_pack";
	}
/**
 * Descriptive name of this item.
 */
public String getItemName() 
	{
	return "Ammo Pack";
	}
/**
 * Name of model representing this item.
 */
public String getModelName() 
	{
	return "models/items/pack/tris.md2";
	}
/**
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE);
	}
/**
 * Called if item was actually taken.
 * @param p	The Player that took this item.
 * @param itemTaken The object given to the player, may be this object or a copy.
 */
protected void touchFinish(Player p, GenericItem itemTaken) 
	{
	super.touchFinish(p, itemTaken);

	// update the player for the various known ammo types
	for (int i = 0; i < AMMO_TYPES.length; i++)
		{
		String ammo = AMMO_TYPES[i];

		// update max amount player can carry
		if (p.getMaxAmmoCount(ammo) < MAX_COUNTS[i])
			p.setMaxAmmoCount(ammo , MAX_COUNTS[i]);

		// actually give them some ammo
		int count;
		if ((count = p.getAmmoCount(ammo)) > 0)
			p.setAmmoCount(ammo, PACK_QUANTITY, false);
		}		
	}
}