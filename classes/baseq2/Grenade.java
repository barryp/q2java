
package baseq2;


import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Grenades that have been fired by a Grenadelauncher,
 * and are flying through the air.
 */
  
public class Grenade extends GenericGrenade
	{
	
public Grenade(GameObject owner, Point3f start, Vector3f aimdir, int damage, int speed, float timer, float radiusDamage) throws q2java.GameException 
	{
	super(owner, start, aimdir, damage, speed, timer, radiusDamage);
	fEntity.setModelIndex(Engine.getModelIndex("models/objects/grenade/tris.md2"));
	}
}	