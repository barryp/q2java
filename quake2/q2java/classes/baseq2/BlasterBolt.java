
package baseq2;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Blaster Bolts that have been fired, 
 * and are flying through the air.
 */
 
 
public class BlasterBolt extends GameObject implements FrameListener
	{
	private float fExpires;	
	private int fDamage;
	private GameObject fOwner;
	
/**
 * Create a blaster bolt and send it on its way.
 * @exception q2java.GameException The exception description.
 */
public BlasterBolt(GameObject owner, Point3f start, Vector3f dir, int damage, int speed, int effect) throws q2java.GameException 
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);
	
	fEntity.setOrigin(start);
	fEntity.setAngles(new Angle3f(dir));
	dir.normalize();
	dir.scale(speed);
	fEntity.setVelocity(dir);
	fEntity.setClipmask(Engine.MASK_SHOT);
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setEffects(effect);
	fEntity.setModelIndex(Engine.getModelIndex("models/objects/laser/tris.md2"));
	fEntity.setSound(Engine.getSoundIndex("misc/lasfly.wav"));
	fOwner = owner;
	fExpires = (float)Game.getGameTime() + 2; // go away after 2 seconds
	fDamage = damage;
	fEntity.linkEntity();
/*
	tr = gi.trace (self->s.origin, NULL, NULL, bolt->s.origin, bolt, MASK_SHOT);
	if (tr.fraction < 1.0)
		{
		VectorMA (bolt->s.origin, -10, dir, bolt->s.origin);
		bolt->touch (bolt, tr.ent, NULL, NULL);
		}		
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
 * Animate the blaster bolt over one frame.
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
		return;	// moved the entire distance without hitting anything

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		dispose();
		return;
		}

	// we hit something other than the sky.  Damage it and remove the bolt.
	if (tr.fEntity.getReference() instanceof GameObject)
		((GameObject)tr.fEntity.getReference()).damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 1, GameObject.DAMAGE_ENERGY, Engine.TE_BLASTER);
		
	dispose();
	}
}	