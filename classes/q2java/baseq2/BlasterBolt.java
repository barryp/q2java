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
	protected float fExpires;	
	protected int fDamage;
	protected GameObject fOwner;
	protected String fObitKey;
	
/**
 * No-arg constructor.
 */
public BlasterBolt() 
	{
	}
/**
 * Get rid of the blaster bolt.
 */
public void dispose() 
	{
	fEntity.freeEntity();
	Game.removeFrameListener(this);
	}
/**
 * We hit some NativeEntity in the world.
 * @param ent q2java.NativeEntity
 */
public void hit(TraceResults tr) 
	{
	if ((tr.fSurfaceName == null) || ((tr.fSurfaceFlags & Engine.SURF_SKY) == 0))
		{
		// hit something besides the sky..see if we can damage it
		Object obj = tr.fEntity.getReference();
		if (obj instanceof GameObject)
			((GameObject)obj).damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 1, GameObject.DAMAGE_ENERGY, Engine.TE_BLASTER, fObitKey);
		}
		
	// get rid of the bolt
	dispose();
	}
/**
 * Create a blaster bolt and send it on its way.
 * @exception q2java.GameException The exception description.
 */
public void launch(GameObject owner, Point3f start, Vector3f dir, int damage, int speed, int effect, String obitKey) throws q2java.GameException 
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
	fEntity.setOwner(owner.fEntity); // required for initial trace to work properly

	fExpires = (float)Game.getGameTime() + 2; // go away after 2 seconds
	fDamage = damage;
	fObitKey = obitKey;
	fEntity.linkEntity();

	// do a trace to check for firing against a wall
	TraceResults tr = Engine.trace(owner.fEntity.getOrigin(), start, fEntity, Engine.MASK_SHOT);
	
	if (tr.fFraction < 1.0)
		{
		// ran into something immediately
		start.scaleAdd(-10F/speed, dir, start);
		fEntity.setOrigin(start);
		hit(tr);
		return;
		}
	else
		// gotta animate the bolt
		Game.addFrameListener(this, 0, 0);
	}
/**
 * Animate the blaster bolt over one frame.
 */
public void runFrame(int phase) 
	{
	if (Game.getGameTime() >= fExpires)
		{
		// get rid of the bolt
		dispose();
		return;
		}

	TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F);

	if (tr.fFraction < 1)
		hit(tr); // we hit something!
	}
}