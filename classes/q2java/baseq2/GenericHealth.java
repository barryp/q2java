package q2java.baseq2;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;

/**
 * Superclass for all health entities lying around in the world.
 *
 * @author Barry Pederson
 */
public abstract class GenericHealth extends GenericItem
	{
	
/**
 * No-arg constructor for health items.
 */
public GenericHealth() 
	{
	super();
	}
public GenericHealth(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	if (BaseQ2.isDMFlagSet(BaseQ2.DF_NO_HEALTH))
		{
		dispose();
		throw new InhibitedException("health items inhibited");
		}
	}
/**
 * Get how much your health goes up when picking up this item.
 * @return int
 */
public abstract int getHealthValue();
/**
 * All health items share the same icon.
 * @return java.lang.String
 */
public String getIconName() 
	{
	return "i_health";
	}
/**
 * Get the name of this item.
 * @return java.lang.String
 */
public String getItemName() 
	{
	return "Health";
	}
/**
 * Can this item boost you past your max health?
 * @return boolean
 */
public boolean isOverridingMax() 
	{
	return false;
	}
}