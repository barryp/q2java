package q2java.baseq2;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;

import q2java.baseq2.spawn.func_plat;

/**
 * Trigger field that causes platforms to move when you 
 * step on them
 *
 * @author Barry Pederson
 */
 
public class PlatformTrigger extends GameObject implements FixedObject
	{
	protected func_plat fOwner;	
	
public PlatformTrigger(q2java.baseq2.spawn.func_plat target, Tuple3f mins, Tuple3f maxs) throws GameException
	{
	fEntity = new NativeEntity();
	fEntity.setReference(this);
	
	fEntity.setMins(mins);
	fEntity.setMaxs(maxs);
	
	fOwner = target;
	fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fEntity.linkEntity();	
	}
/**
 * Trigger the platform to raise.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	if (fOwner.isLowered())
		fOwner.raise();
	}
}