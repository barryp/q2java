package q2java.baseq2;

import java.beans.PropertyVetoException;
import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

import q2java.baseq2.event.*;
import q2java.baseq2.rule.*;
import q2java.baseq2.spawn.*;

/**
 * Player objects are subclassed from GameObject, but also
 * implement the methods necessary for PlayerListener.  
 *
 * @author Barry Pederson
 */

public class Player extends GameObject 
implements ServerFrameListener, PlayerListener, PrintListener, 
	CrossLevel, SwitchablePlayer, GameStatusListener
	{	
	protected transient ResourceGroup fResourceGroup;

	// "Team" player belongs to..null for plain DM
	protected transient Object fTeam;
	
	private int fScore;
	protected float fStartTime;

	private float fHealth;
	private float fHealthMax;
	protected boolean fIsFemale;
	protected boolean fInIntermission;
	
	private float fDamageMultiplier;
		
	protected Hashtable fPlayerInfo;
	protected InventoryTracker fInventory;
	protected Vector fWeaponOrder;
	protected Vector fWeaponsExcluded;

	// CHANGES for Delegation Model
	// Peter Donald
	protected PlayerMoveSupport fPlayerMoveSupport;
	protected PlayerCvarSupport fPlayerCvarSupport;
	protected PlayerCommandSupport fPlayerCommandSupport;
	protected PlayerStateSupport fPlayerStateSupport;
	protected PlayerInfoSupport fPlayerInfoSupport;
	protected InventorySupport fInventorySupport;
	protected DamageSupport fPreArmorDamageSupport;
	protected DamageSupport fArmorDamageSupport;
	protected DamageSupport fPostArmorDamageSupport;
	//END Declarations for Delegation Model

	// CHANGES for static Listeners
	// Peter Donald
	protected static PlayerMoveSupport gPlayerMoveSupport = new PlayerMoveSupport();
	protected static PlayerCommandSupport gPlayerCommandSupport = new PlayerCommandSupport();
	protected static PlayerStateSupport gPlayerStateSupport = new PlayerStateSupport();
	protected static PlayerInfoSupport gPlayerInfoSupport = new PlayerInfoSupport();
	protected static InventorySupport gInventorySupport = new InventorySupport();
	protected static DamageSupport gPreArmorDamageSupport = new DamageSupport();
	protected static DamageSupport gArmorDamageSupport = new DamageSupport();
	protected static DamageSupport gPostArmorDamageSupport = new DamageSupport();
	//END Declarations for static listeners

	protected ArmorDamageFilter fArmor;
	
	public final static int DAMAGE_FILTER_PHASE_PREARMOR = 0;
	public final static int DAMAGE_FILTER_PHASE_ARMOR = 1;
	public final static int DAMAGE_FILTER_PHASE_POSTARMOR = 2;

	// have we given the current weapon a chance to think yet this frame? 
	protected boolean fWeaponThunk;
	protected GenericWeapon fWeapon;
	protected GenericWeapon fLastWeapon;
	protected GenericWeapon fNextWeapon;	
	protected InventoryPack fAmmo;
	
	protected int fHand;
	protected float fBobTime;
	protected float fBobFracSin;
	protected int fBobCycle;
	protected float fBobMove;

	// Saved input to and output from the PMove function
	protected short fCmdForwardMove;
	protected short fCmdUpMove;
	protected short fCmdSideMove;
	public int fButtons; 
	public int fLatchedButtons;	
	protected int fOldButtons;
	protected Angle3f fCmdAngles;	
	public float fViewHeight;
	protected int fWaterType;
	protected int fWaterLevel;
	protected int fOldWaterLevel;
	protected Vector3f fOldVelocity;
	
	// The mass of this Player. Use setMass(int) to set.
	private int fMass;
	
	protected boolean fShowInventory;
	protected boolean fShowScore;
	protected float fShowScoreTime;
	protected float fPickupMsgTime;
	
	// drowning and breathing
	protected float fAirFinished;
	protected float fNextDrownTime;	
	protected int fDrownDamage;
	
	protected boolean fIsDead;
	protected boolean fIsGibbed;
	protected transient GameObject fKiller;
	protected float fKillerYaw;
	protected float fRespawnTime;
		
	// animation variables
	protected int fAnimationFrame;
	protected int fAnimationPriority;
	protected int fAnimationEnd;
	protected boolean fAnimationReversed;
	protected boolean fIsRunning;
	protected boolean fIsDucking;
	
	public Angle3f fKickAngles;
	public Point3f fKickOrigin;
	
	// Color/blend/"kick" related variables (TSW)
	private Color4f fBlend;			// The blend (player view tint) for the current server frame.
	private Color4f fDamageBlend;	// The blend for the damage in the current frame.
	private float fFrameAlpha;		// The alpha level for the current server frame (usually bonus flashes).
	private int fDamageArmor;		// regular armor (hits)
	private int fDamageBlood;		// blood (regular damage hits)
	private int fDamagePArmor;		// power armor (hits)
	private int fDamageKnockback;	// How much "kick" should be visible this frame
	private Point3f fDamageFrom;	// id: "origin for vector calculation"
	private float fDamageRoll;		// how much roll inflicted by damage
	private float fDamagePitch;		// how much pitch inflicted by damage
	private float fDamageTime;		// for fading out of damage kicks (?) 
	
	// Debounce variables to make sure sounds don't collide (?)
	// This seems like a hack, but I can't think of a way to improve it. (TSW)
	private float fPainDebounceTime;
	
	// temp vectors for endFrame()
	protected Vector3f fViewOffset;
	protected Vector3f fRight;
	protected Vector3f fForward;
	protected Vector3f fUp;
	protected float fXYSpeed;

	// ------ CVar stuff shared by all players ---------
	private static float gNextCVarMirror;
	private final static int MIRROR_INTERVAL = 10;
	
	// various CVars
	private static CVar gRunRollCVar = new CVar("run_roll", "0.005", 0);	
	private static CVar gRunPitchCVar = new CVar("run_pitch", "0.002", 0);	
	private static CVar gBobUpCVar = new CVar("bob_up", "0.005", 0);	
	private static CVar gBobRollCVar = new CVar("bob_roll", "0.002", 0);	
	private static CVar gBobPitchCVar = new CVar("bob_pitch", "0.002", 0);	
	private static CVar gRollAngleCVar = new CVar("sv_rollangle", "2", 0);
	private static CVar gRollSpeedCVar = new CVar("sv_rollspeed", "200", 0);
	
	// mirrored copies of CVar values
	public static float gRunRoll;
	public static float gRunPitch;	
	public static float gBobUp;
	public static float gBobRoll;	
	public static float gBobPitch;	
	public static float gRollAngle;
	public static float gRollSpeed;

	// keep some bodies lying around		
	protected static CorpseQueue gCorpseQueue = new CorpseQueue();
	
	// ------- Public constants ---------------------------------	

	public final static int GIB_HEALTH_THRESHOLD = -40; //player turns to chunks when health drops below this
	public final static int PRINT_CHANNELS = PrintEvent.PRINT_ANNOUNCE+PrintEvent.PRINT_TALK+PrintEvent.PRINT_TALK_TEAM+PrintEvent.PRINT_TALK_PRIVATE;
	
	public final static Color4f COLOR_LAVA  = new Color4f(1.0f, 0.3f, 0.0f, 0.6f);
	public final static Color4f COLOR_SLIME = new Color4f(0.0f, 0.1f, 0.05f, 0.6f);
	public final static Color4f COLOR_WATER = new Color4f(0.5f, 0.3f, 0.2f, 0.4f);
	// These colors refer to fFrameAlpha for their alpha, so they only have 3 floats (rgb). 
	// The color used for Power Armor blends. 
	public final static Color3f COLOR_GREEN = new Color3f(0.0f, 1.0f, 0.0f);
	// The color used for regular Armor blends. 
	public final static Color3f COLOR_WHITE = new Color3f(1.0f, 1.0f, 1.0f);
	// The color used for blood blends. 
	public final static Color3f COLOR_RED = new Color3f(1.0f, 0.0f, 0.0f);
	
	// added from id code (g_local.h) for view kicks.
	public final static float DAMAGE_TIME = 0.5f;
	
	// Constants for setAnimation()

	// basic animations
	public final static int ANIMATE_NORMAL	= 0; // normal = stand, normal+1 = run
	public final static int ANIMATE_ATTACK	= 2;
	public final static int ANIMATE_PAIN	= 3;
	public final static int ANIMATE_DEATH	= 6;
	public final static int ANIMATE_VWEP_THROW	= 9; 
	public final static int ANIMATE_VWEP_ACTIVATE		= 10;
	public final static int ANIMATE_VWEP_DEACTIVATE	= 11;
		
	// gesture animations
	public final static int ANIMATE_FLIPOFF	= 24;
	public final static int ANIMATE_SALUTE	= 25;
	public final static int ANIMATE_TAUNT	= 26;
	public final static int ANIMATE_WAVE	= 27;
	public final static int ANIMATE_POINT	= 28;

	// jumping animations
	public final static int ANIMATE_JUMP	= 29;
	public final static int ANIMATE_FLAIL	= 30;
	public final static int ANIMATE_LAND	= 31;
	

	
	// handedness values
	public final static int RIGHT_HANDED	= 0;
	public final static int LEFT_HANDED		= 1;
	public final static int CENTER_HANDED	= 2;
		
	public final static String DM_STATUSBAR = 
		"yb	-24 " +

		// health
		"xv	0 " +
		"hnum " +
		"xv	50 " +
		"pic 0 " +

		// ammo
		"if 2 " +
		"	xv	100 " +
		"	anum " +
		"	xv	150 " +
		"	pic 2 " +
		"endif " +

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

		"yb	-50 " +

		// picked up item
		"if 7 " +
		"	xv	0 " +
		"	pic 7 " +
		"	xv	26 " +
		"	yb	-42 " +
		"	stat_string 8 " +
		"	yb	-50 " +
		"endif " +

		// timer
		"if 9 " +
		"	xv	246 " +
		"	num	2	10 " +
		"	xv	296 " +
		"	pic	9 " +
		"endif " +

		//  help / weapon icon 
		"if 11 " +
		"	xv	148 " +
		"	pic	11 " +
		"endif " +

		//  frags
		"xr	-50 " +
		"yt 2 " +
		"num 3 14" 
		;		


	// ---------------- protected constants ------------------
		
	// animation priority
	protected final static int ANIM_BASIC		= 0; // stand / run
	protected final static int ANIM_WAVE		= 1;
	protected final static int ANIM_JUMP		= 2;
	protected final static int ANIM_PAIN		= 3;
	protected final static int ANIM_ATTACK		= 4;
	protected final static int ANIM_DEATH		= 5;			
	
	// animation table
	// priority, start-frame, endframe, ..........
	protected final static int[] fAnims = 
		{		
		// standing animations
		ANIM_BASIC, 0, 39,		// stand
		ANIM_BASIC, 40, 45	,	// run
		ANIM_ATTACK, 46, 53,	// attack
		ANIM_PAIN, 54, 57,		// pain1
		ANIM_PAIN, 58, 61,		// pain2
		ANIM_PAIN, 62, 65,		// pain3 
		ANIM_DEATH, 178, 183,	// death1
		ANIM_DEATH, 184, 189,	// death2
		ANIM_DEATH, 190, 197,	// death3
		ANIM_ATTACK, 119, 112, // VWeap grenade toss
		ANIM_PAIN, 62, 65,		// VWeap activate
		ANIM_PAIN, 65, 62,		// VWeap deactivate
		
		// croutching animations
		ANIM_BASIC, 135, 153,	// crstnd
		ANIM_BASIC, 154, 159,	// crwalk
		ANIM_ATTACK, 160, 168,	// crattack
		ANIM_PAIN, 169, 172,	// crpain
		ANIM_PAIN, 169, 172,	// crpain
		ANIM_PAIN, 169, 172,	// crpain
		ANIM_DEATH, 173, 177,	// crdeath
		ANIM_DEATH, 173, 177,	// crdeath
		ANIM_DEATH, 173, 177,	// crdeath
		ANIM_ATTACK, 160, 162,	// VWeap grenade toss
		ANIM_PAIN, 169, 172,	// VWeap activate
		ANIM_PAIN, 172, 169,	// VWeap deactivate
		
		// gesture animations
		ANIM_WAVE, 72, 83,		// flip
		ANIM_WAVE, 84, 94,		// salute
		ANIM_WAVE, 95, 111,	// taunt
		ANIM_WAVE, 112, 122,	// wave
		ANIM_WAVE, 123, 134,	// point

		// jumping animations
		ANIM_JUMP, 66, 71,		// jump
		ANIM_JUMP, 66, 67,		// flail around in the air
		ANIM_WAVE, 68, 71		// land on the ground		
		};
/**
 * Create a new Player Game object, and associate it with a Player
 * native entity.
 */
public Player(NativeEntity ent) throws GameException
	{
	Engine.debugLog("q2java.baseq2.Player.<ctor>(" + ent + ")");

	fEntity = ent;
	fEntity.setPlayerListener(this);
	fEntity.setReference(this);

	// make sure the blaster VWep skin is cached before we set the player's skin
	// kind of lame, but if we don't do this, the first player connecting to a 
	// server won't see the blaster VWep properly. Other VWep weapons look ok because
	// they're spawned with the map - it's the blaster that get's its VWep cache
	// entry set very late. (Grappling hook will have the same problem, so CTF needs
	// to do the same thing). 
	GenericWeapon.precacheVWep(".spawn.weapon_blaster");

	// sign up to receive server frame notices at the beginning and end of server frames
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END, 0, 0);

	// get a shortcut to quickly access ResourceBundles for our particular locale
	fResourceGroup = Game.getResourceGroup(Locale.getDefault());
	
	// sign up to receive broadcast messages using the default locale
	Game.getPrintSupport().addPrintListener(this, PRINT_CHANNELS, false);

	// sign up to be called when the game status changes
	// (mostly to watch for PAUSE, so we know when to go into intermission)
	Game.addGameStatusListener(this);
	
	//CHANGES for delegation model
	fPlayerMoveSupport = new PlayerMoveSupport();
	fPlayerCvarSupport = new PlayerCvarSupport();
	fPlayerCommandSupport = new PlayerCommandSupport();
	fPlayerStateSupport = new PlayerStateSupport();
	fPlayerInfoSupport = new PlayerInfoSupport();
	fInventorySupport = new InventorySupport();
	fPreArmorDamageSupport = new DamageSupport();
	fArmorDamageSupport = new DamageSupport();
	fPostArmorDamageSupport = new DamageSupport();
	//END changes for delegation Model

	fPlayerInfo = new Hashtable();
	playerInfoChanged(fEntity.getPlayerInfo());
	
	// create temporary vectors
	fViewOffset = new Vector3f();
	fRight = new Vector3f();
	fForward = new Vector3f();
	fUp = new Vector3f();
	fOldVelocity = new Vector3f();
		
	// Set default values
	fKickAngles = new Angle3f();
	fKickOrigin = new Point3f();
	fDamageFrom = new Point3f();
	fBlend = new Color4f(0.0f, 0.0f, 0.0f, 0.0f);
	fDamageBlend = new Color4f(0.0f, 0.0f, 0.0f, 0.0f);
	setFrameAlpha(0.0f);
	setMass(200);
	
	fInventory = new InventoryTracker();
	
	// Create weapon ordering vectors
	fWeaponOrder = new Vector();
	fWeaponsExcluded = new Vector();

	// create the basic Player Armor
	fArmor = new ArmorDamageFilter(this);
	addDamageListener(fArmor, DAMAGE_FILTER_PHASE_ARMOR);
	
	// Setup default weapon ordering
	fWeaponOrder.addElement("blaster");
	fWeaponOrder.addElement("shotgun");
	fWeaponOrder.addElement("super shotgun");
	fWeaponOrder.addElement("machinegun");
	fWeaponOrder.addElement("chaingun");
	fWeaponOrder.addElement("grenades");
	fWeaponOrder.addElement("grenade launcher");
	fWeaponOrder.addElement("rocket launcher");
	fWeaponOrder.addElement("hyperblaster");
	fWeaponOrder.addElement("railgun");
	fWeaponOrder.addElement("bfg10k");
	
	// Do we need to init values of zero? (TSW)
	fPainDebounceTime = 0.0f;
	fDamageRoll = 0.0f;
	fDamagePitch = 0.0f;
	fDamageTime = 0.0f;
	}
