package q2java.core.event;

import javax.vecmath.*;
import q2java.Engine;
import q2java.baseq2.GenericItem;

public class ServerCommandEvent extends GameEvent implements Consumable
{
  protected boolean fConsumed = false;
  protected String fArgs[] = null;

  private static ServerCommandEvent gCachedEvent = null;

  protected ServerCommandEvent()
	{
	  super(GAME_SERVERCOMMAND_EVENT);
	}
  public ServerCommandEvent(String command, String args[])
	{
	  super(command, GAME_SERVERCOMMAND_EVENT);
	  fArgs = args;
	}
  public final void consume() { setConsumed(true); }      
  public final String[] getArgs() { return fArgs; }      
  public final String getCommand() { return (String)getSource(); }      
  public static final ServerCommandEvent getEvent( String command, String args[] )
	{
	  ServerCommandEvent event = gCachedEvent;

	  if( event == null )
	{
	  event = new ServerCommandEvent();
	}

	  event.fConsumed = false;
	  event.source = command;
	  event.fArgs = args;

	  return event; 
	}
  public final boolean isConsumed() { return fConsumed; }      
  public final static void releaseEvent(ServerCommandEvent event)
	{
	  gCachedEvent = event;
	  event.source = null;
	  event.fArgs = null;
	}
  public final void setConsumed(boolean consumed) { fConsumed = consumed; }      
}