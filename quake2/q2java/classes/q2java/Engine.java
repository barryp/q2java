
package q2java;

public class Engine
	{   
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
	public final static int MZ_BLASTER      = 0;
	public final static int MZ_MACHINEGUN       = 1;
	public final static int MZ_SHOTGUN      = 2;
	public final static int MZ_CHAINGUN1        = 3;
	public final static int MZ_CHAINGUN2        = 4;
	public final static int MZ_CHAINGUN3        = 5;
	public final static int MZ_RAILGUN      = 6;
	public final static int MZ_ROCKET           = 7;
	public final static int MZ_GRENADE      = 8;
	public final static int MZ_LOGIN            = 9;
	public final static int MZ_LOGOUT           = 10;
	public final static int MZ_RESPAWN      = 11;
	public final static int MZ_BFG          = 12;
	public final static int MZ_SSHOTGUN         = 13;
	public final static int MZ_HYPERBLASTER     = 14;
	public final static int MZ_ITEMRESPAWN  = 15;
	public final static int MZ_SILENCED         = 128;      // bit flag ORed with one of the above numbers      

	// boxEntity() can return a list of either solid or trigger entities
	public final static int AREA_SOLID			= 1;
	public final static int AREA_TRIGGERS		= 2;	
	
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


	public native static int argc();


	public native static String args();


	public native static String argv(int n);

/**
 * This method was created by a SmartGuide.
 * @return NativeEntity[]
 * @param mins Vec3
 * @param maxs Vec3
 * @param maxCount int
 * @param areaType int
 */
public static NativeEntity[] boxEntities(Vec3 mins, Vec3 maxs, int areaType) 
	{
	return boxEntities0(mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, areaType);
	}

/**
 * This method was created by a SmartGuide.
 * @return NativeEntity[]
 * @param mins Vec3
 * @param maxs Vec3
 * @param maxCount int
 * @param areaType int
 */
private native static NativeEntity[] boxEntities0(float minsx, float minsy, float minsz,
	float maxsx, float maxsy, float maxsz, int areaType);


	public native static void bprint(int printLevel, String s);


	public native static void configString(int num, String s);


	public native static void debugGraph(float value, int color);


	//
	// Methods that natively call back to the Quake2 Program
	//

	public native static void dprint(String s);


	public native static void error(String s);


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public native static String getGamePath();


	public native static int imageIndex(String name);


	private native static boolean inP0(float x1, float y1, float z1, float x2, float y2, float z2, int calltype);

	public static boolean inPHS(Vec3 p1, Vec3 p2)
		{
		return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPHS);
		}
	public static boolean inPVS(Vec3 p1, Vec3 p2)
		{
		return inP0(p1.x, p1.y, p1.z, p2.z, p2.y, p2.z, CALL_INPVS);
		}

	public native static int modelIndex(String name);

	public static void multicast(Vec3 origin, int to)
		{
		write0(null, origin.x, origin.y, origin.z, to, CALL_MULTICAST);
		}
	public static int pointContents(Vec3 point)
		{
		return pointContents0(point.x, point.y, point.z);
		}

	private native static int pointContents0(float x, float y, float z);


	public native static void setAreaPortalState(int portalnum, boolean open);


	public native static int soundIndex(String name);

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
public static TraceResults trace(Vec3 start, Vec3 mins, Vec3 maxs, Vec3 end, NativeEntity passEnt, int contentMask) 
	{
	return trace0(start.x, start.y, start.z, mins.x, mins.y, mins.z, maxs.x, maxs.y, maxs.z, end.x, end.y, end.z, passEnt, contentMask);
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
	NativeEntity passEnt, int contentMask);

public static void unicast(NativeEntity ent, boolean reliable)
	{
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
	public static void writeDir(Vec3 pos)
		{
		write0(null, pos.x, pos.y, pos.z, 0, CALL_WRITEDIR);
		}
	public static void writeFloat(float f)
		{
		write0(null, f, 0, 0, 0, CALL_WRITEFLOAT);
		}
	public static void writeLong(int c)
		{
		write0(null, 0, 0, 0, c, CALL_WRITELONG);
		}
	public static void writePosition(Vec3 pos)
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