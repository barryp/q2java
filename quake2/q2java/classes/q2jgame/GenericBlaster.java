
package q2jgame;

import q2java.*;

public class GenericBlaster extends GenericWeapon
	{
	protected int fEffect;
	protected int fDamage;
	protected Vec3 fBlasterOffset;
	protected int fMuzzleFlash;
	
public GenericBlaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() 
	{
Game.debugLog("In GenericBlaster.fire()");	
	Player p = (Player) getOwner();
	Vec3 forward = new Vec3();
	Vec3 right = new Vec3();
	Vec3 offset = new Vec3(24, 8, p.fViewHeight - 8);
/*
	if (is_quad)
		damage *= 4;
*/		
	p.getAngles().angleVectors(forward, right, null);
	offset.add(fBlasterOffset);
	Vec3 start = p.projectSource(offset, forward, right);
/*
	VectorScale (forward, -2, ent->client->kick_origin);
	ent->client->kick_angles[0] = -1;
*/
	try
		{
		new BlasterBolt(p, start, forward, fDamage, 1000, fEffect);
		}
	catch (GameException e)
		{
		Engine.dprint("Can't create BlasterBolt " + e);
		}		

	// send muzzle flash
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(getEntityIndex());
	Engine.writeByte(fMuzzleFlash);
	Engine.multicast(p.getOrigin(), Engine.MULTICAST_PVS);

//	PlayerNoise(ent, start, PNOISE_WEAPON);	
	}
}