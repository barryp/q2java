package barryp.persist;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

import q2java.baseq2.*;
import q2java.baseq2.spawn.*;

/**
 * Persistent player class.  
 *
 * @author Barry Pederson
 */

public class Player extends q2java.baseq2.Player 
	{
	protected transient boolean fWasSaved;	
	
/**
 * Create a new Player Game object, and associate it with a Player
 * native entity.
 */
public Player(NativeEntity ent) throws GameException
	{
	super(ent);
	fWasSaved = false;
	}
/**
 * Handle a new connection by first trying to restore a saved
 * player and associating it with the player entity - if that fails,
 * create a brand-new Player object.
 *
 * @param ent q2java.NativeEntity
 * @param playerInfo java.lang.String
 * @param loadgame boolean
 */
public static void connect(NativeEntity ent) throws GameException
	{
	// find the player's name
	Hashtable h = new Hashtable();
	StringTokenizer st = new StringTokenizer(ent.getPlayerInfo(), "\\");
	while (st.hasMoreTokens())
		{
		String key = st.nextToken();
		if (st.hasMoreTokens())
			h.put(key, st.nextToken());
		}		
	String name = (String) h.get("name");		

	// try to restore a saved player
	try
		{
		File sandbox = new File(Engine.getGamePath(), "sandbox");
		File pfile = new File(sandbox, name + ".player");
		FileInputStream fis = new FileInputStream(pfile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Player p = (Player) ois.readObject();
		ois.close();

		p.fWasSaved = true;
		
		// hook the Player game object and entity together		
		p.fEntity = ent;
		ent.setPlayerListener(p);
		ent.setReference(p);

		// hook our weapon back up
		p.getCurrentWeapon().activate();

		// sign up to receive server frame notices at the beginning and end of server frames
		Game.addServerFrameListener(p, Game.FRAME_BEGINNING + Game.FRAME_END, 0, 0);		
	
		// sign up to receive broadcast messages using the default locale
		p.fResourceGroup = Game.getResourceGroup(Locale.getDefault());
		Game.getPrintSupport().addPrintListener(p, PrintEvent.PRINT_ANNOUNCE+PrintEvent.PRINT_TALK, false);
			
		// update the player info, in case they
		// changed something (other than their name)
		// while they were disconnected
		p.playerInfoChanged(ent.getPlayerInfo());		
		return;
		}
	catch (FileNotFoundException fnfe)
		{
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	
	// couldn't restore a saved player, so create a new one.			
	new Player(ent);
	}
/**
 * Called by the DLL when the player should begin playing in the game.
 * @param loadgame boolean
 */
public void playerBegin() 
	{		
	Engine.debugLog("Player.begin()");

	fStartTime = (float) Game.getGameTime();	
	fEntity.setPlayerStat(NativeEntity.STAT_HEALTH_ICON, (short) Engine.getImageIndex("i_health"));	
	fEntity.setPlayerGravity((short)q2java.baseq2.BaseQ2.gGravity);
	
	if (!fWasSaved)
		clearSettings();
		
	fWasSaved = false;	 // this way the player will be reset on level changes
					 // set to true if you want all players to keep their weapons across levels	
	spawn();	
	welcome();

	// make sure all view stuff is valid
	endServerFrame();		
	}
/**
 * Called by the DLL when the player is disconnecting. 
 * We should clean things up and say goodbye.
 * Be sure you drop any references to this player object.  
 */
public void playerDisconnect()
	{
	// write the player object to a file
	try
		{
		File sandbox = new File(Engine.getGamePath(), "sandbox");
		File pfile = new File(sandbox, getName() + ".player");
		FileOutputStream fos = new FileOutputStream(pfile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
		}
	catch (ExceptionInInitializerError eiie)
		{
		eiie.getException().printStackTrace();
		}
	catch (IOException e)
		{
		e.printStackTrace();
		fEntity.cprint(Engine.PRINT_HIGH, "Couldn't save your player state: " + e.getMessage() + "\n");
		}
	
	super.playerDisconnect();
	}
}