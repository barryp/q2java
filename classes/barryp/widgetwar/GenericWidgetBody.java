package barryp.widgetwar;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.GameObject;
import q2java.baseq2.event.*;

/**
 * Actual implementation of the WidgetBody interface.
 *
 * @author Barry Pederson
 */
public abstract class GenericWidgetBody extends GameObject implements WidgetBody, PlayerStateListener
	{
	private WidgetWarrior fWidgetOwner;
	private WidgetComponent[] fComponentArray = new WidgetComponent[DEFAULT_ARRAY_SIZE];
	private int fTop;
	
	private float fHealth = 100;
	private float fEnergy = 500;
	private float fMaxEnergy = 500;

	private DamageSupport fDamageSupport = new DamageSupport();
	protected EnergySource fEnergySource;
	
	private final static float DEFAULT_RECHARGE_INTERVAL = 1.0F;
	private final static float DEFAULT_RECHARGE_AMOUNT = 10;
	private final static int DEFAULT_ARRAY_SIZE = 2;

	//
	// Inner class that replenishes the widget's energy
	protected class EnergySource implements ServerFrameListener
		{
		private float fInterval;
		private float fAmount;
		
		public EnergySource(float interval, float amount)
			{
			setRechargeAmount(amount);
			setRechargeInterval(interval);
			}

		public void runFrame(int phase)
			{
			setEnergy(getEnergy() + fAmount);
			}

		public void dispose()
			{
			Game.removeServerFrameListener(this);
			}

		public float getRechargeAmount()
			{
			return fAmount;
			}
			
		public float getRechargeInterval()
			{
			return fInterval;
			}
			
		public void setRechargeInterval(float interval)
			{
			fInterval = interval;
			Game.addServerFrameListener(this, 0, fInterval);			
			}

		public void setRechargeAmount(float amount)
			{
			fAmount = amount;
			}
		}
	
/**
 * Add a DamageListener to the WidgetBody.
 * @param dl q2java.baseq2.event.DamageListener
 */
public void addDamageListener(DamageListener dl) 
	{
	fDamageSupport.addDamageListener(dl);
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
 * Called when damage is inflicted on the widget.
 * @param de q2java.baseq2.event.DamageEvent
 */
public void damage(DamageEvent de) 
	{
	// cut back damage to zero for team fire
	GameObject attacker = de.getAttacker();
	if (attacker instanceof WidgetWarrior)
		{
		WidgetWarrior ww = (WidgetWarrior) attacker;
		WidgetWarrior owner = getWidgetOwner();

		if ((ww != owner) && (ww.getTeam() == owner.getTeam()))
			de.setAmount(0);
		}
	
	// let any listeners take a crack at the damage first
	fDamageSupport.fireEvent(de);

	// cause sparks or whatever to fly
	spawnDamage(de);
	
	// if the event is harmless, then bail
	int amount = de.getAmount();
	if (amount <= 0)
		return;
		
	// decrease the widget's health
	float h = getHealth() - amount;
	setHealth(h);

	// no health? then we're toast
	if (h <= 0)
		fireWidgetEvent(WidgetBody.DESTROYED);
	}
/**
 * Handle basic deployment tasks.
 */
protected void deployWidget() 
	{
	try
		{
		fEntity = new NativeEntity();
		}
	catch (GameException ge)
		{
		}
		
	// make sure the entity points back to this Java object
	fEntity.setReference(this);

	// set the various values and flags
	fEntity.setMins(-15, -15, -15);
	fEntity.setMaxs(0, 0, 0);
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setClipmask(Engine.MASK_SOLID);

	
	// figure out a starting spot somewhat offset from the player
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	Vector3f offset = new Vector3f(48, 8, fWidgetOwner.fViewHeight - 8);
	
	Angle3f ang = fWidgetOwner.fEntity.getPlayerViewAngles();
	ang.x = 0; // zero out pitch
	ang.getVectors(forward, right, null);
	Point3f start = fWidgetOwner.projectSource(offset, forward, right);
	
	fEntity.setOrigin(start);
	fEntity.setAngles(ang);

	fEnergySource = new EnergySource(DEFAULT_RECHARGE_INTERVAL, DEFAULT_RECHARGE_AMOUNT);
	}
/**
 * Dispose of the widget.
 */
public void dispose() 
	{
	// let the owner know we're going away
	fWidgetOwner.widgetDisposed(this);
	fWidgetOwner.removePlayerStateListener(this);

	// stop getting the energy recharged.
	fEnergySource.dispose();

	// make some sign of the widget exiting
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Game.getSoundSupport().fireMuzzleEvent(fEntity, Engine.MZ_LOGOUT));
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);
	
	super.dispose();
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
	// let the body know about the event first, if it's the deploy event
	if (event == WidgetBody.DEPLOY)
		handleWidgetEvent(event, extra);
		
	// let each component know
	for (int i = 0; i < fTop; i++)
		fComponentArray[i].handleWidgetEvent(event, extra);
		
	// let the body know last, if it isn't a DEPLOY event
	if (event != WidgetBody.DEPLOY)
		handleWidgetEvent(event, extra);
	}
/**
 * Get how much energy the widget has.
 * @return float
 */
public float getEnergy() 
	{
	return fEnergy;
	}
/**
 * Get the health of the widget.
 * @return float
 */
public float getHealth() 
	{
	return fHealth;
	}
/**
 * Get the maximum amount of energy the widget can hold.
 * @return float
 */
public float getMaxEnergy() 
	{
	return fMaxEnergy;
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
	return fEntity;
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
		case WidgetBody.DEPLOY:
			deployWidget();
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
		case PlayerStateEvent.STATE_SUSPENDEDSTART:	
		case PlayerStateEvent.STATE_INVALID:
			fireWidgetEvent(WidgetBody.TERMINATED);
			break;
		}	
	}
/**
 * Remove a DamageListener from the WidgetBody.
 * @param dl q2java.baseq2.event.DamageListener
 */
public void removeDamageListener(DamageListener dl) 
	{
	fDamageSupport.removeDamageListener(dl);
	}
/**
 * Set how much energy the widget has.
 * @param f float
 */
public void setEnergy(float f) 
	{
	fEnergy = Math.min(f, fMaxEnergy);
	}
/**
 * Set the health of the widget.
 * @param f float
 */
public void setHealth(float f) 
	{
	fHealth = f;
	}
/**
 * Set the maximum amount of energy the widget can hold.
 * @param f float
 */
public void setMaxEnergy(float f) 
	{
	fMaxEnergy = f;
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