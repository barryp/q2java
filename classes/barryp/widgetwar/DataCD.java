package barryp.widgetwar;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * Datacard dropped by players containing fragment of technology.
 *
 * @author Barry Pederson
 */
public class DataCD extends GenericItem implements StolenTechnology, PlayerStateListener
	{
	private Team fTeam;
	protected float fTimeStolen;
	protected float fCaptureTimelimit = DEFAULT_CAPTURE_TIMELIMIT;

	protected final static float DEFAULT_CAPTURE_TIMELIMIT = 45.0F; // two minutes
	protected final static float FREE_TIME = 15;  // how many seconds you get before the data starts decaying
	protected final static float MAX_FRAGMENT = 0.5F; // the most you can possibly capture

	// inner class to let the DataCD register to be called later
	// without having to worry about the fact that its superclass
	// is already a ServerFrameListener.
	private class ResetHandler implements ServerFrameListener
		{
		public ResetHandler()
			{
			// register to be called back one time, 15..30 seconds from now
			Game.addServerFrameListener(this, Game.FRAME_BEGINNING, (GameUtil.randomFloat() * 15) + 15, -1);
			}

		public void runFrame(int phase)
			{
			// have the CD return to the base
			restoreCD();
			}
		}
	
/**
 * Construct a DataCard belonging to a particular team.
 * @param t barryp.widgetwar.Team
 */
public DataCD(Team team) 
	{
	super();
	
	fTeam = team;
	}
/**
 * What to do if we've dropped, and nobody's 
 * picked us up in the specified time.  Default is to
 * just quietly go away.  Subclasses like the CTF Tech
 * might want to override this to do something different
 * like reposition to a different spot on the map.
 */
protected void dropTimeout() 
	{
	// make the item disappear from the world
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
	fEntity.linkEntity();

	// start the restart process
	reset();
	}
/**
 * Get how many seconds a team has to make a capture.
 * @return float
 */
public float getCaptureTimelimit() 
	{
	return fCaptureTimelimit;
	}
/**
 * Get how much technology this card is carrying.
 * @return float
 */
public float getFragment() 
	{
	float elapsed = (Game.getGameTime() - fTimeStolen) - FREE_TIME;

	if (elapsed < 0)
		elapsed = 0;
		
	if (elapsed > fCaptureTimelimit)
		return 0;
	else
		return MAX_FRAGMENT * (1 - (elapsed / fCaptureTimelimit));
	}
/**
 * Get the name of the icon representing this item.
 */
public String getIconName() 
	{
	return "k_datacd";
	}
/**
 * Descriptive name of this item.
 */
public String getItemName() 
	{
	return "Data CD";
	}
/**
 * Name of model representing this item.
 */
public String getModelName() 
	{
	return "models/items/keys/data_cd/tris.md2";
	}
/**
 * Get which team this card belongs to.
 */
public Team getTeam() 
	{
	return fTeam;
	}
/**
 * Can a given player touch this item.
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTouchable(Player p) 
	{
	// allow the touch if the item has been stolen, or
	// if the player is on a different team
	return (fTimeStolen != 0) || (p.getTeam() != getTeam());
	}
public void playerStateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())	
		{
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_SUSPENDEDSTART:	
		case PlayerStateEvent.STATE_INVALID:
			WidgetWarrior ww = (WidgetWarrior) pse.getPlayer();
			
			ww.removePlayerStateListener(this);
			ww.removeStolenTechnology(this);

			float timeRemaining = fCaptureTimelimit - (Game.getGameTime() - fTimeStolen);
			if (timeRemaining > 0)			
				drop(pse.getPlayer(), timeRemaining);
			else
				reset();
			break;
		}	
	}
/**
 * Called when a player captures an item or recovers a stolen item.
 * @param ww barryp.widgetwar.WidgetWarrior
 */
public void release(WidgetWarrior ww) 
	{
	ww.removePlayerStateListener(this);
	reset();
	}
/**
 * Start the reset process.
 */
public void reset() 
	{
	// create a ResetHandler object (an inner class of this class)
	// that will cause a delay for a few seconds before restoreCD()
	// is called.  No need to worry about holding a reference to
	// the created object, since it will be registered as a
	// ServerFrameListener - and that code will hold the reference.
	
	new ResetHandler();
	}
/**
 * Return the CD to the team base.
 */
public void restoreCD() 
	{
	drop(getTeam().getTeamBase().getBaseEntity().getOrigin(), null, 0, 0);
	fTimeStolen = 0;
	}
/**
 * Set how many seconds a team has to make a capture.
 */
public void setCaptureTimelimit(float t) 
	{
	fCaptureTimelimit = t;
	}
/**
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE);
	}
/**
 * Called if item was actually taken.
 * @param p	The Player that took this item.
 * @param itemTaken The object given to the player, may be this object or a copy.
 */
protected void touchFinish(Player p, GenericItem itemTaken) 
	{
	super.touchFinish(p, itemTaken);

	((WidgetWarrior)p).addStolenTechnology(this);

	// register to be called if the player quits or dies
	p.addPlayerStateListener(this);

	// if this is the first time the item has been picked up, mark the time
	if (fTimeStolen == 0)
		fTimeStolen = Game.getGameTime();
	}
}