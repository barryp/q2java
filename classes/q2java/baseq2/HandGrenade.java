package q2java.baseq2;


import javax.vecmath.*;
import q2java.*;
import q2java.core.*;

/**
 * Grenades that have been thrown by a player,
 * and are flying through the air.
 */
  
public class HandGrenade extends GenericGrenade
	{
	
/**
 * No-arg constructor.
 */
public HandGrenade() 
	{
	}
/**
 * Setup the grenade and start it running.
 */
public void toss(GameObject owner, Point3f start, Vector3f aimdir, int damage, int speed, float timer, float radiusDamage, boolean held) throws q2java.GameException 
	{
	super.toss(owner, start, aimdir, damage, speed, timer, radiusDamage);

	fEntity.setModelIndex(Engine.getModelIndex("models/objects/grenade2/tris.md2"));
	fEntity.setSound(Engine.getSoundIndex("weapons/hgrenc1b.wav"));

	if (held)
		fSpawnFlags = 3;
	else
		fSpawnFlags = 1;	
	}
}