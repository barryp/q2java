
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class item_health extends GenericHealth
	{
	
public item_health(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/healing/medium/tris.md2", 
		"items/n_health.wav", 10, false);
	}
}