package barryp.map;

import java.util.Vector;

import org.w3c.dom.*;

import q2java.CVar;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Look for CVar tags in a map's data.  You're gonna have to have
 * barryp.spawn.XMLMaps or some other external source of map data
 * running for there to be any CVar tags to process.
 */
public class MapCVars extends Gamelet implements GameStatusListener
	{
	private Vector fStack = new Vector();
	
/**
 * NoBFG constructor comment.
 * @param gameletName java.lang.String
 */
public MapCVars(Document gameletInfo) 
	{
	super(gameletInfo);

	Game.addGameStatusListener(this);
	}
public void gameStatusChanged(GameStatusEvent gse)
	{
	if (gse.getState() == GameStatusEvent.GAME_ENDLEVEL)
		{
		for (int i = fStack.size() - 1; i >= 0; i-=2)
			{
			String oldValue = (String) fStack.elementAt(i);
			CVar c = (CVar) fStack.elementAt(i-1);
			c.setValue(oldValue);
			}
			
		fStack.removeAllElements();
		return;
		}
		
	if (gse.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		Document doc = Game.getDocument("q2java.level");

		// look for <cvar name="..." value="..."/> tags
		NodeList nl = doc.getElementsByTagName("cvar");
		int count = nl.getLength();
		
		for (int i = 0; i < count; i++)
			{
			Element e = (Element) nl.item(i);
			String name = e.getAttribute("name");
			String newValue = e.getAttribute("value");

			CVar c = new CVar(name, newValue, 0);
			fStack.addElement(c);
			fStack.addElement(c.getString()); // save the old value
			c.setValue(newValue);
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