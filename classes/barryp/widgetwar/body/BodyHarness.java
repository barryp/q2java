package barryp.widgetwar.body;

import javax.vecmath.*;

import q2java.*;
import q2java.baseq2.GameObject;
import q2java.baseq2.event.*;

import barryp.widgetwar.*;

/**
 * Actual implementation of the WidgetBody interface.
 *
 * @author Barry Pederson
 */
public class BodyHarness implements WidgetBody, PlayerStateListener
	{	
	private WidgetWarrior fWidgetOwner;
	private WidgetComponent[] fComponentArray = new WidgetComponent[DEFAULT_ARRAY_SIZE];
	private int fTop;
	
	private final static int DEFAULT_ARRAY_SIZE = 2;
	
/**
 * Add a damage listener to the player.
 * @param dl q2java.baseq2.event.DamageListener
 */
public void addDamageListener(DamageListener dl) 
	{
	fWidgetOwner.addDamageListener(dl);
	}
/**
 * Add a component to the body.
 */
public void addWidgetComponent(WidgetComponent wc) 
	{
	// grow our array if necessary
	if (fTop == fComponentArray.length)
		{
		WidgetComponent[] nu = new WidgetComponent[fComponentArray.length + DEFAULT_ARRAY_SIZE];
		for (int i = 0; i < fComponentArray.length; i++)
			{
			nu[i] = fComponentArray[i];
			fComponentArray[i] = null;
			}
			
		fComponentArray = nu;
		}

	// link the two objects together
	fComponentArray[fTop++] = wc;
	wc.setWidgetBody(this);
	}
/**
 * Dispose of the widget.
 */
public void dispose() 
	{
	// let the owner know we're going away
	fWidgetOwner.widgetDisposed(this);
	fWidgetOwner.removePlayerStateListener(this);
	}
/**
 * Fire an event to the components and body of the widget.
 */
public void fireWidgetEvent(int event) 
	{
	fireWidgetEvent(event, null);
	}
/**
 * Fire an event to the components and body of the widget.
 */
public void fireWidgetEvent(int event, Object extra) 
	{
	// let each component know
	for (int i = 0; i < fTop; i++)
		fComponentArray[i].handleWidgetEvent(event, extra);
		
	// let the body know about the event
	handleWidgetEvent(event, extra);
	}
/**
 * Get how much energy the player has.
 * @return float
 */
public float getEnergy() 
	{
	return fWidgetOwner.getEnergy();
	}
/**
 * Get the health of the widget.
 * @return float
 */
public float getHealth() 
	{
	return fWidgetOwner.getHealth();
	}
/**
 * Get which components this widget is carrying
 */
public WidgetComponent[] getWidgetComponents() 
	{
	WidgetComponent[] result = new WidgetComponent[fTop];
	for (int i = 0; i < fTop; i++)
		result[i] = fComponentArray[i];
	return result;
	}
/**
 * Get the NativeEntity that is the widget's manifestation in the Q2 universe.
 * @return NativeEntity
 */
public NativeEntity getWidgetEntity() 
	{
	return fWidgetOwner.fEntity;
	}
/**
 * Get the player that constructed this widget.
 * @return barryp.widgetwar.WidgetWarrior
 */
public WidgetWarrior getWidgetOwner() 
	{
	return fWidgetOwner;
	}
/**
 * Called by the widget body to signal something.
 *
 * @param event one of the WidgetBody.SIGNAL_* constants.
 * @param extra some Object relevant to the event.  SIGNAL_TARGET for example should also pass a Point3f
 */
protected void handleWidgetEvent(int event, Object extra)
	{
	switch (event)
		{
		case WidgetBody.TARGET:
			if (fWidgetOwner.getCurrentWidget() == this)
				fWidgetOwner.setHUDTarget(this, (Point3f)extra);
			break;
			
		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			dispose();
			break;
		}
	}
public void playerStateChanged(PlayerStateEvent pse)
	{
	switch (pse.getStateChanged())			
		{
		case PlayerStateEvent.STATE_DEAD:
		case PlayerStateEvent.STATE_SUSPENDEDSTART:	
		case PlayerStateEvent.STATE_INVALID:
			fireWidgetEvent(WidgetBody.TERMINATED);
			break;
		}	
	}
/**
 * Remove a DamageListener from the player.
 * @param dl q2java.baseq2.event.DamageListener
 */
public void removeDamageListener(DamageListener dl) 
	{
	fWidgetOwner.removeDamageListener(dl);
	}
/**
 * Set how much energy the player has.
 * @param f float
 */
public void setEnergy(float f) 
	{
	fWidgetOwner.setEnergy(f);
	}
/**
 * Set the health of the widget.
 * @param f float
 */
public void setHealth(float f) 
	{
	fWidgetOwner.setHealth(f);
	}
/**
 * Set the player who made this.
 * @param w barryp.widgetwar.WidgetWarrior
 */
public void setWidgetOwner(WidgetWarrior w) 
	{
	fWidgetOwner = w;

	// register to be called if the player disconnects
	w.addPlayerStateListener(this);
	}
}