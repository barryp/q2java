
package baseq2;

import java.util.*;
import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Various minor static utilities useful to the game.
 *
 * @author Barry Pederson
 */
public class MiscUtil 
	{
	// handy random number generator
	private static Random gRandom = new Random();	
	
/**
 * Adjust the specified mins and maxs vectors so that they contain the specified point.
 * @param point Point being added
 * @param mins Min coordinates of the bounding box
 * @param maxs Max coordinates of the bounding box
 */
public static void addPointToBounds(Tuple3f point, Tuple3f mins, Tuple3f maxs) 
	{
	mins.x = Math.min(mins.x, point.x);
	mins.y = Math.min(mins.y, point.y);
	mins.z = Math.min(mins.z, point.z);
	
	maxs.x = Math.max(maxs.x, point.x);
	maxs.y = Math.max(maxs.y, point.y);
	maxs.z = Math.max(maxs.z, point.z);
	}
/**
 * Get the direction the door or button should move.
 * When the entity is spawned, the "angles"
 * indicates the direction the door should move,
 * but the format is a little bizarre, and the
 * value doesn't actually indicate the angles
 * for the entity, so we -have- to clear it,
 * once we get a copy, otherwise the doors
 * will appear on the maps in all sorts of odd positions.
 * @return a Vec3 pointing in the direction the door opens.
 */
public static Vector3f calcMoveDir(Angle3f angles) 
	{
	// door goes up	
	if (equals(angles, 0, -1, 0))
		return new Vector3f(0, 0, 1);

	// door goes down
	if (equals(angles, 0, -2, 0))
		return new Vector3f(0, 0, -1);

	// some other direction?	
	Vector3f result = new Vector3f();	
	angles.getVectors(result, null, null);
	return result;
	}

/**
 * Clamp the Tuple3f to 1/8 units.  This way positions will
 * be accurate for client side prediction.
 */
public static void clampEight(Tuple3f t) 
	{
	t.x = Math.round(t.x * 8) * 0.125F;
	t.y = Math.round(t.y * 8) * 0.125F;
	t.z = Math.round(t.z * 8) * 0.125F;
	}
/**
 * @return A random number between -1.0 and +1.0
 */
public static float cRandom() 
	{
	return (float)((gRandom.nextFloat() - 0.5) * 2.0);
	}
/**
 * Check whether a Tuple3f has a particular set of x,y,z values.
 * @return True if equal, false if not
 */
public static boolean equals(Tuple3f t, float x, float y, float z) 
	{
	return (t.x == x) && (t.y == y) && (t.z == z);
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
public static void fireLead(GameObject p, Point3f start, Vector3f aimDir, int damage, int kick, int teImpact, int hSpread, int vSpread, String obitKey) 
	{
	TraceResults	tr;
	Angle3f  dir;
	Vector3f	forward = new Vector3f();
	Vector3f	right = new Vector3f();
	Vector3f	up = new Vector3f();
	Point3f	end = new Point3f();
	float	r;
	float	u;
	Point3f		waterStart = null;
	boolean	water = false;
	int		content_mask = Engine.MASK_SHOT | Engine.MASK_WATER;

	tr = Engine.trace(p.fEntity.getOrigin(), start, p.fEntity, Engine.MASK_SHOT);
	if (!(tr.fFraction < 1.0))
		{
		dir = new Angle3f(aimDir);
		dir.getVectors(forward, right, up);

		r = (float) (MiscUtil.cRandom() * hSpread);
		u = (float) (MiscUtil.cRandom() * vSpread);
		end.scaleAdd(8192, forward, start);
		end.scaleAdd(r, right, end);
		end.scaleAdd(u, up, end);

		if ((Engine.getPointContents(start) & Engine.MASK_WATER) != 0)
			{
			water = true;
			waterStart = new Point3f(start);
			content_mask &= ~Engine.MASK_WATER;
			}

		tr = Engine.trace(start, end, p.fEntity, content_mask);

		// see if we hit water
		if ((tr.fContents & Engine.MASK_WATER) != 0)
			{
			int		color;

			water = true;
			waterStart = new Point3f(tr.fEndPos);

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
				Vector3f diff = new Vector3f();
				diff.sub(end, start);
				Angle3f ang = new Angle3f(diff);
				ang.getVectors(forward, right, up);
				r = (float)(MiscUtil.cRandom() * hSpread * 2);
				u = (float)(MiscUtil.cRandom() * vSpread * 2);
				end.scaleAdd(8192, forward, waterStart);
				end.scaleAdd(r, right, end);
				end.scaleAdd(u, up, end);
				}

			// re-trace ignoring water this time
			tr = Engine.trace(waterStart, end, p.fEntity, Engine.MASK_SHOT);
			}
		}

	// send gun puff / flash
	if ((tr.fSurfaceName == null) || ((tr.fSurfaceFlags & Engine.SURF_SKY) == 0))
		{
		if ((tr.fFraction < 1.0) && (!tr.fSurfaceName.startsWith("sky")))
			{
			if (tr.fEntity.getReference() instanceof GameObject)
				((GameObject)tr.fEntity.getReference()).damage(p, p, aimDir, tr.fEndPos, tr.fPlaneNormal, damage, kick, GameObject.DAMAGE_BULLET, teImpact, obitKey); 
			}
		}

	// if went through water, determine where the end and make a bubble trail
	if (water)
		{
		Point3f pos = new Point3f();

		Vector3f leadDir = new Vector3f(tr.fEndPos);
		leadDir.sub(waterStart);
		leadDir.normalize();
		pos.scaleAdd(-2, leadDir, tr.fEndPos); // = tr.fEndPos.vectorMA(-2, dir);
		if ((Engine.getPointContents(pos) & Engine.MASK_WATER) != 0)
			tr.fEndPos = new Point3f(pos);
		else
			tr = Engine.trace(pos, waterStart, tr.fEntity, Engine.MASK_WATER);

		pos = new Point3f(waterStart);
		pos.add(tr.fEndPos);
		pos.scale(0.5F);

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
public static void fireRail(GameObject p, Point3f start, Vector3f aimDir, int damage, int kick) 
	{
	TraceResults	tr = null;
	NativeEntity 	ignore;
	int			mask;
	boolean		water;
	Point3f		from = new Point3f(start);
	Point3f		end = new Point3f();

	end.scaleAdd(8192, aimDir, start);
	ignore = p.fEntity;
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

			if ((tr.fEntity.getReference() != p) && (tr.fEntity.getReference() instanceof GameObject))
				((GameObject)tr.fEntity.getReference()).damage(p, p, aimDir, tr.fEndPos, tr.fPlaneNormal, damage, kick, 0, Engine.TE_NONE, "railgun"); 
			}

		from.set(tr.fEndPos);
		}

	// send gun puff / flash
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_RAILTRAIL);
	Engine.writePosition(start);
	Engine.writePosition(tr.fEndPos);
	Engine.multicast(p.fEntity.getOrigin(), Engine.MULTICAST_PHS);
	if (water)
		{
System.out.println("Second water effect");		
		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(Engine.TE_RAILTRAIL);
		Engine.writePosition(start);
		Engine.writePosition(tr.fEndPos);
		Engine.multicast(tr.fEndPos, Engine.MULTICAST_PHS);
		}
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
public static void fireShotgun(GameObject p, Point3f start, Vector3f aimDir, int damage, int kick, int hSpread, int vSpread, int count, String obitKey) 
	{
	for (int i = 0; i < count; i++)
		fireLead(p, start, aimDir, damage, kick, Engine.TE_SHOTGUN, hSpread, vSpread, obitKey);
	}
/**
 * Select the spawnpoint farthest from other players.
 * @return q2jgame.GameEntity
 */
public static GenericSpawnpoint getSpawnpointFarthest() 
	{
	GenericSpawnpoint result = null;
	float bestDistance = 0;
	
	Vector list = Game.getLevelRegistryList(baseq2.spawn.info_player_deathmatch.REGISTRY_KEY);
	Enumeration enum = list.elements();
	while (enum.hasMoreElements())
		{
		GenericSpawnpoint spawnPoint = (GenericSpawnpoint) enum.nextElement();
		float range = MiscUtil.nearestPlayerDistance(spawnPoint);

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
public static GenericSpawnpoint getSpawnpointRandom() 
	{
	GenericSpawnpoint spawnPoint = null;
	GenericSpawnpoint spot1 = null;
	GenericSpawnpoint spot2 = null;
	float range1 = Float.MAX_VALUE;
	float range2 = Float.MAX_VALUE;
	int count = 0;
	
	// find the two deathmatch spawnpoints that are closest to any players
	Vector list = Game.getLevelRegistryList(baseq2.spawn.info_player_deathmatch.REGISTRY_KEY);
	Enumeration enum = list.elements();
	while (enum.hasMoreElements())
		{
		count++;
		spawnPoint = (GenericSpawnpoint) enum.nextElement();
		float range = MiscUtil.nearestPlayerDistance(spawnPoint);

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

	int selection = (MiscUtil.randomInt() & 0x0fff) % count;
	spawnPoint = null;

	enum = list.elements();
	while (enum.hasMoreElements())
		{
		spawnPoint = (GenericSpawnpoint) enum.nextElement();
		
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
public static GenericSpawnpoint getSpawnpointSingle() 
	{
	String target = GameModule.getSpawnpoint();

	if (target == null)
		{
		// look for an info_player_start spawnpoint that's not a target
		Vector list = Game.getLevelRegistryList(baseq2.spawn.info_player_start.REGISTRY_KEY);
		Enumeration enum = list.elements();
		while (enum.hasMoreElements())
			{
			GenericSpawnpoint sp = (GenericSpawnpoint) enum.nextElement();		
			if (sp.getTargetGroup() == null)
				return sp;
			}
			
		// all info_player_starts are targets, so settle for any one of them
		return (GenericSpawnpoint) list.elementAt(0);					
		}
	else
		{
		// look for an info_player_start object within the specified target group
		Vector list = Game.getLevelRegistryList("target-" + target);
		Enumeration enum = list.elements();
		while (enum.hasMoreElements())
			{
			Object obj = enum.nextElement();
			if (obj instanceof baseq2.spawn.info_player_start)
				return (GenericSpawnpoint) obj;
			}		
		}
		
	return null;
	}
/**
 * Calculate how far the nearest player away is from a given entity
 * @return float
 * @param ent q2jgame.GameEntity
 */
public static float nearestPlayerDistance(GenericSpawnpoint ent) 
	{
	float result = Float.MAX_VALUE;
	Point3f startPoint = ent.getOrigin();

	Enumeration players = NativeEntity.enumeratePlayers();
	while (players.hasMoreElements())
		{
		NativeEntity p = (NativeEntity)players.nextElement();
		if (((Player)p.getReference()).getHealth() < 0)
			continue;
			
		float f = startPoint.distanceSquared(p.getOrigin());
		if (f < result)
			result = f;
		}

	return (float) Math.sqrt(result);
	}
/**
 * This method was created by a SmartGuide.
 * @return javax.vecmath.Tuple3f
 * @param s java.lang.String
 */
public static Angle3f parseAngle3f(String s) 
	{
	StringTokenizer st = new StringTokenizer(s, "(, )");
	if (st.countTokens() != 3)
		throw new NumberFormatException("Not a valid format for Angle3f");

	float x = Float.valueOf(st.nextToken()).floatValue();
	float y = Float.valueOf(st.nextToken()).floatValue();
	float z = Float.valueOf(st.nextToken()).floatValue();
	
	return new Angle3f(x, y, z);
	}
/**
 * This method was created by a SmartGuide.
 * @return javax.vecmath.Tuple3f
 * @param s java.lang.String
 */
public static Point3f parsePoint3f(String s) 
	{
	StringTokenizer st = new StringTokenizer(s, "(, )");
	if (st.countTokens() != 3)
		throw new NumberFormatException("Not a valid format for Point3f");

	float x = Float.valueOf(st.nextToken()).floatValue();
	float y = Float.valueOf(st.nextToken()).floatValue();
	float z = Float.valueOf(st.nextToken()).floatValue();
	
	return new Point3f(x, y, z);
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
public static void radiusDamage(GameObject inflictor, GameObject attacker, float damage, GameObject ignore, float radius, String obitKey) 
	{
	Point3f inflictorOrigin = inflictor.fEntity.getOrigin();
	float radiusSquared = radius * radius;  // square the radius for faster checking

	Enumeration enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{		
		Player p  = (Player) ((NativeEntity) enum.nextElement()).getReference();

		if (p == ignore)
			continue;

		Point3f victimOrigin = p.fEntity.getOrigin();
		
		if (inflictorOrigin.distanceSquared(victimOrigin) > radiusSquared)
			continue;
						
		// I don't claim to understand these next 4 lines....
		Vector3f v = new Vector3f(p.fEntity.getMins());
		v.add(p.fEntity.getMaxs());
		v.scaleAdd(0.5f, v, victimOrigin);
		v.sub(inflictorOrigin, v);
		
		int damagePoints = (int)(damage - 0.5 * v.length());
		if (p == attacker)
			damagePoints = damagePoints / 2;
			
		if (damagePoints > 0)			
			{
			Vector3f d = new Vector3f();
			d.sub(victimOrigin, inflictorOrigin);
			p.damage(inflictor, attacker, d, inflictorOrigin, new Vector3f(0,0,0), damagePoints, damagePoints, GameObject.DAMAGE_RADIUS, Engine.TE_NONE, obitKey);			
			}
		}	
	}		
/**
 * Return A random float between 0.0 and 1.0.
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
}