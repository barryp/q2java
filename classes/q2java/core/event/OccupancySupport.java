package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.NativeEntity;

/**
 * @author Peter Donald
 */
final public class OccupancySupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	try
		{
	  	gInvokeMethod = OccupancyListener.class.
	    getMethod("playerChanged", new Class[] { OccupancyEvent.class } );	
		}
	catch(NoSuchMethodException nsme) 
		{
		}
	}

  public void addOccupancyListener(OccupancyListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent( NativeEntity playerEnt, int state ) throws PropertyVetoException
	{
	  OccupancyEvent e = OccupancyEvent.getEvent( playerEnt, state );

	  EventPack.fireEvent( e, gInvokeMethod,  fListeners );
	  OccupancyEvent.releaseEvent(e); 
	}
  public void removeOccupancyListener(OccupancyListener l)
	{
	  fListeners.removeElement(l);
	}
}