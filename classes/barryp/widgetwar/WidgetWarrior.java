package barryp.widgetwar;

import java.io.*;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.gui.*;

import q2java.baseq2.*;
import q2java.baseq2.event.*;
import q2java.baseq2.gui.PlayerMenu;
import q2java.baseq2.spawn.*;

import q2java.gui.*;

/**
 * WidgetWar player.  
 *
 * @author Barry Pederson
 */

public class WidgetWarrior extends q2java.baseq2.Player 
	{
	protected int fCurrentWidget;
	protected GenericWeapon fDeathWeapon;
	
	protected boolean fDestroyWarned;
	protected int[][] fComponents = new int[MAX_WIDGETS][3];
	protected WidgetBody[] fWidgets = new WidgetBody[MAX_WIDGETS];
	protected Point3f[] fWidgetTargets = new Point3f[MAX_WIDGETS];

	protected Vector fStolenTechnologies = new Vector();
	private float fEnergy;
	protected String fCurrentAmmoType;

	// simple timekeeping stuff
	private int fTick;
	private boolean fIconFlash;
	private boolean fCarryingTech;

	// HUD Tracker Stuff
	private DirectionIndicator fHUDDirection;
	private BarGraph           fHUDRange;
	
	// -----------------------------------------------
	final static String[] MENU_HEADER = { "===== WidgetWar =====", ""};
	final static String MENU_AUTHOR = "Barry Pederson";

	protected final static int 	MAX_ENERGY_DROP	= 150;
	protected final static int 	MAX_WIDGETS 	= 5;
	protected final static float MAX_ENERGY  	= 500.0F;
	protected final static float ENERGY_JUMP 	= 4.0F; // cost in energy for each jump
	protected final static float ENERGY_RUN  	= 0.05F; // energy change while running (per tick)
	protected final static float ENERGY_STAND	= 0.1F; // energy change while standing still (per tick)	
	protected final static float HEALTH_STAND	= 0.1F; // health change while standing (per tick)	
	
	// array offsets of the various types of components
	protected final static int COMPONENT_BODY	 = 0;
	protected final static int COMPONENT_CONTROL = 1;
	protected final static int COMPONENT_PAYLOAD = 2;
	protected final static int COMPONENT_PAYLOAD2 = 3;

	public final static int STAT_DIRECTION				= 16;
	public final static int STAT_RANGE					= 17;
	public static final int STAT_CTF_TEAM1_PIC			= 18;
	public static final int STAT_CTF_TEAM1_CAPS  	    = 19;
	public static final int STAT_CTF_TEAM2_PIC    		= 20;
	public static final int STAT_CTF_TEAM2_CAPS     	= 21;
	public static final int STAT_CTF_FLAG_PIC      		= 22;
	public static final int STAT_CTF_JOINED_TEAM1_PIC	= 4;
	public static final int STAT_CTF_JOINED_TEAM2_PIC 	= 5;
	public static final int STAT_CTF_TEAM1_HEADER     	= 6;
	public static final int STAT_CTF_TEAM2_HEADER     	= 9;	
		
	protected final static int STAT_WIDGET_0		= 23;
	protected final static int STAT_WIDGET_1		= 24;
	protected final static int STAT_WIDGET_2		= 25;
	protected final static int STAT_WIDGET_3		= 26;
	protected final static int STAT_WIDGET_4		= 27;

	protected final static int STAT_WIDGET_BODY 	= 28; // STAT_WIDGET + COMPONENT_BODY
	protected final static int STAT_WIDGET_CONTROL	= 29; // STAT_WIDGET + COMPONENT_CONTROL
	protected final static int STAT_WIDGET_PAYLOAD	= 30; // STAT_WIDGET + COMPONENT_PAYLOAD
	protected final static int STAT_WIDGET_PAYLOAD2	= 31; // STAT_WIDGET + COMPONENT_PAYLOAD2

	// image indexes of pics used to show Widget status on HUD
	protected final static short[] gWidgetPics = new short[4];
	
	
/*	
	protected final static int STAT_WIDGET_NUM		= 30;
	protected final static int STAT_WIDGET_ACTIVE	= 31;
*/
	
	private final static String HUD = 
		"yb	-24" +

		// health
		" xv 0" +
		" hnum" +
		" xv 50" +
		" pic " + NativeEntity.STAT_HEALTH_ICON +

		// energy
		" xv 100" +
		" num 5 " + NativeEntity.STAT_AMMO +
		" xv 185" +
		" pic " + NativeEntity.STAT_AMMO_ICON +
/*		
		// armor
		"if 4 " +
		"	xv	200 " +
		"	rnum " +
		"	xv	250 " +
		"	pic 4 " +
		"endif " +

		// selected item
		"if 6 " +
		"	xv	296 " +
		"	pic 6 " +
		"endif " +
*/
		" yb -50" +

		// picked up item
		" if 7" +
			" xv 0" +
			" pic " + NativeEntity.STAT_PICKUP_ICON +
			" xv 26" +
			" yb -42" +
			" stat_string " + NativeEntity.STAT_PICKUP_STRING +
			" yb -50" +
		" endif" +
/*
		// timer
		"if 9 " +
		  "xv 246 " +
		  "num 2 10 " +
		  "xv 296 " +
		  "pic 9 " +
		"endif " +
*/
		//  help / weapon icon 
		" if " + NativeEntity.STAT_HELPICON +
		  " xv 148" +
		  " pic " + NativeEntity.STAT_HELPICON +
		" endif" +

		// Tracking widgets
		" xr -50" + 
		" if " + STAT_DIRECTION +
			// compass
			" yt 2" +
			" pic  " + STAT_DIRECTION +
		" endif" +
		
		" if " + STAT_RANGE +
			// range
			" yt 50" +
			" pic " + STAT_RANGE +
		" endif" +
		
		//  frags
		" xr -50" +
		" yt 75" +
		" num 3 " + NativeEntity.STAT_FRAGS +

		// have flag graph
		" if " + STAT_CTF_FLAG_PIC +
		  " yt 100" +
		  " xr -24" +
		  " pic " + STAT_CTF_FLAG_PIC +
		" endif" +
		
		// red team
		" yb -102" +
		" if " + STAT_CTF_TEAM1_PIC +
		  " xr -26" +
		  " pic " + STAT_CTF_TEAM1_PIC +
		" endif" +
		" xr -115" +
		" num 5 " + STAT_CTF_TEAM1_CAPS +
		
		//joined overlay
		" if " + STAT_CTF_JOINED_TEAM1_PIC +
		  " yb -104" +
		  " xr -28" +
		  " pic " + STAT_CTF_JOINED_TEAM1_PIC +
		" endif" +

		// blue team
		" yb -75" +
		" if " + STAT_CTF_TEAM2_PIC +
		  " xr -26" +
		  " pic " + STAT_CTF_TEAM2_PIC +
		" endif" +
		" xr -115" +
		" num 5 " + STAT_CTF_TEAM2_CAPS +

		// joined overlay
		" if " + STAT_CTF_JOINED_TEAM2_PIC +
		  " yb -77" +
		  " xr -28" +
		  " pic " + STAT_CTF_JOINED_TEAM2_PIC + 
		" endif" +
/*		
		// widget strings
		" xl 0" +
		" yb -100" +
		" num 1 " + STAT_WIDGET_NUM +
		" if " + STAT_WIDGET_ACTIVE + 
		  " xl 20" +
		  " pic " + STAT_WIDGET_ACTIVE +
		" endif" +
*/

		" yb -110" +
		" if " + STAT_WIDGET_0 +
			" xl 0 pic " + STAT_WIDGET_0 +
		" endif" +
		" if " + STAT_WIDGET_1 +
			" xl 20 pic " + STAT_WIDGET_1 +
		" endif" +
		" if " + STAT_WIDGET_2 +
			" xl 40 pic " + STAT_WIDGET_2 +
		" endif" +
		" if " + STAT_WIDGET_3 +
			" xl 60 pic " + STAT_WIDGET_3 +
		" endif" +
		" if " + STAT_WIDGET_4 +
			" xl 80 pic " + STAT_WIDGET_4 +
		" endif" +
		
		" xl 0" +
		" if " + STAT_WIDGET_BODY + 
			" yb -80 stat_string " + STAT_WIDGET_BODY +
		" endif" +
		" if " + STAT_WIDGET_CONTROL + 
			" yb -70 stat_string " + STAT_WIDGET_CONTROL +
		" endif" +
		" if " + STAT_WIDGET_PAYLOAD +
			" yb -60 stat_string " + STAT_WIDGET_PAYLOAD +
		" endif" +
		" if " + STAT_WIDGET_PAYLOAD2 +
			" yb -50 stat_string " + STAT_WIDGET_PAYLOAD2 +
		" endif"
		;
	
/**
 * Create a new Player Game object, and associate it with a Player
 * native entity.
 */
public WidgetWarrior(NativeEntity ent) throws GameException
	{
	super(ent);

	// setup the icons
	fEntity.setPlayerStat(STAT_CTF_TEAM1_PIC,  (short)Engine.getImageIndex("i_ctf1") );
//	fEntity.setPlayerStat(STAT_CTF_TEAM1_CAPS, (short)Team.TEAM1.getCaptures()       );
	fEntity.setPlayerStat(STAT_CTF_TEAM2_PIC,  (short)Engine.getImageIndex("i_ctf2") );
//	fEntity.setPlayerStat(STAT_CTF_TEAM2_CAPS, (short)Team.TEAM2.getCaptures()       );

	fHUDDirection = new DirectionIndicator(this.fEntity, STAT_DIRECTION);
	fHUDRange = new BarGraph(this.fEntity, STAT_RANGE);
	fHUDRange.setMinValue(1500);
	fHUDRange.setMaxValue(100);	
	}
/**
 * Add ammo to the player's inventory.
 * @param ah something that holds ammo, either a weapon or an ammobox.
 * @return boolean true if some or all of the ammo was taken.
 */
protected boolean addAmmo(AmmoHolder ah) 
	{
	setEnergy(getEnergy() + ah.getAmmoCount());
	return true;		
	}
/**
 * Called when the player picks up a datacard.
 * @param team java.lang.Object
 */
public void addStolenTechnology(StolenTechnology st) 
	{
	if (st.getTeam() == getTeam())
		{
		fEntity.cprint(Engine.PRINT_HIGH, "You recovered a bit of your team's technology!\n");
		st.release(this);
		}
	else
		{
		fEntity.cprint(Engine.PRINT_HIGH, "You found a piece of enemy technology!\n");
		fStolenTechnologies.addElement(st);

		// look guilty
		setCarryingTech(true);
		}
	}
/**
 * Think about the player's energy and health.
 */
protected void beginServerFrame() 
	{
	super.beginServerFrame();

	// bail if not joined to a team or dead
	if (!(getTeam() instanceof Team) || (isDead()))
		return;
		
	// give some energy, but less if moving horizontally,
	// and actually take some away if moving vertically
	float energyGain = ENERGY_STAND;
	if ((fCmdForwardMove != 0) || (fCmdSideMove != 0))
		energyGain = ENERGY_RUN;
	setEnergy(fEnergy + energyGain);

	// boost health if they're standing still
	if ((fCmdForwardMove == 0) 
	&& (fCmdSideMove == 0))
		{
		heal(HEALTH_STAND, false);
		}

	// called every 0.8 seconds, let's flash the carriers flag-icon...
	if (fCarryingTech)
		{
		fTick = (fTick + 1) & 7;
		if (fTick == 0)
			{
			if (fIconFlash = !fIconFlash)
				fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)0 );
			else
				fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)((Team)getTeam()).getTeamIcon());
			}
		}

	// Update the HUD Direction indicators
	Point3f target = fWidgetTargets[fCurrentWidget];
	if (target != null)
		{
		Point3f p = fEntity.getOrigin();
		
		Angle3f a = Q2Recycler.getAngle3f();
		a.set(p, target);
		fHUDDirection.setValue(a.y - fEntity.getPlayerViewAngles().y);

		Vector3f v = Q2Recycler.getVector3f();
		v.sub(p, target);
		fHUDRange.setValue(v.length());

		Q2Recycler.put(v);
		Q2Recycler.put(a);
		}
	}
