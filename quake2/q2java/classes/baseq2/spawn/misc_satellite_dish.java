
package baseq2.spawn;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/*QUAKED misc_satellite_dish (1 .5 0) (-64 -64 0) (64 64 128)
*/

public class misc_satellite_dish extends GameObject implements FrameListener
	{
	private int fFrame;
	private boolean fIsMoving;
	
public misc_satellite_dish(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	//ent->movetype = MOVETYPE_NONE;
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setMins( -64, -64, 0 );
	fEntity.setMaxs(  64, 64, 128);
	fEntity.setModel( "models/objects/satellite/tris.md2" );
	fIsMoving = false;
	
	fEntity.linkEntity();
	}
public void runFrame(int phase)
	{
	if (fFrame < 38)
		fEntity.setFrame(++fFrame);
	else
		// no more runFrame() calls
		Game.removeFrameListener(this);
	}
public void use(Player touchedBy) 
	{
	fFrame = 0;
	fEntity.setFrame(0);

	// ask to start receiving runFrame() calls
	Game.addFrameListener(this, 0, 0);
	}
}