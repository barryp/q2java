package q2java;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.vecmath.*;

/**
 * The Engine class provides access to several functions
 * of the Quake II game engine, and holds quite a few
 * of the game constants.
 *
 * @author Barry Pederson
 */

public class Engine
	{
	// grab the output streams
	static
		{
		PrintStream ps = new PrintStream(new ConsoleOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		}
		
	// track VWep skin indexes
	private static Vector gVWepList = new Vector();

	// queue for tasks from other threads that need to run on the main thread
	private static ThreadUtility gRunnableQueue = new ThreadUtility();

	// object (if any) that gets a copy of Java console output
	private static JavaConsoleListener gJavaConsoleListener;
	
	// reference to the main game thread
	private static Thread gGameThread = Thread.currentThread();

	// --------------------------------
	// Various Constants
	// --------------------------------

	public final static float SECONDS_PER_FRAME = 0.1F;
	
	public final static int PRINT_LOW		= 0;	// pickup messages
	public final static int PRINT_MEDIUM	= 1;	// death messages
	public final static int PRINT_HIGH 		= 2;	// critical messages
	public final static int PRINT_CHAT 		= 3;

	public final static int MULTICAST_ALL 	= 0;
	public final static int MULTICAST_PHS 	= 1;
	public final static int MULTICAST_PVS 	= 2;
	public final static int MULTICAST_ALL_R = 3;
	public final static int MULTICAST_PHS_R = 4;
	public final static int MULTICAST_PVS_R = 5;

	//
	// per-level limits
	//
	public final static int MAX_CLIENTS     = 256;  // absolute limit
	public final static int MAX_EDICTS      = 1024; // must change protocol to increase more
	public final static int MAX_LIGHTSTYLES = 256;
	public final static int MAX_MODELS      = 256;  // these are sent over the net as bytes
	public final static int MAX_SOUNDS      = 256;  // so they cannot be blindly increased
	public final static int MAX_IMAGES      = 256;
	public final static int MAX_ITEMS       = 256;
	public final static int MAX_GENERAL		= (MAX_CLIENTS*2);	// general config strings

	//
	// config strings are a general means of communication from
	// the server to all connected clients.
	// Each config string can be at most MAX_QPATH characters.
	//
	public final static int CS_NAME         	= 0;
	public final static int CS_CDTRACK  		= 1;
	public final static int CS_SKY      		= 2;
	public final static int CS_SKYAXIS  		= 3;	// %f %f %f format
	public final static int CS_SKYROTATE    	= 4;
	public final static int CS_STATUSBAR    	= 5;	// display program string

	public final static int CS_AIRACCEL			= 29;	// air acceleration control
	public final static int CS_MAXCLIENTS		= 30;
	public final static int CS_MAPCHECKSUM 		= 31;	// for catching cheater maps

	public final static int CS_MODELS           = 32;
	public final static int CS_SOUNDS           = (CS_MODELS+MAX_MODELS);
	public final static int CS_IMAGES           = (CS_SOUNDS+MAX_SOUNDS);
	public final static int CS_LIGHTS           = (CS_IMAGES+MAX_IMAGES);
	public final static int CS_ITEMS            = (CS_LIGHTS+MAX_LIGHTSTYLES);
	public final static int CS_PLAYERSKINS      = (CS_ITEMS+MAX_ITEMS);
	public final static int CS_GENERAL			= (CS_PLAYERSKINS+MAX_CLIENTS);	
	public final static int MAX_CONFIGSTRINGS   = (CS_GENERAL+MAX_GENERAL);

	
	
	// protocol bytes that can be directly added to messages
	public final static int SVC_MUZZLEFLASH		= 1;
	public final static int SVC_MUZZLEFLASH2	= 2;
	public final static int SVC_TEMP_ENTITY		= 3;
	public final static int SVC_LAYOUT			= 4;
	public final static int SVC_INVENTORY		= 5;
	public final static int SVC_STUFFTEXT 		= 11;	
	
	//
	// muzzle flashes / player effects
	//
	public final static int MZ_BLASTER		= 0;
	public final static int MZ_MACHINEGUN	= 1;
	public final static int MZ_SHOTGUN		= 2;
	public final static int MZ_CHAINGUN1	= 3;
	public final static int MZ_CHAINGUN2	= 4;
	public final static int MZ_CHAINGUN3	= 5;
	public final static int MZ_RAILGUN		= 6;
	public final static int MZ_ROCKET		= 7;
	public final static int MZ_GRENADE		= 8;
	public final static int MZ_LOGIN		= 9;
	public final static int MZ_LOGOUT		= 10;
	public final static int MZ_RESPAWN		= 11;
	public final static int MZ_BFG			= 12;
	public final static int MZ_SSHOTGUN		= 13;
	public final static int MZ_HYPERBLASTER	= 14;
	public final static int MZ_ITEMRESPAWN	= 15;
	public final static int MZ_SILENCED		= 128;      // bit flag ORed with one of the above numbers      

	// temp entity events
	//
	// Temp entity events are for things that happen
	// at a location seperate from any existing entity.
	// Temporary entity messages are explicitly constructed
	// and broadcast.
	public final static int TE_NONE					= -1; // used when damage shouldn't generate a TempEvent
	public final static int TE_GUNSHOT				= 0;
	public final static int TE_BLOOD				= 1;
	public final static int TE_BLASTER				= 2;
	public final static int TE_RAILTRAIL			= 3;
	public final static int TE_SHOTGUN				= 4;
	public final static int TE_EXPLOSION1			= 5;
	public final static int TE_EXPLOSION2			= 6;
	public final static int TE_ROCKET_EXPLOSION		= 7;
	public final static int TE_GRENADE_EXPLOSION	= 8;	
	public final static int TE_SPARKS				= 9;
	public final static int TE_SPLASH				= 10;
	public final static int TE_BUBBLETRAIL			= 11;
	public final static int TE_SCREEN_SPARKS			= 12;
	public final static int TE_SHIELD_SPARKS			= 13;
	public final static int TE_BULLET_SPARKS			= 14;
	public final static int TE_LASER_SPARKS				= 15;
	public final static int TE_PARASITE_ATTACK			= 16;
	public final static int TE_ROCKET_EXPLOSION_WATER	= 17;
	public final static int TE_GRENADE_EXPLOSION_WATER	= 18;
	public final static int TE_MEDIC_CABLE_ATTACK		= 19;
	public final static int TE_BFG_EXPLOSION			= 20;
	public final static int TE_BFG_BIGEXPLOSION			= 21;
	public final static int TE_BOSSTPORT				= 22; // used as '22' in a map, so DON'T RENUMBER!!!
	public final static int TE_BFG_LASER				= 23;
	public final static int TE_GRAPPLE_CABLE			= 24;
	public final static int TE_WELDING_SPARKS			= 25;
	public final static int TE_PLASMATRAIL				= 26;
	public final static int TE_GREENBLOOD				= 27;
	public final static int TE_BLUEHYPERBLASTER			= 28;
	public final static int TE_PLASMA_EXPLOSION			= 29;
	public final static int TE_TUNNEL_SPARKS			= 30;
//ROGUE
	public final static int TE_BLASTER2					= 31;
	public final static int TE_RAILTRAIL2				= 32;
	public final static int TE_FLAME					= 33;
	public final static int TE_LIGHTNING				= 34;
	public final static int TE_DEBUGTRAIL				= 35;
	public final static int TE_PLAIN_EXPLOSION			= 36;
	public final static int TE_FLASHLIGHT				= 37;
	public final static int TE_FORCEWALL				= 38;
	public final static int TE_HEATBEAM					= 39;
	public final static int TE_MONSTER_HEATBEAM			= 40;
	public final static int TE_STEAM					= 41;
	public final static int TE_BUBBLETRAIL2				= 42;
	public final static int TE_MOREBLOOD				= 43;
	public final static int TE_HEATBEAM_SPARKS			= 44;
	public final static int TE_HEATBEAM_STEAM			= 45;
	public final static int TE_CHAINFIST_SMOKE			= 46;
	public final static int TE_ELECTRIC_SPARKS			= 47;
	public final static int TE_TRACKER_EXPLOSION		= 48;
	public final static int TE_TELEPORT_EFFECT			= 49;
	public final static int TE_DBALL_GOAL				= 50;
	public final static int TE_WIDOWBEAMOUT				= 51;
	public final static int TE_NUKEBLAST				= 52;
	public final static int TE_WIDOWSPLASH				= 53;
	public final static int TE_EXPLOSION1_BIG			= 54;
	public final static int TE_EXPLOSION1_NP			= 55;
	public final static int TE_FLECHETTE				= 56;
//ROGUE

	public final static int SPLASH_UNKNOWN			= 0;
	public final static int SPLASH_SPARKS			= 1;
	public final static int SPLASH_BLUE_WATER		= 2;
	public final static int SPLASH_BROWN_WATER		= 3;
	public final static int SPLASH_SLIME			= 4;
	public final static int SPLASH_LAVA				= 5;
	public final static int SPLASH_BLOOD			= 6;	
	
/*
==============================================================

COLLISION DETECTION

==============================================================
*/

	// lower bits are stronger, and will eat weaker brushes completely
	public final static int CONTENTS_SOLID		= 1;		// an eye is never valid in a solid
	public final static int CONTENTS_WINDOW		= 2;		// translucent, but not watery
	public final static int CONTENTS_AUX		= 4;
	public final static int CONTENTS_LAVA		= 8;
	public final static int CONTENTS_SLIME		= 16;
	public final static int CONTENTS_WATER		= 32;
	public final static int CONTENTS_MIST		= 64;
	public final static int LAST_VISIBLE_CONTENTS	= 64;

	// remaining contents are non-visible, and don't eat brushes
	public final static int CONTENTS_AREAPORTAL		= 0x8000;
	public final static int CONTENTS_PLAYERCLIP		= 0x10000;
	public final static int CONTENTS_MONSTERCLIP	= 0x20000;

	// currents can be added to any other contents, and may be mixed
	public final static int CONTENTS_CURRENT_0		= 0x40000;
	public final static int CONTENTS_CURRENT_90		= 0x80000;
	public final static int CONTENTS_CURRENT_180	= 0x100000;
	public final static int CONTENTS_CURRENT_270	= 0x200000;
	public final static int CONTENTS_CURRENT_UP		= 0x400000;
	public final static int CONTENTS_CURRENT_DOWN	= 0x800000;

	public final static int CONTENTS_ORIGIN			= 0x1000000;	// removed before bsping an entity

	public final static int CONTENTS_MONSTER		= 0x2000000;	// should never be on a brush, only in game
	public final static int CONTENTS_DEADMONSTER	= 0x4000000;
	public final static int CONTENTS_DETAIL			= 0x8000000;	// brushes to be added after vis leafs
	public final static int CONTENTS_TRANSLUCENT	= 0x10000000;	// auto set if any surface has trans
	public final static int CONTENTS_LADDER			= 0x20000000;

	// surface masks
	public final static int SURF_LIGHT			= 0x1;	// value will hold the light strength	
	public final static int SURF_SLICK			= 0x2;	// effects game physics
	public final static int SURF_SKY			= 0x4;	// don't draw, but add to skybox
	public final static int SURF_WARP			= 0x8;	// turbulent water warp
	public final static int SURF_TRANS33		= 0x10;
	public final static int SURF_TRANS66		= 0x20;
	public final static int SURF_FLOWING		= 0x40;	// scroll towards angle
	public final static int SURF_NODRAW			= 0x80;	// don't bother referencing the texture

	// content masks
	public final static int MASK_ALL			= (-1);
	public final static int MASK_SOLID			= (CONTENTS_SOLID|CONTENTS_WINDOW);
	public final static int MASK_PLAYERSOLID	= (CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER);
	public final static int MASK_DEADSOLID		= (CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW);
	public final static int MASK_MONSTERSOLID	= (CONTENTS_SOLID|CONTENTS_MONSTERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER);
	public final static int MASK_WATER			= (CONTENTS_WATER|CONTENTS_LAVA|CONTENTS_SLIME);
	public final static int MASK_OPAQUE			= (CONTENTS_SOLID|CONTENTS_SLIME|CONTENTS_LAVA);
	public final static int MASK_SHOT			= (CONTENTS_SOLID|CONTENTS_MONSTER|CONTENTS_WINDOW|CONTENTS_DEADMONSTER);
	public final static int MASK_CURRENT		= (CONTENTS_CURRENT_0|CONTENTS_CURRENT_90|CONTENTS_CURRENT_180|CONTENTS_CURRENT_270|CONTENTS_CURRENT_UP|CONTENTS_CURRENT_DOWN);
	
	// boxEntity() can return a list of either solid or trigger entities
	public final static int AREA_SOLID			= 1;
	public final static int AREA_TRIGGERS		= 2;		
	
	// private constants for communicating with the DLL	
	private final static int CALL_MULTICAST 	= 0;
	private final static int CALL_WRITEPOSITION	= 1;
	private final static int CALL_WRITEDIR 		= 2;
	private final static int CALL_WRITECHAR 	= 3;
	private final static int CALL_WRITEBYTE 	= 4;
	private final static int CALL_WRITESHORT 	= 5;
	private final static int CALL_WRITELONG 	= 6;
	private final static int CALL_WRITEFLOAT 	= 7;
	private final static int CALL_WRITEANGLE 	= 8;
	private final static int CALL_WRITESTRING 	= 9;
	private final static int CALL_UNICAST 		= 10;
	private final static int CALL_INPVS 		= 11;
	private final static int CALL_INPHS 		= 12;

	// private constants for multithread-safe engine calls
	protected final static int DEFER_JAVA_CONSOLE_OUTPUT	= 0;
	protected final static int DEFER_ADD_COMMAND_STRING	= 1;
	protected final static int DEFER_DEBUG_LOG			= 2;
	protected final static int DEFER_BPRINT				= 3;
	protected final static int DEFER_DPRINT				= 4;
	protected final static int DEFER_DEBUG_GRAPH		= 5;
	protected final static int DEFER_ERROR				= 6;
	protected final static int DEFER_SET_CONFIG_STRING	= 7;
	protected final static int DEFER_SET_AREAPORTAL_STATE	= 8;
	protected final static int DEFER_GET_IMAGE_INDEX	= 9;
	protected final static int DEFER_GET_MODEL_INDEX	= 10;
	protected final static int DEFER_GET_SOUND_INDEX	= 11;
	protected final static int DEFER_GET_ARGC			= 12;
	protected final static int DEFER_GET_ARGS			= 13;
	protected final static int DEFER_GET_ARGV			= 14;

/*
 * Multithreading code, most of it commented out
 *
	private static class DeferredAreasConnected implements Runnable
		{
		private int fArea1;
		private int fArea2;
		boolean fResult;

		public DeferredAreasConnected(int area1, int area2)
			{
			fArea1 = area1;
			fArea2 = area2;
			}

		public void run()
			{
			fResult = Engine.areasConnected0(fArea1, fArea2);
			}
		}

	private static class DeferredGetBoxEntities implements Runnable
		{
		private Tuple3f fMins, fMaxs;
		private int fAreaType;
		NativeEntity[] fResult;
		
		public DeferredGetBoxEntities(Tuple3f mins, Tuple3f maxs, int areaType)
			{
			fMins = mins;
			fMaxs = maxs;
			fAreaType = areaType;
			}

		public void run()
			{
			fResult = Engine.getBoxEntities0(fMins.x, fMins.y, fMins.z, fMaxs.x, fMaxs.y, fMaxs.z, fAreaType);			
			}
		}

	private static class DeferredGetPointContents implements Runnable
		{
		private Point3f fPoint;
		int fResult;
		
		public DeferredGetPointContents(Point3f point)
			{
			fPoint = point;
			}

		public void run()
			{
			fResult = Engine.getPointContents0(fPoint.x, fPoint.y, fPoint.z);
			}
		}
		

	private static class DeferredGetRadiusEntities implements Runnable
		{
		private Point3f fPoint;
		private float fRadius;
		private boolean fOnlyPlayers, fSortResults;

		NativeEntity[] fResult;

		public DeferredGetRadiusEntities(Point3f point, float radius, boolean onlyPlayers, boolean sortResults)
			{
			fPoint = point;
			fRadius = radius;
			fOnlyPlayers = onlyPlayers;
			fSortResults = sortResults;
			}

		public void run()
			{
			fResult = Engine.getRadiusEntities0(fPoint.x, fPoint.y, fPoint.z, fRadius, 0, fOnlyPlayers, fSortResults);
			}
		}
		
	private static class DeferredInP implements Runnable
		{
		private float fX1, fY1, fZ1, fX2, fY2, fZ2;
		private int fCallType;
		boolean fResult;

		public DeferredInP(float x1, float y1, float z1, float x2, float y2, float z2, int callType)
			{
			fX1 = x1;
			fY1 = y1;
			fZ1 = z1;
			fX2 = x2;
			fY2 = y2;
			fZ2 = z2;
			fCallType = callType;
			}

		public void run()
			{
			fResult = Engine.inP0(fX1, fY1, fZ1, fX2, fY2, fZ2, fCallType);
			}
		}
		

	private static class DeferredTrace implements Runnable
		{
		private float fStartX, fStartY, fStartZ;
		private float fMinsX, fMinsY, fMinsZ;
		private float fMaxsX, fMaxsY, fMaxsZ;
		private float fEndX, fEndY, fEndZ;
		private NativeEntity fPassEnt;
		private int fContentMask;
		private int fUseMinMax;
		
		public TraceResults fResult;
		
		public DeferredTrace(float startx, float starty, float startz, 
			float minsx, float minsy, float minsz, 
			float maxsx, float maxsy, float maxsz,
			float endx, float endy, float endz, 
			NativeEntity passEnt, int contentMask, int useMinMax)
			{
			fStartX = startx;
			fStartY = starty;
			fStartZ = startz;
			fMinsX = minsx;
			fMinsY = minsy;
			fMinsZ = minsz;
			fMaxsX = maxsx;
			fMaxsY = maxsy;
			fMaxsZ = maxsz;
			fEndX = endx;
			fEndY = endy;
			fEndZ = endz;
			fPassEnt = passEnt;
			fContentMask = contentMask;
			fUseMinMax = useMinMax;
			}

		public void run()
			{
			fResult = Engine.trace0(fStartX, fStartY, fStartZ, 
				fMinsX, fMinsY, fMinsZ, 
				fMaxsX, fMaxsY, fMaxsZ,
				fEndZ, fEndY, fEndZ, 
				fPassEnt, fContentMask, fUseMinMax);
			}
		}

		
	private static class DeferredIntMethod implements Runnable
		{
		private int fMethodType;
		private String fString;
		int fResult;

		public DeferredIntMethod(int methodType, String s)
			{
			fMethodType = methodType;
			fString = s;
			}
			
		public DeferredIntMethod(int methodType)
			{
			fMethodType = methodType;
			}
			
		public void run()
			{
			switch (fMethodType)
				{
				case DEFER_GET_IMAGE_INDEX:
				case DEFER_GET_MODEL_INDEX:
				case DEFER_GET_SOUND_INDEX:
					fResult = Engine.getIndex0(fMethodType, fString);
					break;

				case DEFER_GET_ARGC:
					fResult = Engine.getArgc0();
					break;
				}
			}
		}

	private static class DeferredStringMethod implements Runnable
		{
		private int fMethodType;
		private int fInteger;
		String fResult;

		public DeferredStringMethod(int methodType, int val)
			{
			fMethodType = methodType;
			fInteger = val;
			}
			
		public DeferredStringMethod(int methodType)
			{
			fMethodType = methodType;
			}
			
		public void run()
			{
			switch (fMethodType)
				{
				case DEFER_GET_ARGS:
					fResult = Engine.getArgs0();
					break;

				case DEFER_GET_ARGV:
					fResult = Engine.getArgv0(fInteger);
					break;
				}
			}
		}		
		
*/

	private static class DeferredVoidMethod implements Runnable 
		{
		private int fMethodType;
		private String fString;
		private int fInteger;
		private float fFloat;
		private boolean fBoolean;

		public DeferredVoidMethod(int methodType, String s) 
			{
			fMethodType = methodType;
			fString = s;
			}
			
		public DeferredVoidMethod(int methodType, int intval, String s) 
			{
			fMethodType = methodType;
			fInteger = intval;
			fString = s;
			}
			
		public DeferredVoidMethod(int methodType, float floatval, int intval) 
			{
			fMethodType = methodType;
			fFloat = floatval;
			fInteger = intval;
			}
			
		public DeferredVoidMethod(int methodType, int intval, boolean bool) 
			{
			fMethodType = methodType;
			fInteger = intval;
			fBoolean = bool;
			}

		public void run() 
			{
			switch (fMethodType)
				{
				case Engine.DEFER_JAVA_CONSOLE_OUTPUT:				
					Engine.javaConsoleOutput(fString);
					break;

				case Engine.DEFER_ADD_COMMAND_STRING:					
					Engine.addCommandString0(fString);
					break;
					
				case Engine.DEFER_DEBUG_LOG:
					Engine.debugLog0(fString);
					break;

				case Engine.DEFER_BPRINT:					
					Engine.bprint0(fInteger, fString);
					break;
					
				case Engine.DEFER_DPRINT:					
					Engine.dprint0(fString);
					break;					

				case Engine.DEFER_DEBUG_GRAPH:					
					Engine.debugGraph0(fFloat, fInteger);
					break;

				case Engine.DEFER_ERROR:					
					Engine.error0(fString);
					break;

				case Engine.DEFER_SET_CONFIG_STRING:
					Engine.setConfigString0(fInteger, fString);
					break;

				case Engine.DEFER_SET_AREAPORTAL_STATE:
					Engine.setAreaPortalState0(fInteger, fBoolean);
					break;
				}
			}			
		}
		
	
/**
 * Private constructor to prevent people from trying to
 * create instances of this class.
 */
private Engine() 
	{
	}
/**
 * Cause the Engine to run a command (multithread-safe)
 *
 * @params s the Command to run, should end with a '\n'.
 */
public static void addCommandString(String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (Thread.currentThread() == gGameThread)
		addCommandString0(s);
	else
		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_ADD_COMMAND_STRING, s));
	}
