
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_health_small extends GenericHealth
	{
	
public item_health_small(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/healing/stimpack/tris.md2",
		"items/s_health.wav", 2, true);
	}
}