
package baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Secret doors - they pop out then open
 */

public class func_door_secret extends GenericPusher 
	{	
	protected float fWait;
	protected float fDmg;
		
	// secret opening movement parameters
	protected Point3f fIntermediateOrigin;
	protected Point3f fOpenedOrigin;
	
	protected boolean fShootingOpens;
	protected int fSecretState;
	protected int fHealth;
	protected int fMaxHealth;
	
	// door sounds if any
	protected int fSoundStart;
	protected int fSoundMiddle;
	protected int fSoundEnd;
		
	protected final static int SECRET_STATE_CLOSED = 0;
	protected final static int SECRET_STATE_OPENING_INTERMEDIATE = 1;
	protected final static int SECRET_STATE_OPENED_INTERMEDIATE  = 2;
	protected final static int SECRET_STATE_OPENING              = 3;
	protected final static int SECRET_STATE_OPENED               = 4;
	protected final static int SECRET_STATE_CLOSING_INTERMEDIATE = 5;
	protected final static int SECRET_STATE_CLOSED_INTERMEDIATE  = 6;
	protected final static int SECRET_STATE_CLOSING              = 7;
	
	protected final static int SECRET_ALWAYS_SHOOT	= 1;
	protected final static int SECRET_1ST_LEFT		= 2;
	protected final static int SECRET_1ST_DOWN		= 4;
	
public func_door_secret(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);
			
	fSpeed = fAccel = fDecel = 50;
	fWait = getSpawnArg("wait", 5);
	fDmg = getSpawnArg("dmg", 2);
	fHealth = fMaxHealth = getSpawnArg("health", 0);
	
	// setup door sounds
	if (getSpawnArg("sounds", 0) != 1)
		{
		fSoundStart = Engine.getSoundIndex("doors/dr1_strt.wav");
		fSoundMiddle = Engine.getSoundIndex("doors/dr1_mid.wav");
		fSoundEnd = Engine.getSoundIndex("doors/dr1_end.wav");
		}
	
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Vector3f up = new Vector3f();
	
	float	side;
	float	width;
	float	length;
	
	// calculate positions
	fEntity.getAngles().getVectors(forward, right, up);
	fEntity.setAngles(0, 0, 0);

	side = 1 - (fSpawnFlags & SECRET_1ST_LEFT);
	if ((fSpawnFlags & SECRET_1ST_DOWN) != 0)
		width = Math.abs(up.dot(fEntity.getSize()));
	else
		width = Math.abs(right.dot(fEntity.getSize()));
		
	length = Math.abs(forward.dot(fEntity.getSize()));

	fIntermediateOrigin = new Point3f();	
	if ((fSpawnFlags & SECRET_1ST_DOWN) != 0)
		fIntermediateOrigin.scaleAdd(-1 * width, up, fEntity.getOrigin());
	else
		fIntermediateOrigin.scaleAdd(side * width, right, fEntity.getOrigin());
		
	fOpenedOrigin = new Point3f();
	fOpenedOrigin.scaleAdd(length, forward, fIntermediateOrigin);
	
	fShootingOpens = ((fTargetGroup == null) || ((fSpawnFlags & SECRET_ALWAYS_SHOOT) != 0));
		
/*
	if (!(ent->targetname) || (ent->spawnflags & SECRET_ALWAYS_SHOOT))
	{
		ent->health = 0;
		ent->takedamage = DAMAGE_YES;
		ent->die = door_secret_die;
	}

	if (ent->health)
	{
		ent->takedamage = DAMAGE_YES;
		ent->die = door_killed;
		ent->max_health = ent->health;
	}
	else if (ent->targetname && ent->message)
	{
		gi.soundindex ("misc/talk.wav");
		ent->touch = door_touch;
	}

*/	
	
	// setup for opening and closing
		
	fEntity.linkEntity();		
	}
/**
 * Handle damaging a door, which in some cases opens it.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent) 
	{
	super.damage(inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent);

	if ((fMaxHealth != 0) || fShootingOpens)
		{
		fHealth -= damage;
		if (fHealth < 0)
			{
			fHealth = fMaxHealth;
			use(null);
			}
		}
	}
/**
 * Called by GenericPusher when the door is finished moving.
 */
public void moveFinished() 
	{
	switch (fSecretState)
		{
		case SECRET_STATE_OPENING_INTERMEDIATE:
			fSecretState = SECRET_STATE_OPENED_INTERMEDIATE;			
			// schedule a one-time notification one second from now
			Game.addFrameListener(this, 1, -1);
			break;
			
		case SECRET_STATE_OPENING:
			fSecretState = SECRET_STATE_OPENED;			
			// schedule a one-time notification fWait seconds from now
			if (fWait != -1)
				Game.addFrameListener(this, fWait, -1);
			break;

		case SECRET_STATE_CLOSING_INTERMEDIATE:
			fSecretState = SECRET_STATE_CLOSED_INTERMEDIATE;			
			// schedule a one-time notification one second from now
			Game.addFrameListener(this, 1, -1);
			break;
			
		case SECRET_STATE_CLOSING:
			fSecretState = SECRET_STATE_CLOSED;
			setPortals(false);
			break;			
		}				
	}
/**
 * This method was created by a SmartGuide.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	switch (fSecretState)
		{
		case SECRET_STATE_CLOSED:
		case SECRET_STATE_OPENING_INTERMEDIATE:
		case SECRET_STATE_OPENING:
		case SECRET_STATE_CLOSING_INTERMEDIATE:
		case SECRET_STATE_CLOSING:
			super.runFrame(phase);
			break;
			
		case SECRET_STATE_OPENED_INTERMEDIATE:
			moveTo(fOpenedOrigin);
			fSecretState = SECRET_STATE_OPENING;
			break;
			
		case SECRET_STATE_OPENED:
			moveTo(fIntermediateOrigin);
			fSecretState = SECRET_STATE_CLOSING_INTERMEDIATE;
			break;			
			
		case SECRET_STATE_CLOSED_INTERMEDIATE:
			moveTo(new Point3f());
			fSecretState = SECRET_STATE_CLOSING;
			break;			
		}
	}
/**
 * Use the door by opening it.
 * @param touchedBy The player who is causing the door to open (may be null).
 */
public void use(Player touchedBy) 
	{
	if (fSecretState == SECRET_STATE_CLOSED)
		{
		fSecretState = SECRET_STATE_OPENING_INTERMEDIATE;
		moveTo(fIntermediateOrigin);
		}
	}
}