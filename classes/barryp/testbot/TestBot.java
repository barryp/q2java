
package barryp.testbot;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Simple test bot
 * 
 */
public class TestBot extends baseq2.Player 
	{
	protected float fRespawnTime;
	protected Player fLastAttacker;
		
	protected final static int RESPAWN_INTERVAL = 10;
	
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

	clearSettings();
	spawn();
	Game.dprint("Test bot spawned at " + fEntity.getOrigin() + "\n");
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
 * @param normal q2java.Vec3
 * @param damage how much damage the player is being hit with.
 * @param knockback how much the player should be pushed around because of the damage.
 * @param dflags flags indicating the type of damage, corresponding to GameEntity.DAMAGE_* constants.
 */
public void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent) 
	{
	super.damage(inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent);

	// turn and face the attacker
	Angle3f ang = fEntity.getAngles();
	ang.y = calcAttackerYaw(inflictor, attacker);
	fEntity.setAngles(ang);
	
	if (attacker instanceof TestBot)
		return;
		
	if (attacker instanceof Player)
		{
		Player p = (Player) attacker;
		if (p != fLastAttacker)
			{
			Game.bprint(Engine.PRINT_CHAT, getName() + ": You better watch that shit " + p.getName() + "!\n");			
			fLastAttacker = p;			
			}
		}		
	}
/**
 * Simplified Player death...normal player death used with bots
 * causes the game to crash.
 */
protected void die(GameObject inflictor, GameObject attacker, int damage, Point3f point)
	{
	if (fIsDead)
		return;	// already dead

	fIsDead = true;
	fEntity.setModelIndex2(0); // remove linked weapon model
	fEntity.setPlayerGunIndex(0);
	fWeapon = null;
	fEntity.setSolid(NativeEntity.SOLID_NOT);
					
	obituary(inflictor, attacker);
			
	if (getHealth() < -40)
		{	// gib
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("misc/udeath.wav"), 1, NativeEntity.ATTN_NORM, 0);
//		for (n= 0; n < 4; n++)
//			ThrowGib (self, "models/objects/gibs/sm_meat/tris.md2", damage, GIB_ORGANIC);
//		ThrowClientHead (self, damage);

//		self->takedamage = DAMAGE_NO;

		// workaround until full gib code is implemented
		setAnimation(ANIMATE_DEATH);
		}
	else
		{	// normal death
		setAnimation(ANIMATE_DEATH);
		fEntity.sound(NativeEntity.CHAN_VOICE, getSexedSoundIndex("death"+((MiscUtil.randomInt() & 0x03) + 1)), 1, NativeEntity.ATTN_NORM, 0);
		}
		
	fRespawnTime = Game.getGameTime() + RESPAWN_INTERVAL;
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
 * Disconnect the Bot.
 */
public void playerDisconnect() 
	{
	super.playerDisconnect();
	fEntity.freeEntity();
	}
/**
 * This method was created by a SmartGuide.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	super.runFrame(phase);
	if ((fRespawnTime > 0) && (Game.getGameTime() > fRespawnTime))
		{
		fRespawnTime = 0;
		respawn();
		}
	}
/**
 * Set the name of the bot.
 * @param name java.lang.String
 */
public void setName(String name) 
	{
	setUserInfo("name", name);
	applyPlayerInfo();
	}
/**
 * Set the skin of the bot.
 * @param name java.lang.String
 */
public void setSkin(String name) 
	{
	setUserInfo("skin", name);
	applyPlayerInfo();
	}
}