public static void addAllDamageListener(DamageListener l)
	{
	addAllDamageListener(l,DAMAGE_FILTER_PHASE_PREARMOR);
	}
/**
 * Add an object that wants to filter damage the player takes.
 * @param DamageFilter - The object to add as a damage filter
 * @param int - Phase at which it wants to filter.
 */
public static void addAllDamageListener(DamageListener l, int phase)
	{
	if (l == null)
		return;
	
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		gPreArmorDamageSupport.addDamageListener(l);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		gArmorDamageSupport.addDamageListener(l);
		}
	else if (phase == DAMAGE_FILTER_PHASE_POSTARMOR)
	        {
		gPostArmorDamageSupport.addDamageListener(l);
		}
	
	return;
	}
public static void addAllPlayerCommandListener(PlayerCommandListener l)
	{
	gPlayerCommandSupport.addPlayerCommandListener(l);
	}
public static void addAllPlayerInfoListener(PlayerInfoListener l)
	{
	gPlayerInfoSupport.addPlayerInfoListener(l);
	}
public static void addAllPlayerInventoryListener(InventoryListener l)
	   {
	   gInventorySupport.addInventoryListener(l);
	   }
public static void addAllPlayerMoveListener(PlayerMoveListener l)
	{
	gPlayerMoveSupport.addPlayerMoveListener(l);
	}
public static void addAllPlayerStateListener(PlayerStateListener l)
	{
	gPlayerStateSupport.addPlayerStateListener(l);
	}
/**
 * Add ammo to the player's inventory.
 * @param ah something that holds ammo, either a weapon or an ammobox.
 * @return boolean true if some or all of the ammo was taken.
 */
protected boolean addAmmo(AmmoHolder ah) 
	{
	String ammoType = ah.getAmmoName();
	if (ammoType == null)
		return false;

	int count = ah.getAmmoCount();
	
	InventoryPack pack = (InventoryPack) fInventory.getPack(ammoType);
	if (pack == null)
		{
		// create a new inventory pack and add it to the inventory
		pack = new InventoryPack();
		pack.fMaxAmount = Integer.MAX_VALUE;
		fInventory.addPack(ammoType, pack);
		}
		
	// make sure we don't overfill the ammo pack, figure
	// how how much more we can take
	count = Math.min(count, pack.fMaxAmount - pack.fAmount);

	// don't do anything if the player is already maxed out on this ammo
	if (count < 1)
		return false;
		
	if (pack == fAmmo)
		setAmmoCount(count, false);  // will also update HUD
	else			
		pack.fAmount += count;

	// clear out the object's ammo count in case it was a weapon
	ah.setAmmoCount(0); 

	// make sure we have the class this ammo belongs to
	// (in case the InventoryPack was initialized without it)
	if (pack.fItem == null)
		pack.fItem = ah.getAmmoBoxClass();
		
	return true;		
	}
/**
 * This method adds a color to fBlend, via RGBA (given as
 * xyzw to match the Color4f class).
 *
 * @param x		Red
 * @param y		Green
 * @param z		Blue
 * @param w		Alpha
 * @see			#addBlend(java.vecmath.Color4f color)
 */
public void addBlend(float x, float y, float z, float w)
	{
	float	w2, w3;

	if (w <= 0)	// When alpha is zero, blend is invisible.
		return;

	w2 = fBlend.w + (1 - fBlend.w) * w;	// new total alpha
	w3 = fBlend.w / w2;					// fraction of color from old

	fBlend.x = (fBlend.x * w3) + (x * (1 - w3));
	fBlend.y = (fBlend.y * w3) + (y * (1 - w3));
	fBlend.z = (fBlend.z * w3) + (z * (1 - w3));
	fBlend.w = w2;
	}
/**
 * This method adds the <SAMP>color</SAMP> to fBlend.
 *
 * @param color	The color to add to fBlend.
 */
public void addBlend(Color4f color)
	{
	addBlend(color.x, color.y, color.z, color.w);
	}
public void addDamageListener(DamageListener dl)
	{
	addDamageListener(dl, DAMAGE_FILTER_PHASE_PREARMOR);
	}
/**
 * Add an object that wants to filter damage the player takes.
 * @param DamageFilter - The object to add as a damage filter
 * @param int - Phase at which it wants to filter.
 */
public void addDamageListener(DamageListener dl, int phase)
	{
	if (dl == null)
		return;
	
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		fPreArmorDamageSupport.addDamageListener(dl);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		fArmorDamageSupport.addDamageListener(dl);
		}
	else if (phase == DAMAGE_FILTER_PHASE_POSTARMOR)
	    {
		fPostArmorDamageSupport.addDamageListener(dl);
		}
	
	return;
	}
/**
 * Add an item to the player's inventory.
 * @param item what we're trying to add.
 * @return boolean true if the item was taken.
 */
public boolean addItem(GenericItem item) 
	{
	if (item == null)
		return false;

	try 
		{ 
		fInventorySupport.fireEvent(this,item,true); 
		gInventorySupport.fireEvent(this,item,true); 
		}
	catch(PropertyVetoException pve)
		{
	    String s = pve.getMessage();
	    
	    if( s != null )
	    	{
			fEntity.centerprint(s);
	    	}

	    return false;
		}

	if (item instanceof GenericWeapon)
		return addWeapon((GenericWeapon)item, true);

	if (item instanceof GenericAmmo)
		return addAmmo((GenericAmmo)item);

	if (item instanceof GenericHealth)
		{
		GenericHealth health = (GenericHealth) item;
		return heal(health.getHealthValue(), health.isOverridingMax());
		}

	if (item instanceof GenericArmor)
		return fArmor.addArmor((GenericArmor)item);
		
	// assume item knows what it's doing.
	return true;
	}
public void addPlayerCommandListener(PlayerCommandListener l)
	{
	fPlayerCommandSupport.addPlayerCommandListener(l);
	}
public void addPlayerCvarListener(PlayerCvarListener l, String cvar)
	{
	fPlayerCvarSupport.addPlayerCvarListener(this,l,cvar);
	}
public void addPlayerInfoListener(PlayerInfoListener l)
	{
	fPlayerInfoSupport.addPlayerInfoListener(l);
	}
public void addPlayerInventoryListener(InventoryListener l)
	   {
	   fInventorySupport.addInventoryListener(l);
	   }
public void addPlayerMoveListener(PlayerMoveListener l)
	{
	fPlayerMoveSupport.addPlayerMoveListener(l);
	}
public void addPlayerStateListener(PlayerStateListener l)
	{
	fPlayerStateSupport.addPlayerStateListener(l);
	}
/**
 * Add a class of weapon to a player's inventory.
 * @param weaponClassSuffix class of the weapon, either a whole classname or a suffix.
 * @param allowSwitch Have the player switch weapons if they're currently using just a blaster.
 * @return boolean true if the player took the weapon (or its ammo)
 */
public boolean addWeapon(String weaponClassSuffix, boolean allowSwitch) 
	{
	try
		{
		Class cls = Game.lookupClass(weaponClassSuffix);
		GenericWeapon w = (GenericWeapon) cls.newInstance();		
		return addWeapon(w, allowSwitch);
		}
	catch (Exception e)
		{
		e.printStackTrace();		
		return false;
		}		
	}
/**
 * Add an actual instance of a weapon to a player's inventory.
 * @param weapon baseq2.GenericWeapon
 * @param allowSwitch boolean
 */
public boolean addWeapon(GenericWeapon w, boolean allowSwitch) 
	{
	boolean weaponStay = BaseQ2.isDMFlagSet(BaseQ2.DF_WEAPONS_STAY);

	InventoryPack p = fInventory.getPack(w.getItemName());

	// check if we already have one of these weapons
	if ((p != null) && (p.fAmount > 0))
		{
		if (weaponStay)
			return false; // don't pick it up
		else
			{
			addAmmo(w); // take the ammo
			if (w.getAmmoBoxClass() != w.getClass())
				p.fAmount++; // inc weapon count if it's not its own ammo
			return true; // we picked it up 
			}
		}

	// must be a new weapon at this point		

	// transfer the ammo inside the weapon to the player's inventory
	addAmmo(w);	
	
	// add the weapon to the inventory
	putInventory(w.getItemName(), w);
	
	// make a note of who owns this weapon
	w.setOwner(this);
	
	// add the weapon to the weapon order
	if (!fWeaponOrder.contains(w.getItemName().toLowerCase()))
		fWeaponOrder.addElement(w.getItemName().toLowerCase());

	// switch weapons if we dont have a weapon or are currently using the blaster
	if ((fWeapon == null) || (allowSwitch && (fWeapon == fInventory.get("blaster"))))
		{
		if (fWeapon == null)
			{
			fWeapon = w;
			fWeapon.activate();
			}
		else
			{
			fNextWeapon = w;
			fWeapon.deactivate();
			}
		}
				
	return !weaponStay;
	}
/**
 * Handle the beginning of a server frame.
 */
protected void beginServerFrame()
	{
	float time = Game.getGameTime();

	// mirror various CVars that the player class
	// needs from time to time
	if (time > gNextCVarMirror)
		{
		gRunRoll	= gRunRollCVar.getFloat();
		gRunPitch	= gRunPitchCVar.getFloat();	
		gBobUp		= gBobUpCVar.getFloat();
		gBobRoll	= gBobRollCVar.getFloat();	
		gBobPitch	= gBobPitchCVar.getFloat();	
		gRollAngle	= gRollAngleCVar.getFloat();
		gRollSpeed	= gRollSpeedCVar.getFloat();
		
		gNextCVarMirror = time + MIRROR_INTERVAL;
		}
		
	// reset the color blend for this frame.
	resetBlend();
	
	if (fInIntermission)
		return;
		
	if (fIsDead && (time > fRespawnTime) 
	&& ((fLatchedButtons != 0) || BaseQ2.isDMFlagSet(BaseQ2.DF_FORCE_RESPAWN)))
		{
		respawn();
		fLatchedButtons = 0;
		return;
		}
			
	if (fWeaponThunk)
		fWeaponThunk = false;
	else		
		{
		if (fWeapon != null)
			fWeapon.weaponThink();
		}			
				
	fLatchedButtons = 0;		
	}
/**
 * Update the player's breathing state - normally called automatically
 *  by worldEffects() when above water.
 * @param duration - how many seconds this breath will last
 * @param resetDrownDamage - whether drowning damage is set back to initial levels.
 */
public void breath(float duration, boolean resetDrownDamage)
	{
	fAirFinished = Game.getGameTime() + duration;
	if (resetDrownDamage)
		fDrownDamage = 2;
	}
/**
 * Announce this player's death to the world.
 * @param de the DamageEvent that caused the death
 */
public void broadcastObituary(DamageEvent de) 
	{
	GameObject attacker = de.getAttacker();
	String obitKey = de.getObitKey();
	
	Object[] args = {getName(), new Integer(isFemale() ? 1 : 0), (attacker instanceof Player ? ((Player)attacker).getName() : null)};
	if (attacker == this)
		obitKey = "self_" + obitKey;
		
	if (Game.isResourceAvailable("q2java.baseq2.Messages", obitKey))		
		Game.localecast("q2java.baseq2.Messages", obitKey, args, Engine.PRINT_MEDIUM);	
	else
		{
		// must be some new kind of obitKey, use a default death message
		if (attacker == this)
			Game.localecast("q2java.baseq2.Messages", "self_died", args, Engine.PRINT_MEDIUM);	
		else
			Game.localecast("q2java.baseq2.Messages", "died", args, Engine.PRINT_MEDIUM);				
		}	
	}
/**
 * This method calculates the "blend" that should affect this
 * Player's view (e.g. when you go in lava, a reddish "blend"
 * is applied). This is normally called once per frame, during
 * endServerFrame().
 */
protected void calcBlend()
	{
	int			contents;
	Point3f		vieworg;
	int			remaining;
	float		frameAlpha = getFrameAlpha();
	Color4f		damageBlend = getDamageBlend();

	// add vectors to locate what contents the Player is in: air, water, slime, solid or lava.
	vieworg = fEntity.getOrigin();
	vieworg.add(fViewOffset);
	contents = Engine.getPointContents(vieworg);
	
	if ((contents & (Engine.CONTENTS_LAVA|Engine.CONTENTS_SLIME|Engine.CONTENTS_WATER)) != 0)
		fEntity.setPlayerRDFlags(fEntity.getPlayerRDFlags() | NativeEntity.RDF_UNDERWATER);
	else
		fEntity.setPlayerRDFlags(fEntity.getPlayerRDFlags() & ~NativeEntity.RDF_UNDERWATER);

	if ((contents & (Engine.CONTENTS_SOLID|Engine.CONTENTS_LAVA)) != 0)
		addBlend(COLOR_LAVA);
	else if ((contents & Engine.CONTENTS_SLIME) != 0)
		addBlend(COLOR_SLIME);
	else if ((contents & Engine.CONTENTS_WATER) != 0)
		addBlend(COLOR_WATER);

	// add for damage if alpha level is present
	if (damageBlend.w > 0)
		addBlend(damageBlend);

	if (frameAlpha > 0)
		addBlend(0.85f, 0.7f, 0.3f, frameAlpha);

	// drop the damage alpha value a notch (for fade effect)
	damageBlend.w -= 0.06;
	if (damageBlend.w < 0)
		damageBlend.w = 0;

	// drop the frame alpha value a notch (for fade effect)
	frameAlpha -= 0.1;
	if (frameAlpha < 0)
		frameAlpha = 0;
	
	setFrameAlpha(frameAlpha);
	fEntity.setPlayerBlend(fBlend);
	}
protected void calcBob()
	{
	// setup bob calculations (we save off quite a few of the values for the other functions.
	Vector3f velocity = fEntity.getVelocity();
	fXYSpeed = (float)Math.sqrt((velocity.x*velocity.x) + (velocity.y*velocity.y));
	
	if (fXYSpeed < 5.0)
		{
		fBobMove = 0;
		fBobTime = 0;
		}
	else if (fEntity.getGroundEntity() != null)
		{
		if (fXYSpeed > 210)
			fBobMove = 0.25F;
		else if (fXYSpeed > 100)
			fBobMove = 0.125F;
		else
			fBobMove = 0.0625F;		
		}		
		
	fBobTime += fBobMove;

	float bobtime = fBobTime;

	if (fIsDucking)
		bobtime *= 4;

	fBobCycle = (int) bobtime;
	fBobFracSin = (float) Math.abs(Math.sin(bobtime*Math.PI));			
	}
