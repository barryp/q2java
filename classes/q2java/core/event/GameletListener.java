package q2java.core.event;

import java.beans.PropertyVetoException;

/**
 * interface for Gamelet event
 * if you throw a property veto exception the gamelet will not be loaded
 * or els will not be unloaded (this is ignored when it is a result of game shutdown)
 *
 * Converted to event Delegation Peter Donald
 * @author 	Leigh Dodds (was ModuleListener)
 */
public interface GameletListener  extends java.util.EventListener
{
  public void gameletChanged(GameletEvent e);      
}