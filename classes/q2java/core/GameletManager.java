package q2java.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.event.*;

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
	protected Vector fGameletList;
	protected GameClassFactory fClassFactory;
	
	// for persistant list of loaded modules (useful across reconnects)
	// also checked for gamelets on command line. 
	protected CVar fModules; 

	//used to generate servlet events
	private GameletSupport fGameletSupport;

	// inner class used to GameletManager to track what's going on
	// with the individual gamelets
	private static class GameletItem
		{
		String fName;
		boolean fLevelChangeRequired;
		boolean fIsUnloading;
		Gamelet fGamelet;
		}

	// handy variable can be used over and over
	private final static Class[] gConstructorParamTypes = {Document.class};

	private final static String OPTION_MULTIPLE = "avoid multiple instances";
	private final static String OPTION_UNLOAD = "wait for level change to unload";
	
/**
 * Constructor. The new GameletManager object should be provided with 
 * a reference to the Games GameletSupport object so that it can 
 * generate gamelet events.
 */
public GameletManager()
	{
	fGameletList = new Vector();
	
	fGameletSupport = new GameletSupport();
	
	//setup to listen for server commands
	Game.addServerCommandListener(this);
	
	//setup to listen for level changes, so Gamelets can be 
	//initialised and unloaded automatically.
	Game.addGameStatusListener(this);    
	}
/**
 * Load a Gamelet into the system.
 *
 * @return q2java.core.Gamelet
 * @param classname Classname of the Gamelet we want to load
 * @param suggestedGameletName Suggested name of gamelet, may be altered if the suggested name is already in use - may be null.
 * @param params Element Extra info you may want to pass to the gamelet's constructor.
 * @param unloadAtEndLevel true if the gamelet should automatically go away when the level ends.
 */
public Gamelet addGamelet(String classname, String suggestedGameletName, Element params, boolean unloadAtEndlevel) throws Throwable
	{
	// see if there is actually a class with the specified name
	Class gameletClass = fClassFactory.lookupClass(classname);

	// make sure it's a subclass of q2java.core.Gamelet	
	if (!(Gamelet.class.isAssignableFrom(gameletClass)))
		throw new ClassNotFoundException(classname + " is not a subclass of " + Gamelet.class.getName());

	// make sure it has a compatible constructor
	try
		{
		gameletClass.getConstructor(gConstructorParamTypes);
		}
	catch (Exception ex)
		{
		throw new ClassNotFoundException(classname + " doesn't have a compatible constructor\n");
		}
		
	if ((suggestedGameletName == null) || suggestedGameletName.equals(""))
		suggestedGameletName = makeGameletName(classname);
	else if (getGamelet(suggestedGameletName) != null)
		suggestedGameletName = makeGameletName(suggestedGameletName);
	

	Document gameletDoc = null;

	// try to read external Gamelet description file
	try
		{
		InputStream is = gameletClass.getResourceAsStream("/" + classname.replace('.', '/') + ".gamelet");
		InputStreamReader isr = new InputStreamReader(is);
		gameletDoc = XMLTools.readXMLDocument(isr, null);
		isr.close();
		}
	catch (Throwable t)
		{
		}

	// if no gamelet description found, generate an
	// empty stub
	if (gameletDoc == null)
		{
		gameletDoc = XMLTools.createXMLDocument("gamelet");
		}

	// get a hold of the doc root and make sure its attributes
	// are valid
	Element root = gameletDoc.getDocumentElement();
	root.setAttribute("class", classname);
	root.setAttribute("name", suggestedGameletName);
	
	// copy any params into the gamelet doc
	if (params != null)
		XMLTools.copy(params, root, true);

	// let interested parties know what we're planning to load
	fGameletSupport.fireEvent( GameletEvent.GAMELET_LOADING, null, gameletDoc);

	// start with the creation of the gamelet
	GameletItem gi = new GameletItem();
	gi.fName = suggestedGameletName;

	// look for options
	for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling())
		{
		if (n.getNodeType() != Node.ELEMENT_NODE)
			continue;

		Element e = (Element) n;
		if (!e.getTagName().equals("option"))
			continue;

		String option = e.getAttribute("name");

		if (OPTION_UNLOAD.equalsIgnoreCase(option))
			gi.fLevelChangeRequired = true;

		if (OPTION_MULTIPLE.equalsIgnoreCase(option)
		&& (getGamelet(gameletClass) != null))
			throw new GameException("Avoided loading multiple instances of " + classname);
		}
		
	gi.fGamelet = loadGamelet(gameletClass, gameletDoc);
	gi.fIsUnloading = unloadAtEndlevel;
	fGameletList.addElement(gi);			
	fGameletSupport.fireEvent( GameletEvent.GAMELET_ADDED, gi.fGamelet, null);
	Game.dprint(gi.fName + " loaded\n");
	return gi.fGamelet;
	}
