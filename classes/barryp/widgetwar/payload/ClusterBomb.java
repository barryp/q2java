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
public class ClusterBomb extends GenericWidgetComponent implements ServerFrameListener
	{
	protected Point3f fTarget;
	
	protected final static float PROXIMITY_THRESHOLD = (float) (200.0 * 200.0);

	// parameters for the grenades thrown by the ClusterBomb
	protected final static int NUM_GRENADES = 8;	
	protected final static float TOSS_SPEED = 250;
	protected final static int DAMAGE = 120;
	protected final static int RADIUS_DAMAGE = 160;
	protected final static float TIMER = 0.8F;
	
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
			tossBomblets();
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
/**
 * This method was created in VisualAge.
 */
protected void tossBomblets() 
	{
	WidgetBody b = getWidgetBody();
	Player p = b.getWidgetOwner();
	NativeEntity ent = b.getWidgetEntity();
	Point3f origin = ent.getOrigin();
	
	Vector3f temp = Q2Recycler.getVector3f();
	Vector3f dir = Q2Recycler.getVector3f();

/*
	// if we have a target, figure out what direction it's in
	// otherwise just leave the "dir" object whatever state
	// it was given us from the Q2Recycler	
	if (fTarget != null)
		{
		dir.sub(fTarget, origin);
		dir.normalize();
		}
*/
	for (int i = 0; i < 6; i++)
		{
		temp.set(GameUtil.cRandom(), GameUtil.cRandom(), GameUtil.cRandom()+1);
/*		
		// if we have a target, we should have
		// the "dir" prepared already..skew the paths
		// of the grenades towards it.
		if (fTarget != null)
			{
			temp.scale(0.3F);	
			temp.add(dir);
			temp.normalize();
			}
*/			
		temp.scale(TOSS_SPEED);
		(new Grenade()).toss(p, ent, origin, temp, DAMAGE, TIMER + (GameUtil.cRandom()/2.0F), RADIUS_DAMAGE);
		}

	Q2Recycler.put(dir);
	Q2Recycler.put(temp);
	}
}