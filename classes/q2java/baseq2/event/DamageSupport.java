package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import q2java.baseq2.Player;
import q2java.core.event.*;

/**
 * Support class used to delegate PlayerDamage events.
 *
 * @author Peter Donald
 */
public final class DamageSupport extends GenericEventSupport
	{
	
public void addDamageListener(DamageListener dl)
	{
	addListener(dl, false);
	}
public void fireEvent(DamageEvent e)
	{
	// grab a reference to the list
	Object[] array = fListeners;

	// fire the events
	for (int i = 0; i < array.length; i++)
		{
		try
			{
			((DamageListener)array[i]).damageOccured(e);
			}
		catch (Throwable t)
			{
			t.printStackTrace();
			}
		}
	}
public void removeDamageListener(DamageListener dl)
	{
	removeListener(dl);
	}
}