package q2java.core.event;

import java.lang.reflect.*;
import java.beans.PropertyVetoException;
import q2java.NativeEntity;

/**
 * @author Peter Donald
 */
public final class OccupancySupport extends GenericEventSupport
	{
  	private static Method gInvokeMethod;

  	static
		{
		try
			{
	  		gInvokeMethod = OccupancyListener.class.
	    	  getMethod("playerChanged", new Class[] { OccupancyEvent.class } );	
			}
		catch (NoSuchMethodException nsme) 
			{
			}
		}
	
public void addOccupancyListener(OccupancyListener ol)
	{
	addListener(ol, false);
	}
public void fireEvent(NativeEntity playerEnt, int state) throws PropertyVetoException
	{
	OccupancyEvent oe = OccupancyEvent.getEvent( playerEnt, state );
	try
		{
		firePropertyEvent(oe, gInvokeMethod);
		}
	finally
		{
		oe.recycle();
		}
	}
public void removeOccupancyListener(OccupancyListener ol)
	{
	removeListener(ol);
	}
}