package q2java.ctf.spawn;

/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.ctf.*;


/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class item_flag_team1 extends GenericFlag
{
	
	public item_flag_team1( Element spawnArgs ) throws GameException
	{
		super( spawnArgs, 1 );  // red flag is "1" in messages		
	}
	/**
	 * Get the effects this flag causes on the player.
	 * @return int
	 */
	public int getFlagEffects() 
	{
		return NativeEntity.EF_FLAG1;
	}
	/**
	 * Get the name of this flag's icon.
	 * @return java.lang.String
	 */
	public String getIconName() 
	{
		return "i_ctf1";
	}
	/**
	 * Get the name of this flag.
	 * @return java.lang.String
	 */
	public String getItemName() 
	{
		return "Red Flag";
	}
	/**
	 * Get the name of this flag's model.
	 * @return java.lang.String
	 */
	public String getModelName() 
	{
		return "players/male/flag1.md2";
	}
	/**
	 * Get the name of this flag's small icon.
	 * @return java.lang.String
	 */
	public String getSmallIconName() 
	{
		return 	"sbfctf1";
	}
	/**
	 * Get which team this flag belongs to.
	 * @return menno.ctf.Team
	 */
	public Team getTeam() 
	{
		return Team.TEAM1;
	}
}