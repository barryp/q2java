
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class item_health_large extends GenericHealth
	{
	
public item_health_large(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/healing/large/tris.md2",
		"items/l_health.wav", 25, false);
	}
}