/**
 * Load a Gamelet based on info found within a DOM Element.
 *
 * @return q2java.core.Gamelet
 * @param e an Element with the attributes "class" and possibly "name".
 */
public Gamelet addGamelet(Element description, boolean unloadAtEndlevel) throws Throwable
	{
	String classname = description.getAttribute("class");
	String gameletName = description.getAttribute("name");

	return addGamelet(classname, gameletName, description, unloadAtEndlevel);
	}
/**
 * Other packages or mods may wish to be notified when a package is added to 
 * the game. This event based interface introduces a 'PackageListener'.
 * @param pl The PackageListener to add
 */
public void addGameletListener(GameletListener l) 
	{
	fGameletSupport.addGameletListener(l);
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
		addGamelet(args[2], alias, null, false);
		}
	catch (ClassNotFoundException cnfe)
		{
		Game.dprint("Error: " + args[2] + " not found\n");	
		}
	catch (Throwable t)
		{
		t.printStackTrace();
		}
	}
/**
 * Called when sv listgamelet is typed at the console
 */
public void commandGamelets(String[] args)
	{
	boolean foundUnload = false;

	Gamelet playerGamelet = Game.getPlayerGamelet();
	StringBuffer output = Q2Recycler.getStringBuffer();

	Game.dprint("Currently Loaded Gamelets\n");
	Game.dprint("\n");
	Game.dprint("  Status  Player  Name (class)\n");
	Game.dprint("  ======  ======  ============\n");

	Enumeration enum = fGameletList.elements();
	while (enum.hasMoreElements()) 
		{
		GameletItem gi = (GameletItem) enum.nextElement();
		output.setLength(0);
		if (gi.fIsUnloading)
			{
			output.append("  Unload  ");
			foundUnload = true;            
			}
		else 
			{
			output.append("  Active  ");
			}
			
		if (gi.fGamelet == playerGamelet)
			{
			output.append(" Yes    ");
			}
		else
			{
			output.append("        ");
			}
		output.append(getGameletName(gi.fGamelet) + " (" + gi.fGamelet.getClass().getName() + ")\n");
		Game.dprint(output.toString());		
		}

	//be nice and recycle our used string
	Q2Recycler.put(output);
	
	Game.dprint("\n");

	// explain the (Active)/(Unload) notes if necessary
	Game.dprint("Active   = gamelet currently running\n");
	if (foundUnload)
		Game.dprint("Unload   = gamelet that will be removed on next level change\n");
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
			// check the command line for modules
			loadStartupGamelets();
			break;
			}
		case GameStatusEvent.GAME_ENDLEVEL:
			{
			// remove gamelets waiting for a level change
			unloadGamelets();
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
 * Look for a gamelet based on the class.
 * @return q2java.core.Gamelet, null if not found.
 * @param gameletClass class we're looking for.
 */
public Gamelet getGamelet(Class gameletClass) 
	{
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (gi.fGamelet.getClass().equals(gameletClass))
			return gi.fGamelet;
		}
		
	return null;
	}
/**
 * Lookup a loaded package, based on its name (case-insensitive) or
 * classname.
 *
 * @return the gamelet with the specified name, or the first instance
 *   if a classname was specified, null if not found.
 * @param alias A gamelet name or classname.
 */
public Gamelet getGamelet(String alias) 
	{
	if (alias == null)
		return null;
		
	boolean isClassname = alias.indexOf('.') > 0;
	
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (isClassname)
			{
			if (gi.fGamelet.getClass().getName().equals(alias))
				return gi.fGamelet;
			}
		else
			{
			if (gi.fName.equalsIgnoreCase(alias))
				return gi.fGamelet;
			}
		}
		
	return null;
	}
/**
 * Returns the number of loaded gamelets.
 */
