package menno.ctf;

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


import java.awt.Rectangle;
import java.util.*;
import q2java.*;
import q2jgame.*;



public class CTFMenu extends GenericMenu
{
	static String[] cmdTeamRed   = { "team", "red"  };
	static String[] cmdTeamBlue  = { "team", "blue" };
	static String[] cmdChasecam  = { "chasecam"     };
	static String[] cmdSpectator = { "spectator"    };

	static String[] header = { "===== Q2JAVA CTF v0.5 =====",
	                           ""
							 };
	static String[] footer = { "",
	                           "Press:",
							   "[ and ] to move cursor",
							   "ENTER to select",
							   "TAB to return",
							   "",
							   "Author: Menno van Gangelen",
							  };


	public CTFMenu( Player owner, GenericMenu lastMenu )
	{
		super( owner, lastMenu );
		setHeader( header );

		String[] item0  = { "Join Red Team",  "  Players: " + Team.TEAM1.getNumPlayers() };
		String[] item1  = { "Join Blue Team", "  Players: " + Team.TEAM2.getNumPlayers() };
		String[] item21 = { "Chase Camera", "  (leaves team)" };
		String[] item22 = { "Leave Chase Camera" };
		String[] item3  = { "Spectator",    "  (leaves team)" };
		String[] item4  = { "Credits" };

		addMenuItem( item0 );
		addMenuItem( item1 );
		if ( owner.isChasing() )
			addMenuItem( item22 );
		else
			addMenuItem( item21 );
		addMenuItem( item3 );
		addMenuItem( item4 );

		setFooter( footer );
	}
	public void select()
	{
		switch ( fSelectedItem )
		{
		case 0:		fOwner.cmd_team( cmdTeamRed );
					close();
					break;
		case 1:		fOwner.cmd_team( cmdTeamBlue );
					close();
					break;
		case 2:		fOwner.cmd_chasecam( cmdChasecam );
					close();
					break;
		case 3:		fOwner.cmd_spectator( cmdSpectator );
					close();
					break;
		default:	fOwner.fEntity.cprint( Engine.PRINT_HIGH, "Not implemented yet" );
		}
	}
	public void show()
	{
		//show the background
		String s = "xv 32 yv 8 picn inventory";

		show( s, new Rectangle(50, 25, 222, 156) );
	}
}