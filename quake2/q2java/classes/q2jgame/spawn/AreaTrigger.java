
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class AreaTrigger extends GameEntity
	{
	
public AreaTrigger(GenericPusher target, Vec3 mins, Vec3 maxs) throws GameException
	{
	setMins(mins);
	setMaxs(maxs);
	
	setOwner(target);
	setSolid(SOLID_TRIGGER);
	linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	((GenericPusher)getOwner()).use(touchedBy);
	}
}