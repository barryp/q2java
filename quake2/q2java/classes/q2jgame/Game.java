
package q2jgame;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import q2java.*;

/**
 * This class implements a Quake II Java game. All the
 * fields are static, so the GameEntities, can refer
 * to them without having to keep a reference to the solitary
 * Game object that's instantiated.
 *
 * @author Barry Pederson 
 */

public class Game implements NativeGame
	{
	// game clocks
	private static int gFrameCount;
	public static float gGameTime;
	
	// handy reference to the world
	public static GameEntity gWorld;
		
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
	
	// used while spawnEntities() is running
	// to help gather together groups and targets
	private static Hashtable gGroups;
	private static Hashtable gTargets;

	// handy random number generator
	private static Random gRandom;

	// track level changes	
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
	
/**
 * @return A random number between -1.0 and +1.0
 */
public static double cRandom() 
	{
	return (gRandom.nextFloat() - 0.5) * 2.0;
	}
/**
 * Fire a lead projectile.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param aimDir q2java.Vec3
 * @param damage int
 * @param kick int
 * @param teImpact int
 * @param hSpread int
 * @param vSpread int
 */
public static void fireLead(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick, int teImpact, int hSpread, int vSpread) 
	{
	TraceResults	tr;
	Vec3		dir;
	Vec3		forward = new Vec3();
	Vec3		right = new Vec3();
	Vec3		up = new Vec3();
	Vec3		end;
	float	r;
	float	u;
	Vec3		waterStart = new Vec3();
	boolean	water = false;
	int		content_mask = Engine.MASK_SHOT | Engine.MASK_WATER;

	tr = Engine.trace(p.getOrigin(), start, p, Engine.MASK_SHOT);
	if (!(tr.fFraction < 1.0))
		{
		dir = aimDir.toAngles();
		dir.angleVectors(forward, right, up);

		r = (float) (Game.cRandom() * hSpread);
		u = (float) (Game.cRandom() * vSpread);
		end = start.vectorMA(8192, forward);
		end = end.vectorMA(r, right);
		end = end.vectorMA(u, up);

		if ((Engine.pointContents(start) & Engine.MASK_WATER) != 0)
			{
			water = true;
			waterStart = new Vec3(start);
			content_mask &= ~Engine.MASK_WATER;
			}

		tr = Engine.trace(start, end, p, content_mask);

		// see if we hit water
		if ((tr.fContents & Engine.MASK_WATER) != 0)
			{
			int		color;

			water = true;
			waterStart = new Vec3(tr.fEndPos);

			if (!start.equals(tr.fEndPos))
				{
				if ((tr.fContents & Engine.CONTENTS_WATER) != 0)
					{
					if (tr.fSurfaceName.equals("*brwater"))
						color = Engine.SPLASH_BROWN_WATER;
					else
						color = Engine.SPLASH_BLUE_WATER;
					}
				else if ((tr.fContents & Engine.CONTENTS_SLIME) != 0)
					color = Engine.SPLASH_SLIME;
				else if ((tr.fContents & Engine.CONTENTS_LAVA) != 0)
					color = Engine.SPLASH_LAVA;
				else
					color = Engine.SPLASH_UNKNOWN;

				if (color != Engine.SPLASH_UNKNOWN)
					{
					Engine.writeByte(Engine.SVC_TEMP_ENTITY);
					Engine.writeByte(Engine.TE_SPLASH);
					Engine.writeByte(8);
					Engine.writePosition(tr.fEndPos);
					Engine.writeDir(tr.fPlaneNormal);
					Engine.writeByte(color);
					Engine.multicast(tr.fEndPos, Engine.MULTICAST_PVS);
					}

				// change bullet's course when it enters water
				dir = new Vec3(end).subtract(start).toAngles();
				dir.angleVectors(forward, right, up);
				r = (float)(Game.cRandom() * hSpread * 2);
				u = (float)(Game.cRandom() * vSpread * 2);
				end = waterStart.vectorMA(8192, forward);
				end = end.vectorMA(r, right);
				end = end.vectorMA(u, up);
				}

			// re-trace ignoring water this time
			tr = Engine.trace(waterStart, end, p, Engine.MASK_SHOT);
			}
		}

	// send gun puff / flash
	if ((tr.fSurfaceName == null) || ((tr.fSurfaceFlags & Engine.SURF_SKY) == 0))
		{
		if ((tr.fFraction < 1.0) && (!tr.fSurfaceName.startsWith("sky")))
			((GameEntity)tr.fEntity).damage(p, p, aimDir, tr.fEndPos, tr.fPlaneNormal, damage, kick, GameEntity.DAMAGE_BULLET, teImpact); 
		}

	// if went through water, determine where the end and make a bubble trail
	if (water)
		{
		Vec3 pos;

		dir = new Vec3(tr.fEndPos).subtract(waterStart).normalize();
		pos = tr.fEndPos.vectorMA(-2, dir);
		if ((Engine.pointContents(pos) & Engine.MASK_WATER) != 0)
			tr.fEndPos = new Vec3(pos);
		else
			tr = Engine.trace(pos, waterStart, tr.fEntity, Engine.MASK_WATER);

		pos = new Vec3(waterStart).add(tr.fEndPos).scale(0.5F);

		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_BUBBLETRAIL);
		Engine.writePosition(waterStart);
		Engine.writePosition(tr.fEndPos);
		Engine.multicast(pos, Engine.MULTICAST_PVS);
		}
	}
