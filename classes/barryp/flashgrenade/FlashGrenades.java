package barryp.flashgrenade;

import org.w3c.dom.Document;

import q2java.core.*;

/**
 * Demo of altered weapons.
 * 
 * @author Barry Pederson
 */
public class FlashGrenades extends q2java.core.Gamelet 
	{
	
/**
 * This method was created by a SmartGuide.
 */
public FlashGrenades(Document gameletInfo) 
	{
	super(gameletInfo);

	Game.addPackagePath("barryp.flashgrenade");	
	}
/**
 * Help for FlashGrenades module.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Changes handgrenade to also blind players\n");
	Game.dprint("   no commands available\n");
	}
/**
 * Called when module is unloaded.
 */
public void unload() 
	{
	//remove the packagePath
	Game.removePackagePath("barryp.flashgrenade");	
	}
}