package q2java.baseq2;


import javax.vecmath.*;
import q2java.*;
import q2java.core.*;

/**
 * Grenades that have been thrown by a player,
 * and are flying through the air.
 */
  
public abstract class GenericGrenade extends GameObject implements FrameListener
	{
	protected float      fExpires;	
	protected int        fDamage;
	protected float      fRadiusDamage;
	protected GameObject fOwner;
	protected Vector3f   fAvelocity;
	
	static final float STOP_EPSILON = 0.1f;
	
/**
 * No-arg constructor.
 */
public GenericGrenade() 
	{
	}	
void checkVelocity()
	{
	int		 i;
	Vector3f v;
	float    maxv;

	//
	// bound velocity
	//
	v    = fEntity.getVelocity();
	maxv = BaseQ2.gMaxVelocity.getFloat();

	if (v.x >  maxv)
		v.x =  maxv;
	else if (v.x < -maxv)
		v.x = -maxv;

	if (v.y >  maxv)
		v.y =  maxv;
	else if (v.y < -maxv)
		v.y = -maxv;

	if (v.z >  maxv)
		v.z =  maxv;
	else if (v.z < -maxv)
		v.z = -maxv;

	fEntity.setVelocity(v);
	}
/**
 * Slide off of the impacting object
 *  returns the blocked flags (1 = floor, 2 = step / wall)
 */
int clipVelocity (Vector3f normal, float overbounce)
	{
	float	 backoff;
	int		 i, blocked;
	Vector3f v;
	
	blocked = 0;
	if (normal.z > 0)
		blocked |= 1;		// floor
	if (normal.z == 0)
		blocked |= 2;		// step
	
	v       = fEntity.getVelocity();
	backoff = v.dot(normal) * overbounce;

	v.x -= normal.x * backoff;
	v.y -= normal.y * backoff;
	v.z -= normal.z * backoff;

	if (v.x > -STOP_EPSILON && v.x < STOP_EPSILON)
		v.x = 0;
	if (v.y > -STOP_EPSILON && v.y < STOP_EPSILON)
		v.y = 0;
	if (v.z > -STOP_EPSILON && v.z < STOP_EPSILON)
		v.z = 0;

	fEntity.setVelocity(v);

	return blocked;
	}
/**
 * Disassociate the GenericGrenade from the rest of the game.
 */
public void dispose() 
	{
	if (fEntity != null)	
		fEntity.freeEntity();
	Game.removeFrameListener(this);
	}
protected void explode( TraceResults tr )
	{
	int        effect;
	GameObject victim = null;

	if ((Engine.getPointContents(fEntity.getOrigin()) & Engine.MASK_WATER) == 0)
		{
		if (fEntity.getGroundEntity() != null)
			effect = Engine.TE_GRENADE_EXPLOSION;
		else
			effect = Engine.TE_ROCKET_EXPLOSION;
		}
	else
		{
		if (fEntity.getGroundEntity() != null)
			effect = Engine.TE_GRENADE_EXPLOSION_WATER;
		else
			effect = Engine.TE_ROCKET_EXPLOSION_WATER;
		}

	if ( tr != null )
		{
		//TODO: move victim caused by impact...
		victim = (GameObject)tr.fEntity.getReference();
		if ( victim != null )
			victim.damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, fDamage, 0, 0, effect, "grenade");
		}

	MiscUtil.radiusDamage(this, fOwner, fDamage, victim, fRadiusDamage, "g_splash");

	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(effect);
	Engine.writePosition( fEntity.getOrigin() );
	Engine.multicast( fEntity.getOrigin(), Engine.MULTICAST_PHS);

	}
/**
 * Go away the first chance we get to think
 */
public void runFrame(int phase) 
	{
	GameObject attacker = fOwner;

	if (Game.getGameTime() >= fExpires)
		{
		explode( null );
		dispose();
		return;
		}

	checkVelocity();
	applyGravity();

	TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F); // was MASK_SOLID
	
	if (tr.fFraction == 1)
		{
		Angle3f angles = fEntity.getAngles();
		angles.scaleAdd( Engine.SECONDS_PER_FRAME, fAvelocity, angles );
		fEntity.setAngles(angles);
		return;		// moved the entire distance without hitting anything
		}

	// 'scuse me while I kiss the sky...
	if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
		{
		dispose();
		return;
		}

	if (tr.fFraction < 0.01f)
		return;		// laying on the ground...

	Angle3f angles = fEntity.getAngles();
	angles.scaleAdd( Engine.SECONDS_PER_FRAME, fAvelocity, angles );
	fEntity.setAngles(angles);

	// did we hit a something ??
	Object reference = tr.fEntity.getReference();

	if (reference instanceof Player)	// TODO: Player could also be a monster....
		{
		explode( tr );
		dispose();
		}
	else	//we hit a solid object, so let's bounce...
		{
		clipVelocity(tr.fPlaneNormal, 1.5f);
		fEntity.sound (NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/grenlb1b.wav"), 1, NativeEntity.ATTN_NORM, 0);
		}
	}
/**
 * Setup the grenade and start it running.
 */
public void toss(GameObject owner, Point3f start, Vector3f aimdir, int damage, int speed, float timer, float radiusDamage) throws q2java.GameException 
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);

	Vector3f forward = new Vector3f();
	Vector3f right   = new Vector3f();
	Vector3f up      = new Vector3f();
	Angle3f dir      = new Angle3f(aimdir);
	dir.getVectors( forward, right, up );
	
	fEntity.setOrigin(start);
	aimdir.scale(speed); // this seems wrong...I would think the direction should be normalized first, like the blaster is.

	Vector3f vel = new Vector3f(aimdir);
	vel.scaleAdd( 200 + GameUtil.randomFloat()*10, up,    vel);
	vel.scaleAdd(       GameUtil.randomFloat()*10, right, vel);

	fEntity.setVelocity(vel);

	fAvelocity = new Vector3f( 300, 300, 300 );
	fEntity.setClipmask(Engine.MASK_SHOT); 
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setEffects(NativeEntity.EF_GRENADE);
	fOwner = owner;
	fEntity.setOwner(owner.fEntity);
	fExpires = (float)Game.getGameTime() + timer; // explode after a while
	fDamage = damage;
	fRadiusDamage = radiusDamage;
	fEntity.linkEntity();

	if (timer <= 0.0)
		{
		explode(null);
		dispose();
		}
	else
		{
		Game.addFrameListener(this, 0, 0);
		}
	}
}