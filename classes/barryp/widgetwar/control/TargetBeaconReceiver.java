package barryp.widgetwar.control;

import javax.vecmath.*;
import q2java.core.*;
import q2java.core.event.*;

import barryp.widgetwar.*;
import barryp.widgetwar.payload.TargetBeacon;

/**
 * Home in on widgets with TargetBeacon components.
 *
 * @author Barry Pederson
 */
public class TargetBeaconReceiver extends GenericWidgetComponent implements ServerFrameListener
	{
	Point3f fCurrentTarget;
	
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:
			Game.addServerFrameListener(this, 0, 0);
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.removeServerFrameListener(this);
			break;
		}
	}
/**
 * This method was created in VisualAge.
 * @param phase int
 */
public void runFrame(int phase)	
	{
	Point3f t = TargetBeacon.getTarget();
	
	if (t == null)
		{
		if (fCurrentTarget != null)
			{
			fCurrentTarget = null;
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, null);
			}
		}
	else
		{
		if (!(t.equals(fCurrentTarget)))
			{
			if (fCurrentTarget == null)
				fCurrentTarget = new Point3f(t);
			else
				fCurrentTarget.set(t);
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, fCurrentTarget);			
			}
		}
	}
}