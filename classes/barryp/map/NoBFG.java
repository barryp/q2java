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
public NoBFG(Document gameletInfo) 
	{
	super(gameletInfo);

	Game.addGameStatusListener(this);	
	}
public void gameStatusChanged(GameStatusEvent gse)
	{
	if (gse.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		Document doc = Game.getDocument("q2java.level");

		// look for <entity>..</entity> sections
		Node nextNode = null;
		for (Node n = doc.getDocumentElement().getFirstChild(); n != null; n = nextNode)
			{
			// get the next node now, since we may be deleting the current node
			nextNode = n.getNextSibling();

			try
				{
				Element e = (Element) n;
				if ("entity".equals(e.getTagName())
				&& "weapon_bfg".equals(e.getAttribute("class")))
					e.getParentNode().removeChild(e);
				}
			catch (ClassCastException cce)
				{
				// guess n wasn't an Element..oh well
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