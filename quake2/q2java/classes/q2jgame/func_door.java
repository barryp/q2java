
package q2jgame;

import q2java.*;

public class func_door extends GameEntity
	{
	
public func_door(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	String s = getSpawnArg("model");
	if (s != null)
		setModel(s);
	}
}