package q2java.baseq2;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Rockets that have been fired, and are flying through the air.
 */
  
public class BfgBlast extends GameObject implements ServerFrameListener
	{
	protected float fExpires;	
	protected int fDamage;
	protected int fRadiusDamage;
	protected float fDamageRadius;
	protected GameObject fOwner;

	protected Vector3f v     = new Vector3f();	// used in explode()
	protected Vector3f dir   = new Vector3f();
	protected Point3f  start = new Point3f();
	protected Point3f  end   = new Point3f();

	protected final static Vector3f VECTOR_ORIGIN = new Vector3f(0, 0, 0);

	protected final static int FLYING    = 0;
	protected final static int EXPLODING = 1;
	protected final static int DISPOSING = 2;

	protected int fState;

	public final static int MOD_BFG_LASER  = 12;
	public final static int MOD_BFG_BLAST  = 13;
	public final static int MOD_BFG_EFFECT = 14;
	
/**
 * No-arg constructor.
 */
public BfgBlast() 
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
protected void explode()
	{
	if (fEntity.getFrame() == 0)
		{
		// the BFG effect
		NativeEntity[] ents = Engine.getRadiusEntities( fEntity.getOrigin(), fDamageRadius, true, false );
		// returns only players...should also return monsters
	
		if (ents != null)
			{
			for (int i=0; i<ents.length; i++)
				{
				NativeEntity ent  = ents[i];
				GameObject victim = (GameObject)ent.getReference();

				if ( victim == fOwner )
					continue;
				//if (!ent->takedamage)
				//	continue;
				//if (ent == self->owner)
				//	continue;
				//if (!CanDamage (ent, self))
				//	continue;
				//if (!CanDamage (ent, self->owner))
				//	continue;

				// these next two lines were goofed in that they were
				// using "fEntity" instead of "victim.fEntity" (BBP)
				v.add(victim.fEntity.getMins(), victim.fEntity.getMaxs() );
				v.scaleAdd( 0.5F, victim.fEntity.getOrigin() );

				v.sub( fEntity.getOrigin(), v );
				float dist   = v.length();
				float points = fRadiusDamage * (1F - (float)Math.sqrt(dist/fDamageRadius));
				
				// The next line is NEVER reached... Bug in ID's source !!!
				//if (victim == fOwner)
				//	points *= 0.5;

				Engine.writeByte(Engine.SVC_TEMP_ENTITY);
				Engine.writeByte(Engine.TE_BFG_EXPLOSION);
				Engine.writePosition( fEntity.getOrigin() );
				Engine.multicast( fEntity.getOrigin(), Engine.MULTICAST_PHS);
				
				victim.damage( this, fOwner, fEntity.getVelocity(), ent.getOrigin(), VECTOR_ORIGIN, (int)points, 0, DAMAGE_ENERGY, Engine.TE_NONE, "bfg_effect");
				}
			}
		}

	fEntity.setFrame( fEntity.getFrame()+1 );
	if (fEntity.getFrame() == 5)
		fState = DISPOSING;
	}
/**
 * BlasterBolt constructor comment.
 * @exception q2java.GameException The exception description.
 */
public void launch(GameObject owner, Point3f start, Vector3f dir, int damage, int speed, float damageRadius) throws q2java.GameException 
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);
	
	fEntity.setOrigin(start);
	fEntity.setAngles(new Angle3f(dir));
	dir.scale(speed); // this seems wrong...I would think the direction should be normalized first, like the blaster is.
	fEntity.setVelocity(dir);
	fEntity.setClipmask(Engine.MASK_SHOT);
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setEffects(NativeEntity.EF_BFG | NativeEntity.EF_ANIM_ALLFAST);
	fEntity.setModelIndex(Engine.getModelIndex("sprites/s_bfg1.sp2"));
	fEntity.setSound(Engine.getSoundIndex("weapons/bfg__l1a.wav"));
	fOwner = owner;
	fEntity.setOwner(owner.fEntity);
	fExpires = (float)Game.getGameTime() + (8000 / speed); // go away after a while
	fDamage = (BaseQ2.gIsDeathmatch ? 5 : 10);
	fRadiusDamage = damage;
	fDamageRadius = damageRadius;
	fState        = FLYING;
	fEntity.linkEntity();
/*
	if (self->client)
		check_dodge (self, bolt->s.origin, dir, speed);
*/

	Game.addServerFrameListener(this, 0, 0);
	}
