
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class func_areaportal extends GameEntity
	{
	private int fArea;
	
public func_areaportal(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	String s = getSpawnArg("style");
	if (s != null)
		fArea = Integer.parseInt(s);
	}
/**
 * This method was created by a SmartGuide.
 * @param state boolean
 */
public void setPortal(boolean state) 
	{
	Engine.setAreaPortalState(fArea, state);
	}
}