package q2java.baseq2.event;

import java.beans.PropertyVetoException;

/**
 * interface for PlayerInvChange event
 * if you throw a property veto exception the player 
 * will not be able to pickup an item.
 *
 * @author Peter Donald 24/1/99
 */
public interface InventoryListener  extends java.util.EventListener
{
  public void inventoryChanged(InventoryEvent e) throws PropertyVetoException;    
}