package barryp.map;

import org.w3c.dom.*;

import q2java.core.*;
import q2java.core.event.*;

/**
 * Gamelet that randomizes weapon types.
 */
public class RandomWeapons extends Gamelet implements GameStatusListener
	{
	private final static String[] gWeapons = 
		{"weapon_shotgun",
		"weapon_supershotgun",
		"weapon_machinegun",
		"weapon_chaingun",
		"weapon_grenadelauncher",
		"weapon_rocketlauncher",
		"weapon_hyperblaster",
		"weapon_railgun",
		"weapon_bfg"};
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public RandomWeapons(String gameletName) 
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
		int count = nl.getLength();
		for (int i = 0; i < count; i++)
			{
			Node n = nl.item(i);
			if (!(n instanceof Element))
				continue;

			Element e = (Element) n;
			String className = e.getAttribute("class");

			// Switch the class if it's a weapon
			if (className.startsWith("weapon_"))
				e.setAttribute("class", gWeapons[(GameUtil.randomInt() & 0x0fff) % gWeapons.length]);
			}
		}
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