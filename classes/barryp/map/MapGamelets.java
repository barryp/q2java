package barryp.map;

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
	private Vector fStack;
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public MapGamelets(String gameletName) 
	{
	super(gameletName);
	}
public void gameStatusChanged(GameStatusEvent gse)
	{
	if (gse.getState() == GameStatusEvent.GAME_ENDLEVEL)
		{
		// unload old gamelets
		for (int i = fStack.size() - 1; i >= 0; i--)
			{
			Gamelet g = (Gamelet) fStack.elementAt(i);
			Game.removeGamelet(g);
			}
			
		fStack.removeAllElements();
		return;
		}
		
	if (gse.getState() == GameStatusEvent.GAME_PRESPAWN)
		{		
		Document doc = Game.getLevelDocument();

		// look for <gamelet class="..." alias="..."/> tags
		NodeList nl = doc.getElementsByTagName("gamelet");
		int count = nl.getLength();
		
		for (int i = 0; i < count; i++)
			{
			Node n = nl.item(i);
			if (!(n instanceof Element))
				continue;

			Element e = (Element) n;
			String className = e.getAttribute("class");
			String alias = e.getAttribute("alias");

			try
				{
				Gamelet g = Game.addGamelet(className, alias);				
				fStack.addElement(g);
				
				g.init(); // assume it should be initialized now
				g.markInitialized();
				}
			catch (Exception ex)
				{
				ex.printStackTrace();
				}
			}
		}
	}
/**
 * Actually initialize the Gamelet for action.
 */
public void init() 
	{
	Game.addGameStatusListener(this);
	fStack = new Vector();
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	Game.removeGameStatusListener(this);
	}
}