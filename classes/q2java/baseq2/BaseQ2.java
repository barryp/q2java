package q2java.baseq2;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.vecmath.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * This class implements a Quake II Java game. All the
 * fields are static, so that objects can refer
 * to them without having to keep a reference to the solitary
 * Game object that's instantiated.
 *
 * @author Barry Pederson 
 */

public class BaseQ2 extends q2java.core.Gamelet
  implements ServerFrameListener, GameStatusListener, CrossLevel
	{	
	// handy reference to the world
	public static GameObject gWorld;
		
	// keep some bodies lying around		
	protected static CorpseQueue gCorpseQueue;

	// various CVars
	private static CVar gRunRollCVar;
	private static CVar gRunPitchCVar;	
	private static CVar gBobUpCVar;
	private static CVar gBobRollCVar;	
	private static CVar gBobPitchCVar;	
	private static CVar gRollAngleCVar;
	private static CVar gRollSpeedCVar;
	private static CVar gGravityCVar;
	private static CVar gMaxVelocityCVar;

	// CVars only this Gamelet itself needs to worry about
	private static CVar gFragLimitCVar;
	private static CVar gTimeLimitCVar;
	private static CVar gDMFlagsCVar;
	private static CVar gCheatsCVar;
	
	// Mirrored CVars
	public static float gRunRoll;
	public static float gRunPitch;	
	public static float gBobUp;
	public static float gBobRoll;	
	public static float gBobPitch;	
	public static float gRollAngle;
	public static float gRollSpeed;
	public static float gGravity;
	public static float gMaxVelocity;	

	// Mirrored CVars only the BaseQ2 gamelet looks at
	private static int   gFragLimit;
	private static float gTimeLimit;
	private static int   gDMFlags;
	private static float gCheats;
	

	// Game options
	public static boolean gIsDeathmatch;
	private static boolean gIsCheating;
	public static int gSkillLevel; // this probably isn't necessary since this is a DM-only mod, but wtf.
	
	// track level changes	
	private static float gLevelStartTime;
	private static boolean gInIntermission;
	private static double gIntermissionEndTime;
	private static boolean gChangeMapNow;
	private static String gCurrentMap;
	private static String gNextMap;
	private static String gSpawnpoint;
	
	// ----------- Constants -------------------------
	
	// deathmatch flags
	public final static int DF_NO_HEALTH		= 1;
	public final static int DF_NO_ITEMS			= 2;
	public final static int DF_WEAPONS_STAY		= 4;
	public final static int DF_NO_FALLING		= 8;
	public final static int DF_INSTANT_ITEMS	= 16;
	public final static int DF_SAME_LEVEL		= 32;
	public final static int DF_SKINTEAMS		= 64;
	public final static int DF_MODELTEAMS		= 128;
	public final static int DF_FRIENDLY_FIRE	= 256;
	public final static int DF_SPAWN_FARTHEST	= 512;
	public final static int DF_FORCE_RESPAWN	= 1024;
	public final static int DF_NO_ARMOR			= 2048;
	
	// entity spawn flags.
	public final static int SPAWNFLAG_NOT_EASY		= 0x00000100;
	public final static int SPAWNFLAG_NOT_MEDIUM	= 0x00000200;
	public final static int SPAWNFLAG_NOT_HARD		= 0x00000400;
	public final static int SPAWNFLAG_NOT_DEATHMATCH	= 0x00000800;
	public final static int SPAWNFLAG_NOT_COOP		= 0x00001000;		
	
/**
 * Create the Gamelet.
 * @param gameletName java.lang.String
 */
public BaseQ2(String gameletName) 
	{
	super(gameletName);
	}
/**
 * Check whether an entity should be inhibited because
 * of its spawnargs.
 * @return boolean
 * @param spawnArgs java.lang.String[]
 */
public static void checkInhibited(int spawnFlags) throws InhibitedException
	{
	// inhibit entities based on the spawnflags
	if (gIsDeathmatch)
		{
		if ((spawnFlags & SPAWNFLAG_NOT_DEATHMATCH) != 0)
			throw new InhibitedException("Not in deathmatch");
		}
	else
		{
		int mask = 0;
		switch (gSkillLevel)
			{
			case 0:
				mask = SPAWNFLAG_NOT_EASY;
				break;

			case 1:
				mask = SPAWNFLAG_NOT_MEDIUM;
				break;

			case 2:
			case 3:
				mask = SPAWNFLAG_NOT_HARD;
				break;
			}
			
		if ((spawnFlags & mask) != 0)
			throw new InhibitedException("Not in this skill-level");
		}
	}
/**
 * Check whether an entity should be inhibited because
 * of its spawnargs.
 * @return boolean
 * @param spawnArgs java.lang.String[]
 */
public static void checkInhibited(Element spawnArgs) throws InhibitedException
	{
	checkInhibited(GameUtil.getSpawnFlags(spawnArgs));
	}
/**
 * Make a copy of an entity to keep around for a while.
 * @param ent NativeEntity
 */
public static void copyCorpse(NativeEntity ent) 
	{
	gCorpseQueue.copyCorpse(ent);
	}
/**
 * Start a new level.
 * @param mapname Name of map we're loading
 * @param entString a -very- large string (many 10's of Kbytes) 
 *     listing all the entities that should be spawned and their parameters.
 * @param spawnPoint name of single-player spawnpoint we should use
 *      (I'm guessing this is really the name of the last map we were on).
 */
public void gameStatusChanged(GameStatusEvent e)
	{
	Engine.debugLog("BaseQ2.gameStatusChanged(" + e.getState() + ")");

	if( e.getState() != GameStatusEvent.GAME_PRESPAWN )
	    {
		return;
	    }

	Engine.debugLog("BaseQ2 initialising");

	Element root = (Element) Game.getLevelDocument().getFirstChild();
	String mapname = root.getAttribute("name");
	String spawnPoint = root.getAttribute("spawnpoint");

	gLevelStartTime = Game.getGameTime();
	gInIntermission = false;
	gChangeMapNow = false;
	gCurrentMap = mapname;
	gNextMap = mapname; // in case there isn't a target_changelevel entity in the entString
	
	if ((spawnPoint != null) && (spawnPoint.length() == 0))
		gSpawnpoint = null;
	else
		gSpawnpoint = spawnPoint;
	
	gIsCheating = (gCheats == 1.0);

	gCorpseQueue = new CorpseQueue();
	
	InventoryList.registerList();

	//
	// cache some sounds
	//
		
	Engine.getSoundIndex("player/fry.wav");	// standing in lava / slime
	Engine.getSoundIndex("player/lava1.wav");
	Engine.getSoundIndex("player/lava2.wav");

	Engine.getSoundIndex("misc/pc_up.wav");
	Engine.getSoundIndex("misc/talk1.wav");

	Engine.getSoundIndex("misc/udeath.wav");

	// gibs
	Engine.getSoundIndex("items/respawn1.wav");

	// sexed sounds
	Engine.getSoundIndex("*death1.wav");
	Engine.getSoundIndex("*death2.wav");
	Engine.getSoundIndex("*death3.wav");
	Engine.getSoundIndex("*death4.wav");
	Engine.getSoundIndex("*fall1.wav");
	Engine.getSoundIndex("*fall2.wav");	
	Engine.getSoundIndex("*gurp1.wav");		// drowning damage
	Engine.getSoundIndex("*gurp2.wav");	
	Engine.getSoundIndex("*jump1.wav");		// player jump
	Engine.getSoundIndex("*pain25_1.wav");
	Engine.getSoundIndex("*pain25_2.wav");
	Engine.getSoundIndex("*pain50_1.wav");
	Engine.getSoundIndex("*pain50_2.wav");
	Engine.getSoundIndex("*pain75_1.wav");
	Engine.getSoundIndex("*pain75_2.wav");
	Engine.getSoundIndex("*pain100_1.wav");
	Engine.getSoundIndex("*pain100_2.wav");

	//-------------------

	Engine.getSoundIndex("player/gasp1.wav");		// gasping for air
	Engine.getSoundIndex("player/gasp2.wav");		// head breaking surface, not gasping

	Engine.getSoundIndex("player/watr_in.wav");	// feet hitting water
	Engine.getSoundIndex("player/watr_out.wav");	// feet leaving water

	Engine.getSoundIndex("player/watr_un.wav");	// head going underwater
	
	Engine.getSoundIndex("player/u_breath1.wav");
	Engine.getSoundIndex("player/u_breath2.wav");

	Engine.getSoundIndex("items/pkup.wav");		// bonus item pickup
	Engine.getSoundIndex("world/land.wav");		// landing thud
	Engine.getSoundIndex("misc/h2ohit1.wav");		// landing splash

	Engine.getSoundIndex("items/damage.wav");
	Engine.getSoundIndex("items/protect.wav");
	Engine.getSoundIndex("items/protect4.wav");
	Engine.getSoundIndex("weapons/noammo.wav");

	Engine.getSoundIndex("infantry/inflies1.wav");
	
	// setup player status bar
	Engine.setConfigString (Engine.CS_STATUSBAR, Player.DM_STATUSBAR);	
	
	//
	// Setup light animation tables. 'a' is total darkness, 'z' is doublebright.
	//

	// 0 normal
	Engine.setConfigString(Engine.CS_LIGHTS+0, "m");
	
	// 1 FLICKER (first variety)
	Engine.setConfigString(Engine.CS_LIGHTS+1, "mmnmmommommnonmmonqnmmo");
	
	// 2 SLOW STRONG PULSE
	Engine.setConfigString(Engine.CS_LIGHTS+2, "abcdefghijklmnopqrstuvwxyzyxwvutsrqponmlkjihgfedcba");
	
	// 3 CANDLE (first variety)
	Engine.setConfigString(Engine.CS_LIGHTS+3, "mmmmmaaaaammmmmaaaaaabcdefgabcdefg");
	
	// 4 FAST STROBE
	Engine.setConfigString(Engine.CS_LIGHTS+4, "mamamamamama");
	
	// 5 GENTLE PULSE 1
	Engine.setConfigString(Engine.CS_LIGHTS+5,"jklmnopqrstuvwxyzyxwvutsrqponmlkj");
	
	// 6 FLICKER (second variety)
	Engine.setConfigString(Engine.CS_LIGHTS+6, "nmonqnmomnmomomno");
	
	// 7 CANDLE (second variety)
	Engine.setConfigString(Engine.CS_LIGHTS+7, "mmmaaaabcdefgmmmmaaaammmaamm");
	
	// 8 CANDLE (third variety)
	Engine.setConfigString(Engine.CS_LIGHTS+8, "mmmaaammmaaammmabcdefaaaammmmabcdefmmmaaaa");
	
	// 9 SLOW STROBE (fourth variety)
	Engine.setConfigString(Engine.CS_LIGHTS+9, "aaaaaaaazzzzzzzz");
	
	// 10 FLUORESCENT FLICKER
	Engine.setConfigString(Engine.CS_LIGHTS+10, "mmamammmmammamamaaamammma");

	// 11 SLOW PULSE NOT FADE TO BLACK
	Engine.setConfigString(Engine.CS_LIGHTS+11, "abcdefghijklmnopqrrqponmlkjihgfedcba");
	
	// styles 32-62 are assigned by the light program for switchable lights

	// 63 testing
	Engine.setConfigString(Engine.CS_LIGHTS+63, "a");				
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return Player.class;
	}
/**
 * Get the name of the spawnpoint we're supposed to use (single-player).
 * @return java.lang.String
 */
public static String getSpawnpoint() 
	{
	return gSpawnpoint;
	}
/**
 * Describe this Game.
 * @return java.lang.String
 */
public static String getVersion() 
	{
	return "Q2Java Base Game, v0.9.4";
	}
/**
 * Initialize this gamelet.
 */
public void init() 
	{
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 10.0F);
	Game.addServerFrameListener(this, Game.FRAME_MIDDLE, 0, 0);
	Game.addGameStatusListener(this);
	
	//leighd 04/10/99 - need to register package path for spawning.
	Game.addPackagePath("q2java.baseq2");
	
	// load cvars
	gRunRollCVar = new CVar("run_roll", "0.005", 0);	
	gRunPitchCVar = new CVar("run_pitch", "0.002", 0);	
	gBobUpCVar = new CVar("bob_up", "0.005", 0);	
	gBobRollCVar = new CVar("bob_roll", "0.002", 0);	
	gBobPitchCVar = new CVar("bob_pitch", "0.002", 0);	
	gRollAngleCVar = new CVar("sv_rollangle", "2", 0);
	gRollSpeedCVar = new CVar("sv_rollspeed", "200", 0);	
	gGravityCVar = new CVar("sv_gravity", "800", 0);	
	gMaxVelocityCVar = new CVar("sv_maxvelocity", "2000", 0);
	
	gFragLimitCVar = new CVar("fraglimit", "0", CVar.CVAR_SERVERINFO);
	gTimeLimitCVar = new CVar("timelimit", "0", CVar.CVAR_SERVERINFO);
	gDMFlagsCVar = new CVar("dmflags", "0", CVar.CVAR_SERVERINFO);
	gCheatsCVar = new CVar("cheats", "0", CVar.CVAR_LATCH);
	
	gIsDeathmatch = (new CVar("deathmatch", "0", CVar.CVAR_LATCH)).getFloat() == 1.0;
	gSkillLevel = (int) ((new CVar("skill", "1", CVar.CVAR_LATCH)).getFloat());		
	
	InventoryList.setupList( 42, 84, 128, 172, 214, true);	
	
	new CVar("locale", "en_US", CVar.CVAR_USERINFO | CVar.CVAR_ARCHIVE);
	}
