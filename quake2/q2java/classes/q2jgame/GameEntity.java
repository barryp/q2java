
package q2jgame;


import java.io.*;
import java.util.Enumeration;
import q2java.*;

public class GameEntity extends NativeEntity
	{
	private String[] fSpawnArgs;
	protected int fSpawnFlags;
	
	protected String fTargetName;
	protected String fTarget;
	
	// damage flags
	public final static int DAMAGE_RADIUS		= 0x00000001;	// damage was indirect
	public final static int DAMAGE_NO_ARMOR		= 0x00000002;	// armour does not protect from this damage
	public final static int DAMAGE_ENERGY		= 0x00000004;	// damage is from an energy based weapon
	public final static int DAMAGE_NO_KNOCKBACK	= 0x00000008;	// do not affect velocity, just view angles
	public final static int DAMAGE_BULLET		= 0x00000010; // damage is from a bullet (used for ricochets)
	public final static int DAMAGE_NO_PROTECTION	= 0x00000020; // armor, shields, invulnerability, and godmode have no effect	
	
public GameEntity() throws GameException
	{
	}
public GameEntity(String[] spawnArgs) throws GameException
	{
	this(spawnArgs, false);
	}
public GameEntity(String[] spawnArgs, boolean isWorld) throws GameException
	{
	super(isWorld);
	fSpawnArgs = spawnArgs;

	// look for common spawn arguments

	String s = getSpawnArg("origin");
	if (s != null)
		setOrigin(new Vec3(s));

	s = getSpawnArg("angles");
	if (s != null)
		setAngles(new Vec3(s));

	s = getSpawnArg("angle");
	if (s != null)
		{
		Float f = new Float(s);
		setAngles(0, f.floatValue(), 0);
		}
		
	s = getSpawnArg("spawnflags");
	if (s != null)
		fSpawnFlags = Integer.parseInt(s);
		
	s = getSpawnArg("target");
	if (s != null)
		fTarget = s.intern();		

	s = getSpawnArg("targetname");
	if (s != null)
		fTargetName = s.intern();		
	}
/**
 * This method was created by a SmartGuide.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public void damage(GameEntity inflictor, GameEntity attacker, 
	Vec3 dir, Vec3 point, Vec3 normal, 
	int damage, int knockback, int dflags, int tempEvent) 
	{
	spawnDamage(tempEvent, point, normal, damage);
	}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public Enumeration enumerateTargets() 
	{
	return new TargetEnumeration(fTarget);
	}
public String getSpawnArg(String keyword)
	{
	if (fSpawnArgs == null)
		return null;

	keyword = keyword.intern();
	for (int i = 0; i < fSpawnArgs.length; i+=2)
		if (keyword == fSpawnArgs[i])
			return fSpawnArgs[i+1];

	return null;
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	}
/**
 * This method was created by a SmartGuide.
 * @param damageType int
 * @param origin q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 */
public static void spawnDamage(int damageType, Vec3 origin, Vec3 normal, int damage ) 
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
public String toString()
	{
	if (fSpawnArgs == null)
		return super.toString();
		
	// we have spawn args, so we'll return something a little fancier		
	StringBuffer sb = new StringBuffer(super.toString());

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
}