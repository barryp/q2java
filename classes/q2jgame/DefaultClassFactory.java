
package q2jgame;

import java.util.*;

/**
 * This class provides a package addition and removal scheme for q2java. It is based
 * upon methods originally provided in q2jgame.Game which performed the main functionality.
 * <P>
 * The corresponding methods in q2jgame.Game are still available but are re-implemented 
 * in terms of the methods below.
 * <P>
 * A mod can provide a subclass of GameClassFactory which can replace any or all of the 
 * functionality of this class, but in a transparent way so that existing mods need not
 * be altered.
 * <P>
 * So far this is untested, so there may be runtime problems, treat it gently...!
 *
 * @version 	0.1 
 * @author 	Leigh Dodds
 */
class DefaultClassFactory extends GameClassFactory 
	{
	protected Vector gModList;
	protected Hashtable gClassHash;
	
	/**
	 * Constructor
	 */
	public DefaultClassFactory() {
		gModList = new Vector();
		gClassHash = new Hashtable();
	}
/**
 * Adds a package to the running game.
 */
public void addModule(String packageName, String alias) 
	{
	GameModule gp = getModule(alias);
	if (gp != null) 
		{
		Game.dprint("There is already a [" + alias + "] loaded\n");
		return;
		}
		
	try 
		{
		LoadedModule lp = new LoadedModule();
		lp.fPackageName = packageName;
		lp.fAlias = alias;
		lp.fModule = (GameModule) Class.forName(packageName + ".GameModule").newInstance();

		gModList.insertElementAt(lp, 0);

		// clear the cache so the new package will be 
		// looked at when looking up classes
		gClassHash.clear(); 

		//Withnails 04/22/98
		Game.notifyModuleAdded(lp.fModule);
		} 
	catch (Exception e) 
		{
		e.printStackTrace();
		}
	}
	/**
	 * Lookup a loaded package, based on its name.
	 * @return q2jgame.LoadedPackage, null if not found.
	 * @param alias java.lang.String
	 */
	public GameModule getModule(String alias) 
		{
		for (int i = 0; i < gModList.size(); i++)
			{
			LoadedModule lp = (LoadedModule) gModList.elementAt(i);
			if (lp.fAlias.equals(alias))
				return lp.fModule;
			}
		
		return null;
		}
	/**
	 * Get an Enumeration of all loaded packages. The enumeration will be
	 * of LoadedPackage objects
	 */
	public Enumeration getModules() {
		return gModList.elements();
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
	// check for a full classname
	if (classSuffix.charAt(0) != '.')
		{
		return Class.forName(classSuffix);
		}
		
	Class result = (Class) gClassHash.get(classSuffix);
	if (result != null)
		return result;
	Enumeration enum = gModList.elements();

	while (enum.hasMoreElements())
		{
		LoadedModule lp = (LoadedModule) enum.nextElement();
		try
			{
			result = Class.forName(lp.fPackageName + classSuffix);
			gClassHash.put(classSuffix, result);
			return result;
			}
		catch (ClassNotFoundException e)
			{
			}
		}

	throw new ClassNotFoundException("No match for [" + classSuffix + "]");
	}
	/**
	 * Returns the number of loaded packages
	 */
	public int numModules() {
		return gModList.size();
	}
	/**
	 * Removes a package from a running game.
	 */
	public void removeModule(String alias)  
		{
		int i;
		for (i = 0; i < gModList.size(); i++)
			{
			LoadedModule lp = (LoadedModule) gModList.elementAt(i);
			if (lp.fAlias.equalsIgnoreCase(alias))
				{
				gModList.removeElementAt(i);
				// clear the cache so the old package won't be 
				// looked at when looking up classes
				gClassHash.clear();
				try
					{
					lp.fModule.unload();
					}
				catch (Exception e)
					{
					e.printStackTrace();
					}	
				Game.notifyModuleRemoved(lp.fModule);
				return;
				}
			}
		Game.dprint("[" + alias + "] is not loaded\n");
		}
	/**
	 * Removes a package from a running game.
	 */
	public void removeModule(GameModule gm)  
		{
		int i;
		for (i = 0; i < gModList.size(); i++)
			{
			LoadedModule lp = (LoadedModule) gModList.elementAt(i);
			if (lp.fModule == gm)
				{
				gModList.removeElementAt(i);
				// clear the cache so the old package won't be 
				// looked at when looking up classes
				gClassHash.clear();
				try
					{
					lp.fModule.unload();
					}
				catch (Exception e)
					{
					e.printStackTrace();
					}	
				Game.notifyModuleRemoved(lp.fModule);
				return;
				}
			}
		Game.dprint("GameModule: " + gm + "wasn't loaded\n");
		}
}