private native static void addCommandString0(String s);
public static boolean areasConnected(int area1, int area2)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return areasConnected0(area1, area2);
/*		
	else
		{
		Engine.DeferredAreasConnected def = new Engine.DeferredAreasConnected(area1, area2);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static boolean areasConnected0(int area1, int area2);
/**
 * Broadcast a message to all players
 *
 * @param printLevel One of the PRINT_* constants.
 * @param s The message to send
 */
public static void bprint(int printLevel, String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		bprint0(printLevel, s);
//	else
//		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_BPRINT, printLevel, s));	
	}
/**
 * Broadcast a message to all players
 *
 * @param printLevel One of the PRINT_* constants.
 * @param s The message to send
 */
private native static void bprint0(int printLevel, String s);
/**
 * Update the Q2 debugging graph? (multithread-safe)
 */
public static void debugGraph(float value, int color)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (Thread.currentThread() == gGameThread)
		debugGraph0(value, color);
	else
		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_DEBUG_GRAPH, value, color));	
	}
private native static void debugGraph0(float value, int color);
/**
 * Send a message to Q2Java's debugLog (multithread-safe)
 * @param s message to log.
 */
public static void debugLog(String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (Thread.currentThread() == gGameThread)
		debugLog0(s);
	else
		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_DEBUG_LOG, s));	
	}
