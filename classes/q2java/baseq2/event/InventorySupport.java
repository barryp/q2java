package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.event.EventPack;
import q2java.baseq2.GenericItem;
import q2java.baseq2.Player;

/**
 * Support class for delegation of PlayerInvChange event.
 *
 * @author Peter Donald 24/1/99
 */
final public class InventorySupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();
  private Player fPlayer = null;

  static
	{
	  try
	{
	  gInvokeMethod = InventoryListener.class.
	    getMethod("inventoryChanged", new Class[] { InventoryEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public InventorySupport(Player player)
	{
	  fPlayer = player;
	}
  public void addInventoryListener(InventoryListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent( GenericItem item, boolean isPickingUp )
	throws PropertyVetoException
	{
	  InventoryEvent e =
	InventoryEvent.getEvent( fPlayer, item, isPickingUp );
	  EventPack.fireEvent( e, gInvokeMethod, fListeners );
	  InventoryEvent.releaseEvent(e); 
	}
  public void removeInventoryListener(InventoryListener l)
	{
	  fListeners.removeElement(l);
	}
}