/**
 * Called by the current weapon when it's done deactivating,
 * letting us know it's time to bring up the next weapon.
 */
public void changeWeapon() 
	{
	fLastWeapon = fWeapon;	
	fWeapon = fNextWeapon;						
	fNextWeapon = null;

	if (fWeapon != null)
		{
		fWeapon.activate();
		showVWep();	
		setAnimation(ANIMATE_VWEP_ACTIVATE);
		}
	}
/**
 * This method was created in VisualAge.
 */
public void clearSettings() 
	{
	// remove any quad-type powerup effects
	setDamageMultiplier(1.0F);
	
	// set various other bits to a normal setting
	fIsDead = false;
	fIsGibbed = false;
	fKiller = null;
	if (fInIntermission)
		{
		setScore(0);
		fInIntermission = false;
		}

	fWaterLevel = 0;
	fOldWaterLevel = 0;
	fOldButtons = 0;
	fLatchedButtons = 0;
	fViewHeight = 22;

	setHealth(100);
	setHealthMax(100);
	setAnimation(ANIMATE_NORMAL, true, 0);
	fOldVelocity = new Vector3f();
//-----------
	// give the player some energy to start with
	setEnergy(500);

	// set their widget composer to a known state
	fDestroyWarned = false;
	refreshHUDWidget();
	}