/**
 * Send a message to Q2Java's debugLog.
 * @param s message to log.
 */
private native static void debugLog0(String s);
/**
 * Print a message to the server console (multithread-safe)
 */
public static void dprint(String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		dprint0(s);
//	else
//		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_DPRINT, s));	
	}
/**
 * Print a message to the server console.
 */
private native static void dprint0(String s);
/**
 * Exit the game with an error?
 */
public static void error(String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (Thread.currentThread() == gGameThread)
		error0(s);
	else
		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_ERROR, s));	
	}
/**
 * Exit the game with an error?
 */
private native static void error0(String s);
public static int getArgc()
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getArgc0();
/*		
	else
		{
		Engine.DeferredIntMethod def = new Engine.DeferredIntMethod(DEFER_GET_ARGC);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static int getArgc0();
public static String getArgs()
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getArgs0();
/*		
	else
		{
		Engine.DeferredStringMethod def = new Engine.DeferredStringMethod(DEFER_GET_ARGS);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static String getArgs0();
public static String getArgv(int n)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getArgv0(n);
/*		
	else
		{
		Engine.DeferredStringMethod def = new Engine.DeferredStringMethod(DEFER_GET_ARGV, n);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static String getArgv0(int n);
/**
 * This method was created by a SmartGuide.
 * @return NativeEntity[]
 * @param mins Vec3
 * @param maxs Vec3
 * @param maxCount int
 * @param areaType int
 */
