package q2java.core.event;

import java.lang.reflect.*;
import java.util.Vector;
import q2java.Engine;

/**
 * Convenience class for managing listeners and the actual construction of message
 *
 * @author Peter Donald 25/1/99
 */
public final class GameStatusSupport extends GenericEventSupport
	{
	private static Method gInvokeMethod;
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
	if (addListener(gsl))
		{
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

	fireEvent(gse, gInvokeMethod);

	fEventStack.removeElement(gse);
	gse.recycle();
	}
public void removeGameStatusListener(GameStatusListener gsl)
	{
	removeListener(gsl);
	}
}