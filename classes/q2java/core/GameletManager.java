package q2java.core;

import q2java.*;
import q2java.core.event.*;
import java.util.*;

/**
 * This class implements the functionality required to load, remove 
 * and otherwise manager gamelets within Q2Java. This code has been 
 * partially split off from the original in the Game class, but 
 * has been enhanced to take advantage of the new event model
 *
 *
 * @author Leigh Dodds
 * @version 0.2
 */
public class GameletManager implements ServerCommandListener, GameStatusListener
	{

	//loaded mod list
	protected Vector fModList;
	protected GameClassFactory fClassFactory;
	
	// for persistant list of loaded modules (useful across reconnects)
	// also checked for gamelets on command line. 
	protected CVar fModules; 

	//used when adding gamelets
	protected Vector  fNewGameletList;
	protected boolean fNewGameletsShouldInit;

	//used to generate servlet events
	private GameletSupport fGameletSupport;

	
/**
 * Constructor. The new GameletManager object should be provided with 
 * a reference to the Games GameletSupport object so that it can 
 * generate gamelet events.
 */
public GameletManager(GameletSupport gs)
	{
	fModList = new Vector();
	fGameletSupport = gs;
	
	//setup to listen for server commands
	Game.addServerCommandListener(this);
	
	//setup to listen for level changes, so Gamelets can be 
	//initialised and unloaded automatically.
	Game.addGameStatusListener(this);    
	}
/**
 * Add a new module to the game.  
 * If a module has already been added, nothing happens.
 * @param packageName java.lang.String
 * @param alias short name that the gamelet will be referred to as.
 */
public Gamelet addGamelet(String className, String alias) throws ClassNotFoundException
	{
	//first check that this alias isn't used.
	Gamelet g = getGamelet(alias);
	if (g != null) 
		{
		Game.dprint("There is already a [" + alias + "] loaded\n");
		return null;
		}

	fNewGameletList = new Vector();
	//by default all new gamelets should initialise themselves.
	fNewGameletsShouldInit = true;

	// actually add the gamelet and any others it depends on
	g = addGamelet0(className, alias, null);
	
	if (fNewGameletsShouldInit)
		{
		Enumeration enum = fNewGameletList.elements();
		while (enum.hasMoreElements())
			{
			Gamelet g2 = (Gamelet) enum.nextElement();
			g2.markInitialized();
			g2.init();
			}
		}

	//at this point we've either initialised all the 
	//gamelets, or they are waiting for a level change,
	//and so will be processed from the fModList.
	fNewGameletList.removeAllElements();
	
	//save gamelet list to Cvar.
	saveGameletList();

	return g;
	}
/**
 * Main code to add gamelets.
 * @param className java.lang.String
 * @param alias java.lang.String
 */
private Gamelet addGamelet0(String className, String alias, Gamelet higherGamelet) throws ClassNotFoundException
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
		
	Gamelet g = fClassFactory.loadGamelet(className, alias);
	
	if (g != null)
		{
		// the class factory successfully loaded the Gamelet        
		fGameletSupport.fireEvent( GameletEvent.GAMELET_ADDED, g);            

		int position = 0;
		//add gamelet to list
		if (higherGamelet != null)
			position = fModList.indexOf(higherGamelet) + 1;
		fModList.insertElementAt(g, position);

		//add gamelet to new gamelet list, used to ensure that
		//all dependent gamelets are initialised together
		fNewGameletList.insertElementAt(g, 0);
		fNewGameletsShouldInit &= !(g.isLevelChangeRequired());
		
		// make sure dependent gamelets are loaded
		String[] dependencies = g.getGameletDependencies();
		if (dependencies != null)
			{
			for (int i = 0; i < dependencies.length; i++)
				{
				try
					{
					//check to see if we've already got a gamelet of that
					//type running.
					Class depClass = Class.forName(dependencies[i]);
					Gamelet other = getGamelet(depClass);                    
				
					if (other == null)
						// not loaded? then recursively call to add -that- gamelet and -its- dependencies
						addGamelet0(dependencies[i], null, g);
					else
						// already loaded, think about whether we should initialize or not
						fNewGameletsShouldInit &= (other.isInitialized() || (!other.isLevelChangeRequired()));
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
 * Called when sv addgamelet is typed at the console. Allows the 
 * user to add a gamelet to the game. 
 *
 * Usage: sv addgamelet <class-name> [<alias>]
 */
public void commandAddGamelet(String[] args)
	{
	if (args.length < 3)
		{
		Game.dprint("Usage: sv addgamelet <class-name> [<alias>]\n");
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
		Game.dprint("Error: " + args[2] + " not found\n");
		}    
	}
/**
 * Called when sv listgamelet is typed at the console
 */
public void commandGamelets(String[] args)
	{
	boolean foundLoad = false;
	boolean foundUnload = false;

	Gamelet playerGamelet = Game.getPlayerGamelet();
	StringBuffer output = Q2Recycler.getStringBuffer();

	Game.dprint("Currently Loaded Gamelets\n");
	Game.dprint("\n");
	Game.dprint("      Status   Player   Alias\n");
	Game.dprint("      ======   ======   =====\n");

	Enumeration enum = fModList.elements();
	while (enum.hasMoreElements()) 
		{
		Gamelet g = (Gamelet) enum.nextElement();
		output.setLength(0);
		if (g.isUnloading())
			{
			output.append("      Unload   ");
			foundUnload = true;            
			}
		else if (g.isInitialized())
			{
			output.append("      Active   ");
			}
		else
			{
			output.append("      Load     ");
			foundLoad = true;
			}
		if (g == playerGamelet)
			{
			output.append(" Yes     ");
			}
		else
			{
			output.append("         ");
			}
		output.append(g.getGameletName() + "\n");
		Game.dprint(output.toString());
		
		}

	//be nice and recycle our used string
	Q2Recycler.put(output);
	
	Game.dprint("\n");

	// explain the (load)/(unload) notes if necessary
	Game.dprint("active   = gamelet currently running\n");
	if (foundLoad)
		Game.dprint("load     = uninitialized gamelet waiting for level change\n");
	if (foundUnload)
		Game.dprint("unload   = gamelet that will be removed on next level change\n");
	}
/**
 * Called when sv removegamelet is typed at the console.
 * Allows the user to remove a gamelet from a running game.
 *
 * Usage: sv removegamelet <alias>
 */
public void commandRemoveGamelet(String[] args)
	{
	if (args.length < 3)
		Game.dprint("Usage: sv removegamelet <alias>\n");
	else
		{
		Gamelet g = getGamelet(args[2]);
		if (g == null)
			Game.dprint("No gamelet called " + args[2] + " loaded\n");
		else
			removeGamelet(g);
		}    
	}
/**
 * Catches game events
 */
public void gameStatusChanged(GameStatusEvent e)
	{
	int type = e.getState();
	switch (type)
		{
		case GameStatusEvent.GAME_INIT:
			{
			//check the command line for modules
			loadCommandLineGamelets();
			break;
			}
		case GameStatusEvent.GAME_ENDLEVEL:
			{
			//remove gamelets, init others
			processGamelets();
			break;
			}
		case GameStatusEvent.GAME_SHUTDOWN:
			{
			shutdown();
			break;
			}
		}
	}
/**
 * complement to setClassFactory
 */
public GameClassFactory getClassFactory() 
	{
	return fClassFactory;
	}
/**
 * Append a gamelet and all the other gamelets it depends on to a vector.
 * @param g Gamelet we're looking at.
 * @param v Vector to append the gamelet and its dependencies to.
 */
private void getDependantGamelets(Gamelet g, Vector v) 
	{
	// bail if this gamelet is already on the list
	if (v.indexOf(g) >= 0)
		return;

	v.addElement(g);
	String clsName = g.getClass().getName();

	// look through all gamelets for ones that depend on this one
	Enumeration enum = getGamelets();
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
 * Look for a gamelet based on the class.
 * @return q2java.core.Gamelet, null if not found.
 * @param gameletClass class we're looking for.
 */
public Gamelet getGamelet(Class gameletClass) 
	{
	for (int i = 0; i < fModList.size(); i++)
		{
		Object obj = fModList.elementAt(i);
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
	for (int i = 0; i < fModList.size(); i++)
		{
		Gamelet g = (Gamelet) fModList.elementAt(i);
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
	return fModList.size();
	}
/**
 * Get an Enumeration of all loaded packages. The enumeration will be
 * of LoadedPackage objects
 */
public Enumeration getGamelets() 
	{
	return fModList.elements();
	}
/**
 * This method is called when the GameletManager receives the
 * GAME_INIT method, which is called when the q2java game is
 * originally setup.
 */
protected void loadCommandLineGamelets()
	{
	// look for gamelets specified on the command line. Cvar will
	// also contain cached gamelet names if the DLL has been reloaded.
	fModules = new CVar("gamelets", "", 0);
	String modules = fModules.getString();

	if (!modules.equals(""))
		{       
		//Withnails 04/23/98 - parses out multiple modules on command line, separated by ;,/\\+
		StringTokenizer st = new StringTokenizer(modules, ";,/\\+");
		Vector v = new Vector();
		while (st.hasMoreTokens())
			v.addElement(st.nextToken());

		//now add each gamelet, using the specified alias if available.
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
				Game.dprint("Error: " + temp + " not found\n");
				}
			} // end for loop.
		}
	else
		{
		// read module names from System properties, as there were none
		// on the command line, or already stored in the Cvar.
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
				Game.dprint("Error: " + name + " not found\n");
				}
				
			i++;
			}

		// add default module if nothing else was loaded
		// makes sure we've got a basic game to play
		if (i == 1)
			{
			try
				{                
				addGamelet("q2java.baseq2.BaseQ2", "baseq2");
				}
			catch (ClassNotFoundException cnfe)
				{
				Game.dprint("Error: the default Gamelet q2java.baseq2.BaseQ2 not found\n");
				}
			}
		}
	}
/**
 * Called when the GameletManager receives the GAME_ENDLEVEL event.
 * Any gamelets awaiting initialisation, or removal based on a 
 * level change will be processed here.
 */
protected void processGamelets()
	{
	Engine.debugLog("GameletManager processing gamelets\n");
	
	// look for gamelets that were waiting for a level change
	Vector initList = new Vector();    
	Enumeration enum = fModList.elements();
	while (enum.hasMoreElements())
		{
		Gamelet g = (Gamelet) enum.nextElement();
		if (g.isUnloading())
			{
			removeGamelet0(g);
			}
		else if (!g.isInitialized())
			{
			initList.insertElementAt(g, 0); // build list in reverse order
			}
		}

	// initialize gamelets we found uninitialized
	enum = initList.elements();
	while (enum.hasMoreElements())
		{
		Gamelet g = (Gamelet) enum.nextElement();
		g.markInitialized();
		g.init();    
		}

	//we've processed all the new gamelets, so clear the 
	//new gamelet list.
	fNewGameletList.removeAllElements();    
	}
/**
 * Remove a module from the game's package list
 * @param gm A loaded Gamelet
 */
public void removeGamelet(Gamelet g) 
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
		enum = getGamelets();
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
private void removeGamelet0(Gamelet g) 
	{
	fGameletSupport.fireEvent( GameletEvent.GAMELET_REMOVED, g);

	int i;
	for (i = 0; i < fModList.size(); i++)
		{
		Gamelet g2 = (Gamelet) fModList.elementAt(i);
		if (g2 == g)
			{
			fModList.removeElementAt(i);
//			fClassFactory.clearClassHash();            
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
	
	saveGameletList();    
	}
/**
 * Save the list of loaded gamelets in a CVar.
 */
protected void saveGameletList() 
	{
	StringBuffer sb = new StringBuffer();
	Enumeration enum = getGamelets();
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
		
	fModules.setValue(sb.toString());        
	}
public void serverCommandIssued(ServerCommandEvent e)
	{
	//check for commands that the GameletManager can deal with
	String command = e.getCommand();
	if (command.equals("addgamelet"))
		{
		commandAddGamelet(e.getArgs());
		e.consume();
		}
	if (command.equals("removegamelet"))
		{
		commandRemoveGamelet(e.getArgs());
		e.consume();
		}
	if (command.equals("gamelets"))
		{
		commandGamelets(e.getArgs());
		e.consume();
		}    
	}
/**
 * @see Game.setClassFactory
 */
public void setClassFactory(GameClassFactory gcf)
	{
	//Withnails 04/22/98
	fClassFactory = gcf;
	}
/**
 * 
 */
protected void shutdown()
	{
	Engine.debugLog("GameletManager shutdown\n");    
	 //remove all the gamelets    
	Enumeration enum = fModList.elements();
	while (enum.hasMoreElements())
		{
		Gamelet g = (Gamelet)enum.nextElement();
		//just notify those that have been initialized, don't 
		//bother throwing a GAMELET_REMOVED event, as we don't
		//want anything interferring with a graceful shutdown
		if (g.isInitialized())
			{
			try 
				{
				//gGameletSupport.fireEvent( GameletEvent.GAMELET_REMOVED, g);
				g.unload();
				}
			catch (Exception e)
				{
				}
			}
		}
	fModList.removeAllElements();
	fNewGameletList.removeAllElements();

	//empty the modules cvar, in case a reconnect takes place.
	fModules.setValue("");
	
	//clean up after ourselves
	Game.removeGameStatusListener(this);
	Game.removeServerCommandListener(this);

	//be nice to gc
	fModList = null;
	fGameletSupport = null;
	fClassFactory = null;
	}
}