public int getGameletCount() 
	{
	return fGameletList.size();
	}
/**
 * Lookup the name of a gamelet.
 * @return java.lang.String
 * @param g q2java.core.Gamelet
 */
public String getGameletName(Gamelet g) 
	{
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (gi.fGamelet.equals(g))
			return gi.fName;		
		}
		
	return null;
	}
/**
 * Get an array of references to all loaded Gamelets.
 */
public Gamelet[] getGamelets() 
	{
	int n = fGameletList.size();
	Gamelet[] result = new Gamelet[n];
	for (int i = 0; i < n; i++)
		result[i] = ((GameletItem)fGameletList.elementAt(i)).fGamelet;
		
	return result;
	}
/**
 * Is a particular gamelet marked to unload when the level's over?
 * @return true if the gamelet will unload
 * @param g q2java.core.Gamelet
 */
public boolean isUnloadingAtLevelChange(Gamelet g) 
	{
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (gi.fGamelet.equals(g))
			return gi.fIsUnloading;		
		}
		
	return false;
	}
/**
 * Actually create a gamelet.
 * @return q2java.core.Gamelet
 * @param doc org.w3c.dom.Document
 */
protected Gamelet loadGamelet(Class gameletClass, Document doc) throws Throwable 
	{
	try
		{
		Constructor ctor = gameletClass.getConstructor(gConstructorParamTypes);
	
		Object[] params = new Object[1];
		params[0] = doc;
	
		return (Gamelet) ctor.newInstance(params);
		}
	catch (InvocationTargetException ite)
		{
		throw ite.getTargetException();
		}
	}
/**
 * This method is called when the GameletManager receives the
 * GAME_INIT method, which is called when the q2java game is
 * originally setup.
 */
protected void loadStartupGamelets()
	{
	// look for gamelets specified on the command line. Cvar will
	// also contain cached gamelet names if the DLL has been reloaded.
	fModules = new CVar("gamelets", "", 0);
	String modules = fModules.getString();
	String startup = (new CVar("q2java_startup", "q2java.startup", CVar.CVAR_SERVERINFO)).getString();
	File startupFile = new File(Engine.getGamePath(), startup);
	
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
					addGamelet( temp, null, null, false );
				else 
					addGamelet( temp.substring( 0, tidx ), temp.substring( tidx + 1, temp.length() - 1 ), null, false );
				}
			catch (ClassNotFoundException cnfe)
				{
				Game.dprint("Error: " + temp + " not found\n");
				}
			catch (Throwable t)
				{
				t.printStackTrace();
				}
			} // end for loop.
		}
	else if (startupFile.canRead())
		{
		try
			{
			FileReader fr = new FileReader(startupFile);
			Document startupDoc = XMLTools.readXMLDocument(fr, null);
			for (Node n = startupDoc.getDocumentElement().getFirstChild(); n != null; n = n.getNextSibling())
				{
				if (n.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element e = (Element) n;
				String tagName = e.getTagName();

				if (tagName.equals("cvar"))
					{
					String name = e.getAttribute("name");
					if (name == null)
						continue;

					String value = e.getAttribute("value");
					if (value != null)
						{
						CVar c = new CVar(name, value, 0);
						c.setValue(value);						
						}

					continue;
					}
				
				if (tagName.equals("gamelet"))
					{
					try
						{
						addGamelet(e, false);
						}
					catch (Throwable t)
						{
						t.printStackTrace();						
						}
					continue;
					}
				}
			
			fr.close();
			}
		catch (IOException ioe)
			{
			ioe.printStackTrace();
			}
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
				addGamelet(name, alias, null, false);
				}
			catch (ClassNotFoundException cnfe)
				{
				Game.dprint("Error: " + name + " not found\n");
				}
			catch (Throwable t)
				{
				t.printStackTrace();
				}
			i++;
			}

		// add default module if nothing else was loaded
		// makes sure we've got a basic game to play
		if (i == 1)
			{
			try
				{                
				addGamelet("q2java.baseq2.Deathmatch", null, null, false);
				}
			catch (ClassNotFoundException cnfe)
				{
				Game.dprint("Error: the default Gamelet q2java.baseq2.Deathmatch not found\n");
				}
			catch (Throwable t)
				{
				t.printStackTrace();
				}
			}
		}
	}
