package barryp.widgetwar;

import javax.vecmath.Point3f;
import q2java.NativeEntity;
import q2java.baseq2.event.*;

/**
 * Interface for Wiget Body objects.
 *
 * @author Barry Pederson
 */
public interface WidgetBody 
	{
	public final static int UNKNOWN_EVENT	= 0; // some unknown signal is being sent, components
											     // should refer to the object being passed and try
											     // to make sense if it themselves, or ignore it if
											     // they can't
												     
	public final static int DEPLOY      = 1; // widget construction is complete
	public final static int MANUAL_MODE	= 2; // some component plans to activate the widget manually
	public final static int TRIGGER	    = 3; // widget has been manually activated
	public final static int TARGET		= 4; // a target location has been updated
	public final static int DESTROYED   = 5; // widget is destroyed by enemy action
	public final static int TERMINATED	= 6; // widget voluntarily disposing of itself
	
	// I reserve the values 7..31 for future expansion by myself (Barry)
	
/**
 * This method was created in VisualAge.
 * @param dl q2java.baseq2.event.DamageListener
 */
void addDamageListener(DamageListener dl);
/**
 * Add a component to this body.
 * @param wc barryp.widgetwar.WidgetComponent
 */
public void addWidgetComponent(WidgetComponent wc);
/**
 * Send a signal to the widget body and the components.
 * @param event int
 */
public void fireWidgetEvent(int event);
/**
 * Send a signal to the widget body and the components.
 * @param event int
 * @param extra some Object relevant to the event.  SIGNAL_TARGET for example should also pass a Point3f
 */
public void fireWidgetEvent(int event, Object extra);
/**
 * This method was created in VisualAge.
 * @return float
 */
float getEnergy();
/**
 * Get the health of the widget.
 * @return float
 */
float getHealth();
/**
 * Find out what components this body is holding.
 * @return barryp.widgetwar.WidgetComponent[]
 */
public WidgetComponent[] getWidgetComponents();
/**
 * Get the NativeEntity that is the widget's manifestation in the Q2 universe.
 * @return NativeEntity
 */
public NativeEntity getWidgetEntity();
/**
 * Get the player that constructed this widget.
 * @return barryp.widgetwar.WidgetWarrior
 */
public WidgetWarrior getWidgetOwner();
/**
 * Remove a DamageListener from the WidgetBody.
 * @param dl q2java.baseq2.event.DamageListener
 */
void removeDamageListener(DamageListener dl);
/**
 * This method was created in VisualAge.
 * @param f float
 */
void setEnergy(float f);
/**
 * Set the health of the widget.
 * @param f float
 */
void setHealth(float f);
/**
 * Set the player who made this.
 * @param w barryp.widgetwar.WidgetWarrior
 */
public void setWidgetOwner(WidgetWarrior w);
}