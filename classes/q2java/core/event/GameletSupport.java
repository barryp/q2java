package q2java.core.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;

import org.w3c.dom.Document;

import q2java.Engine;
import q2java.core.Gamelet;


/**
 * Support class for delegation of Gamelet event.
 */
public final class GameletSupport extends GenericEventSupport
	{
	private static Method gInvokeMethod = null;

	static
		{
		try
			{
	  		gInvokeMethod = GameletListener.class.
			getMethod("gameletChanged", new Class[] { GameletEvent.class } );	
			}
		catch (NoSuchMethodException nsme) 
			{
			}
		}
	
public void addGameletListener(GameletListener gl)
	{
	addListener(gl);
	}
public void fireEvent(int state, Gamelet g, Document d) throws PropertyVetoException
	{
	GameletEvent ge = GameletEvent.getEvent(state, g, d);

	try 
		{
		firePropertyEvent(ge, gInvokeMethod);
		}
	finally
		{
		ge.recycle();
		}
	}
public void removeGameletListener(GameletListener gl)
	{
	removeListener(gl);
	}
}