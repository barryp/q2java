package q2java.ctf;

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


import java.util.*;
import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;
import q2java.ctf.spawn.*;

/**
 * Group players together into two teams.
 */


public class Team implements GameStatusListener, PlayerStateListener, DamageListener
{
	public static final String CTF_TEAM1_SKIN = "ctf_r";
	public static final String CTF_TEAM2_SKIN = "ctf_b";

	public static final int STAT_CTF_TEAM1_PIC        = 17;
	public static final int STAT_CTF_TEAM1_CAPS       = 18;
	public static final int STAT_CTF_TEAM2_PIC        = 19;
	public static final int STAT_CTF_TEAM2_CAPS       = 20;
	public static final int STAT_CTF_JOINED_TEAM1_PIC = 22;
	public static final int STAT_CTF_JOINED_TEAM2_PIC = 23;
	public static final int STAT_CTF_TEAM1_HEADER     = 24;
	public static final int STAT_CTF_TEAM2_HEADER     = 25;

	public  static Team    TEAM1 = new Team( 1 );
	public  static Team    TEAM2 = new Team( 2 );

	protected Integer     fTeamIndex;
	protected GenericFlag fFlag;
	protected Vector      fPlayers;

	protected int         fCaptures;

	//===================================================
	// Constructor (private, so cannot be instanciated)
	//===================================================
	private Team( int teamIndex )
	{
		fTeamIndex      = new Integer(teamIndex);
		fPlayers        = new Vector();
		Game.addGameStatusListener( this );
	}
	public void addCapture( CTFPlayer capturer )
	{
		fCaptures++;

		// add bonusses to team-members and update hud
		CTFPlayer[] players = getPlayers();
		for ( int i=0; i<players.length; i++ )
		{
			players[i].setScore( players[i].getScore() + CTFPlayer.CTF_TEAM_BONUS );
		}

		// Add extra to carrier
		capturer.setScore( capturer.getScore() + CTFPlayer.CTF_CAPTURE_BONUS - CTFPlayer.CTF_TEAM_BONUS );

		//inform all players (also spectators) that our captures increased
		int index = ( this == Team.TEAM1 ? STAT_CTF_TEAM1_CAPS : STAT_CTF_TEAM2_CAPS );

		Enumeration enum = NativeEntity.enumeratePlayerEntities();
		while ( enum.hasMoreElements() )
		{
			NativeEntity p = (NativeEntity)enum.nextElement();
			//p.setPlayerStat( fStatCapsIndex, (short)fCaptures );
			p.setPlayerStat( index, (short)fCaptures );
		}

	}
	public void addPlayer( Player p )
	{
		if (fPlayers.contains(p))
			return; // already joined this team
			
		fPlayers.addElement( p );

		// make sure we find out if the team member disconnects
		p.addPlayerStateListener(this);

		// act as a damage filter for this member
		p.addDamageListener(this);

		// assign new skin
		assignSkinTo( p );
		Object[] args = {p.getName(), fTeamIndex};
		Game.localecast("q2java.ctf.CTFMessages", "join_team", args, Engine.PRINT_HIGH);	
		
		//update players stats that he joined this team (the yellow line around team-icon)
		int index1 = ( this == Team.TEAM1 ? STAT_CTF_JOINED_TEAM1_PIC : STAT_CTF_JOINED_TEAM2_PIC );
		int index2 = ( this == Team.TEAM1 ? STAT_CTF_JOINED_TEAM2_PIC : STAT_CTF_JOINED_TEAM1_PIC );
		int picnum = Engine.getImageIndex("i_ctfj");
		p.fEntity.setPlayerStat( index1, (short)picnum );
		p.fEntity.setPlayerStat( index2, (short)0      );
	}
	public void assignSkinTo( Player p )
	{
		String skin;

		//System.out.println( p.getUserInfo("skin") );

		// assign skin based on team
		skin =  ( p.isFemale() ? "female/" : "male/" );
		skin += ( this == TEAM1 ? CTF_TEAM1_SKIN : CTF_TEAM2_SKIN );
		Engine.setConfigString( Engine.CS_PLAYERSKINS + p.fEntity.getPlayerNum(), p.getName() + "\\" + skin );
		//Engine.debugLog( p.getName() + "\\" + skin );
	}
	public static void blinkDeathmatchScoreboard( Player client )
	{
		// if during intermission, we must blink our header if we're the winning team (or tie)
		//if ( inIntermission && ((int)Game.getGameTime()%2 == 0 ) )	// blink every second
		if ( (int)Game.getGameTime()%2 == 0 )	// blink every second
		{
			if ( TEAM1.getCaptures() > TEAM2.getCaptures() )
				client.fEntity.setPlayerStat( STAT_CTF_TEAM1_HEADER, (short)0 );
			else if ( TEAM2.getCaptures() > TEAM1.getCaptures() )
				client.fEntity.setPlayerStat( STAT_CTF_TEAM2_HEADER, (short)0 );
			// Capture tie, check total frags
			else if ( TEAM1.getScore() > TEAM2.getScore() )
				client.fEntity.setPlayerStat( STAT_CTF_TEAM1_HEADER, (short)0 );
			else if ( TEAM2.getScore() > TEAM1.getScore() )
				client.fEntity.setPlayerStat( STAT_CTF_TEAM2_HEADER, (short)0 );
			else
			{	// tie game
				client.fEntity.setPlayerStat( STAT_CTF_TEAM1_HEADER, (short)0 );
				client.fEntity.setPlayerStat( STAT_CTF_TEAM2_HEADER, (short)0 );
			}
		}
		else
		{
			// show both if not blinked
			client.fEntity.setPlayerStat( STAT_CTF_TEAM1_HEADER, (short)Engine.getImageIndex("ctfsb1") );
			client.fEntity.setPlayerStat( STAT_CTF_TEAM2_HEADER, (short)Engine.getImageIndex("ctfsb2") );
		}
	}
	/**
	 * Filter a team member's damage.
	 * @param DamageObject - damage to be filtered.
	 */
	public void damageOccured(DamageEvent damage)
	{
		// check for self-inflicted damage
		if (damage.getVictim() == damage.getAttacker())
			return; // they deserve what they get..no help from us
			
		// check if the attacker also belongs to this team
		if (isTeamMember(damage.getAttacker()))
		{
			damage.fAmount = 0;
			return;  // give the guy a break
		}
		
		if ((damage.getAttacker() instanceof Player) && ((Player)damage.getVictim()).isCarrying("flag"))
		{
			// A CTF Player other than ourselves attacked us and we have the flag			
			// mark the attacker that he was aggressive to the flag-carrier.
			CTFPlayer p = (CTFPlayer)(damage.getAttacker());
			p.fLastCarrierHurt = Game.getGameTime();
		}		
	}
	public void gameStatusChanged(GameStatusEvent e)
	{
		if (e.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
			// a new Level has been started, 
			// reset the captures and the flags, cause every level has it's own flags...
			fCaptures = 0;
			fFlag     = null;		
		}
	}
	/**
	* Returns the origin of the base of this team.
	* This returns null if the base has no flag !!!
	**/
	public Point3f getBaseOrigin()
	{
		if ( fFlag != null )
			return fFlag.getBaseOrigin();
		else
			return null;
	}
	public int getCaptures()
	{
		return fCaptures;
	}
	public String getDeathmatchScoreboardMessage( GameObject victim, GameObject killer, boolean inIntermission ) 
	{
		int xOffset, statHeader, statCaps, headerIndex;
		String s;
		Team otherTeam;
		
		otherTeam   = ( this == TEAM1 ? TEAM2 : TEAM1 );
		xOffset     = ( this == TEAM1 ? 0 : 160 );
		statHeader  = ( this == TEAM1 ? STAT_CTF_TEAM1_HEADER          : STAT_CTF_TEAM2_HEADER          );
		headerIndex = ( this == TEAM1 ? Engine.getImageIndex("ctfsb1") : Engine.getImageIndex("ctfsb2") );
		statCaps    = ( this == TEAM1 ? STAT_CTF_TEAM1_CAPS            : STAT_CTF_TEAM2_CAPS            );

		// display our teamheader
		victim.fEntity.setPlayerStat( statHeader, (short)headerIndex );	

/*		// if during intermission, we must blink our header if we're the winning team (or tie)
		if ( inIntermission && ((int)Game.getGameTime()%2 == 0 ) )	// blink every second
		{
			if ( this.getCaptures() > otherTeam.getCaptures() )
				victim.fEntity.setPlayerStat( statHeader, (short)0 );	
			// Capture tie, check total frags
			else if ( this.getScore() >= otherTeam.getScore() )
				victim.fEntity.setPlayerStat( statHeader, (short)0 );	
		}
*/
		s = "if " + statHeader + " xv " + (8+xOffset) + " yv 8 pic " + statHeader + " endif " +
			"xv " + (40+xOffset) + " yv 28 string \"" + getScore() + "/" + fCaptures + "\" " +
			"xv " + (98+xOffset) + " yv 12 num 2 " + statCaps + " ";

		// TODO: sort players by score
		Player[] players = getPlayers();
	/*	int begin = 0;
		int i     = begin;
		while ( i < players.length-1 )
		{
			if ( players[i].getScore() < players[i+1].getScore() )
			{
				Player dummy = players[i];
				players[i]   = players[i+1];
				players[i+1] = dummy;
			}
			if ( i == players.length-2 )
				i = ++begin;
		}
*/
		for ( int i=0; i<8 && i<players.length; i++ )
		{
			Player p    = players[i];
			int    ping = Math.min( p.fEntity.getPlayerPing(), 999 );

			s += "ctf " + xOffset + " " + (42+i*8) + " " + p.fEntity.getPlayerNum() + " " +
			     p.getScore() + " " +
				 ping + " ";

			GenericFlag flag = (GenericFlag)p.getInventory( "flag" );

			if ( flag != null )	// flag IS other teams flag...
			{
				s += "xv " + (56+xOffset) + " picn " + flag.getSmallIconName() + " ";
			}
		}

		if ( players.length > 8 )
		{
			s += "xv " + (8+xOffset) + " yv " + (42+8*8) + " string \"...and " + (players.length-8) + " more\" ";
		}

		return s;
		
	}
	public GenericFlag getFlag()
	{
		return fFlag;
	}
	public int getNumPlayers()
	{
		return fPlayers.size();
	}
	public CTFPlayer[] getPlayers()
	{
		CTFPlayer[] players = new CTFPlayer[ fPlayers.size() ];
		fPlayers.copyInto( players );
		return players;
	}
	public int getScore()
	{
		int score = 0;
		Player[] players = getPlayers();

		for ( int i=0; i<players.length; i++ )
			score += players[i].getScore();

		return score;
	}
	/**
	* This method finds a ctf spawnpoint for the TEAM,
	* but NOT the two points closest to other players.
	**/
	public GenericSpawnpoint getSpawnpoint()
	{
		GenericSpawnpoint spawnPoint = null;
		GenericSpawnpoint spot1 = null;
		GenericSpawnpoint spot2 = null;
		float range1 = Float.MAX_VALUE;
		float range2 = Float.MAX_VALUE;
		int count = 0;
		String regKey;

		regKey = ( this == TEAM1 ? info_player_team1.REGISTRY_KEY : info_player_team2.REGISTRY_KEY );

		// find the two ctf-team spawnpoints that are closest to any players
		Vector list = Game.getLevelRegistryList( regKey );
		Enumeration enum = list.elements();
		while (enum.hasMoreElements())
		{
			count++;
			spawnPoint = (GenericSpawnpoint) enum.nextElement();
			float range = q2java.baseq2.MiscUtil.nearestPlayerDistance(spawnPoint);

			if (range < range1)
			{
				range1 = range;
				spot1 = spawnPoint;
			}		
			else
			{
				if (range < range2)
				{
					range2 = range;
					spot2 = spawnPoint;
				}
			}			
		}

		if (count == 0)
			return null;
			
		if (count <= 2)
			spot1 = spot2 = null;
		else
			count -= 2;			

		int selection = (GameUtil.randomInt() & 0x0fff) % count;
		spawnPoint = null;

		enum = list.elements();
		while (enum.hasMoreElements())
		{
			spawnPoint = (GenericSpawnpoint) enum.nextElement();
			
			// skip the undesirable spots
			if ((spawnPoint == spot1) || (spawnPoint == spot2))
				continue;
				
			if ((selection--) == 0)					
				break;
		}
		return spawnPoint;
	}
	/**
	 * Get the index number of this team (1=Red, 2=Blue).
	 * @return java.lang.Integer
	 */
	public Integer getTeamIndex() 
	{
		return fTeamIndex;
	}
	/**
	 * Check if a player belongs to this team.
	 * @return boolean
	 * @param p The player we're checking on.
	 */
	public boolean isTeamMember(Object obj) 
	{
		return fPlayers.contains(obj);
	}
	/**
	 * Called when a player dies or disconnects.
	 * @param wasDisconnected true on disconnects, false on normal deaths.
	 */
	public void playerStateChanged(PlayerStateEvent pse)
	{
		if (pse.getStateChanged() == PlayerStateEvent.STATE_INVALID)
			removePlayer((Player)pse.getPlayer());
	}
	public boolean removePlayer( Player p )
	{
		if (!fPlayers.contains(p))
			return false;
			
		//update players stats that he leaved this team (the yellow line around team-icon)
		p.fEntity.setPlayerStat( STAT_CTF_JOINED_TEAM1_PIC, (short)0 );
		p.fEntity.setPlayerStat( STAT_CTF_JOINED_TEAM2_PIC, (short)0 );
		
		p.removePlayerStateListener(this);
		p.removeDamageListener(this);
		return fPlayers.removeElement( p );
	}
	//===================================================
	// Methods
	//===================================================

	public void setFlag( GenericFlag flag )
	{
		fFlag = flag;
	}
}