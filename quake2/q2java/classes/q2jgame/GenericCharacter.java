
package q2jgame;

import q2java.*;

public class GenericCharacter extends GameEntity
	{
	protected int fHealth;
	protected boolean fIsFemale;
	
/**
 * Don't call this constructor, it's required for the DLL. But 
 * you can modify it if you need to initialize fields and methods
 * in the GenericMobile class.
 *
 * @param index int
 */
protected GenericCharacter() throws GameException
	{
	}	
public GenericCharacter(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
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
	spawnDamage(Engine.TE_BLOOD, point, normal, damage);
	setHealth(fHealth - damage);
	if (fHealth < 0)
		die(inflictor, attacker, damage, point);
	}
/**
 * This method was created by a SmartGuide.
 */
public void die(GameEntity inflictor, GameEntity attacker, int damage, Vec3 point)
	{
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 */
public void heal(int amount) 
	{
	setHealth(fHealth + amount);
	}
/**
 * This method was created by a SmartGuide.
 * @param val int
 */
public void setHealth(int val) 
	{
	fHealth = val;
	}
}