
package baseq2;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

import baseq2.spawn.func_plat;

public class PlatformTrigger extends GameObject
	{
	protected func_plat fOwner;	
	
public PlatformTrigger(baseq2.spawn.func_plat target, Tuple3f mins, Tuple3f maxs) throws GameException
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
	fOwner.raise();
	}
}