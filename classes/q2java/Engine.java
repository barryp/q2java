
package q2java;

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
	public final static float SECONDS_PER_FRAME = 0.1F;
	
	public final static int PRINT_LOW		= 0;	// pickup messages
	public final static int PRINT_MEDIUM		= 1;	// death messages
	public final static int PRINT_HIGH 		= 2;	// critical messages
	public final static int PRINT_CHAT 		= 3;

	public final static int MULTICAST_ALL 	= 0;
	public final static int MULTICAST_PHS 	= 1;
	public final static int MULTICAST_PVS 	= 2;
	public final static int MULTICAST_ALL_R 	= 3;
	public final static int MULTICAST_PHS_R 	= 4;
	public final static int MULTICAST_PVS_R 	= 5;

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

	//
	// config strings are a general means of communication from
	// the server to all connected clients.
	// Each config string can be at most MAX_QPATH characters.
	//
	public final static int CS_NAME         	= 0;
	public final static int CS_CDTRACK  		= 1;
	public final static int CS_SKY      		= 2;
	public final static int CS_SKYAXIS  		= 3;        // %f %f %f format
	public final static int CS_SKYROTATE    	= 4;
	public final static int CS_STATUSBAR    	= 5;        // display program string

	public final static int CS_MAPCHECKSUM 	= 31;    // for catching cheater maps

	public final static int CS_MODELS           = 32;
	public final static int CS_SOUNDS           = (CS_MODELS+MAX_MODELS);
	public final static int CS_IMAGES           = (CS_SOUNDS+MAX_SOUNDS);
	public final static int CS_LIGHTS           = (CS_IMAGES+MAX_IMAGES);
	public final static int CS_ITEMS            = (CS_LIGHTS+MAX_LIGHTSTYLES);
	public final static int CS_PLAYERSKINS      = (CS_ITEMS+MAX_ITEMS);
	public final static int MAX_CONFIGSTRINGS   = (CS_PLAYERSKINS+MAX_CLIENTS);

	
	
	// protocol bytes that can be directly added to messages
	public final static int SVC_MUZZLEFLASH		= 1;
	public final static int SVC_MUZZLEFLASH2		= 2;
	public final static int SVC_TEMP_ENTITY		= 3;
	public final static int SVC_LAYOUT			= 4;
	public final static int SVC_INVENTORY		= 5;
	
	//
	// muzzle flashes / player effects
	//
	public final static int MZ_BLASTER		= 0;
	public final static int MZ_MACHINEGUN	= 1;
	public final static int MZ_SHOTGUN		= 2;
	public final static int MZ_CHAINGUN1		= 3;
	public final static int MZ_CHAINGUN2		= 4;
	public final static int MZ_CHAINGUN3		= 5;
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
	public final static int TE_RAILTRAIL				= 3;
	public final static int TE_SHOTGUN				= 4;
	public final static int TE_EXPLOSION1			= 5;
	public final static int TE_EXPLOSION2			= 6;
	public final static int TE_ROCKET_EXPLOSION		= 7;
	public final static int TE_GRENADE_EXPLOSION		= 8;	
	public final static int TE_SPARKS				= 9;
	public final static int TE_SPLASH				= 10;
	public final static int TE_BUBBLETRAIL			= 11;
	public final static int TE_SCREEN_SPARKS			= 12;
	public final static int TE_SHIELD_SPARKS			= 13;
	public final static int TE_BULLET_SPARKS			= 14;
	public final static int TE_LASER_SPARKS			= 15;
	public final static int TE_PARASITE_ATTACK		= 16;
	public final static int TE_ROCKET_EXPLOSION_WATER	= 17;
	public final static int TE_GRENADE_EXPLOSION_WATER	= 18;
	public final static int TE_MEDIC_CABLE_ATTACK		= 19;
	public final static int TE_BFG_EXPLOSION			= 20;
	public final static int TE_BFG_BIGEXPLOSION		= 21;
	public final static int TE_BOSSTPORT				= 22; // used as '22' in a map, so DON'T RENUMBER!!!
	public final static int TE_BFG_LASER				= 23;
	public final static int TE_GRAPPLE_CABLE			= 24;
	public final static int TE_WELDING_SPARKS		= 25;
	public final static int TE_PLASMATRAIL			= 26;
	public final static int TE_GREENBLOOD			= 27;
	
	public final static int SPLASH_UNKNOWN			= 0;
	public final static int SPLASH_SPARKS			= 1;
	public final static int SPLASH_BLUE_WATER		= 2;
	public final static int SPLASH_BROWN_WATER		= 3;
	public final static int SPLASH_SLIME				= 4;
	public final static int SPLASH_LAVA				= 5;
	public final static int SPLASH_BLOOD				= 6;	
	
