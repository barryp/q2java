
package q2jgame;

import q2java.*;

public class DoorTrigger extends GameEntity
	{
	private int fArea;
	
public DoorTrigger(GameEntity door) throws GameException
	{
	Vec3 mins = door.getMins();
	Vec3 maxs = door.getMaxs();

	// expand 
	mins.x -= 60;
	mins.y -= 60;
	maxs.x += 60;
	maxs.y += 60;

	setMins(mins);
	setMaxs(maxs);
	
	setOwner(door);
	setSolid(SOLID_TRIGGER);
	linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	((GameEntity)getOwner()).touch(touchedBy);
	}
}