protected void calcClientEvent()
	{
	if (fEntity.getEvent() != 0)
		return;
	
	if ((fEntity.getGroundEntity() != null) && (fXYSpeed > 225))
		if ((int)(fBobTime + fBobMove) != fBobCycle)
			Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_FOOTSTEP);
	}
/**
 * This method was created by a SmartGuide.
 */
protected void calcClientFrame() 
	{
/*	
	if (ent->s.modelindex != 255)
		return;		// not in the player model
*/
	boolean isDucking = ((fEntity.getPlayerPMFlags() & NativeEntity.PMF_DUCKED) != 0);
	boolean isRunning = (fXYSpeed != 0);

	// check for stand/duck and stop/go transitions
	//
	// this is a nasty "if" statement, but basically: if we're not making
	// a transition, then run the animations normally
	if (!(	((fIsDucking != isDucking)	&& (fAnimationPriority < ANIM_DEATH))
		  ||	((fIsRunning != isRunning)	&& (fAnimationPriority == ANIM_BASIC))
		  ||	((fEntity.getGroundEntity() == null)	 && (fAnimationPriority <= ANIM_WAVE))
	     ))
		{		
		if (fAnimationReversed)
			{
			if (fAnimationFrame > fAnimationEnd)
				{
				decFrame();
				return;
				}
			}
		else
			{
			if (fAnimationFrame < fAnimationEnd)
				{
				incFrame();
				return;
				}
			}
		
		if (fAnimationPriority == ANIM_DEATH)
			return;		// stay there
		
		if (fAnimationPriority == ANIM_JUMP)
			{
			if (fEntity.getGroundEntity() != null)
				setAnimation(ANIMATE_LAND, true, 0);	
			return;
			}
		}
	
	// at this point, we're either here because we're making
	// a transition, or we didn't return from the normal 
	// animation handling, so reset to a basic state
					
	fIsDucking = isDucking;
	fIsRunning = isRunning;

	if (fEntity.getGroundEntity() == null)
		setAnimation(ANIMATE_FLAIL, true, 0);
	else
		setAnimation(ANIMATE_NORMAL, true, 0);	
	}
/**
 * Set sound for client
 */
protected void calcClientSound() 
	{
	String weapsound = null;

	//Help beep goes here
	if (fWeapon != null)
		weapsound = fWeapon.getWeaponSound();
		
	if ((fWaterLevel != 0) && ((fWaterType & (Engine.CONTENTS_LAVA | Engine.CONTENTS_SLIME)) != 0))
		fEntity.setSound(Engine.getSoundIndex("player/fry.wav"));
	else if (weapsound != null)
		fEntity.setSound(Engine.getSoundIndex(weapsound));
	else
		fEntity.setSound(0);
	}
/**
 * This method was created by a SmartGuide.
 * @return float
 * @param angle q2java.Vec3
 * @param velocity q2java.Vec3
 */
protected float calcRoll(Vector3f velocity) 
	{
	float	sign;
	float	side;
	float	value;

	side = velocity.dot(fRight);
	sign = side < 0 ? -1 : 1;
	side = Math.abs(side);
	
	value = gRollAngle;

	if (side < gRollSpeed)
		side = side * value / gRollSpeed;
	else
		side = value;
	
	return side*sign*4;	
	}
/**
 * This method was created by a SmartGuide.
 */
protected void calcViewOffset() 
	{
	fViewOffset.set(0, 0, fViewHeight);
	float ratio;
	Angle3f angles;	
	float delta;

	// add angles based on weapon kick
	fEntity.setPlayerKickAngles(fKickAngles);	
	
	// add angles based on damage kick
	
	ratio = (fDamageTime - Game.getGameTime()) / DAMAGE_TIME;
	if (ratio < 0)
		{
		ratio = 0;
		fDamagePitch = 0;
		fDamageRoll = 0;
		}
	else
		{
		angles = fEntity.getPlayerKickAngles();
		angles.x += ratio * fDamagePitch;
		angles.z += ratio * fDamageRoll;
		fEntity.setPlayerKickAngles(angles);
		}
	
	// add pitch based on fall kick
	/*
	ratio = (ent->client->fall_time - level.time) / FALL_TIME;
	if (ratio < 0)
		ratio = 0;
	angles[PITCH] += ratio * ent->client->fall_value;
	*/

	angles = fEntity.getPlayerKickAngles();

	// add angles based on velocity
	Angle3f  angle   = fEntity.getAngles();

	// this little section limits the scope of 3 Vector3fs
	// for clarity
		{
		Vector3f forward = Q2Recycler.getVector3f();
		Vector3f right = Q2Recycler.getVector3f();
		Vector3f velocity = fEntity.getVelocity();
		
		angle.getVectors( forward, right, null );
		angles.x += velocity.dot(forward) * gRunPitch;
		angles.z += velocity.dot(right) * gRunRoll;

		Q2Recycler.put(right);
		Q2Recycler.put(forward);
		}

	// add angles based on bob
	delta = fBobFracSin * gBobPitch * fXYSpeed;
	if (fIsDucking)
			delta *= 6;             // crouching
	angles.x += delta;
	delta = fBobFracSin * gBobRoll * fXYSpeed;
	if (fIsDucking)
			delta *= 6;             // crouching
	if ((fBobCycle & 1) > 0)
			delta = -delta;
	angles.z += delta;
	fEntity.setPlayerKickAngles(angles);
	

	// add bob height
	float bob = fBobFracSin * fXYSpeed * gBobUp; // *3 added to magnify effect
	if (bob > 6)
		bob = 6.0F;
	fViewOffset.z += bob;	
		
	// add kick offset
	fViewOffset.add(fKickOrigin);
	
	// absolutely bound offsets
	// so the view can never be outside the player box		
	if (fViewOffset.x < -14)
		fViewOffset.x = -14;
	else if (fViewOffset.x > 14)
		fViewOffset.x = 14;
		
	if (fViewOffset.y < -14)
		fViewOffset.y = -14;
	else if (fViewOffset.y > 14)
		fViewOffset.y = 14;
		
	if (fViewOffset.z < -22)
		fViewOffset.z = -22;
	else if (fViewOffset.z > 30)
		fViewOffset.z = 30;
		
	fEntity.setPlayerViewOffset(fViewOffset);
	}
/**
 * Called by the current weapon when it's done deactivating,
 * letting us know it's time to bring up the next weapon.
 */
public void changeWeapon() 
	{
	fLastWeapon = fWeapon;
	
	if (fNextWeapon != null) 
		fWeapon = fNextWeapon;
	else
		fWeapon = nextAvailableWeapon();
						
	fNextWeapon = null;
	fWeapon.activate();
	showVWep();	
	setAnimation(ANIMATE_VWEP_ACTIVATE);	
	}
/**
 * Clear the player's settings so they are a fresh 
 * new Space Marine.
 */
protected void clearSettings( ) 
	{
	// remove any quad-type powerup effects
	setDamageMultiplier(1.0F);
	
	// throw out everything the player was carrying
	fInventory.clear();

	// initialize the AmmoBelt with the max amounts of each type of ammo
	fInventory.addPack("shells", new InventoryPack(100, "a_shells"));
	fInventory.addPack("bullets", new InventoryPack(200, "a_bullets"));
	fInventory.addPack("grenades", new InventoryPack(50, "a_grenades"));
	fInventory.addPack("rockets", new InventoryPack(50, "a_rockets"));
	fInventory.addPack("cells", new InventoryPack(200, "a_cells"));
	fInventory.addPack("slugs", new InventoryPack(50, "a_slugs"));

	// clear weapon references
	fWeapon = fNextWeapon = fLastWeapon = null;
	
	// give the player a hand blaster
	addWeapon(".spawn.weapon_blaster", true);

	// setup the inventory to support hand grenade weapons, but 
	// don't actually give any.  Necessary for hand grenades
	// to work if a grenade launcher is picked up first.
	addWeapon(".spawn.ammo_grenades", false);
	setAmmoCount("grenades", 0, true);

	// initialize the armor settings (jacket_armor quality protection)
	fArmor.reset();


	// set various other bits to a normal setting
	fIsDead = false;
	fIsGibbed = false;
	fKiller = null;
	if (fInIntermission)
		{
		fScore = 0;
		fInIntermission = false;
		}

	fWaterLevel = 0;
	fOldWaterLevel = 0;
	fOldButtons = 0;
	fLatchedButtons = 0;
	fViewHeight = 22;

	setHealth(100);
	fHealthMax = 100;
	setAnimation(ANIMATE_NORMAL, true, 0);
	fOldVelocity = new Vector3f();
	}
/**
 * Closes any special screens
 */
protected void closeDisplay()
	{
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)0);
	fShowScore = false;
	fShowInventory = false;
	}
/**
 * This allows us to test different blend modes.<p>
 * This function will not work unless the setPlayerBlend in calcBlend is commented out.
 *
 * @param argv java.lang.String[]
 */
 
public void cmd_debug_setblend(String[] argv, String args)
	{
	if (argv.length != 5)
		fEntity.cprint(Engine.PRINT_HIGH, "Usage: debug_setblend <red> <green> <blue> <alpha>\n");
	else
		{
		try
			{
			fEntity.setPlayerBlend(Float.valueOf(argv[1]).floatValue(), 
								   Float.valueOf(argv[2]).floatValue(), 
								   Float.valueOf(argv[3]).floatValue(), 
								   Float.valueOf(argv[4]).floatValue());
			}
		catch (NumberFormatException nfe)
			{
			fEntity.cprint(Engine.PRINT_HIGH, nfe.toString());
			}
		}
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

	// get it from our inventory
	InventoryPack ip = fInventory.getPack(itemName);

	// empty handed
	if ((ip == null) || (ip.fAmount < 1))
		{
		Object[] msgargv = {itemName};
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("q2java.baseq2.Messages", "dont_have", msgargv) + "\n");
		return;
		}		

	// deal with dropping GenericItems
	if (ip.fItem instanceof GenericItem)
		{
		GenericItem item = (GenericItem) ip.fItem;

		//CHANGES for Delegation model
		try 
			{ 
			fInventorySupport.fireEvent(this,item,false); 
			gInventorySupport.fireEvent(this,item,false); 
			}
		catch(PropertyVetoException pve)
			{
		    String s = pve.getMessage();
		    
		    if( s != null )
		    	{
				fEntity.centerprint(s);
		    	}

		    return;
			}
		//END changes for delegation model

		// check whether the item is willing
		if (!item.isDroppable())
			{
			fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("q2java.baseq2.Messages", "item_not_droppable", null) + "\n");
			return;
			}

		// handle the subcase of GenericWeapons
		if (item instanceof GenericWeapon)
			{
			// don't let them drop their current weapon if its the only one they have
			if ((item == fWeapon) && (ip.fAmount < 2))
				{
				fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("q2java.baseq2.Messages", "no_current_weapon_drop", null) + "\n");
				return;
				}
				
			// if they have more than one of these weapons, create
			// a new instance to toss
			if (ip.fAmount > 1)
				{
				try
					{
					item = (GenericItem) item.getClass().newInstance();
					}
				catch (Exception e)
					{
					e.printStackTrace();
					return;
					}
				}
				
			// get a weapon reference
			GenericWeapon weapon = (GenericWeapon) item;

			// check for weird case where a weapon is its own ammo
			// (grenades)
			if (weapon.getAmmoBoxClass() == weapon.getClass())
				{
				// figure out how much ammo goes into the ammo/weapon
				int amount = Math.max(1, Math.min(ip.fAmount, weapon.getAmmoCount()));
										
				// transfer that much from the player to the (grenade)
				setAmmoCount(weapon.getItemName(), -amount, false);
				weapon.setAmmoCount(amount);

				// drop it
				weapon.drop(this, GenericItem.DROP_TIMEOUT);
				return;
				}

			// normal weapon - set it to have no ammo
			weapon.setAmmoCount(0);
			}

		// go ahead and chuck the item
		fInventory.remove(itemName);
		item.drop(this, GenericItem.DROP_TIMEOUT);
		return;
		}
		
	// deal with dropping ammo
	if (ip.fItem instanceof Class)
		{
		Class cls = (Class) ip.fItem;
		if (AmmoHolder.class.isAssignableFrom(cls))
			{
			try
				{
				// create an ammo box
				AmmoHolder ammo = (AmmoHolder) cls.newInstance();
				
				// figure out how much ammo goes into the box
				int amount = Math.min(getAmmoCount(ammo.getItemName()), ammo.getAmmoCount());

				if (amount > 0)
					{
					// transfer that much from the player to the box
					setAmmoCount(ammo.getItemName(), -amount, false);
					ammo.setAmmoCount(amount);
	
					// chuck it
					ammo.drop(this, GenericItem.DROP_TIMEOUT);
					}
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			return;
			}

		System.out.println("Don't know how to drop class: " + cls);
		}
		
		
	// wimp out
	fEntity.cprint(Engine.PRINT_MEDIUM, "Dropping " + itemName + " not implemented\n");
	}
/**
 * Change field-of-view.
 * @param argv java.lang.String[]
 */
public void cmd_fov(String[] argv, String args) 
	{
	if (argv.length < 1)
		{
		fEntity.cprint(Engine.PRINT_HIGH, "cmd_fov() called with no arguments\n");
		}
		
	int i = Integer.parseInt(argv[1]);
	
	// limit to reasonable values
	if (i < 1)
		i = 90;
	if (i > 160)
		i = 160;

	fEntity.setPlayerFOV(i);
	}
/**
 * Tell the player about the game.
 * @param argv java.lang.String[]
 */
public void cmd_gameversion(String[] argv, String args) 
	{
	fEntity.cprint(Engine.PRINT_HIGH, BaseQ2.getVersion() + "\n");
	}
/**
 * Give the player one of each weapon.  Good for debugging.
 * @param argv java.lang.String[]
 */
public void cmd_giveall(String[] argv, String args)
	{
/*	
	if (!BaseQ2.isCheating())
		{
		fEntity.cprint(Engine.PRINT_HIGH, "cheating not turned on\n");
		return;
		}
		
	addWeapon(".spawn.ammo_grenades", false);		
	addWeapon(".spawn.weapon_shotgun", false);
	addWeapon(".spawn.weapon_supershotgun", false);
	addWeapon(".spawn.weapon_machinegun", false);
	addWeapon(".spawn.weapon_chaingun", false);
	addWeapon(".spawn.weapon_grenadelauncher", false);
	addWeapon(".spawn.weapon_rocketlauncher", false);
	addWeapon(".spawn.weapon_hyperblaster", false);
	addWeapon(".spawn.weapon_railgun", false);
	addWeapon(".spawn.weapon_bfg", false);
*/	
	}
/**
 * Draw the help computer.
 * Only works for drawing the scoreboard in a deathmatch right now.
 * @param argv java.lang.String[]
 */
public void cmd_help(String[] argv, String args)
	{
 	if (BaseQ2.gIsDeathmatch)
 		playerCommand("score");
	}
