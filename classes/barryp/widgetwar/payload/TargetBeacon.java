package barryp.widgetwar.payload;

import java.util.Vector;
import javax.vecmath.*;

import q2java.core.Game;
import q2java.core.event.*;

import barryp.widgetwar.*;
/**
 * Broadcast a signal that the TargetBeaconReceiver control unit
 * homes in on.
 *
 * @author Barry Pederson
 */
public class TargetBeacon extends GenericWidgetComponent implements ServerFrameListener
	{
	private Point3f fTarget = new Point3f();
	
	private static Vector gTargetQueue = new Vector();
	
/**
 * This method was created in VisualAge.
 * @return javax.vecmath.Point3f
 */
public static Point3f getTarget() 
	{
	int n = gTargetQueue.size();
	if (n == 0)
		return null;
	else
		return (Point3f) gTargetQueue.elementAt(n-1);
	}
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:
			Game.addServerFrameListener(this, 0, 0);
			gTargetQueue.addElement(fTarget);
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.removeServerFrameListener(this);
			gTargetQueue.removeElement(fTarget);
			break;
		}
	}
/**
 * This method was created in VisualAge.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	fTarget.set(getWidgetBody().getWidgetEntity().getOrigin());
	}
}