package barryp.flashgrenade;

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
 * Help for FlashGrenade module.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Changes handgrenade to also blind players\n");
	Game.dprint("   no commands available\n");
	}
}