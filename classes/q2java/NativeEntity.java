package q2java;


import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.*;

/**
 * NativeEntity is equivalent to the C edict_t structure, and
 * every instance of NativeEntity has an matching instance of edict_t
 * that the DLL works with.
 *
 * @author Barry Pederson 
 */

public class NativeEntity
	{
	// ------------------- Instance fields ---------------------
	private int fEntityIndex;
	private NativeEntity fGroundEntity;	
	private boolean fIsBot;  // a pseudo-player

	// this field is not used by the DLL, but is available for 
	// a game to use however it wants through the getReference()
	// and setReference() methods.
	private Object fReference; 
		
	// -------------------- Static fields ----------------------		
		
	// fNumEntities is modified by the DLL to reflect 
	// the maximum number of entities potentially in use, 
	// so we don't have to look at every one when enumerating
	// through the array
	private static int gNumEntities; 
	private static int gMaxPlayers; // set by DLL
	private static NativeEntity[] gEntityArray;
		
	// --------------------- Constants -------------------------
		
	// types of entities we can create
	public final static int ENTITY_NORMAL = 0;
	public final static int ENTITY_WORLD  = 1;
	public final static int ENTITY_PLAYER = 2;		
		
	// sound channels
	// channel 0 never willingly overrides
	// other channels (1-7) allways override a playing sound on that channel
	public final static int CHAN_AUTO		= 0;
	public final static int CHAN_WEAPON		= 1;
	public final static int CHAN_VOICE		= 2;
	public final static int CHAN_ITEM		= 3;
	public final static int CHAN_BODY		= 4;
	// modifier flags
	public final static int CHAN_NO_PHS_ADD	= 8;    // send to all clients, not just ones in PHS (ATTN 0 will also do this)
	public final static int CHAN_RELIABLE	= 16;   // send by reliable message, not datagram

	// sound attenuation values
	public final static int ATTN_NONE		= 0;    // full volume the entire level
	public final static int ATTN_NORM		= 1;
	public final static int ATTN_IDLE		= 2;
	public final static int ATTN_STATIC		= 3;    // diminish very rapidly with distance


	// constants for setEffects()
	// Effects are things handled on the client side (lights, particles, frame animations)
	// that happen constantly on the given entity.
	// An entity that has effects will be sent to the client
	// even if it has a zero index model.
	public final static int EF_ROTATE			= 0x00000001;		// rotate (bonus items)
	public final static int EF_GIB				= 0x00000002;		// leave a trail
	public final static int EF_BLASTER			= 0x00000008;		// redlight + trail
	public final static int EF_ROCKET			= 0x00000010;		// redlight + trail
	public final static int EF_GRENADE			= 0x00000020;
	public final static int EF_HYPERBLASTER		= 0x00000040;
	public final static int EF_BFG				= 0x00000080;
	public final static int EF_COLOR_SHELL		= 0x00000100;
	public final static int EF_POWERSCREEN		= 0x00000200;
	public final static int EF_ANIM01			= 0x00000400;		// automatically cycle between frames 0 and 1 at 2 hz
	public final static int EF_ANIM23			= 0x00000800;		// automatically cycle between frames 2 and 3 at 2 hz
	public final static int EF_ANIM_ALL			= 0x00001000;		// automatically cycle through all frames at 2hz
	public final static int EF_ANIM_ALLFAST		= 0x00002000;		// automatically cycle through all frames at 10hz
	public final static int EF_FLIES			= 0x00004000;
	public final static int EF_QUAD				= 0x00008000;
	public final static int EF_PENT				= 0x00010000;
	public final static int EF_TELEPORTER		= 0x00020000;		// particle fountain
	public final static int EF_FLAG1			= 0x00040000;
	public final static int EF_FLAG2			= 0x00080000;
// RAFAEL
	public final static int EF_IONRIPPER		= 0x00100000;
	public final static int EF_GREENGIB			= 0x00200000;
	public final static int EF_BLUEHYPERBLASTER = 0x00400000;
	public final static int EF_SPINNINGLIGHTS	= 0x00800000;
	public final static int EF_PLASMA			= 0x01000000;
	public final static int EF_TRAP				= 0x02000000;
//ROGUE
	public final static int EF_TRACKER			= 0x04000000;
	public final static int EF_DOUBLE			= 0x08000000;
	public final static int EF_SPHERETRANS		= 0x10000000;
	public final static int EF_TAGTRAIL			= 0x20000000;
	public final static int EF_HALF_DAMAGE		= 0x40000000;
	public final static int EF_TRACKERTRAIL		= 0x80000000;
//ROGUE

	// constants for setEvent()
	// entity events are for effects that take place reletive
	// to an existing entities origin.  Very network efficient.
	public final static int EV_NONE				= 0;
	public final static int EV_ITEM_RESPAWN		= 1;
	public final static int EV_FOOTSTEP			= 2;
	public final static int EV_FALLSHORT		= 3;
	public final static int EV_FALL				= 4;
	public final static int EV_FALLFAR			= 5;
	public final static int EV_PLAYER_TELEPORT	= 6;
	public final static int EV_OTHER_TELEPORT	= 7;


	// setPMType() constants
	// information necessary for client side movement prediction
	//
	// can accelerate and turn
	public final static int PM_NORMAL		= 0;
	public final static int PM_SPECTATOR	= 1;
	// no acceleration or turning
	public final static int PM_DEAD			= 2;
	public final static int PM_GIB			= 3;	// different bounding box
	public final static int PM_FREEZE		= 4;

	// get/setPMFlags() constants
	public final static int PMF_DUCKED			= 1;
	public final static int PMF_JUMP_HELD		= 2;
	public final static int PMF_ON_GROUND		= 4;
	public final static int PMF_TIME_WATERJUMP	= 8;	 	// pm_time is waterjump
	public final static int PMF_TIME_LAND		= 16;	// pm_time is time before rejump
	public final static int PMF_TIME_TELEPORT	= 32;	// pm_time is non-moving time
	public final static int PMF_NO_PREDICTION	= 64;	// temporarily disables prediction (used for grappling hook)

	// player_state_t->refdef flags
	public final static int RDF_UNDERWATER		= 1;		// warp the screen as apropriate
	public final static int RDF_NOWORLDMODEL	= 2;		// used for player configuration screen
//ROGUE
	public final static int RDF_IRGOGGLES		= 4;
	public final static int RDF_UVGOGGLES		= 8;
//ROGUE

	// constants for setRenderFX()
	public final static int RF_MINLIGHT			= 1;		// allways have some light (viewmodel)
	public final static int RF_VIEWERMODEL		= 2;		// don't draw through eyes, only mirrors
	public final static int RF_WEAPONMODEL		= 4;		// only draw through eyes
	public final static int RF_FULLBRIGHT		= 8;		// allways draw full intensity
	public final static int RF_DEPTHHACK		= 16;	// for view weapon Z crunching
	public final static int RF_TRANSLUCENT		= 32;
	public final static int RF_FRAMELERP		= 64;
	public final static int RF_BEAM				= 128;
	public final static int RF_CUSTOMSKIN		= 256;	// skin is an index in image_precache
	public final static int RF_GLOW				= 512;	// pulse lighting for bonus items
	public final static int RF_SHELL_RED		= 1024;
	public final static int RF_SHELL_GREEN		= 2048;
	public final static int RF_SHELL_BLUE		= 4096;
//ROGUE
	public final static int RF_IR_VISIBLE		= 0x00008000;		// 32768
	public final static int RF_SHELL_DOUBLE		= 0x00010000;		// 65536
	public final static int RF_SHELL_HALF_DAM	= 0x00020000;
	public final static int RF_USE_DISGUISE		= 0x00040000;
//ROGUE

	// constants for setSolid()
	public final static int SOLID_NOT			= 0;		// no interaction with other objects
	public final static int SOLID_TRIGGER		= 1;		// only touch when inside, after moving
	public final static int SOLID_BBOX			= 2;		// touch on edge
	public final static int SOLID_BSP			= 3;		// bsp clip, touch on edge
		
	// setStat() field indexes
	public final static int STAT_HEALTH_ICON	= 0;
	public final static int STAT_HEALTH			= 1;
	public final static int STAT_AMMO_ICON		= 2;
	public final static int STAT_AMMO			= 3;
	public final static int STAT_ARMOR_ICON		= 4;
	public final static int STAT_ARMOR			= 5;
	public final static int STAT_SELECTED_ICON	= 6;
	public final static int STAT_PICKUP_ICON	= 7;
	public final static int STAT_PICKUP_STRING	= 8;
	public final static int STAT_TIMER_ICON		= 9;
	public final static int STAT_TIMER			= 10;
	public final static int STAT_HELPICON		= 11;
	public final static int STAT_SELECTED_ITEM	= 12;
	public final static int STAT_LAYOUTS		= 13;
	public final static int STAT_FRAGS			= 14;
	public final static int STAT_FLASHES		= 15; // cleared each frame, 1 = health, 2 = armor
	public final static int STAT_CHASE			= 16;
	public final static int STAT_SPECTATOR		= 17;	
	public final static int MAX_STATS			= 32;

	// constants for setSVFlags()
	public final static int SVF_NOCLIENT		= 0x00000001;	// don't send entity to clients, even if it has effects
	public final static int SVF_DEADMONSTER		= 0x00000002;	// treat as CONTENTS_DEADMONSTER for collision
	public final static int SVF_MONSTER			= 0x00000004;	// treat as CONTENTS_MONSTER for collision	


	// --- Private constants for communicating with the DLL
	private final static int BYTE_CLIENT_PS_PMOVE_PMFLAGS	= 100;
	private final static int BYTE_CLIENT_PS_PMOVE_PMTIME	= 101;
	
	private final static int SHORT_CLIENT_PS_PMOVE_GRAVITY	= 100;
		
	private final static int INT_S_MODELINDEX	= 1;
	private final static int INT_S_MODELINDEX2	= 2;
	private final static int INT_S_MODELINDEX3	= 3;
	private final static int INT_S_MODELINDEX4	= 4;
	private final static int INT_SVFLAGS		= 5;
	private final static int INT_SOLID			= 6;
	private final static int INT_CLIPMASK		= 7;
	private final static int INT_S_FRAME		= 8;
	private final static int INT_S_SKINNUM		= 9;
	private final static int INT_S_EFFECTS		= 10;
	private final static int INT_S_RENDERFX		= 11;
	private final static int INT_S_SOLID		= 12;
	private final static int INT_S_SOUND		= 13;
	private final static int INT_S_EVENT		= 14;
	private final static int INT_LINKCOUNT		= 15;
	private final static int INT_AREANUM		= 16;
	
	private final static int INT_CLIENT_PS_GUNINDEX	= 100;
	private final static int INT_CLIENT_PS_GUNFRAME	= 101;
	private final static int INT_CLIENT_PS_RDFLAGS	= 102;
	private final static int INT_CLIENT_PS_PMOVE_PMTYPE = 103;
	private final static int INT_CLIENT_PING			= 104;
	
	private final static int FLOAT_CLIENT_PS_FOV 		= 100;
	private final static int FLOAT_CLIENT_PS_BLEND 		= 101;			
	private final static int FLOAT_CLIENT_PS_BLEND_R	= 102;
	private final static int FLOAT_CLIENT_PS_BLEND_G 	= 103;
	private final static int FLOAT_CLIENT_PS_BLEND_B 	= 104;
	private final static int FLOAT_CLIENT_PS_BLEND_A 	= 105;

	private final static int VEC3_S_ORIGIN 			= 0;
	private final static int VEC3_S_ANGLES 			= 1;
	private final static int VEC3_S_OLD_ORIGIN 		= 2;
	private final static int VEC3_MINS 				= 3;
	private final static int VEC3_MAXS 				= 4;
	private final static int VEC3_ABSMIN 			= 5;
	private final static int VEC3_ABSMAX 			= 6;
	private final static int VEC3_SIZE 				= 7;
	private final static int VEC3_VELOCITY 			= 8;

	private final static int VEC3_CLIENT_PS_VIEWANGLES	= 100;
	private final static int VEC3_CLIENT_PS_VIEWOFFSET	= 101;
	private final static int VEC3_CLIENT_PS_KICKANGLES	= 102;
	private final static int VEC3_CLIENT_PS_GUNANGLES	= 103;
	private final static int VEC3_CLIENT_PS_GUNOFFSET	= 104;
	private final static int VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES = 105;

	private final static int ENTITY_OWNER		= 1;
	private final static int ENTITY_GROUND		= 2;
			
	private final static int CALL_SOUND = 1;
	private final static int CALL_POSITIONED_SOUND = 2;
	
	// Tuple types
//	private final static int TYPE_TUPLE 		= 0;
	private final static int TYPE_POINT 		= 1;
	private final static int TYPE_VECTOR 		= 2;
	private final static int TYPE_ANGLE 		= 3;
	
/**
 * Create a NativeEntity, which corresponds 
 * to a single Quake2 edict_t structure.
 *
 * @exception q2java.GameException when there are no more entities available.
 */
public NativeEntity () throws GameException
	{
	this(ENTITY_NORMAL, 0);
	}
/**
 * Create a NativeEntity, which corresponds 
 * to a single Quake2 edict_t structure.
 *
 * @param entType One of the ENTITY_* constants
 *
 * @exception q2java.GameException when there are no more entities available.
 */
public NativeEntity(int entType) throws GameException
	{
	this(entType, 0);
	}
/**
 * Create a NativeEntity, which corresponds 
 * to a single Quake2 edict_t structure.
 *
 * @param entType One of the ENTITY_* constants
 *
 * @exception q2java.GameException when there are no more entities available.
 */
private NativeEntity(int entType, int entIndex) throws GameException
	{
	if (entIndex != 0)
		fEntityIndex = entIndex;
	else
		{
		fEntityIndex = allocateEntity(entType);
		if (fEntityIndex < 0)
			throw new GameException("Can't allocate entity");
			
		fIsBot = (entType == ENTITY_PLAYER);
		}
					
	gEntityArray[fEntityIndex] = this;
	}
private native static int allocateEntity(int entType);
/** 
 * Print a message on the center of a player's screen.
 * Won't do anything for non-player entities or bots.
 *
 * @param s The message to print.
 */
public void centerprint(String s)
	{
	if (!fIsBot)
		centerprint0(fEntityIndex, s);
	}
/**
 * Player only
 */
private native static void centerprint0(int index, String msg);
/**
 * Copy settings from another entity.
 * @param source NativeEntity to copy settings from.
 */
public void copySettings(NativeEntity source) 
	{
	copySettings0(source.fEntityIndex, fEntityIndex);
	}
/**
 * This method was created by a SmartGuide.
 * @param sourceIndex int
 * @param destIndex int
 */
private native static void copySettings0(int sourceIndex, int destIndex);
/** 
 * Print a message on player's screen.
 * Won't do anything for non-player entities or bots.
 *
 * @param printLevel One of the Engine.PRINT_* constants.
 * @param s The message to print.
 */
public void cprint(int printLevel, String s)
	{
	if (!fIsBot)
		cprint0(fEntityIndex, printLevel, s);
	}
/**
 * Player Only
 */
private native static void cprint0(int index, int printlevel, String msg);
/**
 * Create an enumeration to run through all
 * the active NativeEntity objects in the system.
 */
public static Enumeration enumerateEntities() 
	{
	return new EntityEnumeration();
	}
/**
 * Create an enumeration to run through all
 * the active NativeEntity objects in the system
 * that are instances or descendants of a given class.
 *
 * @param targetClass name of the class we are looking for instances or descendants of.
 */
public static Enumeration enumerateEntities(String targetClassName) 
	{
	return new EntityEnumeration(targetClassName);
	}
/**
 * Create an enumeration to run through all
 * the active NativeEntity objects in the system
 * that are associated with active players.
 */
public static Enumeration enumeratePlayerEntities() 
	{
	return new PlayerEntityEnumeration();
	}
static NativeEntity findNext(NativeEntity start, Class targetClass)
	{
	NativeEntity result = null;
	int index = (start == null ? 0 : start.fEntityIndex + 1);
				
	while ((result == null) && (index < gNumEntities))
		{
		if (	(gEntityArray[index] != null) 
		&& 	((targetClass == null) || (targetClass.isAssignableFrom(gEntityArray[index].getClass())))
		)
			result = gEntityArray[index];
		index++;
		}
		
	return result;
	}
static NativeEntity findNextPlayer(NativeEntity start)
	{
	NativeEntity result = null;
	int index = (start == null ? 1 : start.fEntityIndex + 1);
				
	while ((result == null) && (index <= gMaxPlayers)) 
		{
		if (gEntityArray[index] != null) 
			result = gEntityArray[index];
		index++;
		}
		
	return result;
	}
/**
 * Removes this entity from the Quake2 DLL.
 * The NativeEntity itself will remain accessible
 * in Java, but all it's methods will generally be 
 * no-ops
 */
public void freeEntity()
	{
	if (fEntityIndex < 0)
		return; // already freed
		
	// remove from the DLL
	freeEntity0(fEntityIndex);	
	
	// remove from Java side of things
	gEntityArray[fEntityIndex] = null;
	fEntityIndex = -1;

	// clear references to other Java objects
	fGroundEntity = null;
	fReference = null;
	}
private native static void freeEntity0(int index);
public Point3f getAbsMaxs()
	{
	return (Point3f) getVec3(fEntityIndex, VEC3_ABSMAX, TYPE_POINT);
	}
public Point3f getAbsMins()
	{
	return (Point3f) getVec3(fEntityIndex, VEC3_ABSMIN, TYPE_POINT);
	}
public Angle3f getAngles()
	{
	return (Angle3f) getVec3(fEntityIndex, VEC3_S_ANGLES, TYPE_ANGLE);
	}
public int getAreaNum()
	{
	return getInt(fEntityIndex, INT_AREANUM);
	}
public NativeEntity[] getBoxEntities(int areaType) 
	{
	return getBoxEntities0(fEntityIndex, areaType);
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.NativeEntity[]
 * @param index int
 * @param maxCount int
 * @param areaType int
 */
private static native NativeEntity[] getBoxEntities0(int index, int areaType);
private native static byte getByte(int index, int fieldNum);
public int getClipmask()
	{
	return getInt(fEntityIndex, INT_CLIPMASK);
	}
public int getEffects()
	{
	return getInt(fEntityIndex, INT_S_EFFECTS);
	}
private native static NativeEntity getEntity(int index, int fieldNum);
/**
 * Return the index of this entity's corresponding
 * edict_t in the DLL's edict_t table.  Mostly needed
 * as a parameter for sending temporary effects to 
 * the clients.
 *
 * @return int
 */
public final int getEntityIndex() 
	{
	return fEntityIndex;
	}
public int getEvent()
	{
	return getInt(fEntityIndex, INT_S_EVENT);
	}
private native static float getFloat(int index, int fieldNum);
public int getFrame()
	{
	return getInt(fEntityIndex, INT_S_FRAME);
	}
/**
 * Find what this entity is resting on.  This needs to
 * be set by the Java game, but is used by the DLL
 * in the getPotentialPushed() method.
 *
 * @return The NativeEntity this entity is standing
 * on, null if in the air or unknown.
 */
public NativeEntity getGroundEntity() 
	{
	return fGroundEntity;
	}
private native static int getInt(int index, int fieldNum);
public int getLinkCount()
	{
	return getInt(fEntityIndex, INT_LINKCOUNT);
	}
/**
 * Get the maximum number of players allowed in the game.
 * This is controlled by the engine, so there's no equivalent "set"
 * method.
 *
 * @return int
 */
public static int getMaxPlayers() 
	{
	return gMaxPlayers;
	}
public Point3f getMaxs()
	{
	return (Point3f) getVec3(fEntityIndex, VEC3_MAXS, TYPE_POINT);
	}
public Point3f getMins()
	{
	return (Point3f) getVec3(fEntityIndex, VEC3_MINS, TYPE_POINT);
	}
public int getModelIndex()
	{
	return getInt(fEntityIndex, INT_S_MODELINDEX);
	}
public int getModelIndex2()
	{
	return getInt(fEntityIndex, INT_S_MODELINDEX2);
	}
public int getModelIndex3()
	{
	return getInt(fEntityIndex, INT_S_MODELINDEX3);
	}
public int getModelIndex4()
	{
	return getInt(fEntityIndex, INT_S_MODELINDEX4);
	}
public Point3f getOrigin()
	{
	return (Point3f) getVec3(fEntityIndex, VEC3_S_ORIGIN, TYPE_POINT);
	}
public NativeEntity getOwner() 
	{
	return getEntity(fEntityIndex, ENTITY_OWNER);
	}
/**
 * Get the blend colors on the player's screen.
 * @param dest Color4f to place values in - if null then a new Color4f will be created and returned.
 * @return Color4f representation of RGBA
 */
public Color4f getPlayerBlend(Color4f dest) 
	{
	float r = getFloat(fEntityIndex, FLOAT_CLIENT_PS_BLEND_R);
	float g = getFloat(fEntityIndex, FLOAT_CLIENT_PS_BLEND_G);
	float b = getFloat(fEntityIndex, FLOAT_CLIENT_PS_BLEND_B);
	float a = getFloat(fEntityIndex, FLOAT_CLIENT_PS_BLEND_A);
	
	if (dest == null)
		dest = new Color4f(r, g, b, a);
	else
		dest.set(r, g, b, a);

	return dest;		
	}
public Angle3f getPlayerDeltaAngles()
	{
	return (Angle3f) getVec3(fEntityIndex, VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES, TYPE_ANGLE);
	}
/**
 * Get the Player's Field of View. 
 *
 * @return Field of View in degrees
 */
public float getPlayerFOV() 
	{
	return getFloat(fEntityIndex, FLOAT_CLIENT_PS_FOV);
	}
public short getPlayerGravity()
	{
	return getShort(fEntityIndex, SHORT_CLIENT_PS_PMOVE_GRAVITY);
	}
public int getPlayerGunFrame()
	{
	return getInt(fEntityIndex, INT_CLIENT_PS_GUNFRAME);
	}
public int getPlayerGunIndex()
	{
	return getInt(fEntityIndex, INT_CLIENT_PS_GUNINDEX);
	}
/**
 * Find a player's info string.
 * @return Player info.
 */
public String getPlayerInfo() 
	{
	return getPlayerInfo0(fEntityIndex);
	}
/**
 * Find a player's info string.
 * @return Player info.
 */
private static native String getPlayerInfo0(int index);
public Angle3f getPlayerKickAngles()
	{
	return (Angle3f) getVec3(fEntityIndex, VEC3_CLIENT_PS_KICKANGLES, TYPE_ANGLE);
	}
/**
 * Find the object that's receiving this Player's events.
 * @return Object receiving this entity's Player events, null if not a player.
 */
public PlayerListener getPlayerListener() 
	{
	return getPlayerListener0(fEntityIndex);
	}
/**
 * Set which object responds to this player's events.
 * @param pl q2java.PlayerListener
 */
private native static PlayerListener getPlayerListener0(int index);
/**
 * Get this player's number, needed for things like setting
 * skins with the Engine.configString() method.
 *
 * @return The zero-based number of this player, -1 if not a player
 */
public int getPlayerNum() 
	{
	if ((fEntityIndex == 0) || (fEntityIndex > gMaxPlayers))
		return -1; // maybe this should be an exception

	return fEntityIndex - 1;
	}
public int getPlayerPing()
	{
	return getInt(fEntityIndex, INT_CLIENT_PING);
	}
public byte getPlayerPMFlags()
	{
	return getByte(fEntityIndex, BYTE_CLIENT_PS_PMOVE_PMFLAGS);
	}
public int getPlayerRDFlags()
	{
	return getInt(fEntityIndex, INT_CLIENT_PS_RDFLAGS);
	}
public Angle3f getPlayerViewAngles()
	{
	return (Angle3f) getVec3(fEntityIndex, VEC3_CLIENT_PS_VIEWANGLES, TYPE_ANGLE);
	}
public Vector3f getPlayerViewOffset()
	{
	return (Vector3f) getVec3(fEntityIndex, VEC3_CLIENT_PS_VIEWOFFSET, TYPE_VECTOR);
	}
/**
 * Get an array of entities that might need to be pushed.
 *
 * This method is needed to speed up things like elevators
 * and trains...otherwise Java'd have to iterate through
 * hundreds and hundreds of entities every server frame,
 * trying to decide which ones to move.
 * Java's not fast enough especially
 * on levels such as "ware2" with 45-50 trains all running
 * simultaneously.  Without this native support, a pure
 * Java solution drags the game down to about 2fps on a
 * PentiumPro 233. This method cuts the number of entities
 * the Java code has to check down to just a handful, which
 * it can do fairly well.
 *
 * @return An array of entities that either might intersect 
 * this object, or have it as their ground entity.
 */
public NativeEntity[] getPotentialPushed(Tuple3f mins, Tuple3f maxs, int defaultMask)
	{
	return getPotentialPushed0(fEntityIndex, 
		mins.x, mins.y, mins.z, 
		maxs.x, maxs.y, maxs.z, 
		defaultMask);
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.NativeEntity[]
 * @param index int
 */
private static native NativeEntity[] getPotentialPushed0(int index, 
	float minx, float miny, float minz, 
	float maxx, float maxy, float maxz,
	int defaultMask);
/**
 * Get a list of entities who have their origin within
 * a certain radius of this entity's origin.
 */
public NativeEntity[] getRadiusEntities(float radius, boolean onlyPlayers, boolean sortResults) 
	{
	return getRadiusEntities0(fEntityIndex, radius, onlyPlayers, sortResults);
	}
/**
 * Get a list of entities who have their origin within
 * a certain radius of this entity's origin.
 */
private native static NativeEntity[] getRadiusEntities0(int index, float radius, boolean onlyPlayers, boolean sortResults);
/**
 * Get the object that was set by setReference().
 * Not used by the DLL, but available for a game to
 * use however it wants.
 *
 * @return java.lang.Object
 */
public Object getReference() 
	{
	return fReference;
	}
public int getRenderFX()
	{
	return getInt(fEntityIndex, INT_S_RENDERFX);
	}
private native static short getShort(int index, int fieldNum);
public Vector3f getSize()
	{
	return (Vector3f) getVec3(fEntityIndex, VEC3_SIZE, TYPE_VECTOR);
	}
public int getSkinNum()
	{
	return getInt(fEntityIndex, INT_S_SKINNUM);
	}
public int getSolid()
	{
	return getInt(fEntityIndex, INT_SOLID);
	}
public int getSound()
	{
	return getInt(fEntityIndex, INT_S_SOUND);
	}
public int getSVFlags()
	{
	return getInt(fEntityIndex, INT_SVFLAGS);
	}
private native static Tuple3f getVec3(int index, int fieldNum, int tupleType);
public Vector3f getVelocity()
	{
	return (Vector3f) getVec3(fEntityIndex, VEC3_VELOCITY, TYPE_VECTOR);
	}
/**
 * Is this entity a player entity but not connected to
 * an actual Q2 client?
 * @return boolean
 */
public boolean isBot() 
	{
	return fIsBot;
	}
/**
 * Check whether or not this entity is one of the special ones used by players.
 * @return boolean
 */
public boolean isPlayer() 
	{
	return (fEntityIndex > 0) && (fEntityIndex <= gMaxPlayers);
	}
/**
 * Is this entity active, or has it been freed.
 * @return true if active, false if it's been freed.
 */
public boolean isValid() 
	{
	return (fEntityIndex >= 0);
	}
public void linkEntity()
	{
	linkEntity0(fEntityIndex);
	}
private native static void linkEntity0(int index);
/**
 * Move the player.
 * @param cmd A PlayerCmd usually received by a class implementing
 *   the NativePlayer.playerThink() method
 */
public PMoveResults pMove(PlayerCmd cmd, int traceMask) 
	{
	return pMove0(fEntityIndex, cmd.fMsec, cmd.fButtons, 
		cmd.fPitch, cmd.fYaw, cmd.fRoll, 
		cmd.fForwardMove, cmd.fSideMove, cmd.fUpMove,
		cmd.fImpulse, cmd.fLightLevel, traceMask);
	}
/**
 * Player Only
 */
private static native PMoveResults pMove0(int index, byte msec, byte buttons,
	short angle0, short angle1, short angle2,
	short forward, short side, short up,
	byte impulse, byte lightlevel, int traceMask);
public void positionedSound(Point3f origin, int channel, int soundindex, float volume, float attenuation, float timeofs)
	{   
	sound0(origin.x, origin.y, origin.z, fEntityIndex, channel, soundindex, volume, attenuation, timeofs, CALL_POSITIONED_SOUND);
	}
public void setAngles(float pitch, float yaw, float roll)
	{
	setVec3(fEntityIndex, VEC3_S_ANGLES, pitch, yaw, roll);
	}
public void setAngles(Angle3f a)
	{
	setVec3(fEntityIndex, VEC3_S_ANGLES, a.x, a.y, a.z);
	}
//
// protected so that NativePlayer can call on it to set a few fields
// in the client structures
//
private native static void setByte(int index, int fieldNum, byte val);
public void setClipmask(int val)
	{
	setInt(fEntityIndex, INT_CLIPMASK, val);
	}
public void setEffects(int val)
	{
	setInt(fEntityIndex, INT_S_EFFECTS, val);
	}
//
// protected so that NativePlayer can call on it to set a few fields
// in the client structures
//
private native static void setEntity(int index, int fieldNum, int valIndex);
public void setEvent(int val)
	{
	setInt(fEntityIndex, INT_S_EVENT, val);
	}
/**
 * Player Only
 */
private native static void setFloat0(int index, int fieldNum, float r, float g, float b, float a);
public void setFrame(int val)
	{
	setInt(fEntityIndex, INT_S_FRAME, val);
	}
/**
 * Set a reference to the entity this entity is standing on.
 * @param ent q2java.NativeEntity
 */
public void setGroundEntity(NativeEntity ent) 
	{
	fGroundEntity = ent;
	
	if (ent == null)
		setEntity(fEntityIndex, ENTITY_GROUND, -1);
	else		
		setEntity(fEntityIndex, ENTITY_GROUND, ent.fEntityIndex);
	}
//
// protected so that NativePlayer can call on it to set a few fields
// in the client structures
//
private native static void setInt(int index, int fieldNum, int val);
public void setMaxs(float x, float y, float z)
	{
	setVec3(fEntityIndex, VEC3_MAXS, x, y, z);
	}
public void setMaxs(Tuple3f v)
	{
	setVec3(fEntityIndex, VEC3_MAXS, v.x, v.y, v.z);
	}
public void setMins(float x, float y, float z)
	{
	setVec3(fEntityIndex, VEC3_MINS, x, y, z);
	}
public void setMins(Tuple3f v)
	{
	setVec3(fEntityIndex, VEC3_MINS, v.x, v.y, v.z);
	}
public void setModel(String name)
	{
	setModel0(fEntityIndex, name);
	}
private native static void setModel0(int index, String name);
public void setModelIndex(int val)
	{
	setInt(fEntityIndex, INT_S_MODELINDEX, val);
	}
public void setModelIndex2(int val)
	{
	setInt(fEntityIndex, INT_S_MODELINDEX2, val);
	}
public void setModelIndex3(int val)
	{
	setInt(fEntityIndex, INT_S_MODELINDEX3, val);
	}
public void setModelIndex4(int val)
	{
	setInt(fEntityIndex, INT_S_MODELINDEX4, val);
	}
public void setOrigin(Point3f p)
	{
	setVec3(fEntityIndex, VEC3_S_ORIGIN, p.x, p.y, p.z);
	}
public void setOwner(NativeEntity ent) 
	{
	if (ent == null)
		setEntity(fEntityIndex, ENTITY_OWNER, -1);
	else		
		setEntity(fEntityIndex, ENTITY_OWNER, ent.fEntityIndex);
	}
/**
 * Set the blend colors on the player's screen.
 * @param r Red amount
 * @param g Green amount
 * @param b Blue amount
 * @param a Alpha amount
 */
public void setPlayerBlend(float r, float g, float b, float a) 
	{
	setFloat0(fEntityIndex, FLOAT_CLIENT_PS_BLEND, r, g, b, a);
	}
/**
 * Set the blend colors on the player's screen.
 * @param c Color4f representation of RGBA
 */
public void setPlayerBlend(Color4f c) 
	{
	setFloat0(fEntityIndex, FLOAT_CLIENT_PS_BLEND, c.x, c.y, c.z, c.w);
	}
public void setPlayerDeltaAngles(float pitch, float yaw, float roll)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES, pitch, yaw, roll);
	}
public void setPlayerDeltaAngles(Angle3f a)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES, a.x, a.y, a.z);
	}
