package q2java.core.event;

import q2java.NativeEntity;

/**
 * An event to notify when a player connects/disconnects/changes class.
 *
 * @author Peter Donald 25/1/99
 */
public class OccupancyEvent extends GameEvent
{
  public final static int PLAYER_CONNECTED = 0;
  public final static int PLAYER_DISCONNECTED = 1;
  public final static int PLAYER_CLASSCHANGE = 2;
  
  protected int fState;
  protected NativeEntity fPlayerEntity;

  private static OccupancyEvent gCachedEvent = null;
  protected OccupancyEvent()
	{
	  super(GAME_OCCUPANCY_EVENT);
	}
  public OccupancyEvent(NativeEntity playerEnt, int state)
	{
	  super(null, GAME_OCCUPANCY_EVENT);
	  fPlayerEntity = playerEnt;
	  fState = state;
	}
  /**
   * if you want conserve memory use this method to get message objects.
   * Note you must then call releaseEvent to conserve memory
   */
  public static final OccupancyEvent getEvent( NativeEntity playerEnt, int state )
	{
	  OccupancyEvent event = null;

	  if( event == null )
	{
	  event = new OccupancyEvent();
	}

	  // if ever implement single player then have to include
	  // the local gameinfo as source
	  //event.source = 
	  event.fPlayerEntity = playerEnt;
	  event.fState = state;

	  return event; 
	}
public final NativeEntity getPlayerEntity() 
	{ 
	return fPlayerEntity; 
	}
  public final int getState() { return fState; }      
  /**
   * Releases an event any may put it in cache to be re-used.
   */
  public final static void releaseEvent(OccupancyEvent event)
	{
	gCachedEvent = event;
	event.source = null;
	}
}