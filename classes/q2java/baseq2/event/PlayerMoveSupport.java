package q2java.baseq2.event;

import java.lang.reflect.*;
import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.Vector;
import q2java.PlayerCmd;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;

/**
 * support class for PlayerMove events delegation.
 *
 * @author Peter Donald
 */
final public class PlayerMoveSupport
	{
	private static Method gInvokeMethod = null;
	private Vector fListeners = new Vector();

	static
		{
		try
			{
			gInvokeMethod = PlayerMoveListener.class.getMethod("playerMoved", new Class[] { PlayerMoveEvent.class } );	
			}
		catch(NoSuchMethodException nsme) 
			{
			}
		}

	
public PlayerMoveSupport()
	{
	}
public void addPlayerMoveListener(PlayerMoveListener l)
	{
	if( !fListeners.contains(l) ) 
		fListeners.addElement(l);
	}
public void fireEvent( Player p, PlayerCmd move )
	{
	if (fListeners.size() == 0)
		return;
		
	PlayerMoveEvent e = PlayerMoveEvent.getEvent( p, move );

	try 
		{ 
		EventPack.fireEvent( e, gInvokeMethod, fListeners ); 
		}
	catch(PropertyVetoException pve) 
		{
		}

	PlayerMoveEvent.releaseEvent(e); 
	}
public void removePlayerMoveListener(PlayerMoveListener l)
	{
	fListeners.removeElement(l);
	}
}