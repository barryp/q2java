package q2java.ctf;

import java.beans.PropertyVetoException;
import java.util.Enumeration;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * Add the CTF grappling hook to any game.
 *
 * @author Barry Pederson 
 */
public class GrapplingHook extends Gamelet 
implements PlayerStateListener, OccupancyListener, GameStatusListener
	{
	public final static String GRAPPLE_WEAPON_CLASS = "q2java.ctf.GrappleWeapon";
	
/**
 * Create the gamelet object.
 * @param gameletName java.lang.String
 */
public GrapplingHook(Document gameletDoc) 
	{
	super(gameletDoc);

	// Listen for new players connection
	Game.addOccupancyListener(this);

	// Listen to existing players activities
	Enumeration players = NativeEntity.enumeratePlayerEntities();
	while (players.hasMoreElements())
		{
		try
			{
			NativeEntity ent = (NativeEntity) players.nextElement();
			Player p = (Player) ent.getReference();
			p.addPlayerStateListener(this);
			p.addWeapon(GRAPPLE_WEAPON_CLASS, false );		
			}
		catch (Exception e)
			{
			// NativeEntity probably didn't refer to a Player object
			// just quietly ignore and go on.
			}
		}	
	}
/**
 * This method was created in VisualAge.
 * @param gse q2java.core.event.GameStatusEvent
 */
public void gameStatusChanged(GameStatusEvent gse) 
	{
	switch (gse.getState())
		{
		case GameStatusEvent.GAME_POSTSPAWN:
			// create an instance of a GrappleWeapon to make sure 
			// the grapple VWep skin is cached before we set the 
			// player's skin
			GrappleWeapon gw = new GrappleWeapon();
			break;
		}
	}
 /**
  * Called with a new player connects - attach to the new player
  * as a PlayerStateListener, so we can give them a weapon_grapple
  * whenever they spawn.
  */
public void playerChanged(OccupancyEvent oe) throws PropertyVetoException
	{
	switch (oe.getState())
		{
		case OccupancyEvent.PLAYER_CONNECTED:
		case OccupancyEvent.PLAYER_CLASSCHANGE:
			try
				{
				Player p = (Player) oe.getPlayerEntity().getReference();
				p.addPlayerStateListener(this);
				}
			catch (Exception e)
				{
				// probably a NullPointerException or ClassCastException
				}
			break;
		}
	}
/**
 * Called when a player state changes - we're interested in
 * players spawning - so we can tuck a weapon_grapple into their inventory.
 */
public void playerStateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())	
		{		
		case PlayerStateEvent.STATE_SPAWNED:
			// put the grapple in inventory
			pse.getPlayer().addWeapon(GRAPPLE_WEAPON_CLASS, false );		
			break;

		case PlayerStateEvent.STATE_INVALID:
			// stop listening to this player
			pse.getPlayer().removePlayerStateListener(this);
			break;
		}	
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	// don't want to hear about new players connection
	Game.removeOccupancyListener(this);

	// don't want to hear about players respawning
	Enumeration players = NativeEntity.enumeratePlayerEntities();
	while (players.hasMoreElements())
		{
		try
			{
			NativeEntity ent = (NativeEntity) players.nextElement();
			Player p = (Player) ent.getReference();
			p.removePlayerStateListener(this);
			}
		catch (Exception e)
			{
			// NativeEntity probably didn't refer to a Player object
			// just quietly ignore and go on.
			}
		}
	}
}