/**
 * Set the Player's Field of View. 
 *
 * @param v Field of View in degrees
 */
public void setPlayerFOV(float v) 
	{
	setFloat0(fEntityIndex, FLOAT_CLIENT_PS_FOV, v, 0, 0, 0);
	}
public void setPlayerGravity(short val)
	{
	setShort(fEntityIndex, SHORT_CLIENT_PS_PMOVE_GRAVITY, val);
	}
public void setPlayerGunAngles(Angle3f a)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_GUNANGLES, a.x, a.y, a.z);
	}
public void setPlayerGunFrame(int val)
	{
	setInt(fEntityIndex, INT_CLIENT_PS_GUNFRAME, val);
	}
public void setPlayerGunIndex(int val)
	{
	setInt(fEntityIndex, INT_CLIENT_PS_GUNINDEX, val);
	}
public void setPlayerGunOffset(Vector3f v)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_GUNOFFSET, v.x, v.y, v.z);
	}
public void setPlayerKickAngles(Angle3f a)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_KICKANGLES, a.x, a.y, a.z);
	}
/**
 * Register an object to receive this entity's player events.
 * Has no effect if not a player entity.
 * @param pl q2java.PlayerListener
 */
public void setPlayerListener(PlayerListener pl) 
	{
	setPlayerListener0(fEntityIndex, pl);
	}
