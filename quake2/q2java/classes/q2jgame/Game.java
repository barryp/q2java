
package q2jgame;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;

/**
 * This class provides the framework for a Quake II Java game. 
 * All the fields are static, so that objects can refer
 * to them without having to keep a reference to the solitary
 * Game object that's instantiated.
 *
 * @author Barry Pederson 
 */

public class Game implements GameListener
	{
	// Manage FrameListeners
	public final static int FRAME_BEGINNING	= 0x01;
	public final static int FRAME_MIDDLE		= 0x02;
	public final static int FRAME_END		= 0x04;
	
	private static FrameList gFrameBeginning;
	private static FrameList gFrameMiddle;
	private static FrameList gFrameEnd;
		
	// Manage GameListeners
	private static Vector gGameListeners;

	// Manage LevelListeners
	private static Vector gLevelListeners;
		
	// Manage PrintListeners
	private static Vector gPrintListeners;	
	
	// Manage mod packages
	private static Vector gModList;
	private static Hashtable gModHash;
	private static Hashtable gClassHash;
	
	// Allow objects to find each other
	private static Hashtable gLevelRegistry;
	
	// game clocks
	private static int gFrameCount;
	private static float gGameTime;
	private static long gCPUTime;	
	
/**
 * Register an object that implements FrameListener to 
 * receive normal (FRAME_MIDDLE phase) frame events.
 *
 * @param f object that wants its runFrame() method called.
 * @param delay Number of seconds to wait before calling the listener, use 0 to start calling right away.
 * @param interval Number of seconds between calls, use 0 to call on every frame, a negative interval will
 *   be a one-shot notification with the listener removed automatically afterwards.
 */
public static void addFrameListener(FrameListener f, float delay, float interval) 
	{
	gFrameMiddle.addFrameListener(f, delay, interval);
	}
/**
 * Register an object that implements FrameListener to 
 * receive frame events at specific phases of processing.
 * Used for special cases like Player objects that want to be
 * called both at the beginning and end of a server frame.
 *
 * @param f object that wants its runFrame() method called.
 * @param phase A combination (or'ed or added together) of the FrameManager.FRAME_* constants.
 * @param delay Number of seconds to wait before calling the listener, use 0 to start calling right away.
 * @param interval Number of seconds between calls, use 0 to call on every frame, a negative interval will
 *   be a one-shot notification with the listener removed automatically afterwards.
 */
public static void addFrameListener(FrameListener f, int phase, float delay, float interval) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.addFrameListener(f, delay, interval);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.addFrameListener(f, delay, interval);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.addFrameListener(f, delay, interval);
	}
/**
 * Add a game listener.
 * @param pl q2jgame.GameListener
 */
public static void addGameStatusListener(GameStatusListener gl) 
	{
	if (!gGameListeners.contains(gl))
		gGameListeners.addElement(gl);
	}
/**
 * Add a level listener.
 * @param pl q2jgame.GameListener
 */
public static void addLevelListener(LevelListener x) 
	{
	if (!gLevelListeners.contains(x))
		gLevelListeners.addElement(x);
	}
/**
 * Add an arbitrary object to the Game's registry.
 * @param key Key to reference the Vector that the object is added to.
 * @param value Any object
 * @return the Vector that the object was added to.
 */
public static Vector addLevelRegistry(Object key, Object value) 
	{
	Vector list = (Vector) gLevelRegistry.get(key);
	if (list == null)
		{
		list = new Vector();
		gLevelRegistry.put(key, list);
		}
		
	list.addElement(value);
	return list;
	}
/**
 * Add a new module to the game.  If the module has a class named:
 * <packagename>.GameModule, then that classes static void load() method is called.  
 * If a module has already been added, nothing happens.
 * @param packageName java.lang.String
 */
