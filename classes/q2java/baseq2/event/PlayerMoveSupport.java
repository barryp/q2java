package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import q2java.PlayerCmd;
import q2java.baseq2.Player;
import q2java.core.event.*;

/**
 * support class for PlayerMove events delegation.
 *
 * @author Peter Donald
 */
public final class PlayerMoveSupport extends GenericEventSupport
	{
	
public void addPlayerMoveListener(PlayerMoveListener listener)
	{
	addListener(listener, false);
	}
/**
 * Let interested listeners know about the player moving.
 */
public void fireEvent( Player p, PlayerCmd move )
	{
	// I've implemented a custom fire method, instead of using
	// the superclass's, for a little extra speed, since this
	// is called very very often.
	
	// grab a reference to the listeners
	Object[] array = fListeners;
	
	if (array.length == 0)
		return;
		
	PlayerMoveEvent pme = PlayerMoveEvent.getEvent( p, move );

	for (int i = 0; i < array.length; i++)
		{
		try
			{
			((PlayerMoveListener)array[i]).playerMoved(pme);
			}
		catch (Throwable t) 
			{
			t.printStackTrace();
			}
		}

	pme.recycle();
	}
public void removePlayerMoveListener(PlayerMoveListener listener)
	{
	removeListener(listener);
	}
}