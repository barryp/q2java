package barryp.widgetwar;

/**
 * Interface for objects representing Stolen technology.
 *
 * @author Barry Pederson
 */
public interface StolenTechnology 
	{
	
/**
 * Get how much technology this item is carrying.
 * @return float
 */
float getFragment();
/**
 * This method was created in VisualAge.
 * @return barryp.widgetwar.Team
 */
Team getTeam();
/**
 * Called when a player captures an item or recovers a stolen item.
 * @param ww barryp.widgetwar.WidgetWarrior
 */
void release(WidgetWarrior ww);
}