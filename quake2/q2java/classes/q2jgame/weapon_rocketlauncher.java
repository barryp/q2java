
package q2jgame;

import q2java.*;

public class weapon_rocketlauncher extends GenericWeapon
	{
	
public weapon_rocketlauncher(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_rocket/tris.md2");
	linkEntity();
	}
}