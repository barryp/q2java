package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.gui.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * The Rebreather.
 * @author Brian Haskin
 */
public class item_breather extends GenericPowerUp implements PlayerStateListener
	{	
	protected Player fOwner;
	protected IconCountdownTimer fHUDTimer;
	protected int fMillis;
	protected float fAirFinished;
	protected boolean fFirstSound;
	
/**
 * No-arg constructor.
 */
public item_breather() 
	{
	}
public item_breather(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get rebreather icon name.
 * @return java.lang.String
 */
public String getIconName()
	{
	return "p_rebreather";
	}
/**
 * Get item name.
 * java.lang.String
 */
public String getItemName()
	{
	return "Rebreather";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/breather/tris.md2";
	}
/**
 * Undo effects of enviroment suit.
 */
protected void reset() 
	{
	if (fOwner != null)
		{
		// Set the player's air to run out at the correct time
		fOwner.breath(10, false);
		
		// disassociate from the player
		fOwner.removePlayerStateListener(this);		
		fOwner = null;
		}
		
	fMillis = 0;
	Game.removeServerFrameListener(this);

	if (fHUDTimer != null)
		{
		fHUDTimer.setVisible(false);
		fHUDTimer.setRunning(false);
		}	
	}
/**
 * Play any sounds that we need to and clean up when time's up.
 */
public void runFrame(int Phase)
	{
	if (fOwner == null)	// Someone above us must want it. - Is this really needed?
		{
		super.runFrame(Phase);
		return;
		}
	
	if (fMillis-- > 30)
		{
		fOwner.addBlend(0.4f, 1f, 0.4f, 0.04f);
		}
	else if ((fMillis & 4) == 4)
		{
		fOwner.addBlend(0.4f, 1f, 0.4f, 0.04f);
		}
	
	if (fOwner.getWaterLevel() == 3 && (fMillis % 25) == 0)
		{
		if (fFirstSound)
			{
			fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("player/u_breath1.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fFirstSound = false;
			}
		else
			{
			fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("player/u_breath2.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fFirstSound = true;
			}
		}
		
	if (fMillis == 30)
		fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("items/airout.wav"), 1, NativeEntity.ATTN_NORM, 0);
		
	if (fMillis == 0)
		reset();
	else
		fOwner.breath(1, false); // give them another little puff
	}
/**
 * Called by the carrying player when they die. This gives us the chance to reset 
 * their effects if we were in use when they died.
 */
public void stateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())	
		{
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_INVALID:
		case PlayerStateEvent.STATE_SUSPENDEDSTART:
			reset();
			break;
		}	
	}
/**
 * When used filter the Player's damage for 30 seconds.
 */
public void use(Player p)
	{
	fOwner = p;
	
	Game.addServerFrameListener(this, 0, 0); // Call us every frame
	fOwner.addPlayerStateListener(this);
	
	fMillis += 300;
	
	fAirFinished = Game.getGameTime() + (fMillis / 10) + 10;
	
	if (fHUDTimer == null)
		{
		fHUDTimer = new IconCountdownTimer(fOwner.fEntity, NativeEntity.STAT_TIMER_ICON, Engine.getImageIndex("p_envirosuit"), NativeEntity.STAT_TIMER, (fMillis/10)-1);
		fHUDTimer.setVisible(true);
		fHUDTimer.setRunning(true);
		}
	else
		{
		fHUDTimer.setValue((fMillis/10)-1);
		fHUDTimer.setVisible(true);
		fHUDTimer.setRunning(true);
		}
	}
}