/*
==============================================================

COLLISION DETECTION

==============================================================
*/

	// lower bits are stronger, and will eat weaker brushes completely
	public final static int CONTENTS_SOLID		= 1;		// an eye is never valid in a solid
	public final static int CONTENTS_WINDOW		= 2;		// translucent, but not watery
	public final static int CONTENTS_AUX			= 4;
	public final static int CONTENTS_LAVA		= 8;
	public final static int CONTENTS_SLIME		= 16;
	public final static int CONTENTS_WATER		= 32;
	public final static int CONTENTS_MIST		= 64;
	public final static int LAST_VISIBLE_CONTENTS	= 64;

	// remaining contents are non-visible, and don't eat brushes
	public final static int CONTENTS_AREAPORTAL	= 0x8000;
	public final static int CONTENTS_PLAYERCLIP	= 0x10000;
	public final static int CONTENTS_MONSTERCLIP	= 0x20000;

	// currents can be added to any other contents, and may be mixed
	public final static int CONTENTS_CURRENT_0	= 0x40000;
	public final static int CONTENTS_CURRENT_90	= 0x80000;
	public final static int CONTENTS_CURRENT_180	= 0x100000;
	public final static int CONTENTS_CURRENT_270	= 0x200000;
	public final static int CONTENTS_CURRENT_UP	= 0x400000;
	public final static int CONTENTS_CURRENT_DOWN	= 0x800000;

	public final static int CONTENTS_ORIGIN		= 0x1000000;	// removed before bsping an entity

	public final static int CONTENTS_MONSTER		= 0x2000000;	// should never be on a brush, only in game
	public final static int CONTENTS_DEADMONSTER	= 0x4000000;
	public final static int CONTENTS_DETAIL		= 0x8000000;	// brushes to be added after vis leafs
	public final static int CONTENTS_TRANSLUCENT	= 0x10000000;	// auto set if any surface has trans
	public final static int CONTENTS_LADDER		= 0x20000000;

	// surface masks
	public final static int SURF_LIGHT			= 0x1;	// value will hold the light strength	
	public final static int SURF_SLICK			= 0x2;	// effects game physics
	public final static int SURF_SKY			= 0x4;	// don't draw, but add to skybox
	public final static int SURF_WARP			= 0x8;	// turbulent water warp
	public final static int SURF_TRANS33			= 0x10;
	public final static int SURF_TRANS66			= 0x20;
	public final static int SURF_FLOWING			= 0x40;	// scroll towards angle
	public final static int SURF_NODRAW			= 0x80;	// don't bother referencing the texture

	// content masks
	public final static int MASK_ALL			= (-1);
	public final static int MASK_SOLID			= (CONTENTS_SOLID|CONTENTS_WINDOW);
	public final static int MASK_PLAYERSOLID		= (CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER);
	public final static int MASK_DEADSOLID		= (CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW);
	public final static int MASK_MONSTERSOLID	= (CONTENTS_SOLID|CONTENTS_MONSTERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER);
	public final static int MASK_WATER			= (CONTENTS_WATER|CONTENTS_LAVA|CONTENTS_SLIME);
	public final static int MASK_OPAQUE			= (CONTENTS_SOLID|CONTENTS_SLIME|CONTENTS_LAVA);
	public final static int MASK_SHOT			= (CONTENTS_SOLID|CONTENTS_MONSTER|CONTENTS_WINDOW|CONTENTS_DEADMONSTER);
	public final static int MASK_CURRENT			= (CONTENTS_CURRENT_0|CONTENTS_CURRENT_90|CONTENTS_CURRENT_180|CONTENTS_CURRENT_270|CONTENTS_CURRENT_UP|CONTENTS_CURRENT_DOWN);
	
	// boxEntity() can return a list of either solid or trigger entities
	public final static int AREA_SOLID			= 1;
	public final static int AREA_TRIGGERS		= 2;		
	
	// private constants for communicating with the DLL	
	private final static int CALL_MULTICAST 		= 0;
	private final static int CALL_WRITEPOSITION	= 1;
	private final static int CALL_WRITEDIR 		= 2;
	private final static int CALL_WRITECHAR 		= 3;
	private final static int CALL_WRITEBYTE 		= 4;
	private final static int CALL_WRITESHORT 	= 5;
	private final static int CALL_WRITELONG 		= 6;
	private final static int CALL_WRITEFLOAT 	= 7;
	private final static int CALL_WRITEANGLE 	= 8;
	private final static int CALL_WRITESTRING 	= 9;
	private final static int CALL_UNICAST 		= 10;
	private final static int CALL_INPVS 			= 11;
	private final static int CALL_INPHS 			= 12;
	

public native static void addCommandString(String s);


public native static boolean areasConnected(int area1, int area2);


/**
 * Broadcast a message to all players
 *
 * @param printLevel One of the PRINT_* constants.
 * @param s The message to send
 */
public native static void bprint(int printLevel, String s);


public native static void debugGraph(float value, int color);


/**
 * Send a message to Q2Java's debugLog.
 * @param s message to log.
 */
public native static void debugLog(String s);


/**
 * Print a message to the server console.
 */
public native static void dprint(String s);


public native static void error(String s);


public native static int getArgc();


public native static String getArgs();


public native static String getArgv(int n);

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
	return getBoxEntities0(mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, areaType);
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


public native static int getImageIndex(String name);


public native static int getModelIndex(String name);

public static int getPointContents(Point3f point)
	{
	return getPointContents0(point.x, point.y, point.z);
	}

private native static int getPointContents0(float x, float y, float z);

/**
 * Get a list of entities who have their origin within a certain radius of the given point.
 * @return NativeEntity[]
 * @param point 
 * @param radius
 * @param onlyPlayers Only return player entities.
 * @param sortResults Sort results with the nearest entities first.
 */
public static NativeEntity[] getRadiusEntities(Point3f point, float radius, boolean onlyPlayers, boolean sortResults) 
	{
	return getRadiusEntities0(point.x, point.y, point.z, radius, 0, onlyPlayers, sortResults);
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


public native static int getSoundIndex(String name);


private native static boolean inP0(float x1, float y1, float z1, float x2, float y2, float z2, int calltype);

public static boolean inPHS(Point3f p1, Point3f p2)
	{
	return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPHS);
	}
public static boolean inPVS(Point3f p1, Point3f p2)
	{
	return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPVS);
	}
public static void multicast(Point3f origin, int to)
	{
	write0(null, origin.x, origin.y, origin.z, to, CALL_MULTICAST);
	}

public native static void setAreaPortalState(int portalnum, boolean open);


public native static void setConfigString(int num, String s);

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