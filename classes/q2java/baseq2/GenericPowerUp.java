package q2java.baseq2;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

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
public GenericPowerUp(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * The default respawn time for powerup's is 60 seconds.
 */
public float getRespawnTime()
	{
	return 60;
	}
/**
 * Can a given player touch this item.
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTouchable(Player p) 
	{
	if (BaseQ2.isDMFlagSet(BaseQ2.DF_INSTANT_ITEMS))
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

	if (BaseQ2.isDMFlagSet(BaseQ2.DF_INSTANT_ITEMS))
		itemTaken.use(p);
	else
		p.putInventory(itemTaken.getItemName(), itemTaken);	
	}
}