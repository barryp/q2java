package q2java.baseq2;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Corpse lying on the ground.
 * 
 */
public class Corpse extends GameObject implements ServerFrameListener
	{
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
public void copy(NativeEntity ent) 
	{
	fEntity.unlinkEntity();
	fEntity.copySettings(ent);
	fEntity.linkEntity();
	
	// get called back to remove the corpse after a certain time.
	Game.addServerFrameListener(this, CORPSE_DELAY, -1);
	}
/**
 * Cause the corpse to spray blood.
 *
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent) 
	{
	spawnDamage(Engine.TE_BLOOD, point, normal, damage);	
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