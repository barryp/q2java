package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * The environment suit.
 * @author Brian Haskin
 */
public class item_enviro extends GenericPowerUp implements PlayerStateListener, DamageFilter
	{	
	protected Player fOwner;
	protected q2java.gui.IconCountdownTimer fHUDTimer;
	protected int fMillis;
	protected float fAirFinished;
	protected boolean fFirstSound;
	
/**
 * No-arg constructor.
 */
public item_enviro() 
	{
	}
public item_enviro(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Called by the carrying player when they take damage.
 */
public DamageObject filterDamage(DamageObject damage)
	{
	if (damage.fInflictor == baseq2.GameModule.gWorld && damage.fObitKey.equals("slime"))
		{
		damage.fAmount = 0;
		}
	else if (damage.fInflictor == baseq2.GameModule.gWorld && damage.fObitKey.equals("lava"))
		{
		damage.fAmount /= 3;
		}
		
	return damage;
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
public void playerStateChanged(Player p, int changeEvent)
	{
	reset();
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
		fOwner.removeDamageFilter(this);
		fOwner = null;
		}
		
	fMillis = 0;
	Game.removeFrameListener(this);

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
 * When used filter the Player's damage for 30 seconds.
 */
public void use(Player p)
	{
	fOwner = p;
	
	Game.addFrameListener(this, 0, 0); // Call us every frame
	fOwner.addPlayerStateListener(this);
	fOwner.addDamageFilter(this);
	
	fMillis += 300;
	
	fAirFinished = Game.getGameTime() + (fMillis / 10) + 10;
	
	if (fHUDTimer == null)
		{
		fHUDTimer = new q2java.gui.IconCountdownTimer(fOwner.fEntity, NativeEntity.STAT_TIMER_ICON, Engine.getImageIndex("p_envirosuit"), NativeEntity.STAT_TIMER, (fMillis/10)-1);
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