/**
 * Identify the player in your crosshairs.
 * @param argv java.lang.String[]
 */
public void cmd_id(String[] argv, String args) 
	{
	Point3f start = fEntity.getOrigin();
	start.z += fViewHeight;

	Vector3f forward = new Vector3f();
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, null, null);

	Point3f end = new Point3f();
	end.scaleAdd(8192, forward, start);

	TraceResults tr = Engine.trace(start, end, fEntity, Engine.MASK_SHOT|Engine.CONTENTS_SLIME|Engine.CONTENTS_LAVA);
	if ((tr.fEntity != null) && (tr.fEntity.isPlayer()))
		fEntity.centerprint(((Player)tr.fEntity.getPlayerListener()).getName());
	}
/**
 * Identify the Java object in your crosshairs.
 * @param argv java.lang.String[]
 */
public void cmd_iddebug(String[] argv, String args) 
	{
	Point3f start = fEntity.getOrigin();
	start.z += fViewHeight;

	Vector3f forward = new Vector3f();
	Angle3f ang = fEntity.getPlayerViewAngles();
	ang.getVectors(forward, null, null);

	Point3f end = new Point3f();
	end.scaleAdd(8192, forward, start);

	TraceResults tr = Engine.trace(start, end, fEntity, Engine.MASK_ALL);
	if (tr.fEntity == null)
		return;
		
	Object obj = tr.fEntity.getReference();
	if (obj != null)
		fEntity.cprint(Engine.PRINT_HIGH, obj.toString() + "\n");
	else
		fEntity.cprint(Engine.PRINT_HIGH, tr.fEntity.toString() + "\n");
	}
/**
 * displays the inventory
 * @param argv java.lang.String[] - not used.
 */
public void cmd_inven(String[] argv, String args)
	{
	fShowScore = false;
	
	if (fShowInventory)
		{
		fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short) 0);
		fShowInventory = false;
		return;
		}
	
	fShowInventory = true;
	
	Engine.writeByte(Engine.SVC_INVENTORY);	
	
	for (int i=0; i<InventoryList.length(); i++)
		{
		int n = fInventory.getNumberOf(InventoryList.getItemAtIndex(i));
		Engine.writeShort((short)n);
		}

	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)2);
	}
/**
 * Do-nothing placeholder to to keep players from driving 
 * other people nuts when they hit this particular key over and over.
 */
public void cmd_invnext(String[] argv, String args)
	{
	}
/**
 * Do-nothing placeholder to to keep players from driving 
 * other people nuts when they hit this particular key over and over.
 */
public void cmd_invprev(String[] argv, String args)
	{
	}
/**
 * Do-nothing placeholder to to keep players from driving 
 * other people nuts when they hit this particular key over and over.
 */
public void cmd_invuse(String[] argv, String args)
	{
	}
/**
 * Suicide.
 * @param argv java.lang.String[]
 */
public void cmd_kill(String[] argv, String args) 
	{
	if (fIsDead)
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.getBundle("q2java.baseq2.Messages").getString("already_dead") + "\n");
	else
		{
		DamageEvent de = DamageEvent.getEvent(this, this, this, null, fEntity.getOrigin(), null, 0,
				       0, 0, 0, "suicide");
		die(de);
		de.recycle();
		}
	}
/**
 * Put's away the any special screens that are currently displayed, e.g. scoreboard, inventory or help computer.
 * when the special screen is displayed and the esc key is hit this cmd is called
 * @param argv java.lang.String[]
 */
public void cmd_putaway(String[] argv, String args)
	{
	closeDisplay();
	}
/**
 * Send a chat message to all players.
 * @param (Ignored, uses the Engine.argv() value instead)
 */
public void cmd_say(String[] argv, String args) 
	{
	//leighd 04/14/99, index out of bounds exception with no
	//arguments - so don't do anything
	if (args.length() == 0)
		return;
		
	// remove any quote marks
	if (args.charAt(args.length()-1) == '"')
		args = args.substring(args.indexOf('"')+1, args.length()-1);

	// send it out
	Game.getPrintSupport().fireEvent(PrintEvent.PRINT_TALK, Engine.PRINT_CHAT, fEntity, getName(), null, args);	
	}
/**
 * Treat "say_team" the same as "say" if no team is set.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say_team(String[] argv, String args) 
	{
	//leighd 04/14/99, index out of bounds exception with no
	//arguments - so don't do anything
	if (args.length() == 0)
		return;
		
	// remove any quote marks
	if (args.charAt(args.length()-1) == '"')
		args = args.substring(args.indexOf('"')+1, args.length()-1);

	// send it out
	Game.getPrintSupport().fireEvent((fTeam == null ? PrintEvent.PRINT_TALK : PrintEvent.PRINT_TALK_TEAM), Engine.PRINT_CHAT, fEntity, getName(), fTeam, args);	
	}
/**
 * Display the scoreboard.
 * @param argv java.lang.String[]
 */
public void cmd_score(String[] argv, String args)
	{
	// needs to check for coop mode
	// --FIXME--
	if (!BaseQ2.gIsDeathmatch)
		 return;

	if (fShowScore == true)
		{
		fShowScore = false;
  		fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)0);
  		return;
 		}

 	fShowScore = true;
 	writeDeathmatchScoreboardMessage(null);
 	Engine.unicast(fEntity, true);
 	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);
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
		if (!fNextWeapon.isEnoughAmmo())
			{
			Object[] msgargv = {itemName};
			fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("q2java.baseq2.Messages", "no_ammo", msgargv) + "\n");
			fNextWeapon = null;
			return;
			}
		
		// signal the current weapon to deactivate..when it's
		// done deactivating, it will signal back to the player to 
		// changeWeapon() and we'll use() the next weapon		
		fWeapon.deactivate();	
		setAnimation(ANIMATE_VWEP_DEACTIVATE);
		return;
		}
	
	fInventory.getPack(itemName.toLowerCase()).fAmount--;
	ent.use(this);
	}
/**
 * Invoke player gestures.
 * @param argv java.lang.String[]
 */
public void cmd_wave(String[] argv, String args) 
	{
	int i = 0;
		
	if (argv.length > 1)
		i = Integer.parseInt(argv[1]);
			
	switch(i)
		{
		case 0: 
			setAnimation(ANIMATE_FLIPOFF);
			fEntity.cprint(Engine.PRINT_HIGH, "flipoff\n");
			break;

  		case 1: 
  			setAnimation(ANIMATE_SALUTE);
			fEntity.cprint(Engine.PRINT_HIGH, "salute\n");
			break;

  		case 2: 
  			setAnimation(ANIMATE_TAUNT);
			fEntity.cprint(Engine.PRINT_HIGH, "taunt\n");
			break;

  		case 3: 
  			setAnimation(ANIMATE_WAVE);
			fEntity.cprint(Engine.PRINT_HIGH, "wave\n");
			break;

	  	default: 
	  		setAnimation(ANIMATE_POINT);
			fEntity.cprint(Engine.PRINT_HIGH, "point\n");
			break;		
		}
	}
/**
 * Add a list of weapon to be excluded from the switching order. 
 * @author Brian Haskin
 * @param argv java.lang.String[] - list of weapons
 */
public void cmd_weapaddexcluded(String[] argv, String args)
	{
	for (int i=1; i < argv.length; i++)
		fWeaponsExcluded.addElement(argv[i]);
	}
/**
 * Switch to the last weapon used
 * @param argv java.lang.String[] - not used.
 */
public void cmd_weaplast(String[] argv, String args)
	{
	if ((fWeapon != null) && (fLastWeapon != null) && (fLastWeapon.isEnoughAmmo()))
		{
		fNextWeapon = fLastWeapon;
		fWeapon.deactivate();
		}
	}
/**
 * Switch to the next available weapon.
 * @param argv java.lang.String[] - not used.
 */
public void cmd_weapnext(String[] argv, String args) 
	{
	if (fWeapon == null)
		return;
		
	int i = (fWeaponOrder.indexOf(fWeapon.getItemName().toLowerCase()) + 1) % fWeaponOrder.size();
	int crashguard = i; // used to keep infinite loops from occuring
	
	do
		{
		fNextWeapon = (GenericWeapon) fInventory.get(((String) fWeaponOrder.elementAt(i)));
		i = (i+1) % fWeaponOrder.size();
		} while (((fNextWeapon == null) || !fNextWeapon.isEnoughAmmo() || fWeaponsExcluded.contains(fNextWeapon.getItemName().toLowerCase())) && (crashguard != i));
		
	if (fNextWeapon == fWeapon)
		fNextWeapon = null;
	else
		fWeapon.deactivate();
	}
/**
 * Switch to the previous available weapon.
 * @param argv java.lang.String[] - not used.
 */
public void cmd_weapprev(String[] argv, String args) 
	{
	if (fWeapon == null)
		return;
		
	int i = fWeaponOrder.indexOf(fWeapon.getItemName().toLowerCase()) - 1;
	if (i < 0)
		i = fWeaponOrder.size() - 1;
		
	int crashguard = i; // used to keep infinite loops from occuring
		
	do {
		fNextWeapon = (GenericWeapon) fInventory.get(((String) fWeaponOrder.elementAt(i)));
		if (--i < 0)
			i = fWeaponOrder.size() - 1;
	} while (((fNextWeapon == null) || !fNextWeapon.isEnoughAmmo() || fWeaponsExcluded.contains(fNextWeapon.getItemName().toLowerCase())) && (i != crashguard));

	if (fNextWeapon == fWeapon)
		fNextWeapon = null;
	else
		fWeapon.deactivate();
	}
/**
 * Print the excluded weapons list. <p>
 * This list should really be implemented as a CVAR somehow.
 * The biggest problem is the size limit on CVARs.
 * @param argv java.lang.String[] - not used.
 */
public void cmd_weapprintexcluded(String[] argv, String args)
	{
	fEntity.cprint(Engine.PRINT_HIGH, "Weapons Excluded: ");
	
	for (int i=0; i < fWeaponsExcluded.size(); i++)
		{
		fEntity.cprint(Engine.PRINT_HIGH, fWeaponsExcluded.elementAt(i) + ", ");
		}
	
	fEntity.cprint(Engine.PRINT_HIGH, ".\n");
	}
/**
 * Print the weapon order list. <p>
 * This list should really be implemented as a CVAR somehow.
 * The biggest problem is the size limit on CVARs.
 * @param argv java.lang.String[] - not used.
 */
public void cmd_weapprintorder(String[] argv, String args)
	{
	fEntity.cprint(Engine.PRINT_HIGH, "Weapon Order: ");
	
	for (int i=0; i < fWeaponOrder.size(); i++)
		{
		fEntity.cprint(Engine.PRINT_HIGH, fWeaponOrder.elementAt(i) + ", ");
		}
		
	fEntity.cprint(Engine.PRINT_HIGH, ".\n");
	}
/**
 * remove a list of weapons from the excluded list.
 * @author Brian Haskin
 * @param argv java.lang.String[] - list of weapons
 */
public void cmd_weapremoveexcluded(String[] argv, String args)
	{
	for (int i=1; i < argv.length; i++)
		{
		if (fWeaponsExcluded.contains(argv[i]))
			fWeaponsExcluded.removeElementAt(fWeaponsExcluded.indexOf(argv[i]));
		}
	}
/**
 * set the weapon switching order. If called without
 * arguments it will reset the order to the default id order.
 * Also, any weapons that have already been picked up but are not
 * explicitly listed will not be added to the list till the next respawn.
 * @author Brian Haskin
 * @param argv java.lang.String[] - list of weapons
 */
public void cmd_weapsetorder(String[] argv, String args)
	{
	fWeaponOrder.removeAllElements();
	
	if (argv.length > 1)
		{
		for (int i=1; i < argv.length; i++)
			fWeaponOrder.addElement(argv[i].toLowerCase());
		}
	else
		{
		fWeaponOrder.addElement("blaster");
		fWeaponOrder.addElement("shotgun");
		fWeaponOrder.addElement("super shotgun");
		fWeaponOrder.addElement("machinegun");
		fWeaponOrder.addElement("chaingun");
		fWeaponOrder.addElement("grenades");
		fWeaponOrder.addElement("grenade launcher");
		fWeaponOrder.addElement("rocket launcher");
		fWeaponOrder.addElement("hyperblaster");
		fWeaponOrder.addElement("railgun");
		fWeaponOrder.addElement("bfg10k");
		}
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
	Engine.debugLog("q2java.baseq2.Player.connect(" + ent + ")");
//	Runtime.getRuntime().traceMethodCalls(true);

	new Player(ent);
	}
/**
 * Make a copy of an entity to keep around for a while.
 * @param ent NativeEntity
 */
public static void copyCorpse(NativeEntity ent, float health) 
	{
	gCorpseQueue.copyCorpse(ent, health);
	}
/**
 * Inflict damage on the Player.
 * If the player takes enough damage, this function will call the Player.die() function.
 * @param DamageObject - The damage we are taking.
 */
public void damage(DamageEvent damage)	
	{
	// special case of damaging a corpse
	if (fIsDead && !fIsGibbed)
		{
		setHealth(fHealth - damage.fAmount);
		if (fHealth < GIB_HEALTH_THRESHOLD)
			gib(damage);
		else
			spawnDamage(Engine.TE_BLOOD, damage.fPoint, damage.fNormal, damage.fAmount);		
		return;
		}
		
	// call all the pre-armor damage filters
	filterDamage(damage, DAMAGE_FILTER_PHASE_PREARMOR);
	
	// knock the player around
	knockback(damage.fAttacker, damage.fDirection, damage.fKnockback, damage.fDamageFlags);

	// notify any armor timed filters
	filterDamage(damage, DAMAGE_FILTER_PHASE_ARMOR);
	
	// notify post-armor filters
	filterDamage(damage, DAMAGE_FILTER_PHASE_POSTARMOR);

	// always spawn explosive damage
	if ((damage.fTempEvent == Engine.TE_ROCKET_EXPLOSION) || (damage.fTempEvent == Engine.TE_ROCKET_EXPLOSION_WATER))
		spawnDamage(damage.fTempEvent, damage.fPoint, damage.fNormal, damage.fAmount);
		
	if (damage.fAmount > 0)
		{
		// damaging a live player causes a blood spray
		spawnDamage(Engine.TE_BLOOD, damage.fPoint, damage.fNormal, damage.fAmount);

		// cause screams if damage is caused by lava
		if (damage.fObitKey.equals("lava") && (Game.getGameTime() > fPainDebounceTime)) 
			{
			if ((GameUtil.randomInt() & 1) != 0)
				Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/burn1.wav"), 1, NativeEntity.ATTN_NORM, 0);
			else
				Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/burn2.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fPainDebounceTime = Game.getGameTime() + 1;
			}		

		setHealth(fHealth - damage.fAmount);

		if ((fHealth <= 0) && (!fIsDead))
			die(damage);
		}
	
	// These are used to determine the blend for this frame. (TSW)
	fDamageBlood += damage.fTakeDamage;
	fDamageArmor += damage.fArmorSave;
	fDamagePArmor += damage.fPowerArmorSave;
	
	// For view angle kicks this frame. (TSW)
	fDamageKnockback += damage.fKnockback;
	damage.fPoint.get(fDamageFrom);	// set fDamageFrom to value of point
	}