/**
 * Rotate through carrier items.
 * @param argv java.lang.String[]
 */
public void cmd_body(String[] argv, String args) 
	{
	setComponent(COMPONENT_BODY, getComponent(COMPONENT_BODY)+1);
	}
/**
 * Rotate through carrier items.
 * @param argv java.lang.String[]
 */
public void cmd_control(String[] argv, String args) 
	{
	setComponent(COMPONENT_CONTROL, getComponent(COMPONENT_CONTROL)+1);
	}
/**
 * Deploy or destroy a widget.
 * @param argv java.lang.String[]
 */
public void cmd_deploy(String[] argv, String args) 
	{
	Object o = getTeam();
	if (!(o instanceof Team))
		return;

	// if there's already a widget in the current slot,
	// send it the signal to destroy itself
	if (fWidgets[fCurrentWidget] != null)
		{
		if (fDestroyWarned)
			{
			fWidgets[fCurrentWidget].fireWidgetEvent(WidgetBody.TERMINATED);
			GameUtil.stuffCommand(fEntity, "play misc/spawn1.wav");
			}
		else
			{
			fDestroyWarned = true;
			GameUtil.stuffCommand(fEntity, "play misc/secret.wav");
			}			
		return;
		}
	fDestroyWarned = false;
		
	Team t = (Team) o;
		
	Technology bodyTech    = t.getTechnology(Technology.TYPE_BODY, fComponents[fCurrentWidget][COMPONENT_BODY]);	
	Technology controlTech = t.getTechnology(Technology.TYPE_CONTROL, fComponents[fCurrentWidget][COMPONENT_CONTROL]);
	Technology payloadTech = t.getTechnology(Technology.TYPE_PAYLOAD, fComponents[fCurrentWidget][COMPONENT_PAYLOAD]);

	float energyChange = getEnergy() - (bodyTech.getEnergyCost() + controlTech.getEnergyCost() + payloadTech.getEnergyCost());
	
	if (energyChange < 0)
		{
		GameUtil.stuffCommand(fEntity, "play misc/power2.wav");
		fEntity.cprint(Engine.PRINT_HIGH, "You need " + (int)(-energyChange) + " more energy units to build this\n");
		return;
		}
		
	try
		{
		WidgetBody body = (WidgetBody) bodyTech.getImplementationClass().newInstance();
		
		fWidgets[fCurrentWidget] = body;		
		body.setWidgetOwner(this);

		Class cls = controlTech.getImplementationClass();
		if (cls != null)
			{
			WidgetComponent comp = (WidgetComponent) cls.newInstance();			
			body.addWidgetComponent(comp);
			}

		cls = payloadTech.getImplementationClass();
		if (cls != null)
			{
			WidgetComponent comp = (WidgetComponent) cls.newInstance();			
			body.addWidgetComponent(comp);			
			}

		body.fireWidgetEvent(WidgetBody.DEPLOY);		
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_AUTO, Engine.getSoundIndex("items/respawn1.wav"), 1, NativeEntity.ATTN_STATIC, 0);
		refreshHUDWidget();
		
		bodyTech.incCounter();
		controlTech.incCounter();
		payloadTech.incCounter();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}

	setEnergy(energyChange);
	reconsiderHUDIndicators();	
	}
