
package q2jgame;

import q2java.*;

public class ammo_bullets extends GenericAmmo
	{
	
public ammo_bullets(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/ammo/bullets/medium/tris.md2");
	linkEntity();
	}
}