package q2java.baseq2;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Rockets that have been fired, and are flying through the air.
 */
  
public class Rocket extends GameObject implements ServerFrameListener
	{
	protected float fExpires;	
	protected int fDamage;
	protected int fRadiusDamage;
	protected float fDamageRadius;
	protected GameObject fOwner;
	
/**
 * No-arg constructor.
 */
public Rocket() 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
public void dispose() 
	{
	fEntity.freeEntity();
	Game.removeServerFrameListener(this);
	}
/**
 * Launch a rocket.
 * @exception q2java.GameException The exception description.
 */
public void launch(GameObject owner, Point3f start, Vector3f dir, int damage, int speed, float damageRadius, int radiusDamage) throws q2java.GameException 
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
	fEntity.setOwner(owner.fEntity);
	fExpires = (float)Game.getGameTime() + (8000 / speed); // go away after a while
	fDamage = damage;
	fRadiusDamage = radiusDamage;
	fDamageRadius = damageRadius;
	fEntity.linkEntity();

	// register to be called every server frame
	// so we can animate the rocket's flight.
	Game.addServerFrameListener(this, 0, 0);
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

	// we hit something other than the sky.

	int effect;
	if ((Engine.getPointContents(fEntity.getOrigin()) & Engine.MASK_WATER) == 0)
		effect = Engine.TE_ROCKET_EXPLOSION;
	else
		effect = Engine.TE_ROCKET_EXPLOSION_WATER;
				
	if (tr.fEntity.getReference() instanceof GameObject)
		{
		GameObject victim = (GameObject) tr.fEntity.getReference();
		victim.damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 0, 0, effect, "rocket");
		MiscUtil.radiusDamage(this, fOwner, fRadiusDamage, victim.fEntity, fDamageRadius, "r_splash");
		}
	else
		MiscUtil.radiusDamage(this, fOwner, fRadiusDamage, null, fDamageRadius, "r_splash");		
	
	dispose();
	}
}