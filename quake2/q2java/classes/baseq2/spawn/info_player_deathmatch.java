
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class info_player_deathmatch extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-deathmatch";
	
	private NativeEntity fPad;
	
public info_player_deathmatch(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
			
	if (!GameModule.gIsDeathmatch)
		throw new InhibitedException("Inhibited in non-deathmatch");
		
	Game.addLevelRegistry(REGISTRY_KEY, this);
	
	fPad = new NativeEntity();	
	fPad.setOrigin(getOrigin());		
	fPad.setModel("models/objects/dmspot/tris.md2");
	fPad.setSolid(NativeEntity.SOLID_BBOX);
	fPad.setMins(-32, -32, -24);
	fPad.setMaxs(32, 32, -16);
	fPad.linkEntity();	
	}
}