package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.baseq2.*;
import q2java.core.event.EventPack;

/**
 * Support class for GoalScore event delegation.
 *
 * @author Peter Donald
 */
final public class GoalScoreSupport
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = GoalScoreListener.class.
	    getMethod("goalAchieved", new Class[] { GoalScoreEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public void addGoalScoreListener(GoalScoreListener l)
	{
	  if( !fListeners.contains(l) ) fListeners.addElement(l);
	}
  public void fireEvent( GameObject active, 
			 GameObject passive,
			 String goalKey,
			 int scoreChange)
	{
	  GoalScoreEvent e = GoalScoreEvent.getEvent( active, passive, goalKey, scoreChange );

	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(PropertyVetoException pve) {}

	  GoalScoreEvent.releaseEvent( e );
	}
  public void removeGoalScoreListener(GoalScoreListener l)
	{
	  fListeners.removeElement(l);
	}
}