package baseq2;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

public abstract class GenericBlaster extends GenericWeapon
	{
	protected Vector3f fBlasterOffset;
	protected int fEffect;  // the hyperblaster changes this as it fires
	protected String fObitKey;
	protected int fDamage;
	protected int fMuzzleFlash;
	
/**
 * This method was created by a SmartGuide.
 */
public GenericBlaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public GenericBlaster(int blasterEffect, int blasterDamage, int blasterMuzzleFlash, String obitKey)
	{
	super();

	fEffect = blasterEffect;
	fObitKey = obitKey;
	fDamage = blasterDamage;
	fMuzzleFlash = blasterMuzzleFlash;
	fBlasterOffset = new Vector3f();
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Vector3f offset = new Vector3f(24, 8, fPlayer.fViewHeight - 8);
	int damage = fDamage;
	
	damage *= fPlayer.getDamageMultiplier();
	
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, right, null);
	offset.add(fBlasterOffset);
	Point3f start = fPlayer.projectSource(offset, forward, right);
	
	fPlayer.fKickOrigin.set(forward);
	fPlayer.fKickOrigin.scale(-2);	
	fPlayer.fKickAngles.x = -1;

	try
		{
		Class boltClass = Game.lookupClass(".BlasterBolt");
		// assume we're launching a baseq2.BlasterBolt or subclass
		BlasterBolt b = (BlasterBolt) boltClass.newInstance();		
		b.launch(fPlayer, start, forward, damage, 1000, fEffect, fObitKey);
		}
	catch (Exception e)
		{
		Game.dprint("Can't create BlasterBolt " + e);
		}		

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(fMuzzleFlash);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);	
	}
}