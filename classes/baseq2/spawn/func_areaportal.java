package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class func_areaportal implements GameTarget
	{
	private int fArea;
	
public func_areaportal(String[] spawnArgs) throws GameException
	{
	fArea = Game.getSpawnArg(spawnArgs, "style", 0);

	String s = Game.getSpawnArg(spawnArgs, "targetname", null);
	if (s != null)
		Game.addLevelRegistry("target-" + s, this);			
	}
/**
 * This method was created by a SmartGuide.
 * @param state boolean
 */
public void setPortal(boolean state) 
	{
	Engine.setAreaPortalState(fArea, state);
	}
/**
 * This method was created by a SmartGuide.
 * @param p baseq2.Player
 */
public void use(Player p) 
	{
	Engine.setAreaPortalState(fArea, true);
	}
}