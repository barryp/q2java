package q2java.ctf;

/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.BaseQ2;
import q2java.baseq2.InventoryList;

/**
 * Q2Java CTF module.
 * 
 * @author Menno van Gangelen
 */

public class CTF extends q2java.core.Gamelet implements GameStatusListener
{
	public final static int STAT_CTF_TECH          = 26;
	protected Object fBaseQ2Token;
	public CTF(Document gameletDoc)
	{
		super( gameletDoc );
			
		// ask to be called on level changes
		Game.addGameStatusListener(this);	
	}
	public void gameStatusChanged(GameStatusEvent gse)
	{
		switch (gse.getState())
		{
			case GameStatusEvent.GAME_PRESPAWN:
				// overrule the statbar
				Engine.setConfigString (Engine.CS_STATUSBAR, CTFPlayer.CTF_STATUSBAR);

				// do this stuff only once during the first level change we hear about
				if (fBaseQ2Token == null)
					{
					// make sure there's some environment for the GameObjects
					// to operate in.
					fBaseQ2Token = BaseQ2.getReference();	
					
					Game.addPackagePath("q2java.baseq2");
					Game.addPackagePath("q2java.ctf");
	
					// update player inventory lists to support techs
					InventoryList.addItem("Disruptor Shield");
					InventoryList.addItem("AutoDoc");
					InventoryList.addItem("Time Accel");
					InventoryList.addItem("Power Amplifier");
					}					
				break;

			case GameStatusEvent.GAME_POSTSPAWN:
				// now it's time to spawn the techs.
				try 
				{
					new AutoDoc(STAT_CTF_TECH);
					new PowerAmplifier(STAT_CTF_TECH);
					new DisruptorShield(STAT_CTF_TECH);
					new TimeAccel(STAT_CTF_TECH);
				}
				catch ( Exception e )
				{
					// do nothing here.
					System.out.println( "error in spwaning techs... " + e );
				}		
				break;
			
		default:
			break;			
		}
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return CTFPlayer.class;
	}
	/**
	 * This method was created by a SmartGuide.
	 */
	public void svcmd_help(String[] args) 
	{
		Game.dprint("A Capture The Flag conversion,\n");
		Game.dprint("the popular Q2 mod from Zoid.\n");
		Game.dprint("   no commands available\n");
	}
	/**
	 * Detach the CTF gamelet from the system
	 */
	public void unload() 
	{
		if (fBaseQ2Token != null)
		{
			BaseQ2.freeReference(fBaseQ2Token);
		
			//leighd 04/11/99 - remove the package path
			Game.removePackagePath("q2java.ctf");
			Game.removePackagePath("q2java.baseq2");
		}
		
		// we no longer want to be notified of level changes
		Game.removeGameStatusListener(this);
	}
}