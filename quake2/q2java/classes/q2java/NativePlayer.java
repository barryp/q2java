
public abstract class NativePlayer extends GenericMobile
	{
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
	
	// private flags for setFloat0()
	private final static int FLOAT_CLIENT_PS_FOV 		= 0;
	private final static int FLOAT_CLIENT_PS_BLEND 	= 1;		
	
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
protected NativePlayer() throws GameException
	{
	Game.debugLog("new NativePlayer()");	
	}
/**
 * This method was created by a SmartGuide.
 * @param loadgame boolean
 */
public abstract void begin(boolean loadgame);
public void centerprint(String s)
	{
	centerprint0(getEntityIndex(), s);
	}

private native static void centerprint0(int index, String msg);

/**
 * This method was created by a SmartGuide.
 */
public abstract void command();
public void cprint(int printLevel, String s)
	{
	cprint0(getEntityIndex(), printLevel, s);
	}

private native static void cprint0(int index, int printlevel, String msg);

/**
 * This method was created by a SmartGuide.
 */
public abstract void disconnect();
/**
 * This method was created by a SmartGuide.
 */
public void freeEntity() throws GameException
	{
	throw new GameException("You can't free a player entity");
	}
/**
 * Return the index of this player object (zero-based)
 * @return int
 */
public int getPlayerNum() 
	{
	return getEntityIndex() - 1;
	}
/**
 * This method was created by a SmartGuide.
 */
public PMoveResults pMove() 
	{
	return pMove0(getEntityIndex());
	}

/**
 * This method was created by a SmartGuide.
 */
private static native PMoveResults pMove0(int index);

/**
 * This method was created by a SmartGuide.
 * @param r float
 * @param g float
 * @param b float
 * @param a float
 */
public void setBlend(float r, float g, float b, float a) 
	{
	setFloat0(getEntityIndex(), FLOAT_CLIENT_PS_BLEND, r, g, b, a);
	}

private native static void setFloat0(int index, int fieldNum, float r, float g, float b, float a);

/**
 * This method was created by a SmartGuide.
 * @param r float
 * @param g float
 * @param b float
 * @param a float
 */
public void setFOV(float v) 
	{
	setFloat0(getEntityIndex(), FLOAT_CLIENT_PS_FOV, v, 0, 0, 0);
	}
public void setGunAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_GUNANGLES, v.x, v.y, v.z);
	}
public void setGunFrame(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_GUNFRAME, val);
	}
public void setGunIndex(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_GUNINDEX, val);
	}
public void setGunOffset(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_GUNOFFSET, v.x, v.y, v.z);
	}
public void setKickAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_KICKANGLES, v.x, v.y, v.z);
	}
public void setRDFlags(int val)
	{
	setInt(getEntityIndex(), INT_CLIENT_PS_RDFLAGS, val);
	}
/**
 * This method was created by a SmartGuide.
 * @param fieldindex int
 * @param value int
 */
public void setStat(int fieldindex, short value) 
	{
	setStat0(getEntityIndex(), fieldindex, value);
	}

/**
 * This method was created by a SmartGuide.
 * @param index int
 * @param fieldIndex int
 * @param value int
 */
private native static void setStat0(int index, int fieldIndex, short value);

public void setViewAngles(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_VIEWANGLES, v.x, v.y, v.z);
	}
public void setViewOffset(Vec3 v)
	{
	setVec3(getEntityIndex(), VEC3_CLIENT_PS_VIEWOFFSET, v.x, v.y, v.z);
	}
/**
 * This method was created by a SmartGuide.
 * @param cmd q2java.UserCmd
 */
public abstract void think();
/**
 * This method was created by a SmartGuide.
 * @param userinfo java.lang.String
 */
public abstract void userinfoChanged(String userinfo);
}