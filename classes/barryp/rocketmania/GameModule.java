package barryp.rocketmania;

import q2jgame.*;

/**
 * Demo of altered weapons.
 * 
 * @author Barry Pederson
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
	Game.dprint("Changes hand blaster and machine gun to fire rockets,\n");
	Game.dprint("and makes the RocketLauncher more powerful\n");
	Game.dprint("   no commands available\n");
	}
}