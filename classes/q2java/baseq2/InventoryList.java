package q2java.baseq2;


import q2java.Engine;

/**
 * This class keeps track of the items that are displayed in the inventory of a player.
 * @author Brian Haskin
 */
public class InventoryList 
	{
	public final static int FIRST_ITEMS = 1;
	public final static int BEFORE_ARMOR = FIRST_ITEMS+1;
	public final static int AFTER_ARMOR = BEFORE_ARMOR+1;
	public final static int BEFORE_WEAPONS = AFTER_ARMOR+1;
	public final static int AFTER_WEAPONS = BEFORE_WEAPONS+1;
	public final static int BEFORE_AMMO = AFTER_WEAPONS+1;
	public final static int AFTER_AMMO = BEFORE_AMMO+1;
	public final static int BEFORE_POWERUPS = AFTER_AMMO+1;
	public final static int AFTER_POWERUPS = BEFORE_POWERUPS+1;
	public final static int BEFORE_MISC = AFTER_POWERUPS+1;
	public final static int AFTER_MISC = BEFORE_MISC+1;
	public final static int LAST_ITEMS = AFTER_MISC+1;
	
	protected static String[] itemList;
	
	protected static int startOfArmor;
	protected static int startOfWeapon;
	protected static int startOfAmmo;
	protected static int startOfPowerup;
	protected static int startOfMisc;
	
	private final static String[] idDefaultArmor = { "Body Armor",
											 "Combat Armor",
											 "Jacket Armor",
											 "Armor Shard",
											 "Power Screen",
											 "Power Shield" };
										
	private final static String[] idDefaultWeapons = { "Blaster",
											   "Shotgun",
											   "Super Shotgun",
											   "Machinegun",
											   "Chaingun",
											   "Grenades",
											   "Grenade Launcher",
											   "Rocket Launcher",
											   "HyperBlaster",
											   "Railgun",
											   "BFG10K" };
								  
	private final static String[] idDefaultAmmo = { "Shells",
											"Bullets",
											"Cells",
											"Rockets",
											"Slugs" };
								  
	private final static String[] idDefaultPowerups = { "Quad Damage",
												"Invulnerability",
												"Silencer",
												"Rebreather",
												"Environment Suit" };
								  
	private final static String[] idDefaultMisc = { "Ancient Head",
											"Adrenaline",
											"Bandolier",
											"Ammo Pack",
											"Data CD",
											"Power Cube",
											"Pyramid Key",
											"Data Spinner",
											"Security Pass",
											"Blue Key",
											"Red Key",
											"Commander's Head",
											"Airstrike Marker",
											"Health" };
								  

/*
 * add an item to the list in first available slot after index i
 */
protected static boolean addAfterIndex(int i, String item)
	{
	if (i<0) return false;
	
	while (i < itemList.length)
		{
		if (itemList[i] == null)
			{
			itemList[i] = item;
			return true;
			} 
		else
			{
			i++;
			}
		}

	return false;
	}
/*
 * add an item to the list in first available slot before index i
 */
protected static boolean addBeforeIndex(int i, String item)
	{
	if (i > itemList.length) return false;
	
	while (i >= 0) 
		{
		if (itemList[i] == null)
			{
			itemList[i] = item;
			return true;
			}
		else
			{
			i++;
			}
		}
	
	Engine.setConfigString(Engine.CS_ITEMS + i, itemList[i]);
	
	return false;
	}
/**
 * add an item to the list. Will be added after the weapons if not already in list.
 * @param java.lang.String item - name of item to be added.
 */
public static boolean addItem(String item)
	{
	return addItem(item, AFTER_WEAPONS, false);
	}
/**
 * add an item to the list in a specified area.
 * @param java.lang.String item - name of item to be added.
 * @param int PositionFlag - area of list to place the item.
 * @param boolean reposition - if item is already in list should it be repositioned.
 */
public static boolean addItem(String item, int PositionFlag, boolean reposition)
	{
	int i;
	if ((i = getIndexOf(item)) != -1)	// check to see if item is already in list
		{
		if (reposition == false)
			return true;
		else
			itemList[i] = null;
		}
		
	switch (PositionFlag)
		{
		case FIRST_ITEMS:
			return addAfterIndex(0, item);
				
		case BEFORE_ARMOR:
			return addBeforeIndex(startOfArmor, item);
				
		case AFTER_ARMOR:
			return addAfterIndex(startOfArmor, item);
				
		case BEFORE_WEAPONS:
			return addBeforeIndex(startOfWeapon, item);
			
		case AFTER_WEAPONS:
			return addAfterIndex(startOfWeapon, item);
			
		case BEFORE_AMMO:
			return addBeforeIndex(startOfAmmo, item);
			
		case AFTER_AMMO:
			return addAfterIndex(startOfAmmo, item);
			
		case BEFORE_POWERUPS:
			return addBeforeIndex(startOfPowerup, item);
			
		case AFTER_POWERUPS:
			return addAfterIndex(startOfPowerup, item);
			
		case BEFORE_MISC:
			return addBeforeIndex(startOfMisc, item);
			
		case AFTER_MISC:
			return addAfterIndex(startOfMisc, item);
			
		case LAST_ITEMS:
			return addBeforeIndex(itemList.length-1, item);
		
		// out of range argument
		default:
			return false;
		}
	}
/**
 * get the index of an item.
 * @param java.lang.String item - name of item
 * @returns int - set to the index of the item, -1 if not in list.
 */
public static int getIndexOf(String item)
	{
	for (int i=0; i<itemList.length; i++)
		{
		if (item.equals(itemList[i]))
			return i;
		}
		
	return -1;
	}
/**
 * get the item at a particular index.
 * @param int i - index of item.
 * @returns java.lang.String - Item at index.
 */
public static String getItemAtIndex(int i)
	{
	if (i >= 0 && i < itemList.length)
		return itemList[i];
			
	return "";
	}
/**
 * get the length of the inventory list.
 * @returns int - length of inventory list.
 */
public static int length()
	{
	return itemList.length;
	}
/**
 * register the list with the game engine.
 */
public static void registerList()
	{
	for (int i=0; i<itemList.length; i++)
		{
		if (itemList[i] != null)
			{
			Engine.setConfigString(Engine.CS_ITEMS + i, itemList[i]);
			}
		} 
	}
//	InventoryList( 42, 84, 128, 172, 214, true);

/*
 * Create and initiliaze the Inventory list. This is currently done in baseq2.GameModule.
 * @param int arI - starting index for the armor section.
 * @param int wI - starting index for the weapon section.
 * @param int amI - starting index for the ammo section.
 * @param int pI - starting index for the powerup section.
 * @param int mI - starting index for the misc section.
 * @param boolean installid - whether to install the default id items and ordering
 */
public static void setupList(int arI, int wI, int amI, int pI, int mI, boolean installId)
	{
	itemList = new String[256];
/*	
	for (int i=0; i<itemList.length; i++)
	{
		itemList[i] = "";
	}
*/		
	startOfArmor = arI;
	startOfWeapon = wI;
	startOfAmmo = amI;
	startOfPowerup = pI;
	startOfMisc = mI;

	if (installId)
		{
		if (startOfArmor >= 0)
			{
			for (int i=0; i<idDefaultArmor.length; i++)
				itemList[(i+startOfArmor) % 256] = idDefaultArmor[i];
			}
			
		if (startOfWeapon >= 0)
			{	
			for (int i=0; i<idDefaultWeapons.length; i++)
				itemList[(i+startOfWeapon) % 256] = idDefaultWeapons[i];
			}
		
		if (startOfAmmo >= 0)
			{
			for (int i=0; i<idDefaultAmmo.length; i++)
				itemList[(i+startOfAmmo) % 256] = idDefaultAmmo[i];
			}
		
		if (startOfPowerup >= 0)
			{
			for (int i=0; i<idDefaultPowerups.length; i++)
				itemList[(i+startOfPowerup) % 256] = idDefaultPowerups[i];
			}
			
		if (startOfMisc >= 0)
			{
			for (int i=0; i<idDefaultMisc.length; i++)
				itemList[(i+startOfMisc) % 256] = idDefaultMisc[i];
			}
			
		}
				
	return;
	}
}