/**
 * Set which object responds to this player's events.
 * @param pl q2java.PlayerListener
 */
private native static void setPlayerListener0(int index, PlayerListener pl);
public void setPlayerPMFlags(byte b)
	{
	setByte(fEntityIndex, BYTE_CLIENT_PS_PMOVE_PMFLAGS, b);
	}
public void setPlayerPMTime(byte val)
	{
	setByte(fEntityIndex, BYTE_CLIENT_PS_PMOVE_PMTIME, val);
	}
public void setPlayerPMType(int val)
	{
	setInt(fEntityIndex, INT_CLIENT_PS_PMOVE_PMTYPE, val);
	}
public void setPlayerRDFlags(int val)
	{
	setInt(fEntityIndex, INT_CLIENT_PS_RDFLAGS, val);
	}
public void setPlayerStat(int fieldIndex, short value) 
	{
	setStat0(fEntityIndex, fieldIndex, value);
	}
public void setPlayerViewAngles(float pitch, float yaw, float roll)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_VIEWANGLES, pitch, yaw, roll);
	}
public void setPlayerViewAngles(Angle3f a)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_VIEWANGLES, a.x, a.y, a.z);
	}
public void setPlayerViewOffset(Vector3f v)
	{
	setVec3(fEntityIndex, VEC3_CLIENT_PS_VIEWOFFSET, v.x, v.y, v.z);
	}
