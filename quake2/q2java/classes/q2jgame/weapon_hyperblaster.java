
package q2jgame;

import q2java.*;

public class weapon_hyperblaster extends GenericWeapon
	{
	
public weapon_hyperblaster(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_hyperb/tris.md2");
	linkEntity();
	}
}