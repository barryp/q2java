package barryp.map;

import org.w3c.dom.*;

import q2java.core.*;
import q2java.core.event.*;

/**
 * Gamelet that keeps BFGs from being spawned.
 */
public class NoBFG extends Gamelet implements GameStatusListener
	{
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public NoBFG(String gameletName) 
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

			// remove from the document if it's a BFG
			if (className.equals("weapon_bfg"))
				e.getParentNode().removeChild(e);
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