package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import q2java.baseq2.*;
import q2java.core.event.*;

/**
 * Support class for GoalScore event delegation.
 *
 * @author Peter Donald
 */
public final class GoalScoreSupport extends GenericEventSupport
	{
  	private static Method gInvokeMethod;

  	static
		{
	  	try
			{
	  		gInvokeMethod = GoalScoreListener.class.
	    		getMethod("goalAchieved", new Class[] { GoalScoreEvent.class } );	
			}
	  	catch(NoSuchMethodException nsme) {}
		}
	
public void addGoalScoreListener(GoalScoreListener gsl)
	{
	addListener(gsl);
	}
public void fireEvent(GameObject active, GameObject passive, String goalKey, int scoreChange)
	{
	GoalScoreEvent gse = GoalScoreEvent.getEvent( active, passive, goalKey, scoreChange );

	fireEvent(gse, gInvokeMethod);
	
	gse.recycle();
	}
public void removeGoalScoreListener(GoalScoreListener gsl)
	{
	removeListener(gsl);
	}
}