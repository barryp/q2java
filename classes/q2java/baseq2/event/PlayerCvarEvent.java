package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.baseq2.*;

/**
 * event used to notify of value of client cvars
 *
 * @author Peter Donald 27/1/99
 */
public class PlayerCvarEvent extends PlayerEvent
{
  protected String fValue;
  
  private static PlayerCvarEvent gCachedEvent = null;

  protected PlayerCvarEvent()
	{
	  super(PLAYER_CVAR_EVENT);
	}
  public PlayerCvarEvent(String cvar, String value, Player player)
	{
	  super(cvar, player, PLAYER_CVAR_EVENT);
	  fValue = value;
	}
  public String getCvar() { return (String)source; }    
  public static final PlayerCvarEvent getEvent( String cvar,
						Player player,
						String value )
	{
	  PlayerCvarEvent event = gCachedEvent;
	  gCachedEvent = null;

	  if( event == null )
	{
	  event = new PlayerCvarEvent();
	}

	  event.source = cvar;
	  event.fPlayer = player;
	  event.fValue = value;

	  return event; 
	}
  public String getValue() { return fValue; }    
  public final static void releaseEvent(PlayerCvarEvent event)
	{
	  gCachedEvent = event;
	  event.fPlayer = null;
	  event.source = null;
	  event.fValue = null;
	}
}