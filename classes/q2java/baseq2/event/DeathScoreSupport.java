package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.core.event.*;
import q2java.baseq2.Player;

/**
 * Support class for DeathScore event delegation.
 *
 * @author Peter Donald 25/1/99
 */
public final class DeathScoreSupport extends GenericEventSupport
	{
	private static Method gInvokeMethod;

	static	
		{
		try
			{
	  		gInvokeMethod = DeathScoreListener.class.
	    	getMethod("deathOccured", new Class[] { DeathScoreEvent.class } );	
			}
		catch(NoSuchMethodException nsme) 
			{
	  		}
		}
	
public void addDeathScoreListener(DeathScoreListener dsl)
	{
	addListener(dsl, false);
	}
public void fireEvent(DeathScoreEvent dse)
	{
	fireEvent(dse, gInvokeMethod);
	}
public void removeDeathScoreListener(DeathScoreListener dsl)
	{
	removeListener(dsl);
	}
}