
package baseq2;

import javax.vecmath.*;
import q2java.*;

/**
 * Corpse lying on the ground.
 * 
 */
public class Corpse extends GameObject 
	{
	
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
}