package barryp.widgetwar.payload;

import java.util.Vector;
import javax.vecmath.*;

import q2java.*;
import q2java.baseq2.*;
import q2java.core.*;
import q2java.core.event.*;

import barryp.widgetwar.*;
/**
 * Warhead for Widgets that throws out several grenades on detonation.
 *
 * @author Barry Pederson
 */
public class Explosive extends GenericWidgetComponent implements ServerFrameListener
	{
	protected Point3f fTarget;
	
	protected final static int DAMAGE = 160;
	protected final static int RADIUS = 200;
	
	protected final static float PROXIMITY_THRESHOLD = (float) (RADIUS * RADIUS);
	
/**
 * This method was created in VisualAge.
 */
protected void explode() 
	{
	WidgetBody wb = getWidgetBody();
	NativeEntity ent = wb.getWidgetEntity();

	try
		{
		MiscUtil.explode((GameObject) ent.getReference(), (GameObject) wb.getWidgetOwner(), ent, DAMAGE, RADIUS);
		}
	catch (Exception e)
		{
		e.printStackTrace();
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

		case WidgetBody.TARGET:
			fTarget = (Point3f) extra;
			break;
			
		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.removeServerFrameListener(this);
			explode();
			break;
		}
	}
/**
 * This method was created in VisualAge.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	if (fTarget != null)
		{
		WidgetBody b = getWidgetBody();
		
		Vector3f v = Q2Recycler.getVector3f();
		v.sub(fTarget, b.getWidgetEntity().getOrigin());
		float distSquared = v.lengthSquared();
		Q2Recycler.put(v);

		if (distSquared < PROXIMITY_THRESHOLD)
			b.fireWidgetEvent(WidgetBody.TERMINATED);
		}
	}
}