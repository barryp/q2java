package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.PlayerCmd;
import q2java.baseq2.*;

/**
 * event used to notify of value of client cvars
 *
 * @author Peter Donald 27/1/99
 */
public class PlayerMoveEvent extends PlayerEvent
{
  protected PlayerCmd fMove;
  
  private static PlayerMoveEvent gCachedEvent = null;

  protected PlayerMoveEvent()
	{
	  super(PLAYER_MOVE_EVENT);
	}
  public PlayerMoveEvent(Player player, PlayerCmd move)
	{
	  super(player, player, PLAYER_MOVE_EVENT);
	  fMove = move;
	}
  public static final PlayerMoveEvent getEvent( Player player, PlayerCmd move )
	{
	  PlayerMoveEvent event = gCachedEvent;
	  gCachedEvent = null;

	  if( event == null )
	{
	  event = new PlayerMoveEvent();
	}

	  event.source = player;
	  event.fPlayer = player;
	  event.fMove = move;

	  return event; 
	}
  public PlayerCmd getMove() { return fMove; }    
public final void recycle()
	{
	source = null;
	fPlayer = null;
	fMove = null;
	gCachedEvent = this;
	}
}