
package baseq2;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

/**
 * This class implements a Quake II Java game. All the
 * fields are static, so that objects can refer
 * to them without having to keep a reference to the solitary
 * Game object that's instantiated.
 *
 * @author Barry Pederson 
 */

public class GameModule implements GameStatusListener, FrameListener, LevelListener	, CrossLevel
	{	
	// an actual object that can be registered
	// as a listener to things.
	private static GameModule gMod;
	
	// handy reference to the world
	public static GameObject gWorld;
		
	// various CVars
	public static CVar gBobUp;	
	public static CVar gRollAngle;
	public static CVar gRollSpeed;
	public static CVar gGravity;
	
	// Game options
	public static boolean gIsDeathmatch;
	public static int gSkillLevel; // this probably isn't necessary since this is a DM-only mod, but wtf.
	
	// CVars only the Game object itself needs to worry about
	private static CVar gFragLimit;
	private static CVar gTimeLimit;
	private static CVar gDMFlags;
	

	// track level changes	
	private static float gLevelStartTime;
	private static boolean gInIntermission;
	private static double gIntermissionEndTime;
	private static String gCurrentMap;
	private static String gNextMap;
	

	// ----------- Constants -------------------------
	
	// deathmatch flags
	public final static int DF_NO_HEALTH			= 1;
	public final static int DF_NO_ITEMS			= 2;
	public final static int DF_WEAPONS_STAY		= 4;
	public final static int DF_NO_FALLING		= 8;
	public final static int DF_INSTANT_ITEMS		= 16;
	public final static int DF_SAME_LEVEL		= 32;
	public final static int DF_SKINTEAMS			= 64;
	public final static int DF_MODELTEAMS		= 128;
	public final static int DF_FRIENDLY_FIRE		= 256;
	public final static int DF_SPAWN_FARTHEST	= 512;
	public final static int DF_FORCE_RESPAWN		= 1024;
	public final static int DF_NO_ARMOR			= 2048;
	
	// entity spawn flags.
	public final static int SPAWNFLAG_NOT_EASY		= 0x00000100;
	public final static int SPAWNFLAG_NOT_MEDIUM		= 0x00000200;
	public final static int SPAWNFLAG_NOT_HARD		= 0x00000400;
	public final static int SPAWNFLAG_NOT_DEATHMATCH	= 0x00000800;
	public final static int SPAWNFLAG_NOT_COOP		= 0x00001000;		
	
/**
 * This method was created by a SmartGuide.
 */
private GameModule ( ) {
}
/**
 * Check whether an entity should be inhibited because
 * of its spawnargs.
 * @return boolean
 * @param spawnArgs java.lang.String[]
 */
public static void checkInhibited(String[] spawnArgs) throws InhibitedException
	{
	checkInhibited(Game.getSpawnArg(spawnArgs, "spawnflags", 0));
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
 * Describe this Game.
 * @return java.lang.String
 */
public static String getVersion() 
	{
	return "Quake2Java Test Game, v0.1";
	}	
/**
 * Display help info to the console.
 */
public static void help() 
	{
	Game.dprint("Q2Java Base Quake2 game v0.3\n\n");
	Game.dprint("    no commands available\n");
	}
/**
 * Check whether a given deathmatch flag is set.  Use the Game.DF_* constants.
 * @return true if the flag is set, false if not.
 */
public static boolean isDMFlagSet(int flag) 
	{
	return (((int)gDMFlags.getFloat()) & flag) != 0;
	}
/**
 * Called when this module is loaded.
 */
public static void load()
	{	
	gMod = new GameModule();
	Game.addGameStatusListener(gMod);
	Game.addFrameListener(gMod, 0, 0);
	Game.addLevelListener(gMod);
	
	// load cvars
	gBobUp = new CVar("bob_up", "0.005", 0);	
	gRollAngle = new CVar("sv_rollangle", "2", 0);
	gRollSpeed = new CVar("sv_rollspeed", "200", 0);	
	gGravity = new CVar("sv_gravity", "800", 0);	
	
	gFragLimit = new CVar("fraglimit", "0", CVar.CVAR_SERVERINFO);
	gTimeLimit = new CVar("timelimit", "0", CVar.CVAR_SERVERINFO);
	gDMFlags = new CVar("dmflags", "0", CVar.CVAR_SERVERINFO);

	gIsDeathmatch = (new CVar("deathmatch", "0", CVar.CVAR_LATCH)).getFloat() == 1.0;
	gSkillLevel = (int) ((new CVar("skill", "1", CVar.CVAR_LATCH)).getFloat());
	}
/**
 * Called when the DLL's ReadGame() function is called.
 */
public void readGame(String filename)
	{
	}
/**
 * Called when the DLL's ReadLevel() function is called.
 */
public void readLevel(String filename)
	{
	}
/**
 * Called by the DLL when the DLL's RunFrame() function is called.
 */
public void runFrame(int phase)
	{		
	if (gInIntermission && (Game.getGameTime() > gIntermissionEndTime))
		{
		if (isDMFlagSet(DF_SAME_LEVEL))
			Engine.addCommandString("gamemap \"" + gCurrentMap + "\"");
		else
			Engine.addCommandString("gamemap \"" + gNextMap + "\"");
		return;
		}

	if (!gInIntermission && timeToQuit())
		startIntermission();
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
 * Called when the DLL's Shutdown() function is called.
 */
public void shutdown()
	{
	}
/**
 * Pick an intermission spot, and notify each player.
 */
public void startIntermission() 
	{
	Enumeration enum;
	Vector v;
	
	// gather list of info_player_intermission entities
	v = Game.getLevelRegistryList(baseq2.spawn.info_player_intermission.REGISTRY_KEY);

	// if there weren't any intermission spots, try for info_player_start spots
	if (v.size() < 1)
		v = Game.getLevelRegistryList(baseq2.spawn.info_player_start.REGISTRY_KEY);

	// still no spots found? try for info_player_deathmatch
	if (v.size() < 1)
		v = Game.getLevelRegistryList(baseq2.spawn.info_player_deathmatch.REGISTRY_KEY);
		
	// randomly pick something from the list
	int i = (MiscUtil.randomInt() & 0x0fff) % v.size();
	GenericSpawnpoint spot = (GenericSpawnpoint) v.elementAt(i);

	// notify each player
	enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) ((NativeEntity) enum.nextElement()).getReference();
		p.startIntermission(spot);
		}
		
	gInIntermission = true;	
	gIntermissionEndTime = Game.getGameTime() + 5.0;	
	}
/**
 * Start a new level.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	Engine.debugLog("Game.spawnEntities(\"" + mapname + "\", <entString>, \"" + spawnPoint + "\")");

	gLevelStartTime = Game.getGameTime();
	gInIntermission = false;
	gCurrentMap = mapname;
	gNextMap = mapname; // in case there isn't a target_changelevel entity in the entString

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
 * Check the timelimit and fraglimit values and decide
 * whether to end the level or not.
 * @return boolean true if it's time to end the level
 */
private static boolean timeToQuit() 
	{
	float timeLimit = gTimeLimit.getFloat();

	if ((timeLimit > 0) && (Game.getGameTime() > (gLevelStartTime + (timeLimit * 60))))
		{
		Game.bprint(Engine.PRINT_HIGH, "Timelimit hit.\n");		
		return true;
		}
		
	int fragLimit = (int) gFragLimit.getFloat();
	if (fragLimit < 1)
		return false;
		
	Enumeration enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		if (p.getScore() > fragLimit)
			{
			Game.bprint(Engine.PRINT_HIGH, "Timelimit hit.\n");		
			return true;
			}
		}		
		
	return false;		
	}
/**
 * Unload baseq2 from the game - do this at your own risk.
 */
public static void unload() 
	{
	Game.removeGameStatusListener(gMod);
	Game.removeFrameListener(gMod);
	Game.removeLevelListener(gMod);
	gMod = null;
	}
/**
 * Called when the DLL's WriteGame() function is called.
 */
public void writeGame(String filename)
	{
	}
/**
 * Called when the DLL's WriteLevel() function is called.
 */
public void writeLevel(String filename)
	{
	}
}