public static NativeEntity[] getBoxEntities(Tuple3f mins, Tuple3f maxs, int areaType) 
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getBoxEntities0(mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, areaType);
/*		
	else
		{
		Engine.DeferredGetBoxEntities def = new Engine.DeferredGetBoxEntities(mins, maxs, areaType);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
/**
 * This method was created by a SmartGuide.
 * @return NativeEntity[]
 * @param mins Vec3
 * @param maxs Vec3
 * @param maxCount int
 * @param areaType int
 */
private native static NativeEntity[] getBoxEntities0(float minsx, float minsy, float minsz,
	float maxsx, float maxsy, float maxsz, int areaType);
/**
 * Fetch the path of the current Quake2 game.
 *
 * @return A path such as "c:\\quake2\\q2java".
 */
public native static String getGamePath();
/**
 * Get a reference to the main game thread.
 * @return java.lang.Thread
 */
public static Thread getGameThread() 
	{
	return gGameThread;
	}
public static int getImageIndex(String name)
	{
	return getIndex(DEFER_GET_IMAGE_INDEX, name);
	}
private static int getIndex(int indexType, String name)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getIndex0(indexType, name);
/*		
	else
		{
		Engine.DeferredIntMethod def = new Engine.DeferredIntMethod(indexType, name);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static int getIndex0(int indexType, String name);
public static int getModelIndex(String name)
	{
	return getIndex(DEFER_GET_MODEL_INDEX, name);
	}
/**
 * Get a value indicating elapsed time in some number of ticks.
 * Use getPerformanceFrequency to find out how many ticks per second
 * occur on this system.  Similar to the Win32 QueryPerformanceCounter() function.
 * @return long
 */
