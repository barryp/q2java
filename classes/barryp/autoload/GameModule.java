package barryp.autoload;

import q2java.*;
import q2java.core.*;

/**
 * Simple module that automatically exec's a file
 * named "autoload.cfg" as soon as possible once
 * the game is running.
 * 
 */
public class GameModule extends q2java.core.Gamelet implements FrameListener, CrossLevel
	{
	
/**
 * Set ourselves up to be called one time, 10 seconds after 
 * the game starts.
 */
public GameModule(String moduleName) 
	{
	super(moduleName);
	
	Game.addFrameListener(this, Game.FRAME_BEGINNING, 10, -1);
	}
/**
 * Do our business and vacate from the scene.
 */
public void runFrame(int phase)
	{
	Engine.addCommandString("exec autoload.cfg");
	Game.removeGamelet(this);
	}
}