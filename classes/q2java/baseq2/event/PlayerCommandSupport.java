package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;

/**
 * support class for PlayerCommand events delegation.
 *
 * @author Peter Donald
 */
final public class PlayerCommandSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = PlayerCommandListener.class.
	    getMethod("commandIssued", new Class[] { PlayerCommandEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerCommandSupport()
	{
	}
  public void addPlayerCommandListener(PlayerCommandListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public PlayerCommandEvent fireEvent( Player p, String command, String args )
	{
	  PlayerCommandEvent e = PlayerCommandEvent.getEvent( p, command, args );

	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(PropertyVetoException pve) {}

	  PlayerCommandEvent.releaseEvent(e); 

	  // nb: e is not guarenteed to be valid when returned
	  // if multiple threads are accessing PlayerCommandSupport
	  return e;
	}
  public void removePlayerCommandListener(PlayerCommandListener l)
	{
	  fListeners.removeElement(l);
	}
}