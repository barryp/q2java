package barryp.widgetwar.control;

import q2java.baseq2.Player;
import barryp.widgetwar.WidgetWarrior;

/**
 * Track enemy players carrying stolen technologies.
 *
 * @author Barry Pederson
 */
public class EnemyCarrierTracker extends EnemyTracker 
	{
	
/**
 * Is a given player a suitable target?
 * @return boolean
 * @param p q2java.baseq2.Player
 */
public boolean isPlayerSuitable(Player p) 
	{
	return super.isPlayerSuitable(p) && ((WidgetWarrior)p).isCarryingTech();
	}
}