package q2jgame;

import q2java.*;

/**
 * Abstract class for Game Modules
 * 
 */
public abstract class GameModule 
	{
	private String fModuleName;
	
/**
 * Constructor for all GameModules.
 * @param moduleName java.lang.String
 */
public GameModule(String moduleName) 
	{
	fModuleName = moduleName;
	}
/**
 * Get the name of this module.
 * @return java.lang.String
 */
public String getModuleName() 
	{
	return fModuleName;
	}
/**
 * Get the name of the package this game module belongs to.
 * @return java.lang.String
 */
public String getPackageName() 
	{
	String clsName = getClass().getName();
	int i = clsName.lastIndexOf('.');
	return clsName.substring(0, i);
	}
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