public native static long getPerformanceCounter();
/**
 * Get a value indicating how many ticks per second
 * occur on this system.  Similar to the Win32 QueryPerformanceFrequency() function.
 * @return long
 */
public native static long getPerformanceFrequency();
public static int getPointContents(Point3f point)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getPointContents0(point.x, point.y, point.z);
/*		
	else
		{
		Engine.DeferredGetPointContents def = new Engine.DeferredGetPointContents(point);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static int getPointContents0(float x, float y, float z);
/**
 * Get a list of entities who have their origin within 
 * a certain radius of the given point.
 * @return NativeEntity[]
 * @param point 
 * @param radius
 * @param onlyPlayers Only return player entities.
 * @param sortResults Sort results with the nearest entities first.
 */
public static NativeEntity[] getRadiusEntities(Point3f point, float radius, boolean onlyPlayers, boolean sortResults) 
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		return getRadiusEntities0(point.x, point.y, point.z, radius, 0, onlyPlayers, sortResults);
/*		
	else
		{
		Engine.DeferredGetRadiusEntities def = new Engine.DeferredGetRadiusEntities(point, radius, onlyPlayers, sortResults);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
/**
 * This method was created by a SmartGuide.
 * @return NativeEntity[]
 * @param mins Vec3
 * @param maxs Vec3
 * @param maxCount int
 * @param areaType int
 */
