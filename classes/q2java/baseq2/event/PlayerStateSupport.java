package q2java.baseq2.event;

import java.lang.reflect.*;
import q2java.core.event.*;
import q2java.baseq2.*;

/**
 * Support class for delegating player state change events.
 *
 * @author Peter Donald 25/1/99
 */
public final class PlayerStateSupport extends GenericEventSupport
	{
	// all PlayerStateSupport objects will use the same invoke method
	// so we only need to do this once, when the class is loaded
	// instead of everytime an instance is created.
	private static Method gInvokeMethod;
	
	static
		{
	  	try
			{
	  		gInvokeMethod = PlayerStateListener.class.
	    	   getMethod("playerStateChanged", new Class[] { PlayerStateEvent.class } );	
			}
	  	catch (NoSuchMethodException nsme) 
	  		{
	  		nsme.printStackTrace();
	  		}
		}	
	
public void addPlayerStateListener(PlayerStateListener listener)
	{
	addListener(listener, false);
	}	
public void fireEvent( Player p, int stateChanged, GameObject source )
	{
	// make sure there are some listeners before going
	// the the bother of getting a hold of a PlayerStateEvent
	if (fListeners.length == 0)
		return;
		
	PlayerStateEvent pse = PlayerStateEvent.getEvent(p, stateChanged, source);
	fireEvent(pse, gInvokeMethod);
	pse.recycle();
	}
public void removePlayerStateListener(PlayerStateListener listener)
	{
	removeListener(listener);
	}
}