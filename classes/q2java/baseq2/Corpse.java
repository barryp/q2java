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
 */
public void damage(DamageEvent de) 
	{
	spawnDamage(Engine.TE_BLOOD, de.getDamagePoint(), de.getDamageNormal(), de.getAmount());	
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