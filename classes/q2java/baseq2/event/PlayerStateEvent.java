package q2java.baseq2.event;

import javax.vecmath.*;
import java.lang.reflect.*;
import q2java.Engine;
import q2java.baseq2.GameObject;
import q2java.baseq2.Player;

/**
 * event used to notify of player state changes.
 * this is a conversion of old playerStateListener interface
 *
 * @author Peter Donald 25/1/99
 */
public class PlayerStateEvent extends PlayerEvent
{
  // when player dies STATE_DEAD with source = attacker
  public final static int STATE_DEAD = 0; 

  // when player is respawned with source = world entity
  public final static int STATE_SPAWNED = 1;

  // when a level is changing/in intermission with source = world entity
  public final static int STATE_SUSPENDEDSTART = 2;

  // when a player is changing class/disconnecting .. source = player
  public final static int STATE_INVALID = 3;

  // when a player makes a DRASTIC change in position .. source = player
  // (handy for items like grapple hook that need to reset in this case)
  public final static int STATE_TELEPORTED = 4;
  
  protected int fStateChanged;

  // prolly not more than 2 events floating round at any one time 
  private static PlayerStateEvent gCachedEvent = null;

  protected PlayerStateEvent()
	{
	  super(PLAYER_STATE_EVENT);
	}
  public PlayerStateEvent(Player player, int stateChanged, GameObject source)
	{
	  super(source, player, PLAYER_STATE_EVENT);
	  fStateChanged = stateChanged;
	}
  public static final PlayerStateEvent getEvent( Player player, 
						 int stateChanged,
						 GameObject source )
	{
	  PlayerStateEvent event = gCachedEvent;
	  gCachedEvent = null;

	  if( event == null )
	{
	  event = new PlayerStateEvent();
	}

	  event.source = source;
	  event.fPlayer = player;
	  event.fStateChanged = stateChanged;

	  return event; 
	}
  public final int getStateChanged() { return fStateChanged; }    
  public final static void releaseEvent(PlayerStateEvent event)
	{
	  gCachedEvent = event;
	  event.fPlayer = null;
	  event.source = null;
	}
}