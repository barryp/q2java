
package q2jgame;

import q2java.*;

public class item_health extends GenericHealth
	{
	
public item_health(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/healing/medium/tris.md2");
	linkEntity();
	}
}