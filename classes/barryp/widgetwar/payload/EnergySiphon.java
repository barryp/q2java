package barryp.widgetwar.payload;

import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.Game;
import q2java.core.event.*;

import barryp.widgetwar.*;

/**
 * Steal energy from enemy players and transfer to the owner of this widget.
 *
 * @author Barry Pederson
 */
public class EnergySiphon extends GenericWidgetComponent implements ServerFrameListener
	{
	public final static float AMOUNT_SIPHONED = 0.15F; // amount of energy stolen per tick per player standing right next to the widget
	public final static float EFFECT_RADIUS = 300;
	public final static float EFFECT_RADIUS_SQUARED = EFFECT_RADIUS * EFFECT_RADIUS;
	
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:
			Game.addServerFrameListener(this, 0, 0F);
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.removeServerFrameListener(this);
			break;
		}
	}
/**
 * Steal a little energy from nearby players.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	Point3f p = getWidgetBody().getWidgetEntity().getOrigin();

	Vector3f v = Q2Recycler.getVector3f();
	WidgetWarrior owner = getWidgetBody().getWidgetOwner();
	
	Enumeration enum = 	NativeEntity.enumeratePlayerEntities();
	while (enum.hasMoreElements())
		{
		NativeEntity ne = (NativeEntity) enum.nextElement();
		Point3f p2 = ne.getOrigin();
		v.sub(p, p2);
		
		float f = v.lengthSquared();
		
		if (f > EFFECT_RADIUS_SQUARED)
			continue; // player was too far away

		// figure out the strength of the siphon, 1.0 at the origin of the widget,
		// decreasing to zero at the EFFECT_RADIUS
		f = (float)(AMOUNT_SIPHONED * (1.0F - (Math.sqrt(f) / EFFECT_RADIUS)));
		
		WidgetWarrior ww = (WidgetWarrior) ne.getReference();
		float e = ww.getEnergy();
		f = Math.min(f, e); // don't take more than the player is carrying
		if (f > 0)
			{
			ww.setEnergy(e - f);
			owner.setEnergy(owner.getEnergy() + f);
			}			
		}

	Q2Recycler.put(v);
	}
}