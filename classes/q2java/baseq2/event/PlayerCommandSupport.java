package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.core.event.*;
import q2java.baseq2.Player;

/**
 * support class for PlayerCommand events delegation.
 *
 * @author Peter Donald
 */
public final class PlayerCommandSupport extends GenericEventSupport
	{
  	private static Method gInvokeMethod;

  	static
		{
	  	try
			{
	  		gInvokeMethod = PlayerCommandListener.class.
	    		getMethod("commandIssued", new Class[] { PlayerCommandEvent.class } );	
			}
	  	catch(NoSuchMethodException nsme) {}
		}
	
public void addPlayerCommandListener(PlayerCommandListener pcl)
	{
	addListener(pcl, false);
	}
/**
 * Fire an Event
 *
 * @return true if the event was consumed, false if not
 */
public boolean fireEvent( Player p, String command, String args )
	{
	PlayerCommandEvent pce = PlayerCommandEvent.getEvent( p, command, args );

	fireEvent(pce, gInvokeMethod);
	boolean isConsumed = pce.isConsumed();
	
	pce.recycle(); 

	return isConsumed;
	}
public void removePlayerCommandListener(PlayerCommandListener pcl)
	{
	removeListener(pcl);
	}
}