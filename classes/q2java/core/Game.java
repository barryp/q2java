package q2java.core;

import java.lang.reflect.*;
import java.io.*;
import java.net.InetAddress;
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
 * Updated by leighd 04/11/99
 * @author Barry Pederson 
 */

public class Game implements GameListener, JavaConsoleListener
	{
	// Manage FrameListeners
	public final static int FRAME_BEGINNING	= 0x01;
	public final static int FRAME_MIDDLE	= 0x02;
	public final static int FRAME_END		= 0x04;
	
	private static ServerFrameSupport gFrameBeginning;
	private static ServerFrameSupport gFrameMiddle;
	private static ServerFrameSupport gFrameEnd;
		
	// Event delegation support
	private static ServerCommandSupport gServerCommandSupport = null;
	private static GameStatusSupport gGameStatusSupport = null;
	private static OccupancySupport gOccupancySupport = null;

	// Manage PrintListeners
	private static PrintSupport gPrintSupport;
	
	// leighd 04/07/99
	// Manage gamelets and game classes
	protected static GameClassFactory gClassFactory = null;
	protected static GameletManager gGameletManager = null;
  
	// Track the current player class and the gamelet it came from
	protected static Class	 gPlayerClass;
	protected static Gamelet gPlayerGamelet;

	// Track a list of packages to search for partially specified classnames
	private static Vector gPackagePath;
	private static class PackageEntry
		{
		String fPackageName;
		int fCount = 1;
		PackageEntry(String s)
			{
			fPackageName = s;
			}
		}
	
	// Allow objects to find each other
	private static Hashtable gLevelRegistry;

	// DOM Documents describing initial info for the current level
	// and current game status
	private static Hashtable gDocumentHashtable;
//	private static Document gLevelDocument;
//	private static Document gStatusDocument;
//	private static Element gStatusDocumentRoot;

	// remember the name of what we're playing.
	private static String gCurrentMapName;
	
	// game clocks
	private static int gFrameCount;
	private static float gGameTime;
	private boolean gLevelStarted;
	
	// performance clocks
	private static int  gPerformanceFrames;
	private static long gCPUTime;

	private static Vector gResourceGroups;	

	// leighd 04/10/99 
	// object which registers Game to receive server commands
	private static ServerCommandListener gServerCommands = null;
	
/**
 * Private constructor to keep anything other than the 
 * native code from creating instances of this class.
 */
private Game() 
	{
	}
/**
 * Register an object that implements FrameListener to 
 * receive normal (FRAME_MIDDLE phase) frame events.
 *
 * @param f object that wants its runFrame() method called.
 * @param delay Number of seconds to wait before calling the listener, use 0 to start calling right away.
 * @param interval Number of seconds between calls, use 0 to call on every frame, a negative interval will
 *   be a one-shot notification with the listener removed automatically afterwards.
 * @deprecated use addServerFrameListener instead 
 */
public static void addFrameListener(ServerFrameListener f, float delay, float interval) 
	{
	gFrameMiddle.addServerFrameListener(f, delay, interval);
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
 * @deprecated use addServerFrameListener instead
 */
public static void addFrameListener(ServerFrameListener f, int phase, float delay, float interval) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.addServerFrameListener(f, delay, interval);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.addServerFrameListener(f, delay, interval);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.addServerFrameListener(f, delay, interval);
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
 * notify when number of players change or player changes class...
 */
public static void addOccupancyListener(OccupancyListener l) 
	{
	gOccupancySupport.addOccupancyListener(l);
	}
/**
 * Add the name of a package to a list of packages that will
 * be searched for classes.  If the specified package is already
 * in the list, a counter is incremented to track how many times
 * the package has been added.  Otherwise, add it with the
 * initial count to 1, and update the class factory with the
 * expanded list.
 *
 * When the game is looking up a class, packages are searched in
 * reverse order of the sequence they were added...so the last
 * package added is the first one searched, and the first one
 * added is actually the last one searched.
 *
 * @param pathName name of a package, such as "q2java.baseq2"
 * @see q2java.core.Game#removePackagePath 
 */
public static void addPackagePath(String pathName)
	{
	if (pathName == null)
		return;

	Enumeration enum = gPackagePath.elements();
	while (enum.hasMoreElements())
		{
		PackageEntry pe = (PackageEntry) enum.nextElement();
		if (pathName.equals(pe.fPackageName))
			{
			pe.fCount++;
			return;
			}
		}

	gPackagePath.addElement(new PackageEntry(pathName));

	// let the class factory know things have changed.
	if (gClassFactory != null)
		gClassFactory.setPackagePath(getPackagePaths());
	}
/**
 * notify server command issued...
 */
public static void addServerCommandListener(ServerCommandListener l) 
	{
	gServerCommandSupport.addServerCommandListener(l);
	}
/**
 * Register an object that implements FrameListener to 
 * receive normal (FRAME_MIDDLE phase) frame events.
 *
 * @param f object that wants its runFrame() method called.
 * @param delay Number of seconds to wait before calling the listener, use 0 to start calling right away.
 * @param interval Number of seconds between calls, use 0 to call on every frame, a negative interval will
 *   be a one-shot notification with the listener removed automatically afterwards.
 */
public static void addServerFrameListener(ServerFrameListener f, float delay, float interval) 
	{
	gFrameMiddle.addServerFrameListener(f, delay, interval);
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
public static void addServerFrameListener(ServerFrameListener f, int phase, float delay, float interval) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.addServerFrameListener(f, delay, interval);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.addServerFrameListener(f, delay, interval);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.addServerFrameListener(f, delay, interval);
	}
/**
 * Handle broadcast print messages.
 * @param flags int
 * @param msg java.lang.String
 */
public static void bprint(int flags, String msg) 
	{
	// send out to interested listeners
	gPrintSupport.fireEvent(PrintEvent.PRINT_ANNOUNCE, flags, null, null, null, msg);
	}
/**
 * Handle debugging print messages.
 * @param msg java.lang.String
 */
public static void dprint(String msg) 
	{
	// send down to console
	Engine.dprint(msg);
	
	// send out to interested listeners
	gPrintSupport.fireEvent(PrintEvent.PRINT_SERVER_CONSOLE, 0, null, null, null, msg);		
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
		return true;
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
		return true; 
		}
	catch (Exception e)
		{
		e.printStackTrace();
		return true;		
		}		

	// try event-delegation
	if (g instanceof ServerCommandListener)
		{
		String[] sa = (String[]) params[0];
		return gServerCommandSupport.fireEvent(sa[1], sa); 
		}
	else
		{
		// reflection didn't find a method and the 
		// gamelet didn't implement ServerCommandListener
		return false;
		}
	}
/**
 * Get the name of the current map - may be null if the game is 
 * just initializing.
 * @return java.lang.String
 */
public static String getCurrentMapName() 
	{
	return gCurrentMapName;
	}
/**
 * Get one of the DOM documents the Game keeps internally.
 * @return org.w3c.dom.Document
 * @param documentKey java.lang.String
 */
public static Document getDocument(String documentKey) 
	{
	return (Document) gDocumentHashtable.get(documentKey);
	}
/**
 * Retrieves a reference to the current GameletManager object
 * leighd 04/07/99
 */
public static GameletManager getGameletManager()
	{
	return gGameletManager;
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
 *
 * @deprecated use getDocument("q2java.level") instead.
 * @return org.w3c.dom.Document
 */
public static Document getLevelDocument() 
	{
	return getDocument("q2java.level");
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
 * Get a reference to the OccupancySupport object.
 * @return q2java.core.event.OccupancySupport
 */
public static OccupancySupport getOccupancySupport()
	{
	return gOccupancySupport;
	}
/**
 * Get the current list of packages to search for classes in.
 */
public static String[] getPackagePaths()
	{
	int n = gPackagePath.size();
	String[] result = new String[n];
	
	for (int i = 0; i < n; i++)
		result[i] = ((PackageEntry) gPackagePath.elementAt(i)).fPackageName;
		
	return result;
	}
/**
 * Called by the DLL to find out what class to use for new Players.
 * @return java.lang.Class
 */
public Class getPlayerClass() throws ClassNotFoundException
	{
	return q2java.NativeEntity.class;
	}
/**
 * leighd 04/10/99
 *
 * Returns a reference to the Gamelet which is currently managing
 * the Player class.
 */
protected static Gamelet getPlayerGamelet()
	{
	return gPlayerGamelet;
	}
/**
 * Get the PrintSupport object the game is using.
 * @return q2java.core.event.PrintSupport
 */
public static PrintSupport getPrintSupport() 
	{
	return gPrintSupport;
	}
/**
 * Get a ResourceGroup object that tracks a specified locale
 * @return q2java.core.ResourceGroup
 * @param localeName name of locale
 */
public static ResourceGroup getResourceGroup(String localeName) 
	{
	return getResourceGroup(GameUtil.getLocale(localeName));
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
	gFrameBeginning = new ServerFrameSupport(64, 16);
	gFrameMiddle = new ServerFrameSupport(512, 128);
	gFrameEnd = new ServerFrameSupport(64, 16);

	// event delegation support
	gGameStatusSupport = new GameStatusSupport();		
	gOccupancySupport = new OccupancySupport();
	gServerCommandSupport = new ServerCommandSupport();

	//leighd 04/10/99 register for commands
	gServerCommands = new BasicServerCommands();
	addServerCommandListener(gServerCommands);
	
	// setup to manage PrintListeners
	gPrintSupport = new PrintSupport();
	Engine.setJavaConsoleListener(this);
	
	// setup to manage BroadcastListeners and cached ResourceBundles
	gResourceGroups = new Vector();
	
	// setup hashtable to let objects find each other in a level
	gLevelRegistry = new Hashtable();

	// setup Vector to keep a list of packages to search for classes in
	gPackagePath = new Vector();
	
	// setup to manage Gamelets
	//leighd 04/07/99
	setGameletManager(new GameletManager());	
	setClassFactory(new DefaultClassFactory());		

	gDocumentHashtable = new Hashtable();
	Document statusDoc = XMLTools.createXMLDocument();
	gDocumentHashtable.put("q2java.status", statusDoc);
	Element root = statusDoc.createElement("status");
	root.setAttribute("game", "Quake2");
	
	CVar ipCVar = new CVar("ip", "localhost", 0);

	String ipaddr = ipCVar.getString();
	
	if (ipaddr.equalsIgnoreCase("localhost"))
		{
		try
			{
			byte[] addr = InetAddress.getLocalHost().getAddress();
			StringBuffer sb = new StringBuffer();
		
			sb.append(((int) addr[0]) & 0x00ff);
			sb.append('.');
			sb.append(((int) addr[1]) & 0x00ff);
			sb.append('.');
			sb.append(((int) addr[2]) & 0x00ff);
			sb.append('.');
			sb.append(((int) addr[3]) & 0x00ff);
		
			ipaddr = sb.toString();
			}
		catch (Exception e)
			{
			}
		}
		
	root.setAttribute("ip", ipaddr);
	CVar portCVar = new CVar("port", "27910", 0);
	root.setAttribute("port", portCVar.getString());	
	statusDoc.appendChild(root);
	
	// Game clocks;
	gFrameCount = 0;
	gGameTime = 0;				

	//leighd 04/10/99 Removed command line gamelet parsing, and 
	//moved to GameletManager. The fGameletManager object will
	//perform this processing when it receives the following
	//game init event.
	
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
 * Relay stuff sent through System.out and System.err.
 * @param s java.lang.String
 */
public void javaConsoleOutput(String s) 
	{
	// send out to interested listeners
	gPrintSupport.fireEvent(PrintEvent.PRINT_JAVA, 0, null, null, null, s);		
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
	// new event-delegation support
	gPrintSupport.fireLocalizedEvent(PrintEvent.PRINT_ANNOUNCE, printLevel, null, null, null, basename, key, args);		
	}
/**
 * Broadcast a localized message to interested objects.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 * @param printLevel One of the Engine.PRINT_* constants.
 */
public static void localecast(String basename, String key, int printLevel) 
	{
	// new event-delegation support
	gPrintSupport.fireLocalizedEvent(PrintEvent.PRINT_ANNOUNCE, printLevel, null, null, null, basename, key, null);
	}
/**
 * Look through the game package path, trying to find a class
 * that matches the classSuffix.  Packages are searched in reverse
 * order of the sequence they were added - the the last package added
 * is the first one looked at.
 * 
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
 * Let interested objects know the status document has been updated -
 * should be called by code that modifies that document.
 */
public static void notifyDocumentUpdated(String documentName) 
	{
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_DOCUMENT_UPDATED, documentName);	
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
 * @deprecated use removeServerFrameListener instead
 */
public static void removeFrameListener(ServerFrameListener f) 
	{
	gFrameMiddle.removeServerFrameListener(f);
	}
/**
 * Remove a FrameListener that's registered to be notified
 * for specific phases of the Server frame.
 *
 * @param f q2jgame.FrameListener
 * @param phase A combination (or'ed or added together) of the FrameManager.FRAME_* constants.
 * @deprecated use removeServerFrameListener instead
 */
public static void removeFrameListener(ServerFrameListener f, int phase) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.removeServerFrameListener(f);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.removeServerFrameListener(f);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.removeServerFrameListener(f);
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
 * notify when number of players change or player changes class...
 */
public static void removeOccupancyListener(OccupancyListener l) 
	{
	gOccupancySupport.removeOccupancyListener(l);
	}
/**
 * Decrement the counter associated with a package name, and
 * if it hits zero, actually remove it from the list.
 *
 * @see q2java.core.Game#addPackagePath
 */
public static void removePackagePath(String pathName)
	{
	if (pathName == null)
		return;

	for (int i = gPackagePath.size() - 1; i >= 0; i--)
		{
		PackageEntry pe = (PackageEntry) gPackagePath.elementAt(i);
		if (pathName.equals(pe.fPackageName))
			{
			pe.fCount--;

			if (pe.fCount < 1)
				{
				gPackagePath.removeElementAt(i);
				
				// let the class factory know things have changed.
				if (gClassFactory != null)
					gClassFactory.setPackagePath(getPackagePaths());
				}
				
			return;
			}	
		}
	}
/**
 * notify server command issued...
 */
public static void removeServerCommandListener(ServerCommandListener l) 
	{
	gServerCommandSupport.removeServerCommandListener(l);
	}
/**
 * Remove a normal FrameListener that's registered to listen
 * for the middle phase of Server Frames. 
 *
 * @param f q2jgame.FrameListener
 */
public static void removeServerFrameListener(ServerFrameListener f) 
	{
	gFrameMiddle.removeServerFrameListener(f);
	}
/**
 * Remove a FrameListener that's registered to be notified
 * for specific phases of the Server frame.
 *
 * @param f q2jgame.FrameListener
 * @param phase A combination (or'ed or added together) of the FrameManager.FRAME_* constants.
 */
public static void removeServerFrameListener(ServerFrameListener f, int phase) 
	{
	if ((phase & FRAME_BEGINNING) != 0)
		gFrameBeginning.removeServerFrameListener(f);
			
	if ((phase & FRAME_MIDDLE) != 0)
		gFrameMiddle.removeServerFrameListener(f);
			
	if ((phase & FRAME_END) != 0)
		gFrameEnd.removeServerFrameListener(f);
	}
/**
 * Reconsider which class should be controlling players.
 */
private static void rethinkPlayerClass() 
	{
	Gamelet g = null;
	Class cls = null;
	
	// look for a gamelet that provides a player class
	// searching from latest loaded to oldest loaded
	Gamelet[] gamelets = gGameletManager.getGamelets();
	for (int i = gamelets.length - 1; i >= 0; i--)
		{
		g = gamelets[i];
		cls = g.getPlayerClass();
		if (cls != null)
			break;
		}

	// check if the current player gamelet is the same as what we found
	if (g == gPlayerGamelet)
		return;
		
	// must be different, do the big switch
	Constructor con = null;
	
	try
		{
		Class[] paramTypes = new Class[1];
		paramTypes[0] = NativeEntity.class;
		con = g.getPlayerClass().getConstructor(paramTypes);	
		}
	catch (Exception e2)
		{
		e2.printStackTrace();
		return;
		}
	
	Object[] params = new Object[1];

	java.util.Enumeration players = NativeEntity.enumeratePlayerEntities();
	while (players.hasMoreElements())
		{
		try
			{
			NativeEntity ent = (NativeEntity) players.nextElement();
			SwitchablePlayer sp = (SwitchablePlayer) ent.getReference();

			sp.dispose();

			params[0] = ent;	
			con.newInstance(params);
			Game.getOccupancySupport().fireEvent(ent, OccupancyEvent.PLAYER_CLASSCHANGE );			
			}
		catch (ClassCastException cce)
		    {
		    // entity probably didn't refer to a SwitchablePlayer
		    }
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}		
		
	gPlayerGamelet = g;
	gPlayerClass = cls;
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
	// make it look like the user typed "sv help"
	if (argc == 1)
		sa[1] = "help";

	// create parameter type array for reflection
	Class[] paramTypes = new Class[1];
	paramTypes[0] = sa.getClass();
	Object[] params = new Object[1];
	params[0] = sa;

	// figure out what command we're processing, and which specific
	// gamelet (if any) should handle it.
	Gamelet g = null;
	String cmd;
	int dot = sa[1].lastIndexOf('.');
	if (dot < 0)
		cmd = sa[1].toLowerCase();
	else
		{
		String alias = sa[1].substring(0, dot);
		g = gGameletManager.getGamelet(alias);		
		cmd = sa[1].substring(dot+1).toLowerCase();
		if (g == null)
			{
			dprint( alias + " is not a loaded gamelet.\n" );
			return;
			}
		}

	// run the command	
	if (g != null)
		{
		if (externalServerCommand(g, cmd, paramTypes, params))
			return;
		// else fall through so it prints "unrecognized command below
		}
	else
		{
		// no particular gamelet was specified, look in
		// various places for something to handle the naked command

		// try event-delegation
	    if (gServerCommandSupport.fireEvent(sa[1], sa))
			return;
		
		// look for a method inside this class
		String methodName = null;
		try
			{
			methodName = "svcmd_" + sa[1].toLowerCase();
			java.lang.reflect.Method meth = getClass().getMethod(methodName, paramTypes);						
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
			
		// use reflection to look for a module command second
		Gamelet[] gamelets = gGameletManager.getGamelets();
		for (int i = 0; i < gamelets.length; i++)
			{
			Gamelet g2 = gamelets[i];
			if (externalServerCommand(g2, cmd, paramTypes, params))
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
	
	//leighd 04/11/99 - give the gamelet manager a reference to the 
	//new class factory
	gGameletManager.setClassFactory(gClassFactory);
	}
public static void setGameletManager(GameletManager gm)
	{
	//leighd 04/07/99
	gGameletManager = gm;
	}
/**
 * Called by the DLL when the DLL's Shutdown() function is called.
 */
public void shutdown()
	{
	Game.dprint("Game shutdown\n");
	// let interested objects know the last map is done
	if (gLevelStarted)
		gGameStatusSupport.fireEvent(GameStatusEvent.GAME_ENDLEVEL);

	// tell everyone we're on our way out
	// leighd 04/11/99 - GameletManager will catch this event and 
	// remove all gamelets.
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_SHUTDOWN);

	//remove from server command notification
	removeServerCommandListener(gServerCommands);
	
	Engine.debugLog("Game.shutdown()");
	}
/**
 * Read the current level DOM document and spawn the map's entities.
 */
private static void spawnEntities() 
	{
	Object[] ctorParams = new Object[1];
	Class[] newCtorParamTypes = new Class[1];
	newCtorParamTypes[0] = Element.class;
	
	// look for <entity>..</entity> sections
	NodeList nl = getDocument("q2java.level").getElementsByTagName("entity");
	for (int i = 0; i < nl.getLength(); i++)
		{
		String className = null;

		try
			{
			Element e = (Element) nl.item(i);
			className = e.getAttribute("class");
			ctorParams[0] = e;
			
			//leighd 04/10/99 - altered lookup method to reference class factory
			//directly rather than calling Game.lookup class.
			Class entClass = gClassFactory.lookupClass(".spawn." + className.toLowerCase());
			Constructor ctor = entClass.getConstructor(newCtorParamTypes);

			try
				{
				ctor.newInstance(ctorParams);
				}
			catch (InvocationTargetException ite)
				{
				throw ite.getTargetException();
				}
			
			}
		catch (ClassNotFoundException cnfe)
			{
			Engine.debugLog("Couldn't find class to handle: " + className);
			}
		catch (NoSuchMethodException nsme)
			{
			Engine.debugLog("Class " + className + " doesn't have the right kind of constructor");
			}
		catch (InhibitedException ie)
			{
			}
		catch (Throwable t)
			{
			t.printStackTrace();
			}
		}	
	}
/**
 * Put the game into the GAME_INTERMISSION state.
 */
public static void startIntermission() 
	{
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_INTERMISSION);	
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

	// if there -was- a level running, clean up.
	if (gLevelStarted)
		{
		// let interested parties know the level is over with
		gGameStatusSupport.fireEvent(GameStatusEvent.GAME_ENDLEVEL);

		// purge non-CrossLevel objects from FrameListener lists
		gFrameBeginning.purge();
		gFrameMiddle.purge();
		gFrameEnd.purge();
	
		// clear the object registry
		gLevelRegistry.clear();
		
		gLevelStarted = false;
		}	

	// make a note of what map we're going to play and update the server's name
	gCurrentMapName = mapname;
	Element statusRoot = getDocument("q2java.status").getDocumentElement();
	statusRoot.setAttribute("map", mapname);
	statusRoot.setAttribute("name", (new CVar("hostname", "noname", 0)).getString());
	notifyDocumentUpdated("q2java.status");
	
	// create the inital, mostly empty level document (don't pass the entString)
	Document levelDoc = GameUtil.buildLevelDocument(mapname, null, spawnPoint);
	gDocumentHashtable.put("q2java.level", levelDoc);
		
	// let interested objects know we're building a new level
	// document, and they may add on to it.
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_BUILD_LEVEL_DOCUMENT, entString, spawnPoint);	

	// parse the entString and place its contents in the document
	// if no spawn entities have been placed in the document yet, 
	// or if the <include-default-entities/> tag appears
	Element root = levelDoc.getDocumentElement(); 
	NodeList nl = root.getElementsByTagName("entity");
	NodeList nl2 = root.getElementsByTagName("include-default-entities");
	if ((nl.getLength() == 0) || (nl2.getLength() > 0))
		GameUtil.parseEntString(root, entString);
	
	// let interested objects know we're starting a new map, but nothing's
	// been spawned yet - so that can inspect or tweak the level document.
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_PRESPAWN);	

	// ponder whether or not we need to change player classes
	rethinkPlayerClass();
	
	// suggest a gc, to clean things up from the last level, before
	// spawning all the new entities
	System.gc();		
	
	// read through the document and actually create the entities
	spawnEntities();

	// let interested objects know that map entities have been spawned.
	gGameStatusSupport.fireEvent(GameStatusEvent.GAME_POSTSPAWN);	
						
	// now, right before the game starts, is a good time to 
	// suggest that Java to do another garbage collection to tidy things up.
	System.gc();	

	gPerformanceFrames = 0;
	gCPUTime = 0;
	gLevelStarted = true;
	}
/**
 * Print timing info to the console. and reset counters.  Kept inside
 * the Game class since it needs to access the counters, and moving
 * it out would have required either making the counters non-private or
 * writing accessor methods.
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