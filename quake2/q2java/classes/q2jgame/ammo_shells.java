
package q2jgame;

import q2java.*;

public class ammo_shells extends GenericAmmo
	{
	
public ammo_shells(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/ammo/shells/medium/tris.md2");
	linkEntity();
	}
}