/**
 * Fire a railgun slug.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param forward q2java.Vec3
 * @param damage int
 * @param kick int
 */
public static void fireRail(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick) 
	{
	Vec3			from;
	Vec3			end;
	TraceResults	tr = null;
	GameEntity 	ignore;
	int			mask;
	boolean		water;

	end = start.vectorMA(8192, aimDir);
	from = new Vec3(start);
	ignore = p;
	water = false;
	mask = Engine.MASK_SHOT | Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA;
	while (ignore != null)
		{
		tr = Engine.trace(from, end, ignore, mask);

		if ((tr.fContents & (Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA)) != 0)
			{
			mask &= ~(Engine.CONTENTS_SLIME | Engine.CONTENTS_LAVA);
			water = true;
			}
		else
			{
/*			if ((tr.ent->svflags & SVF_MONSTER) || (tr.ent->client))
				ignore = tr.ent;
			else
*/				ignore = null;

			if (tr.fEntity != p)
				((GameEntity)tr.fEntity).damage(p, p, aimDir, tr.fEndPos, tr.fPlaneNormal, damage, kick, 0, Engine.TE_NONE); 
			}

		from.set(tr.fEndPos);
		}

	// send gun puff / flash
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_RAILTRAIL);
	Engine.writePosition(start);
	Engine.writePosition(tr.fEndPos);
	Engine.multicast(tr.fEndPos, Engine.MULTICAST_PHS);
//	Engine.multicast(start, Engine.MULTICAST_PHS);
	if (water)
		{
		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_RAILTRAIL);
		Engine.writePosition(start);
		Engine.writePosition(tr.fEndPos);
		Engine.multicast(tr.fEndPos, Engine.MULTICAST_PHS);
		}

//	if (self->client)
//		PlayerNoise(self, tr.endpos, PNOISE_IMPACT);
	}
/**
 * Fire a shotgun shell.
 * @param p q2jgame.Player
 * @param start q2java.Vec3
 * @param aimDir q2java.Vec3
 * @param damage int
 * @param kick int
 * @param teImpact int
 * @param hSpread int
 * @param vSpread int
 */
public static void fireShotgun(GameEntity p, Vec3 start, Vec3 aimDir, int damage, int kick, int hSpread, int vSpread, int count) 
	{
	for (int i = 0; i < count; i++)
		fireLead(p, start, aimDir, damage, kick, Engine.TE_SHOTGUN, hSpread, vSpread);
	}
/**
 * Lookup a group Vector, based on the name.
 * Only good while Game.spawnEntities() is running, 
 * once it's finished, this function will return null.
 * Also will return null if groupName is null.
 * @return Vector containing GameEntities belonging to the group.
 * @param groupName Name of the group.
 */
public static Vector getGroup(String groupName) 
	{
	if ((gGroups == null) || (groupName == null))
		return null;
		
	Vector v = (Vector)gGroups.get(groupName);
	if (v == null)
		{
		v = new Vector();
		gGroups.put(groupName, v);
		}
	return v;			
	}
/**
 * Select the spawnpoint farthest from other players.
 * @return q2jgame.GameEntity
 */