/**
 * Check whether or not the Cheating option is on.
 * @return boolean
 */
public static boolean isCheating() 
	{
	return gIsCheating;
	}
/**
 * Check whether a given deathmatch flag is set.  Use the Game.DF_* constants.
 * @return true if the flag is set, false if not.
 */
public static boolean isDMFlagSet(int flag) 
	{
	return (gDMFlags & flag) != 0;
	}
/**
 * Called by the DLL when the DLL's RunFrame() function is called.
 */
public void runFrame(int phase)
	{
	switch (phase)
		{
		case Game.FRAME_BEGINNING:
			// mirror various CVars
			gRunRoll	= gRunRollCVar.getFloat();
			gRunPitch	= gRunPitchCVar.getFloat();	
			gBobUp		= gBobUpCVar.getFloat();
			gBobRoll	= gBobRollCVar.getFloat();	
			gBobPitch	= gBobPitchCVar.getFloat();	
			gRollAngle	= gRollAngleCVar.getFloat();
			gRollSpeed	= gRollSpeedCVar.getFloat();
			gGravity	= gGravityCVar.getFloat();
			gMaxVelocity= gMaxVelocityCVar.getFloat();

			gFragLimit	= (int) gFragLimitCVar.getFloat();
			gTimeLimit	= gTimeLimitCVar.getFloat();
			gDMFlags	= (int) gDMFlagsCVar.getFloat();
			gCheats		= gCheatsCVar.getFloat();			
			break;
			
		case Game.FRAME_MIDDLE:
			if (gInIntermission && (Game.getGameTime() > gIntermissionEndTime))
				{
				if (gChangeMapNow || (!isDMFlagSet(DF_SAME_LEVEL)))
					Engine.addCommandString("gamemap \"" + gNextMap + "\"\n");
				else
					Engine.addCommandString("gamemap \"" + gCurrentMap + "\"\n");
				return;
				}

			if (!gInIntermission && timeToQuit())
				startIntermission();
			break;
		}
	}
