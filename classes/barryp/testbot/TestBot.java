package barryp.testbot;


import java.text.MessageFormat;
import java.util.Locale;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;
import java.util.*;

/**
 * Simple test bot
 *
 */
public class TestBot extends baseq2.Player implements LevelListener
	{
	protected Player fLastAttacker;
	protected boolean fLevelChanged;
	
	protected final static int RESPAWN_INTERVAL = 15;
	protected final static String BUNDLE_NAME = "barryp.testbot.talk";
	
/**
 * TestBot constructor comment.
 * @param ent q2java.NativeEntity
 * @param loadgame boolean
 * @exception q2java.GameException The exception description.
 */
TestBot(q2java.NativeEntity ent, String name) throws q2java.GameException
	{
	super(ent, false);

	setName(name);
	setSkin("male/grunt");

	fEntity.setGroundEntity(baseq2.GameModule.gWorld.fEntity);

	// place the bot into the game
	playerBegin(false);

	// ask to be notified when levels change (so the bot can respawn)
	Game.addLevelListener(this);
	}
/**
 * Extend the Player.clearSettings() method to clear
 * some bot fields.
 */
public void clearSettings() 
	{
	super.clearSettings();
	fLastAttacker = null;
	}
/**
 * Inflict damage on the Player. 
 * If the player takes enough damage, this function will call the Player.die() function.
 * @param inflictor the entity that's causing the damage, such as a rocket.
 * @param attacker the entity that's gets credit for the damage, for example the player who fired the above example rocket.
 * @param dir the direction the damage is coming from.
 * @param point the point where the damage is being inflicted.
 * @param normal surface normal at the point damage is inflicted
 * @param damage how much damage the player is being hit with.
 * @param knockback how much the player should be pushed around because of the damage.
 * @param dflags flags indicating the type of damage, corresponding to GameEntity.DAMAGE_* constants.
 */
public void damage(GameObject inflictor, GameObject attacker,
	Vector3f dir, Point3f point, Vector3f normal,
	int damage, int knockback, int dflags, int tempEvent, String obitKey)
	{
	// turn and face the attacker
	Angle3f ang = fEntity.getAngles();
	ang.y = calcAttackerYaw(inflictor, attacker);
	fEntity.setAngles(ang);

	if (attacker instanceof Player)
		{
		Player p = (Player) attacker;
		if (p != fLastAttacker)
			{
			// randomly pick a reaction message from the resource bundle
			Object[] args = {p.getName()};
			playerCommand("say " + fResourceGroup.format("barryp.testbot.Messages", "react", args));
			fLastAttacker = p;
			}
		}

	super.damage(inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent, obitKey);
	}
/**
 * Work around a protected method.
 */
public void doRespawn()
	{
	respawn();
	Game.dprint("Test bot respawned at " + fEntity.getOrigin() + "\n");
	}
/**
 * Get this bot's locale
 */
public Locale getLocale()
	{
	return fResourceGroup.getLocale();
	}
/**
 * Called when a new level is starting, after entities have been spawned.
 */
public void levelEntitiesSpawned()
	{
	// Make a note of the level change
	fLevelChanged = true;
	}
/**
 * Disconnect the Bot.
 */
public void playerDisconnect()
	{
	NativeEntity ent = fEntity;
	super.playerDisconnect();
	ent.freeEntity();
	}
/**
 * Tick-tock, tick-tock, the game moves along.
 * @param phase int
 */
public void runFrame(int phase)
	{
	super.runFrame(phase);

	// if the level has changed, re-place the
	// bot into the game
	if (fLevelChanged)
		{
		fLevelChanged = false;
		playerBegin(false);
		}
		
	// if the fRespawnTime field isn't zero, then
	// the bot must be dead, and that field shows
	// the soonest the bot/player is allowed to reenter
	// the game.  If that time has been reached, stick
	// the bot back in.
	if ((fRespawnTime > 0) && (Game.getGameTime() > (fRespawnTime + RESPAWN_INTERVAL)))
		{
		fRespawnTime = 0;
		respawn();
		}
	}
/**
 * Set the bot's locale
 * @param val Locale string such as "en_GB" or "en_US_redneck", null resets to default locale.
 */
public void setLocale(String val)
	{
	setPlayerInfo("locale", val);
	}
/**
 * Set the name of the bot.
 * @param name java.lang.String
 */
public void setName(String name)
	{
	setPlayerInfo("name", name);
	}
/**
 * Set the skin of the bot.
 * @param name java.lang.String
 */
public void setSkin(String name) 
	{
	setPlayerInfo("skin", name);
	}
/**
 * Called when a new level is starting, before entities are spawned.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	}
/**
 * Do nothing - sending a scoreboard to a bot would crash the game.
 * @param killer q2jgame.Player
 */
protected void writeDeathmatchScoreboardMessage(GameObject killer) 
	{	
	}
}