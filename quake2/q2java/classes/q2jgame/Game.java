
package q2jgame;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import q2java.*;

public class Game implements NativeGame
	{
	// game clocks
	private static int fFrameCount;
	public static double fGameTime;
	
	private static boolean fInIntermission;

	// handy random number generator
	private static Random fRandom;
	
	// various CVars
	public static CVar fBobUp;	
	public static CVar fRollAngle;
	public static CVar fRollSpeed;
	public static CVar fGravity;
	
	private static CVar fFragLimit;
	private static CVar fTimeLimit;
	
	// reference to the world
	public static GameEntity fWorld;
	
/**
 * This method was created by a SmartGuide.
 * @return double
 */
public static double cRandom() 
	{
	return (fRandom.nextFloat() - 0.5) * 2.0;
	}
/**
 * This method was created by a SmartGuide.
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
 * This method was created by a SmartGuide.
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

		tr.fEndPos.copyTo(from);
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
 * This method was created by a SmartGuide.
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
 * Select a random point, but NOT the two points closest
 * to other players.
 * @return q2jgame.GameEntity
 */
public static GameEntity getFarthestSpawnpoint() 
	{
	GameEntity result = null;
	float bestDistance = 0;
	Enumeration enum;
	
	// find the deathmatch spawnpoint farthest from any players
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
 * Select a random point, but NOT the two points closest
 * to other players.
 * @return q2jgame.GameEntity
 */
public static GameEntity getRandomSpawnpoint() 
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
public void init()
	{	
/*	
	// get setup so the debugLog() method has a place to write
	File gameDir = new File(Engine.getGamePath());
	File sandbox = new File(gameDir, "sandbox");
	fLogFile = new File(sandbox, "game.log");
	fLogFile.delete();
*/		
	// actually initialize the game
	Engine.debugLog("Game.init()");
	fRandom = new Random();
	fFrameCount = 0;
	fInIntermission = false;
	
	// load cvars
	fBobUp = new CVar("bob_up", "0.005", 0);	
	fRollAngle = new CVar("sv_rollangle", "2", 0);
	fRollSpeed = new CVar("sv_rollspeed", "200", 0);	
	fGravity = new CVar("sv_gravity", "800", 0);	
	
	fFragLimit = new CVar("fraglimit", "0", CVar.CVAR_SERVERINFO);
	fTimeLimit = new CVar("timelimit", "0", CVar.CVAR_SERVERINFO);
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
		v.copyFrom(startPoint).subtract(p.getOrigin());						
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
			
		v.copyFrom(point);
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
 * This method was created by a SmartGuide.
 * @return int
 */
public static float randomFloat() 
	{
	return fRandom.nextFloat();
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public static int randomInt() 
	{
	return fRandom.nextInt();
	}
public void readGame(String filename)
	{
	Engine.debugLog("Game.readGame(\"" + filename + "\")");
	}
public void readLevel(String filename)
	{
	Engine.debugLog("Game.readLevel(\"" + filename + "\")");
	}
public void runFrame()
	{
	Enumeration enum;
	Object obj;
	
	// increment the clocks
	fFrameCount++;
	fGameTime = fFrameCount * Engine.SECONDS_PER_FRAME;

	if (fInIntermission)
		return;

	// notify all the players we're beginning a frame
	enum = new PlayerEnumeration();
	while (enum.hasMoreElements())
		((Player) enum.nextElement()).beginFrame();
				
	// give each non-player entity a chance to run				
	enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		{
		obj = enum.nextElement();
		if (!(obj instanceof Player))
			((GameEntity) obj).runFrame();
		}

	if (timeToQuit())
		{
		startIntermission();
		return;
		}

	// notify all the players we're ending a frame
	enum = new PlayerEnumeration();
	while (enum.hasMoreElements())
		((Player) enum.nextElement()).endFrame();
	}
public void shutdown()
	{
	Engine.debugLog("Game.shutdown()");
	}
public void spawnEntities(String mapname, String entString, String spawnPoint)
	{
	Engine.debugLog("Game.spawnEntities(\"" + mapname + ", <entString>, \"" + spawnPoint + "\")");


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

		FileOutputStream fos = new FileOutputStream(Engine.getGamePath() + "\\sandbox\\entity.log");
		PrintStream ps = new PrintStream(fos);		

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
						Class entClass = Class.forName("q2jgame.spawn." + className);
						Constructor ctor = entClass.getConstructor(paramTypes);							
						GameEntity ent = (GameEntity) ctor.newInstance(params);
						ps.println(ent);
						}
					catch (Exception e)
						{
						ps.print("---- " + className + "(");
						if (sa != null)
							{
							String prefix = "";
							for (int i = 0; i < sa.length; i+=2)
								{
								ps.print(prefix + sa[i] + "=\"" + sa[i+1] + "\"");
								prefix = ", ";
								}
							}
						ps.println(")");	
						}

					foundClassname = false;
					className = null;
					break;

				default  : 
					ps.println("Unknown entity-string token: [" + st.sval + "]\n");
					foundClassname = false;				
				}
			}
		ps.close();
		}
	catch (Exception e)
		{
		Engine.dprint(e.getMessage() + "\n");
		Engine.debugLog(e.getMessage());
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
		
	fInIntermission = true;		
	}
/**
 * Check the timelimit and fraglimit values and decide
 * whether to end the level or not.
 * @return boolean true if it's time to end the level
 */
private static boolean timeToQuit() 
	{
	float quitTime = fTimeLimit.getFloat() * 60;

	if ((quitTime > 0) && (fGameTime > quitTime))
		{
		Engine.bprint(Engine.PRINT_HIGH, "Timelimit hit.\n");		
		return true;
		}
		
	int fragLimit = (int) fFragLimit.getFloat();
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
public void writeGame(String filename)
	{
	Engine.debugLog("Game.writeGame(\"" + filename + "\")");		
	}
public void writeLevel(String filename)
	{
	Engine.debugLog("Game.writeLevel(\"" + filename + "\")");
	}
}