package q2java.core;

import java.util.*;

/**
 * The GameClassFactory is basically just a class loader and should
 * be used by gamelets (via the Game.lookupClass()) method to load
 * any specific classes.
 *
 * @version 	0.4
 * @author 	Leigh Dodds
 */
public interface GameClassFactory 
	{
	
/**
 * Looks up a class in loaded packages, or attempts to load the 
 * given class if not currently loaded. Packages are searched in reverse
 * order of the sequence they were added - the the last package added
 * is the first one looked at.
 *
 * @param classSuffix Either a suffix, like ".spawn.weapon_shotgun", 
 * 	or a whole classname like "baseq2.spawn.weapon_shotgun"
 * @return The class matching the suffix/name
 * @exception java.lang.ClassNotFoundException if there was no match.
 */
public Class lookupClass(String classSuffix) throws ClassNotFoundException;
/**
 * Called by the Game if this is the current GameClassFactory to let
 * it know the packagePath has changed.
 * @param path java.lang.String[]
 */
public void setPackagePath(String[] path);
}