
package q2jgame;

import q2java.*;

public class ammo_grenades extends GenericAmmo
	{
	
public ammo_grenades(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/ammo/grenades/medium/tris.md2");
	linkEntity();
	}
}