/**
 * Store a handle to an arbitrary object.
 * Not used by the DLL, but available for a game to
 * use however it wants.
 *
 * @param obj java.lang.Object
 */
public void setReference(Object obj) 
	{
	fReference = obj;
	}
public void setRenderFX(int val)
	{
	setInt(fEntityIndex, INT_S_RENDERFX, val);
	}
//
// protected so that NativePlayer can call on it to set a few fields
// in the client structures
//
private native static void setShort(int index, int fieldNum, short val);
public void setSkinNum(int val)
	{
	setInt(fEntityIndex, INT_S_SKINNUM, val);
	}
public void setSolid(int val)
	{
	setInt(fEntityIndex, INT_SOLID, val);
	}
public void setSound(int val)
	{
	setInt(fEntityIndex, INT_S_SOUND, val);
	}
/**
 * Player only
 * @param index int
 * @param fieldIndex int
 * @param value int
 */
private native static void setStat0(int index, int fieldIndex, short value);
public void setSVFlags(int val)
	{
	setInt(fEntityIndex, INT_SVFLAGS, val);
	}
private native static void setVec3(int index, int fieldNum, float x, float y, float z);
public void setVelocity(float x, float y, float z)
	{
	setVec3(fEntityIndex, VEC3_VELOCITY, x, y, z);
	}
