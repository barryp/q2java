package barryp.autoload;

import org.w3c.dom.Document;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;

/**
 * Simple module that automatically exec's a file
 * named "autoload.cfg" as soon as possible once
 * the game is running.
 * 
 */
public class GameModule extends Gamelet implements ServerFrameListener, CrossLevel
	{
	
/**
 * Set ourselves up to be called one time, 10 seconds after 
 * the game starts.
 */
public GameModule(Document gameletInfo) 
	{
	super(gameletInfo);
	
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 10, -1);
	}
/**
 * Do our business and vacate from the scene.
 */
public void runFrame(int phase)
	{
	Engine.addCommandString("exec autoload.cfg");
	Game.getGameletManager().removeGamelet(this);
	}
}