package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.*;
import q2java.baseq2.Player;

/**
 * Support class for event delegation.  Subclasses may use this class's
 * fire methods, or perhaps implement their own and just use this superclass
 * for its add/remove methods.  If you're really hell-bent on being fast, you
 * might just want to copy this entire class and customize the add/fire/remove
 * methods to use the exact classes you're interested in.
 *
 * @author Barry Pederson
 */
public class GenericEventSupport
	{
	protected Object[] fListeners = new Object[0];
	private Object[] fCachedParams; // param array can be reused, rather than create a new one for each fire call
	
   	//leighd 04/14/99 - modified for event debugging
  	protected static boolean gDebugEvents;
 	static
		{
		String debug = System.getProperty("q2java.event.debug", "0");
		if (debug.equals("1"))
			gDebugEvents = true;
		}	
	
/**
 * Create a GenericEventSupport object without specifying an invoke-method,
 * which means that subclasses should be implementing their own fire method
 * and only using this superclass for the add/remove methods.
 */
protected GenericEventSupport() 
	{
	}
/**
 * Add a listener
 *
 * @return true if the listener was added, false if it was already present
 */
protected final boolean addListener(Object listener)
	{
	// bail if the listener is already added
	for (int i = 0; i < fListeners.length; i++)
		{
		if (fListeners[i] == listener)
			return false;
		}

	// make a new listener array, one element larger than the old one
	// and add the new listener to the end
	Object[] newArray = new Object[fListeners.length+1];
	System.arraycopy(fListeners, 0, newArray, 0, fListeners.length);
	newArray[fListeners.length] = listener;

	// make it the active array
	fListeners = newArray;
	return true;
	}
/**
 * Fire an event to the registered listeners - will not stop for any reason,
 * exceptions will just have their stack-traces printed.  Subclasses may choose to
 * implement their own fire method, and just use this class for the
 * add/remove methods.
 *
 * @param evt the Event we're going to pass to the listeners
 */
protected final void fireEvent(Object evt, Method m)
	{
	// grab a reference to the listeners
	Object[] array = fListeners;
	
	if (array.length == 0)
		return;

	Object[] params = fCachedParams;
	if (params == null)
		params = new Object[]{ evt }; // wasn't available, make a new array
	else
		{
		fCachedParams = null; // set to null in case fire is called re-entrantly
		params[0] = evt;
		}

	try
		{
		for (int i = 0; i < array.length; i++)
		    {
		    try 
				{ 
				m.invoke(array[i], params); 
				}
	   	    catch(IllegalAccessException iae) 
				{ 
				iae.printStackTrace(); 
				}
		    catch(InvocationTargetException ite) 
		        {
				Throwable t = ite.getTargetException();
				t.printStackTrace();		  		
				}
				
		    if ((evt instanceof Consumable) && ((Consumable)evt).isConsumed())
		        {
		        if (gDebugEvents)
		            Engine.debugLog("Consumed By : " + array[i]);
		        return;
				}			
			}
		}
	finally
		{
		// make our param array available for re-use
		// doesn't matter if we made it fresh, or already
		// reused it from a previous fire call
		params[0] = null;
		fCachedParams = params;
		}
	}
/**
 * Fire an event to the registered listeners, but stop on a PropertyVetoException.  
 * Subclasses may choose to implement their own fire method, and just use 
 * this class for the add/remove methods.
 *
 * @param evt the Event we're going to pass to the listeners
 * @throws java.beans.PropertyVetoException
 */
protected final void firePropertyEvent(Object evt, Method m) throws PropertyVetoException
	{
	// grab a reference to the listeners
	Object[] array = fListeners;
	
	if (array.length == 0)
		return;

	Object[] params = fCachedParams;
	if (params == null)
		params = new Object[]{ evt }; // wasn't available, make a new array
	else
		{
		fCachedParams = null; // set to null in case fire is called re-entrantly
		params[0] = evt;
		}

	try
		{
		for (int i = 0; i < array.length; i++)
		    {
		    try 
				{ 
				m.invoke(array[i], params); 
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
				
		    if ((evt instanceof Consumable) && ((Consumable)evt).isConsumed())
		        {
		        if (gDebugEvents)
		            Engine.debugLog("Consumed By : " + array[i]);
		        return;
				}			
			}
		}
	finally
		{
		// make our param array available for re-use
		// doesn't matter if we made it fresh, or already
		// reused it from a previous fire call
		params[0] = null;
		fCachedParams = params;
		}
	}
/**
 * Find out whether event-debugging is on or off.
 * @return boolean
 */
public static boolean isDebugEvents() 
	{
	return gDebugEvents;
	}
protected final void removeListener(Object listener)
	{
	// make sure the listener is actually registered
	boolean ok = false;
	for (int i = 0; i < fListeners.length; i++)
		{
		if (fListeners[i] == listener)
			{
			ok = true;
			break;
			}
		}

	// if the listener wasn't added, then there's nothing to do
	if (!ok)
		return;

	// make a new array one element smaller than the old one, 
	// copy the elements from the old to new, but skip the
	// listener we're trying to remove
	Object[] newArray = new Object[fListeners.length-1];
	int j = 0;
	for (int i = 0; i < fListeners.length; i++)
		{
		if (fListeners[i] != listener)
			newArray[j++] = fListeners[i];
		}

	// make it the active array
	fListeners = newArray;
	}
/**
 * Control whether event-debugging is off or on.
 * @param b boolean
 */
public static void setDebugEvents(boolean b) 
	{
	gDebugEvents = b;
	}
}