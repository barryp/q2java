package q2java.baseq2;

import java.util.Hashtable;

/**
 * This class helps track a player's inventory.
 *
 * @author Brian Haskin
 */
public class InventoryTracker implements java.io.Serializable
	{
	protected Hashtable fItemList;
	
public InventoryTracker()
	{
	fItemList = new Hashtable();
	}
/**
 * add an item to the inventory
 * @param java.lang.String item - item name
 */
public void add(String item)
	{
	InventoryPack p = (InventoryPack) fItemList.get(item.toLowerCase());
	
	if (p != null)
		p.fAmount += 1;	
	else
		{
		p = new InventoryPack();
		p.fAmount = 1;
		fItemList.put(item.toLowerCase(), p);
		}
	}
/**
 * add an item to the inventory
 * @param java.lang.String item - item name
 */
public void addPack(String item, InventoryPack p)
	{
	fItemList.put(item.toLowerCase(), p);
	}
/**
 * Totally clear the inventory.
 */
public void clear() 
	{
	fItemList = new Hashtable();
	}
/**
 * This method was created by a SmartGuide.
 * @return baseq2.InventoryPack
 * @param name java.lang.String
 */
public Object get(String name) 
	{
	InventoryPack p = (InventoryPack) fItemList.get(name.toLowerCase());
	if (p == null || p.fAmount == 0)
		return null;
	else
		return p.fItem;
	}
/**
 * how many of an item do we have
 * @param java.lang.String item - item name
 * @returns int - number that we have
 */
public int getNumberOf(String item)
	{
	if (item == null)
		return 0;
		
	InventoryPack i = (InventoryPack) fItemList.get(item.toLowerCase());
	
	if (i == null)
		return 0;
	else
		return i.fAmount;
	}
/**
 * This method was created by a SmartGuide.
 * @return baseq2.InventoryPack
 * @param name java.lang.String
 */
public InventoryPack getPack(String name) 
	{
	return (InventoryPack) fItemList.get(name.toLowerCase());
	}
/**
 * is an item in the inventory
 * @param java.lang.String item - item name
 * @returns boolean - true if item is in inventory.
 */
public boolean isCarrying(String item)
	{
	InventoryPack p = (InventoryPack) fItemList.get(item.toLowerCase());
	return ((p != null) && (p.fAmount > 0));
	}
/**
 * remove an item from the inventory
 * @param java.lang.String item - item name
 * @returns boolean - false if item was not in inventory
 */
public boolean remove(String item)
	{
	InventoryPack p = (InventoryPack) fItemList.get(item.toLowerCase());
		
	if ((p != null) && (p.fAmount > 0))		
		{
		p.fAmount -= 1;
		if (p.fAmount < 1)
			p.fItem = null;
		return true;
		}
		
	return false;
	}
/**
 * remove all of a particular item in the inventory
 * @param java.lang.String item - item name
 * @param boolean - false if item was not in inventory
 */
public boolean removeAll(String item)
	{
	if (fItemList.get(item.toLowerCase()) == null)
		return false;
		
	fItemList.remove(item.toLowerCase());
	return true;
	}
/**
 * set how many of an item we have
 * @param java.lang.String item - item name
 * @param int val - number that we have
 */
public void setNumberOf(String item, int val)
	{
	InventoryPack p = (InventoryPack) fItemList.get(item.toLowerCase());
	
	if (p != null)
		p.fAmount = val;
	else
		{
		p = new InventoryPack();
		p.fAmount = val;
		fItemList.put(item.toLowerCase(), p);
		}
	}
}