package barryp.map;

import java.util.Vector;
import javax.vecmath.Point3f;

import org.w3c.dom.*;

import q2java.core.*;
import q2java.core.event.*;

/**
 * Similar to barryp.map.RandomWeapons, but not as random.  Keeps the same
 * count of each class of weapon, and just swaps the positions.  Also attempts
 * to update nearby ammoboxes to correspond with the swapped weapons.
 *
 * @author Barry Pederson
 */
public class ShuffleWeapons extends Gamelet implements GameStatusListener
	{
	// max distance between a weapon and its associated ammo - squared.
	private final static float AMMO_THRESHOLD = 150F * 150F;  

	// list of weapons and associated ammo
	// used to help convert ammo that's near to
	// weapons being shuffled.
	private final static String[] WEAPON_AMMO_LIST = 
		{"weapon_shotgun",			"ammo_shells",
		"weapon_supershotgun",		"ammo_shells", 
		"weapon_machinegun",		"ammo_bullets",
		"weapon_chaingun",			"ammo_bullets", 
		"weapon_grenadelauncher",	"ammo_grenades", 
		"weapon_rocketlauncher",	"ammo_rockets",
		"weapon_hyperblaster",		"ammo_cells", 
		"weapon_railgun",			"ammo_slugs", 
		"weapon_bfg",				"ammo_cells"};

	// Utility class used in building a temporary list of weapons
	private static class Weapon
		{
		Element fElement;
		String fOldClass;
		String fNewClass;
		Point3f fOrigin;
		}
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public ShuffleWeapons(String gameletName) 
	{
	super(gameletName);
	}
public void gameStatusChanged(GameStatusEvent gse)
	{
	if (gse.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		Document doc = Game.getLevelDocument();

		// look for <entity>..</entity> sections
		NodeList nl = doc.getElementsByTagName("entity");
		int nodeCount = nl.getLength();

		// Build a list of weapons
		Vector v = new Vector();		
		for (int i = 0; i < nodeCount; i++)
			{
			Element e = (Element) nl.item(i);
			
			String className = e.getAttribute("class");
			if ((className != null) && className.startsWith("weapon_"))
				{
				Weapon w = new Weapon();
				w.fElement = e;
				w.fOldClass = className;
				w.fOrigin = GameUtil.getPoint3f(e, "origin");
				v.addElement(w);
				}
			}
		Weapon[] wa = new Weapon[v.size()];
		v.copyInto(wa);

		// shuffle the weapon classes
		for (int i = 0; i < wa.length; i++)
			{
			Weapon w = wa[i];  // get a direct reference for convenience
			
			// pick another weapon from the vector to take a class from
			// (we have innate knowledge of how big the vector is so we don't have to call Vector.size())
			// it is possible that the weapon chosen will be the same as "w", but that's ok
			int choice = GameUtil.randomInt(wa.length - i);

			// update the weapon to take on the new class
			w.fNewClass = ((Weapon) v.elementAt(choice)).fOldClass;
			w.fElement.setAttribute("class", w.fNewClass);

			// remove the weapon we picked from the vector so it
			// won't be chosen again.
			v.removeElementAt(choice);
			}

		// Look for related ammo
		for (int i = 0; i < nodeCount; i++)
			{
			Element e = (Element) nl.item(i);
			
			String className = e.getAttribute("class");
			if ((className == null) || (!(className.startsWith("ammo_"))))
				continue;
				
			Point3f p = GameUtil.getPoint3f(e, "origin");
			if (p == null)
				continue;

			// check each weapon to see if it's the right
			// classtype and is nearby this ammo
			for (int j = 0; j < wa.length; j++)
				{
				Weapon w = wa[j];
				if (className.equals(getAmmoClass(w.fOldClass))
				&&  ((p.distanceSquared(w.fOrigin) <= AMMO_THRESHOLD)))
					e.setAttribute("class", getAmmoClass(w.fNewClass));
				}
			}
		}		
	}
/**
 * Get the type of ammo that goes with a weapon class.
 * @return java.lang.String
 * @param weaponClass java.lang.String
 */
private String getAmmoClass(String weaponClass) 
	{
	for (int i = 0; i < WEAPON_AMMO_LIST.length; i += 2)
		{
		if (weaponClass.equals(WEAPON_AMMO_LIST[i]))
			return WEAPON_AMMO_LIST[i+1];
		}
		
	return null;
	}
/**
 * Actually initialize the Gamelet for action.
 */
public void init() 
	{
	Game.addGameStatusListener(this);
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	Game.removeGameStatusListener(this);
	}
}