public void setVelocity(Vector3f v)
	{
	setVec3(fEntityIndex, VEC3_VELOCITY, v.x, v.y, v.z);
	}
public void sound(int channel, int soundindex, float volume, float attenuation, float timeofs)
	{   
	sound0(0, 0, 0, fEntityIndex, channel, soundindex, volume, attenuation, timeofs, CALL_SOUND);
	}
private native static void sound0(float x, float y, float z, int index, int channel, int soundindex, float volume, float attenuation, float timeofs, int calltype);
/**
 * Provide a string representation of the object, useful
 * for debugging.
 */
public String toString()
	{
	StringBuffer sb = new StringBuffer();
	sb.append('[');
	sb.append(fEntityIndex);
	sb.append("] ");
	sb.append(this.getClass().getName());

	return sb.toString();
	}
/**
 * Shortcut method to quickly move an entity based on its velocity
 * and return a TraceResults object.
 *
 * @return q2java.TraceResults
 * @param mask int
 */
public TraceResults traceMove(int contentMask, float frameFraction) 
	{
	return traceMove0(fEntityIndex, contentMask, frameFraction);
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.TraceResults
 * @param mask int
 */
private native static TraceResults traceMove0(int index, int contentMask, float frameFraction);
public void unlinkEntity()
	{
	unlinkEntity0(fEntityIndex);
	}
private native static void unlinkEntity0(int index);
}