/**
 * Set what the next map will be.
 * @param mapname java.lang.String
 */
public static void setNextMap(String mapname) 
	{
	if (mapname != null)
		gNextMap = mapname;
	}
/**
 * Called by svcmd_scores
 * @author _Quinn
 * @param int num Number of spaces to return
 */

public String spaces( float spaces ) 
	{
	StringBuffer sb = new StringBuffer();
	for ( int i = 0; i <= spaces; i++ ) 
			{
			sb.append(" ");
			} // end for

	return sb.toString();
	} // end spaces
/**
 * Pick an intermission spot, and notify each player.
 */
public static void startIntermission() 
	{
	if (gInIntermission)
		return; // already in intermission

	gChangeMapNow = false; 
		
	Enumeration enum;
	Vector v;
	
	// gather list of info_player_intermission entities
	v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_intermission.REGISTRY_KEY);

	// if there weren't any intermission spots, try for info_player_start spots
	if (v.size() < 1)
		v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_start.REGISTRY_KEY);

	// still no spots found? try for info_player_deathmatch
	if (v.size() < 1)
		v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_deathmatch.REGISTRY_KEY);
		
	// randomly pick something from the list
	int i = (GameUtil.randomInt() & 0x0fff) % v.size();
	GenericSpawnpoint spot = (GenericSpawnpoint) v.elementAt(i);

	// notify each player
	enum = Player.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		p.startIntermission(spot);
		}
		
	gInIntermission = true;	
	gIntermissionEndTime = Game.getGameTime() + 5.0;	
	}
