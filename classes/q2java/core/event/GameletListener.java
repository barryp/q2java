package q2java.core.event;

import java.beans.PropertyVetoException;

/**
 * Interface for Gamelet event
 *
 * Converted to event Delegation Peter Donald
 * @author 	Leigh Dodds (was ModuleListener)
 */
public interface GameletListener  extends java.util.EventListener
	{
	
public void gameletChanged(GameletEvent e);      
}