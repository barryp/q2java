
package q2jgame;

import q2java.*;

public class weapon_bfg extends GenericWeapon
	{
	
public weapon_bfg(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/weapons/g_bfg/tris.md2");
	linkEntity();
	}
}