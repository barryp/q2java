
package q2jgame;

import q2java.*;

/**
 * Abstract class for Game Modules
 * 
 */
public abstract class GameModule 
	{
	
/**
 * Default help svcmd for a GameModule.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint("Mod author was too lazy to write decent help\n");
	}
/**
 * Default do-nothing implementation for unloading a package.
 */
public void unload() 
	{
	}
}