/** 
 * Handles color blends and view kicks
 * for this Player's damage this frame.
 * Called during endServerFrame().
 */
protected void damageFeedback()
	{
	Color3f		color;
	float		realcount, count, kick;
	float		side;
	int			n;						// Which pain sound to play

	Color4f	damageBlend	= getDamageBlend();
	short	statFlashes	= 0;

	// flash the backgrounds behind the status numbers (health = 1/armor = 2)
	if (fDamageBlood != 0)
		statFlashes |= 1;
	if (fDamageArmor != 0 /*&& !(player->flags & FL_GODMODE) && (client->invincible_framenum <= level.framenum)*/)
		statFlashes |= 2;

	fEntity.setPlayerStat(NativeEntity.STAT_FLASHES, statFlashes);

	// total points of damage shot at the player this frame
	count = (fDamageBlood + fDamageArmor + fDamagePArmor);
	
	if (count == 0)
		return;		// didn't take any damage

	// start a pain animation if still in the player model
//	if (fAnimationPriority < ANIM_PAIN /*&& player->s.modelindex == 255*/)
	setAnimation(ANIMATE_PAIN);
	
	realcount = count;
	if (count < 10)
		count = 10;	// always make a visible effect

	// play an apropriate pain sound
	if (fDamageBlood != 0 && Game.getGameTime() > fPainDebounceTime /* && !(player->flags & FL_GODMODE) && (client->invincible_framenum <= level.framenum)*/)
		{
		fPainDebounceTime = Game.getGameTime() + 0.7f;	// Wait .7 seconds before starting another pain sound. (?)
		if (fHealth < 25)
			n = 25;
		else if (fHealth < 50)
			n = 50;
		else if (fHealth < 75)
			n = 75;
		else
			n = 100;
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, getSexedSoundIndex( "pain" + Integer.toString(n) + "_" + Integer.toString((1 + ((int)Math.random() & 1))) ), 1, NativeEntity.ATTN_NORM, 0);
		}

	if (damageBlend.w < 0)
		damageBlend.w = 0;	// redundant with calcBlend?
		
	// the total alpha of the blend is always proportional to count
	damageBlend.w += count * 0.01;
	
	if (damageBlend.w < 0.2)
		damageBlend.w = 0.2f;
		
	if (damageBlend.w > 0.6)
		damageBlend.w = 0.6f;		// don't go too saturated

	// the color of the blend will vary based on how much was absorbed by different armors
	color = new Color3f();	// the alpha is not stored in this color, so we'll use a Color3f instead of 4f
	
	if (fDamagePArmor != 0)
		color.scaleAdd(fDamagePArmor / realcount, COLOR_GREEN, color);
	if (fDamageArmor != 0)
		color.scaleAdd(fDamageArmor / realcount, COLOR_WHITE, color);
	if (fDamageBlood != 0)
		color.scaleAdd(fDamageBlood / realcount, COLOR_RED, color);
	
	setDamageBlend(color.x, color.y, color.z, damageBlend.w);

	//
	// calculate view angle kicks
	//
	kick = Math.abs(fDamageKnockback);
	if (kick != 0 && getHealth() > 0)	// kick of 0 means no view adjust at all
		{
		kick = kick * 100 / getHealth();

		if (kick < count*0.5)
			kick = count*0.5f;
		if (kick > 50)
			kick = 50;

		Vector3f v = Q2Recycler.getVector3f();

		v.sub(fDamageFrom, fEntity.getOrigin());
		v.normalize();
		
		side = v.dot(fRight);
		fDamageRoll = kick*side*0.3f;
		
		side = -v.dot(fForward);
		fDamagePitch = kick*side*0.3f;

		fDamageTime = Game.getGameTime() + DAMAGE_TIME;

		Q2Recycler.put(v);
		}

	//
	// clear totals
	//
	fDamageBlood = 0;
	fDamageArmor = 0;
	fDamagePArmor = 0;
	fDamageKnockback = 0;
	}
/**
 * Advance the player's animation frame.
 */
protected void decFrame() 
	{
	fEntity.setFrame(--fAnimationFrame);
	}
/**
 * Called when the player croaks.
 */
protected void die(DamageEvent de)
	{
	if (fIsDead)
		return;	// already dead
		
	fIsDead = true;

	// remember who killed us
	fKiller = de.getAttacker();
	
	// figure the soonest time the player will be allowed to respawn
	fRespawnTime = (float)(Game.getGameTime() + 1);  
	
	// broadcast a message announcing our death
	broadcastObituary(de);
	
	// let the attacker know he killed us
	DeathScoreEvent dse = DeathScoreEvent.getEvent(fKiller, this, de.getObitKey(), de.getInflictor());
	RuleManager.getScoreManager().registerScoreEvent(dse);
	dse.recycle();

	fEntity.setSound(0);
	fEntity.setModelIndex2(0); // remove linked weapon model
	// remove the weapon from the POV
	fEntity.setPlayerGunIndex(0);
	fWeapon = null;
	
	Point3f maxs = fEntity.getMaxs();
	fEntity.setMaxs(maxs.x, maxs.y, maxs.z - 8);
	
	fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_DEADMONSTER);

	// let interested objects know the player died.
	fPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_DEAD, fKiller );
	gPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_DEAD, fKiller );
		
	fKillerYaw = calcAttackerYaw(de.getInflictor(), fKiller);
	writeDeathmatchScoreboardMessage(fKiller);
	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);
	fShowScore = true;
	
	if (fHealth < GIB_HEALTH_THRESHOLD) //FIXME..should be -40, was set lower for testing
		gib(de);
	else
		{	// normal death
		fEntity.setPlayerPMType(NativeEntity.PM_DEAD);		
		setAnimation(ANIMATE_DEATH);
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, getSexedSoundIndex("death"+((GameUtil.randomInt() & 0x03) + 1)), 1, NativeEntity.ATTN_NORM, 0);
		}
		
	fEntity.linkEntity();
	}
/**
 * Detach this Player object from the game, but this doesn't necessarily
 * mean the player is disconnecting.  They may be switching Player classes
 * so work quietly.
 */
public void dispose() 
	{
	// let interested objects know we're disconnecting.
	fPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_INVALID, this );	
	gPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_INVALID, this );	

	// instead of us calling Game.PlayerDisconnect 
	// Game should get called and then call this.
	Game.playerDisconnect(fEntity);

	// remove from various event sources
	Game.getPrintSupport().removePrintListener(this);
	Game.removeServerFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END);
	Game.removeGameStatusListener(this);

	// unhook from the NativeEntity we were associated with
	fEntity.setReference(null);
	fEntity.setPlayerListener(null);
	fEntity = null;
	}
/**
 * Called for each player after all the entities have 
 * had a chance to runFrame()
 */
protected void endServerFrame() 
	{	
	if (fInIntermission)
		return;
	
	worldEffects();
		
	//
	// set model angles from view angles so other things in
	// the world can tell which direction you are looking
	//
	Angle3f newAngles = fEntity.getPlayerViewAngles();
	newAngles.getVectors(fForward, fRight, fUp);
	if (newAngles.x > 180) 
		newAngles.x = newAngles.x - 360;
	newAngles.x /= 3;		
	newAngles.z = calcRoll(fEntity.getVelocity());
	fEntity.setAngles(newAngles);
	
	//Must calc the bob before anything else
	calcBob();
	fallingDamage();
	damageFeedback();
	calcViewOffset();	
	calcClientFrame();
	calcClientSound();
	calcClientEvent();
	
	// determine the full screen color blend
	// must be after viewoffset, so eye contents can be
	// accurately determined
	// FIXME: with client prediction, the contents
	// should be determined by the client
	calcBlend();

	// remember our velocity from this frame
	fOldVelocity = fEntity.getVelocity();
	
	// clear weapon kicks
	fKickAngles.set(0,0,0);
	fKickOrigin.set(0,0,0);
	
	// if the scoreboard is being diplayed update it
	if (fShowScore && (Game.getGameTime() > fShowScoreTime))
		{
		writeDeathmatchScoreboardMessage(fKiller);
		Engine.unicast(fEntity, false);
		fShowScoreTime = Game.getGameTime() + 3; // refresh at 3 second intervals
		}
		
	// disable pickup message		
	if ((fPickupMsgTime > 0) && (Game.getGameTime() > fPickupMsgTime))		
		{
		fEntity.setPlayerStat(NativeEntity.STAT_PICKUP_ICON, (short) 0);
		fEntity.setPlayerStat(NativeEntity.STAT_PICKUP_STRING, (short)0);		
		fPickupMsgTime = 0;
		}
	}
/**
 * Get an enumeration of all active instances of this class.
 * @return java.util.Enumeration
 */
public static Enumeration enumeratePlayers() 
	{
	return new PlayerEnumeration();
	}
/**
 * This method was created by a SmartGuide.
 */
protected void fallingDamage() 
	{
	// no damage if you're airborne
	if (fEntity.getGroundEntity() == null)
		return;

	// never take falling damage if completely underwater
	if (fWaterLevel == 3)
		return;

	Vector3f velocity = fEntity.getVelocity();
	float delta = velocity.z - fOldVelocity.z;
	delta = (float) (delta * delta * 0.0001);

	// decrease damage if you're landing in water		
	if (fWaterLevel == 2)
		delta *= 0.25;
	if (fWaterLevel == 1)
		delta *= 0.5;

	// silent like a ninja
	if (delta < 1)
		return;

	// land with a regular footstep noise
	if (delta < 15)
		{
		Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_FOOTSTEP);
		return;
		}
		
		
/*
	ent->client->fall_value = delta*0.5;
	if (ent->client->fall_value > 40)
		ent->client->fall_value = 40;
	ent->client->fall_time = level.time + FALL_TIME;
*/

	// land a little heavier		
	if (delta < 30)
		{
		Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_FALLSHORT);
		return;
		}			

	// land hard enough to damage and make more noise
	if (fHealth > 0)
		{
		if (delta >= 55)
			Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_FALLFAR);
		else
			Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_FALL);
		}

//	ent->pain_debounce_time = level.time;	// no normal pain sound

	float damage = (delta - 30) / 2;
	if (damage < 1)
		damage = 1;

	if (!BaseQ2.isDMFlagSet(BaseQ2.DF_NO_FALLING))
		damage(null, null, new Vector3f(0, 0, 1), fEntity.getOrigin(), new Vector3f(0, 0, 0), (int) damage, 0, 0, Engine.TE_NONE, "falling");
	}
/**
 * Let all the damage listeners from a particular phase filter a DamageObject;
 * @param DamageObject - the damage being taken
 * @param int - phase of filtering, one of DAMAGE_FILTER_PHASE_*
 * @return DamageObject - the modified damage
 */
protected void filterDamage(DamageEvent damage, int phase) 
	{
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		fPreArmorDamageSupport.fireEvent(damage);
		gPreArmorDamageSupport.fireEvent(damage);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		fArmorDamageSupport.fireEvent(damage);
		gArmorDamageSupport.fireEvent(damage);
		}
	else
		{
		fPostArmorDamageSupport.fireEvent(damage);
		gPostArmorDamageSupport.fireEvent(damage);
		}
	}
/**
 * Called when the game status changes.
 */
