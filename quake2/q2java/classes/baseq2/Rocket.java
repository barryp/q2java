
package baseq2;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Rockets that have been fired, and are flying through the air.
 */
  
public class Rocket extends GameObject implements FrameListener
	{
	private float fExpires;	
	private int fDamage;
	private int fRadiusDamage;
	private float fDamageRadius;
	private GameObject fOwner;
	
/**
 * BlasterBolt constructor comment.
 * @exception q2java.GameException The exception description.
 */
public Rocket(GameObject owner, Point3f start, Vector3f dir, int damage, int speed, float damageRadius, int radiusDamage) throws q2java.GameException 
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);
	
	fEntity.setOrigin(start);
	fEntity.setAngles(new Angle3f(dir));
	dir.scale(speed); // this seems wrong...I would think the direction should be normalized first, like the blaster is.
	fEntity.setVelocity(dir);
	fEntity.setClipmask(Engine.MASK_SHOT);
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setEffects(NativeEntity.EF_ROCKET);
	fEntity.setModelIndex(Engine.getModelIndex("models/objects/rocket/tris.md2"));
	fEntity.setSound(Engine.getSoundIndex("weapons/rockfly.wav"));
	fOwner = owner;
	fExpires = (float)Game.getGameTime() + (8000 / speed); // go away after a while
	fDamage = damage;
	fRadiusDamage = radiusDamage;
	fDamageRadius = damageRadius;
	fEntity.linkEntity();
/*
	if (self->client)
		check_dodge (self, bolt->s.origin, dir, speed);
*/

	Game.addFrameListener(this, 0, 0);
	}
/**
 * This method was created by a SmartGuide.
 */
public void dispose() 
	{
	fEntity.freeEntity();
	Game.removeFrameListener(this);
	}
/**
 * Go away the first chance we get to think
 */
public void runFrame(int phase) 
	{
	if (Game.getGameTime() >= fExpires)
		{
		dispose();
		return;
		}

	TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F);
	
	if (tr.fFraction == 1)
		return;		// moved the entire distance

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		dispose();
		return;
		}

	int effect;
	if ((Engine.getPointContents(fEntity.getOrigin()) & Engine.MASK_WATER) == 0)
		effect = Engine.TE_ROCKET_EXPLOSION;
	else
		effect = Engine.TE_ROCKET_EXPLOSION_WATER;

	GameObject attacker = fOwner;
				
	// we hit something other than the sky.
	if (tr.fEntity.getReference() instanceof GameObject)
		{
		GameObject victim = (GameObject) tr.fEntity.getReference();
		victim.damage(this, attacker, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 0, 0, effect);
		MiscUtil.radiusDamage(this, attacker, fRadiusDamage, victim, fDamageRadius);
		}
	else
		MiscUtil.radiusDamage(this, attacker, fRadiusDamage, null, fDamageRadius);		
	
	dispose();
	}
}	