/**
 * Force a map change, but do it nicely so that players see the scoreboard.
 */
public void svcmd_changemap(String[] args) 
	{
	gChangeMapNow = true;
	
	if (args.length > 2)
		{
		gNextMap = args[2];
		}
	else
		{
		if (isDMFlagSet(DF_SAME_LEVEL))
			gNextMap = gCurrentMap;
		}
	}
/**
 * Control the Cheating option (for debugging of course!)
 */
public void svcmd_cheating(String[] args) 
	{
	if (args.length > 2)
		{
		if (args[2].equalsIgnoreCase("on"))
			gIsCheating = true;		
		else if (args[2].equalsIgnoreCase("off"))
			gIsCheating = false;
		else
			Game.dprint("Usage: sv cheating [on | off]\n");
		}

	// make sure everyone knows what the situation is.
	Game.bprint(Engine.PRINT_HIGH, "Cheating is " + (gIsCheating ? "on" : "off") + "\n");
	}
/**
 * Display help info to the console.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint(getVersion());
	Game.dprint("\n\n    sv commands:\n");
	Game.dprint("       changemap [mapname]\n");
	Game.dprint("       cheating [on | off]\n");
	Game.dprint("       scores\n");
	}
/**
 * Runs the svcmd.  For now, ignores arguments.
 * Later: { ping score time ascend descend }
 * and so on.
 * @param args java.lang.String[]
 * @author _Quinn <tmiller@haverford.edu>
 * @version 2
 */