public static GameEntity getSpawnpointFarthest() 
	{
	GameEntity result = null;
	float bestDistance = 0;
	Enumeration enum;
	
	enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_deathmatch");
	while (enum.hasMoreElements())
		{
		GameEntity spawnPoint = (GameEntity) enum.nextElement();
		float range = nearestPlayerDistance(spawnPoint);

		if (range > bestDistance)
			{
			bestDistance = range;
			result = spawnPoint;
			}
		}

	return result;
	}
/**
 * Select a random spawnpoint, but exclude the two points closest
 * to other players.
 * @return q2jgame.GameEntity
 */
public static GameEntity getSpawnpointRandom() 
	{
	GameEntity spawnPoint = null;
	GameEntity spot1 = null;
	GameEntity spot2 = null;
	float range1 = Float.MAX_VALUE;
	float range2 = Float.MAX_VALUE;
	int count = 0;
	Enumeration enum;
	
	// find the two deathmatch spawnpoints that are closest to any players
	enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_deathmatch");
	while (enum.hasMoreElements())
		{
		count++;
		spawnPoint = (GameEntity) enum.nextElement();
		float range = nearestPlayerDistance(spawnPoint);

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

	int selection = (randomInt() & 0x0fff) % count;
	spawnPoint = null;

	enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_deathmatch");
	while (enum.hasMoreElements())
		{
		spawnPoint = (GameEntity) enum.nextElement();
		
		// skip the undesirable spots
		if ((spawnPoint == spot1) || (spawnPoint == spot2))
			continue;
			
		if ((selection--) == 0)					
			break;
		}

	return spawnPoint;
	}
/**
 * Find a single-player spawnpoint. Kind of simplistic.
 * @return q2jgame.GameEntity
 */
public static GameEntity getSpawnpointSingle() 
	{
	Enumeration enum;
	
	enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_start");
	if (enum.hasMoreElements())
		return (GameEntity) enum.nextElement();
		
	return null;
	}
/**
 * Lookup a target Vector, based on the name.
 * Only good while Game.spawnEntities() is running, 
 * once it's finished, this function will return null.
 * Also will return null if targetName is null.
 * @return Vector containing GameEntities belonging to the target set.
 * @param targetName Name of the target.
 */
public static Vector getTarget(String targetName) 
	{
	if ((gTargets == null) || (targetName == null))
		return null;
		
	Vector v = (Vector)gTargets.get(targetName);
	if (v == null)
		{
		v = new Vector();
		gTargets.put(targetName, v);
		}
	return v;			
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
 * Called by the DLL when Quake II calls the DLL's init() function.
 */
public void init()
	{	
	// actually initialize the game
	Engine.debugLog("Game.init()");
	gRandom = new Random();
	
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

	Engine.debugLog("Game.init() finished");
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
 * Calculate how far the nearest player away is from a given entity
 * @return float
 * @param ent q2jgame.GameEntity
 */
public static float nearestPlayerDistance(GameEntity ent) 
	{
	float result = Float.MAX_VALUE;
	Vec3 startPoint = ent.getOrigin();
	Vec3 v = new Vec3();

	Enumeration players = NativeEntity.enumeratePlayers();
	while (players.hasMoreElements())
		{
		Player p = (Player) players.nextElement();
		if (p.getHealth() < 0)
			continue;
		v.set(startPoint).subtract(p.getOrigin());						
		float f = v.length();
		if (f < result)
			result = f;
		}

	return result;
	}
/**
 * Inflict damage on all Players within a certain radius of the inflictor.
 * This is different from the stock DLL which inflicts damage on all entities, not just players.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param damage float
 * @param ignore q2jgame.GameEntity
 * @param radius float
 */
public static void radiusDamage(GameEntity inflictor, GameEntity attacker, float damage, GameEntity ignore, float radius) 
	{
	Enumeration enum = NativeEntity.enumeratePlayers();
	Vec3 point = inflictor.getOrigin();
	Vec3 v = new Vec3();
	Vec3 dir;
	Vec3 normal = new Vec3();
	while (enum.hasMoreElements())
		{		
		Player p = (Player) enum.nextElement();

		if (p == ignore)
			continue;
			
		v.set(point);
		v.subtract(p.getOrigin());
		if (v.length() > radius)
			continue;

		// I don't claim to understand these next 4 lines....
		v = p.getMins().add(p.getMaxs());
		dir = p.getOrigin();
		v = dir.vectorMA(0.5F, v).subtract(point);							
		int damagePoints = (int)(damage - 0.5 * v.length());

		if (p == attacker)
			damagePoints = damagePoints / 2;
			
		if (damagePoints > 0)			
			{
			dir.subtract(point);
			p.damage(inflictor, attacker, dir, point, normal, damagePoints, damagePoints, GameEntity.DAMAGE_RADIUS, Engine.TE_NONE);			
			}
		}	
	}		
/**
 * @return A random number between 0.0 and 1.0
 */
public static float randomFloat() 
	{
	return gRandom.nextFloat();
	}
/**
 * Get a random integer, values are distributed across 
 * the full range of the signed 32-bit integer type.
 * @return A random integer.
 */
public static int randomInt() 
	{
	return gRandom.nextInt();
	}
/**
 * Called by the DLL when the DLL's ReadGame() function is called.
 */
public void readGame(String filename)
	{
	Engine.debugLog("Game.readGame(\"" + filename + "\")");
	}
/**
 * Called by the DLL when the DLL's ReadLevel() function is called.
 */
public void readLevel(String filename)
	{
	Engine.debugLog("Game.readLevel(\"" + filename + "\")");
	}
/**
 * Called by the DLL when the DLL's RunFrame() function is called.
 */
public void runFrame()
	{
	Enumeration enum;
	Object obj;
	
	// increment the clocks
	// if we just added Engine.SECONDS_PER_FRAME to fGameTime,
	// the clock drifts due to rounding errors, so we
	// have to keep a frame count and multiply.
	gFrameCount++;
	gGameTime = gFrameCount * Engine.SECONDS_PER_FRAME;
	
	if (gInIntermission && (gGameTime > gIntermissionEndTime))
		{
		if (isDMFlagSet(DF_SAME_LEVEL))
			Engine.addCommandString("gamemap \"" + gCurrentMap + "\"");
		else
			Engine.addCommandString("gamemap \"" + gNextMap + "\"");
		return;
		}

	// notify all the players we're beginning a frame
	enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		try
			{
			((Player) enum.nextElement()).beginServerFrame();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}						
				
	// give each non-player entity a chance to run				
	enum = NativeEntity.enumerateEntities();
	while (enum.hasMoreElements())
		{
		obj = enum.nextElement();
		if (!(obj instanceof Player))
			{
			try
				{
				((GameEntity) obj).runFrame();
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}				
			}				
		}

	if (!gInIntermission && timeToQuit())
		startIntermission();

	// notify all the players we're ending a frame
	enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		try
			{
			((Player) enum.nextElement()).endServerFrame();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}			
		}			
	}
/**
 * Called by the DLL when the DLL's ServerCommand() function is called.
 */
public void serverCommand() {
	return;
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
 * Called by the DLL when the DLL's Shutdown() function is called.
 */
public void shutdown()
	{
	Engine.debugLog("Game.shutdown()");
	}
/**
 * Spawn entities into the Quake II environment.
 * This methods parses the entString passed to it, and looks
 * for Java classnames equivalent to the classnames specified 
 * in the entString, and instantiates instances of them, with
 * the entity parameters passed as an array of Strings.
 */
private void spawnEntities(String entString)
	{
	try
		{
		StringReader sr = new StringReader(entString);
		StreamTokenizer st = new StreamTokenizer(sr);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		int token;
		Vector v = new Vector(16);
		boolean foundClassname = false;
		String className = null;

		Object[] params = new Object[1];
		Class[] paramTypes = new Class[1];
		String[] sa = new String[1];
		paramTypes[0] = sa.getClass();

		while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
			{
			switch (token)
				{
				case '"' : 
					if (foundClassname)
						{
						className = st.sval;
						foundClassname = false;
						break;
						}
					if (st.sval.equalsIgnoreCase("classname"))
						{
						foundClassname = true;
						break;
						}
					v.addElement(st.sval.intern()); 
					break;

				case '{' : foundClassname = false; break;

				case '}' : 
					sa = new String[v.size()];
					v.copyInto(sa);
					v.removeAllElements();
					params[0] = sa;
					try
						{
						Class entClass = Class.forName("q2jgame.spawn." + className.toLowerCase());
						Constructor ctor = entClass.getConstructor(paramTypes);							
						GameEntity ent = (GameEntity) ctor.newInstance(params);
						}
					// this stinks..since we're using reflection to find the 
					// constructor, the compiler can't tell what exceptions
					// it'll throw - so we have to catch 'em all
					// and sort out the ones we really want to deal with.
					catch (InvocationTargetException ite)
						{
						Throwable te = ite.getTargetException();
						if (!(te instanceof InhibitedException))
							te.printStackTrace();						
						}
					catch (ClassNotFoundException cnfe)
						{
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}

					foundClassname = false;
					className = null;
					break;

				default  : 
					foundClassname = false;				
				}
			}
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Pick an intermission spot, and notify each player.
 */
public void startIntermission() 
	{
	Enumeration enum;
	Vector v = new Vector();
	
	// gather list of info_player_intermission entities
	enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_intermission");
	while (enum.hasMoreElements())
		v.addElement(enum.nextElement());

	// if there weren't any intermission spots, try for info_player_start spots
	if (v.size() < 1)
		{
		enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_start");
		while (enum.hasMoreElements())
			v.addElement(enum.nextElement());		
		}

	// still no spots found? try for info_player_deathmatch
	if (v.size() < 1)
		{
		enum = NativeEntity.enumerateEntities("q2jgame.spawn.info_player_deathmatch");
		while (enum.hasMoreElements())
			v.addElement(enum.nextElement());		
		}

	// randomly pick something from the list
	int i = (randomInt() & 0x0fff) % v.size();
	GameEntity spot = (GameEntity) v.elementAt(i);
	
	// notify each player
	enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		p.startIntermission(spot);
		}
		
	gInIntermission = true;	
	gIntermissionEndTime = gGameTime + 5.0;	
	}
/**
 * Spawn entities into the Quake II environment.
 * This methods parses the entString passed to it, and looks
 * for Java classnames equivalent to the classnames specified 
 * in the entString, and instantiates instances of them, with
 * the entity parameters passed as an array of Strings.
 */
public void startLevel(String mapname, String entString, String spawnPoint)
	{
	Engine.debugLog("Game.spawnEntities(\"" + mapname + ", <entString>, \"" + spawnPoint + "\")");

	gFrameCount = 0;
	gGameTime = 0;
	gInIntermission = false;
	gCurrentMap = mapname;
	gNextMap = mapname; // in case there isn't a target_changelevel entity in the entString

	gGroups = new Hashtable();
	gTargets = new Hashtable();

	// force a gc, to clean things up from the last level, before
	// spawning all the new entities
	System.gc();		

	spawnEntities(entString);

	// no more need for hashtables, free them so they can be gc'ed
	gGroups = null;
	gTargets = null;
		
		
	//
	// cache some sounds
	//
		
	Engine.soundIndex("player/fry.wav");	// standing in lava / slime
	Engine.soundIndex("player/lava1.wav");
	Engine.soundIndex("player/lava2.wav");

	Engine.soundIndex("misc/pc_up.wav");
	Engine.soundIndex("misc/talk1.wav");

	Engine.soundIndex("misc/udeath.wav");

	// gibs
	Engine.soundIndex("items/respawn1.wav");

	// sexed sounds
	Engine.soundIndex("*death1.wav");
	Engine.soundIndex("*death2.wav");
	Engine.soundIndex("*death3.wav");
	Engine.soundIndex("*death4.wav");
	Engine.soundIndex("*fall1.wav");
	Engine.soundIndex("*fall2.wav");	
	Engine.soundIndex("*gurp1.wav");		// drowning damage
	Engine.soundIndex("*gurp2.wav");	
	Engine.soundIndex("*jump1.wav");		// player jump
	Engine.soundIndex("*pain25_1.wav");
	Engine.soundIndex("*pain25_2.wav");
	Engine.soundIndex("*pain50_1.wav");
	Engine.soundIndex("*pain50_2.wav");
	Engine.soundIndex("*pain75_1.wav");
	Engine.soundIndex("*pain75_2.wav");
	Engine.soundIndex("*pain100_1.wav");
	Engine.soundIndex("*pain100_2.wav");

	//-------------------

	Engine.soundIndex("player/gasp1.wav");		// gasping for air
	Engine.soundIndex("player/gasp2.wav");		// head breaking surface, not gasping

	Engine.soundIndex("player/watr_in.wav");	// feet hitting water
	Engine.soundIndex("player/watr_out.wav");	// feet leaving water

	Engine.soundIndex("player/watr_un.wav");	// head going underwater
	
	Engine.soundIndex("player/u_breath1.wav");
	Engine.soundIndex("player/u_breath2.wav");

	Engine.soundIndex("items/pkup.wav");		// bonus item pickup
	Engine.soundIndex("world/land.wav");		// landing thud
	Engine.soundIndex("misc/h2ohit1.wav");		// landing splash

	Engine.soundIndex("items/damage.wav");
	Engine.soundIndex("items/protect.wav");
	Engine.soundIndex("items/protect4.wav");
	Engine.soundIndex("weapons/noammo.wav");

	Engine.soundIndex("infantry/inflies1.wav");
	
	// setup player status bar
	Engine.configString (Engine.CS_STATUSBAR, Player.DM_STATUSBAR);	
	
	//
	// Setup light animation tables. 'a' is total darkness, 'z' is doublebright.
	//

	// 0 normal
	Engine.configString(Engine.CS_LIGHTS+0, "m");
	
	// 1 FLICKER (first variety)
	Engine.configString(Engine.CS_LIGHTS+1, "mmnmmommommnonmmonqnmmo");
	
	// 2 SLOW STRONG PULSE
	Engine.configString(Engine.CS_LIGHTS+2, "abcdefghijklmnopqrstuvwxyzyxwvutsrqponmlkjihgfedcba");
	
	// 3 CANDLE (first variety)
	Engine.configString(Engine.CS_LIGHTS+3, "mmmmmaaaaammmmmaaaaaabcdefgabcdefg");
	
	// 4 FAST STROBE
	Engine.configString(Engine.CS_LIGHTS+4, "mamamamamama");
	
	// 5 GENTLE PULSE 1
	Engine.configString(Engine.CS_LIGHTS+5,"jklmnopqrstuvwxyzyxwvutsrqponmlkj");
	
	// 6 FLICKER (second variety)
	Engine.configString(Engine.CS_LIGHTS+6, "nmonqnmomnmomomno");
	
	// 7 CANDLE (second variety)
	Engine.configString(Engine.CS_LIGHTS+7, "mmmaaaabcdefgmmmmaaaammmaamm");
	
	// 8 CANDLE (third variety)
	Engine.configString(Engine.CS_LIGHTS+8, "mmmaaammmaaammmabcdefaaaammmmabcdefmmmaaaa");
	
	// 9 SLOW STROBE (fourth variety)
	Engine.configString(Engine.CS_LIGHTS+9, "aaaaaaaazzzzzzzz");
	
	// 10 FLUORESCENT FLICKER
	Engine.configString(Engine.CS_LIGHTS+10, "mmamammmmammamamaaamammma");

	// 11 SLOW PULSE NOT FADE TO BLACK
	Engine.configString(Engine.CS_LIGHTS+11, "abcdefghijklmnopqrrqponmlkjihgfedcba");
	
	// styles 32-62 are assigned by the light program for switchable lights

	// 63 testing
	Engine.configString(Engine.CS_LIGHTS+63, "a");		
		
		
		
	// now, right before the game starts, is a good time to 
	// force Java to do another garbage collection to tidy things up.
	System.gc();		
	}
/**
 * Check the timelimit and fraglimit values and decide
 * whether to end the level or not.
 * @return boolean true if it's time to end the level
 */
private static boolean timeToQuit() 
	{
	float quitTime = gTimeLimit.getFloat() * 60;

	if ((quitTime > 0) && (gGameTime > quitTime))
		{
		Engine.bprint(Engine.PRINT_HIGH, "Timelimit hit.\n");		
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
			Engine.bprint(Engine.PRINT_HIGH, "Timelimit hit.\n");		
			return true;
			}
		}		
		
	return false;		
	}
/**
 * Called by the DLL when the DLL's WriteGame() function is called.
 */
public void writeGame(String filename)
	{
	Engine.debugLog("Game.writeGame(\"" + filename + "\")");		
	}
/**
 * Called by the DLL when the DLL's WriteLevel() function is called.
 */
public void writeLevel(String filename)
	{
	Engine.debugLog("Game.writeLevel(\"" + filename + "\")");
	}
}