package q2java.baseq2;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.event.*;

/**
 * GameObject represents a "thing" in the Quake world, such
 * as a weapon, a box of ammo, a door, and so on.
 *
 * @author Barry Pederson 
 */

public class GameObject implements GameTarget, Serializable
	{
	public transient NativeEntity fEntity;
	protected int fSpawnFlags;		
	protected Vector fGroup;
	protected Vector fTargets;	
	protected Vector fTargetGroup;
	protected Vector3f fGravity = new Vector3f(0, 0, -1); // objects tend to fall down

//	protected String[] fSpawnArgs;
	protected Element fSpawnArgs;
	
	// damage flags
	public final static int DAMAGE_RADIUS		= 0x00000001;	// damage was indirect
	public final static int DAMAGE_NO_ARMOR		= 0x00000002;	// armour does not protect from this damage
	public final static int DAMAGE_ENERGY		= 0x00000004;	// damage is from an energy based weapon
	public final static int DAMAGE_NO_KNOCKBACK	= 0x00000008;	// do not affect velocity, just view angles
	public final static int DAMAGE_BULLET		= 0x00000010; // damage is from a bullet (used for ricochets)
	public final static int DAMAGE_NO_PROTECTION	= 0x00000020; // armor, shields, invulnerability, and godmode have no effect		
	
public GameObject()
	{
	}
public GameObject(Element spawnArgs) throws GameException
	{
	this(spawnArgs, NativeEntity.ENTITY_NORMAL);
	}
public GameObject(Element spawnArgs, int entityType) throws GameException
	{
	fSpawnArgs = spawnArgs;

	// look for common spawn arguments
	fSpawnFlags = GameUtil.getSpawnFlags(spawnArgs);

	// The worldspawn is never inhibited..although
	// on the jail1 map, it's flagged as if it is.
	if (entityType != NativeEntity.ENTITY_WORLD)
		BaseQ2.checkInhibited(fSpawnFlags);
				
	// at this point, looks like the object will be sticking around
	// so create the entity that represents it in the Quake world
	// and set some basic properties.
			
	fEntity = new NativeEntity(entityType);
	fEntity.setReference(this);	

	Point3f p = GameUtil.getPoint3f(spawnArgs, "origin");
	if (p != null)
		fEntity.setOrigin(p);

	Angle3f a = GameUtil.getAngle3f(spawnArgs, "angles");
	if (a != null)
		fEntity.setAngles(a);

	// hook this object up with other game objects

	String s = GameUtil.getSpawnArg(spawnArgs, "target", "id", null);
	if (s != null)
		fTargets = Game.getLevelRegistryList("target-" + s);		

	s = GameUtil.getSpawnArg(spawnArgs, "targetname", "id", null);
	if (s != null)
		fTargetGroup = Game.addLevelRegistry("target-" + s, this);
		
	s = GameUtil.getSpawnArg(spawnArgs, "team", "id", null);
	if (s != null)
		fGroup = Game.addLevelRegistry("team-" + s, this);
	}
/**
 * Alter an object's entity's velocity based on gravity
 */ 
public void applyGravity()
	{
	Vector3f v = fEntity.getVelocity();
	float f = BaseQ2.gGravity * Engine.SECONDS_PER_FRAME;

	// we could have used the Tuple3f scaleAdd() here too.
	v.x += fGravity.x * f;
	v.y += fGravity.y * f;
	v.z += fGravity.z * f;
	
	fEntity.setVelocity(v);
	}
/**
 * Get rid of the object by exploding it.
 *
 * @param tempEntity effect to display, usually Engine.TE_EXPLOSION1 or
 *  Engine.TE_EXPLOSION2
 */
public void becomeExplosion(int tempEntity) 
	{
	if (fEntity == null)
		return;
		
	Point3f origin = fEntity.getOrigin();
	
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(tempEntity);
	Engine.writePosition(origin);
	Engine.multicast(origin, Engine.MULTICAST_PVS);

	dispose();
	}
/**
 * This method points the Player view at it's killer. (?)
 * @param inflictor The thing that killed the player. (?)
 * @param attacker The player/monster/machine that used the <SAMP>inflictor</SAMP> to kill the Player. (?)
 */
protected float calcAttackerYaw(GameObject inflictor, GameObject attacker) 
	{
	Tuple3f	dir;
	
	if ((attacker != null) && (attacker != this))
		{
		dir = attacker.fEntity.getOrigin();
		dir.sub(fEntity.getOrigin());
		}
	else if ((inflictor != null) && (inflictor != this))
		{
		dir = inflictor.fEntity.getOrigin();
		dir.sub(fEntity.getOrigin());
		}
	else
		return fEntity.getAngles().y;

	return (float) ((180.0 / Math.PI) * Math.atan2(dir.y, dir.x));	
	}
/**
 * Called when the object has damage inflicted on it.
 *
 */
public void damage(DamageEvent de) 
	{
	if ((de.getDamageFlags() & DamageEvent.DAMAGE_RADIUS) == 0)
		spawnDamage(de);
	}
/**
 * Wrapper class for backwards compatibility - converts old-style
 * damage call to an event-based one.
 *
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public final void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent, String obitKey) 
	{
	DamageEvent de = DamageEvent.getEvent(inflictor, attacker, this, dir, point, normal, damage,
				       knockback, dflags, tempEvent, obitKey);
	damage(de);

	de.recycle();
	}
/**
 * Clean a few things up before calling NativeEntity.freeEntity().
 */
public void dispose() 
	{
	// disassociate this entity from any groups
	if (fGroup != null)
		{
		fGroup.removeElement(this);
		fGroup = null;
		}
		
	// disassociate this entity from any targetlist
	if (fTargetGroup != null)
		{
		fTargetGroup.removeElement(this);
		fTargetGroup = null;
		}
		
		
	// help with GC a little
	fSpawnArgs = null;
	fTargets = null;
				
	if (fEntity != null)				
		fEntity.freeEntity();

	fEntity = null;
	}
/**
 * Get the gravity vector for this object.
 * @return javax.vecmath.Vector3f
 */
public Vector3f getGravity() 
	{
	return new Vector3f(fGravity);
	}
/**
 * Randomly pick one of this entity's targets.
 * @return one of the entries in the fTargets Vector, 
 *     or null if there are no targets.
 */
public GameObject getRandomTarget() 
	{
	if ((fTargets == null) || (fTargets.size() < 1))
		return null;
		
	int choice = (GameUtil.randomInt() & 0x0fff) % fTargets.size();
	return (GameObject) fTargets.elementAt(choice);
	}
/**
 * Lookup an float spawn argument.
 * @return value found, or defaultValue.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public float getSpawnArg(String keyword, float defaultValue) 
	{
	return GameUtil.getSpawnArg(fSpawnArgs, keyword, defaultValue);
	}
/**
 * Lookup an integer spawn argument.
 * @return value found, or defaultValue.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public int getSpawnArg(String keyword, int defaultValue) 
	{
	return GameUtil.getSpawnArg(fSpawnArgs, keyword, defaultValue);
	}
/**
 * Lookup a string spawn argument.
 * @return value found, or defaultValue.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public String getSpawnArg(String keyword, String defaultValue)
	{
	return GameUtil.getSpawnArg(fSpawnArgs, keyword, defaultValue);
	}
/**
 * Get this object's spawnflags.
 * @return int
 */
public int getSpawnFlags() 
	{
	return fSpawnFlags;
	}
/**
 * Get the group of targets this objects belongs to.
 * @return java.util.Vector will be null if the object isn't a target.
 */
public Vector getTargetGroup() 
	{
	return fTargetGroup;
	}
/**
 * Get this object's targets.
 * @return java.util.Vector
 */
public Vector getTargets() 
	{
	return fTargets;
	}
/**
 * Check whether this Entitiy is a group slave. 
 * @return true if a group slave, false if a master or not in a group at all.
 */
public boolean isGroupSlave() 
	{
	return ((fGroup != null) && (fGroup.elementAt(0) != this));
	}
/**
 * Kills all entities that would touch the proposed new positioning
 * of ent.  Ent should be unlinked before calling this!.
 * @return boolean
 */
public boolean killBox() 
	{
	TraceResults tr;
	
	Point3f origin = fEntity.getOrigin();
	Tuple3f mins = fEntity.getMins();
	Tuple3f maxs = fEntity.getMaxs();
	Vector3f zerovec = new Vector3f();

	while (true)
		{
		tr = Engine.trace(origin, mins, maxs, origin, null, Engine.MASK_PLAYERSOLID);
		if (tr.fEntity == null)
			break;

		// nail it
		Object obj = tr.fEntity.getReference();
		if (obj instanceof GameObject)
			((GameObject) obj).damage(this, this, zerovec, origin, zerovec, 100000, 0, DAMAGE_NO_PROTECTION, Engine.TE_NONE, "telefrag");

		// if we didn't kill it, fail
		if (tr.fEntity.getSolid() != 0)
			return false;
		}

	return true;		// all clear
	}
/**
 * Let the object know it killed a player.
 * @param p baseq2.Player
 */
protected void registerKill(Player p) 
	{
	// We're just a dumb object, tell the stupid player he killed himself
	p.registerKill(null);
	}
/**
 * Set the gravity vector for this object - (0, 0, -1) would be the normal value for "down".
 * @param x float
 * @param y float
 * @param z float
 */
public void setGravity(float x, float y, float z) 
	{
	fGravity.x = x;
	fGravity.y = y;
	fGravity.z = z;
	}
/**
 * Set the gravity vector for this object.
 * @param g javax.vecmath.Vector3f (0, 0, -1) would be the traditional "down" value.
 */
public final void setGravity(Vector3f g) 
	{
	setGravity(g.x, g.y, g.z);
	}
/**
 * Cause some sign of damage to appear in the world.
 *
 * @param damageType what kind of damage is being done
 * @param origin spot where damage is occuring
 * @param normal direction damage is coming from
 * @param damage how much damage is being done.
 */
public static void spawnDamage(int damageType, Point3f origin, Vector3f normal, int damage) 
	{
	if (damageType != Engine.TE_NONE)
		{
		if (damage > 255)
			damage = 255;
			
		Engine.writeByte(Engine.SVC_TEMP_ENTITY);
		Engine.writeByte(damageType);
		Engine.writePosition(origin);
		
		if ((damageType != Engine.TE_ROCKET_EXPLOSION) && (damageType != Engine.TE_ROCKET_EXPLOSION_WATER))
			Engine.writeDir(normal);
			
		Engine.multicast(origin, Engine.MULTICAST_PVS);
		}
	}
/**
 * Spawn damage based on the info in a DamageEvent.
 */
public static void spawnDamage(DamageEvent de) 
	{
	spawnDamage(de.getTempEvent(), de.getDamagePoint(), de.getDamageNormal(), de.getAmount());
	}
public String toString()
	{
	if (fSpawnArgs == null)
		return super.toString();
		
	// we have spawn args, so we'll return something a little fancier		
	StringBuffer sb = new StringBuffer(super.toString());
/*
	sb.append("(");
	for (int i = 0; i < fSpawnArgs.length; i+=2)
		{
		sb.append(fSpawnArgs[i]);
		sb.append("=\"");
		sb.append(fSpawnArgs[i+1]);
		sb.append("\"");
		if (i < (fSpawnArgs.length - 2))
			sb.append(", ");
		}
	sb.append(")");
*/
	return sb.toString();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	}
/**
 * This method was created by a SmartGuide.
 * @param p q2jgame.Player
 */
public void use(Player p) 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
public void useTargets() 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)
		{
		Object obj = fTargets.elementAt(i);
		if (obj instanceof GameTarget)
			((GameTarget) obj).use(null);
		else
			System.out.println(obj.getClass().getName() + " doesn't implement GameTarget");
		}
	}
}