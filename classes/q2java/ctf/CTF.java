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

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.InventoryList;

/**
 * Q2Java CTF module.
 * 
 * @author Menno van Gangelen
 */

public class CTF extends q2java.core.Gamelet implements GameStatusListener
{
	public final static int STAT_CTF_TECH          = 26;
	public CTF(String moduleName)
	{
		super( moduleName );		
	}
	public void gameStatusChanged(GameStatusEvent gse)
	{
		switch (gse.getState())
		{
			case GameStatusEvent.GAME_PRESPAWN:
				// overrule the statbar
				Engine.setConfigString (Engine.CS_STATUSBAR, CTFPlayer.CTF_STATUSBAR);				
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
 * Get which Gamelet classes this Gamelet requires.
 * @return java.lang.Class[]
 */
public String[] getGameletDependencies() 
	{
	String[] result = { "q2java.baseq2.BaseQ2" };
	return result;
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
	 * Initialize the CTF gamelet.
	 */
	public void init() 
	{    
		//leighd 04/10/99 - add the package path
		Game.addPackagePath("q2java.ctf");
	
		// ask to be called on level changes
		Game.addGameStatusListener(this);

		// update player inventory lists to support techs
		InventoryList.addItem("Disruptor Shield");
		InventoryList.addItem("AutoDoc");
		InventoryList.addItem("Time Accel");
		InventoryList.addItem("Power Amplifier");			
	}
/**
 * CTF needs special maps, so require a level change before starting.
 * @return boolean
 */
public boolean isLevelChangeRequired() 
	{
	return true;
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
	 * Switch players back to being baseq2.Players
	 */
	public void unload() 
	{
		//leighd 04/11/99 - remove the package path
		Game.removePackagePath("q2java.ctf");
	
		// we no longer want to be notified of level changes
		Game.removeGameStatusListener(this);		
	}
}