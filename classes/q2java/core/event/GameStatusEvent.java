package q2java.core.event;

import q2java.Recycler;

/**
 * Event to notify of Game status changes.
 * This is a conversion of the old interface GameStatusListener. 
 *
 * Adapted to delegation event model by Peter Donald 25/1/99
 * @author Barry Pederson.
 */
public class GameStatusEvent extends GameEvent
	{
	public final static int GAME_READGAME	= 0;
	public final static int GAME_READLEVEL	= 1;
	public final static int GAME_WRITEGAME	= 2;
	public final static int GAME_WRITELEVEL	= 3;
	public final static int GAME_INIT 		= 4;
	public final static int GAME_SHUTDOWN	= 5;
	public final static int GAME_PRESPAWN	= 6;
	public final static int GAME_POSTSPAWN	= 7;
	public final static int GAME_ENDLEVEL	= 8;
	public final static int GAME_INTERMISSION			= 9; // pause for intermission
	public final static int GAME_BUILD_LEVEL_DOCUMENT	= 10; // the level document is being built
	public final static int GAME_DOCUMENT_UPDATED		= 11; 
	
	protected int fState;
	protected String fFilename;
	protected String fSpawnpoint;
	protected String fMapEntities;

	protected static Recycler gRecycler = Recycler.getRecycler(GameStatusEvent.class);
	
public GameStatusEvent()
	{
	super(GAME_STATUS_EVENT);
	}
/**
 * Get the name of the document that was updated in a GAME_DOCUMENT_UPDATED event.
 */
public final String getDocumentName() 
	{
	// we'll reuse the fFilename field for this.
	return fFilename; 
	}
/**
 * if you want conserve memory use this method to get message objects.
 * Note you must then call releaseEvent to conserve memory
 */
public static final GameStatusEvent getEvent(int state, String filename, String entString, String spawnpoint)
	{
	GameStatusEvent event = (GameStatusEvent) gRecycler.getObject();
	
if (event == null)
	q2java.core.Game.dprint("Recycler put out a null!\n");
	// if ever implement single player then have to include
	// the local gameinfo as source
	//event.source = 
	event.fFilename = filename;
	event.fMapEntities = entString;
	event.fSpawnpoint = spawnpoint;
	event.fState = state;
	
	return event; 
	}
/**
 * this properties defines the filename to load from.
 * NB this is not applicable if the state property is init or shutdown
 */
public final String getFilename() 
	{ 
	return fFilename; 
	}
/**
 * For the BUILD_DOCUMENT event, this allows you to see
 * what info was embedded in the map itself.
 * @return java.lang.String
 */
public String getMapEntities() 
	{
	return fMapEntities;
	}
/**
 * For the BUILD_DOCUMENT event, find out what spawnpoint the Engine
 * specified.
 */
public String getSpawnpoint()
	{
	return fSpawnpoint;
	}
 /**
  * state of game that caused this message. 
  * see above list of constants for possible values.
  */
public final int getState() 
	{ 
	return fState; 
	}
/**
 * Releases an event any may put it in cache to be re-used.
 */
public final static void releaseEvent(GameStatusEvent event)
	{
	event.fFilename = null;
	event.fSpawnpoint = null;
	event.fMapEntities = null;
	
	gRecycler.putObject(event);
	}
}