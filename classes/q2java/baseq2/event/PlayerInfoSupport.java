package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.core.event.*;
import q2java.baseq2.Player;

/**
 * support class for PlayerInfo events delegation.
 *
 * @author Peter Donald
 */
public final class PlayerInfoSupport extends GenericEventSupport
	{
  	private static Method gInvokeMethod = null;

  	static
		{
	  	try
			{
	  		gInvokeMethod = PlayerInfoListener.class.
	    		getMethod("infoChanged", new Class[] { PlayerInfoEvent.class } );	
			}
	  	catch(NoSuchMethodException nsme) {}
		}
	
public void addPlayerInfoListener(PlayerInfoListener pil)
	{
	addListener(pil);
	}
public void fireEvent(Player p, String key, String newValue, String oldValue )
	throws PropertyVetoException
	{
	if (fListeners.length == 0)
		return;
		
	PlayerInfoEvent pie = PlayerInfoEvent.getEvent( key, newValue, oldValue );
  	pie.setPlayer( p ); 
	pie.setSource( p );
	try
		{
		firePropertyEvent(pie, gInvokeMethod);
		}
	finally
		{
	  	pie.recycle();
	  	}
	}
public void removePlayerInfoListener(PlayerInfoListener pil)
	{
	removeListener(pil);
	}
}