package barryp.widgetwar.body;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import barryp.widgetwar.*;

/**
 * Guided missile
 *
 * @author Barry Pederson
 */
public class Missile extends GenericWidgetBody implements ServerFrameListener
	{
	private final static int TIMEOUT = 50; // clock ticks before missile self-destructs
	private final static int TRACKING_DELAY = 3; // ticks before missile guidance kicks in
	private final static double TURN_LIMIT = 10 * (Math.PI / 180.0); // 1 degree per clock-tick turn rate limit.
	private final static float ACCELERATION = 80;
	private final static float MAX_SPEED = 1200;
	
	// two representations of the direction the Missile is flying
	private Angle3f fAngle;
	private Vector3f fDirection;
	
	private float fSpeed;
	private Vector3f fVelocity = new Vector3f();	// working variable, should be fDirection.scale(fSpeed);
	int fCount;

	private Point3f fTarget;
	
/**
 * This method was created in VisualAge.
 */
protected void deployWidget() 
	{
	super.deployWidget();

	fAngle = getWidgetOwner().fEntity.getPlayerViewAngles();
	fDirection = new Vector3f();
	fAngle.getVectors(fDirection, null, null);	
	
	fEntity.setAngles(fAngle);
	setMinsMaxs();
	fEntity.setModel("models/objects/bomb/tris.md2");
	fEntity.setSound(Engine.getSoundIndex("weapons/rockfly.wav"));
	fEntity.setEffects(fEntity.getEffects() | NativeEntity.EF_ROCKET);
	fEntity.linkEntity();

	Game.addServerFrameListener(this, 0, 0);
	}
/**
 * This method was created in VisualAge.
 */
public void dispose() 
	{
	Game.removeServerFrameListener(this);
	
	super.dispose();
	}
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:
			deployWidget();
			break;
			
		case WidgetBody.TARGET:
			fTarget = (Point3f) extra;
			break;
			
		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			dispose();	
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
		// create a vector pointing from the missile to the target
		Vector3f newDir = new Vector3f();
		newDir.sub(fTarget, getWidgetEntity().getOrigin());

		// figure out the angle between where the missile's current heading
		// and the target
		float angle =  newDir.angle(fDirection);

		// alter course if necessary by interpolating between the current
		// direction and the direction to the target.  But limit
		// how fast the direction can change, so it makes cool swooping turns.
		if (angle != 0)
			{
			newDir.normalize();
//			double interp = TURN_LIMIT / angle;
			double interp = ((fSpeed / 100) * (Math.PI/180.0))/angle;
			if (interp >= 1)
				fDirection = newDir;
			else
				{
				fDirection.interpolate(newDir, (float)interp);
				fDirection.normalize();						
				}

			fAngle.set(fDirection);
			fEntity.setAngles(fAngle);
			setMinsMaxs();
			}		
		}

	fSpeed += ACCELERATION;
	if (fSpeed > MAX_SPEED)
		fSpeed = MAX_SPEED;
		
	fVelocity.scale(fSpeed, fDirection);
	fEntity.setVelocity(fVelocity);
	
	TraceResults tr = fEntity.traceMove(Engine.MASK_SHOT, 1.0F);
	if (tr.fFraction < 1.0F)
		{
		fireWidgetEvent(WidgetBody.TERMINATED);
		return;
		}
		
	if (++fCount > TIMEOUT)
		fireWidgetEvent(WidgetBody.TERMINATED);
	}
/**
 * Set a minimal bounding box for the missile, based on its orientation.
 */
private void setMinsMaxs() 
	{
	Vector3f v1 = Q2Recycler.getVector3f();
	Vector3f v2 = Q2Recycler.getVector3f();

	// make a vector representing the long axis of the missile
	fAngle.getVectors(v1, null, null);
	v1.scale(32.0F);

	// Make sure that no side is less than 12 units across
	if ((v1.x < 6) && (v1.x > -6))
		v1.x = 6;
		
	if ((v1.y < 6) && (v1.y > -6))
		v1.y = 6;
		
	if ((v1.z < 6) && (v1.z > -6))
		v1.z = 6;

	// Flip the vector around to come up with the other side of the
	// bounding box
	v2.scale(-1F, v1);

	// make sure the mins are less than the maxs, swapping if necessary
	if (v1.x > v2.x)
		{
		v1.x = v2.x;
		v2.x = -v2.x;
		}

	if (v1.y > v2.y)
		{
		v1.y = v2.y;
		v2.y = -v2.y;
		}
		
	if (v1.z > v2.z)
		{
		v1.z = v2.z;
		v2.z = -v2.z;
		}
		

	fEntity.setMins(v1);
	fEntity.setMaxs(v2);
	
	Q2Recycler.put(v2);
	Q2Recycler.put(v1);
	}
}