/**
 * Describe the current widget.
 * @param argv java.lang.String[]
 */
public void cmd_describe(String[] argv, String args) 
	{
	Team t = (Team) getTeam();
	
	Technology tech = t.getTechnology(Technology.TYPE_BODY, fComponents[fCurrentWidget][COMPONENT_BODY]);
	fEntity.cprint(Engine.PRINT_HIGH, "Body: " + tech.getName() + ": " + tech.getDescription() + "\n");

	tech = t.getTechnology(Technology.TYPE_CONTROL, fComponents[fCurrentWidget][COMPONENT_CONTROL]);
	fEntity.cprint(Engine.PRINT_HIGH, "Control: " + tech.getName() + ": " + tech.getDescription() + "\n");

	tech = t.getTechnology(Technology.TYPE_PAYLOAD, fComponents[fCurrentWidget][COMPONENT_PAYLOAD]);
	fEntity.cprint(Engine.PRINT_HIGH, "Payload: " + tech.getName() + ": " + tech.getDescription() + "\n");	
	}
/**
 * Drop an item in our inventory.
 */
public void cmd_drop(String[] argv, String args) 
	{
	if (argv.length < 2)		
		{
		fEntity.cprint(Engine.PRINT_HIGH, "Usage: drop <itemname>\n");
		return;
		}
		
	// build up the name of the item
	String itemName = argv[1];
	for (int i = 2; i < argv.length; i++)
		itemName = itemName + " " + argv[i];
	itemName = itemName.toLowerCase();

	// handle dropping cells
	if (itemName.equals("cells"))
		{
		float e = getEnergy();
		float t = Math.min(e, 50);

		if (t > 0)
			{
			setEnergy(e - t);
			ammo_cells ac = new ammo_cells();
			ac.setAmmoCount((int) t);
			ac.drop(this, GenericItem.DROP_TIMEOUT);
			}		
		}

	// wimp out
	fEntity.cprint(Engine.PRINT_MEDIUM, "Can't drop " + itemName + "\n");
	}
