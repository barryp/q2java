package q2java.core.event;

import q2java.core.Gamelet;

public class GameletEvent extends GameEvent
	{
	public final static int GAMELET_ADDED = 0;
	public final static int GAMELET_REMOVED = 1;

	protected int fState;
	protected Gamelet fGamelet;

	private static GameletEvent gCachedEvent = null;
	
  protected GameletEvent()
	{
	  super(GAME_GAMELET_EVENT);
	}
public GameletEvent(int state, Gamelet g)
	{
	super(null, GAME_GAMELET_EVENT);
	fGamelet = g;
	fState = state;
	}
/**
 * if you want conserve memory use this method to get message objects.
 * Note you must then call releaseEvent to conserve memory
 */
public static final GameletEvent getEvent( int state, Gamelet g ) 
	{
	GameletEvent event = gCachedEvent;
	   
	if( event == null )
		{
		event = new GameletEvent();
		}

	// if ever implement single player then have to include
	// the local gameinfo as source
	//event.source = 
	event.fGamelet = g;
	event.fState = state;

	return event; 
	}
public final Gamelet getGamelet() 
	{ 
	return fGamelet; 
	}
  /**
   * state of game that caused this message. 
   * see above list of constants for possible values.
   */
  public final int getState() { return fState; }      
  /**
   * Releases an event any may put it in cache to be re-used.
   */
  public final static void releaseEvent(GameletEvent event)
	{
	  gCachedEvent = event;
	  event.fGamelet = null;
	}
}