
package barryp.persist;

import q2jgame.*;

/**
 * Simple module for persistance.
 * 
 */
public class GameModule extends q2jgame.GameModule 
	{
	
/**
 * This method was created by a SmartGuide.
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	}
/**
 * This method was created by a SmartGuide.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Will save a player's settings in the sandbox when they disconnect\n");
	Game.dprint("   no commands available\n");
	}
}