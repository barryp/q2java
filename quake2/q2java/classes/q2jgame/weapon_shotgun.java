
package q2jgame;

import q2java.*;

public class weapon_shotgun extends GenericWeapon
	{
	
public weapon_shotgun(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_shotg/tris.md2");
	linkEntity();
	}
}