package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * The quad damage power up.
 * @author Brian Haskin
 */
public class item_quad extends GenericPowerUp implements PlayerStateListener
	{	
	protected Player fOwner;
	protected q2java.gui.IconCountdownTimer fHUDTimer;
	protected int fMillis;
	protected int fNumberUsed;
	protected boolean fClearGlow;
	
	protected float fDebounceTime;
	
	protected final static float DAMAGE_MULTIPLIER = 4F;
	
/**
 * No-arg constructor.
 */
public item_quad() 
	{
	}
public item_quad(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * Get quad icon name.
 * @return java.lang.String
 */
public String getIconName()
	{
	return "p_quad";
	}
/**
 * Get item name.
 * java.lang.String
 */
public String getItemName()
	{
	return "Quad Damage";
	}
/**
 * Get the name of this item's model.
 * @return java.lang.String
 */
public String getModelName() 
	{
	return "models/items/quaddama/tris.md2";
	}
/**
 * Quad's don't respawn for 60 seconds.
 */
public float getRespawnTime()
	{
	return 60;
	}
/**
 * called by the carrying player when they die to give us a chance to reset 
 * there damage multiplier and effects if we were in use when they died.
 */
public void playerStateChanged(Player p, int changeEvent)
	{
	reset();
	}
/**
 * Undo effects of quad.
 */
protected void reset() 
	{
	if (fOwner != null)
		{
		// reset the players damage amount and effects
		fOwner.setDamageMultiplier(fOwner.getDamageMultiplier() / (DAMAGE_MULTIPLIER * fNumberUsed));
		fOwner.fEntity.setEffects(fOwner.fEntity.getEffects() & ~NativeEntity.EF_QUAD);
	
		// disassociate from the player
		fOwner.removePlayerStateListener(this);		
		fOwner = null;
		}
		
	fNumberUsed = 0;
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
	if (fOwner == null)	// Someone above us must want it. Is this really needed?
		{
		super.runFrame(Phase);
		return;
		}
	
	GenericWeapon gw = fOwner.getCurrentWeapon();
	if(gw != null && gw.isFiring()) // we really need a function to check if the weapon was just fired this frame.
		{
		float volume = 1F; 
		
		// change volume when we have a silencer.
		
		float i;
		if ( (i = Game.getGameTime()) > fDebounceTime)
			{
			fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("items/damage3.wav"), volume, NativeEntity.ATTN_NORM, 0);
			fDebounceTime = i+1;
			}
		}
	
	if (fMillis-- > 30)
		{
		fOwner.addBlend(0f, 0f, 1f, 0.08f);
		}
	else if ((fMillis & 4) == 4)
		{
		fOwner.addBlend(0f, 0f, 1f, 0.08f);
		fOwner.fEntity.setEffects(fOwner.fEntity.getEffects() | NativeEntity.EF_QUAD);	 // Our current interface has no clean way for multiple things to place an effect on the player.
											 // But I don't know how much this matters or how much it will really happen. - BH
											 // In CTF, if a flag carrier picks up a Quad, then multiple effects are needed 
											 //   maybe addEffect()/clearEffect() methods would be handy - BP
		fClearGlow = true;
		}
	else if (fClearGlow)
		{
		fOwner.fEntity.setEffects(fOwner.fEntity.getEffects() & ~NativeEntity.EF_QUAD);	// Maybe this should be defined in NativeEntity as EF_CLEAR or some such?
		fClearGlow = false;
		}
	
	if (fMillis == 30)
		{
		fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("items/damage2.wav"), 1, NativeEntity.ATTN_NORM, 0);
		}
	
	if (fMillis == 0)
		reset();
	}
/**
 * Increase the Players damage multiplier for 30 seconds when used.
 */
public void use(Player p)
	{
	fOwner = p;
	
	fOwner.setDamageMultiplier(fOwner.getDamageMultiplier() * DAMAGE_MULTIPLIER);
	fNumberUsed++;
	
	Game.addFrameListener(this, 0, 0); // Call us every frame
	fOwner.addPlayerStateListener(this);
	
	fMillis += 300;
	
	if (fHUDTimer == null)
		{
		fHUDTimer = new q2java.gui.IconCountdownTimer(fOwner.fEntity, NativeEntity.STAT_TIMER_ICON, Engine.getImageIndex("p_quad"), NativeEntity.STAT_TIMER, (fMillis/10)-1);
		fHUDTimer.setVisible(true);
		fHUDTimer.setRunning(true);
		}
	else
		{
		fHUDTimer.setValue((fMillis/10)-1);
		fHUDTimer.setVisible(true);
		fHUDTimer.setRunning(true);
		}
	
	fOwner.fEntity.sound( NativeEntity.CHAN_ITEM, Engine.getSoundIndex("items/damage.wav"), 1, NativeEntity.ATTN_NORM, 0);
	fOwner.fEntity.setEffects(fOwner.fEntity.getEffects() | NativeEntity.EF_QUAD);
	}
}