/**
 * Bring up the team menu.
 * Derived from CTFPlayer.cmd_inven(), that and menu code originated by Menno van Gangelen
 */
public void cmd_inven(String[] argv, String args)
	{
	// build the menu
	PlayerMenu menu = new PlayerMenu();
		
	// setup the layout of the menu;
	menu.setPrefix("xv 32 yv 8 picn inventory");
	menu.setBounds(50, 25, 222, 156);
		
	// Set the header of the menu
	menu.setHeader( MENU_HEADER );

	// Setup the body of the menu
	ResourceGroup rg = getResourceGroup();
	Object[] msgArgs = new Object[1];

	// Join Red Team
	msgArgs[0] = new Integer(Team.TEAM1.getNumPlayers());
	String[] item0  = { rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_join_red"),  
					    rg.format("barryp.widgetwar.WidgetMessages", "menu_playercount", msgArgs)};
	menu.addMenuItem( item0, "team red" );

	// Join Blue Team		
	msgArgs[0] = new Integer(Team.TEAM2.getNumPlayers());
	String[] item1  = { rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_join_blue"),  
					    rg.format("barryp.widgetwar.WidgetMessages", "menu_playercount", msgArgs)};
	menu.addMenuItem( item1, "team blue" );

	// Common string for Chasecam and Spectator menu items
	String leavesTeam = rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_leaves_team");		

	// Setup the footer of the menu
	msgArgs[0] = MENU_AUTHOR;
	String[] footer = { "",
						rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_footer_press"),
						rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_footer_cursor"),
						rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_footer_enter"),
						rg.getRandomString("barryp.widgetwar.WidgetMessages", "menu_footer_tab"),
						"",
						rg.format("barryp.widgetwar.WidgetMessages", "menu_footer_author", msgArgs)
					  };						  
	menu.setFooter( footer );

	// actually show it
	menu.show(this);
	}
/**
 * Rotate through carrier items.
 * @param argv java.lang.String[]
 */
public void cmd_payload(String[] argv, String args) 
	{
	setComponent(COMPONENT_PAYLOAD, getComponent(COMPONENT_PAYLOAD)+1);
	}
/**
 * Change team
 */
public void cmd_team(String[] argv, String args) 
	{
	Team newTeam;
	String teamName = null;
		
	if ( argv.length > 1 )
		teamName = argv[1].toLowerCase();

	if ( teamName.equals("red") )
		newTeam = Team.TEAM1;
	else if ( teamName.equals("blue") )
		newTeam = Team.TEAM2;
	else
		{
		fEntity.cprint(Engine.PRINT_HIGH, "Unknown team: " + teamName + "\n");
		return;
		}
		
	if ( newTeam == getTeam() )
		{
		fEntity.cprint(Engine.PRINT_HIGH, "You are already on the " + teamName + " team.\n");
		return;	// Can't change to same team
		}
		
	// pretend we disconnected - to drop weapons and techs and leave teams
	fPlayerStateSupport.fireEvent(this, PlayerStateEvent.STATE_INVALID, q2java.baseq2.BaseQ2.gWorld);
	destroyAllWidgets();
	
	// join new team
	newTeam.addPlayer(this);
	setTeam(newTeam);

	// drop everything and..
	fInventory.clear();

	// ... setup the player's weapons
	prepareWeapon(".spawn.weapon_shotgun", "shotgun");
	prepareWeapon(".spawn.weapon_supershotgun", "super shotgun");
	prepareWeapon(".spawn.weapon_machinegun", "machinegun");
	prepareWeapon(".spawn.weapon_chaingun", "chaingun");
	prepareWeapon(".spawn.weapon_grenadelauncher", "grenade launcher");
	prepareWeapon(".spawn.weapon_rocketlauncher", "rocket launcher");
	prepareWeapon(".spawn.weapon_hyperblaster", "hyperblaster");
/*	
	setComponent(COMPONENT_BODY, 0);
	setComponent(COMPONENT_CONTROL, 0);
	setComponent(COMPONENT_PAYLOAD, 0);
*/
	
	// respawn to new base and set score to zero...
	setScore(0);
	clearSettings();
	spawn();
	}
/**
 * Use an item.
 * @param argv java.lang.String[]
 */
