
package q2jgame;

import q2java.*;

public class GenericItem extends GameEntity
	{
	
public GenericItem(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setRenderFX(RF_GLOW); // all items glow
	setSolid(SOLID_BBOX);
	}
}