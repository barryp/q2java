package q2java.baseq2;

import java.util.Enumeration;
import java.util.StringTokenizer;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Gamelet responsible for changing levels.  Basically watches the
 * timelimit and fraglimit, and decides what map comes next.
 *
 * @author Barry Pederson 
 */

public class LevelChanger extends Gamelet
  implements ServerFrameListener, GameStatusListener, CrossLevel
	{	
	// CVars only this Gamelet itself needs to worry about
	private CVar fFragLimitCVar;
	private CVar fTimeLimitCVar;
	private CVar fMapListCVar;
	
	// Mirrored CVar values
	protected int   fFragLimit;
	protected float fTimeLimit;	
	
	// track level changes	
	protected float fLevelStartTime;
	protected boolean fInIntermission;
	protected double fIntermissionEndTime;
	protected boolean fChangeMapNow;
	protected String fNextMap;
	
/**
 * Create the Gamelet.
 * @param gameletName java.lang.String
 */
public LevelChanger(Document gameletDoc) throws Throwable
	{
	super(gameletDoc);

	// get called back every 10 seconds to update the timelimit and fraglimit cvars
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 10.0F);

	// get called back every second to actually check the timelimit and fraglimit
	Game.addServerFrameListener(this, Game.FRAME_MIDDLE, 0, 1.0F);

	// get called in case the map changes so we can reset our counters
	Game.addGameStatusListener(this);
	
	fFragLimitCVar = new CVar("fraglimit", "0", CVar.CVAR_SERVERINFO);
	fTimeLimitCVar = new CVar("timelimit", "0", CVar.CVAR_SERVERINFO);
	fMapListCVar = new CVar("sv_maplist", "", 0);	
	}
/**
 * Called when the game status changes.
 */
public void gameStatusChanged(GameStatusEvent e)
	{
	// when a new level starts, reset everything
	if( e.getState() == GameStatusEvent.GAME_PRESPAWN )
	    {
		fLevelStartTime = Game.getGameTime();
		fInIntermission = false;
		fChangeMapNow = false;
		fNextMap = null;
	    }
	}
/**
 * Figure out what the name of the next map is.  
 *
 * This implementation looks at the "nextmap" parameter 
 * in the "worldspawn" entity.  A fancier LevelChanger gamelet 
 * might override this to read an external file, or allow 
 * players to vote somehow.
 *
 * @return Name of the next map that should be played
 */
protected String getNextMap() 
	{
	// check if the DM flags are set to keep playing the same
	// level over and over and over and over...(boring!)
	if (BaseQ2.isDMFlagSet(BaseQ2.DF_SAME_LEVEL))
		return Game.getCurrentMapName();
		
	// first try looking at the sv_maplist CVar
	String result = getNextMapFromCVar();

	// nothing? then try looking at the current map
	if (result == null)
		result = getNextMapFromDocument();

	// still nothing?  then just replay the current map
	if (result == null)
		result = Game.getCurrentMapName();
	
	return result;
	}
/**
 * Figure out what the next map should be - based on the 
 * sv_maplist CVar (feature added in Quake2 3.17)
 *
 * Example:  typing
 *    set sv_maplist "base1 q2dm1 q2dm3 fact3"
 * on the console will cause the server to rotate through those
 * four maps.
 *
 * @return name of next map, or null if cvar isn't set
 */
protected String getNextMapFromCVar() 
	{
	String s = fMapListCVar.getString();
	
	if ((s == null) || (s.length() == 0))
		return null;
		
	StringTokenizer st = new StringTokenizer(s);
	String firstEntry = null;
	while (st.hasMoreTokens())
		{
		s = st.nextToken();

		// remember the first entry in the list, in case
		// we need to wrap around
		if (firstEntry == null)
			firstEntry = s;

		if (s.equals(Game.getCurrentMapName()))
			break;
		}

	if (st.hasMoreTokens())
		return st.nextToken();
	else
		return firstEntry;
	}
/**
 * Figure out what the next map should be - based on the 
 * info embedded in the current map.
 *
 * @return name of next map, or null if it can't be determined
 */
protected String getNextMapFromDocument() 
	{
	Document doc = Game.getDocument("q2java.level");
	Element e;

	try
		{
		// look for <entity>..</entity> sections
		NodeList nl = doc.getElementsByTagName("entity");
		int count = nl.getLength();
		for (int i = 0; i < count; i++)
			{
			e = (Element) nl.item(i);
			
			// check if it's a "worldspawn" element and if
			// so, get the next map from that
			String className = e.getAttribute("class");
			if (className.equals("worldspawn"))
				{
				nl = e.getElementsByTagName("nextmap");
				e = (Element) nl.item(0);
				return e.getFirstChild().getNodeValue();
				}
			}
		}
	catch (Exception ex)
		{
		// must have been some problem in the document, where it
		// didn't contain a "worldspawn" entity with a "nextmap" subtag.
		}
		
	return null;
	}
/**
 * Check if it's time to quit this level.
 *
 * Look at the timelimit and fraglimit values and decide
 * if we're there yet.
 *
 * @return boolean true if it's time to end the level
 */
protected boolean isTimeToQuit() 
	{
	if (fChangeMapNow)
		return true;		

	if ((fTimeLimit > 0) && (Game.getGameTime() > (fLevelStartTime + (fTimeLimit * 60))))
		{
		Game.localecast("q2java.baseq2.Messages", "timelimit",  Engine.PRINT_HIGH);
		return true;
		}
		
	if (fFragLimit < 1)
		return false;
		
	Enumeration enum = Player.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		if (p.getScore() > fFragLimit)
			{
			Game.localecast("q2java.baseq2.Messages", "fraglimit", Engine.PRINT_HIGH);
			return true;
			}
		}		
		
	return false;		
	}
/**
 * Tick tock tick tock..gotta keep time with the clock.
 */
public void runFrame(int phase)
	{
	switch (phase)
		{
		case Game.FRAME_BEGINNING:
			// mirror various CVars
			fFragLimit	= (int) fFragLimitCVar.getFloat();
			fTimeLimit	= fTimeLimitCVar.getFloat();
			break;
			
		case Game.FRAME_MIDDLE:
			if (fInIntermission)
				{
				// check if we've been in intermission long enough
				if (Game.getGameTime() > fIntermissionEndTime)
					{
					// figure out the next map if one hasn't been specified
					if (fNextMap == null)
						fNextMap = getNextMap();

					// actually cause the Q2 engine to start a new map
					Engine.addCommandString("gamemap \"" + fNextMap + "\"\n");
					}
				}
			else
				{
				// check if we should start intermission
				if (isTimeToQuit())
					{
					// pause the game - player classes
					// should notice this and go into intermission mode
					Game.startIntermission();
					fInIntermission = true;
					fIntermissionEndTime = Game.getGameTime() + 5.0;					
					}
				}
			break;
		}
	}
/**
 * Force a map change, but do it nicely so that players see the scoreboard.
 */
public void svcmd_changemap(String[] args) 
	{
	fChangeMapNow = true;
	
	// if they've specified a particular map, make a note of it.
	if (args.length > 2)
		{
		fNextMap = args[2];
		}
	}
/**
 * Display help info to the console.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("\n\n    sv commands:\n");
	Game.dprint("       changemap [mapname]\n");
	}
/**
 * Unload from the game.
 */
public void unload() 
	{
	Game.removeServerFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_MIDDLE);
	Game.removeGameStatusListener(this);
	}
}