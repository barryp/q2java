package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;

/**
 * Support class for DeathScore event delegation.
 *
 * @author Peter Donald 25/1/99
 */
final public class DeathScoreSupport
	{
	private static Method gInvokeMethod = null;
	private Vector fListeners = new Vector();

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
	
public void addDeathScoreListener(DeathScoreListener l)
	{
	if(!fListeners.contains(l)) 
		fListeners.addElement(l);
	}
public void fireEvent(DeathScoreEvent e)
	{
	try 
		{ 
		EventPack.fireEvent( e, gInvokeMethod, fListeners ); 
		}
	catch(PropertyVetoException pve) 
		{
		}
	}
public void removeDeathScoreListener(DeathScoreListener l)
	{
	fListeners.removeElement(l);
	}
}