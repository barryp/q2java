
package q2jgame;

import q2java.*;

public class item_health_large extends GenericHealth
	{
	
public item_health_large(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/healing/large/tris.md2");
	fHealthValue = 25;
	fPickupSoundIndex = Engine.soundIndex("items/l_health.wav");		
	linkEntity();
	}
}