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

	final static String[] header = { "===== Q2Java CTF v0.6 =====", ""};
	final static String author = "Menno van Gangelen";
	public CTFMenu( Player owner, GenericMenu lastMenu )
	{
		super( owner, lastMenu );
		setHeader( header );

		ResourceGroup rg = owner.getResourceGroup();
		Object[] msgArgs = new Object[1];

		msgArgs[0] = new Integer(Team.TEAM1.getNumPlayers());
		String[] item0  = { rg.getRandomString("menno.ctf.CTFMessages", "menu_join_red"),  
						    rg.format("menno.ctf.CTFMessages", "menu_playercount", msgArgs)};

		msgArgs[0] = new Integer(Team.TEAM2.getNumPlayers());
		String[] item1  = { rg.getRandomString("menno.ctf.CTFMessages", "menu_join_blue"),  
						    rg.format("menno.ctf.CTFMessages", "menu_playercount", msgArgs)};
						    
		String leavesTeam = rg.getRandomString("menno.ctf.CTFMessages", "menu_leaves_team");
		
		String[] item3  = { rg.getRandomString("menno.ctf.CTFMessages", "menu_spectator"), leavesTeam};
		String[] item4  = { rg.getRandomString("menno.ctf.CTFMessages", "menu_credits") };

		addMenuItem( item0 );
		addMenuItem( item1 );
		if ( owner.isChasing() )
			{
			String[] item22 = { rg.getRandomString("menno.ctf.CTFMessages", "menu_stop_chase") };
			addMenuItem( item22 );
			}
		else
			{
			String[] item21 = { rg.getRandomString("menno.ctf.CTFMessages", "menu_start_chase"), leavesTeam};
			addMenuItem( item21 );
			}
		addMenuItem( item3 );
		addMenuItem( item4 );


		msgArgs[0] = author;
	    String[] footer = { "",
	                        rg.getRandomString("menno.ctf.CTFMessages", "menu_footer_press"),
							rg.getRandomString("menno.ctf.CTFMessages", "menu_footer_cursor"),
							rg.getRandomString("menno.ctf.CTFMessages", "menu_footer_enter"),
							rg.getRandomString("menno.ctf.CTFMessages", "menu_footer_tab"),
							"",
							rg.format("menno.ctf.CTFMessages", "menu_footer_author", msgArgs)
						  };
						  
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