
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class worldspawn extends GameEntity
	{
	
public worldspawn(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, true); 	// make sure we get a special spot in the array

	setSolid(SOLID_BSP);
	setModelIndex(1); 			// world model is always index 1
	Game.gWorld = this;

	//
	// deal with spawn args
	//
	
	Engine.configString(Engine.CS_SKY, getSpawnArg("sky", "unit1_"));
	Engine.configString(Engine.CS_SKYROTATE, Float.toString(getSpawnArg("skyrotate", 0.0F)));
	Engine.configString(Engine.CS_SKYAXIS, getSpawnArg("skyaxis", "0 0 0"));
	Engine.configString(Engine.CS_CDTRACK, getSpawnArg("sounds", "0"));
//	Engine.configString(Engine.CS_MAXCLIENTS, );
	String s = getSpawnArg("message", null);
	if (s != null)
		Engine.configString(Engine.CS_NAME, s);
		
	s = getSpawnArg("nextmap", null);
	if (s != null)
		Game.setNextMap(s);		
	}
}