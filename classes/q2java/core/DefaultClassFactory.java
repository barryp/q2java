package q2java.core;

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
 * Note that after removing itself as GameClassFactory should ensure that the
 * system is in a stable state by calling Game.setClassFactory(new DefaultClassFactory())
 * @version 	0.2
 * @author 	Leigh Dodds
 */
public class DefaultClassFactory implements GameClassFactory 
	{
	protected Vector gModList;
	protected Hashtable gClassHash;
	
/**
 * Constructor
 */
public DefaultClassFactory() 
	{
	gModList = new Vector();
	gClassHash = new Hashtable();
	}
/**
 * Adds a package to the running game.
 */
public Gamelet addGamelet(String className, String alias,
	Gamelet higherGamelet) throws ClassNotFoundException
	{
	Gamelet g = getGamelet(alias);
	if (g != null) 
		{
		Game.dprint("There is already a [" + alias + "] loaded\n");
		return null;
		}
		
	try 
		{
		Class cls = Class.forName(className);
		Object[] args = new Object[1];
		args[0] = alias;
		Class[] argTypes = new Class[1];
		argTypes[0] = alias.getClass();
		java.lang.reflect.Constructor ctor = cls.getConstructor(argTypes);		
		g = (Gamelet) ctor.newInstance(args);

		int position = 0;
		if (higherGamelet != null)
			position = gModList.indexOf(higherGamelet) + 1;
		gModList.insertElementAt(g, position);

		// clear the cache so the new package will be 
		// looked at when looking up classes
		gClassHash.clear();

		return g;
		} 
	catch (java.lang.reflect.InvocationTargetException ite)
		{
		ite.getTargetException().printStackTrace();
		}
	catch (Exception e) 
		{
		e.printStackTrace();
		}

	return null;
	}
/**
 * Look for a gamelet based on the class.
 * @return q2java.core.Gamelet, null if not found.
 * @param gameletClass class we're looking for.
 */
public Gamelet getGamelet(Class gameletClass) 
	{
	for (int i = 0; i < gModList.size(); i++)
		{
		Object obj = gModList.elementAt(i);
		if (obj.getClass().equals(gameletClass))
			return (Gamelet) obj;
		}
		
	return null;
	}
/**
 * Lookup a loaded package, based on its name.
 * @return q2jgame.GameModule, null if not found.
 * @param alias java.lang.String
 */
public Gamelet getGamelet(String alias) 
	{
	for (int i = 0; i < gModList.size(); i++)
		{
		Gamelet g = (Gamelet) gModList.elementAt(i);
		if (g.getGameletName().equalsIgnoreCase(alias))
			return g;
		}
		
	return null;
	}
/**
 * Returns the number of loaded packages
 */
public int getGameletCount() 
	{
	return gModList.size();
	}
/**
 * Get an Enumeration of all loaded packages. The enumeration will be
 * of LoadedPackage objects
 */
public Enumeration getGamelets() 
	{
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
		Gamelet g = (Gamelet) enum.nextElement();

		// only check initialized Gamelets
		if (g.isInitialized())
			{
			try
				{
				result = Class.forName(g.getPackageName() + classSuffix);
				gClassHash.put(classSuffix, result);
				return result;
				}
			catch (ClassNotFoundException e)
				{
				}
			}
		}

	throw new ClassNotFoundException("No match for [" + classSuffix + "]");
	}
/**
 * Removes a module from a running game.
 */
public void removeGamelet(Gamelet g)  
	{
	int i;
	for (i = 0; i < gModList.size(); i++)
		{
		Gamelet g2 = (Gamelet) gModList.elementAt(i);
		if (g2 == g)
			{
			gModList.removeElementAt(i);
			// clear the cache so the old package won't be 
			// looked at when looking up classes
			gClassHash.clear();
			try
				{
				g.unload();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}	
			return;
			}
		}
	Game.dprint("Gamelet: " + g + "wasn't loaded\n");
	}
}