
package q2jgame;

import java.util.*;

/**
 * @version 	0.2
 * @author 	Leigh Dodds
 */
public abstract class GameClassFactory {


	/**
	 * Adds a package to the running game.
	 */
	public abstract void addModule(String packageName, String alias);
	/**
	 * Lookup a loaded package, based on its name.
	 * @return q2jgame.LoadedPackage, null if not found.
	 * @param alias java.lang.String
	 */
	public abstract GameModule getModule(String alias);
	/**
	 * Get an Enumeration of all loaded packages. The enumeration will be
	 * of LoadedPackage objects
	 */
	public abstract Enumeration getModules();
/**
 * Looks up a class in loaded packages, or attempts to load the 
 * given class if not currently loaded.
 * @param classSuffix Either a suffix, like ".spawn.weapon_shotgun", 
 * 	or a whole classname like "baseq2.spawn.weapon_shotgun"
 * @return The class matching the suffix/name
 * @exception java.lang.ClassNotFoundException if there was no match.
 */
public abstract Class lookupClass(String classSuffix) throws ClassNotFoundException;
	/**
	 * Returns the number of loaded packages
	 */
	public abstract int numModules();
	/**
	 * Removes a package from a running game.
	 */
	public abstract void removeModule(String alias);
	/**
	 * Removes a package from a running game.
	 */
	public abstract void removeModule(GameModule gm);
}