public static void addPackage(String packageName) 
	{
	if (gModList.contains(packageName))
		return;
		
	gModList.insertElementAt(packageName, 0);

	// clear the cache so the new package will be 
	// looked at when looking up classes
	gClassHash.clear(); 
	
	try
		{
		Class modClass = Class.forName(packageName + ".GameModule");
		gModHash.put(packageName, modClass);
		
		Method loadMethod = modClass.getMethod("load", null);
		loadMethod.invoke(null, null);
		}
	catch (ClassNotFoundException cnfe)
		{		
		}
	catch (NoSuchMethodException nsme)
		{
		}
	catch (InvocationTargetException ite)
		{
		ite.getTargetException().printStackTrace();
		}		
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Add a print listener.
 * @param pl q2jgame.PrintListener
 */
public static void addPrintListener(PrintListener pl) 
	{
	if (!gPrintListeners.contains(pl))
		gPrintListeners.addElement(pl);
	}
/**
 * Handle broadcast print messages.
 * @param flags int
 * @param msg java.lang.String
 */
public static void bprint(int flags, String msg) 
	{
	Engine.bprint(flags, msg);
	
	Enumeration enum = gPrintListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((PrintListener) enum.nextElement()).bprint(flags, msg);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Relay stuff sent to the console from outside the game.
 * @param s java.lang.String
 */
public void consoleOutput(String s) 
	{
	Enumeration enum = gPrintListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((PrintListener) enum.nextElement()).consoleOutput(s);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Handle debugging print messages.
 * @param msg java.lang.String
 */
public static void dprint(String msg) 
	{
	Engine.dprint(msg);
	
	Enumeration enum = gPrintListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((PrintListener) enum.nextElement()).dprint(msg);
			}
		catch (Exception e)
			{
			}
		}	
	}
/**
 * Fetch the current gametime, measured as seconds since 
 * the server (not the level) started.
 * @return float
 */
public static float getGameTime() 
	{
	return gGameTime;
	}
/**
 * Fetch a list of objects that were registered under a given key.
 * @return Vector containing registered objects
 * @param key java.lang.Object
 */
public static Vector getLevelRegistryList(Object key) 
	{
	Vector result = (Vector) gLevelRegistry.get(key);
	if (result == null)
		{
		result = new Vector();
		gLevelRegistry.put(key, result);
		}
		
	return result;
	}
/**
 * Let the DLL know what class to use for new Players.
 * @return java.lang.Class
 */
public Class getPlayerClass() throws ClassNotFoundException
	{
	return q2java.NativeEntity.class;
	}
/**
 * Lookup an float spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public static float getSpawnArg(String[] args, String keyword, float defaultValue) 
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		if (keyword == args[i])
			return Float.valueOf(args[i+1]).floatValue();

	return defaultValue;
	}
/**
 * Lookup an integer spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public static int getSpawnArg(String[] args, String keyword, int defaultValue) 
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		if (keyword == args[i])
			return Integer.parseInt(args[i+1]);

	return defaultValue;
	}
/**
 * Lookup a string spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public static String getSpawnArg(String[] args, String keyword, String defaultValue)
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		{
		if (keyword == args[i])
			return args[i+1];
		}
			
	return defaultValue;
	}
/**
 * Called by the DLL when Quake II calls the DLL's init() function.
 */
public void init()
	{	
	// actually initialize the game
	Engine.debugLog("Game.init()");
	
	// setup to manage FrameListeners
	gFrameBeginning = new FrameList(64, 16);
	gFrameMiddle = new FrameList(512, 128);
	gFrameEnd = new FrameList(64, 16);
		
	// setup to manage GameListeners
	gGameListeners = new Vector();		
	
	// setup to manage LevelListeners
	gLevelListeners = new Vector();
	
	// setup to manage PrintListeners
	gPrintListeners = new Vector();	
	
	// setup hashtable to let objects find each other in a level
	gLevelRegistry = new Hashtable();
		
	// setup to manage Game mods
	gModList = new Vector();
	gModHash = new Hashtable();
	gClassHash = new Hashtable();		
				
	gFrameCount = 0;
	gGameTime = 0;				
	
	CVar packages = new CVar("q2jgame_packages", "baseq2", 0);
	StringTokenizer st = new StringTokenizer(packages.getString(), ";,/\\+");
	Vector v = new Vector();
	while (st.hasMoreTokens())
		v.addElement(st.nextToken());

	for (int i = v.size()-1; i >= 0; i--)
		addPackage((String) v.elementAt(i));			

	Engine.debugLog("Game.init() finished");
	}
/**
 * Look through the game mod list, trying to find a class
 * that matches the classSuffix
 * 
 * @return java.lang.Class
 * @param classSuffix The end of a classname, for example ".spawn.weapon_shotgun";
 */
public static Class lookupClass(String classSuffix) throws ClassNotFoundException
	{
	Class result = (Class) gClassHash.get(classSuffix);
	if (result != null)
		return result;
		
	Enumeration enum = gModList.elements();
	while (enum.hasMoreElements())
		{
		String s = (String) enum.nextElement();
		try
			{
			result = Class.forName(s + classSuffix);
			gClassHash.put(classSuffix, result);
			return result;
			}
		catch (ClassNotFoundException e)
			{
			}
		}
		
	throw new ClassNotFoundException("Couldn't find a match for [" + classSuffix + "]");
	}
/**
 * Called by the DLL when a new player connects. Throw an exception to reject the connection.
 * @param playerEntity instance of NativeEntity (or a subclass of NativeEntity) that represents the player.
 * @param userinfo Player's basic info
 * @param loadgame boolean
 */
public void playerConnect(NativeEntity playerEntity, boolean loadgame) throws Exception, Throwable
	{
	try
		{
		Class playerClass = Game.lookupClass(".Player");
	
		Class[] paramTypes = new Class[2];
		paramTypes[0] = q2java.NativeEntity.class;
		paramTypes[1] = java.lang.Boolean.TYPE;

		Method initMethod = playerClass.getMethod("connect", paramTypes);

		Object[] params = new Object[2];
		params[0] = playerEntity;
		params[1] = new Boolean(loadgame);

		initMethod.invoke(null, params);		
		}
	catch (InvocationTargetException ite)
		{
		throw ite.getTargetException();
		}
	}
/**
 * Called by the DLL when the DLL's ReadGame() function is called.
 */
public void readGame(String filename)
	{
	Enumeration enum = gGameListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((GameStatusListener) enum.nextElement()).readGame(filename);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Called by the DLL when the DLL's ReadLevel() function is called.
 */
public void readLevel(String filename)
	{
	Enumeration enum = gGameListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((GameStatusListener) enum.nextElement()).readLevel(filename);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Remove a normal FrameListener that's registered to listen
 * for the middle phase of Server Frames. 
 *
 * @param f q2jgame.FrameListener
 */
public static void removeFrameListener(FrameListener f) 
	{
	gFrameMiddle.removeFrameListener(f);
	}
/**
 * Remove a FrameListener that's registered to be notified
 * for specific phases of the Server frame.
 *
 * @param f q2jgame.FrameListener
 * @param phase A combination (or'ed or added together) of the FrameManager.FRAME_* constants.
 */
public static void removeFrameListener(FrameListener f, int phase) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.removeFrameListener(f);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.removeFrameListener(f);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.removeFrameListener(f);
	}	
/**
 * Remove a game listener.
 * @param pl q2jgame.GameListener
 */
public static void removeGameStatusListener(GameStatusListener gl) 
	{
	gGameListeners.removeElement(gl);
	}
/**
 * Remove a game listener.
 * @param pl q2jgame.GameListener
 */
public static void removeLevelListener(LevelListener x) 
	{
	gLevelListeners.removeElement(x);
	}
/**
 * This method was created by a SmartGuide.
 * @param key java.lang.Object
 * @param value java.lang.Object
 */
public static void removeLevelRegistry(Object key, Object value)
	{
	Vector list = (Vector) gLevelRegistry.get(key);
	if (list != null)
		list.removeElement(value);
	}
/**
 * Remove a module from the game's package list
 * @param packageName java.lang.String
 */
public static void removePackage(String packageName) 
	{
	gModList.removeElement(packageName);

	// clear the cache so the old package won't be 
	// looked at when looking up classes
	gClassHash.clear(); 
	
	Class modClass = (Class) gModHash.get(packageName);
	if (modClass != null)
		{
		gModHash.remove(packageName);
		try
			{
			Method unloadMethod = modClass.getMethod("unload", null);
			unloadMethod.invoke(null, null);
			}
		catch (NoSuchMethodException nsme)
			{
			}
		catch (InvocationTargetException ite)
			{
			ite.getTargetException().printStackTrace();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}	
		}
	gModHash.remove(packageName);
	}
/**
 * Remove a listener.
 * @param pl q2jgame.PrintListener
 */
public static void removePrintListener(PrintListener pl) 
	{
	gPrintListeners.removeElement(pl);
	}
/**
 * Called by the DLL when the DLL's RunFrame() function is called.
 */
public void runFrame()
	{
	long startTime = System.currentTimeMillis();
		
	// increment the clocks
	// if we just added Engine.SECONDS_PER_FRAME to fGameTime,
	// the clock drifts due to rounding errors, so we
	// have to keep a frame count and multiply.
	gFrameCount++;
	gGameTime = gFrameCount * Engine.SECONDS_PER_FRAME;

	gFrameBeginning.runFrame(FRAME_BEGINNING, gGameTime);
	gFrameMiddle.runFrame(FRAME_MIDDLE, gGameTime);
	gFrameEnd.runFrame(FRAME_END, gGameTime);
		
	long endTime = System.currentTimeMillis();
	gCPUTime += (endTime - startTime);		
	}
/**
 * Called by the DLL when the DLL's ServerCommand() function is called.
 * The admin can type "sv xxx a b" at the console, this method
 * will use reflection to look for a method named svcmd_xxx and execute
 * it if possible, otherwise just repeat the command back to the console. 
 */
public void serverCommand() 
	{
	String[] sa = new String[Engine.getArgc()];
	for (int i = 0; i < sa.length; i++)
		sa[i] = Engine.getArgv(i);

	Class[] paramTypes = new Class[1];
	paramTypes[0] = sa.getClass();


	// look for a built-in command first
	try
		{		
		java.lang.reflect.Method meth = getClass().getMethod("svcmd_" + sa[1].toLowerCase(), paramTypes);						
		Object[] params = new Object[1];
		params[0] = sa;
		meth.invoke(this, params);
		}
	catch (NoSuchMethodException nsme)
		{				
		// look for a module command second
		try
			{
			Class cls = lookupClass(".svcmd_" + sa[1].toLowerCase());
			java.lang.reflect.Method meth = cls.getMethod("run", paramTypes);						
			Object[] params = new Object[1];
			params[0] = sa;
			meth.invoke(this, params);
			}
		catch (ClassNotFoundException cnfe)
			{
			// Send unrecognized input back to the console
			dprint("serverCommand()\n");
			dprint("    args(): [" + Engine.getArgs() + "]\n");
			for (int i = 0; i < sa.length; i++)
				dprint("    argv(" + i + "): [" + sa[i] + "]\n");							
			}
		catch (java.lang.reflect.InvocationTargetException ite)		
			{
			ite.getTargetException().printStackTrace();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}									
		}
	catch (java.lang.reflect.InvocationTargetException ite2)		
		{
		ite2.getTargetException().printStackTrace();
		}
	catch (Exception e2)
		{
		e2.printStackTrace();
		}
	}	
/**
 * Called by the DLL when the DLL's Shutdown() function is called.
 */
public void shutdown()
	{
	Enumeration enum = gGameListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((GameStatusListener) enum.nextElement()).shutdown();
			}
		catch (Exception e)
			{
			}
		}

	Engine.debugLog("Game.shutdown()");
	}
