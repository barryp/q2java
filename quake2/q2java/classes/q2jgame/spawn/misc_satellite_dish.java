
package q2jgame.spawn;


import q2java.*;
import q2jgame.*;

/*QUAKED misc_satellite_dish (1 .5 0) (-64 -64 0) (64 64 128)
*/

public class misc_satellite_dish extends GameEntity
	{
	private int fFrame;
	private boolean fIsMoving;
	
public misc_satellite_dish(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	//ent->movetype = MOVETYPE_NONE;
	setSolid( SOLID_BBOX );
	setMins( -64, -64, 0 );
	setMaxs(  64, 64, 128);
	setModel( "models/objects/satellite/tris.md2" );
	fIsMoving = false;
	
	linkEntity();
	}
public void runFrame()
	{
	if (fIsMoving)
		{
		if (fFrame < 38)
			setFrame(++fFrame);
		else
			fIsMoving = false;
		}			
	}
public void use(Player touchedBy) 
	{
	fFrame = 0;
	setFrame(0);
	fIsMoving = true;
	}
}