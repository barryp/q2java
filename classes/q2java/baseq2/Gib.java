package q2java.baseq2;

import javax.vecmath.*;
import q2java.*;
import q2java.baseq2.event.DamageEvent;
import q2java.core.*;
import q2java.core.event.*;

/**
 * The nasty stuff that litters the hallways after a particulary good game.
 *
 * @author Barry Pederson
 */
public class Gib extends GameObject implements ServerFrameListener
	{
	protected int fGibType;
	protected Vector3f fAVelocity;	
	
	// two basic types of gib
	public final static int GIB_ORGANIC = 0;
	public final static int GIB_METALLIC = 1;
	
/**
 * Given some amount of damage, come up with a base velocity for the gib.
 *
 * @return javax.vecmath.Vector3f
 * @param damage int
 */
public static Vector3f calcVelocity(DamageEvent de) 
	{
	Vector3f result = new Vector3f(100.0F * GameUtil.cRandom(), 
								   100.0F * GameUtil.cRandom(),
								   200.0F + 100.0F * GameUtil.cRandom());
	if (de.fAmount < 50)
		result.scale(0.7F);
	else
		result.scale(1.2F);
		
	return result;
	}
/**
 * Massage a gib velocity into something reasonable.
 *
 * @param v javax.vecmath.Vector3f
 */
protected void clipGibVelocity(Vector3f v) 
	{
	if (v.x < -300)
		v.x = -300;
	else if (v.x > 300)
		v.x = 300;
	
	if (v.y < -300)
		v.y = -300;
	else if (v.y > 300)
		v.y = 300;
		
	if (v.z < 200)
		v.z = 200; // always some upwards
	else if (v.z > 500)
		v.z = 500;
	}
/**
 * Just go away.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	dispose();
	}
/**
 * Cause the gib to toss itself out into the world.
 *
 * @param source NativeEntity
 * @param modelName java.lang.String
 * @param damage int
 * @param gibType int
 */
public void toss(NativeEntity source, String modelName, DamageEvent de, int gibType) 
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);

	fGibType = gibType;
	
	Vector3f size = source.getSize();
	size.scale(0.5F);
	Point3f origin = new Point3f(size);
	origin.add(source.getAbsMins());

	origin.x += GameUtil.cRandom() * size.x;
	origin.y += GameUtil.cRandom() * size.y;
	origin.z += GameUtil.cRandom() * size.z;
	
	fEntity.setOrigin(origin);	
	fEntity.setModel(modelName);
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setEffects(NativeEntity.EF_GIB);

	Vector3f gibVelocity = calcVelocity(de);
	if (fGibType == GIB_ORGANIC)
		gibVelocity.scale(0.5F);
	gibVelocity.add(source.getVelocity());
	clipGibVelocity(gibVelocity);
	fEntity.setVelocity(gibVelocity);
		
	fAVelocity = new Vector3f(GameUtil.randomFloat()*600, GameUtil.randomFloat()*600, GameUtil.randomFloat()*600);

	(new DropHelper()).drop(null, fEntity, null, 0);
	Game.addServerFrameListener(this, 10 + 10 * GameUtil.randomFloat(), -1);
	}
}