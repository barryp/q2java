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

  static
	{
	  try
	{
	  gInvokeMethod = PlayerInfoListener.class.
	    getMethod("infoChanged", new Class[] { PlayerInfoEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerInfoSupport()
	{
	}
  public void addPlayerInfoListener(PlayerInfoListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent(Player p, String key, String newValue, String oldValue )
	throws PropertyVetoException
	{
	if (fListeners.size() == 0)
		return;
		
	  PlayerInfoEvent e =
	PlayerInfoEvent.getEvent( key, newValue, oldValue );
	  e.setPlayer( p ); 
	  e.setSource( p ); 
	  EventPack.fireEvent( e, gInvokeMethod, fListeners );
	  PlayerInfoEvent.releaseEvent(e); 
	}
  public void removePlayerInfoListener(PlayerInfoListener l)
	{
	  fListeners.removeElement(l);
	}
}