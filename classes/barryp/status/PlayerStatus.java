package barryp.status;

import java.util.Enumeration;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

/**
 * Watch for players connecting/disconnecting, and update the
 * game status document.
 *
 * @author Barry Pederson
 */
public class PlayerStatus extends Gamelet
implements OccupancyListener, PlayerInfoListener
	{
	protected Element fPlayersElement;
	private NativeEntity fSkipPlayer;
	
/**
 * Create the gamelet.
 * @param gameletName java.lang.String
 */
public PlayerStatus(Document gameletInfo) 
	{
	super(gameletInfo);

	// add as listener
	Game.addOccupancyListener(this);
	Player.addAllPlayerInfoListener(this);

	// update status doc to reflect current players
	updateInfo();	
	}
/**
 * Given a player NativeEntity, add info about it to the parent element.
 * @param playersRoot org.w3c.dom.Element
 * @param playerEntity q2java.NativeEntity
 */
protected void addPlayerElement(NativeEntity playerEntity) 
	{
	try
		{
		Player p = (Player) playerEntity.getReference();
		Element player = fPlayersElement.getOwnerDocument().createElement("player");
		player.setAttribute("name", p.getName());
		fPlayersElement.appendChild(player);
		}
	catch (Exception e)
		{
		// Probably a ClassCastException ... if the
		// NativeEntity was associated with something
		// other than q2java.baseq2.Player.
		}	
	}
/**
 * Called with some player info changes.
 * @param pie q2java.baseq2.event.PlayerInfoEvent
 */
public void infoChanged(PlayerInfoEvent pie) 
	{
	if (pie.getKey().equalsIgnoreCase("name"))
		updateInfo(); // regenerate entire section

	// a fancier implementation might locate the right
	// DOM element and just change the attribute, rather
	// than use the brute-force approach.
	}
/**
 * Called when a player connects/disconnects.
 *
 * @param oe q2java.core.event.OccupancyEvent
 */
public void playerChanged(OccupancyEvent oe) 
	{
	switch (oe.getState())
		{
		case OccupancyEvent.PLAYER_CONNECTED:
			try	
				{
				NativeEntity ent = oe.getPlayerEntity();

				// add new element to status document
				addPlayerElement(oe.getPlayerEntity());
				Game.notifyDocumentUpdated("q2java.status");
				}
			catch (Exception e)
				{
				// probably ClassCastException if the player wasn't
				// a subclass of q2java.baseq2.Player
				}
			break;

		case OccupancyEvent.PLAYER_DISCONNECTED:
			// regenerate the entire "players" element
			fSkipPlayer = oe.getPlayerEntity();
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
	Game.removeOccupancyListener(this);
	Player.removeAllPlayerInfoListener(this);
	
	// remove the element created by this gamelet
	if (fPlayersElement != null)
		{
		fPlayersElement.getParentNode().removeChild(fPlayersElement);
		Game.notifyDocumentUpdated("q2java.status");
		}
	}
/**
 * Update all the info about the players.
 */
protected void updateInfo() 
	{
	// get rid of old DOM element if any
	if (fPlayersElement != null)
		fPlayersElement.getParentNode().removeChild(fPlayersElement);

	// create a new DOM Element to hold player info
	Document doc = Game.getDocument("q2java.status");
	Element statusRoot = doc.getDocumentElement();
	fPlayersElement = doc.createElement("players");
	statusRoot.appendChild(fPlayersElement);

	// fill it with sub-elements describing the players
	Enumeration players = NativeEntity.enumeratePlayerEntities();
	while (players.hasMoreElements())
		{
		NativeEntity ent = (NativeEntity) players.nextElement();
		if (ent != fSkipPlayer)
			addPlayerElement(ent);
		}
		
	fSkipPlayer = null;
	
	// let everyone know we updated
	Game.notifyDocumentUpdated("q2java.status");
	}
}