public void gameStatusChanged(GameStatusEvent e)
	{
	switch (e.getState())
		{
		case GameStatusEvent.GAME_INTERMISSION:
			if (!fInIntermission)
				startIntermission();
			break;		
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param itemname java.lang.String
 */
public int getAmmoCount(String ammoName) 
	{
	if (ammoName == null)
		return 0;
		
	InventoryPack p = fInventory.getPack(ammoName);
	
	if (p == null)
		return 0;		
	else
		return p.fAmount;				
	}
/**
 * Get this player's ArmorDamageFilter - useful for CTF AutoDoc which
 * repairs the player's armor.
 * @return baseq2.ArmorDamageFilter
 */
public ArmorDamageFilter getArmor() 
	{
	return fArmor;
	}
/**
 * Get which weapon the Player is currently using.
 * @return baseq2.GenericWeapon, may be null
 */
public GenericWeapon getCurrentWeapon() 
	{
	return fWeapon;
	}
/**
 * Returns the current damage blend color for this frame.
 * @return float
 */
public Color4f getDamageBlend() 
	{
	return fDamageBlend;
	}
/**
 * How much damage should be scaled for this player. 
 *
 * @return Damage multiplier - normally 1.0, but something like a quad might cause this to be 4.0
 */
public float getDamageMultiplier() 
	{
	return fDamageMultiplier;
	}
/**
 * Returns the current alpha level for this frame.
 * @return float
 */
public float getFrameAlpha() 
	{
	return fFrameAlpha;
	}
/**
 * Get the player's health.
 * @return int
 */
public float getHealth() 
	{
	return fHealth;
	}
/**
 * Get the upper limit of the player's health range.
 * @return int
 */
public float getHealthMax() 
	{
	return fHealthMax;
	}
/**
 * Figure out a spot to use for intermission.  It could be that
 * different players will see different intermission spots - unlike
 * the stock Q2 game - but that just makes life more interesting.
 *
 * @return q2java.baseq2.GenericSpawnpoint
 */
public GenericSpawnpoint getIntermissionSpot() 
	{
	// gather list of info_player_intermission entities
	Vector v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_intermission.REGISTRY_KEY);

	// if there weren't any intermission spots, try for info_player_start spots
	if (v.size() < 1)
		v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_start.REGISTRY_KEY);

	// still no spots found? try for info_player_deathmatch
	if (v.size() < 1)
		v = Game.getLevelRegistryList(q2java.baseq2.spawn.info_player_deathmatch.REGISTRY_KEY);
		
	// randomly pick something from the list
	int i = (GameUtil.randomInt() & 0x0fff) % v.size();
	return (GenericSpawnpoint) v.elementAt(i);
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.Object
 * @param itemName java.lang.String
 */
public Object getInventory(String itemName) 
	{
	return fInventory.get(itemName);
	}
/**
 * Get how many of a kind of item we're carrying.
 * @return int
 * @param itemName java.lang.String
 */
public int getInventoryCount(String itemName) 
	{
	return fInventory.getNumberOf(itemName);
	}
/**
 * Returns the mass of the player (normally 200).
 * @return	The player's mass.
 */
public int getMass()
	{
	return fMass;
	}
/**
 * Get the maximum amount of a given type of ammo the player can carry.
 * @return int
 * @param ammoName
 */
public int getMaxAmmoCount(String ammoName) 
	{
	if (ammoName == null)
		return Integer.MAX_VALUE;
		
	InventoryPack p = fInventory.getPack(ammoName);
	
	if (p == null)
		return Integer.MAX_VALUE;		
	else
		return p.fMaxAmount;				
	}
/**
 * Get the Player's name
 * @return The player's name.
 */
public String getName()
	{
	return getPlayerInfo("name");
	}
/**
 * Lookup a value from the userinfo string.
 * @param key name of the value we're looking for.
 * @return value if key is found, null otherwise.
 */
public String getPlayerInfo(String key) 
	{
	return getPlayerInfo(key, null);
	}
/**
 * Lookup a value from the userinfo string.
 * @param key name of the value we're looking for.
 * @param defaultValue what to return if key isn't found.
 * @return java.lang.String
 */
public String getPlayerInfo(String key, String defaultValue) 
	{
	if (fPlayerInfo == null)
		return defaultValue;

	String result = (String) fPlayerInfo.get(key);		
	if (result == null)
		return defaultValue;
	else
		return result;
	}
/**
 * Get the ResourceGroup this player belongs to.
 * @return q2jgame.ResourceGroup
 */
public ResourceGroup getResourceGroup() 
	{
	return fResourceGroup;
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getScore() 
	{
	return fScore;
	}
/**
 * Get the index of a sound, based on the sex of the player.
 * @return int
 * @param base java.lang.String
 */
protected int getSexedSoundIndex(String base) 
	{
//	return Engine.getSoundIndex((fIsFemale ? "player/female/" : "player/male/") + base + ".wav");

	StringBuffer sb = Q2Recycler.getStringBuffer();
	
	if (fIsFemale)
		sb.append("player/female/");
	else
		sb.append("player/male/");

	sb.append(base);
	sb.append(".wav");

	int result = Engine.getSoundIndex(sb.toString());
	
	Q2Recycler.put(sb);

	return result;	
	}
/**
 * Get a suitable Spawnpoint to jump to.  Mods like CTF might
 * want to override this to use team spawnpoints instead.
 * @return baseq2.GenericSpawnpoint
 */
protected GenericSpawnpoint getSpawnpoint() 
	{
	if (BaseQ2.gIsDeathmatch)
		{
		if (BaseQ2.isDMFlagSet(BaseQ2.DF_SPAWN_FARTHEST))
			return MiscUtil.getSpawnpointFarthest();		
		else
			return MiscUtil.getSpawnpointRandom();
		}
		
	return MiscUtil.getSpawnpointSingle();
	}
/**
 * Get an object representing which team if any this player belongs to.
 * @return java.lang.Object
 */
public Object getTeam() 
	{
	return fTeam;
	}
/**
 * Find out if we're underwater
 * @return int - level of water. 0, we're out - 3, head is under.
 */
public int getWaterLevel()
	{
	return fWaterLevel;
	}
/**
 * Called when the player (alive or dead) receives too much damage.
 *
 * @param de, the DamageEvent that's putting the player over the edge
 *  between leaving a good-looking corpse, and becoming chum for the fishes.
 *  affects how far the chunks fly :)
 */
public void gib(DamageEvent de) 
	{
	fIsGibbed = true;

	// make a nasty noise
	Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("misc/udeath.wav"), 1, NativeEntity.ATTN_NORM, 0);

	// throw meaty chunks
	for (int n = 0; n < 4; n++)
		(new Gib()).toss(fEntity, "models/objects/gibs/sm_meat/tris.md2", de, Gib.GIB_ORGANIC);
		
	// conver the player model from a full body into just a head
	if ((GameUtil.randomInt() & 1) == 1)
		{
		fEntity.setModel("models/objects/gibs/head2/tris.md2");
		fEntity.setSkinNum(1);		// second skin is player
		}
	else
		{
		fEntity.setModel("models/objects/gibs/skull/tris.md2");
		fEntity.setSkinNum(0);
		}

	Point3f origin = fEntity.getOrigin();
	origin.z += 32;
	fEntity.setOrigin(origin);

	fEntity.setFrame(0);
	fAnimationPriority = ANIM_DEATH;
	fAnimationEnd = 0;
	
	fEntity.setMins(-16, -16, 0);
	fEntity.setMaxs( 16, 16, 16);

	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setEffects(NativeEntity.EF_GIB);
	fEntity.setPlayerPMType(NativeEntity.PM_GIB);		
	fEntity.setSound(0);

	Vector3f v = Gib.calcVelocity(de);
	v.add(fEntity.getVelocity());
	fEntity.setVelocity(v);

	fEntity.linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 */
public boolean heal(float amount, boolean overrideMax) 
	{
	if (overrideMax)
		{
		setHealth(fHealth + amount);
		return true;
		}
		
	if (fHealth < fHealthMax)
		{
		setHealth(fHealth + Math.min(amount, fHealthMax - fHealth));
		return true;
		}

	// we didn't need it		
	return false;
	}
/**
 * Advance the player's animation frame.
 */
protected void incFrame() 
	{
	fEntity.setFrame(++fAnimationFrame);
	}
/**
 * Is the player carrying a particular item.
 * @return boolean
 * @param itemName java.lang.String
 */
public boolean isCarrying(String itemName) 
	{
	return fInventory.isCarrying(itemName);
	}
/**
 * Is this player pining for the fjords?
 * @return boolean
 */
public boolean isDead() 
	{
	return fIsDead;
	}
/** 
 * Is this guy really a chick?
 */
public boolean isFemale()
	{
	return fIsFemale;
	}
/**
 * Are these two players on the same team?
 * @return boolean
 * @param p baseq2.Player
 */
public boolean isTeammate(Player p) 
	{
	return (fTeam != null) && (fTeam.equals(p.getTeam()));
	}
/**
 * Knock the player around. 
 * @param dir the direction the damage is coming from.
 * @param knockback how much the player should be pushed around because of the damage.
 * @param dflags flags indicating the type of damage, corresponding to GameEntity.DAMAGE_* constants.
 */
public void knockback(GameObject attacker, Vector3f dir, int knockback, int dflags) 
	{
	if ((dflags & DAMAGE_NO_KNOCKBACK) == 0)
		{
		if (knockback != 0)
			{
			Vector3f	kickVelocity = Q2Recycler.getVector3f();
			int			mass = getMass();

			if (mass < 50)
				mass = 50;
				
			// If you don't normalize this, you'll get insane amounts of kickback!
			dir.normalize();

			// If we are our own attacker, do id's "rocket jump hack"
			if (this == attacker)
				kickVelocity.scale(1600 * knockback / mass, dir);
			else
				kickVelocity.scale(500 * knockback / mass, dir);

			// Add the new kick velocity to our current velocity.
			kickVelocity.add(fEntity.getVelocity());
			fEntity.setVelocity(kickVelocity);
			
			Q2Recycler.put(kickVelocity);
			}
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return q2jgame.weapon.PlayerWeapon
 */
protected GenericWeapon nextAvailableWeapon() 
	{
	GenericWeapon w;
	
	w = (GenericWeapon) fInventory.get("railgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;
		
	w = (GenericWeapon) fInventory.get("hyperblaster");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (GenericWeapon) fInventory.get("chaingun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (GenericWeapon) fInventory.get("machinegun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (GenericWeapon) fInventory.get("super shotgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (GenericWeapon) fInventory.get("shotgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	return (GenericWeapon) fInventory.get("blaster");
	}
/**
 * Notify the player (on their HUD) that they picked something up.
 * @param itemName java.lang.String
 * @param iconName java.lang.String
 */
public void notifyPickup(String itemName, String iconName) 
	{
	// flash the Player's screen
	setFrameAlpha(0.25f);
	
	// show icon and name on status bar
	fEntity.setPlayerStat(NativeEntity.STAT_PICKUP_ICON, (short) Engine.getImageIndex(iconName));
	fEntity.setPlayerStat(NativeEntity.STAT_PICKUP_STRING, (short)(Engine.CS_ITEMS + InventoryList.getIndexOf(itemName)));
	fPickupMsgTime = Game.getGameTime() + 3;
	}
/**
 * Called by the DLL when the player should begin playing in the game.
 * @param loadgame boolean
 */
public void playerBegin() 
	{
	Engine.debugLog("Player.begin()");

	fStartTime = (float) Game.getGameTime();	
	
	// restore key entity settings - needs to be done each level change.
	fEntity.setPlayerStat(NativeEntity.STAT_HEALTH_ICON, (short) Engine.getImageIndex("i_health"));	
//	fEntity.setPlayerGravity((short)GameModule.gGravity.getFloat());
	setGravity(0, 0, -1);
	Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), getName() + "\\" + getPlayerInfo("skin"));			
		
	// things that need to be reset on map changes
	fCmdAngles  = new Angle3f();		

	// notify objects we're changing levels
	//	notifyPlayerStateListeners(PlayerStateListener.PLAYER_LEVELCHANGE);	
	fPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_SUSPENDEDSTART, 
					     BaseQ2.gWorld  );
	gPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_SUSPENDEDSTART, 
					     BaseQ2.gWorld  );
	
	// things that need to be reset on map changes or respawn
	clearSettings();

	// place in game
	spawn();	

	// greet the player
	welcome();

	// make sure all view stuff is valid
	endServerFrame();	
	}
/**
 * Called by the DLL when the player has typed, or initiated a command.
 */
public void playerCommand() 
	{
	// get the command args from the Engine
	String[] argv = new String[Engine.getArgc()];
	for (int i = 0; i < argv.length; i++)
		argv[i] = Engine.getArgv(i);

	// call a more intelligent playerCommand()
	playerCommand("cmd_" + argv[0], argv, Engine.getArgs());
	}
/**
 * Simulate a player command sent from the Engine.
 */
public void playerCommand(String command) 
	{
	// breakup the command string into individual words
	StringTokenizer st = new StringTokenizer(command);
	String[] argv = new String[st.countTokens()];
	for (int i = 0; i < argv.length; i++)
		argv[i] = st.nextToken();

	// strip the first word out of the command string
	if (argv.length < 2)
		command = "";
	else
		{
		int p = command.indexOf(argv[0]);
		p = command.indexOf(argv[1], p+1);
		command = command.substring(p);
		}
	
	// call the -real- playerCommand()
	playerCommand("cmd_" + argv[0], argv, command);		
	}
/**
 * Process a player command. Look for a handler with a
 * given method name, that takes the parameters (Player, String[], String),
 * and if not that, then a Player method that takes parameters (String[] String),
 * and if -that- can't be found, treat it as a chat.
 *
 * @param methodName, usually "cmd_" + Engine.argv(0)
 * @param argv A String array containg individual words such as are returned by Engine.argv()
 * @param args A String containing the command string -except- for the first word, such as is returned by Engine.args()
 */
public void playerCommand(String methodName, String[] argv, String args)
	{
	Class[] paramTypes;
	Object[] params;
	
	if (fPlayerCommandSupport.fireEvent( this, argv[0], args ))
		return;

	if (gPlayerCommandSupport.fireEvent( this, argv[0], args ))
		return;

	//
	// look for the command to be handled by the player class (or subclasses) itself
	//
	paramTypes = new Class[2];
	paramTypes[0] = argv.getClass();
	paramTypes[1] = args.getClass();
	
	params = new Object[2];
	params[0] = argv;
	params[1] = args;

	try
		{
		java.lang.reflect.Method meth = getClass().getMethod(methodName, paramTypes);						
		meth.invoke(this, params);
		return;
		}
	catch (NoSuchMethodException e1)
		{
		}
	catch (java.lang.reflect.InvocationTargetException e2)		
		{
		e2.getTargetException().printStackTrace();
		return;
		}
	catch (Exception e3)
		{
		e3.printStackTrace();
		return;
		}

	//
	// Couldn't find any methods to handle it? treat command as a chat
	//
	//leighd 04/14/99, index out of bounds exception with no
	//arguments - so don't do anything
	
	StringBuffer sb = Q2Recycler.getStringBuffer();
	
	sb.append(argv[0]);
	sb.append(' ');
	sb.append(args);
	
	// send it out
	Game.getPrintSupport().fireEvent(PrintEvent.PRINT_TALK, Engine.PRINT_CHAT, fEntity, getName(), null, sb.toString());
		
	Q2Recycler.put(sb);
	}
/**
 * Called by the DLL when the player is disconnecting. 
 * We should clean things up and say goodbye.
 * Be sure you drop any references to this player object.  
 */
public void playerDisconnect()
	{
	Engine.debugLog("Player.disconnect()");

	// broadcast an announcement
	Object[] args = {getName()};
	Game.localecast("q2java.baseq2.Messages", "disconnect", args, Engine.PRINT_HIGH);
	
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGOUT);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	fEntity.setModelIndex(0);
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.linkEntity();

	Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), "");	

	// disassociate this object from the rest of the game
	dispose();
	}
/**
 * Called (usually by the DLL) when the player's userinfo has changed.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
public void playerInfoChanged(String playerInfo) 
	{
//	Engine.debugLog("q2java.baseq2.Player.playerInfoChanged(" + playerInfo + ")");

	if (playerInfo == null)
		return;
		
	StringTokenizer st = new StringTokenizer(playerInfo, "\\");
	while (st.hasMoreTokens())
		{
		String key = st.nextToken();
		if (st.hasMoreTokens())
			{
			String val = st.nextToken();
			String oldVal = (String) fPlayerInfo.get(key);
			if ((oldVal == null) || (!val.equals(oldVal)))
				{
				fPlayerInfo.put(key, val);
				playerVariableChanged(key, oldVal, val);
				}
			}
		}					
	}
/**
 * Called by playerThink() when it decides that the player has jumped.
 */
protected void playerJumped() 
	{
	Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, getSexedSoundIndex("jump1"), 1, NativeEntity.ATTN_NORM, 0);	
	}
/**
 * All player entities get a chance to think.  When
 * a player entity thinks, it has to handle the 
 * users movement commands by calling pMove().
 * @param cmd commands from the client..indicate movement, jumping, weapon firing.
 */
public void playerThink(PlayerCmd cmd)
	{
	if (fInIntermission)
		return;

	fPlayerMoveSupport.fireEvent( this, cmd );		
	gPlayerMoveSupport.fireEvent( this, cmd );		
	PMoveResults pm = fEntity.pMove(cmd, Engine.MASK_PLAYERSOLID);

//  if (pm_passent->health > 0)
//      return gi.trace (start, mins, maxs, end, pm_passent, MASK_PLAYERSOLID);
//  else
//      return gi.trace (start, mins, maxs, end, pm_passent, MASK_DEADSOLID);

	fCmdForwardMove = cmd.fForwardMove;
	fCmdUpMove = cmd.fUpMove;
	fCmdSideMove = cmd.fSideMove;
 	fCmdAngles = cmd.getCmdAngles();
 	
	if ((fEntity.getGroundEntity() != null) && (pm.fGroundEntity == null) && (cmd.fUpMove >= 10) && (pm.fWaterLevel == 0))
		playerJumped();
		
	fViewHeight = pm.fViewHeight;	
	fWaterType = pm.fWaterType;
	fWaterLevel = pm.fWaterLevel;	
	fEntity.setGroundEntity(pm.fGroundEntity);

	if (fIsDead)
		fEntity.setPlayerViewAngles(-15, fKillerYaw, 40);
		
	fEntity.linkEntity();	
	
	touchTriggers();
	
	// notify everything the player has collided with
	if (pm.fTouched != null)
		{
		for (int i = 0; i < pm.fTouched.length; i++)	
			{
			Object obj = pm.fTouched[i].getReference();
			if (obj instanceof GameObject)
				((GameObject)obj).touch(this);
			}
		}

	fOldButtons = fButtons;	
	fButtons = cmd.fButtons;		
	fLatchedButtons |= fButtons & ~fOldButtons;	
	
	// fire weapon from final position if needed
	if (((fLatchedButtons & PlayerCmd.BUTTON_ATTACK) != 0) && (!fWeaponThunk) && (!fIsDead) && (fWeapon != null))
		{
		fWeaponThunk = true;
		fWeapon.weaponThink();
		}		
	}
