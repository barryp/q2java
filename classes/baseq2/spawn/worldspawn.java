package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class worldspawn extends GameObject
	{
	
public worldspawn(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, NativeEntity.ENTITY_WORLD); 	// make sure we get a special spot in the array

	fEntity.setSolid(NativeEntity.SOLID_BSP);
	fEntity.setModelIndex(1); 			// world model is always index 1
	baseq2.GameModule.gWorld = this;

	//
	// deal with spawn args
	//
	
	Engine.setConfigString(Engine.CS_SKY, getSpawnArg("sky", "unit1_"));
	Engine.setConfigString(Engine.CS_SKYROTATE, Float.toString(Game.getSpawnArg(spawnArgs, "skyrotate", 0.0F)));
	Engine.setConfigString(Engine.CS_SKYAXIS, getSpawnArg("skyaxis", "0 0 0"));
	Engine.setConfigString(Engine.CS_CDTRACK, getSpawnArg("sounds", "0"));
//	Engine.setConfigString(Engine.CS_MAXCLIENTS, );
	String s = getSpawnArg("message", null);
	if (s != null)
		Engine.setConfigString(Engine.CS_NAME, s);
		
	s = getSpawnArg("nextmap", null);
	if (s != null)
		baseq2.GameModule.setNextMap(s);		
	}
}