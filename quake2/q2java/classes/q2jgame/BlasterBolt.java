
package q2jgame;

/**
 * Blaster Bolts that have been fired, 
 * and are flying through the air.
 */
 
import q2java.*;
 
public class BlasterBolt extends GameEntity 
	{
	private float fExpires;	
	private int fDamage;
	
/**
 * Create a blaster bolt and send it on its way.
 * @exception q2java.GameException The exception description.
 */
public BlasterBolt(GameEntity owner, Vec3 start, Vec3 dir, int damage, int speed, int effect) throws q2java.GameException 
	{
	super();
	setOrigin(start);
	setAngles(dir.toAngles());
	dir.normalize().scale(speed);
	setVelocity(dir);
	setClipmask(Engine.MASK_SHOT);
	setSolid(SOLID_BBOX);
	setEffects(effect);
	setModelIndex(Engine.modelIndex("models/objects/laser/tris.md2"));
	setSound(Engine.soundIndex("misc/lasfly.wav"));
	setOwner(owner);
	fExpires = (float)Game.gGameTime + 2; // go away after 2 seconds
	fDamage = damage;
	linkEntity();
/*
	tr = gi.trace (self->s.origin, NULL, NULL, bolt->s.origin, bolt, MASK_SHOT);
	if (tr.fraction < 1.0)
		{
		VectorMA (bolt->s.origin, -10, dir, bolt->s.origin);
		bolt->touch (bolt, tr.ent, NULL, NULL);
		}		
*/
	}
/**
 * Animate the blaster bolt over one frame.
 */
public void runFrame() 
	{
	if (Game.gGameTime >= fExpires)
		{
		freeEntity();
		return;
		}

	TraceResults tr = traceMove(Engine.MASK_SHOT, 1.0F);
	
	if (tr.fFraction == 1)
		return;	// moved the entire distance without hitting anything

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		freeEntity();
		return;
		}

	// we hit something other than the sky.  Damage it and remove the bolt.
	((GameEntity)tr.fEntity).damage(this, (GameEntity)getOwner(), getVelocity(), getOrigin(), tr.fPlaneNormal, fDamage, 1, DAMAGE_ENERGY, Engine.TE_BLASTER);
	freeEntity();
	}
}	