private native static NativeEntity[] getRadiusEntities0(float x, float y, float z,
	float radius, int ignoreIndex, boolean onlyPlayers, boolean sortResults);
public static int getSoundIndex(String name)
	{
	return getIndex(DEFER_GET_SOUND_INDEX, name);
	}
/**
 * Get the index of the VWep skin for a given weapon.
 * Similar to the other get*Index() methods, but this 
 * is something the Engine class simulates and isn't
 * actually supplied by the real Q2 Engine.
 *
 * The VWep indexes are assigned in the order they're 
 * requested, starting at 1.
 *
 * @return int
 * @param weaponIconName java.lang.String
 */
public static int getVWepIndex(String weaponIconName) 
	{
//	synchronized (gVWepList)
		{
		int nEntries = gVWepList.size();
	
		// look for pre-existing entries.
		for (int i = 0; i < nEntries; i++)
			{
			if (((String) gVWepList.elementAt(i)).equals(weaponIconName))
				return i+1;
			}
		
		// wasn't in the list, so add it and return the new entry's index.		
		getModelIndex("#" + weaponIconName + ".md2");
		gVWepList.addElement(weaponIconName);
		return nEntries + 1;
		}
	}
private native static boolean inP0(float x1, float y1, float z1, float x2, float y2, float z2, int calltype);

