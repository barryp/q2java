package barryp.map;

import java.lang.reflect.*;
import java.util.Vector;

import org.w3c.dom.*;

import q2java.CVar;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Temporarily load gamelets based on tags found in map data.  
 * You're gonna have to have
 * barryp.spawn.XMLMaps or some other external source of map data
 * running for there to be any gamelet tags to process.
 */
public class MapGamelets extends Gamelet implements GameStatusListener
	{
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public MapGamelets(Document gameletInfo) 
	{
	super(gameletInfo);

	Game.addGameStatusListener(this);
	}
public void gameStatusChanged(GameStatusEvent gse)
	{		
	if (gse.getState() == GameStatusEvent.GAME_PRESPAWN)
		{		
		Document doc = Game.getDocument("q2java.level");

		// look for <gamelet class="..." alias="..."/> tags
		NodeList nl = doc.getElementsByTagName("gamelet");
		int count = nl.getLength();
		
		for (int i = 0; i < count; i++)
			{
			Element e = (Element) nl.item(i);

			// for backwards compatibility with older
			// versions of this gamelet, convert
			// "alias" attributes to the standard
			// "name" attribute
			String name = e.getAttribute("name");
			String alias = e.getAttribute("alias");
			if (alias != null)
				e.setAttribute("alias", null);
				
			if ((name == null) && (alias != null))
				e.setAttribute("name", name);
				
			try
				{
				Gamelet g = Game.getGameletManager().addGamelet(e, true);				
				}
			catch (Throwable t)
				{
				t.printStackTrace();
				}
			}
		}
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	Game.removeGameStatusListener(this);
	}
}