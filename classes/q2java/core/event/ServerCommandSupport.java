package q2java.core.event;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import java.util.Vector;
import java.lang.reflect.*;
import q2java.Engine;

/**
 * support class for ServerCommand events delegation.
 *
 * @author Peter Donald
 */
final public class ServerCommandSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = ServerCommandListener.class.
	    getMethod("serverCommandIssued", new Class[] { ServerCommandEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public void addServerCommandListener(ServerCommandListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public ServerCommandEvent fireEvent( String command, String args[] )
	{
	  ServerCommandEvent e = ServerCommandEvent.getEvent( command, args );

	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(PropertyVetoException pve) {}

	  ServerCommandEvent.releaseEvent(e); 

	  // nb: e is not guarenteed to be valid when returned
	  // if multiple threads are accessing ServerCommandSupport
	  return e;
	}
  public void removeServerCommandListener(ServerCommandListener l)
	{
	  fListeners.removeElement(l);
	}
}