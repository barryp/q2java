package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.Engine;
import q2java.baseq2.GenericItem;
import q2java.baseq2.Player;

/**
 * event to notify, alter and veto inventory pickups/drops.
 * 
 * @author Peter Donald 24/1/99
 */
public class InventoryEvent extends PlayerEvent
{
  protected GenericItem fItem = null;
  protected boolean fIsPickingUp = true;

  private static InventoryEvent gCachedEvent; 

  protected InventoryEvent()
	{
	  super(PLAYER_INVENTORY_EVENT);
	}
  public InventoryEvent(Player player)
	{
	  super(player, player, PLAYER_INVENTORY_EVENT);
	}
  public static final InventoryEvent getEvent( Player player, 
					       GenericItem item, 
					       boolean isPickingUp )
	{
	  InventoryEvent event = gCachedEvent;

	  if( event == null )
	{
	  event = new InventoryEvent();
	}
	  
	  gCachedEvent = null;

	  event.source = event.fPlayer = player;
	  event.fItem = item;

	  return event; 
	}
  /*
   * getter for property item.
   * how was damaged
   */
  public final GenericItem getItem() { return fItem; }    
  public final boolean isPickingUp() { return fIsPickingUp; }    
public final void recycle()
	{
	source = null;
	fPlayer = null;
	fItem = null;
	gCachedEvent = this;
	}
  public final void setItem(GenericItem item) { fItem = item; }    
}