package barryp.widgetwar.control;

import java.util.*;
import javax.vecmath.*;
import q2java.*;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;
import q2java.core.*;
import q2java.core.event.*;

import barryp.widgetwar.*;

/**
 * Home in on whatever spot the widget owner's crosshair is pointing at.
 *
 * @author Barry Pederson
 */
public class LaserGuidance extends GenericWidgetComponent implements ServerFrameListener
	{
	protected Point3f fLastPoint;	
	
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
	WidgetBody wb = getWidgetBody();
	WidgetWarrior ww = wb.getWidgetOwner();

	// check if this widget is what's currently selected
	// in the player's HUD
	if (ww.getCurrentWidget() != wb)
		return;

	// check if the fire button is being pressed
	if ((ww.fButtons & PlayerCmd.BUTTON_ATTACK) == 0)
		{
		// no? then if we had a target before, forget it and fire
		// a null target event
		if (fLastPoint != null)
			{
			fLastPoint = null;			
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, null);
			}
		return;
		}

	// do a trace to find what player's crosshair is resting on
	Point3f start = ww.fEntity.getOrigin();
	start.z += ww.fViewHeight;

	Vector3f forward = new Vector3f();
	Angle3f ang = ww.fEntity.getPlayerViewAngles();
	ang.getVectors(forward, null, null);

	Point3f end = new Point3f();
	end.scaleAdd(8192, forward, start);

	TraceResults tr = Engine.trace(start, end, ww.fEntity, Engine.MASK_SHOT|Engine.CONTENTS_SLIME|Engine.CONTENTS_LAVA);

	// a new point? see if it's visible from the widget itself
	NativeEntity ent = wb.getWidgetEntity();
	Point3f p = tr.fEndPos;
	tr = Engine.trace(ent.getOrigin(), p, ent, Engine.MASK_SHOT|Engine.CONTENTS_SLIME|Engine.CONTENTS_LAVA);
	
	if (tr.fFraction < 1.0)
		p = null;
		
	// check if the point has changed from before
	if ((p != fLastPoint) 
	|| ((p != null) && !(p.equals(fLastPoint))))
		{
		fLastPoint = p;
		getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, p);
		}
	}
}