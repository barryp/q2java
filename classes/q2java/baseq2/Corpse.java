package q2java.baseq2;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.baseq2.event.*;

/**
 * Corpse lying on the ground.
 * 
 */
public class Corpse extends GameObject implements ServerFrameListener
	{
	private float fHealth; // a healthy corpse is one piece..an unhealthy corpse becomes gibs
	private DropHelper fDropHelper = new DropHelper(); // might as well keep this around continously
	
	protected final static int CORPSE_DELAY = 45; // remove corpses after this many seconds
	
/**
 * Create a corpse entity.
 */
public Corpse() 
	{
	try
		{
		fEntity = new NativeEntity();
		fEntity.setReference(this);
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}
	}
/**
 * Take on the properties of another entity.
 * @param ent q2java.NativeEntity
 */
public void copy(NativeEntity ent, float health) 
	{
	fEntity.unlinkEntity();
	fEntity.copySettings(ent);
	fEntity.linkEntity();

	fHealth = health;
	
	// get called back to remove the corpse after a certain time.
	Game.addServerFrameListener(this, CORPSE_DELAY, -1);

	// cause the corpse to fall to the ground
	fDropHelper.drop(null, fEntity, null, 0);
	}
/**
 * Cause the corpse to spray blood.
 *
 */
public void damage(DamageEvent de) 
	{
	fHealth -= de.fAmount;
	if (fHealth >= Player.GIB_HEALTH_THRESHOLD)
		spawnDamage(Engine.TE_BLOOD, de.getDamagePoint(), de.getDamageNormal(), de.getAmount());
	else
		{
		// make a nasty sound
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("misc/udeath.wav"), 1, NativeEntity.ATTN_NORM, 0);

		// toss chunks
		for (int n = 0; n < 4; n++)
			(new Gib()).toss(fEntity, "models/objects/gibs/sm_meat/tris.md2", de, Gib.GIB_ORGANIC);

		// convert the corpse to just a head
		if ((GameUtil.randomInt() & 1) == 1)
			{
			fEntity.setModel("models/objects/gibs/head2/tris.md2");
			fEntity.setSkinNum(1);		// second skin is player
			}
		else
			{
			fEntity.setModel("models/objects/gibs/skull/tris.md2");
			fEntity.setSkinNum(0);
			}

		// position the head
		Point3f origin = fEntity.getOrigin();
		origin.z += 32;
		fEntity.setOrigin(origin);

		fEntity.setFrame(0);
	
		fEntity.setMins(-16, -16, 0);
		fEntity.setMaxs( 16, 16, 16);

		fEntity.setSolid(NativeEntity.SOLID_NOT);
		fEntity.setEffects(NativeEntity.EF_GIB);
		fEntity.setSound(0);

		// figure out an initial velocity
		Vector3f v = Gib.calcVelocity(de);
		v.add(fEntity.getVelocity());
		fEntity.setVelocity(v);
		
		fEntity.linkEntity();

		// make sure it falls to the ground
		fDropHelper.drop(null, fEntity, null, 0);
		}
	}
/**
 * Hide the corpse.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);
	fEntity.unlinkEntity();	
	}
}