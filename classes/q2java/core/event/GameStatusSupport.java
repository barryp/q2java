package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;

/**
 * Convenience class for managing listeners and the actual construction of message
 *
 * @author Peter Donald 25/1/99
 */
final public class GameStatusSupport
	{
	private static Method gInvokeMethod = null;
	private Vector fListeners = new Vector();
	private Vector fEventStack = new Vector();
	
	static
		{
		try
			{
			gInvokeMethod = GameStatusListener.class.getMethod("gameStatusChanged", new Class[] { GameStatusEvent.class } );	
			}
		catch(NoSuchMethodException nsme) 
			{
			}
		}

	
public void addGameStatusListener(GameStatusListener gsl)
	{
	if (!fListeners.contains(gsl))
		{
		fListeners.addElement(gsl);

		// if we're adding a listener right in the middle of firing off
		// an event, let the new listener in on the action
		int nEvents = fEventStack.size();
		if (nEvents > 0)
			gsl.gameStatusChanged((GameStatusEvent)fEventStack.elementAt(nEvents-1));
		}
	}
public void fireEvent(int state)
	{
	fireEvent(state, null, null, null);
	}
public void fireEvent(int state, String filename)
	{
	fireEvent(state, filename, null, null);
	}
public void fireEvent(int state, String entString, String spawnpoint)
	{
	fireEvent(state, null, entString, spawnpoint);
	}
public void fireEvent(int state, String filename, String entString, String spawnpoint)
	{
	GameStatusEvent gse = GameStatusEvent.getEvent(state, filename, entString, spawnpoint);
	fEventStack.addElement(gse);
	
	try 
		{ 
		EventPack.fireEvent(gse, gInvokeMethod, fListeners); 
		}
	catch(PropertyVetoException pve) 
		{
		}

	fEventStack.removeElement(gse);
	GameStatusEvent.releaseEvent(gse);
	}
public void removeGameStatusListener(GameStatusListener gsl)
	{
	fListeners.removeElement(gsl);
	}
}