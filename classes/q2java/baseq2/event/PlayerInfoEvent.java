package q2java.baseq2.event;

import q2java.baseq2.Player;

/**
 * Event to notify when a players localinfo has changed.
 *
 * @author Peter Donald
 */
public class PlayerInfoEvent extends PlayerEvent
{
  protected String fKey = null;
  protected String fNewValue = null;
  protected String fOldValue = null;

  // prolly not more than 2 events floating round at any one time 
  private static PlayerInfoEvent gCachedEvent = null;

  protected PlayerInfoEvent()
	{
	  super(PLAYER_INFO_EVENT);
	}
  public PlayerInfoEvent(Player player)
	{
	  super(player, player, PLAYER_INFO_EVENT);
	}
  public static final PlayerInfoEvent getEvent( String key, 
						String newValue,
						String oldValue ) 
	{
	 PlayerInfoEvent event = gCachedEvent;
	 gCachedEvent = null;
	 
	 if( event == null )
	   {
	 event = new PlayerInfoEvent();
	   }
	  
	  event.fKey = key;
	  event.fNewValue = newValue;
	  event.fOldValue = oldValue;

	  return event; 
	}
  /*
   * getter for property key.
   * how was damaged
   */
  public final String getKey() { return fKey; }    
  /*
   * getter for property newValue.
   * how was damaged
   */
  public final String getNewValue() { return fNewValue; }    
  /*
   * getter for property oldValue.
   * how was damaged
   */
  public final String getOldValue() { return fOldValue; }    
  public final static void releaseEvent(PlayerInfoEvent event)
	{
	  gCachedEvent = event;
	  event.fPlayer = null;
	  event.source = null;
	  event.fKey = null;
	  event.fNewValue = null;
	  event.fOldValue = null;
	}
}