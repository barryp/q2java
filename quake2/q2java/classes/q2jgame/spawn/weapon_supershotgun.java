
package q2jgame;

import q2java.*;

public class weapon_supershotgun extends GenericWeapon
	{
	
public weapon_supershotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_shotg2/tris.md2");
	linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 */
public void fire() {
	return;
}
}