public void cmd_use(String[] argv, String args) 
	{
	String itemName = argv[1];
	for (int i = 2; i < argv.length; i++)
		itemName = itemName + " " + argv[i];

	// make the "Blaster" into a null weapon..so when you
	// "use blaster" (press the 1 key for most people), you 
	// actually have no weapon at all
	if ((fWeapon != null) && itemName.equalsIgnoreCase("blaster"))
		{
		fNextWeapon = null;
		fWeapon.deactivate();
		return;
		}
		
	GameObject ent = (GameObject) fInventory.get(itemName.toLowerCase());
	if (ent == null)
		{
		Object[] msgargv = {itemName};
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("q2java.baseq2.Messages", "dont_have", msgargv) + "\n");
		return;
		}

	// handle weapons a little differently
	if (ent instanceof GenericWeapon)
		{
		if (ent == fWeapon)
			return; // do nothing if we're already using the weapon
		
		// make a note of what the next weapon will be	
		fNextWeapon = (GenericWeapon) ent;

		if (fWeapon != null)
			{
			// signal the current weapon to deactivate..when it's
			// done deactivating, it will signal back to the player to 
			// changeWeapon() and we'll use() the next weapon		
			fWeapon.deactivate();	
			setAnimation(ANIMATE_VWEP_DEACTIVATE);
			}
		else
			changeWeapon();
			
		return;
		}
	
	fInventory.getPack(itemName.toLowerCase()).fAmount--;
	ent.use(this);
	}
/**
 * Rotate through widget slots.
 * @param argv java.lang.String[]
 */
public void cmd_widget(String[] argv, String args) 
	{
	if (argv.length < 2)
		// cycle through slots
		fCurrentWidget = (++fCurrentWidget) % MAX_WIDGETS;
	else
		{
		int n = 0;
		try
			{
			n = Integer.parseInt(argv[1]);
			}
		catch (Exception e)
			{
			}
			
		if ((n < 1) || (n > MAX_WIDGETS))
			fEntity.cprint(Engine.PRINT_HIGH, "Error: " + argv[1] + " not in the range 1.." + MAX_WIDGETS + "\n");
		else
			fCurrentWidget = n - 1;			
		}
		
	fDestroyWarned = false;

	refreshHUDWidget();
	}
/**
 * Handle a new connection by just creating a new Player object 
 * and associating it with the player entity.
 * @param ent q2java.NativeEntity
 * @param playerInfo java.lang.String
 * @param loadgame boolean
 */
public static void connect(NativeEntity ent) throws GameException
	{
	new WidgetWarrior(ent);
	}
/**
 * Get rid of all this player's widgets.
 */
private void destroyAllWidgets() 
	{
	for (int i = 0; i < fWidgets.length; i++)
		{
		if (fWidgets[i] != null)
			fWidgets[i].fireWidgetEvent(WidgetBody.TERMINATED);
		}	
	}
/**
 * Override die() to also drop energy cells.
 */
protected void die(DamageEvent de)
	{
	// remember what we were using when we died
	fDeathWeapon = fWeapon;
	
	super.die(de);

	// drop energy cell
	float e = getEnergy();
	setEnergy(0);

	if (e > 0)
		{
		ammo_cells ac = new ammo_cells();
		ac.setAmmoCount((int) Math.min(e, MAX_ENERGY_DROP));
		ac.drop(this, GenericItem.DROP_TIMEOUT);
		}
	
	// drop DataCard
	if (GameUtil.randomFloat() < 0.05)
		{
		DataCard dc = new DataCard((Team)getTeam());
		dc.drop(this, GenericItem.DROP_TIMEOUT);
		}

	// make sure our corpse looks innocent
	setCarryingTech(false);
	}
/**
 * Disassociate the player from the game.
 */
public void dispose() 
	{
	destroyAllWidgets();
		
	super.dispose();
	}	
/**
 * Figure out what the ammo equivalent of our current energy charge is.
 * Overrides q2java.baseq2.Player.getAmmoCount() to make the baseq2
 * weapons work with this different ammo scheme.
 *
 * @return int
 * @param itemname java.lang.String
 */
public int getAmmoCount(String ammoName) 
	{
	float f = getAmmoEnergy(ammoName);

	if (f == 0)
		return 0;
	else
		return (int) (getEnergy() / f);
	}
/**
 * Figure out the energy equivalent of an ammo type.
 * @return float
 * @param ammoName java.lang.String
 */
public float getAmmoEnergy(String ammoName) 
	{
	if (ammoName.equals("rockets"))
		return 10;
	
	if (ammoName.equals("bullets"))
		return 0.5F;
		
	if (ammoName.equals("shells"))
		return 2F;
		
	if (ammoName.equals("grenades"))
		return 7F;

	if (ammoName.equals("cells"))
		return 1F;
		
	return 0;
	}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getComponent(int slot) 
	{
	return fComponents[fCurrentWidget][slot];
	}
/**
 * Get the widget currently selected by the player, may be null if the current
 * widget is inactive.
 *
 * @return barryp.widgetwar.WidgetBody
 */
public WidgetBody getCurrentWidget() 
	{
	return fWidgets[fCurrentWidget];
	}
/**
 * This method was created in VisualAge.
 * @return float
 * @param amount float
 */
public float getEnergy() 
	{	
	return fEnergy;
	}
