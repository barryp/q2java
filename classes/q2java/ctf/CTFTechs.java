package q2java.ctf;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.InventoryList;

/**
 * Q2Java CTF Techs by themselves - can be mixed with other gamelets like BaseQ2 or Paranoia.
 * 
 * @author Barry Pederson
 */

public class CTFTechs extends q2java.core.Gamelet implements GameStatusListener
	{
	
	public CTFTechs(Document gameletDoc)
	{
		super( gameletDoc );
		
		// ask to be called on level changes
		Game.addGameStatusListener(this);

		// update player inventory lists to support techs
		InventoryList.addItem("Disruptor Shield");
		InventoryList.addItem("AutoDoc");
		InventoryList.addItem("Time Accel");
		InventoryList.addItem("Power Amplifier");
		
	}
public void gameStatusChanged(GameStatusEvent gse)
	{
	if (gse.getState() == GameStatusEvent.GAME_POSTSPAWN)
		{
		// now it's time to spawn the techs.
		try 
			{
			new AutoDoc(GenericTech.NO_HUD_ICON);
			new DisruptorShield(GenericTech.NO_HUD_ICON);
			new PowerAmplifier(GenericTech.NO_HUD_ICON);
			new TimeAccel(GenericTech.NO_HUD_ICON);
			}
		catch ( Exception e )
			{
			// do nothing here.
			System.out.println( "error in spawning techs... " + e );
			}		
		}
	}
/**
 * Help for this module.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("Adds CTF techs to any Q2Java game\n");
	Game.dprint("   no commands available\n");
	}
/**
 * Unload the tech module
 */
public void unload() 
	{
	// we no longer want to be notified of level changes
	Game.removeGameStatusListener(this);
	}
}