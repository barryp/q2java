
package q2java;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Vector;

public abstract class NativeEntity
	{
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

	// constants for setEvent()
	// entity events are for effects that take place reletive
	// to an existing entities origin.  Very network efficient.
	public final static int EV_NONE				= 0;
	public final static int EV_ITEM_RESPAWN		= 1;
	public final static int EV_FOOTSTEP			= 2;
	public final static int EV_FALLSHORT			= 3;
	public final static int EV_MALE_FALL			= 4;
	public final static int EV_MALE_FALLFAR		= 5;
	public final static int EV_FEMALE_FALL		= 6;
	public final static int EV_FEMALE_FALLFAR	= 7;
	public final static int EV_PLAYER_TELEPORT	= 8;

	// player_state_t->refdef flags
	public final static int RDF_UNDERWATER		= 1;		// warp the screen as apropriate
	public final static int RDF_NOWORLDMODEL		= 2;		// used for player configuration screen

	// constants for setRenderFX()
	public final static int RF_MINLIGHT			= 1;		// allways have some light (viewmodel)
	public final static int RF_VIEWERMODEL		= 2;		// don't draw through eyes, only mirrors
	public final static int RF_WEAPONMODEL		= 4;		// only draw through eyes
	public final static int RF_FULLBRIGHT		= 8;		// allways draw full intensity
	public final static int RF_DEPTHHACK			= 16;	// for view weapon Z crunching
	public final static int RF_TRANSLUCENT		= 32;
	public final static int RF_FRAMELERP			= 64;
	public final static int RF_BEAM				= 128;
	public final static int RF_CUSTOMSKIN		= 256;	// skin is an index in image_precache
	public final static int RF_GLOW				= 512;	// pulse lighting for bonus items
	public final static int RF_SHELL_RED			= 1024;
	public final static int RF_SHELL_GREEN		= 2048;
	public final static int RF_SHELL_BLUE		= 4096;

	// constants for setSolid()
	public final static int SOLID_NOT			= 0;		// no interaction with other objects
	public final static int SOLID_TRIGGER		= 1;		// only touch when inside, after moving
	public final static int SOLID_BBOX			= 2;		// touch on edge
	public final static int SOLID_BSP			= 3;		// bsp clip, touch on edge

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
	
	// setStat() field indexes
	public final static int STAT_HEALTH_ICON    = 0;
	public final static int STAT_HEALTH         = 1;
	public final static int STAT_AMMO_ICON      = 2;
	public final static int STAT_AMMO           = 3;
	public final static int STAT_ARMOR_ICON     = 4;
	public final static int STAT_ARMOR          = 5;
	public final static int STAT_SELECTED_ICON  = 6;
	public final static int STAT_PICKUP_ICON    = 7;
	public final static int STAT_PICKUP_STRING  = 8;
	public final static int STAT_TIMER_ICON     = 9;
	public final static int STAT_TIMER          = 10;
	public final static int STAT_HELPICON       = 11;
	public final static int STAT_SELECTED_ITEM  = 12;
	public final static int STAT_LAYOUTS        = 13;
	public final static int STAT_FRAGS          = 14;
	public final static int STAT_FLASHES        = 15; // cleared each frame, 1 = health, 2 = armor
	public final static int MAX_STATS           = 32;



	//
	// handles to C entity fields
	// (same as constants in Entity.c)
	//
	private final static int VEC3_S_ORIGIN 			= 0;
	private final static int VEC3_S_ANGLES 			= 1;
	private final static int VEC3_S_OLD_ORIGIN 		= 2;
	private final static int VEC3_MINS 				= 3;
	private final static int VEC3_MAXS 				= 4;
	private final static int VEC3_ABSMIN 			= 5;
	private final static int VEC3_ABSMAX 			= 6;
	private final static int VEC3_SIZE 				= 7;
	private final static int VEC3_VELOCITY 			= 8;

	// ONLY use these in NativePlayer
	protected final static int VEC3_CLIENT_PS_VIEWANGLES	= 9;
	protected final static int VEC3_CLIENT_PS_VIEWOFFSET	= 10;
	protected final static int VEC3_CLIENT_PS_KICKANGLES	= 11;
	protected final static int VEC3_CLIENT_PS_GUNANGLES	= 12;
	protected final static int VEC3_CLIENT_PS_GUNOFFSET	= 13;
	
	private final static int INT_S_MODELINDEX	= 1;
	private final static int INT_S_MODELINDEX2	= 2;
	private final static int INT_S_MODELINDEX3	= 3;
	private final static int INT_S_MODELINDEX4	= 4;
	private final static int INT_SVFLAGS			= 5;
	private final static int INT_SOLID			= 6;
	private final static int INT_CLIPMASK		= 7;
	private final static int INT_S_FRAME			= 8;
	private final static int INT_S_SKINNUM		= 9;
	private final static int INT_S_EFFECTS		= 10;
	private final static int INT_S_RENDERFX		= 11;
//	private final static int INT_S_SOLID			= 12; // is set by linkentity()
	private final static int INT_S_SOUND			= 13;
	private final static int INT_S_EVENT			= 14;
	
	// ONLY use these in NativePlayer
	protected final static int INT_CLIENT_PS_GUNINDEX = 15;
	protected final static int INT_CLIENT_PS_GUNFRAME = 16;
	protected final static int INT_CLIENT_PS_RDFLAGS	= 17;
	
	// private flags for setFloat0()
	private final static int FLOAT_CLIENT_PS_FOV 		= 0;
	private final static int FLOAT_CLIENT_PS_BLEND 	= 1;			
		
	private final static int CALL_SOUND = 1;
	private final static int CALL_POSITIONED_SOUND = 2;

	private static NativeEntity[] fEntityArray;
	private int fEntityIndex;
	
/**
 * This method was created by a SmartGuide.
 */
protected NativeEntity () throws GameException
	{
	this(false);
	}
protected NativeEntity(boolean isWorld) throws GameException
	{
	if (fEntityIndex == 0) // players will already have this set to something > 0
		{
		fEntityIndex = allocateEntity(isWorld);
		if (fEntityIndex < 0)
			throw new GameException("Can't allocate entity");
		}
					
	fEntityArray[fEntityIndex] = this;
	}

private native static int allocateEntity(boolean isWorld);

/** 
 * Player Only
 */
public void centerprint(String s)
	{
	centerprint0(getEntityIndex(), s);
	}

/**
 * Player only
 */
private native static void centerprint0(int index, String msg);

public static void clearAllEntities()
	{
	fEntityArray = null;
	}
/**
 * Player Only
 */
public void cprint(int printLevel, String s)
	{
	cprint0(getEntityIndex(), printLevel, s);
	}

/**
 * Player Only
 */
private native static void cprint0(int index, int printlevel, String msg);

public static Enumeration enumerateEntities(String targetClassName) 
	{
	return new EntityEnumeration(targetClassName);
	}
public static NativeEntity findNext(NativeEntity start, Class targetClass)
	{
	NativeEntity result = null;
	int index;
	if (start == null)
		index = 0;
	else 
		index = start.fEntityIndex + 1;
				
	while ((result == null) && (index < NativeEntity.fEntityArray.length))
		{
		if (	(NativeEntity.fEntityArray[index] != null) 
		&& 	((targetClass == null) || (targetClass.isAssignableFrom(NativeEntity.fEntityArray[index].getClass())))
		)
			result = NativeEntity.fEntityArray[index];
		index++;
		}
		
	return result;
	}
/**
 * This method was created by a SmartGuide.
 */
public void freeEntity() throws GameException
	{
	fEntityArray[fEntityIndex] = null;
	freeEntity0(fEntityIndex);
	
	// using an entity after it's been freed will
	// probably cause Quake II to go down in flames, 
	// but at least it won't be a subtle bug 
	// that you aren't aware of :)
	
	fEntityIndex = -1;
	}

private native static void freeEntity0(int index);

public Vec3 getAngles()
	{
	return getVec3(fEntityIndex, VEC3_S_ANGLES);
	}
public int getEffects()
	{
	return getInt(fEntityIndex, INT_S_EFFECTS);
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public final int getEntityIndex() 
	{
	return fEntityIndex;
	}

private native static int getInt(int index, int fieldNum);

public Vec3 getMaxs()
	{
	return getVec3(fEntityIndex, VEC3_MAXS);
	}
public Vec3 getMins()
	{
	return getVec3(fEntityIndex, VEC3_MINS);
	}
public Vec3 getOrigin()
	{
	return getVec3(fEntityIndex, VEC3_S_ORIGIN);
	}
/**
 * Player Only
 *
 * Return the index of this player object (zero-based)
 * @return int
 */
public int getPlayerNum() 
	{
	return getEntityIndex() - 1;
	}

private native static Vec3 getVec3(int index, int fieldNum);

public Vec3 getVelocity()
	{
	return getVec3(fEntityIndex, VEC3_VELOCITY);
	}
public void linkEntity()
	{
	linkEntity0(fEntityIndex);
	}

private native static void linkEntity0(int index);

/**
 * Player Only
 */
public PMoveResults pMove() 
	{
	return pMove0(getEntityIndex());
	}

/**
 * Player Only
 */
private static native PMoveResults pMove0(int index);

public void positionedSound(Vec3 origin, int channel, int soundindex, float volume, float attenuation, float timeofs)
	{   
	sound0(origin.x, origin.y, origin.z, fEntityIndex, channel, soundindex, volume, attenuation, timeofs, CALL_POSITIONED_SOUND);
	}
public void setAngle(float f)
	{
	setVec3(fEntityIndex, VEC3_S_ANGLES, 0, f, 0);
	}
public void setAngles(Vec3 v)
	{
	setVec3(fEntityIndex, VEC3_S_ANGLES, v.x, v.y, v.z);
	}
/**
 * Player Only
 * @param r float
 * @param g float
 * @param b float
 * @param a float
 */
public void setBlend(float r, float g, float b, float a) 
	{
	setFloat0(getEntityIndex(), FLOAT_CLIENT_PS_BLEND, r, g, b, a);
	}
public void setClipmask(int val)
	{
	setInt(fEntityIndex, INT_CLIPMASK, val);
	}
public void setEffects(int val)
	{
	setInt(fEntityIndex, INT_S_EFFECTS, val);
	}
public void setEvent(int val)
	{
	setInt(fEntityIndex, INT_S_EVENT, val);
	}

/**
 * Player Only
 */
private native static void setFloat0(int index, int fieldNum, float r, float g, float b, float a);

/**
 * Player Only
 * @param r float
 * @param g float
 * @param b float
 * @param a float
 */
public void setFOV(float v) 
	{
	setFloat0(getEntityIndex(), FLOAT_CLIENT_PS_FOV, v, 0, 0, 0);
	}
public void setFrame(int val)
	{
	setInt(fEntityIndex, INT_S_FRAME, val);
	}
/**
 * Player Only
 */
public void setGunAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_GUNANGLES, v.x, v.y, v.z);
	}
