package barryp.flashgrenade;

import q2java.core.*;

/**
 * Demo of altered weapons.
 * 
 * @author Barry Pederson
 */
public class FlashGrenades extends q2java.core.Gamelet 
	{
	
/**
 * This method was created by a SmartGuide.
 */
public FlashGrenades(String moduleName) 
	{
	super(moduleName);
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
 * Help for FlashGrenades module.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Changes handgrenade to also blind players\n");
	Game.dprint("   no commands available\n");
	}
}