/**
 * Get the HUD String this Player class uses.
 * @return java.lang.String
 */
public static String getHUD() 
	{
	return HUD;
	}
/**
 * This method was created in VisualAge.
 * @return float
 */
public Vector getStolenTechnologyList() 
	{
	return fStolenTechnologies;
	}
/**
 * Called when players enter and at beginning of each level.
 */
public void playerBegin() 
	{
	super.playerBegin();

	fEntity.setPlayerStat(NativeEntity.STAT_AMMO_ICON, (short) Engine.getImageIndex("a_cells"));
	}
/**
 * Called when the player has jumped.
 */
public void playerJumped() 
	{
	super.playerJumped();

	setEnergy(getEnergy() - ENERGY_JUMP);
	}
/**
 * All player entities get a chance to think.  When
 * a player entity thinks, it has to handle the 
 * users movement commands by calling pMove().
 * @param cmd commands from the client..indicate movement, jumping, weapon firing.
 */
public void playerThink(PlayerCmd cmd)
	{	
	if ((fEnergy < ENERGY_JUMP) && (cmd.fUpMove > 0))
		cmd.fUpMove = 0;
		
	super.playerThink(cmd);
	}
/**
 * Initialize the indexes of images used to display HUD status on the HUD.
 */
public static void precacheImages() 
	{
	gWidgetPics[0] = (short) Engine.getImageIndex("q2j_widget");;
	gWidgetPics[1] = (short) Engine.getImageIndex("q2j_widget_select");
	gWidgetPics[2] = (short) Engine.getImageIndex("q2j_widget_active");
	gWidgetPics[3] = (short) Engine.getImageIndex("q2j_widget_select_active");
	}
/**
 * Add a weapon to the players inventory, and alter it to suit this mod.
 * @param weaponName java.lang.String
 */
