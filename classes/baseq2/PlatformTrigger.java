
package baseq2;

import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

public class PlatformTrigger extends AreaTrigger
	{
	
public PlatformTrigger(baseq2.spawn.func_plat target, Tuple3f mins, Tuple3f maxs) throws GameException
	{
	super(target, mins, maxs);
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	((baseq2.spawn.func_plat)fOwner).raise();
	}
}