/**
 * Player Only
 */
public void setGunFrame(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_GUNFRAME, val);
	}
/**
 * Player Only
 */
public void setGunIndex(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_GUNINDEX, val);
	}
/**
 * Player Only
 */
public void setGunOffset(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_GUNOFFSET, v.x, v.y, v.z);
	}

//
// protected so that NativePlayer can call on it to set a few fields
// in the client structures
//
protected native static void setInt(int index, int fieldNum, int val);

/**
 * Player Only
 */
public void setKickAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_KICKANGLES, v.x, v.y, v.z);
	}
public static void setMaxEntities(int max)
	{
	fEntityArray = new NativeEntity[max];
	}
public void setMaxs(Vec3 v)
	{
	setVec3(fEntityIndex, VEC3_MAXS, v.x, v.y, v.z);
	}
public void setMins(Vec3 v)
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
public void setOrigin(Vec3 v)
	{
	setVec3(fEntityIndex, VEC3_S_ORIGIN, v.x, v.y, v.z);
	}
/**
 * Player Only
 */
public void setRDFlags(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_RDFLAGS, val);
	}
public void setRenderFX(int val)
	{
	setInt(fEntityIndex, INT_S_RENDERFX, val);
	}
public void setSolid(int val)
	{
	setInt(fEntityIndex, INT_SOLID, val);
	}
