package q2java.baseq2.event;

import q2java.core.event.GenericEvent;
import q2java.baseq2.Player;

/**
 * Superclass of all events to do with a specific player.
 *
 * @author Peter Donald
 */
abstract public class PlayerEvent extends GenericEvent
{
  protected Player fPlayer = null;
  public PlayerEvent(int type)
	{
	  super(null,type);
	}
public PlayerEvent(Object source, Player player, int type)
	{
	// we assume here that type is >= FIRST_PLAYER_EVENT && <= LAST_PLAYER_EVENT
	super(source,type);
	fPlayer = player;
	}
public Player getPlayer() 
	{ 
	return fPlayer; 
	}
public void setPlayer(Player player) 
	{ 
	fPlayer = player; 
	}
}