public void svcmd_scores(String[] args) 
	{
	// _Quinn: 04/20/98: shamelessly looted from baseq2.Player
	// _Quinn: 05.04.98: made pretty printing prettier

	// Name           Score Ping Time Rate RelPing Rank
	// Rate is Score/Time
	// RelPing is relative ping, 0 to 1.0 with 1 the max.
	// Rank is Rate times RPing

	Enumeration enum = Player.enumeratePlayers();
	Vector playerData = new Vector();
	Vector players = new Vector();

	float minPing = 25000;
	float maxPing = 0;
	int i = 0;

	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		float playerD[] = new float[7];
		playerD[0] = p.getScore(); // score
		playerD[1] = p.fEntity.getPlayerPing(); // ping
		playerD[2] = ((Game.getGameTime() - p.fStartTime) / 60); //time
		playerD[3] = playerD[0] / playerD[2]; // rate
		playerD[4] = 0; // RelPing
		playerD[5] = 0; // K/M * RelPing
		playerD[6] = 15 - p.getName().length() - 1; // padding.

		if ( playerD[1] < minPing ) { minPing = playerD[1]; }
		if ( playerD[1] > maxPing ) { maxPing = playerD[1]; }

		// perform sorting based on playerD 0 to 6
		// ( constants ) later.

		playerData.addElement( playerD );
		players.addElement( p.getName() );
		} // end while loop.

		int playerCount = players.size();

	// calculate relPing.
	float range = maxPing - minPing;
	if ( range == 0 ) { range = 1; } // no division errors...
	float difference = 0;
	for ( i = 0; i < playerCount; i++ ) 
		{
		difference = ((float[])playerData.elementAt(i))[1] - minPing;
		if ( difference == 0 ) { difference = 1; } // head off zeroresults.
		((float[])playerData.elementAt(i))[4] = difference / range;
		} // end calculating loop.

	// calculate rank.
	float pD[] = new float[7];
	for ( i = 0; i < playerCount; i++ ) 
		{
		pD = (float[])playerData.elementAt(i);
		pD[5] = pD[3] * pD[4];
		playerData.setElementAt( pD, i);
		} // end rank calculation

	// later, sort based on args passed in.

	// prepare to pretty print the numbers
	DecimalFormat dfThree = new DecimalFormat( "##0" );
	DecimalFormat dfTwoDotOne = new DecimalFormat( "##.0" );
	DecimalFormat dfDotThree = new DecimalFormat( "#.0##" );
	
		// generate the pretty printing.
	StringBuffer sb = new StringBuffer( "Name            Score Ping  Time Rate RelPing Rank\n" );
	String s = "";
	for (i = 0; i < playerCount; i++)
		{
		pD = (float[])playerData.elementAt(i);
		// name
		sb.append( players.elementAt(i).toString() + spaces(pD[6]) );
		// score
		s = dfThree.format(pD[0]);
		sb.append( spaces( 5 - s.length() ) + s );
		// ping
		s = dfThree.format(pD[1]);
		sb.append( spaces( 4 - s.length() ) + s );
		// time
		s = dfThree.format(pD[2]);
		sb.append( spaces( 4 - s.length() ) + s );
		// rate
		s = dfTwoDotOne.format(pD[3]);
		sb.append( spaces( 4 - s.length() ) + s );
		// relping
		s = dfDotThree.format(pD[4]);
		sb.append( spaces( 7 - s.length() ) + s );
		// rank
		s = dfDotThree.format(pD[5]);
		sb.append( spaces( 4 - s.length() ) + s + "\n" );
		}

	System.out.print( sb );
	} // end scores ();
/**
 * Check the timelimit and fraglimit values and decide
 * whether to end the level or not.
 * @return boolean true if it's time to end the level
 */
protected static boolean timeToQuit() 
	{
	if (gChangeMapNow)
		return true;
		

	if ((gTimeLimit > 0) && (Game.getGameTime() > (gLevelStartTime + (gTimeLimit * 60))))
		{
		Game.localecast("q2java.baseq2.Messages", "timelimit",  Engine.PRINT_HIGH);
		return true;
		}
		
	if (gFragLimit < 1)
		return false;
		
	Enumeration enum = Player.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		if (p.getScore() > gFragLimit)
			{
			Game.localecast("q2java.baseq2.Messages", "fraglimit", Engine.PRINT_HIGH);
			return true;
			}
		}		
		
	return false;		
	}
/**
 * Unload baseq2 from the game - do this at your own risk.
 */
public void unload() 
	{
	Game.removePackagePath("q2java.baseq2");
	Game.removeServerFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_MIDDLE);
	Game.removeGameStatusListener(this);
	}
}