protected void prepareExplosion(TraceResults tr)
	{
	GameObject victim = null;
	Point3f    org    = new Point3f();


	//if (self->owner->client)
	//	PlayerNoise(self->owner, self->s.origin, PNOISE_IMPACT);

	if ( tr.fEntity.getReference() instanceof GameObject)	// Should also be monster...
		{
		victim = (GameObject)tr.fEntity.getReference();
		victim.damage(this, fOwner, fEntity.getVelocity(), fEntity.getOrigin(), tr.fPlaneNormal, 200, 0, 0, Engine.TE_NONE, "bfg_blast");
		}

	MiscUtil.radiusDamage(this, fOwner, 200, victim.fEntity, 100, "bfg_effect");

	fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("weapons/bfg__x1b.wav"), 1, NativeEntity.ATTN_NORM, 0);
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	org.scaleAdd( -Engine.SECONDS_PER_FRAME, fEntity.getVelocity(), fEntity.getOrigin() );
	fEntity.setOrigin( org );
	fEntity.setVelocity(0, 0, 0);
	fEntity.setModelIndex( Engine.getModelIndex("sprites/s_bfg3.sp2") );
	fEntity.setFrame(0);
	fEntity.setSound(0);
	fEntity.setEffects( fEntity.getEffects() & ~NativeEntity.EF_ANIM_ALLFAST );
	//self->enemy = other;

	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_BFG_BIGEXPLOSION);
	Engine.writePosition( fEntity.getOrigin() );
	Engine.multicast( fEntity.getOrigin(), Engine.MULTICAST_PVS);
	}
public void runFrame(int phase) 
	{
	NativeEntity ignore;

	if (Game.getGameTime() >= fExpires)
		fState = DISPOSING;

	if (fState == DISPOSING)
		{
		dispose();
		return;
		}

	if (fState == EXPLODING)
		explode();

	TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F); 

	if (tr.fFraction < 1)	// We have hit an object...let's explode..
		{
		// 'scuse me while I kiss the sky...
		if ((tr.fSurfaceName != null) && ((tr.fSurfaceFlags & Engine.SURF_SKY) != 0))
			{
			dispose();
			return;
			}
		
		prepareExplosion(tr);
		fState = EXPLODING;
		}


	NativeEntity[] ents = Engine.getRadiusEntities( fEntity.getOrigin(), 256, true, false );

	if (ents == null)
		return;

	// TODO: only players should be players+monsters...
	for (int i=0; i<ents.length; i++)
		{
		NativeEntity ent = ents[i];
		if (ent == fEntity)
			continue;

		if ( ent.getReference() == fOwner )
			continue;

		//if (!ent->takedamage)
		//	continue;

		//if (!(ent->svflags & SVF_MONSTER) && (!ent->client) && (strcmp(ent->classname, "misc_explobox") != 0))
		//	continue;
		
		//if ( !(ent.getReference() instanceof Player) )
		//	continue;

		dir.scaleAdd( 0.5F, ent.getSize(), ent.getAbsMins() );
		dir.sub( fEntity.getOrigin() );
		dir.normalize();

		ignore = fEntity;
		start.set( fEntity.getOrigin() );
		end.scaleAdd( 2048F, dir, start );

		int mask =	Engine.CONTENTS_SOLID |
					Engine.CONTENTS_MONSTER|
					Engine.CONTENTS_DEADMONSTER;

		// HACK: Engine.trace() seems never to return a tr.fEntity of null !!!!!! (BUG)
		// So we can't use "while(true)".......
		// FIX IT !!!!!!!!!!!!!
		int g = 1;
		while(g-- > 0)
		//while(true)
			{
			tr = Engine.trace (start, end, ignore, mask);

			// This never seems to be true...... see above..
			if (tr.fEntity == null)
				break;

			Object reference = tr.fEntity.getReference();
			if ( reference instanceof GameObject )
				{
				GameObject go = (GameObject)reference;

				// hurt it if we can
				//if ((tr.ent->takedamage) && !(tr.ent->flags & FL_IMMUNE_LASER) && (tr.ent != self->owner))
				if ( tr.fEntity != fEntity.getOwner() )
					go.damage (this, fOwner, dir, tr.fEndPos, VECTOR_ORIGIN, fDamage, 1, DAMAGE_ENERGY, Engine.TE_NONE, "bfg_laser");

				}

			// if we hit something that's not a monster or player we're done
			//if (!(tr.ent->svflags & SVF_MONSTER) && (!tr.ent->client))
			else
				{
				Engine.writeByte(Engine.SVC_TEMP_ENTITY);
				Engine.writeByte(Engine.TE_LASER_SPARKS);
				Engine.writeByte(4);
				Engine.writePosition( tr.fEndPos );
				Engine.writeDir( tr.fPlaneNormal );
				Engine.writeByte( fEntity.getSkinNum() );
				Engine.multicast( tr.fEndPos, Engine.MULTICAST_PVS);
				break;
				}
			ignore = tr.fEntity;
			start.set(tr.fEndPos);
			}
		
		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_BFG_LASER);
		Engine.writePosition( fEntity.getOrigin() );
		Engine.writePosition( tr.fEndPos );
		Engine.multicast( fEntity.getOrigin(), Engine.MULTICAST_PHS);
		
		}
	}
}