package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.Engine;
import q2java.core.event.Consumable;
import q2java.baseq2.GenericItem;
import q2java.baseq2.Player;

public class PlayerCommandEvent extends PlayerEvent implements Consumable
{
  protected String fArgs = null;
  protected boolean fConsumed = false;

  private static PlayerCommandEvent gCachedEvent = null;

  protected PlayerCommandEvent()
	{
	  super(PLAYER_COMMAND_EVENT);
	}
  public PlayerCommandEvent(Player player, String command, String args)
	{
	  super(command, player, PLAYER_COMMAND_EVENT);
	  fArgs = args;
	}
  public final void consume() { setConsumed(true); }    
  public final String getArgs() { return fArgs; }    
  public final String getCommand() { return (String)getSource(); }    
  public static final PlayerCommandEvent getEvent( Player player, 
						   String command,
						   String args )
	{
	  PlayerCommandEvent event = gCachedEvent;

	  if( event == null )
	{
	  event = new PlayerCommandEvent();
	}

	  event.fConsumed = false;
	  event.source = command;
	  event.fPlayer = player;
	  event.fArgs = args;

	  return event; 
	}
  public final boolean isConsumed() { return fConsumed; }    
  public final static void releaseEvent(PlayerCommandEvent event)
	{
	  gCachedEvent = event;
	  event.fPlayer = null;
	  event.source = null;
	  event.fArgs = null;
	}
  public final void setConsumed(boolean consumed) { fConsumed = consumed; }    
}