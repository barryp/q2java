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
public class EnemyTracker extends GenericWidgetComponent implements ServerFrameListener, PlayerStateListener
	{
	protected Player fTarget;
	protected Point3f fLastPoint;
	
/**
 * Choose an enemy to track.
 */
protected void chooseTarget() 
	{
	NativeEntity widgetEntity = getWidgetBody().getWidgetEntity();
	Point3f widgetOrigin = widgetEntity.getOrigin();
	Vector3f v = Q2Recycler.getVector3f();
		
	float best_ls = Float.MAX_VALUE;
	Player bestPlayer = null;
		
	Enumeration enum = NativeEntity.enumeratePlayerEntities();
	while (enum.hasMoreElements())
		{
		try
			{
			Player p = (Player) ((NativeEntity) enum.nextElement()).getReference();

			if (!isPlayerSuitable(p))
				continue;

			// check if they're closer than previously checked players
			Point3f playerOrigin = p.fEntity.getOrigin();
			v.sub(playerOrigin, widgetOrigin);
			float ls = v.lengthSquared();
			if (ls < best_ls)
				{
				// check if they're visible from where we are
				TraceResults tr = Engine.trace(widgetOrigin, playerOrigin, widgetEntity, Engine.MASK_SOLID);
				if (tr.fFraction == 1.0F)
					bestPlayer = p;
				}
			}
		catch (Exception e)
			{
			}
		}

	if (bestPlayer != null)
		{
		fTarget = bestPlayer;
		fTarget.addPlayerStateListener(this);
		}
		
	Q2Recycler.put(v);
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
			if (fTarget != null)
				{
				fTarget.removePlayerStateListener(this);
				fTarget = null;
				}
			break;
		}
	}
/**
 * Is a given player a suitable target?
 * @return boolean
 * @param p q2java.baseq2.Player
 */
public boolean isPlayerSuitable(Player p) 
	{
	// ignore dead players
	if (p.isDead())
		return false;
			
	// ignore the owner of this widget
	Player owner = getWidgetBody().getWidgetOwner();
	if (p == owner)
		return false;

	// ignore teammates
	Object team = owner.getTeam();
	if ((team != null) && (team == p.getTeam()))
		return false;

	return true;
	}
/**
 * Called when a player dies, disconnects, teleports, etc.
 * @param pse the event describing the change.
 */
public void playerStateChanged(PlayerStateEvent pse)
	{
	fTarget.removePlayerStateListener(this);
	fTarget = null;
	fLastPoint = null;
	getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, null);	
	}
/**
 * This method was created in VisualAge.
 * @param phase int
 */
public void runFrame(int phase)	
	{
	// see if the current target is still visible
	if (fTarget != null)
		{
		NativeEntity widgetEntity = getWidgetBody().getWidgetEntity();
		TraceResults tr = Engine.trace(widgetEntity.getOrigin(), fTarget.fEntity.getOrigin(), widgetEntity, Engine.MASK_SOLID);
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
		Point3f p = fTarget.fEntity.getOrigin();
		if (!(p.equals(fLastPoint)))
			{
			fLastPoint = p;
			getWidgetBody().fireWidgetEvent(WidgetBody.TARGET, p);
			}
		}
	}
}