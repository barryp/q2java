
package q2jgame.spawn;


import q2java.*;
import q2jgame.*;

/**
 * A misc_banner seems to be a giant flag that
 * just sits and flutters in the wind.
 */

public class misc_banner extends GameEntity
	{
	private int fCurrentFrame;
	
public misc_banner(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	setSolid( SOLID_NOT );
	setModel( "models/objects/banner/tris.md2" );

	fCurrentFrame = (Game.randomInt() & 0x0fff) % 16;
	setFrame(fCurrentFrame);

	linkEntity();
	}
public void runFrame()
	{
	fCurrentFrame = (fCurrentFrame + 1) % 16;
	setFrame(fCurrentFrame);
	}
}