package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.*;

public final class EventPack
	{
	
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
	  	  
	Object params[] = new Object[] { e };

	for (int i = 0; i < snapshot.length; i++)
	    {
	    if ((e instanceof Consumable) && ((Consumable)e).isConsumed())
	        return;

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
}