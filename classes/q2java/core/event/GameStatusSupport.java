package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;

/**
 * Convenience class for managing listeners and the actual construction of message
 *
 * @author Peter Donald 25/1/99
 */
final public class GameStatusSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = GameStatusListener.class.
	    getMethod("gameStatusChanged", new Class[] { GameStatusEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public void addGameStatusListener(GameStatusListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent(int state, String filename)
	{
	  fireEvent( state, filename, null, null );
	}
  public void fireEvent(int state, String filename, String entString, String spawnPoint)
	{
	  GameStatusEvent e = GameStatusEvent.getEvent( state, filename, entString, spawnPoint );

	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(PropertyVetoException pve) {}

	  GameStatusEvent.releaseEvent(e); 
	}
  public void removeGameStatusListener(GameStatusListener l)
	{
	  fListeners.removeElement(l);
	}
}