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
public final class ServerCommandSupport extends GenericEventSupport
	{
	private static Method gInvokeMethod;
  
  	static
		{
	  	try
			{
	  		gInvokeMethod = ServerCommandListener.class.
	    	  getMethod("serverCommandIssued", new Class[] { ServerCommandEvent.class } );	
			}
	 	catch (NoSuchMethodException nsme) {}
		}
	
public void addServerCommandListener(ServerCommandListener scl)
	{
	addListener(scl);
	}
/**
 * Fire a ServerCommandEvent
 *
 * @return true if the event was consumed, false if not
 */
public boolean fireEvent( String command, String args[] )
	{
	ServerCommandEvent sce = ServerCommandEvent.getEvent( command, args );

	fireEvent(sce, gInvokeMethod);
	
	boolean isConsumed = sce.isConsumed();	
	sce.recycle(); 

	// return the event, so the caller can check if it was consumed
  	// nb: e is not guarenteed to be valid when returned
  	// if multiple threads are accessing ServerCommandSupport
	return isConsumed;
	}
public void removeServerCommandListener(ServerCommandListener scl)
	{
	removeListener(scl);
	}
}