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

public class BaseQ2 implements ServerFrameListener, GameStatusListener, CrossLevel
	{
	// keep track of how many objects want us around
	private static BaseQ2 gInstance;
	private static int gReferenceCount;	
	private static class Key 
		{
		boolean fIsFreed;

		// if the key is going to be gc'ed then let the BaseQ2
		// class know the key is no longer out there
		protected void finalize()
			{
			BaseQ2.freeReference(this); 
			}
		}
	
	// handy reference to the world
	public static GameObject gWorld;		

	// various CVars
	private static CVar gGravityCVar;
	private static CVar gMaxVelocityCVar;

	// CVars only this Gamelet itself needs to worry about
	private static CVar gDMFlagsCVar;
	private static CVar gCheatsCVar;
	
	// Mirrored CVars

	public static float gGravity;
	public static float gMaxVelocity;	

	// Mirrored CVars only the BaseQ2 gamelet looks at
	private static int   gDMFlags;
	private static float gCheats;

	// handle to keep InventoryList from being GCed
	Class gInventoryListClass;
	
	// Game options
	public static boolean gIsDeathmatch;
	public static int gSkillLevel; // this probably isn't necessary since this is a DM-only mod, but wtf.
	
	// track level changes	
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
 * Create a reference to the BaseQ2 class.
 */
private BaseQ2() 
	{
	// load cvars	
	gGravityCVar = new CVar("sv_gravity", "800", 0);	
	gMaxVelocityCVar = new CVar("sv_maxvelocity", "2000", 0);
	
	gDMFlagsCVar = new CVar("dmflags", "0", CVar.CVAR_SERVERINFO);
	gCheatsCVar = new CVar("cheats", "0", CVar.CVAR_LATCH);
	
	gIsDeathmatch = (new CVar("deathmatch", "0", CVar.CVAR_LATCH)).getFloat() == 1.0;
	gSkillLevel = (int) ((new CVar("skill", "1", CVar.CVAR_LATCH)).getFloat());		

	gInventoryListClass = InventoryList.class;
	InventoryList.setupList( 42, 84, 128, 172, 214, true);	
	
	new CVar("locale", "en_US", CVar.CVAR_USERINFO | CVar.CVAR_ARCHIVE);
	
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 10.0F);
	Game.addGameStatusListener(this);	
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
 * Release this reference to the BaseQ2 class.
 */
public static void freeReference(Object obj) 
	{
	Key k = (Key) obj;

	// do nothing if this particular was already used
	// in a call to this method
	if (k.fIsFreed)
		return;

	// invalidate this key
	k.fIsFreed = true;

	// decrement the count of objects that want us around
	gReferenceCount--;

	// nobody wants us
	if (gReferenceCount < 1)
		{
		Game.removeServerFrameListener(gInstance, Game.FRAME_BEGINNING);
		Game.removeGameStatusListener(gInstance);
		gInstance = null;
		gWorld = null;
		}
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
	Engine.debugLog("World.gameStatusChanged(" + e.getState() + ")");

	if( e.getState() != GameStatusEvent.GAME_PRESPAWN )
		return;

	Element root = (Element) Game.getDocument("q2java.level").getDocumentElement();
	String mapname = root.getAttribute("name");
	String spawnPoint = root.getAttribute("spawnpoint");
	
	if ((spawnPoint != null) && (spawnPoint.length() == 0))
		gSpawnpoint = null;
	else
		gSpawnpoint = spawnPoint;
	
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
 * Get a token from BaseQ2 to indicate we want it around.
 *
 * @see q2java.baseq2.BaseQ2#freeReference
 * @return java.lang.Object
 */
public static Object getReference() 
	{
	gReferenceCount++;
	if (gReferenceCount == 1)
		gInstance = new BaseQ2();
	
	return new Key();
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
	return "Q2Java Base Game, v0.9.5";
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

			gGravity	= gGravityCVar.getFloat();
			gMaxVelocity= gMaxVelocityCVar.getFloat();

			gDMFlags	= (int) gDMFlagsCVar.getFloat();
			gCheats		= gCheatsCVar.getFloat();			
			break;			
		}
	}
}