/**
 * Come up with a suitable name for the gamelet based on
 * its classname or originally specified name.
 *
 * @return java.lang.String
 * @param clasname java.lang.String
 */
protected String makeGameletName(String name) 
	{
	// strip packagename from classname
	int p = name.lastIndexOf('.');
	if (p >= 0)
		name = name.substring(p+1);

	// see if what we have so far is unused
	if (getGamelet(name) == null)
		return name;

	// must already be a gamelet with that name, try adding
	// numbers
	for (int i = 2; i < 100; i++)
		{
		String temp = name + i;
		if (getGamelet(temp) == null)
			return temp;
		}

	// something is seriously wrong..
	throw new IndexOutOfBoundsException("Can't generate gamelet name");
	}
/**
 * Remove a module from the game's package list
 * @param gm A loaded Gamelet
 */
public void removeGamelet(Gamelet g) 
	{
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (gi.fGamelet == g)
			{
			if (gi.fLevelChangeRequired)
				{
				gi.fIsUnloading = true;
				Game.dprint(gi.fName + " will unload after level change\n");
				}
			else
				{
				unload(gi.fGamelet);
				fGameletList.removeElementAt(i);
				Game.dprint(gi.fName + " unloaded\n");
				}
			return;
			}
		}		
	}
/**
 * Removes a package listener
 * @param pl the PackageListener to remove
 */
public void removeGameletListener(GameletListener l)
	{
	fGameletSupport.removeGameletListener(l);
	}
/**
 * Save the list of loaded gamelets in a CVar.
 */
protected void saveGameletList() 
	{
	StringBuffer sb = Q2Recycler.getStringBuffer();
	
	Enumeration enum= fGameletList.elements();
	while (enum.hasMoreElements())
		{
		GameletItem gi = (GameletItem) enum.nextElement();

		// insert the gamelet info in reverse order, so the
		// first gamelet loaded appears first when all is done.
		if (sb.length() > 0)
			sb.append('+');
		sb.append(gi.fGamelet.getClass().getName());
		sb.append('[');
		sb.append(gi.fName);
		sb.append(']');
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
		return;
		}
		
	if (command.equals("removegamelet"))
		{
		commandRemoveGamelet(e.getArgs());
		e.consume();
		return;
		}
		
	if (command.equals("gamelets"))
		{
		commandGamelets(e.getArgs());
		e.consume();
		return;
		}    
	}
/**
 * @see Game#setClassFactory(GameClassFactory)
 */
public void setClassFactory(GameClassFactory gcf)
	{
	//Withnails 04/22/98
	fClassFactory = gcf;
	}
/**
 * Called when the game is shutting down, the GameletManager
 * will clean things up here.
 */
protected void shutdown()
	{
	Engine.debugLog("GameletManager shutdown\n");
	
	 //remove all the gamelets    
	Enumeration enum = fGameletList.elements();
	while (enum.hasMoreElements())
		{
		GameletItem gi = (GameletItem) enum.nextElement();		
		unload(gi.fGamelet);
		}
		
	fGameletList.removeAllElements();

	//empty the modules cvar, in case a reconnect takes place.
	fModules.setValue("");
	
	//clean up after ourselves
	Game.removeGameStatusListener(this);
	Game.removeServerCommandListener(this);

	//be nice to gc
	fGameletList = null;
	fGameletSupport = null;
	fClassFactory = null;
	}
/**
 * Call a gamelet's unload method and fire off a notification event.
 * @param g q2java.core.Gamelet
 */
protected void unload(Gamelet g) 
	{
	try 
		{
		fGameletSupport.fireEvent( GameletEvent.GAMELET_UNLOADING, g, null);
		g.unload();
		}
	catch (Exception e)
		{
		}	
	}
/**
 * Called when the GameletManager receives the GAME_ENDLEVEL event.
 * Any gamelets awaiting removal based on a 
 * level change will be processed here.
 */
protected void unloadGamelets()
	{
	Engine.debugLog("GameletManager unloading gamelets\n");

	// look for gamelets that were waiting for a level change
	// do in reverse order so we don't miss anything
	for (int i = fGameletList.size() - 1; i >= 0; i--)
		{
		GameletItem gi = (GameletItem) fGameletList.elementAt(i);
		if (gi.fIsUnloading)
			{
			unload(gi.fGamelet);
			fGameletList.removeElementAt(i);
			}
		}
	}
}