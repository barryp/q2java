package barryp.rocketmania;

import q2java.core.*;

/**
 * Demo of altered weapons.
 * 
 * @author Barry Pederson
 */
public class GameModule extends q2java.core.Gamelet 
	{
	
/**
 * This method was created by a SmartGuide.
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	
	//leighd 04/10/99 - add the package path
	Game.addPackagePath("barryp.rocketmania");
	
	}
/**
 * Get which Gamelet classes this Gamelet requires.
 * @return java.lang.Class[]
 */
public String[] getGameletDependencies() 
	{
	String[] result = { "q2java.baseq2.BaseQ2" };
	return result;
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
/**
 * leighd 04/10/99
 * Added unload method to remove package path
 */
public void unload()
	{
	//leighd 04/11/99 - add the package path
	Game.removePackagePath("barryp.rocketmania");    
	}
}