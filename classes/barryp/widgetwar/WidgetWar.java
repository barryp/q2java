package barryp.widgetwar;

import java.util.Vector;

import org.w3c.dom.*;

import q2java.*;
import q2java.gui.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.core.gui.*;
import q2java.baseq2.*;

/**
 * WidgetWar gamelet
 *
 * @author Barry Pederson
 */
public class WidgetWar extends Gamelet implements GameStatusListener, ServerFrameListener
	{
	// handle to the general BaseQ2 world
	protected Object fBaseQ2Token;

	protected Vector fRandomTechnologies = new Vector();
	
	protected final static float DEVELOPMENT_INTERVAL = 240;
	protected final static float DEVELOPMENT_PADDING  = 120;
	
/**
 * Constructor for BountyHunter game module.
 * @param moduleName java.lang.String
 */
public WidgetWar(Document gameletInfo) 
	{
	super(gameletInfo);	

	// ask to be called on level changes
	Game.addGameStatusListener(this);		
	}
public void gameStatusChanged(GameStatusEvent e)
	{
	switch (e.getState())
		{
		case GameStatusEvent.GAME_PRESPAWN:
			// do this stuff only once during the first level change we hear about
			if (fBaseQ2Token == null)
				{
				// make sure there's some environment for the GameObjects
				// to operate in.
				fBaseQ2Token = BaseQ2.getReference();	
					
				Game.addPackagePath("q2java.baseq2");
				Game.addPackagePath("barryp.widgetwar");
				}

			// precache HUD icons
			DirectionIndicator.precacheImages();
			BarGraph.precacheImages();
			WidgetWarrior.precacheImages();
			
			// remove ammo/weapons/other items from map
			stripMap();
			break;

		case GameStatusEvent.GAME_POSTSPAWN:
			// override the players HUDs
			Engine.setConfigString(Engine.CS_STATUSBAR, WidgetWarrior.getHUD());

			// load technologies and kick out the first ones
			loadTechnologies();

			// schedule the first tech distribution
			Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 20 + (GameUtil.randomFloat() * 10), -1);
			
			// drop DataCDs
			DataCD cd = new DataCD(Team.TEAM1);
			cd.reset();
			cd = new DataCD(Team.TEAM2);
			cd.reset();
			
			break;
		}
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return WidgetWarrior.class;
	}
/**
 * Setup technologies for the start of a level.
 */
private void loadTechnologies() 
	{
	// setup special null technologies for control and payload
	Team.TEAM1.addTechnology(new Technology(Technology.TYPE_CONTROL), null);
	Team.TEAM2.addTechnology(new Technology(Technology.TYPE_CONTROL), null);
	
	Team.TEAM1.addTechnology(new Technology(Technology.TYPE_PAYLOAD), null);
	Team.TEAM2.addTechnology(new Technology(Technology.TYPE_PAYLOAD), null);
	
	// Give both teams the common technologies
	NodeList nl = getGameletDocument().getElementsByTagName("common");
	int n = nl.getLength();
	for (int i = 0; i < n; i++)
		{
		Element e = (Element) nl.item(i);
		NodeList nl2 = e.getElementsByTagName("technology");
		int n2 = nl2.getLength();
		for (int i2 = 0; i2 < n2; i2++)
			{
			Element e2 = (Element) nl2.item(i2);
			try
				{
				Team.TEAM1.addTechnology(new Technology(e2), null);
				Team.TEAM2.addTechnology(new Technology(e2), null);
				}
			catch (Exception ex)
				{
				ex.printStackTrace();
				}
			}
		}

	// Build up the list of technologies to be distributed at random
	fRandomTechnologies.removeAllElements();	
	nl = getGameletDocument().getElementsByTagName("random");
	n = nl.getLength();
	for (int i = 0; i < n; i++)
		{
		Element e = (Element) nl.item(i);
		NodeList nl2 = e.getElementsByTagName("technology");
		int n2 = nl2.getLength();
		for (int i2 = 0; i2 < n2; i2++)
			{
			Element e2 = (Element) nl2.item(i2);
			try
				{
				Technology t = new Technology(e2);
				fRandomTechnologies.addElement(t);
				}
			catch (Exception ex)
				{
				ex.printStackTrace();
				}
			}
		}
	}
/**
 * We use this to periodically add new technologies to the game.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	int nTechnologies = fRandomTechnologies.size();
	if (nTechnologies >= 2)
		{
		int i = GameUtil.randomInt(nTechnologies);
		Technology t = (Technology) fRandomTechnologies.elementAt(i);
		fRandomTechnologies.removeElementAt(i);
		Team.TEAM1.addTechnology(t, "The Red R&D Lab has perfected " + t.getName() + " technology!\n");

		i = GameUtil.randomInt(nTechnologies - 1);
		t = (Technology) fRandomTechnologies.elementAt(i);
		fRandomTechnologies.removeElementAt(i);
		Team.TEAM2.addTechnology(t, "The Blue R&D Lab has perfected " + t.getName() + " technology!\n");

		// play a non-event sound to let players know they have new tech
		Team.TEAM1.getTeamBase().getBaseEntity().sound(NativeEntity.CHAN_RELIABLE+NativeEntity.CHAN_NO_PHS_ADD+NativeEntity.CHAN_VOICE, Engine.getSoundIndex("world/v_cit1.wav"), 1, NativeEntity.ATTN_NONE, 0);	
		
		// call back again between 4 and 6 minutes from now
		Game.addServerFrameListener(this, Game.FRAME_BEGINNING, DEVELOPMENT_INTERVAL + (GameUtil.randomFloat() * DEVELOPMENT_PADDING), -1);
		}	
	}
/**
 * Strip ammo/weapons/items from the map.
 */
private void stripMap() 
	{
	Document doc = Game.getDocument("q2java.level");

	// look for <entity>..</entity> sections
	Node nextNode = null;
	for (Node n = doc.getDocumentElement().getFirstChild(); n != null; n = nextNode)
		{
		// get the next node now, since we may be deleting the current node
		nextNode = n.getNextSibling();

		try
			{
			Element e = (Element) n;
			if ("entity".equals(e.getTagName()))
				{
				String entClassName = e.getAttribute("class");
				if (entClassName == null)
					continue;

				// keep the CTF flag spawnpoint
				if (entClassName.startsWith("item_flag_"))
					continue;

				// drop everything else
				if (entClassName.startsWith("weapon_")
				||  entClassName.startsWith("ammo_")
				||  entClassName.startsWith("item_"))	
	 					e.getParentNode().removeChild(e);
				}
			}
		catch (ClassCastException cce)
			{
			// guess n wasn't an Element..oh well
			}
		}		
	}
/**
 * Default help svcmd for a GameModule.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint("No special commands available in WidgetWar\n");
	}
/**
 * Called when module is unloaded.
 */
public void unload() 
	{
	if (fBaseQ2Token != null)
		{
		BaseQ2.freeReference(fBaseQ2Token);
		Game.removePackagePath("barryp.widgetwar");
		Game.removePackagePath("q2java.baseq2");				
		}
		
	// we no longer want to be notified of level changes
	Game.removeGameStatusListener(this);

	// stop being called to add new technologies
	Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
	}
}