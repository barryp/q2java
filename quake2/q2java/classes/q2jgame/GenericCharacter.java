
package q2jgame;

import q2java.*;

public class GenericCharacter extends GameEntity
	{
	protected int fHealth;
	protected boolean fIsFemale;
	protected GenericWeapon fWeapon;
	protected GenericWeapon fNextWeapon;
	
/**
 * Don't call this constructor, it's required for the DLL. But 
 * you can modify it if you need to initialize fields and methods
 * in the GenericMobile class.
 *
 * @param index int
 */
protected GenericCharacter() throws GameException
	{
	Game.debugLog("Executing GenericMobile() constructor");	
	}	
public GenericCharacter(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
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