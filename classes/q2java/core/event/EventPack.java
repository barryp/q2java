package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.*;
import q2java.Engine;

public final class EventPack
	{  
  	//leighd 04/14/99 - modified for event debugging
  	private static boolean gDebugEvents;
  
  	static
		{
		String debug = System.getProperty("q2java.event.debug", "0");
		if (debug.equals("1"))
			gDebugEvents = true;
		}
	
// this is so no instance can ever be created
private EventPack() 
	{
	}
public static final void fireEvent(GenericEvent e, Method m, Vector listeners)
	throws PropertyVetoException
	{
	Object[] snapshot = null;

	synchronized(listeners)
		{
		snapshot = new Object[listeners.size()];
		listeners.copyInto(snapshot);     
		}

	//leighd 04/14/99 - modified for event debugging
	if (gDebugEvents)
		Engine.debugLog("Event : " + e.toString());
		
	Object params[] = new Object[] { e };

	for (int i = 0; i < snapshot.length; i++)
	    {
	    if ((e instanceof Consumable) && ((Consumable)e).isConsumed())
	        {
	        //leighd 04/14/99
	        //note that we don't catch the consumption of the
	        //event until we've passed through the loop again,
	        //so we reference i-1
	        if (gDebugEvents)
	            Engine.debugLog("Consumed By : " + snapshot[i-1]);
	        return;
			}
	    try 
			{ 
			m.invoke(snapshot[i], params); 
			}
   	    catch(IllegalAccessException iae) 
			{ 
			iae.printStackTrace(); 
			}
	    catch(InvocationTargetException ite) 
	        {
			Throwable t = ite.getTargetException();
	        if( t instanceof PropertyVetoException )
		  		throw ((PropertyVetoException)t);
	      	else
				t.printStackTrace();		  
			}
		}
	}
/**
 * Find out whether event-debugging is on or off.
 * @return boolean
 */
public boolean isDebugEvents() 
	{
	return gDebugEvents;
	}
/**
 * Control whether event-debugging is off or on.
 * @param b boolean
 */
public void setDebugEvents(boolean b) 
	{
	gDebugEvents = b;
	}
}