/**
 * Called when an individual player variable changes.
 * @param key Keyword such as "name", "fov", etc..
 * @param oldValue Previous value of this keyword (may be null)
 * @param newValue New value of keyword (may be null)
 */
protected void playerVariableChanged(String key, String oldValue, String newValue) 
	{
//	Engine.debugLog("q2java.baseq2.Player.playerVariableChanged(" + key + ", " + oldValue + ", " + newValue + ")");

	try
	    {
	    fPlayerInfoSupport.fireEvent(this, key, newValue, oldValue);
	    gPlayerInfoSupport.fireEvent(this, key, newValue, oldValue);
	    }
	catch(PropertyVetoException pve)
		{
	    String s = pve.getMessage();
	    
	    if( s != null )
			fEntity.centerprint(s);
			

		// put the variable back the way it was - reset the Java
		// hashtable first, so that when we stuff the command and
		// it comes back to Java through playerInfoChanged(), it
		// won't trigger -another- call to this method thinking
		// that the old value is a new value (if that makes any sense)
		
	    //CHANGE: Pete 10/6/99
		if( oldValue != null )
			fPlayerInfo.put(key, oldValue);
			
	    GameUtil.stuffCommand(fEntity, "set " + key + " " + oldValue);
	    return;
		}

	if (key.equalsIgnoreCase("skin") || key.equalsIgnoreCase("name"))
		{
		Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), getPlayerInfo("name") + "\\" + getPlayerInfo("skin"));			

		// id's C code just checks the first letter of the skin for a 'f' or 'F'
		fIsFemale = (newValue != null) &&  newValue.toLowerCase().startsWith("female");

		showVWep();
		return;
		}
		
	if (key.equalsIgnoreCase("hand"))
		{		
		fHand = Integer.parseInt(newValue);			
		return;
		}
		
	if (key.equalsIgnoreCase("fov"))
		{
		fEntity.setPlayerFOV((new Float(newValue)).floatValue());	
		return;
		}

	if (key.equalsIgnoreCase("locale"))
		{
		Locale loc = GameUtil.getLocale(newValue);		
		fResourceGroup = Game.getResourceGroup(loc);
		Game.getPrintSupport().addPrintListener(this, PRINT_CHANNELS, loc, false);
		return;
		}
	}
/**
 * Called when a PrintEvent is fired - will actually
 * display the message on the player's screen.
 * @param pe q2java.core.event.PrintEvent
 */
public void print(PrintEvent pe)
	{
	int channel = pe.getPrintChannel();
	Object destination = pe.getDestination();

	// if the print event has a particular destination,
	// and we're not related to that destination
	// then ignore the PrintEvent.
	if ((destination != null) 
	&& (!destination.equals(fTeam))
	&& (!destination.equals(fEntity))
	&& (!destination.equals(this)))
		return;
				
	// see if some other player object has done the work
	// of formatting the message so it's suitable for
	// display on the player screens
	// and if not, format it and save the results
	// so other player objects can use it if they choose.
	//
	String msg = pe.getPlayerMessage();
	if (msg == null)
		{
		// get a StringBuffer
		StringBuffer sb = Q2Recycler.getStringBuffer();
	
		// build up the string to print
		String name = pe.getSourceName();		
			
		switch (channel)
			{
			case PrintEvent.PRINT_TALK:
				if (name != null)
					{
					sb.append(name);
					sb.append(": ");
					}			
				break;
			
			case PrintEvent.PRINT_TALK_TEAM:					
				if (name != null)
					{
					sb.append('(');
					sb.append(name);
					sb.append("): ");
					}			
				break;
				
			case PrintEvent.PRINT_TALK_PRIVATE:					
				if (name != null)
					{
					sb.append("<<");
					sb.append(name);
					sb.append(" tells you >>: ");
					}			
				break;
			}

		sb.append(pe.getMessage());
		
		// keep the message down to a reasonable length
		if (sb.length() > 150)
			sb.setLength(150);
		
		// finish it off
		sb.append('\n');
		msg = sb.toString();
		pe.setPlayerMessage(msg);
		
		// be nice
		Q2Recycler.put(sb);				
		}
				
	// assume the PrintEvent contains a message formatted for players
	fEntity.cprint(pe.getPrintFlags(), msg);	
	}
/**
 * This method was created by a SmartGuide.
 * @param foo q2java.Vec3
 */
public Point3f projectSource(Vector3f offset, Vector3f forward, Vector3f right) 
	{
	Vector3f distance = Q2Recycler.getVector3f();
	distance.set(offset);
	
	if (fHand == LEFT_HANDED)
		distance.y *= -1;
	else if (fHand == CENTER_HANDED)
		distance.y = 0;


	Point3f result = new Point3f();
	Point3f point = fEntity.getOrigin();
	
	result.x = point.x + forward.x * distance.x + right.x * distance.y;
	result.y = point.y + forward.y * distance.x + right.y * distance.y;
	result.z = point.z + forward.z * distance.x + right.z * distance.y + distance.z;
	
	Q2Recycler.put(distance);
	
	return result;		
	}
/**
 * Put an object into our inventory under a given name.
 * @param itemName key for storing the object
 * @param ent some arbitrary object
 */
public void putInventory(String itemName, Object ent) 
	{
	InventoryPack p = fInventory.getPack(itemName);
	if (p == null)
		fInventory.addPack(itemName, new InventoryPack(ent));
	else
		{
		// put the item in the pack and make sure
		// the count is at least one.
		p.fItem = ent;				
		if (p.fAmount == 0)
			p.fAmount = 1;
		}
	}
/**
 * Called when this player kills someone else.
 * @param p baseq2.Player this player's victim, may be the player himself or null.
 */
protected void registerKill(Player p) 
	{
	if ((p == this) || (p == null))
		setScore(-1, false);
	else
		setScore(1, false);	
	}
public static void removeAllDamageListener(DamageListener l)
	{
	removeAllDamageListener(l,DAMAGE_FILTER_PHASE_PREARMOR);
	}
/**
 * Remove an object that was registered to filter damage.
 * @param DamageFilter - filter to remove.
 * @param int - phase the filter was at.
 */
public static void removeAllDamageListener(DamageListener l, int phase)
	{
	if (l == null)
		return;
		
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		gPreArmorDamageSupport.removeDamageListener(l);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		gArmorDamageSupport.removeDamageListener(l);
		}
	else
		{
		gPostArmorDamageSupport.removeDamageListener(l);
		}
	}
public static void removeAllPlayerCommandListener(PlayerCommandListener l)
	{
	gPlayerCommandSupport.removePlayerCommandListener(l);
	}
public static void removeAllPlayerInfoListener(PlayerInfoListener l)
	{
	gPlayerInfoSupport.removePlayerInfoListener(l);
	}
public static void removeAllPlayerInventoryListener(InventoryListener l)
	{
	gInventorySupport.removeInventoryListener(l);
	}
public static void removeAllPlayerMoveListener(PlayerMoveListener l)
	{
	gPlayerMoveSupport.removePlayerMoveListener(l);
	}
public static void removeAllPlayerStateListener(PlayerStateListener l)
	{
	gPlayerStateSupport.removePlayerStateListener(l);
	}
public void removeDamageListener(DamageListener l)
	{
	removeDamageListener(l,DAMAGE_FILTER_PHASE_PREARMOR);
	}
/**
 * Remove an object that was registered to filter damage.
 * @param DamageFilter - filter to remove.
 * @param int - phase the filter was at.
 */
public void removeDamageListener(DamageListener l, int phase)
	{
	if (l == null)
		return;
		
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		fPreArmorDamageSupport.removeDamageListener(l);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		fArmorDamageSupport.removeDamageListener(l);
		}
	else
		{
		fPostArmorDamageSupport.removeDamageListener(l);
		}
	}
/**
 * Remove an object that was registered to filter damage.
 * @param DamageFilter - filter to remove.
 */
	  /*
public void removeDamageFilter(PlayerDamageListener l)
	{

	if (l == null)
		return;
		
	fPreArmorPlayerDamageSupport.removePlayerDamageListener(l);
	}
	  */
/**
 * Remove an object that was registered to filter damage.
 * @param DamageFilter - filter to remove.
 * @param int - phase the filter was at.
 */
	  /*
public void removeDamageFilter(PlayerDamageListener l, int phase)
	{
	if (l == null)
		return;
		
	if (phase == DAMAGE_FILTER_PHASE_PREARMOR)
		{
		fPreArmorPlayerDamageSupport.removePlayerDamageListener(l);
		}
	else if (phase == DAMAGE_FILTER_PHASE_ARMOR)
		{
		fArmorPlayerDamageSupport.removePlayerDamageListener(l);
		}
	else
		{
		fPostArmorPlayerDamageSupport.removePlayerDamageListener(l);
		}
	}
	  */
/**
 * This method was created by a SmartGuide.
 * @param name java.lang.String
 */
public void removeInventory(String name) 
	{
	fInventory.remove(name);
	}
public void removePlayerCommandListener(PlayerCommandListener l)
	{
	fPlayerCommandSupport.removePlayerCommandListener(l);
	}
public void removePlayerCvarListener(PlayerCvarListener l)
	{
	fPlayerCvarSupport.removePlayerCvarListener(this,l);
	}
public void removePlayerInfoListener(PlayerInfoListener l)
	{
	fPlayerInfoSupport.removePlayerInfoListener(l);
	}
public void removePlayerInventoryListener(InventoryListener l)
	{
	fInventorySupport.removeInventoryListener(l);
	}
public void removePlayerMoveListener(PlayerMoveListener l)
	{
	fPlayerMoveSupport.removePlayerMoveListener(l);
	}
public void removePlayerStateListener(PlayerStateListener l)
	{
	fPlayerStateSupport.removePlayerStateListener(l);
	}
/**
 * Reset the color blend.
 * @author Brian Haskin
 */
protected void resetBlend()
	{
	fBlend.x = 0.0f;	// red
	fBlend.y = 0.0f;	// green
	fBlend.z = 0.0f;	// blue
	fBlend.w = 0.0f;	// alpha

	return;
	}
/**
 * Put a dead player back into the game
 */
protected void respawn()
	{
	// leave a corpse behind
	copyCorpse(fEntity, fHealth);
	
	clearSettings();
	
	// put a live body back into the game
	spawn();
	
	// add a teleportation effect
	Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_PLAYER_TELEPORT);
	
	// hold in place briefly
	fEntity.setPlayerPMTime((byte)50);
	}
/**
 * This method was created by a SmartGuide.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	switch (phase)
		{
		case Game.FRAME_BEGINNING:
			beginServerFrame();
			break;
			
		case Game.FRAME_END:
			endServerFrame();
			break;
		}
	}
/**
 * Set the current ammo count, or alter it by some amount.
 * @param amount int
 * @param isAbsolute is the amount an absolute value, or a relative one?
 */
public void setAmmoCount(int amount, boolean isAbsolute) 
	{
	if (fAmmo != null)
		{
		if (isAbsolute)
			fAmmo.fAmount = amount;
		else
			fAmmo.fAmount += amount;
			
		if (fAmmo.fAmount > fAmmo.fMaxAmount)
			fAmmo.fAmount = fAmmo.fMaxAmount;
			
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO, (short) fAmmo.fAmount);
		}
	}
/**
 * Set the ammo count, or alter it by some amount.
 * @param ammoType name of a kind of ammo
 * @param amount int
 * @param isAbsolute is the amount an absolute value, or a relative one?
 */
public void setAmmoCount(String ammoType, int amount, boolean isAbsolute) 
	{
	InventoryPack ip = fInventory.getPack(ammoType);
	if (ip == fAmmo)
		// we're changing the current ammo
		setAmmoCount(amount, isAbsolute);
	else
		{
		if (isAbsolute)
			ip.fAmount = amount;
		else
			ip.fAmount += amount;

		if (ip.fAmount > ip.fMaxAmount)
			ip.fAmount = ip.fMaxAmount;
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 */
public void setAmmoType(String ammoType) 
	{
	fAmmo = (ammoType == null ? null : fInventory.getPack(ammoType));	
	if (fAmmo == null)
		{
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO, (short) 0);
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO_ICON, (short) 0);
		}
	else
		{
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO, (short) fAmmo.fAmount);
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO_ICON, (short) (fAmmo.fIconName == null ? 0 : Engine.getImageIndex(fAmmo.fIconName)));
		}	
	}
/**
 * Set which animation cycle the player will run through.
 * @param animation one of the Player.ANIMATE_* constants.
 */
public void setAnimation(int animation) 
	{
	setAnimation(animation, false, 0);
	}
/**
 * Set which animation cycle the player will run through.
 * @param animation			one of the Player.ANIMATE_* constants.
 * @param ignorePriority	true if you want to force the player to start this new animation no matter what is already running, otherwise false.
 * @param frameOffset		frame offset for animation (?)
 */
public void setAnimation(int animation, boolean ignorePriority, int frameOffset) 
	{
	boolean ducking = ((fEntity.getPlayerPMFlags() & NativeEntity.PMF_DUCKED) != 0);
	
	// don't allow gestures when crouching
	if (ducking && (animation >= ANIMATE_FLIPOFF) && (animation <= ANIMATE_POINT))
		return;
		
	// if we're moving, then use a slightly different animation
	if ((animation == ANIMATE_NORMAL) && fIsRunning)
		animation += 1;
		
	// pain and death can have 3 variations...pick one randomly
	if ((animation == ANIMATE_PAIN) || (animation == ANIMATE_DEATH))
		animation += (GameUtil.randomInt() & 0x00ff) % 3;
		
	// use different animations when crouching		
	if ((animation < ANIMATE_FLIPOFF) && ducking)
		animation += 12;
		
	int newPriority = fAnims[animation * 3];
	if (ignorePriority || (newPriority >= fAnimationPriority))
		{
		fAnimationPriority = newPriority;
		int start = fAnims[(animation*3) + 1];
		int end = fAnims[(animation*3) + 2];
		fAnimationReversed = (end < start);
		if (fAnimationReversed)
			{
			setFrame(end + frameOffset);
			fAnimationEnd = start;
			}
		else
			{
			setFrame(start + frameOffset);
			fAnimationEnd = end;
			}
		}
	}
/**
 * Set the damage color blend for this frame.
 * @param x	Red value of damage color.
 * @param y Green value of damage color.
 * @param z Blue value of damage color.
 * @param w Alpha value of damage color.
 */
public void setDamageBlend(float x, float y, float z, float w) 
	{
	fDamageBlend.x = x;
	fDamageBlend.y = y;
	fDamageBlend.z = z;
	fDamageBlend.w = w;
	}
/**
 * Set how much damage should be scaled for this player. 
 *
 * @param f Damage multiplier..for example a quad would use the value 4.0
 */
public void setDamageMultiplier(float f) 
	{
	fDamageMultiplier = f;
	}
/**
 * This method was created by a SmartGuide.
 * @param n int
 */
public void setFrame(int n) 	// Should this be renamed to setAnimationFrame()? (TSW)
	{
	fAnimationFrame = n;
	fEntity.setFrame(n);
	}
/**
 * Set the alpha level for this frame, in the range 0.0 to 1.0.
 * @param f The level, 0.0 to 1.0, to set the alpha.
 */
public void setFrameAlpha(float f) 
	{
	fFrameAlpha = f;
	}
/**
 * Override baseq2.GameObject.setGravity() to do extra player stuff.
 * @param x float
 * @param y float
 * @param z float
 */
