
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class info_player_deathmatch extends GameEntity
	{
	
public info_player_deathmatch(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/objects/dmspot/tris.md2");
	setSolid(SOLID_BBOX);
	setMins(-32, -32, -24);
	setMaxs(32, 32, -16);
	linkEntity();	
	}
}