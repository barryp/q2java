
package q2jgame.weapon;

import q2java.*;
import q2jgame.*;

public abstract class PlayerWeapon
	{
	protected Player fOwner;
		
	protected final static int DEFAULT_BULLET_HSPREAD		= 300;
	protected final static int DEFAULT_BULLET_VSPREAD		= 500;
	protected final static int DEFAULT_SHOTGUN_HSPREAD		= 1000;
	protected final static int DEFAULT_SHOTGUN_VSPREAD		= 500;	
	protected final static int DEFAULT_DEATHMATCH_SHOTGUN_COUNT		= 12;
	protected final static int DEFAULT_SHOTGUN_COUNT				= 12;
	protected final static int DEFAULT_SSHOTGUN_COUNT				= 20;	
	
	// keep our own copy of the animation frame, so
	// we don't have to go back and forth to C so much
	private int fGunFrame;

	private String fWeaponName;
	private String fAmmoType;

	// animation settings
	private int fWeaponModel;
	private int fFrameActivateLast;
	private int fFrameFireLast;
	private int fFrameIdleLast;
	private int fFrameDeactivateLast;
	private int[] fPauseFrames;
	private int[] fFireFrames;	

	// private fields to manage the state of the weapon and
	// its animation
	private int fWeaponState;
	private boolean fIsSwitching;
	
	private final static int WEAPON_UNUSED		= 0;
	private final static int WEAPON_READY		= 1;
	private final static int WEAPON_ACTIVATING	= 2;
	private final static int WEAPON_DROPPING		= 3;
	private final static int WEAPON_FIRING		= 4;
	
/**
 * This method was created by a SmartGuide.
 */
public PlayerWeapon(String ammoType, String weaponModelName, int lastActivate, int lastFire, int lastIdle, int lastDeactivate, int[] pauseFrames, int[] fireFrames) throws GameException
	{
	fAmmoType = ammoType;
Engine.debugLog("Created weapon with ammoType = " + fAmmoType);	
	// animation settings
	fWeaponModel = Engine.modelIndex(weaponModelName);
	fFrameActivateLast = lastActivate;
	fFrameFireLast = lastFire;
	fFrameIdleLast = lastIdle;
	fFrameDeactivateLast = lastDeactivate;
	fPauseFrames = pauseFrames;
	fFireFrames = fireFrames;
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	fIsSwitching = true;
	fWeaponState = WEAPON_ACTIVATING;
	setWeaponFrame(0);
	fOwner.setAmmoType(fAmmoType);
	}
/**
 * This method was created by a SmartGuide.
 */
public void deactivate() 
	{
	fIsSwitching = true;
	}
/**
 * This method was created by a SmartGuide.
 */
public abstract void fire();

/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param aimDir q2java.Vec3
 * @param damage int
 * @param kick int
 * @param teImpact int
 * @param hSpread int
 * @param vSpread int
 */
protected final static void fireLead(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick, int teImpact, int hSpread, int vSpread) 
	{
	TraceResults	tr;
	Vec3		dir;
	Vec3		forward = new Vec3();
	Vec3		right = new Vec3();
	Vec3		up = new Vec3();
	Vec3		end;
	float	r;
	float	u;
	Vec3		waterStart = new Vec3();
	boolean	water = false;
	int		content_mask = Engine.MASK_SHOT | Engine.MASK_WATER;

	tr = Engine.trace(p.getOrigin(), start, p, Engine.MASK_SHOT);
	if (!(tr.fFraction < 1.0))
		{
		dir = aimDir.toAngles();
		dir.angleVectors(forward, right, up);

		r = (float) (Game.cRandom() * hSpread);
		u = (float) (Game.cRandom() * vSpread);
		end = start.vectorMA(8192, forward);
		end = end.vectorMA(r, right);
		end = end.vectorMA(u, up);

		if ((Engine.pointContents(start) & Engine.MASK_WATER) != 0)
			{
			water = true;
			waterStart = new Vec3(start);
			content_mask &= ~Engine.MASK_WATER;
			}

		tr = Engine.trace(start, end, p, content_mask);

		// see if we hit water
		if ((tr.fContents & Engine.MASK_WATER) != 0)
			{
			int		color;

			water = true;
			waterStart = new Vec3(tr.fEndPos);

			if (!start.equals(tr.fEndPos))
				{
				if ((tr.fContents & Engine.CONTENTS_WATER) != 0)
					{
					if (tr.fSurfaceName.equals("*brwater"))
						color = Engine.SPLASH_BROWN_WATER;
					else
						color = Engine.SPLASH_BLUE_WATER;
					}
				else if ((tr.fContents & Engine.CONTENTS_SLIME) != 0)
					color = Engine.SPLASH_SLIME;
				else if ((tr.fContents & Engine.CONTENTS_LAVA) != 0)
					color = Engine.SPLASH_LAVA;
				else
					color = Engine.SPLASH_UNKNOWN;

				if (color != Engine.SPLASH_UNKNOWN)
					{
					Engine.writeByte(Engine.SVC_TEMP_ENTITY);
					Engine.writeByte(Engine.TE_SPLASH);
					Engine.writeByte(8);
					Engine.writePosition(tr.fEndPos);
					Engine.writeDir(tr.fPlaneNormal);
					Engine.writeByte(color);
					Engine.multicast(tr.fEndPos, Engine.MULTICAST_PVS);
					}

				// change bullet's course when it enters water
				dir = new Vec3(end).subtract(start).toAngles();
				dir.angleVectors(forward, right, up);
				r = (float)(Game.cRandom() * hSpread * 2);
				u = (float)(Game.cRandom() * vSpread * 2);
				end = waterStart.vectorMA(8192, forward);
				end = end.vectorMA(r, right);
				end = end.vectorMA(u, up);
				}

			// re-trace ignoring water this time
			tr = Engine.trace(waterStart, end, p, Engine.MASK_SHOT);
			}
		}

	// send gun puff / flash
	if (!((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0)))
		{
		if (tr.fFraction < 1.0)
			{
/*			
			if (tr.ent->takedamage)
				{
				T_Damage (tr.ent, self, self, aimdir, tr.endpos, tr.plane.normal, damage, kick, DAMAGE_BULLET);
				}
			else
*/			
				{
				if (!tr.fSurfaceName.startsWith("sky"))
					{
					Engine.writeByte(Engine.SVC_TEMP_ENTITY);
					Engine.writeByte(teImpact);
					Engine.writePosition(tr.fEndPos);
					Engine.writeDir(tr.fPlaneNormal);
					Engine.multicast(tr.fEndPos, Engine.MULTICAST_PVS);

//					if (self->client)
//						PlayerNoise(self, tr.endpos, PNOISE_IMPACT);
					}
				}
			}
		}

	// if went through water, determine where the end and make a bubble trail
	if (water)
		{
		Vec3 pos;

		dir = new Vec3(tr.fEndPos).subtract(waterStart).normalize();
		pos = tr.fEndPos.vectorMA(-2, dir);
		if ((Engine.pointContents(pos) & Engine.MASK_WATER) != 0)
			tr.fEndPos = new Vec3(pos);
		else
			tr = Engine.trace(pos, waterStart, tr.fEntity, Engine.MASK_WATER);

		pos = new Vec3(waterStart).add(tr.fEndPos).scale(0.5F);

		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_BUBBLETRAIL);
		Engine.writePosition(waterStart);
		Engine.writePosition(tr.fEndPos);
		Engine.multicast(pos, Engine.MULTICAST_PVS);
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param forward q2java.Vec3
 * @param damage int
 * @param kick int
 */
protected static void fireRail(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick) 
	{
	Vec3			from;
	Vec3			end;
	TraceResults	tr = null;
	GameEntity 	ignore;
	int			mask;
	boolean		water;

	end = start.vectorMA(8192, aimDir);
	from = new Vec3(start);
	ignore = p;
	water = false;
	mask = Engine.MASK_SHOT | Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA;
	while (ignore != null)
		{
		tr = Engine.trace(from, end, ignore, mask);

		if ((tr.fContents & (Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA)) != 0)
			{
			mask &= ~(Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA);
			water = true;
			}
		else
			{
/*			if ((tr.ent->svflags & SVF_MONSTER) || (tr.ent->client))
				ignore = tr.ent;
			else
*/				ignore = null;

//			if ((tr.ent != self) && (tr.ent->takedamage))
//				T_Damage (tr.ent, self, self, aimdir, tr.endpos, tr.plane.normal, damage, kick, 0);
			}

		tr.fEndPos.copyTo(from);
		}

	// send gun puff / flash
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_RAILTRAIL);
	Engine.writePosition(start);
	Engine.writePosition(tr.fEndPos);
	Engine.multicast(tr.fEndPos, Engine.MULTICAST_PHS);
//	Engine.multicast(start, Engine.MULTICAST_PHS);
	if (water)
		{
		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_RAILTRAIL);
		Engine.writePosition(start);
		Engine.writePosition(tr.fEndPos);
		Engine.multicast(tr.fEndPos, Engine.MULTICAST_PHS);
		}

//	if (self->client)
//		PlayerNoise(self, tr.endpos, PNOISE_IMPACT);
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param aimDir q2java.Vec3
 * @param damage int
 * @param kick int
 * @param teImpact int
 * @param hSpread int
 * @param vSpread int
 */
protected final static void fireShotgun(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick, int hSpread, int vSpread, int count) 
	{
	for (int i = 0; i < count; i++)
		fireLead(p, start, aimDir, damage, kick, Engine.TE_SHOTGUN, hSpread, vSpread);
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public final int getWeaponFrame() 
	{
	return fGunFrame;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void incWeaponFrame() 
	{
	fOwner.setGunFrame(++fGunFrame);
	}
/**
 * Check whether this weapon has enough ammo to fire.  One unit of
 * ammo is good enough for most weapons.  Special weapons will 
 * override this.
 * @return boolean
 */
public boolean isEnoughAmmo() 
	{
	if (fAmmoType == null)
		return true;
	else		
		return (fOwner.getAmmoCount(fAmmoType) >= 1);
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void setOwner(Player p) 
	{
	fOwner = p;
	}
/**
 * This method was created by a SmartGuide.
 */
public final void setWeaponFrame(int newFrame) 
	{
	fGunFrame = newFrame;
	fOwner.setGunFrame(fGunFrame);
	}
/**
 * This method was created by a SmartGuide.
 */
public void weaponThink() 
	{
	if (fWeaponState == WEAPON_UNUSED)
		return;
		
	if (fWeaponState == WEAPON_DROPPING)
		{
		if (fGunFrame == 0)
			{
			fWeaponState = WEAPON_UNUSED;
			fIsSwitching = false;
			fOwner.changeWeapon();
			}
		else		
			{
			if (fGunFrame < fFrameDeactivateLast)
				incWeaponFrame();
			else
				setWeaponFrame(0);
			}				
		return;
		}

	if (fWeaponState == WEAPON_ACTIVATING)
		{
		if (fIsSwitching)
			{
			fOwner.setGunIndex(fWeaponModel);
			fIsSwitching = false;
			}
			
		if (fGunFrame  < fFrameActivateLast)
			incWeaponFrame();
		else
			{
			fWeaponState = WEAPON_READY;
			setWeaponFrame(fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			}
		return;
		}

	if (fIsSwitching && (fWeaponState != WEAPON_FIRING))
		{
		fWeaponState = WEAPON_DROPPING;
		setWeaponFrame(fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
		return;
		}

	if (fWeaponState == WEAPON_READY)
		{
		if (fOwner.isAttacking())
			{
			if (isEnoughAmmo())
				{
				fWeaponState = WEAPON_FIRING;
				setWeaponFrame(fFrameActivateLast + 1); // FRAME_FIRE_FIRST = FRAME_ACTIVATE_LAST + 1
				}
			else
				{
				fOwner.sound(NativeEntity.CHAN_VOICE, Engine.soundIndex("weapons/noammo.wav"), 1, NativeEntity.ATTN_NORM, 0);
				fWeaponState = WEAPON_DROPPING;
				setWeaponFrame(fFrameIdleLast + 1); // FRAME_DEACTIVATE_FIRST = FRAME_IDLE_LAST + 1
				return;
				}				
			}
		else
/*		
		if ( ((ent->client->latched_buttons|ent->client->buttons) & BUTTON_ATTACK) )
			{
			ent->client->latched_buttons &= ~BUTTON_ATTACK;
			if ((!ent->client->ammo_index) || 
				( ent->client->pers.inventory[ent->client->ammo_index] >= ent->client->pers.weapon->quantity))
				{
				ent->client->ps.gunframe = FRAME_FIRE_FIRST;
				ent->client->weaponstate = WEAPON_FIRING;

				// start the animation
				ent->client->anim_priority = ANIM_ATTACK;
				if (ent->client->ps.pmove.pm_flags & PMF_DUCKED)
					{
					ent->s.frame = FRAME_crattak1-1;
					ent->client->anim_end = FRAME_crattak9;
					}
				else
					{
					ent->s.frame = FRAME_attack1-1;
					ent->client->anim_end = FRAME_attack8;
					}
				}
			else
				{
				if (level.time >= ent->pain_debounce_time)
					{
					gi.sound(ent, CHAN_VOICE, gi.soundindex("weapons/noammo.wav"), 1, ATTN_NORM, 0);
					ent->pain_debounce_time = level.time + 1;
					}
				NoAmmoWeaponChange (ent);
				}
			}
		else
*/		
			{
			if (fGunFrame == fFrameIdleLast)
				{
				setWeaponFrame(fFrameFireLast + 1); // FRAME_IDLE_FIRST = FRAME_IDLE_LAST + 1
				return;
				}

			if (fPauseFrames != null)
				{
				int n;
				for (n = 0; fPauseFrames[n] != 0; n++)
					{
					if (fGunFrame == fPauseFrames[n])
						{
						if ((Game.randomInt() & 15) != 0)
							return;
						}
					}
				}
			incWeaponFrame();
			return;
			}
			
		}

	if (fWeaponState == WEAPON_FIRING)
		{
		int n;
		for (n = 0; fFireFrames[n] != 0; n++)
			{
			if (fGunFrame == fFireFrames[n])
				{
/*				
				if (ent->client->quad_framenum > level.framenum)
					gi.sound(ent, CHAN_ITEM, gi.soundindex("items/damage3.wav"), 1, ATTN_NORM, 0);
*/
				fire();
				break;
				}
			}
	
		if (fFireFrames[n] == 0)
			incWeaponFrame();

		if (fGunFrame == fFrameFireLast + 2) // FRAME_IDLE_FIRST = FRAME_FIRE_LAST + 1
			fWeaponState = WEAPON_READY;
		}
		
	}
}