/** 
 * Method for multithreading the inPHS and inPVS methods - but not currently used.
  
private static boolean inP1(float x1, float y1, float z1, float x2, float y2, float z2, int callType)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread	
	if (Thread.currentThread() == gGameThread)
		return inP1(x1, y1, z1, x2, y2, z2, callType);
	else
		{
		Engine.DeferredInP def = new Engine.DeferredInP(x1, y1, z1, x2, y2, z2, callType);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
		
	}
*/
public static boolean inPHS(Point3f p1, Point3f p2)
	{
//	return inP1(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPHS);
	return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPHS);	
	}
public static boolean inPVS(Point3f p1, Point3f p2)
	{
//	return inP1(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPVS);
	return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPVS);
	}
/**
 * Causes doRun.run() to be executed synchronously on the main game thread.
 *
 * This is basically the same technique Java Swing uses to allow threads to
 * run code on the main thread.
 *
 * @param doRun An object that implements java.lang.Runnable
 *
 * @exception  InterruptedException If we're interrupted while waiting for
 *             the event dispatching thread to finish excecuting <i>doRun.run()</i>
 * @exception  InvocationTargetException  If <i>doRun.run()</i> throws
 *
 * @see #invokeLater 
 */
public static void invokeAndWait(Runnable doRun) throws InterruptedException, InvocationTargetException
	{
	gRunnableQueue.invokeAndWait(doRun);
	}

	
/**
 * Causes doRun.run() to be executed asynchronously on the main game thread.
 *
 * This is basically the same technique Java Swing uses to allow threads to
 * run code on the main thread.
 *
 * Any exceptions thrown on the main thread are quietly ignored.
 *
 * @param doRun An object that implements java.lang.Runnable
 */
public static void invokeLater(Runnable doRun)
	{
	gRunnableQueue.invokeLater(doRun);
	}
/**
 * Called when the JVM has console output.
 * @param s java.lang.String
 */
static void javaConsoleOutput(String s) 
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (!(Thread.currentThread().equals(gGameThread)))
		{
		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_JAVA_CONSOLE_OUTPUT, s));
		return;
		}

	// send the string down to the Q2 console		
	Engine.dprint(s);

	// send the string out to any interested Java code
	if (gJavaConsoleListener != null)
		gJavaConsoleListener.javaConsoleOutput(s);
	}
public static void multicast(Point3f origin, int to)
	{
	write0(null, origin.x, origin.y, origin.z, to, CALL_MULTICAST);
	}
/**
 * Called by the DLL at regular intervals.
 */
private static void runDeferred() 
	{
	gRunnableQueue.run();	
	}	
public static void setAreaPortalState(int portalNum, boolean open)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		setAreaPortalState0(portalNum, open);
//	else
//		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_SET_AREAPORTAL_STATE, portalNum, open));		
	}
private native static void setAreaPortalState0(int portalnum, boolean open);
public static void setConfigString(int num, String s)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == gGameThread)
		setConfigString0(num, s);
//	else
//		Engine.invokeLater(new Engine.DeferredVoidMethod(DEFER_SET_CONFIG_STRING, num, s));		
	}
private native static void setConfigString0(int num, String s);
/**
 * Set an object to be called when the JVM has output.
 * @param jcl q2java.JavaConsoleListener
 */
