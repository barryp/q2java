package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class func_areaportal implements GameTarget
	{
	private int fArea;
	
public func_areaportal(Element spawnArgs) throws GameException
	{
	fArea = GameUtil.getSpawnArg(spawnArgs, "style", 0);

	String s = GameUtil.getSpawnArg(spawnArgs, "targetname", "id", null);
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