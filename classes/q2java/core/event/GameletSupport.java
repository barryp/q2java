package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.Gamelet;


/**
 * Support class for delegation of Gamelet event.
 */
final public class GameletSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = GameletListener.class.
	    getMethod("gameletChanged", new Class[] { GameletEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public void addGameletListener(GameletListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
public void fireEvent(int state, Gamelet g)
	{
	GameletEvent e = GameletEvent.getEvent( state, g);

	try 
		{ 
		EventPack.fireEvent( e, gInvokeMethod, fListeners ); 
		}
	catch(PropertyVetoException pve) 
		{
		}

	GameletEvent.releaseEvent(e); 
	}
  public void removeGameletListener(GameletListener l)
	{
	  fListeners.removeElement(l);
	}
}