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
 * Home in on enemy players.
 *
 * @author Barry Pederson
 */
public class EnemyWidgetTracker extends GenericWidgetComponent implements ServerFrameListener
	{
	protected NativeEntity fTarget;
	protected Point3f fLastPoint;

	protected final static float RADIUS = 1500;
	
/**
 * This method was created in VisualAge.
 */
protected void chooseTarget() 
	{	
	Player widgetOwner = getWidgetBody().getWidgetOwner();
	Object widgetTeam = widgetOwner.getTeam();
	NativeEntity widgetEntity = getWidgetBody().getWidgetEntity();
	Point3f widgetOrigin = widgetEntity.getOrigin();

	NativeEntity[] nea = Engine.getRadiusEntities(widgetOrigin, RADIUS, false, true);
	
	for (int i = 0; i < nea.length; i++)
		{
		try
			{
			Object obj = nea[i].getReference();
			if (!(obj instanceof WidgetBody))
				continue;

			WidgetBody wb = (WidgetBody) obj;

			// ignore teammates widgets
			if (widgetTeam == wb.getWidgetOwner().getTeam())
				continue;
				
			// check if they're visible from where we are
			TraceResults tr = Engine.trace(widgetOrigin, nea[i].getOrigin(), widgetEntity, Engine.MASK_SOLID);
			if (tr.fFraction == 1.0F)
				{
				fTarget = nea[i];
				return;
				}				
			}
		catch (Exception e)
			{
			}
		}
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
	// check if the old target still exists
	if ((fTarget != null) && !fTarget.isValid())
		fTarget = null;
		
	// see if the current target is still visible
	if (fTarget != null)
		{
		NativeEntity widgetEntity = getWidgetBody().getWidgetEntity();
		TraceResults tr = Engine.trace(widgetEntity.getOrigin(), fTarget.getOrigin(), widgetEntity, Engine.MASK_SOLID);
		if (tr.fFraction < 1.0F) 
			fTarget = null;		
		}
		
	// pick a new target if necessary
	if (fTarget == null)
		chooseTarget();

	if (fTarget == null)
		{
		// no target...if there was one before, forget about it 
		// and fire a null TARGET event
		if (fLastPoint != null)
			{
			fLastPoint = null;
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, null);
			}
		}
	else
		{
		// there was a target..see if it's different than before, and
		// if so, fire a new TARGET event
		Point3f p = fTarget.getOrigin();
		if (!(p.equals(fLastPoint)))
			{
			fLastPoint = p;
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, p);
			}
		}
	}
}