protected void prepareWeapon(String className, String weaponName) 
	{
	try
		{
		addWeapon(className, false);
	
		GenericWeapon gw = ((GenericWeapon) fInventory.get(weaponName));
		gw.setAutoSwitch(false);
		gw.setDroppable(false);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * Rethink what to do with the Direction and Range indicators.
 */
public void reconsiderHUDIndicators() 
	{
	boolean hudVisible = (fWidgets[fCurrentWidget] instanceof barryp.widgetwar.body.BodyHarness);
		
	fHUDDirection.setVisible(hudVisible);
	fHUDRange.setVisible(hudVisible);

	if ((hudVisible) && (fWidgetTargets[fCurrentWidget] == null))
		{
		fHUDDirection.setValue(Float.NaN);
		fHUDRange.setValue(Float.NaN);
		}		
	}
/**
 * Update the HUD to show the current widget.
 * @param n int
 */
public void refreshHUDWidget() 
	{
	// update the HUD to show the current widget
	setComponent(COMPONENT_BODY, fComponents[fCurrentWidget][COMPONENT_BODY]);
	setComponent(COMPONENT_CONTROL, fComponents[fCurrentWidget][COMPONENT_CONTROL]);
	setComponent(COMPONENT_PAYLOAD, fComponents[fCurrentWidget][COMPONENT_PAYLOAD]);

	for (int i = 0; i < MAX_WIDGETS; i++)
		{
		int pic = 0;
		if (i == fCurrentWidget)
			pic = 1;
		if (fWidgets[i] != null)
			pic += 2;

		fEntity.setPlayerStat(STAT_WIDGET_0 + i, gWidgetPics[pic]);		
		}
		
	reconsiderHUDIndicators();
	}
/**
 * Remove a stolen technology from the player's inventory.
 * @param st barryp.widgetwar.StolenTechnology
 */
public void removeStolenTechnology(StolenTechnology st) 
	{
	fStolenTechnologies.removeElement(st);

	// if we're not holding, then no sense in looking guilty
	if (fStolenTechnologies.size() == 0)
		setCarryingTech(false);
	}
/**
 * This method was created in VisualAge.
 */
public void respawn() 
	{
	super.respawn();
	
	// bring back whatever weapon we were using when died
	fWeapon = fDeathWeapon;

	if (fWeapon != null)
		fWeapon.activate();	
	}
/**
 * Set the current ammo count, or alter it by some amount.
 * @param amount int
 * @param isAbsolute is the amount an absolute value, or a relative one?
 */
public void setAmmoCount(int amount, boolean isAbsolute) 
	{
	setAmmoCount(fCurrentAmmoType, amount, isAbsolute);
	}
/**
 * Set the ammo count, or alter it by some amount.
 * @param ammoType name of a kind of ammo
 * @param amount int
 * @param isAbsolute is the amount an absolute value, or a relative one?
 */
public void setAmmoCount(String ammoType, int amount, boolean isAbsolute) 
	{
	// assume isAbsolute is false
	setEnergy(getEnergy() + (amount * getAmmoEnergy(ammoType)));
	}
/**
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 */
public void setAmmoType(String ammoType) 
	{
	fCurrentAmmoType = ammoType;
	}
/**
 * Turn the tech carrier visual effect off or on.
 * @param b boolean
 */
public void setCarryingTech(boolean b) 
	{
	fCarryingTech = b;
	if (fCarryingTech)
		fEntity.setEffects( fEntity.getEffects() | ((Team)getTeam()).getCarrierEffect() );				
	else
		{
		fEntity.setEffects( fEntity.getEffects() & ~((Team)getTeam()).getCarrierEffect() );
		fEntity.setPlayerStat( STAT_CTF_FLAG_PIC, (short)0 );
		}		
	}
/**
 * Change the index of a weapon component.
 * @param slot - which component is being changed: body, control or payload
 * @param n - which technology the component should be set to
 */
public void setComponent(int slot, int n) 
	{
	Object obj = getTeam();

	// bail if we haven't joined a team yet
	if (!(obj instanceof Team))
		return;
		
	Vector v = ((Team) obj).getTechnologyList(slot);

	// wrap if necessary
	if (n >= v.size())
		n = 0;

	// are we trying to change the value of a slot of an active widget?
	// if so then make a warning noise;
	if ((fWidgets[fCurrentWidget] != null)
	&&  (fComponents[fCurrentWidget][slot] != n))
		{
		GameUtil.stuffCommand(fEntity, "play weapons/noammo.wav");
		return;
		}
			
	fComponents[fCurrentWidget][slot] = n;
			
	Technology t = (Technology) v.elementAt(n);
	fEntity.setPlayerStat(STAT_WIDGET_BODY + slot, (short)(Engine.CS_ITEMS + t.getInventoryIndex()));
	}
/**
 * Set how much energy a player is carrying.
 * @param e int
 */
public void setEnergy(float e) 
	{
	fEnergy = Math.min(e, MAX_ENERGY);
	fEntity.setPlayerStat(NativeEntity.STAT_AMMO, (short) fEnergy);	
	}
/**
 * Set what point the HUD direction and range indicators should point at.
 * May be null for "no target"
 * @param p javax.vecmath.Point3f
 */
public void setHUDTarget(WidgetBody wb, Point3f p) 
	{
	for (int i = 0; i < MAX_WIDGETS; i++)
		{
		if (fWidgets[i] == wb)
			{
			fWidgetTargets[i] = p;
			if (i == fCurrentWidget)
				reconsiderHUDIndicators();
			break;
			}
		}		
	}
/**
 * spawn the player into the game.
 */
protected void spawn() 
	{
	super.spawn();

	if (getTeam() instanceof Team)
		{
		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
		fEntity.setPlayerPMType( NativeEntity.PM_NORMAL );
		fEntity.linkEntity();
		}
	else	
		{
		// set as spectator
		fEntity.setSolid( NativeEntity.SOLID_NOT );
		fEntity.setPlayerPMType( NativeEntity.PM_FREEZE );	
		fEntity.setClipmask( Engine.MASK_PLAYERSOLID );	
		fEntity.setSkinNum( 0 );
		fEntity.setModelIndex( 0 );
		fEntity.setModelIndex2( 0 );
		fEntity.setPlayerGunIndex(0);	
		fWeapon = null;
		fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
		fEntity.linkEntity();

		// show the menu
		cmd_inven(null, null );
		}
	}
/**
 * Called when the player touches his home base.
 */
public void touchBase() 
	{
	// do nothing if we're not carrying any stolen tech
	if (fStolenTechnologies.size() == 0)
		return;

	// transfer what we're carrying to the team
	Team t = (Team) getTeam();
	Enumeration enum = fStolenTechnologies.elements();
	while (enum.hasMoreElements())
		{
		StolenTechnology st = (StolenTechnology) enum.nextElement();
		t.addStolenTechnology(st, this);
		st.release(this);
		}

	// clear our list
	fStolenTechnologies.removeAllElements();

	// look innocent
	setCarryingTech(false);
	}
/**
 * Welcome the player to the game.
 */
public void welcome() 
	{
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGIN);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	Object[] args = {getName()};
	Game.localecast("q2java.baseq2.Messages", "entered", args, Engine.PRINT_HIGH);
	}
/**
 * Called to let the player know a widget is going away.
 * @param b barryp.widgetwar.WidgetBody
 */
public void widgetDisposed(WidgetBody b) 
	{
	for (int i = 0; i < fWidgets.length; i++)
		{
		if (fWidgets[i] == b)
			{
			fWidgets[i] = null;
			fWidgetTargets[i] = null;
			
			refreshHUDWidget();				
			return;
			}
		}
	}
}