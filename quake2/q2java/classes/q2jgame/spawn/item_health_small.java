
package q2jgame;

import q2java.*;

public class item_health_small extends GenericHealth
	{
	
public item_health_small(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/healing/stimpack/tris.md2");
	fHealthValue = 2;
	fPickupSoundIndex = Engine.soundIndex("items/s_health.wav");	
	linkEntity();
	}
}