public void setGravity(float x, float y, float z) 
	{
	// Q2 only deals with player gravity along the Z axis
	super.setGravity(0, 0, z);
	
	fEntity.setPlayerGravity((short)(BaseQ2.gGravity * -fGravity.z));
	}
/**
 * This method was created by a SmartGuide.
 * @param val int
 */
public void setHealth(float val) 
	{
	fHealth = val;
	fEntity.setPlayerStat(NativeEntity.STAT_HEALTH, (short)fHealth);
	}
/**
 * Set the Player's max health amount
 */
public void setHealthMax(int maxCount)
	{
	fHealthMax = maxCount;
	}
/**
 * Sets this player's mass. Used in determining physics, knockback, etc.
 * @param	mass	The mass to set. Normal player mass is 200.
 */
public void setMass(int mass)
	{
	fMass = mass;
	}
/**
 * Set the maximum amount of a given type of ammo the player can carry.
 *
 * @param ammoName name of type of ammo
 * @param newMax
 */
public void setMaxAmmoCount(String ammoName, int newMax) 
	{
	InventoryPack p = fInventory.getPack(ammoName);
	
	if (p == null)
		{
		p = new InventoryPack(newMax, null); // new kind of ammo we haven't heard of I guess.
		fInventory.addPack(ammoName, p);
		}
	else
		p.fMaxAmount = newMax;
	}
/**
 * Put a value into the playerInfo hashtable.
 * @param key name of the value we're storing.
 * @para  value the value to store
 */
protected void setPlayerInfo(String key, String value) 
	{
	String oldVal = (String) fPlayerInfo.get(key);
	
	// update the current hashtable
	if (value == null)
		fPlayerInfo.remove(key);
	else
		fPlayerInfo.put(key, value);

	// notify about the change
	playerVariableChanged(key, oldVal, value);	
	}
/**
 * Set the player's score.
 */
public void setScore(int amount)
	{
	setScore(amount, true);
	}
/**
 * Set the player's score.
 */
public void setScore(int amount, boolean isAbsolute)
	{
	if (isAbsolute)
		fScore = amount;
	else
		fScore += amount;
		
	fEntity.setPlayerStat( NativeEntity.STAT_FRAGS, (short)fScore );
	}
/**
 * Associate the Player with some sort of "Team" object.
 * @param o java.lang.Object
 */
public void setTeam(Object o) 
	{
	fTeam = o;
	}
/**
 * VWeap support...show the right weapon to the rest of the world.
 */
public void showVWep() 
	{
	if (fIsDead)
		return;
		
	if (fWeapon == null)
		{
		fEntity.setModelIndex2(0);
		return;		
		}
	
	// ---- Old-style VWep support
/*			
	if (!baseq2.GameModule.isVWepOn())
		{
		fEntity.setModelIndex2(255);
		return;
		}
				
	String weaponIcon = fWeapon.getIconName();		
	if (weaponIcon == null)
		fEntity.setModelIndex2(255);
	else
		{
		String playerSkin = getPlayerInfo("skin");
		int p = playerSkin.indexOf('/');
		String weaponModel = "players/" + playerSkin.substring(0, p+1) + weaponIcon + ".md2";
		fEntity.setModelIndex2(Engine.getModelIndex(weaponModel));	
		}
*/
	// ---- New-style VWep support
	fEntity.setSkinNum((fEntity.getSkinNum() & 0x00ff) | (fWeapon.getVWepIndex() << 8));
	fEntity.setModelIndex2(255);
	}
/**
 * spawn the player into the game.
 */
protected void spawn() 
	{	
	GenericSpawnpoint spawnPoint = getSpawnpoint();
					
	if (spawnPoint == null)
		Game.dprint("Couldn't pick spawnpoint\n");
	else
		{							
		// set player origin
		Point3f origin = spawnPoint.getOrigin();
		origin.z += 9; // is 1 in C code?
		fEntity.setOrigin(origin);

		// set player angles
		Angle3f ang = spawnPoint.getAngles();
		fEntity.setAngles(0, ang.y, 0);
		fEntity.setPlayerViewAngles(0, ang.y, 0);
		
		ang.sub(fCmdAngles);
		fEntity.setPlayerDeltaAngles(ang);
		}
	
	// clear entity values
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setClipmask(Engine.MASK_PLAYERSOLID);	
	fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_DEADMONSTER);
	fEntity.setPlayerPMType(NativeEntity.PM_NORMAL);	
	fEntity.setMins(-16, -16, -24);  // James Bielby found a typo here, used to be +24, screwed up barryp.testbot
	fEntity.setMaxs(16, 16, 32);
	
	// clear entity state values
	fEntity.setEffects(0);
	fEntity.setSkinNum(fEntity.getPlayerNum());
	fEntity.setModelIndex(255);	// will use the skin specified model
	showVWep();

	killBox();
	fEntity.linkEntity();			

	// restore the player's FOV, it's changed at intermission, this should 
	// set it back to the player's preference 
	String fov = (String) fPlayerInfo.get("fov");
	try
		{
		if (fov != null)
			fEntity.setPlayerFOV(Float.valueOf(fov).floatValue());
		}
	catch (Exception e)
		{
		// could be that the fov parameter is not a valid float
		fEntity.cprint(Engine.PRINT_HIGH, e.getMessage());
		}
		
	closeDisplay();
	
	// let interested objects know the player spawnned.
	fPlayerStateSupport.fireEvent(this, PlayerStateEvent.STATE_SPAWNED, BaseQ2.gWorld);	
	gPlayerStateSupport.fireEvent(this, PlayerStateEvent.STATE_SPAWNED, BaseQ2.gWorld);	
	}
/**
 * Switch the player into intermission mode.  Their view should be from
 * a specified intermission spot, their movement should be frozen, and the 
 * scoreboard displayed.
 *
 * @param intermissionSpot The spot the player should be moved do.
 */
public void startIntermission() 
	{
	GenericSpawnpoint intermissionSpot = getIntermissionSpot();
	
	fEntity.setOrigin(intermissionSpot.getOrigin());
	fEntity.setAngles(intermissionSpot.getAngles());
	fEntity.setPlayerViewAngles(intermissionSpot.getAngles());
	fEntity.setPlayerPMType(NativeEntity.PM_FREEZE);
	fEntity.setPlayerRDFlags(0);
	fEntity.setModelIndex(0);
	fEntity.setModelIndex2(0);
	fEntity.setModelIndex3(0);
	fEntity.setModelIndex4(0);
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setPlayerGunIndex(0);	
	fEntity.setPlayerBlend(0, 0, 0, 0);
	fEntity.setPlayerFOV(90);
	fEntity.setEffects(0);
	fEntity.setSound(0);
	fEntity.setGroundEntity(null);
	fEntity.linkEntity();

	// notify objects we're changing levels
	//	notifyPlayerStateListeners(PlayerStateListener.PLAYER_LEVELCHANGE);	
	fPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_SUSPENDEDSTART, 
					     BaseQ2.gWorld );
	gPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_SUSPENDEDSTART, 
					     BaseQ2.gWorld );

	writeDeathmatchScoreboardMessage(null);
	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);	
	fShowScore = true;
	fInIntermission = true;
	}
/**
 * Teleport the player to another point in the map.
 * @param origin javax.vecmath.Point3f
 * @param angles q2java.Angle3f
 */
public void teleport(Point3f origin, Angle3f angles) 
	{
	// notify objects we're drastically changing position
	fPlayerStateSupport.fireEvent( this, PlayerStateEvent.STATE_TELEPORTED, this );
	
	// unlink to make sure it can't possibly interfere with KillBox
	fEntity.unlinkEntity();	

	fEntity.setOrigin(origin);

	// clear the velocity and hold them in place briefly
	fEntity.setVelocity(0, 0, 0);
	fEntity.setPlayerPMTime((byte)20);	// hold time 160ms (20 * 8)
	fEntity.setPlayerPMFlags((byte)(fEntity.getPlayerPMFlags() | NativeEntity.PMF_TIME_TELEPORT));

	// draw the teleport splash at source and on the player
	Game.getSoundSupport().fireTempEvent(fEntity, NativeEntity.EV_PLAYER_TELEPORT);

	// set angles
	angles.sub(fCmdAngles);
	fEntity.setPlayerDeltaAngles(angles);

	fEntity.setAngles(0, 0, 0);
	fEntity.setPlayerViewAngles(0, 0, 0);

	killBox();	
	fEntity.linkEntity();
	}
/**
 * This method was created by a SmartGuide.
 */
public void touchTriggers() 
	{
	if (fIsDead)
		return;
		
	// notify all the triggers we're intersecting with
	NativeEntity[] triggers = fEntity.getBoxEntities(Engine.AREA_TRIGGERS);
	if (triggers != null)
		{
		for (int i = 0; i < triggers.length; i++)
			{
			Object obj = triggers[i].getReference();
			if (obj instanceof GameObject)
				((GameObject)obj).touch(this);
			}
		}
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

	// don't let problems with resource bundles blow the connection
	try
		{
		Object[] args = {getName()};
		Game.localecast("q2java.baseq2.Messages", "entered", args, Engine.PRINT_HIGH);
		fEntity.centerprint(WelcomeMessage.getMessage());
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * This method was created by a SmartGuide.
 */
protected void worldEffects() 
	{
	int oldWaterLevel = fOldWaterLevel;
	fOldWaterLevel = fWaterLevel;
	
	//
	// if just entered a water volume, play a sound
	//
	if ((fWaterLevel != 0) && (oldWaterLevel == 0))
		{
		if ((fWaterType & Engine.CONTENTS_LAVA) != 0)
			Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/lava_in.wav"), 1, NativeEntity.ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_SLIME) != 0)
			Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_in.wav"), 1, NativeEntity.ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_WATER) != 0)
			Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_in.wav"), 1, NativeEntity.ATTN_NORM, 0);			
		}

	//
	// if just completely exited a water volume, play a sound
	//
	if ((fWaterLevel == 0) && (oldWaterLevel != 0))
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_out.wav"), 1, NativeEntity.ATTN_NORM, 0);

	//
	// check for head just going under water
	//
	if ((fWaterLevel == 3) && (oldWaterLevel != 3))
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_un.wav"), 1, NativeEntity.ATTN_NORM, 0);

	//
	// check for head just coming out of water
	//
	if ((fWaterLevel != 3) && (oldWaterLevel == 3))
		Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/gasp2.wav"), 1, NativeEntity.ATTN_NORM, 0);

	// generate a zero vector for use in damage funcs	
	Vector3f origin = Q2Recycler.getVector3f();
	origin.set(0, 0, 0);

	//
	// check for drowning
	//
	if (fWaterLevel != 3)
		// reset breath counter and restore normal drowning damage level
		breath(12, true);
	else
		{	
		// if out of air, start drowning
		if (fAirFinished < Game.getGameTime())
			{	// drown!
			if ((fNextDrownTime < Game.getGameTime()) && (fHealth > 0))
				{
				fNextDrownTime = Game.getGameTime() + 1;

				// take more damage the longer underwater
				fDrownDamage += 2;
				if (fDrownDamage  > 15)
					fDrownDamage = 15;

				// play a gurp sound instead of a normal pain sound
				if (fHealth <= fDrownDamage)
					Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/drown1.wav"), 1, NativeEntity.ATTN_NORM, 0);
				else if ((GameUtil.randomInt() & 1) != 0)
					Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("*gurp1.wav"), 1, NativeEntity.ATTN_NORM, 0);
				else
					Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_VOICE, Engine.getSoundIndex("*gurp2.wav"), 1, NativeEntity.ATTN_NORM, 0);

				fPainDebounceTime = Game.getGameTime();

				damage(BaseQ2.gWorld, BaseQ2.gWorld, origin, fEntity.getOrigin(), origin, fDrownDamage, 0, DAMAGE_NO_ARMOR, Engine.TE_NONE, "water");
				}
			}
		}

		
	//
	// check for sizzle damage
	//
	if ((fWaterLevel != 0) && ((fWaterType & Engine.CONTENTS_LAVA) != 0))
		damage(BaseQ2.gWorld, BaseQ2.gWorld, origin, fEntity.getOrigin(), origin, 3* fWaterLevel, 0, 0, Engine.TE_NONE, "lava");

	if ((fWaterLevel != 0) && ((fWaterType & Engine.CONTENTS_SLIME) != 0))
		damage(BaseQ2.gWorld, BaseQ2.gWorld, origin, fEntity.getOrigin(), origin, fWaterLevel, 0, 0, Engine.TE_NONE, "slime");

	// put back the Vector we borrowed
	Q2Recycler.put(origin);
	}
/**
 * Build up a layout string that displays the current score and write it
 * out to this player.
 * @param killer q2jgame.Player
 */
protected void writeDeathmatchScoreboardMessage(GameObject killer) 
	{
	int i;
		
	// get a vector
	Vector players = Q2Recycler.getVector();
	
	// generate a list of players sorted by score	
	Enumeration enum = Player.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		boolean isInserted = false;
		for (i = 0; i < players.size(); i++)
			{
			Player p2 = (Player) players.elementAt(i);
			if (p.fScore > p2.fScore)
				{
				players.insertElementAt(p, i);
				isInserted = true;
				break;
				}
			}	
		if (!isInserted)
			players.addElement(p);				
		}	

	int playerCount = players.size();
	if (playerCount > 12)
		playerCount = 12;		

	// get a couple StringBuffers from the Recycler
	StringBuffer sb = Q2Recycler.getStringBuffer();
	StringBuffer sb2 = Q2Recycler.getStringBuffer();		

	for (i = 0; i < playerCount; i++)
		{
		int x = (i >= 6) ? 160 : 0;		// column
		int y = (32 * (i % 6)) + 32;	// row
		Player p = (Player) players.elementAt(i);
		
		// add a dogtag to the player and his killer		
		if ((p == this) || (p == killer))
			{
			sb2.setLength(0);
			sb2.append("xv ");
			sb2.append(x + 32);
			sb2.append(" yv ");
			sb2.append(y);
			if (p == this)
				sb2.append(" picn tag1 ");
			else
				sb2.append(" picn tag2 ");

			// add dogtag string only if it doesn't overflow the layout string
			if ((sb.length() + sb2.length()) < 1024)
				sb.append(sb2.toString());				
			}
						

		// build the players scrore string
		sb2.setLength(0);
		sb2.append("client ");
		sb2.append(x);
		sb2.append(' ');
		sb2.append(y);
		sb2.append(' ');
		sb2.append(p.fEntity.getPlayerNum());
		sb2.append(' ');
		sb2.append(p.fScore);
		sb2.append(' ');
		sb2.append(p.fEntity.getPlayerPing());
		sb2.append(' ');
		sb2.append((int)((Game.getGameTime() - p.fStartTime) / 60));
		sb2.append(' ');
		
		// the last 0 should really be the number of minutes the player's been in the game

		// add particular player only if it doesn't overflow layout string
		if ((sb.length() + sb2.length()) < 1024)
			sb.append(sb2.toString());		
		}
				
	Engine.writeByte(Engine.SVC_LAYOUT);
	Engine.writeString(sb.toString());

	// Be nice, put stuff back into the Recycler
	Q2Recycler.put(sb);
	Q2Recycler.put(sb2);
	Q2Recycler.put(players);
	}
}