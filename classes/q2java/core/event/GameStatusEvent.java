package q2java.core.event;

/**
 * Event to notify of Game status changes.
 * This is a conversion of the old interface GameStatusListener. 
 *
 * Adapted to delegation event model by Peter Donald 25/1/99
 * @author Barry Pederson.
 */
public class GameStatusEvent extends GameEvent
{
  public final static int GAME_READGAME = 0;
  public final static int GAME_READLEVEL = 1;
  public final static int GAME_WRITEGAME = 2;
  public final static int GAME_WRITELEVEL = 3;
  public final static int GAME_INIT = 4;
  public final static int GAME_SHUTDOWN = 5;
  public final static int GAME_PRESPAWN = 6;
  public final static int GAME_POSTSPAWN = 7;

  protected int fState;
  protected String fFilename;
  protected String fEntityString;
  protected String fSpawnPoint;

  private static GameStatusEvent gCachedEvent = null;

  protected GameStatusEvent()
	{
	  super(GAME_STATUS_EVENT);
	}
  public GameStatusEvent(int state, String filename)
	{
	  super(null, GAME_STATUS_EVENT);
	  fFilename = filename;
	  fState = state;
	}
  /**
   * this is only applicable if state is a pre/post entity spawn event.
   */
  public final String getEntityString() { return fEntityString; }      
  /**
   * if you want conserve memory use this method to get message objects.
   * Note you must then call releaseEvent to conserve memory
   */
  public static final GameStatusEvent getEvent( int state, 
						String filename, 
						String entityString,
						String spawnPoint )
	{
	  GameStatusEvent event = gCachedEvent;
	   
	  if( event == null )
	{
	  event = new GameStatusEvent();
	}

	  // if ever implement single player then have to include
	  // the local gameinfo as source
	  //event.source = 
	  event.fFilename = filename;
	  event.fState = state;
	  event.fSpawnPoint = spawnPoint;
	  event.fEntityString = entityString;

	  return event; 
	}
  /**
   * this properties defines the filename to load from.
   * NB this is not applicable if the state property is init or shutdown
   */
  public final String getFilename() { return fFilename; }      
  /**
   * this properties defines the filename to load from.
   * NB this is not applicable if the state property is init or shutdown
   */
  public final String getMapname() { return getFilename(); }      
  /**
   * this is only applicable if state is a pre/post entity spawn event.
   */
  public final String getSpawnPoint() { return fSpawnPoint; }      
  /**
   * state of game that caused this message. 
   * see above list of constants for possible values.
   */
  public final int getState() { return fState; }      
  /**
   * Releases an event any may put it in cache to be re-used.
   */
  public final static void releaseEvent(GameStatusEvent event)
	{
	  gCachedEvent = event;
	  event.fFilename = null;
	  event.fEntityString = null;
	  event.fSpawnPoint = null;
	}
}