/**
 * Spawn entities into the Quake II environment.
 * This methods parses the entString passed to it, and looks
 * for Java classnames equivalent to the classnames specified 
 * in the entString, and instantiates instances of them, with
 * the entity parameters passed as an array of Strings.
 */
private void spawnEntities(String entString)
	{
	try
		{
		StringReader sr = new StringReader(entString);
		StreamTokenizer st = new StreamTokenizer(sr);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		int token;
		Vector v = new Vector(16);
		boolean foundClassname = false;
		String className = null;

		Object[] params = new Object[1];
		Class[] paramTypes = new Class[1];
		String[] sa = new String[1];
		paramTypes[0] = sa.getClass();

		while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
			{
			switch (token)
				{
				case '"' : 
					if (foundClassname)
						{
						className = st.sval;
						foundClassname = false;
						break;
						}
					if (st.sval.equalsIgnoreCase("classname"))
						{
						foundClassname = true;
						break;
						}
					v.addElement(st.sval.intern()); 
					break;

				case '{' : foundClassname = false; break;

				case '}' : 
					sa = new String[v.size()];
					v.copyInto(sa);
					v.removeAllElements();
					params[0] = sa;
					try
						{
						Class entClass = lookupClass(".spawn." + className.toLowerCase());
						Constructor ctor = entClass.getConstructor(paramTypes);							
						ctor.newInstance(params);
						}
					// this stinks..since we're using reflection to find the 
					// constructor, the compiler can't tell what exceptions
					// it'll throw - so we have to catch 'em all
					// and sort out the ones we really want to deal with.
					catch (InvocationTargetException ite)
						{
						Throwable te = ite.getTargetException();
						if (!(te instanceof InhibitedException))
							te.printStackTrace();						
						}
					catch (ClassNotFoundException cnfe)
						{
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}

					foundClassname = false;
					className = null;
					break;

				default  : 
					foundClassname = false;				
				}
			}
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Spawn entities into the Quake II environment.
 * This methods parses the entString passed to it, and looks
 * for Java classnames equivalent to the classnames specified 
 * in the entString, and instantiates instances of them, with
 * the entity parameters passed as an array of Strings.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	Enumeration enum = gLevelListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((LevelListener) enum.nextElement()).startLevel(mapname, entString, spawnPoint);
			}
		catch (Exception e)
			{
			}
		}

	// purge non-player NativeEntity objects from FrameListener lists
	gFrameBeginning.purge();
	gFrameMiddle.purge();
	gFrameEnd.purge();
	
	// clear the object registry
	gLevelRegistry.clear();
	
	// force a gc, to clean things up from the last level, before
	// spawning all the new entities
	System.gc();		

	spawnEntities(entString);
					
	// now, right before the game starts, is a good time to 
	// force Java to do another garbage collection to tidy things up.
	System.gc();		
	}
