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
final public class DamageSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = DamageListener.class.
	    getMethod("damageOccured", new Class[] { DamageEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public DamageSupport()
	{
	}
  public void addDamageListener(DamageListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent(DamageEvent e)
	{
	if (fListeners.size() == 0)
		return;
		
	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners); }
	  catch(PropertyVetoException pve) {}
	}
  public void removeDamageListener(DamageListener l)
	{
	  fListeners.removeElement(l);
	}
}