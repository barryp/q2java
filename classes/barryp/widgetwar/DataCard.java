package barryp.widgetwar;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * Datacard dropped by players containing fragment of technology.
 *
 * @author Barry Pederson
 */
public class DataCard extends GenericItem implements StolenTechnology
	{
	private Team fTeam;

	private final static float FRAGMENT = 0.15F;
	
/**
 * Construct a DataCard belonging to a particular team.
 * @param t barryp.widgetwar.Team
 */
public DataCard(Team team) 
	{
	super();
	
	fTeam = team;
	}
/**
 * Get how much technology this card is carrying.
 * @return float
 */
public float getFragment() 
	{
	return FRAGMENT;
	}
/**
 * Get the name of the icon representing this item.
 */
public String getIconName() 
	{
	return "k_security";
	}
/**
 * Descriptive name of this item.
 */
public String getItemName() 
	{
	return "Data Card";
	}
/**
 * Name of model representing this item.
 */
public String getModelName() 
	{
	return "models/items/keys/key/tris.md2";
	}
/**
 * Get which team this card belongs to.
 */
public Team getTeam() 
	{
	return fTeam;
	}
/**
 * Called when a player captures an item or recovers a stolen item.
 * @param ww barryp.widgetwar.WidgetWarrior
 */
public void release(WidgetWarrior ww) 
	{
	// do nothing
	}
/**
 * Setup this item's NativeEntity.
 */
public void setupEntity() 
	{
	super.setupEntity();
	fEntity.setEffects(NativeEntity.EF_ROTATE);
	}
/**
 * Called if item was actually taken.
 * @param p	The Player that took this item.
 * @param itemTaken The object given to the player, may be this object or a copy.
 */
protected void touchFinish(Player p, GenericItem itemTaken) 
	{
	super.touchFinish(p, itemTaken);

	((WidgetWarrior)p).addStolenTechnology(this);
	}
}