/**
 * Let the user add a game module.
 */
public static void svcmd_addpackage(String[] args) 
	{
	if (args.length < 3)
		dprint("Usage: sv addpackage <package-name>\n");
	else
		addPackage(args[2]);		
	}
/**
 * Print timing info to the console.
 */
public static void svcmd_help(String[] args) 
	{
	if (args.length > 2)		
		{
		Class modClass = (Class) gModHash.get(args[2]);
		if (modClass == null)
			{
			dprint("Sorry, " + args[2] + " isn't a loaded mod\n");
			return;
			}
			
		try
			{
			Method helpMethod = modClass.getMethod("help", null);
			helpMethod.invoke(null, null);
			}
		catch (NoSuchMethodException nsme)
			{
			dprint("Sorry, no help available for " + args[2] + "\n");
			}
		catch (InvocationTargetException ite)
			{
			ite.getTargetException().printStackTrace();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
			
		return;		
		}
	
	dprint("Q2Java Game Framework\n\n");
	dprint("   commands:\n");
	dprint("      sv addpackage <package-name>\n");
	dprint("      sv removepackage <package-name>\n");
	dprint("\n");
	dprint("      sv javamem\n");
	dprint("      sv javagc\n");
	dprint("\n");
	dprint("      sv time\n");
	dprint("      sv help <package-name>\n");
	dprint("\n");
	dprint("   loaded packages:\n");

	for (int i = 0; i < gModList.size(); i++)
		dprint("      " + gModList.elementAt(i) + "\n");		
	}
/**
 * Force the Java Garbage collector to run.
 */
public static void svcmd_javagc(String[] args) 
	{
	long oldFree, freeMem, totalMem;
	Runtime rt = Runtime.getRuntime();

	oldFree = rt.freeMemory();
	System.gc();
	freeMem = rt.freeMemory();
	totalMem = rt.totalMemory();
		
	dprint((freeMem - oldFree) + " bytes freed in GC\n");
	dprint("Total Java memory: " + totalMem + " bytes    Used: " + (totalMem - freeMem) + " Free: " + freeMem + "\n");
	}
/**
 * Print Java memory info to the console.
 */
public static void svcmd_javamem(String[] args) 
	{
	long totalMem, freeMem;
	Runtime rt = Runtime.getRuntime();
	totalMem = rt.totalMemory();
	freeMem = rt.freeMemory();
	dprint("Total Java memory: " + totalMem + " bytes    Used: " + (totalMem - freeMem) + " Free: " + freeMem + "\n");
	}
/**
 * Let the user remove a game module.
 */
public static void svcmd_removepackage(String[] args) 
	{
	if (args.length < 3)
		dprint("Usage: sv removepackage <package-name>\n");
	else
		removePackage(args[2]);		
	}
/**
 * Print timing info to the console.
 */
public static void svcmd_time(String[] args) 
	{
	dprint(gFrameCount + " server frames, " + gCPUTime + " milliseconds, " + (((double)gCPUTime) / ((double)gFrameCount)) + " msec/server frame\n");
	}
/**
 * Called by the DLL when the DLL's WriteGame() function is called.
 */
public void writeGame(String filename)
	{
	Enumeration enum = gGameListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((GameStatusListener) enum.nextElement()).writeGame(filename);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Called by the DLL when the DLL's WriteLevel() function is called.
 */
public void writeLevel(String filename)
	{
	Enumeration enum = gGameListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((GameStatusListener) enum.nextElement()).writeLevel(filename);
			}
		catch (Exception e)
			{
			}
		}
	}
}