package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;

/**
 * support class for PlayerInfo events delegation.
 *
 * @author Peter Donald
 */
final public class PlayerInfoSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();
  private Player fPlayer = null;

  static
	{
	  try
	{
	  gInvokeMethod = PlayerInfoListener.class.
	    getMethod("infoChanged", new Class[] { PlayerInfoEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerInfoSupport(Player player)
	{
	  fPlayer = player;
	}
  public void addPlayerInfoListener(PlayerInfoListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent( String key, String newValue, String oldValue )
	throws PropertyVetoException
	{
	  PlayerInfoEvent e =
	PlayerInfoEvent.getEvent( key, newValue, oldValue );
	  e.setPlayer( fPlayer ); 
	  e.setSource( fPlayer ); 
	  EventPack.fireEvent( e, gInvokeMethod, fListeners );
	  PlayerInfoEvent.releaseEvent(e); 
	}
  public void removePlayerInfoListener(PlayerInfoListener l)
	{
	  fListeners.removeElement(l);
	}
}