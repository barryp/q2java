package barryp.persist;

import org.w3c.dom.Document;

import q2java.core.*;

/**
 * Simple module for persistance.
 * 
 */
public class GameModule extends q2java.core.Gamelet 
	{
	
/**
 * This method was created by a SmartGuide.
 */
public GameModule(Document gameletInfo) 
	{
	super(gameletInfo);
	}
/**
 * This method was created by a SmartGuide.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Will save a player's settings in the sandbox when they disconnect\n");
	Game.dprint("   no commands available\n");
	}
}