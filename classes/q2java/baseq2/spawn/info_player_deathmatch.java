package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class info_player_deathmatch extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-deathmatch";
	
public info_player_deathmatch(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
			
	if (!baseq2.GameModule.gIsDeathmatch)
		throw new InhibitedException("Inhibited in non-deathmatch");
		
	Game.addLevelRegistry(REGISTRY_KEY, this);

	fEntity = new NativeEntity();
	fEntity.setReference(this);	
		
	fEntity.setOrigin(getOrigin());		
	fEntity.setModel("models/objects/dmspot/tris.md2");
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setMins(-32, -32, -24);
	fEntity.setMaxs(32, 32, -16);
	fEntity.linkEntity();	
	}
}