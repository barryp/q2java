package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.*;

public final class EventPack
{
  // this is so no instance can ever be created
  private EventPack() {}      
  public static final void fireEvent(GenericEvent e, Method m, Vector listeners)
	throws PropertyVetoException
	{
	  Vector v = null;

	  synchronized(listeners) { v = (Vector)listeners.clone(); }
	  
	  Enumeration enum = v.elements();
	  v = null;
	  
	  Object params[] = new Object[] { e };

	  while( enum.hasMoreElements() )
	{
	  if( e instanceof Consumable && ((Consumable)e).isConsumed() )
	    {
	      return;
	    }

	  try { m.invoke(enum.nextElement(), params); }
	  catch(IllegalAccessException iae) { iae.printStackTrace(); }
	  catch(InvocationTargetException ite) 
	    {
	      if( ite.getTargetException() instanceof PropertyVetoException )
		{
		  throw ((PropertyVetoException)ite.getTargetException());
		}
	      else
		{
		  ite.getTargetException().printStackTrace();		  
		}
	    }
	}
	}
}