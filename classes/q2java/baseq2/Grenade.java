package q2java.baseq2;


import javax.vecmath.*;
import q2java.*;
import q2java.core.*;

/**
 * Grenades that have been fired by a Grenadelauncher,
 * and are flying through the air.
 */
  
public class Grenade extends GenericGrenade
	{
	
public void toss(GameObject owner, Point3f start, Vector3f aimdir, int damage, int speed, float timer, float radiusDamage) throws q2java.GameException 
	{
	super.toss(owner, start, aimdir, damage, speed, timer, radiusDamage);
	fEntity.setModelIndex(Engine.getModelIndex("models/objects/grenade/tris.md2"));
	}
public void toss(GameObject owner, NativeEntity ownerEntity, Point3f start, Vector3f velocity, int damage, float timer, float radiusDamage) throws q2java.GameException 
	{
	super.toss(owner, ownerEntity, start, velocity, damage, timer, radiusDamage);
	fEntity.setModelIndex(Engine.getModelIndex("models/objects/grenade/tris.md2"));
	}
}