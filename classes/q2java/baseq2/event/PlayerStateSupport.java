package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.event.EventPack;
import q2java.baseq2.GameObject;
import q2java.baseq2.Player;


/**
 * Support class for delegating player state change events.
 *
 * @author Peter Donald 25/1/99
 */
final public class PlayerStateSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();
  private Player fPlayer = null;

  static
	{
	  try
	{
	  gInvokeMethod = PlayerStateListener.class.
	    getMethod("stateChanged", new Class[] { PlayerStateEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerStateSupport(Player player)
	{
	  fPlayer = player;
	}
  public void addPlayerStateListener(PlayerStateListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent( int stateChanged, GameObject source )
	{
	  PlayerStateEvent e =
	PlayerStateEvent.getEvent( fPlayer, stateChanged, source );
	  
	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(PropertyVetoException pve) {}

	  PlayerStateEvent.releaseEvent(e); 
	}
  public void removePlayerStateListener(PlayerStateListener l)
	{
	  fListeners.removeElement(l);
	}
}