package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.Vector;
import java.lang.reflect.*;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;

/**
 * Support class used to delegate PlayerDamage events.
 *
 * @author Peter Donald
 */
final public class PlayerDamageSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();
  private Player fPlayer = null;

  static
	{
	  try
	{
	  gInvokeMethod = PlayerDamageListener.class.
	    getMethod("damageOccured", new Class[] { PlayerDamageEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerDamageSupport(Player player)
	{
	  fPlayer = player;
	}
  public void addPlayerDamageListener(PlayerDamageListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent(PlayerDamageEvent e)
	{
	  e.setPlayer( fPlayer );
	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners); }
	  catch(PropertyVetoException pve) {}
	}
  public void removePlayerDamageListener(PlayerDamageListener l)
	{
	  fListeners.removeElement(l);
	}
}