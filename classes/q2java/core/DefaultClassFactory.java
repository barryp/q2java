package q2java.core;

import java.util.*;
import q2java.*;

/**
 * This simple ClassFactory implementation just makes use of the 
 * virtual machines own class loader to retrieve classes from the 
 * local file system. An more advanced implementation may add network
 * support.
 *
 * @version 	0.4
 * @author 	Leigh Dodds
 */
public class DefaultClassFactory implements GameClassFactory 
	{
	protected String[] fPackagePaths = null;
	protected Hashtable fClassCache = null;
	
/**
 * Constructor
 */
public DefaultClassFactory() 
	{
	fClassCache = new Hashtable();
	//Engine.debugLog("class factory init");
	}
/**
 * Looks up a class in loaded packages, or attempts to load the 
 * given class if not currently loaded.
 * @param classSuffix Either a suffix, like ".spawn.weapon_shotgun", 
 * 	or a whole classname like "baseq2.spawn.weapon_shotgun"
 * @return The class matching the suffix/name
 * @exception java.lang.ClassNotFoundException if there was no match.
 */
public Class lookupClass(String classSuffix) throws ClassNotFoundException 
	{
	//Engine.debugLog("ClassFactory: lookupClass :" + classSuffix);	
	// check for a full classname
	if (classSuffix.charAt(0) != '.')
		{
		return Class.forName(classSuffix);
		}


	//only a suffix then huh?
	//we may have loaded this class recently...
	Class result = (Class) fClassCache.get(classSuffix);
	if (result != null)
		return result;

	//Engine.debugLog("Looking for non-cached Class : " + classSuffix);

	if (fPackagePaths == null)
		{
		//probably ought to do something sensible here, like default?
		throw new ClassNotFoundException("No paths for suffix [" + classSuffix + "]");        
		}
		
	//Check out packagePaths to see if we can find it. 
	//most recently registered paths get checked first.
	for (int i = fPackagePaths.length-1; i >= 0; i--)
		{
		try
			{
			Class c = Class.forName(fPackagePaths[i]+classSuffix);
			//store it for later use
			fClassCache.put(classSuffix, c);
			return c;
			}
		catch (ClassNotFoundException e)
			{
			//do nothing yet
			}
		}

	throw new ClassNotFoundException("No match for [" + classSuffix + "]");
	}
/**
 * Called by the Game if this is the current GameClassFactory to let
 * it know the packagePath has changed.
 * @param path java.lang.String[]
 */
public void setPackagePath(String[] path)
	{
	// make a copy, so we know it won't be messed with externally
	fPackagePaths = new String[path.length];
	System.arraycopy(path, 0, fPackagePaths, 0, path.length);

	// clear the hashtable, so the new path will be searched instead
	// of relying on old info
	fClassCache.clear();
	}
}