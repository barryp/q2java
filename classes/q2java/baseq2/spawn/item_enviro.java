package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.core.gui.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * The environment suit.
 * @author Brian Haskin
 */
public class item_enviro extends GenericPowerUp 
  implements ServerFrameListener, PlayerStateListener, DamageListener
	{	
	protected Player fOwner;
	protected IconCountdownTimer fHUDTimer;
	protected int fMillis;
	protected float fAirFinished;
	protected boolean fFirstSound;
	
/**
 * No-arg constructor.
 */
public item_enviro() 
	{
	}
public item_enviro(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Called by the carrying player when they take damage.
 */
public void damageOccured(DamageEvent damage)
	{
	if (damage.getInflictor() == BaseQ2.gWorld && 
	    damage.getObitKey().equals("slime"))
		{
		damage.setAmount(0);
		}
	else if (damage.getInflictor() == BaseQ2.gWorld &&
		 damage.getObitKey().equals("lava"))
		{
		damage.setAmount( damage.getAmount() / 3 );
		}
	}
/**
 * Get enviroment suit icon name.
 * @return java.lang.String
 */
public String getIconName()
	{
	return "p_envirosuit";
	}
/**
 * Get item name.
 * java.lang.String
 */
public String getItemName()
	{
	return "Environment Suit";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/enviro/tris.md2";
	}
/**
 * Called by the carrying player when they die. This gives us the chance to reset 
 * their effects if we were in use when they died.
 */
public void playerStateChanged(PlayerStateEvent pse)
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
		fOwner.removeDamageListener(this);
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
	if (fMillis-- > 30)
		{
		fOwner.addBlend(0f, 1f, 0f, 0.08f);
		}
	else if ((fMillis & 4) == 4)
		{
		fOwner.addBlend(0f, 1f, 0f, 0.08f);
		}
	
	if (fOwner.getWaterLevel() == 3 && (fMillis % 25) == 0)
		{
		if (fFirstSound)
			{
			Game.getSoundSupport().fireEvent(fOwner.fEntity, NativeEntity.CHAN_ITEM, Engine.getSoundIndex("player/u_breath1.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fFirstSound = false;
			}
		else
			{
			Game.getSoundSupport().fireEvent(fOwner.fEntity, NativeEntity.CHAN_ITEM, Engine.getSoundIndex("player/u_breath2.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fFirstSound = true;
			}
		}
		
	if (fMillis == 30)
		Game.getSoundSupport().fireEvent(fOwner.fEntity, NativeEntity.CHAN_ITEM, Engine.getSoundIndex("items/airout.wav"), 1, NativeEntity.ATTN_NORM, 0);
		
	if (fMillis == 0)
		reset();
	else
		fOwner.breath(1, false); // give them another little puff
	}
/**
 * When used filter the Player's damage for 30 seconds.
 */
public void use(Player p)
	{
	fOwner = p;
	
	Game.addServerFrameListener(this, 0, 0); // Call us every frame
	fOwner.addPlayerStateListener(this);
	fOwner.addDamageListener(this);
	
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