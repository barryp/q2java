
package q2jgame;

import q2java.*;

public class weapon_machinegun extends GenericWeapon
	{
	
public weapon_machinegun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_machn/tris.md2");
	linkEntity();
	}
}