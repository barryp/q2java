package baseq2;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Super class of all power ups lying around
 * in the world and being carried.
 * @author Brian Haskin
 */
public abstract class GenericPowerUp extends GenericItem
	{
	
/**
 * No-arg constructor.
 */
public GenericPowerUp() 
	{
	}
public GenericPowerUp(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Can a given player touch this item.
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTouchable(Player p) 
	{
	if (GameModule.isDMFlagSet(GameModule.DF_INSTANT_ITEMS))
		return true;
		
	// let superclass nix the deal
	if (!super.isTouchable(p))
		return false;

	// allow the touch as long as the player isn't hogging this item
	return (p.getInventoryCount(getItemName()) < 3);
	}
/**
 * Setup this item's NativeEntity.<p>
 * Power up's default to rotating.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE); // Power up's rotate
	}
/**
 * Called if item was actually taken.
 * @param p	The Player that took this item.
 */
protected void touchFinish(Player p, GenericItem itemTaken) 
	{
	super.touchFinish(p, itemTaken);

	if (GameModule.isDMFlagSet(GameModule.DF_INSTANT_ITEMS))
		itemTaken.use(p);
	else
		p.putInventory(itemTaken.getItemName(), itemTaken);	
	}
}