
package q2jgame;

/**
 * Rockets that have been fired, and are flying through the air.
 */
 
import q2java.*;
 
public class Rocket extends GameEntity 
	{
	private float fExpires;	
	private int fDamage;
	private int fRadiusDamage;
	private float fDamageRadius;
	
/**
 * BlasterBolt constructor comment.
 * @exception q2java.GameException The exception description.
 */
public Rocket(GameEntity owner, Vec3 start, Vec3 dir, int damage, int speed, float damageRadius, int radiusDamage) throws q2java.GameException 
	{
	super();
	setOrigin(start);
	setAngles(dir.toAngles());
	dir.scale(speed); // this seems wrong...I would think the direction should be normalized first, like the blaster is.
	setVelocity(dir);
	setClipmask(Engine.MASK_SHOT);
	setSolid(SOLID_BBOX);
	setEffects(EF_ROCKET);
	setModelIndex(Engine.modelIndex("models/objects/rocket/tris.md2"));
	setSound(Engine.soundIndex("weapons/rockfly.wav"));
	setOwner(owner);
	fExpires = (float)Game.gGameTime + (8000 / speed); // go away after a while
	fDamage = damage;
	fRadiusDamage = radiusDamage;
	fDamageRadius = damageRadius;
	linkEntity();
/*
	if (self->client)
		check_dodge (self, bolt->s.origin, dir, speed);
*/
	}
/**
 * Go away the first chance we get to think
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
		return;		// moved the entire distance

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		freeEntity();
		return;
		}

	int effect;
	if ((Engine.pointContents(getOrigin()) & Engine.MASK_WATER) == 0)
		effect = Engine.TE_ROCKET_EXPLOSION;
	else
		effect = Engine.TE_ROCKET_EXPLOSION_WATER;
				
	// we hit something other than the sky.
	GameEntity victim = (GameEntity) tr.fEntity;
	GameEntity attacker = (GameEntity) getOwner();

	victim.damage(this, attacker, getVelocity(), getOrigin(), tr.fPlaneNormal, fDamage, 0, 0, effect);
	Game.radiusDamage(this, attacker, fRadiusDamage, victim, fDamageRadius);
	
	freeEntity();
	}
}	