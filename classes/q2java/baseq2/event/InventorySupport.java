package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.core.event.*;
import q2java.baseq2.GenericItem;
import q2java.baseq2.Player;

/**
 * Support class for delegation of PlayerInvChange event.
 *
 * @author Peter Donald 24/1/99
 */
public final class InventorySupport extends GenericEventSupport
	{
  	private static Method gInvokeMethod = null;

  	static
		{
	  	try
			{
	  		gInvokeMethod = InventoryListener.class.
	    		getMethod("inventoryChanged", new Class[] { InventoryEvent.class } );	
			}
	  	catch(NoSuchMethodException nsme) {}
		}
	
public void addInventoryListener(InventoryListener il)
	{
	addListener(il, false);
	}
public void fireEvent( Player p, GenericItem item, boolean isPickingUp )
	throws PropertyVetoException
	{
	if (fListeners.length == 0)
		return;
		
	InventoryEvent ie = InventoryEvent.getEvent( p, item, isPickingUp );
	try
		{
		firePropertyEvent(ie, gInvokeMethod);
		}
	finally
		{
	  	ie.recycle();
	  	}
	}
public void removeInventoryListener(InventoryListener il)
	{
	removeListener(il);
	}
}