package q2java.core;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.event.*;
import q2java.baseq2.*;

/**
 * This class provides the framework for a Quake II Java game. 
 * All the fields are static, so that objects can refer
 * to them without having to keep a reference to the solitary
 * Game object that's instantiated.
 *
 * Updated by Withnails 04/22/98
 * @author Barry Pederson 
 */

public class Game implements GameListener, JavaConsoleListener
	{
	// Manage FrameListeners
	public final static int FRAME_BEGINNING	= 0x01;
	public final static int FRAME_MIDDLE	= 0x02;
	public final static int FRAME_END		= 0x04;
	
	private static FrameList gFrameBeginning;
	private static FrameList gFrameMiddle;
	private static FrameList gFrameEnd;
		
	// Event delegation support
	private static ServerCommandSupport gServerCommandSupport = null;
	private static GameStatusSupport gGameStatusSupport = null;
	private static OccupancySupport gOccupancySupport = null;
	private static GameletSupport gGameletSupport = null;

	// Manage PrintListeners
	private static Vector gPrintListeners;	

	// Manage mod packages
	//Withnails 04/22/98 - mods are now managed by GameClassFactory
	protected static GameClassFactory gClassFactory;
	protected static CVar gModules; // for persistant list of loaded modules

	// fields used when loading new gamelets
	protected static Vector  gNewGameletList;
	protected static boolean gNewGameletsShouldInit;
	
	// Track the current player class and the gamelet it came from
	protected static Class	 gPlayerClass;
	protected static Gamelet gPlayerGamelet;
	
	// Allow objects to find each other
	private static Hashtable gLevelRegistry;

	// Handle spawning entities
	private static LevelDocumentFactory gLevelDocumentFactory;

	// DOM Document describing initial info for the current level
	private static Document gLevelDocument;
	
	// game clocks
	private static int gFrameCount;
	private static float gGameTime;
	private boolean gLevelStarted;
	
	// performance clocks
	private static int  gPerformanceFrames;
	private static long gCPUTime;	
	
	private static Vector gResourceGroups;	
	
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
 * Add a new module to the game.  
 * If a module has already been added, nothing happens.
 * @param packageName java.lang.String
 * @param alias short name that the gamelet will be referred to as.
 */
public static Gamelet addGamelet(String className, String alias) throws ClassNotFoundException
	{
	gNewGameletList = new Vector();
	gNewGameletsShouldInit = true;

	// actually add the gamelet and any others it depends on
	Gamelet g = addGamelet0(className, alias, null);
	
	if (gNewGameletsShouldInit)
		{
		Enumeration enum = gNewGameletList.elements();
		while (enum.hasMoreElements())
			{
			Gamelet g2 = (Gamelet) enum.nextElement();
			g2.markInitialized();
			g2.init();
			}			
		}
		
	saveGameletList();

	return g;
	}
/**
 * This method was created in VisualAge.
 * @param className java.lang.String
 * @param alias java.lang.String
 */
private static Gamelet addGamelet0(String className, String alias, Gamelet higherGamelet) throws ClassNotFoundException
	{
	if (alias == null)
		{
		// build up a default alias for this gamelet
		int p = className.lastIndexOf('.');
		if (p >= 0)
			alias = className.substring(p+1);
		else
			alias = "unknown";
		}
		
	Gamelet g = gClassFactory.addGamelet(className, alias, higherGamelet);
	
	if (g != null)
		{
		// the class factory successfully added the Gamelet		
		gGameletSupport.fireEvent( GameletEvent.GAMELET_ADDED, g);			
		
		gNewGameletList.insertElementAt(g, 0);
		gNewGameletsShouldInit &= !(g.isLevelChangeRequired());
		
		// make sure dependent gamelets are loaded
		String[] dependencies = g.getGameletDependencies();
		if (dependencies != null)
			{
			for (int i = 0; i < dependencies.length; i++)
				{
				try
					{
					// see if the class factory already has a gamelet we depend on
					Class depClass = Class.forName(dependencies[i]);
					Gamelet other = gClassFactory.getGamelet(depClass);					
				
					if (other == null)
						// not loaded? then recursively call to add -that- gamelet and -its- dependencies
						addGamelet0(dependencies[i], null, g);
					else
						// already loaded, think about whether we should initialize or not
						gNewGameletsShouldInit &= (other.isInitialized() || (!other.isLevelChangeRequired()));
					}
				catch (ClassNotFoundException cnfe)
					{
					}
				}
			}
		}
		
	return g;
	}
/**
 * Other packages or mods may wish to be notified when a package is added to 
 * the game. This event based interface introduces a 'PackageListener'.
 * @param pl The PackageListener to add
 */
public static void addGameletListener(GameletListener l) 
	{
	gGameletSupport.addGameletListener(l);
	}
/**
 * Add a game listener.
 * @param pl q2jgame.GameListener
 */
public static void addGameStatusListener(GameStatusListener gsl) 
	{
	gGameStatusSupport.addGameStatusListener(gsl);
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
 * Add an object that wants to receive broadcasts that are localized with the default locale.
 * @param obj an object that implements LocaleListener - if this object
 *   has already been added, it'll be removed before being re-added.
 * @return ResourceGroup this object is now registered with.
 */
public static ResourceGroup addLocaleListener(LocaleListener obj) 
	{
	return addLocaleListener(obj, Locale.getDefault());
	}
/**
 * Add an object that wants to receive broadcasts that are localized with the default locale.
 * @param obj an object that implements LocaleListener - if this object
 *   has already been added, it'll be removed before being re-added.
 * @param localeName String representation of locale
 * @return ResourceGroup this object is now registered with.
 */
public static ResourceGroup addLocaleListener(LocaleListener obj, String localeName) 
	{
	return addLocaleListener(obj, GameUtil.getLocale(localeName));
	}
/**
 * Add an object that wants to receive broadcasts that are localized.
 * @param obj an object that implements LocaleListener.
 * @param loc java.util.Locale
 * @return ResourceGroup this object is now registered with. 
 */
public static ResourceGroup addLocaleListener(LocaleListener obj, Locale loc) 
	{
	ResourceGroup grp = getResourceGroup(loc);
	grp.addLocaleListener(obj);

	return grp;
	}
/**
 * notify when number of players change or player changes class...
 */
public static void addOccupancyListener(OccupancyListener l) 
	{
	gOccupancySupport.addOccupancyListener(l);
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
 * notify server command issued...
 */
public static void addServerCommandListener(ServerCommandListener l) 
	{
	gServerCommandSupport.addServerCommandListener(l);
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
 * This method was created by a SmartGuide.
 * @return boolean
 * @param alias java.lang.String
 * @param cmd java.lang.String
 * @param args java.lang.String[]
 */
private boolean externalServerCommand(String alias, String cmd, Class[] paramTypes, Object[] params) 
	{
	Gamelet g = gClassFactory.getGamelet(alias);
	if (g == null) 
		{
		// _Quinn:05/15/98
		dprint( alias + " is not a loaded gamelet.\n" );
		return false;
		}

	return externalServerCommand(g, cmd, paramTypes, params);
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param alias java.lang.String
 * @param cmd java.lang.String
 * @param args java.lang.String[]
 */
private boolean externalServerCommand(Gamelet g, String cmd, Class[] paramTypes, Object[] params) 
	{
	try
		{
		java.lang.reflect.Method meth = g.getClass().getMethod("svcmd_" + cmd, paramTypes);						
		meth.invoke(g, params);
		}
	catch (NoSuchMethodException nsme)
		{
		return false;
		}
	catch (java.lang.reflect.InvocationTargetException ite)		
		{
		Throwable t = ite.getTargetException();
		if (t instanceof ExceptionInInitializerError)
			t = ((ExceptionInInitializerError)t).getException();
		t.printStackTrace();		
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}		
									
	return true;
	}
/**
 * complement to setClassFactory, used for prp.
 */
public static GameClassFactory getClassFactory() 
	{
	return gClassFactory;
	}
/**
 * Append a gamelet and all the other gamelets it depends on to a vector.
 * @param g Gamelet we're looking at.
 * @param v Vector to append the gamelet and its dependencies to.
 */
private static void getDependantGamelets(Gamelet g, Vector v) 
	{
	// bail if this gamelet is already on the list
	if (v.indexOf(g) >= 0)
		return;

	v.addElement(g);
	String clsName = g.getClass().getName();

	// look through all gamelets for ones that depend on this one
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements())
		{
		Gamelet g2 = (Gamelet) enum.nextElement();
		
		String[] dependencies = g2.getGameletDependencies();
		if (dependencies != null)
			{			
			for (int i = 0; i < dependencies.length; i++)
				{
				if (clsName.equals(dependencies[i]))
					{
					// g2 depends on g, so recursively call to see
					// what else depends on g
					getDependantGamelets(g2, v);
					break; // out of the for loop
					}
				}
			}
		}
	}
/**
 * Lookup a loaded package, based on its name.
 * @return q2jgame.LoadedPackage, null if not found.
 * @param alias java.lang.String
 */
public static Gamelet getGamelet(String alias) 
	{
	//Withnails 04/22/98
	return gClassFactory.getGamelet(alias);
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
 * Get the DOM document describing the current level.
 * @return org.w3c.dom.Document
 */
public static Document getLevelDocument() 
	{
	return gLevelDocument;
	}
/**
 * Get which object the Game is using to generate level DOM Documents
 * when a new map starts.
 * @return q2java.core.LevelDocumentFactory - will never be null.
 */
public static LevelDocumentFactory getLevelDocumentFactory() 
	{
	if (gLevelDocumentFactory == null)
		gLevelDocumentFactory = new DefaultLevelDocumentFactory();
		
	return gLevelDocumentFactory;
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
 * Get a ResourceGroup object that tracks a specified locale
 * @return q2jgame.ResourceGroup
 * @param loc java.util.Locale
 */
public static ResourceGroup getResourceGroup(Locale loc) 
	{
	ResourceGroup grp;
	
	for (int i = 0; i < gResourceGroups.size(); i++)
		{
		grp = (ResourceGroup) gResourceGroups.elementAt(i);
		if (grp.equalsLocale(loc))
			return grp;
		}
		
	// we didn't find a matching locale, so add a new one
	grp = new ResourceGroup(loc);
	gResourceGroups.addElement(grp);
	return grp;
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

	// event delegation support
	gGameStatusSupport = new GameStatusSupport();		
	gOccupancySupport = new OccupancySupport();
	gGameletSupport = new GameletSupport();
	gServerCommandSupport = new ServerCommandSupport();

	// setup to manage PrintListeners
	gPrintListeners = new Vector();	
	Engine.setJavaConsoleListener(this);
	
	// setup to manage BroadcastListeners and cached ResourceBundles
	gResourceGroups = new Vector();
	
	// setup hashtable to let objects find each other in a level
	gLevelRegistry = new Hashtable();

	// setup to manage Game mods
	//Withnails 04/22/98
	//gModList = new Vector();
	//gClassHash = new Hashtable();
	setClassFactory(new DefaultClassFactory());

	// Game clocks;
	gFrameCount = 0;
	gGameTime = 0;				

	// look for gamelets specified on the command line	
	gModules = new CVar("gamelets", "", 0);
	String modules = gModules.getString();

	if (!modules.equals(""))
		{
		//Withnails 04/23/98 - parses out multiple modules on command line, separated by ;,/\\+
		StringTokenizer st = new StringTokenizer(modules, ";,/\\+");
		Vector v = new Vector();
		while (st.hasMoreTokens())
			v.addElement(st.nextToken());

		//Quinn 06/16/98: handle commandline / stored aliases.
		//Withnails 04/23/98 - now load each package
		String temp = "";
		int tidx = 0;
		for (int i = v.size()-1; i >= 0; i--) 
			{
			temp = v.elementAt(i).toString();
			tidx = temp.indexOf( "[" );
			try
				{
				if ( tidx == -1 ) 
					addGamelet( temp, null );
				else 
					addGamelet( temp.substring( 0, tidx ), temp.substring( tidx + 1, temp.length() - 1 ) );
				}
			catch (ClassNotFoundException cnfe)
				{
				dprint("Error: " + temp + " not found\n");
				}
			} // end for loop.
		}
	else
		{
		// read module names from System properties
		int i = 1;
		String s;
		while ((s = System.getProperty("q2java.gamelet."+i, null)) != null)
			{
			StringTokenizer st = new StringTokenizer(s);
			String name = st.nextToken();
			String alias = null;
			if (st.hasMoreElements())
				alias = st.nextToken();

			try
				{
				addGamelet(name, alias);
				}
			catch (ClassNotFoundException cnfe)
				{
				dprint("Error: " + name + " not found\n");
				}
				
			i++;
			}

		// add default module if nothing else was loaded			
		if (i == 1)
			{
			try
				{				
				addGamelet("q2java.baseq2.BaseQ2", "baseq2");
				}
			catch (ClassNotFoundException cnfe)
				{
				dprint("Error: the default Gamelet q2java.baseq2.BaseQ2 not found\n");
				}
			}
		}

	// Added for delegation event model
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_INIT);
			
	Engine.debugLog("Game.init() finished");
	}
/**
 * Check if a resource can be located given a basename and key.
 * @return boolean
 * @param basename java.lang.String
 * @param key java.lang.String
 */
public static boolean isResourceAvailable(String basename, String key) 
	{
	if ((gResourceGroups == null) || (gResourceGroups.size() < 1))
		return false;
		
	ResourceGroup grp = (ResourceGroup) gResourceGroups.elementAt(0);
	ResourceBundle bundle = grp.getBundle(basename);
	try
		{
		Object obj = bundle.getObject(key);
		return true;
		}
	catch (MissingResourceException mre)
		{
		return false;
		}				
	}
/**
 * Relay stuff sent to the console from outside the game.
 * @param s java.lang.String
 */
public void javaConsoleOutput(String s) 
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
 * Broadcast a localized message to interested objects.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 * @param args args to pass to java.text.MessageFormat.format().
 * @param printLevel One of the Engine.PRINT_* constants.
 */
public static void localecast(String basename, String key, Object[] args, int printLevel) 
	{
	for (int i = 0; i < gResourceGroups.size(); i++)
		{
		ResourceGroup grp = (ResourceGroup) gResourceGroups.elementAt(i);
		grp.localecast(basename, key, args, printLevel);
		}
	}
/**
 * Broadcast a localized message to interested objects.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 * @param printLevel One of the Engine.PRINT_* constants.
 */
public static void localecast(String basename, String key, int printLevel) 
	{
	for (int i = 0; i < gResourceGroups.size(); i++)
		{
		ResourceGroup grp = (ResourceGroup) gResourceGroups.elementAt(i);
		grp.localecast(basename, key, printLevel);
		}
	}
/**
 * Look through the game mod list, trying to find a class
 * that matches the classSuffix
 * @param classSuffix Either a suffix, like ".spawn.weapon_shotgun", 
 * 	or a whole classname like "baseq2.spawn.weapon_shotgun"
 * @return The class matching the suffix/name
 * @exception java.lang.ClassNotFoundException if there was no match.
 */
public static Class lookupClass(String classSuffix) throws ClassNotFoundException
	{
	//Withnails 04/22/98
	return gClassFactory.lookupClass(classSuffix);
	}
/**
 * Called by the DLL when a new player connects. Throw an exception to reject the connection.
 * @param playerEntity instance of NativeEntity (or a subclass of NativeEntity) that represents the player.
 */
public void playerConnect(NativeEntity playerEntity) throws Exception, Throwable
	{
	Engine.debugLog("Game.playerConnect() class = " + gPlayerClass.getName());
	
	if (gPlayerClass == null)
		{
		Game.dprint("Big problem..none of the loaded gamelets specify a player class\n");
		return;
		}
	
	try
		{
		Class[] paramTypes = new Class[1];
		paramTypes[0] = q2java.NativeEntity.class;

		Method initMethod = gPlayerClass.getMethod("connect", paramTypes);

		Object[] params = new Object[1];
		params[0] = playerEntity;

		initMethod.invoke(null, params);		

		gOccupancySupport.fireEvent(playerEntity, OccupancyEvent.PLAYER_CONNECTED );
		}
	catch (InvocationTargetException ite)
		{
		throw ite.getTargetException();
		}
	catch (java.beans.PropertyVetoException pve)
		{
		Object o = playerEntity.getReference();
		if( o instanceof GameObject )
		    {
		    ((GameObject)o).dispose();
		    }
		throw pve;
		}  
	}
/**
 * Should be called by the DLL when a new player disconnects.
 * currently called by baseq2.Player
 * @param playerEntity instance of NativeEntity 
 * @author Peter Donald
 */
public static void playerDisconnect(NativeEntity playerEntity) 
	{
	try 
		{
	    gOccupancySupport.fireEvent(playerEntity, OccupancyEvent.PLAYER_DISCONNECTED );
		}
	catch(java.beans.PropertyVetoException pve) 
		{
		}
	}
/**
 * Called by the DLL when the DLL's ReadGame() function is called.
 */
public void readGame(String filename)
	{
	gGameStatusSupport.fireEvent( GameStatusEvent.GAME_READGAME, filename );
	}
/**
 * Called by the DLL when the DLL's ReadLevel() function is called.
 */
public void readLevel(String filename)
	{
	gGameStatusSupport.fireEvent( GameStatusEvent.GAME_READLEVEL, filename );
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
 * Remove a module from the game's package list
 * @param gm A loaded Gamelet
 */
public static void removeGamelet(Gamelet g) 
	{
	// get a list of all gamelets that depend on this one
	Vector v = new Vector();
	getDependantGamelets(g, v);

	// look them all over and decide if any require a level change
	// and will hold all the others up
	boolean levelChangeRequired = false;
	Enumeration enum = v.elements();
	while (enum.hasMoreElements())
		{
		Gamelet g2 = (Gamelet) enum.nextElement();
		levelChangeRequired |= g2.isLevelChangeRequired();
		}

	// deal with the gamelets we decided have to be unloaded together
	if (levelChangeRequired)
		{
		// mark the gamelets we want to unload at level change
		enum = v.elements();
		while (enum.hasMoreElements())
			{
			Gamelet g2 = (Gamelet) enum.nextElement();
			g2.markUnloading();
			}
		}
	else
		{
		// unload gamelets now, highest priority first
		enum = gClassFactory.getGamelets();
		while (enum.hasMoreElements())
			{
			Gamelet g2 = (Gamelet) enum.nextElement();
			if (v.indexOf(g2) >= 0)
				removeGamelet0(g2);
			}
		}
	}
/**
 * Do the dirty work of actually removing a gamelet. Doesn't check for
 * depencencies or whether a level change is required - that should have
 * already been handled by removeGamelet()
 *
 * @param gm A loaded Gamelet
 */
private static void removeGamelet0(Gamelet g) 
	{
	gGameletSupport.fireEvent( GameletEvent.GAMELET_REMOVED, g);
	gClassFactory.removeGamelet(g);
	saveGameletList();	
	}
/**
 * Removes a package listener
 * @param pl the PackageListener to remove
 */
public static void removeGameletListener(GameletListener l)
	{
	gGameletSupport.removeGameletListener(l);
	}
/**
 * Remove a game listener.
 * @param pl q2jgame.GameListener
 */
public static void removeGameStatusListener(GameStatusListener gsl) 
	{
	gGameStatusSupport.removeGameStatusListener(gsl);
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
 * Remove a broadcast listener from the game.
 * @param obj q2jgame.LocaleListener
 */
public static void removeLocaleListener(LocaleListener obj) 
	{
	for (int i = 0; i < gResourceGroups.size(); i++)
		{
		ResourceGroup grp = (ResourceGroup) gResourceGroups.elementAt(i);
		grp.removeLocaleListener(obj);
		}
	}
/**
 * Remove a broadcast listener from a specific locale.
 * @param obj q2jgame.LocaleListener
 */
public static void removeLocaleListener(LocaleListener obj, String localeName) 
	{
	removeLocaleListener(obj, GameUtil.getLocale(localeName));
	}
/**
 * Remove a broadcast listener from a specific locale.
 * @param obj q2jgame.LocaleListener
 */
public static void removeLocaleListener(LocaleListener obj, Locale loc) 
	{
	ResourceGroup grp = getResourceGroup(loc);
	grp.removeLocaleListener(obj);	
	}
/**
 * notify when number of players change or player changes class...
 */
public static void removeOccupancyListener(OccupancyListener l) 
	{
	gOccupancySupport.removeOccupancyListener(l);
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
 * notify server command issued...
 */
public static void removeServerCommandListener(ServerCommandListener l) 
	{
	gServerCommandSupport.removeServerCommandListener(l);
	}
/**
 * Reconsider which class should be controlling players.
 */
public static void rethinkPlayerClass() 
	{
	Gamelet g = null;
	Class cls = null;
	
	// look for a gamelet that provides a player class
	// searching from highest priority on down
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements())
		{
		g = (Gamelet) enum.nextElement();
		if (!g.isInitialized())
			continue;
			
		cls = g.getPlayerClass();
		if (cls != null)
			break;
		}

	// check if the current player gamelet is different 
	// from what we found
	if (g != gPlayerGamelet)
		{
		// different? then do the big switch
		
		if (gPlayerGamelet != null)
			gPlayerGamelet.releasePlayers();

		gPlayerGamelet = g;
		gPlayerClass = cls;
		g.grabPlayers();
		}
	}
/**
 * Called by the DLL when the DLL's RunFrame() function is called.
 */
public void runFrame()
	{
	long startTime = Engine.getPerformanceCounter();
		
	// increment the clocks
	// if we just added Engine.SECONDS_PER_FRAME to fGameTime,
	// the clock drifts due to rounding errors, so we
	// have to keep a frame count and multiply.
	gFrameCount++;
	gGameTime = gFrameCount * Engine.SECONDS_PER_FRAME;

	// call all the objects that registered to have runFrame() called
	gFrameBeginning.runFrame(FRAME_BEGINNING, gGameTime);
	gFrameMiddle.runFrame(FRAME_MIDDLE, gGameTime);
	gFrameEnd.runFrame(FRAME_END, gGameTime);

	long endTime = Engine.getPerformanceCounter();
	gCPUTime += (endTime - startTime);	
	gPerformanceFrames++;	
	}
/**
 * Save the list of loaded gamelets in a CVar.
 */
protected static void saveGameletList() 
	{
	StringBuffer sb = new StringBuffer();
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements()) 
		{
		Gamelet g = (Gamelet) enum.nextElement();

		// insert the gamelet info in reverse order, so the
		// first gamelet loaded appears first when all is done.
		if (sb.length() > 0)
			sb.insert(0, '+');
		sb.insert(0, ']');
		sb.insert(0, g.getGameletName());
		sb.insert(0, '[');
		sb.insert(0,g.getClass().getName());
		}	
		
	gModules.setValue(sb.toString());		
	}
/**
 * Called by the DLL when the DLL's ServerCommand() function is called.
 * The admin can type "sv xxx a b" at the console, this method
 * will use reflection to look for a method named svcmd_xxx and execute
 * it if possible, otherwise just repeat the command back to the console. 
 */
public void serverCommand() 
	{
	// build up some arg params
	int argc = Engine.getArgc();
	String[] sa = new String[Math.max(argc, 2)]; // at least two args
	for (int i = 0; i < argc; i++)
		sa[i] = Engine.getArgv(i);			

	// special case where the user just typed "sv" by itself
	if (argc == 1)
		{
		sa[1] = "help";
		}
	else
		{
	    ServerCommandEvent e = gServerCommandSupport.fireEvent(sa[1], sa); 
	    if( e.isConsumed() )
	    	{
			return;
		    }
		}

	// create parameter type array for reflection
	Class[] paramTypes = new Class[1];
	paramTypes[0] = sa.getClass();
	Object[] params = new Object[1];
	params[0] = sa;

	// figure out what command we're processing, and which specific
	// package (if any) should handle it.
	String alias = null;
	String cmd;
	int dot = sa[1].lastIndexOf('.');
	if (dot < 0)
		cmd = sa[1].toLowerCase();
	else
		{
		alias = sa[1].substring(0, dot);
		cmd = sa[1].substring(dot+1).toLowerCase();
		}

	// run the command	
	if (alias != null)
		{
		if (externalServerCommand(alias, cmd, paramTypes, params))
			return;
		}
	else
		{
		// look for a built-in command first
		try
			{		
			java.lang.reflect.Method meth = getClass().getMethod("svcmd_" + sa[1].toLowerCase(), paramTypes);						
			meth.invoke(this, params);
			return;
			}
		catch (NoSuchMethodException nsme)
			{
			}			
		catch (java.lang.reflect.InvocationTargetException ite)		
			{
			Throwable t = ite.getTargetException();
			if (t instanceof ExceptionInInitializerError)
				t = ((ExceptionInInitializerError)t).getException();
			t.printStackTrace();			
			}
		catch (ExceptionInInitializerError eiie)
			{
			eiie.getException().printStackTrace();
			}
		catch (Exception e2)
			{
			e2.printStackTrace();
			}
								
		// look for a module command second
		//Withnails 04/22/98 - now makes use of an enumeration instead
		Enumeration enum = gClassFactory.getGamelets();
		while (enum.hasMoreElements()) 
			{
			Gamelet g = (Gamelet)enum.nextElement();
			if (externalServerCommand(g, cmd, paramTypes, params))
				return;
			}
		}
	
	// Send unrecognized input back to the console
	dprint("Unrecognized sv command\n");
	dprint("    args(): [" + Engine.getArgs() + "]\n");
	for (int i = 0; i < sa.length; i++)
		dprint("    argv(" + i + "): [" + sa[i] + "]\n");
	}
/**
 * Withnails 04/22/98
 * Sets the current ClassFactory. The init() method sets it to the default q2jgame.GameClassFactory
 * initially. Any mod may replace the add/remove package functionality by providing a subclass
 * of GameClassFactory and calling this method. This provides a 'boot-strapping' scheme which
 * means a mod can be loaded by the default system (i.e. Barrys implementation), and then
 * replace this with a custom system which is in place from then on.
 * <P>
 * Clashes between mods shouldn't be a problem, as long as any subclass of GameClassFactory
 * still provides access to the default classes. i.e. A mod should try its custom system, and 
 * if this fails, fall back to the original implementation.
 * <P>
 * @see GameClassFactory
 */
public static void setClassFactory(GameClassFactory gcf)
	{
	//Withnails 04/22/98
	gClassFactory = gcf;
	}
/**
 * Set which object will handle spawning entities when a new map is started.
 * @param sm q2java.core.SpawnManager may be null, which will cause the Game
 *  to create and set itself to use a DefaultSpawnManager.
 */
public static void setLevelDocumentFactory(LevelDocumentFactory ldf) 
	{
	gLevelDocumentFactory = ldf;
	}
/**
 * Called by the DLL when the DLL's Shutdown() function is called.
 */
public void shutdown()
	{
	// let interested objects know the last map is done
	if (gLevelStarted)
		gGameStatusSupport.fireEvent(GameStatusEvent.GAME_ENDLEVEL);

	// tell everyone we're on our way out		
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_SHUTDOWN);

	// unload the gamelets
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements()) 
		{
		Gamelet g = (Gamelet) enum.nextElement();
		try
			{
			gGameletSupport.fireEvent( GameletEvent.GAMELET_REMOVED, g);			
			g.unload();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
		
	Engine.debugLog("Game.shutdown()");
	}
/**
 * Read the current level DOM document and spawn the map's entities.
 */
private static void spawnEntities() 
	{
	Object[] ctorParams = new Object[1];
	Class[] oldCtorParamTypes = new Class[1];
	Class[] newCtorParamTypes = new Class[1];
	String[] sa = new String[1];
	oldCtorParamTypes[0] = sa.getClass();
	newCtorParamTypes[0] = Element.class;
	
	// look for <entity>..</entity> sections
	NodeList nl = gLevelDocument.getElementsByTagName("entity");
	for (int i = 0; i < nl.getLength(); i++)
		{
		Node n = nl.item(i);
		if (!(n instanceof Element))
			continue;

		Element e = (Element) n;
		String className = e.getAttribute("class");

		try
			{
			Class entClass = Game.lookupClass(".spawn." + className.toLowerCase());
			Constructor ctor;
			
			// get a constructor - look first for the new-style that takes a DOM element
			// and if that fails, go for the old-style that takes an array of strings
			try
				{
				ctor = entClass.getConstructor(newCtorParamTypes);
				ctorParams[0] = e;
				}
			catch (NoSuchMethodException nsme)
				{			
				ctor = entClass.getConstructor(oldCtorParamTypes);
				ctorParams[0] = DefaultLevelDocumentFactory.getParamPairs(e);			
				}

			// we must had found an constructor, so create the object
			ctor.newInstance(ctorParams);
			}
		catch (ClassNotFoundException cnfe)
			{
			Engine.debugLog("Couldn't find class to handle: " + className);
			}
		catch (InhibitedException ie)
			{
			}
		catch (InvocationTargetException ite)
			{
			ite.getTargetException().printStackTrace();
			}
		catch (Throwable t)
			{
			t.printStackTrace();
			}
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
	Engine.debugLog("Game.startLevel(\"" + mapname + "\", <entString>, \"" + spawnPoint + "\")");

	// let interested objects know the last map is done
	if (gLevelStarted)
		gGameStatusSupport.fireEvent(GameStatusEvent.GAME_ENDLEVEL);

	gLevelStarted = false;
	
	// purge non-player NativeEntity objects from FrameListener lists
	gFrameBeginning.purge();
	gFrameMiddle.purge();
	gFrameEnd.purge();
	
	// clear the object registry
	gLevelRegistry.clear();
	
	// look for gamelets that were waiting for a level change
	Vector initList = new Vector();
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements())
		{
		Gamelet g = (Gamelet) enum.nextElement();
		if (g.isUnloading())
			removeGamelet0(g);
		else if (!g.isInitialized())
			initList.insertElementAt(g, 0); // build list in reverse order
		}

	// initialize gamelets we found uninitialized
	enum = initList.elements();
	while (enum.hasMoreElements())
		{
		Gamelet g = (Gamelet) enum.nextElement();
		g.markInitialized();
		g.init();	
		}
	
	// Build up a DOM document representing the contents of the new map
	gLevelDocument = getLevelDocumentFactory().createLevelDocument(mapname, entString, spawnPoint);

	// let interested objects know we're starting a new map
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_PRESPAWN);	

	// ponder whether or not we need to change player classes
	rethinkPlayerClass();
	
	// force a gc, to clean things up from the last level, before
	// spawning all the new entities
	System.gc();		
	
	// read through the document and actually create the entities
	spawnEntities();

	// let interested objects know that map entities have been spawned.
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_POSTSPAWN);	
						
	// now, right before the game starts, is a good time to 
	// force Java to do another garbage collection to tidy things up.
	System.gc();	

	gPerformanceFrames = 0;
	gCPUTime = 0;
	gLevelStarted = true;
	}
/**
 * Let the user add a game module.
 */
public static void svcmd_addgamelet(String[] args) 
	{
	if (args.length < 3)
		{
		dprint("Usage: sv addgamelet <class-name> [<alias>]\n");
		return;
		}
		
	String alias = null;
	
	if (args.length > 3)
		alias = args[3];

	try
		{
		addGamelet(args[2], alias);
		}
	catch (ClassNotFoundException cnfe)
		{
		dprint("Error: " + args[2] + " not found\n");
		}
	}
/**
 * Print timing info to the console.
 */
public static void svcmd_help(String[] args) 
	{	
	dprint("Q2Java Game Framework\n\n");
	dprint("   commands:\n");
	dprint("      sv addgamelet <class-name> [<alias>]\n");
	dprint("      sv removegamelet <alias>\n");
	dprint("\n");
	dprint("      sv javamem       // show Java memory usage\n");
	dprint("      sv javagc        // force a Java GC\n");
	dprint("\n");
	dprint("      sv time          // show performance timing\n");
	dprint("      sv help          // this screen\n");
	dprint("      sv <gamelet>.help // help for a loaded module\n");


	//Withnails 04/22/98 - now makes use of an enumeration instead
	dprint("\n");
	dprint("   loaded gamelets:\n");

	boolean foundLoad = false;
	boolean foundUnload = false;
	
	Enumeration enum = gClassFactory.getGamelets();
	while (enum.hasMoreElements()) 
		{
		Gamelet g = (Gamelet) enum.nextElement();
		if (g.isUnloading())
			{
			dprint("     (unload) " + g.getGameletName() + "\n");
			foundUnload = true;
			}
		else if (g.isInitialized())
			dprint("              " + g.getGameletName() + "\n");        
		else
			{
			dprint("       (load) " + g.getGameletName() + "\n");
			foundLoad = true;
			}
		}
	dprint("\n");

	// explain the (load)/(unload) notes if necessary
	if (foundLoad)
		dprint("    (load) = uninitialized gamelet waiting for level change\n");
	if (foundUnload)
		dprint("  (unload) = gamelet that will be removed on next level change\n");
	}
/**
 * Force the Java Garbage collector to run.
 */
public static void svcmd_javagc(String[] args) 
	{
	long oldFree, freeMem, totalMem, gcStart, gcStop;
	Runtime rt = Runtime.getRuntime();

	oldFree = rt.freeMemory();
	gcStart = Engine.getPerformanceCounter();
	System.gc();
	gcStop = Engine.getPerformanceCounter();
	freeMem = rt.freeMemory();
	totalMem = rt.totalMemory();
		
	dprint("GC freed " + (freeMem - oldFree) + " bytes in " + (((double)(gcStop-gcStart) / Engine.getPerformanceFrequency()) * 1000.0) + " msec\n");
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
 * Dump a list of system properties.
 */
public static void svcmd_properties(String[] args) 
	{
	Properties props = System.getProperties();
	Enumeration names = props.propertyNames();
	while (names.hasMoreElements())
		{
		String name = (String) names.nextElement();
		String value = props.getProperty(name);
		dprint("[" + name + "] = [" + value + "]\n");
		}
	}
/**
 * Let the user remove a game module.
 */
public static void svcmd_removegamelet(String[] args) 
	{
	if (args.length < 3)
		dprint("Usage: sv removegamelet <alias>\n");
	else
		{
		Gamelet g = gClassFactory.getGamelet(args[2]);
		removeGamelet(g);
		}
	}
/**
 * Print timing info to the console. and reset counters.
 */
public static void svcmd_time(String[] args) 
	{
	double frames = (double) gPerformanceFrames;
	double tpm = Engine.getPerformanceFrequency() / 1000.0;
	double msec = gCPUTime / tpm;
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");

	// there seems to be a bug in the JDK where using the same format twice causes 
	//the second usage to take on the number of digits to the left of the decimal
	// as the first usage
	java.text.DecimalFormat df2 = new java.text.DecimalFormat(".000"); 

	dprint("Java: " + gCPUTime + " ticks, " + gPerformanceFrames + " frames, " + df.format(tpm) + " ticks/msec " + df2.format(msec / frames) + " msec/frame average\n");

	gPerformanceFrames = 0;
	gCPUTime = 0;
	}
/**
 * Called by the DLL when the DLL's WriteGame() function is called.
 */
public void writeGame(String filename, boolean autosave)
	{
	gGameStatusSupport.fireEvent( GameStatusEvent.GAME_WRITEGAME, filename );
	}
/**
 * Called by the DLL when the DLL's WriteLevel() function is called.
 */
public void writeLevel(String filename)
	{
	gGameStatusSupport.fireEvent( GameStatusEvent.GAME_WRITEGAME, filename );
	}
}