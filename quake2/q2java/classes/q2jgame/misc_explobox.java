
package q2jgame;

import q2java.*;

public class misc_explobox extends GameEntity
	{
	
public misc_explobox(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/objects/barrels/tris.md2");
	setSolid(SOLID_BBOX);
	linkEntity();
	}
}