package barryp.widgetwar.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;

import barryp.widgetwar.*;

/**
 * Home base for the Red team.
 *
 * @author Barry Pederson
 */

public class item_flag_team1 extends TeamBase
	{	
	
public item_flag_team1( Element spawnArgs ) throws GameException
	{
	super(spawnArgs);	
	}
/**
 * Get which team this base belongs to.
 * @return barryp.widgetwar.Team
 */
public Team getTeam() 
	{
	return Team.TEAM1;
	}
}