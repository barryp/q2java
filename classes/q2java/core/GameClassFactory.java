package q2java.core;

import java.util.*;

/**
 * @version 	0.3
 * @author 	Leigh Dodds
 */
public interface GameClassFactory 
	{
	
/**
 * Adds a package to the running game.
 */
public Gamelet addGamelet(String className, String alias, 
	Gamelet higherGamelet) throws ClassNotFoundException;
/**
 * Lookup a loaded package, based on its name.
 * @return q2java.core.Gamelet, null if not found.
 * @param alias java.lang.String
 */
public Gamelet getGamelet(Class gameletClass);
/**
 * Lookup a loaded package, based on its name.
 * @return q2java.core.Gamelet, null if not found.
 * @param alias java.lang.String
 */
public Gamelet getGamelet(String alias);
/**
 * Returns the number of loaded packages
 */
public int getGameletCount();
/**
 * Get an Enumeration of all loaded packages. The enumeration will be
 * of Gamelet objects in order of priority - highest to lowest.
 */
public Enumeration getGamelets();
/**
 * Looks up a class in loaded packages, or attempts to load the 
 * given class if not currently loaded.
 * @param classSuffix Either a suffix, like ".spawn.weapon_shotgun", 
 * 	or a whole classname like "baseq2.spawn.weapon_shotgun"
 * @return The class matching the suffix/name
 * @exception java.lang.ClassNotFoundException if there was no match.
 */
public Class lookupClass(String classSuffix) throws ClassNotFoundException;
/**
 * Removes a gamelet from a running game.
 */
public void removeGamelet(Gamelet g);
}