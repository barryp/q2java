package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class worldspawn extends GameObject
	{
	
public worldspawn(Element spawnArgs) throws GameException
	{
	super(spawnArgs, NativeEntity.ENTITY_WORLD); 	// make sure we get a special spot in the array

	fEntity.setSolid(NativeEntity.SOLID_BSP);
	fEntity.setModelIndex(1); 			// world model is always index 1
	q2java.baseq2.BaseQ2.gWorld = this;

	//
	// deal with spawn args
	//
	
	Engine.setConfigString(Engine.CS_SKY, getSpawnArg("sky", "unit1_"));
	Engine.setConfigString(Engine.CS_SKYROTATE, Float.toString(GameUtil.getSpawnArg(spawnArgs, "skyrotate", 0.0F)));
	Engine.setConfigString(Engine.CS_SKYAXIS, getSpawnArg("skyaxis", "0 0 0"));
	Engine.setConfigString(Engine.CS_CDTRACK, getSpawnArg("sounds", "0"));
//	Engine.setConfigString(Engine.CS_MAXCLIENTS, );
	String s = getSpawnArg("message", null);
	if (s != null)
		Engine.setConfigString(Engine.CS_NAME, s);		
	}
}