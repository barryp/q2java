
package baseq2;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

public class AreaTrigger extends GameObject
	{
	protected GameTarget fOwner;
	
public AreaTrigger(GameTarget target, Tuple3f mins, Tuple3f maxs) throws GameException
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
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	fOwner.use(touchedBy);
	}
}