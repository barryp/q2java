package barryp.widgetwar;

import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

import barryp.widgetwar.spawn.*;

/**
 * Group players together into two teams.
 *
 * @author Barry Pederson - based on CTF Team class originated by Menno van Gangelen
 */

public class Team implements GameStatusListener, PlayerStateListener, DamageListener
	{
	public static final String CTF_TEAM1_SKIN = "ctf_r";
	public static final String CTF_TEAM2_SKIN = "ctf_b";

	public  static Team    TEAM1 = new Team( 1 );
	public  static Team    TEAM2 = new Team( 2 );

	protected final static int MAX_PARTIAL = 3;

	private class RespawnHelper implements ServerFrameListener, CrossLevel
		{
		private boolean fIsRunning;

		public void setRunning(boolean b)
			{
			if (b && (!fIsRunning))
				{
				Game.addServerFrameListener(this, 0, 0);
				fIsRunning = b;
				return;
				}

			if ((!b) && fIsRunning)
				{
				Game.removeServerFrameListener(this);
				fIsRunning = b;
				return;
				}
			}
			
		public void runFrame(int phase)
			{
			ponderRespawn();
			}
		}
	
	protected Integer     fTeamIndex;

	protected int		  fCaptureSoundIndex;
	protected int 		  fCarrierEffect;
	protected int 		  fTeamIcon;
	
	protected Vector      fPlayers;
	protected Vector	  fSpawnQueue;
	protected RespawnHelper fRespawnHelper;
	protected Vector[]	  fTechnologies;        // technologies we totally own
	protected Vector	  fAllTechnologies;     // all technologies, both complete and incomplete
	protected Vector	  fPartialTechnologies; // incomplete technologies only

	private Team		  fOtherTeam;
	private TeamBase 	  fTeamBase;
	
	private float 	  fTeamScore;

	static
		{
		TEAM1.fOtherTeam = TEAM2;
		TEAM2.fOtherTeam = TEAM1;
		}

	
/**
 * Private constructor so it can't be instantiated
 */
private Team( int teamIndex )
	{
	fTeamIndex      = new Integer(teamIndex);
	fPlayers        = new Vector();
	fSpawnQueue 	= new Vector();
	fRespawnHelper	= new RespawnHelper();
	Game.addGameStatusListener( this );

	// build the technology lists
	fTechnologies = new Vector[3];
	fTechnologies[0] = new Vector();
	fTechnologies[1] = new Vector();
	fTechnologies[2] = new Vector();
	
	fAllTechnologies = new Vector();
	fPartialTechnologies = new Vector();

	// setup a few team constants
	switch (teamIndex)
		{
		case 1:
			fCarrierEffect = NativeEntity.EF_FLAG1;
			fTeamIcon = Engine.getImageIndex("i_ctf1");
			break;
			
		case 2:
			fCarrierEffect = NativeEntity.EF_FLAG2;
			fTeamIcon = Engine.getImageIndex("i_ctf2");
			break;
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

	int index1 = ( this == Team.TEAM1 ? WidgetWarrior.STAT_CTF_JOINED_TEAM1_PIC : WidgetWarrior.STAT_CTF_JOINED_TEAM2_PIC );
	int index2 = ( this == Team.TEAM1 ? WidgetWarrior.STAT_CTF_JOINED_TEAM2_PIC : WidgetWarrior.STAT_CTF_JOINED_TEAM1_PIC );
	int picnum = Engine.getImageIndex("i_ctfj");
	p.fEntity.setPlayerStat( index1, (short)picnum );
	p.fEntity.setPlayerStat( index2, (short)0      );	
	}
/**
 * Called when a player brings back some fragments of technology to the team base.
 * @param st What the player stole
 * @param ww Player who made the capture.
 */
public void addStolenTechnology(StolenTechnology st, WidgetWarrior ww) 
	{
	// keep some number of partially captured technologies around
	while (fPartialTechnologies.size() < MAX_PARTIAL)
		{
		Team t = (Team) st.getTeam();		
		Technology tech = t.getStolenTechnology(fAllTechnologies);
		if (tech == null)
			break;
		else
			{
			fPartialTechnologies.addElement(tech);
			fAllTechnologies.addElement(tech);
			}
		}

	Game.getSoundSupport().fireEvent(getTeamBase().getBaseEntity(), NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_AUTO, fCaptureSoundIndex, 1, NativeEntity.ATTN_NONE, 0);	

	// give credit for the capture
	setTeamScore(getTeamScore() + st.getFragment());
	
	// check if there are any technologies left to steal
	int n = fPartialTechnologies.size();
	if (n < 1)
		{
		printTeam(ww.getName() + " captured some enemy technology!\n");
		return;
		}

	// pick one of the incomplete technologies the team has captured so far
	int i = GameUtil.randomInt(fPartialTechnologies.size());
	Technology tech = (Technology) fPartialTechnologies.elementAt(i);

	// add another piece captured and see if we've got the whole thing yet (or very close)
	tech.incFragmentsCaptured(st.getFragment());
//	teamStuffCommand("play ctf/flagcap.wav");

	if (tech.getFragmentsCaptured() < 1.0F)
		printTeam(ww.getName() + " captured a " + ((int)(st.getFragment()*100)) + "% piece of " + tech.getName()+ " technology!\n");		
	else
		{
		// add to completed tech list, and remove from partial list
		fTechnologies[tech.getTechType()].addElement(tech);			
		fPartialTechnologies.removeElementAt(i);
		
		// let the team know what was nabbed
		printTeam(ww.getName() + " captured the final piece of " + tech.getName()+ " technology!\n");
		}
	}
/**
 * Add a technology to a team's inventory.
 * @param t The Technology that was aquired
 * @param announcement What if anything that should be announced to the team.
 */
public void addTechnology(Technology t, String announcement) 
	{
	// mark the tech as being completely owned
	t.incFragmentsCaptured(1.0F);

	// add it to our lists
	fTechnologies[t.getTechType()].addElement(t);	
	fAllTechnologies.addElement(t);

	if (announcement != null)
		printTeam(announcement);
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
	}
public void gameStatusChanged(GameStatusEvent e)
	{
	if (e.getState() == GameStatusEvent.GAME_PRESPAWN)
		{
		// clear the technology lists
		fTechnologies[0].removeAllElements();
		fTechnologies[1].removeAllElements();
		fTechnologies[2].removeAllElements();

		fAllTechnologies.removeAllElements();
		fPartialTechnologies.removeAllElements();
		}

	if (e.getState() == GameStatusEvent.GAME_POSTSPAWN)
		{
		fCaptureSoundIndex = Engine.getSoundIndex("ctf/flagcap.wav");
		}
	}
/**
 * Get which effect should be used when a team member is carrying enemy tech.
 * @return int
 */
public int getCarrierEffect() 
	{
	return fCarrierEffect;
	}
	public String getDeathmatchScoreboardMessage( GameObject victim, GameObject killer, boolean inIntermission ) 
	{
		int xOffset, statHeader, statCaps, headerIndex;
		String s;
		Team otherTeam;
		
		otherTeam   = ( this == TEAM1 ? TEAM2 : TEAM1 );
		xOffset     = ( this == TEAM1 ? 0 : 160 );
		statHeader  = ( this == TEAM1 ? WidgetWarrior.STAT_CTF_TEAM1_HEADER          : WidgetWarrior.STAT_CTF_TEAM2_HEADER          );
		headerIndex = ( this == TEAM1 ? Engine.getImageIndex("ctfsb1") : Engine.getImageIndex("ctfsb2") );
		statCaps    = ( this == TEAM1 ? WidgetWarrior.STAT_CTF_TEAM1_CAPS            : WidgetWarrior.STAT_CTF_TEAM2_CAPS            );

		// display our teamheader
		victim.fEntity.setPlayerStat( statHeader, (short)headerIndex );	

		// if during intermission, we must blink our header if we're the winning team (or tie)
		if ( inIntermission && ((int)Game.getGameTime()%2 == 0 ) )	// blink every second
			{
			if ( this.getTeamScore() >= otherTeam.getTeamScore() )
				victim.fEntity.setPlayerStat( statHeader, (short)0 );	
			}

		s = "if " + statHeader + " xv " + (8+xOffset) + " yv 8 pic " + statHeader + " endif " +
			"xv " + (40+xOffset) + " yv 28 string \"" + ((int)getTeamScore()) + "/" + 0 /*fCaptures*/ + "\" " +
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
/*
			GenericFlag flag = (GenericFlag)p.getInventory( "flag" );

			if ( flag != null )	// flag IS other teams flag...
			{
				s += "xv " + (56+xOffset) + " picn " + flag.getSmallIconName() + " ";
			}
*/	
		}

		if ( players.length > 8 )
		{
			s += "xv " + (8+xOffset) + " yv " + (42+8*8) + " string \"...and " + (players.length-8) + " more\" ";
		}

		return s;
		
	}
/**
 * Get the number of live (non-dead) players on the team.
 * @return int
 */
public int getNumLivePlayers() 
	{
	int result = 0;
	int n = fPlayers.size();
	for (int i = 0; i < n; i++)
		{
		WidgetWarrior ww = (WidgetWarrior) fPlayers.elementAt(i);
		if (!ww.isDead())
			result++;			
		}
		
	return result;
	}
public int getNumPlayers()
	{
	return fPlayers.size();
	}
public WidgetWarrior[] getPlayers()
	{
	WidgetWarrior[] players = new WidgetWarrior[ fPlayers.size() ];
	fPlayers.copyInto( players );
	return players;
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
 * Figure out what technology the enemy team will 
 * steal, given a list of what they already have.
 *
 * @return barryp.widgetwar.Technology
 * @param enemyTechnologies java.util.Vector
 */
public Technology getStolenTechnology(Vector enemyTechnologies) 
	{
	// build up a list of technologies the other team doesn't have
	// and figure out the total number of times these technologies have been used (+1)
	Vector v = new Vector();
	int total = 0;
	Enumeration enum = fAllTechnologies.elements();
	while (enum.hasMoreElements())
		{
		Technology myTech = (Technology) enum.nextElement();

		// skip the empty technologies
		if (myTech.getClass() == null)
			continue;

		// skip technologies we don't completely own
		if (myTech.getFragmentsCaptured() < 1.0F)
			continue;
			
		if (!(enemyTechnologies.contains(myTech)))
			{
			v.addElement(myTech);
			total += myTech.getCounter() + 1;
			}
		}

	// no technologies available to steal
	if (v.size() == 0)
		return null;
		
	// make a random pick of which technology to return
	// weighted towards the most used technologies
	int i = GameUtil.randomInt(total);
	enum = v.elements();
	total = 0;
	while (enum.hasMoreElements())
		{
		Technology myTech = (Technology) enum.nextElement();
		total += myTech.getCounter() + 1;
		if (total > i)
			return new Technology(myTech);
		}

	// this shouldn't be reached..but just in case...
	return new Technology((Technology) v.elementAt(0));
	}
/**
 * Get the point where the team's CD should appear.
 * @return javax.vecmath.Point3f
 */
public TeamBase getTeamBase() 
	{
	return fTeamBase;
	}
/**
 * Get the index of the image used for the team icon.
 * @return int
 */
public int getTeamIcon() 
	{
	return fTeamIcon;
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
 * Get how much tech the team has captured.
 * @return float
 */
public float getTeamScore() 
	{
	return fTeamScore;
	}
/**
 * Fetch a particular technology from the Team's list.
 * @return barryp.widgetwar.Technology
 * @param techType int
 * @param n int
 */
public Technology getTechnology(int techType, int n) 
	{
	return (Technology) fTechnologies[techType].elementAt(n);
	}
/**
 * Get the list of technologies the team knows about, in a given category.
 * @return Vectory
 * @param index int
 */
public Vector getTechnologyList(int index) 
	{
	return fTechnologies[index];
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
 */
public void playerStateChanged(PlayerStateEvent pse)
	{
	if (pse.getStateChanged() == PlayerStateEvent.STATE_INVALID)
		removePlayer((Player)pse.getPlayer());
	}
/**
 * Think about respawning dead team members.
 */
public void ponderRespawn() 
	{
	int otherSize = fOtherTeam.fPlayers.size();
	
	while ((fSpawnQueue.size() > 0) 
	&& ((otherSize == 0) || (getNumLivePlayers() < otherSize)))
		{
		WidgetWarrior ww = (WidgetWarrior) fSpawnQueue.elementAt(0);
		fSpawnQueue.removeElementAt(0);

		if (fSpawnQueue.size() == 0)
			fRespawnHelper.setRunning(false);
			
		ww.respawnTeam();
		}
	}
/**
 * Broadcast a message to the team.
 * @param message java.lang.String
 */
public void printTeam(String message) 
	{
	Game.getPrintSupport().fireEvent(PrintEvent.PRINT_TALK_TEAM, Engine.PRINT_HIGH, null, null, this, message);		
	}
public boolean removePlayer( Player p )
	{
	if (!fPlayers.contains(p))
		return false;
			
	//update players stats that he leaved this team (the yellow line around team-icon)
//	p.fEntity.setPlayerStat( STAT_CTF_JOINED_TEAM1_PIC, (short)0 );
//	p.fEntity.setPlayerStat( STAT_CTF_JOINED_TEAM2_PIC, (short)0 );
		
	p.removePlayerStateListener(this);
	p.removeDamageListener(this);
	
	fSpawnQueue.removeElement(p);
	if (fSpawnQueue.size() == 0)
		fRespawnHelper.setRunning(false);
		
	return fPlayers.removeElement( p );
	}
/**
 * Called by a WidgetWarrior when it wants to enter the game.
 * @param ww barryp.widgetwar.WidgetWarrior
 */
public void respawn(WidgetWarrior ww) 
	{
	fSpawnQueue.addElement(ww);
	fRespawnHelper.setRunning(true);
	}
/**
 * Set the location of where the Team's DataCD appears.
 * @param p javax.vecmath.Point3f
 */
public void setTeamBase(TeamBase tb) 
	{
	fTeamBase = tb;
	}
/**
 * Set how much tech this team has captured.
 * @param f float
 */
public void setTeamScore(float f) 
	{
	fTeamScore = f;

	short s = (short)(f * 100);
	//inform all players (also spectators) of the current score
	int index = ( this == Team.TEAM1 ? WidgetWarrior.STAT_CTF_TEAM1_CAPS : WidgetWarrior.STAT_CTF_TEAM2_CAPS );

	Enumeration enum = NativeEntity.enumeratePlayerEntities();
	while ( enum.hasMoreElements() )
		{
		NativeEntity p = (NativeEntity)enum.nextElement();
		p.setPlayerStat( index, s);
		}	
	}
/**
 * Play a sound just for this team.
 * @param s java.lang.String
 */
public void teamStuffCommand0(String cmd) 
	{
	int n = fPlayers.size();
	for (int i = 0; i < n; i++)
		{
		Player p = (Player) fPlayers.elementAt(i);
		GameUtil.stuffCommand(p.fEntity, cmd);
		}
	}
}