package barryp.status;

import java.util.Enumeration;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Watch for gamelets loading/unloading, and update the
 * game status document.
 *
 * @author Barry Pederson
 */
public class GameletStatus extends Gamelet
implements GameletListener
	{
	protected Element fGameletsElement;
	protected Gamelet fSkipGamelet;
	
/**
 * Create the gamelet.
 * @param gameletName java.lang.String
 */
public GameletStatus(Document gameletInfo) 
	{
	super(gameletInfo);

	// add as listener
	Game.getGameletManager().addGameletListener(this);
		
	updateInfo();	
	}
/**
 * Given a gamelet add info about it to the parent element.
 * @param g q2java.core.Gamelet
 */
protected void addGameletElement(Gamelet g) 
	{
	Element ge = fGameletsElement.getOwnerDocument().createElement("gamelet");
	ge.setAttribute("class", g.getClass().getName());		
	fGameletsElement.appendChild(ge);
	}
/**
 * Called when a gamelet is loaded/unloaded.
 * @param ge q2java.core.event.GameletEvent
 */
public void gameletChanged(GameletEvent ge) 
	{
	switch (ge.getState())
		{
		case GameletEvent.GAMELET_ADDED:
			// do a full update..so the correct order is displayed
			updateInfo();
			break;
			
		case GameletEvent.GAMELET_UNLOADING:
			fSkipGamelet = ge.getGamelet();
			updateInfo();
			break;
		}
	}
/**
 * Remove the gamelet.
 */
public void unload() 
	{
	// remove as listener
	Game.getGameletManager().removeGameletListener(this);
	
	// remove the element created by this gamelet
	if (fGameletsElement != null)
		{
		fGameletsElement.getParentNode().removeChild(fGameletsElement);
		Game.notifyDocumentUpdated("q2java.status");
		}
	}
/**
 * Update all the info about the gamelets.
 */
protected void updateInfo() 
	{
	// get rid of old DOM element if any
	if (fGameletsElement != null)
		fGameletsElement.getParentNode().removeChild(fGameletsElement);

	// create a new DOM Element to hold player info
	Document doc = Game.getDocument("q2java.status");
	Element statusRoot = doc.getDocumentElement();
	fGameletsElement = doc.createElement("gamelets");
	statusRoot.appendChild(fGameletsElement);

	// fill it with sub-elements describing the gamelets
	Gamelet[] gamelets = Game.getGameletManager().getGamelets();
	for (int i = 0; i < gamelets.length; i++)
		{
		if (!gamelets[i].equals(fSkipGamelet))
			addGameletElement(gamelets[i]);
		}
		
	fSkipGamelet = null;

	// let everyone know we updated
	Game.notifyDocumentUpdated("q2java.status");
	}
}