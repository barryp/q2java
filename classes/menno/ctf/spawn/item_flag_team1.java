package menno.ctf.spawn;

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


import q2java.*;
import q2jgame.*;
//import baseq2.*;
import javax.vecmath.*;
import menno.ctf.*;


/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class item_flag_team1 extends GenericFlag
{
	
	public item_flag_team1( String[] spawnArgs ) throws GameException
	{
		super( spawnArgs );
	}
	protected void setFields()
	{
		fModelIndex    = Engine.getModelIndex( "players/male/flag1.md2" );
		fIconIndex     = Engine.getImageIndex( "i_ctf1" );
		fSmallIconName = "sbfctf1";
		fEffects       = NativeEntity.EF_FLAG1;
		fName          = "Red Flag";
		fTeam          = Team.TEAM1;
	}
}