public static void setJavaConsoleListener(JavaConsoleListener jcl)
	{
	gJavaConsoleListener = jcl;
	}
/**
 * Called by the DLL to let the Engine know that a new level 
 * is starting.
 */
private static void startLevel() 
	{	
	// reset the VWep index
	gVWepList.removeAllElements();
	}
/**
 * Trace a line through the world.
 * @return TraceResults
 * @param start Vec3
 * @param end Vec3
 * @param passEnt NativeEntity
 * @param contentMask int
 */
public static TraceResults trace(Point3f start, Point3f end, NativeEntity passEnt, int contentMask) 
	{
//	return trace1(start.x, start.y, start.z, 0, 0, 0, 0, 0, 0, end.x, end.y, end.z, passEnt, contentMask, 0);
	return trace0(start.x, start.y, start.z, 0, 0, 0, 0, 0, 0, end.x, end.y, end.z, passEnt, contentMask, 0);
	}
/**
 * Trace a volume across the world.
 * @return TraceResults
 * @param start Vec3
 * @param mins Vec3
 * @param maxs Vec3
 * @param end Vec3
 * @param passEnt NativeEntity
 * @param contentMask int
 */
public static TraceResults trace(Point3f start, Tuple3f mins, Tuple3f maxs, Point3f end, NativeEntity passEnt, int contentMask) 
	{
//	return trace1(start.x, start.y, start.z, mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, end.x, end.y, end.z, passEnt, contentMask, 1);
	return trace0(start.x, start.y, start.z, mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, end.x, end.y, end.z, passEnt, contentMask, 1);
	}
/**
 * This method was created by a SmartGuide.
 * @return TraceResults
 * @param start Vec3
 * @param mins Vec3
 * @param maxs Vec3
 * @param end Vec3
 * @param passEnt NativeEntity
 * @param contentMask int
 */
private native static TraceResults trace0(float startx, float starty, float startz,
	float minsx, float minsy, float minsz, 
	float maxsx, float maxsy, float maxsz,
	float endx, float endy, float endz,
	NativeEntity passEnt, int contentMask, int useMinMax);

/**
 * Method to help with multithreaded trace calls - not currently used.
 * @return TraceResults
 * @param start Vec3
 * @param mins Vec3
 * @param maxs Vec3
 * @param end Vec3
 * @param passEnt NativeEntity
 * @param contentMask int
 
private static TraceResults trace1(float startx, float starty, float startz,
	float minsx, float minsy, float minsz, 
	float maxsx, float maxsy, float maxsz,
	float endx, float endy, float endz,
	NativeEntity passEnt, int contentMask, int useMinMax)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
	if (Thread.currentThread() == gGameThread)
		return trace0(startx, starty, startz, minsx, minsy, minsz, maxsx, maxsy, maxsz,
			endx, endy, endz, passEnt, contentMask, useMinMax);
	else
		{
		Engine.DeferredTrace def = new Engine.DeferredTrace(startx, starty, startz, 
			minsx, minsy, minsz, maxsx, maxsy, maxsz,
			endx, endy, endz, passEnt, contentMask, useMinMax);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}	
	}

*/	
/**
 * Send a packet to a particular client?
 */
public static void unicast(NativeEntity ent, boolean reliable)
	{
	if (!ent.isBot())
		write0(ent, 0, 0, 0, (reliable ? 1 : 0), CALL_UNICAST);
	}
private native static void write0(Object obj, float x, float y, float z, int c, int calltype);
public static void writeAngle(float f)
	{
	write0(null, f, 0, 0, 0, CALL_WRITEANGLE);
	}
public static void writeByte(int c)
	{
	write0(null, 0, 0, 0, c, CALL_WRITEBYTE);
	}
// wasn't used in original game dll, 
// not sure if this should be a java char instead of int
public static void writeChar(int c)         
	{
	write0(null, 0, 0, 0, c, CALL_WRITECHAR);
	}
public static void writeDir(Vector3f dir)
	{
	write0(null, dir.x, dir.y, dir.z, 0, CALL_WRITEDIR);
	}
public static void writeFloat(float f)
	{
	write0(null, f, 0, 0, 0, CALL_WRITEFLOAT);
	}
public static void writeLong(int c)
	{
	write0(null, 0, 0, 0, c, CALL_WRITELONG);
	}
public static void writePosition(Point3f pos)
	{
	write0(null, pos.x, pos.y, pos.z, 0, CALL_WRITEPOSITION);
	}
public static void writeShort(int c)
	{
	write0(null, 0, 0, 0, c, CALL_WRITESHORT);
	}
public static void writeString(String s)
	{
	write0(s, 0, 0, 0, 0, CALL_WRITESTRING);
	}
}