
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

public class item_health extends GenericHealth
	{
	
public item_health(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, "models/items/healing/medium/tris.md2", 
		"items/n_health.wav", 10, false);
	}
}