/**
 * Player Only
 * @param fieldindex int
 * @param value int
 */
public void setStat(int fieldindex, short value) 
	{
	setStat0(getEntityIndex(), fieldindex, value);
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

protected native static void setVec3(int index, int fieldNum, float x, float y, float z);

public void setVelocity(Vec3 v)
	{
	setVec3(fEntityIndex, VEC3_S_ORIGIN, v.x, v.y, v.z);
	}
/**
 * Player Only
 */
public void setViewAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_VIEWANGLES, v.x, v.y, v.z);
	}
/**
 * Player Only
 */
public void setViewOffset(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_VIEWOFFSET, v.x, v.y, v.z);
	}
public void sound(int channel, int soundindex, float volume, float attenuation, float timeofs)
	{   
	sound0(0, 0, 0, fEntityIndex, channel, soundindex, volume, attenuation, timeofs, CALL_SOUND);
	}

private native static void sound0(float x, float y, float z, int index, int channel, int soundindex, float volume, float attenuation, float timeofs, int calltype);

public String toString()
	{
	StringBuffer sb = new StringBuffer();
	sb.append('[');
	sb.append(fEntityIndex);
	sb.append("] ");
	sb.append(this.getClass().getName());

	return sb.toString();
	}
public void unlinkEntity()
	{
	unlinkEntity0(fEntityIndex);
	}

private native static void unlinkEntity0(int index);

}