package baseq2;

import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;

import baseq2.spawn.*;

/**
 * Player objects are subclassed from GameObject, but also
 * implement the methods necessary for PlayerListener.  
 *
 * @author Barry Pederson
 */

public class Player extends GameObject implements FrameListener, PlayerListener, LocaleListener, CrossLevel
	{	
	protected transient ResourceGroup fResourceGroup;
	
	private int fScore;
	protected float fStartTime;

	private int fHealth;
	private int fHealthMax;
	protected boolean fIsFemale;
	protected boolean fInIntermission;
	
	private float fDamageMultiplier;
		
	protected Hashtable fPlayerInfo;
	protected InventoryTracker fInventory;
	protected Vector fWeaponOrder;
	protected Vector fWeaponsExcluded;

	// Armor 
	private int fArmorCount;
	private int fArmorMaxCount;
	/** what fraction of normal damage the player's armor absorbs. */
	protected float fArmorProtection;
	/** fraction of energy damage the player's armor absorbs. */
	protected float fArmorEnergyProtection;

	/** have we given the current weapon a chance to think yet this frame? */
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
	public int fButtons; 
	public int fLatchedButtons;	
	protected int fOldButtons;
	protected Angle3f fCmdAngles;
	public float fViewHeight;
	protected int fWaterType;
	protected int fWaterLevel;
	protected int fOldWaterLevel;
	protected Vector3f fOldVelocity;
	/** The mass of this Player. Use setMass(int) to set.*/
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

	// ------- Public constants ---------------------------------	

	public final static Color4f COLOR_LAVA  = new Color4f(1.0f, 0.3f, 0.0f, 0.6f);
	public final static Color4f COLOR_SLIME = new Color4f(0.0f, 0.1f, 0.05f, 0.6f);
	public final static Color4f COLOR_WATER = new Color4f(0.5f, 0.3f, 0.2f, 0.4f);
	// These colors refer to fFrameAlpha for their alpha, so they only have 3 floats (rgb). 
	/** The color used for Power Armor blends. */
	public final static Color3f COLOR_GREEN = new Color3f(0.0f, 1.0f, 0.0f);
	/** The color used for regular Armor blends. */
	public final static Color3f COLOR_WHITE = new Color3f(1.0f, 1.0f, 1.0f);
	/** The color used for blood blends. */
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
public Player(NativeEntity ent, boolean loadgame) throws GameException
	{
	Engine.debugLog("baseq2.Player.<ctor>(" + ent + ", " + loadgame + ")");

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
	Game.addFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END, 0, 0);
	
	// sign up to receive broadcast messages using the default locale
	fResourceGroup = Game.addLocaleListener(this);
		
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
 * This method was created by a SmartGuide.
 * @param amount int
 * @param maxAmount int
 * @param protection float
 * @param energyProtection float
 */
protected boolean addArmor(GenericArmor armor) 
	{
	int amount = armor.getArmorValue();
	int maxAmount = armor.getArmorMaxValue();
	
	// handle shards differently
	if (maxAmount == 0) 
		{
		if (fArmorCount == 0)
			{
			fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) Engine.getImageIndex(armor.getIconName()));
			fArmorMaxCount = 50;
			fArmorProtection = 0.3F;			
			}
		fArmorCount += amount;
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		return true;
		}

	// is this an armor upgrade?
	float protection = armor.getProtectionFactor();
	if (protection > fArmorProtection)
		{ 
		int salvage = (int)((fArmorProtection / protection) * fArmorCount);
		fArmorCount = amount + salvage;
		fArmorMaxCount = maxAmount;
		fArmorProtection = protection;
		fArmorEnergyProtection = armor.getEnergyProtectionFactor();		
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) Engine.getImageIndex(armor.getIconName()));
		return true;
		}		

	// is our armor not up to capacity?		
	if (fArmorCount < fArmorMaxCount)		
		{
		fArmorCount = Math.min(fArmorMaxCount, fArmorCount + amount);
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		return true;
		}		

	// guess we don't need this armor
	return false;		
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
/**
 * Add an item to the player's inventory.
 * @param item what we're trying to add.
 * @return boolean true if the item was taken.
 */
public boolean addItem(GenericItem item) 
	{
	if (item == null)
		return false;

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
		return addArmor((GenericArmor)item);
		
	// don't know what to do with the item, so reject it.
	return false;
	}
/**
 * Add an actual instance of a weapon to a player's inventory.
 * @param weapon baseq2.GenericWeapon
 * @param allowSwitch boolean
 */
protected boolean addWeapon(GenericWeapon w, boolean allowSwitch) 
	{
	boolean weaponStay = GameModule.isDMFlagSet(GameModule.DF_WEAPONS_STAY);

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
 * Add a class of weapon to a player's inventory.
 * @param weaponClassSuffix class of the weapon, either a whole classname or a suffix.
 * @param allowSwitch Have the player switch weapons if they're currently using just a blaster.
 * @return boolean true if the player took the weapon (or its ammo)
 */
protected boolean addWeapon(String weaponClassSuffix, boolean allowSwitch) 
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
 * Handle the beginning of a server frame.
 */
protected void beginServerFrame()
	{
	if (fInIntermission)
		return;
		
	if (fIsDead && (Game.getGameTime() > fRespawnTime) && ((fLatchedButtons != 0) || GameModule.isDMFlagSet(GameModule.DF_FORCE_RESPAWN)))
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
 * Announce this player's death to the world.
 * @param attacker baseq2.GameObject the responsible party
 */
public void broadcastObituary(GameObject attacker, String obitKey) 
	{
	Object[] args = {getName(), new Integer(isFemale() ? 1 : 0), (attacker instanceof Player ? ((Player)attacker).getName() : null)};
	if (attacker == this)
		obitKey = "self_" + obitKey;
		
	if (Game.isResourceAvailable("baseq2.Messages", obitKey))		
		Game.localecast("baseq2.Messages", obitKey, args, Engine.PRINT_MEDIUM);	
	else
		{
		// must be some new kind of obitKey, use a default death message
		if (attacker == this)
			Game.localecast("baseq2.Messages", "self_died", args, Engine.PRINT_MEDIUM);	
		else
			Game.localecast("baseq2.Messages", "died", args, Engine.PRINT_MEDIUM);				
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

	// We reset the blend first
	fBlend.x = 0.0f;	// red
	fBlend.y = 0.0f;	// green
	fBlend.z = 0.0f;	// blue
	fBlend.w = 0.0f;	// alpha

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

/*	// add for powerups
	if (ent->client->quad_framenum > level.framenum)
	{
		remaining = ent->client->quad_framenum - level.framenum;
		if (remaining == 30)	// beginning to fade
			gi.sound(ent, CHAN_ITEM, gi.soundindex("items/damage2.wav"), 1, ATTN_NORM, 0);
		if (remaining > 30 || (remaining & 4) )
			SV_AddBlend (0, 0, 1, 0.08, ent->client->ps.blend);
	}
	else if (ent->client->invincible_framenum > level.framenum)
	{
		remaining = ent->client->invincible_framenum - level.framenum;
		if (remaining == 30)	// beginning to fade
			gi.sound(ent, CHAN_ITEM, gi.soundindex("items/protect2.wav"), 1, ATTN_NORM, 0);
		if (remaining > 30 || (remaining & 4) )
			SV_AddBlend (1, 1, 0, 0.08, ent->client->ps.blend);
	}
	else if (ent->client->enviro_framenum > level.framenum)
	{
		remaining = ent->client->enviro_framenum - level.framenum;
		if (remaining == 30)	// beginning to fade
			gi.sound(ent, CHAN_ITEM, gi.soundindex("items/airout.wav"), 1, ATTN_NORM, 0);
		if (remaining > 30 || (remaining & 4) )
			SV_AddBlend (0, 1, 0, 0.08, ent->client->ps.blend);
	}
	else if (ent->client->breather_framenum > level.framenum)
	{
		remaining = ent->client->breather_framenum - level.framenum;
		if (remaining == 30)	// beginning to fade
			gi.sound(ent, CHAN_ITEM, gi.soundindex("items/airout.wav"), 1, ATTN_NORM, 0);
		if (remaining > 30 || (remaining & 4) )
			SV_AddBlend (0.4, 1, 0.4, 0.04, ent->client->ps.blend);
	}
*/
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
			fEntity.setEvent(NativeEntity.EV_FOOTSTEP);
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
	
	value = GameModule.gRollAngle.getFloat();

	if (side < GameModule.gRollSpeed.getFloat())
		side = side * value / GameModule.gRollSpeed.getFloat();
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
	Vector3f forward = new Vector3f();
	Vector3f right = new Vector3f();
	angle.getVectors( forward, right, null );
	delta = (fEntity.getVelocity()).dot(forward);
	angles.x += delta*GameModule.gRunPitch.getFloat();

	delta = (fEntity.getVelocity()).dot(right);
	angles.z += delta*GameModule.gRunRoll.getFloat();

	// add angles based on bob
	delta = fBobFracSin * GameModule.gBobPitch.getFloat() * fXYSpeed;
	if (fIsDucking)
			delta *= 6;             // crouching
	angles.x += delta;
	delta = fBobFracSin * GameModule.gBobRoll.getFloat() * fXYSpeed;
	if (fIsDucking)
			delta *= 6;             // crouching
	if ((fBobCycle & 1) > 0)
			delta = -delta;
	angles.z += delta;
	fEntity.setPlayerKickAngles(angles);
	

	// add bob height
	float bob = fBobFracSin * fXYSpeed * GameModule.gBobUp.getFloat(); // *3 added to magnify effect
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
 * This method was created by a SmartGuide.
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
	fArmorCount = 0;
	fArmorMaxCount =  0;
	fArmorProtection = 0; // almost, but not quite as good as jacket_armor
	fArmorEnergyProtection = 0.0F;
	fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) 0);


	// set various other bits to a normal setting
	fIsDead = false;		
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
 * This allows us to test different blend modes.
 *
 * @param args java.lang.String[]
 */
 
public void cmd_debug_setblend(String[] args)
	{
	if (args.length != 5)
		{
		fEntity.cprint(Engine.PRINT_HIGH, "Usage: debug_setblend <red> <green> <blue> <alpha>\n");
		return;
		}
	try
		{
		fEntity.setPlayerBlend(Float.valueOf(args[1]).floatValue(), 
							   Float.valueOf(args[2]).floatValue(), 
							   Float.valueOf(args[3]).floatValue(), 
							   Float.valueOf(args[4]).floatValue());
		}
	catch (NumberFormatException nfe)
		{
		fEntity.cprint(Engine.PRINT_HIGH, nfe.toString());
		}
	}
/**
 * Drop an item in our inventory.
 */
public void cmd_drop(String[] args) 
	{
	// build up the name of the item
	String itemName = args[1];
	for (int i = 2; i < args.length; i++)
		itemName = itemName + " " + args[i];
	itemName = itemName.toLowerCase();

	// get it from our inventory
	InventoryPack ip = fInventory.getPack(itemName);

	// empty handed
	if ((ip == null) || (ip.fAmount < 1))
		{
		Object[] msgArgs = {itemName};
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("baseq2.Messages", "dont_have", msgArgs) + "\n");
		return;
		}		

	// deal with dropping GenericItems
	if (ip.fItem instanceof GenericItem)
		{
		GenericItem item = (GenericItem) ip.fItem;

		// check whether the item is willing
		if (!item.isDroppable())
			{
			fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("baseq2.Messages", "item_not_droppable", null) + "\n");
			return;
			}

		// handle the subcase of GenericWeapons
		if (item instanceof GenericWeapon)
			{
			// don't let them drop their current weapon if its the only one they have
			if ((item == fWeapon) && (ip.fAmount < 2))
				{
				fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("baseq2.Messages", "no_current_weapon_drop", null) + "\n");
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
 * @param args java.lang.String[]
 */
public void cmd_fov(String[] args) 
	{
	if (args.length < 1)
		{
		fEntity.cprint(Engine.PRINT_HIGH, "cmd_fov() called with no arguments\n");
		return;
		}
		
	int i = Integer.parseInt(args[1]);
	
	// limit to reasonable values
	if (i < 1)
		i = 90;
	if (i > 160)
		i = 160;

	fEntity.setPlayerFOV(i);				
	}
/**
 * Tell the player about the game.
 * @param args java.lang.String[]
 */
public void cmd_gameversion(String[] args) 
	{
	fEntity.cprint(Engine.PRINT_HIGH, GameModule.getVersion() + "\n");
	}
/**
 * Give the player one of each weapon.  Good for debugging.
 * @param args java.lang.String[]
 */
public void cmd_giveall(String[] args)
	{
	if (!baseq2.GameModule.isCheating())
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
	}
/**
 * Draw the help computer.
 * Only works for drawing the scoreboard in a deathmatch right now.
 * @param args java.lang.String[]
 */
public void cmd_help(String[] args)
	{
 	if (GameModule.gIsDeathmatch)
  		cmd_score(args);
	}
/**
 * Identify the player in your crosshairs.
 * @param args java.lang.String[]
 */
public void cmd_id(String[] args) 
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
 * @param args java.lang.String[]
 */
public void cmd_iddebug(String[] args) 
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
 * @param args java.lang.String[] - not used.
 */
public void cmd_inven(String[] args)
	{
	StringBuffer sb = new StringBuffer();
	
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
	
	return;
	}
/**
 * Do-nothing placeholder to make things smoother
 * for mods that use menus like CTF and NWRA
 * @param args java.lang.String[] - not used.
 */
public void cmd_invnext(String[] args)
	{
	}
/**
 * Do-nothing placeholder to make things smoother
 * for mods that use menus like CTF and NWRA
 * @param args java.lang.String[] - not used.
 */
public void cmd_invprev(String[] args)
	{
	}
/**
 * Do-nothing placeholder to make things smoother
 * for mods that use menus like CTF and NWRA
 * @param args java.lang.String[] - not used.
 */
public void cmd_invuse(String[] args)
	{
	}
/**
 * Suicide.
 * @param args java.lang.String[]
 */
public void cmd_kill(String[] args) 
	{
	if (fIsDead)
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.getBundle("baseq2.Messages").getString("already_dead") + "\n");
	else		
		die(this, this, 0, fEntity.getOrigin(), "suicide");
	}
/**
 * Put's away the any special screens that are currently displayed, e.g. scoreboard, inventory or help computer.
 * when the special screen is displayed and the esc key is hit this cmd is called
 * @param args java.lang.String[]
 */
public void cmd_putaway(String[] args)
	{
		closeDisplay();
	}
/**
 * Send a chat message to all players.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say(String[] args) 
	{
	String msg = Engine.getArgs();
	
	// remove any quote marks
	if (msg.charAt(msg.length()-1) == '"')
		msg = msg.substring(msg.indexOf('"')+1, msg.length()-1);
		
	say(msg);
	}
/**
 * Treat "say_team" the same as "say" for now.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say_team(String[] args) 
	{
	String msg = Engine.getArgs();
	
	// remove any quote marks
	if (msg.charAt(msg.length()-1) == '"')
		msg = msg.substring(msg.indexOf('"')+1, msg.length()-1);
		
	sayTeam(msg);
	}
/**
 * Display the scoreboard.
 * @param args java.lang.String[]
 */
public void cmd_score(String[] args)
	{
	// needs to check for coop mode
	// --FIXME--
	if (!GameModule.gIsDeathmatch)
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
 * @param args java.lang.String[]
 */
public void cmd_use(String[] args) 
	{
	String itemName = args[1];
	for (int i = 2; i < args.length; i++)
		itemName = itemName + " " + args[i];
	
	Object ent = fInventory.get(itemName.toLowerCase());
	if (ent == null)
		{
		Object[] msgArgs = {itemName};
		fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("baseq2.Messages", "dont_have", msgArgs) + "\n");
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
			Object[] msgArgs = {itemName};
			fEntity.cprint(Engine.PRINT_HIGH, fResourceGroup.format("baseq2.Messages", "no_ammo", msgArgs) + "\n");
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
			
//	ent.use(this);	---FIXME--
	}
/**
 * Invoke player gestures.
 * @param args java.lang.String[]
 */
public void cmd_wave(String[] args) 
	{
	int i = 0;
		
	if (args.length > 1)
		i = Integer.parseInt(args[1]);
			
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
 * @param args java.lang.String[] - list of weapons
 */
public void cmd_weapaddexcluded(String[] args)
	{
	for (int i=1; i < args.length; i++)
		fWeaponsExcluded.addElement(args[i]);
	
	return;
	}
/**
 * Switch to the last weapon used
 * @param args java.lang.String[] - not used.
 */
public void cmd_weaplast(String[] args)
	{
	if ((fLastWeapon != null) && (fLastWeapon.isEnoughAmmo()))
		{
		fNextWeapon = fLastWeapon;
		fWeapon.deactivate();
		}
	 }
/**
 * Switch to the next available weapon.
 * @param args java.lang.String[] - not used.
 */
public void cmd_weapnext(String[] args) 
	{
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
 * @param args java.lang.String[] - not used.
 */
public void cmd_weapprev(String[] args) 
	{
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
 * Print the excluded weapons list.
 * @param args java.lang.String[] - not used.
 */
public void cmd_weapprintexcluded(String[] args)
	{
	fEntity.cprint(Engine.PRINT_HIGH, "Weapons Excluded: ");
	
	for (int i=0; i < fWeaponsExcluded.size(); i++)
		{
		fEntity.cprint(Engine.PRINT_HIGH, fWeaponsExcluded.elementAt(i) + ", ");
		}
	
	fEntity.cprint(Engine.PRINT_HIGH, ".\n");
	
	return;
	}
/**
 * Print the weapon order list.
 * @param args java.lang.String[] - not used.
 */
public void cmd_weapprintorder(String[] args)
	{
	fEntity.cprint(Engine.PRINT_HIGH, "Weapon Order: ");
	
	for (int i=0; i < fWeaponOrder.size(); i++)
		{
		fEntity.cprint(Engine.PRINT_HIGH, fWeaponOrder.elementAt(i) + ", ");
		}
		
	fEntity.cprint(Engine.PRINT_HIGH, ".\n");
	
	return;
	}
/**
 * remove a list of weapons from the excluded list.
 * @author Brian Haskin
 * @param args java.lang.String[] - list of weapons
 */
public void cmd_weapremoveexcluded(String[] args)
	{
	for (int i=1; i < args.length; i++)
		{
		if (fWeaponsExcluded.contains(args[i]))
			fWeaponsExcluded.removeElementAt(fWeaponsExcluded.indexOf(args[i]));
		}
		
	return;
	}
/**
 * set the weapon switching order. If called without
 * arguments it will reset the order to the default id order.
 * Also, any weapons that have already been picked up but are not
 * explicitly listed will not be added to the list till the next respawn.
 * @author Brian Haskin
 * @param args java.lang.String[] - list of weapons
 */
public void cmd_weapsetorder(String[] args)
	{
	fWeaponOrder.removeAllElements();
	
	if (args.length > 1)
		{
		for (int i=1; i < args.length; i++)
			fWeaponOrder.addElement(args[i].toLowerCase());
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
	
	return;
	}
/**
 * Handle a new connection by just creating a new Player object 
 * and associating it with the player entity.
 * @param ent q2java.NativeEntity
 * @param playerInfo java.lang.String
 * @param loadgame boolean
 */
public static void connect(NativeEntity ent, boolean loadgame) throws GameException
	{
	Engine.debugLog("baseq2.Player.connect(" + ent + ", " + loadgame + ")");
	new Player(ent, loadgame);
	}
/**
 * Inflict damage on the Player. 
 * If the player takes enough damage, this function will call the Player.die() function.
 * @param inflictor the entity that's causing the damage, such as a rocket.
 * @param attacker the entity that's gets credit for the damage, for example the player who fired the above example rocket.
 * @param dir the direction the damage is coming from.
 * @param point the point where the damage is being inflicted.
 * @param normal q2java.Vec3
 * @param damage how much damage the player is being hit with.
 * @param knockback how much the player should be pushed around because of the damage.
 * @param dflags flags indicating the type of damage, corresponding to GameEntity.DAMAGE_* constants.
 */
public void damage(GameObject inflictor, GameObject attacker, 
	Vector3f dir, Point3f point, Vector3f normal, 
	int damage, int knockback, int dflags, int tempEvent, String obitKey) 
	{

	int armorSave = 0;		// for calculating blends later (TSW)
	int powerArmorSave = 0;	// for calculating blends later (TSW)
	int takeDamage = 0;		// for calculating blends later (TSW)
	
	// don't take any more damage if already dead
	// but spray a little blood for kicks
	if (fIsDead)
		{
//		spawnDamage(Engine.TE_BLOOD, point, normal, damage);
		return;
		}

	// knock the player around
	knockback(attacker, dir, knockback, dflags);

	// decrease damage based on armor
	if ((dflags & DAMAGE_NO_ARMOR) == 0)
		{
		int save; // the amount of damage our armor protects us from
		if ((dflags & DAMAGE_ENERGY) != 0)
			{
			save = (int) Math.ceil(damage * fArmorEnergyProtection);
			powerArmorSave = save;
			}
		else
			{
			save = (int) Math.ceil(damage * fArmorProtection);			
			armorSave = save;
			}
			
		save = Math.min(save, fArmorCount);
		
		// FIXME: Do we need to do any adjustments here for armorSave or
		// powerArmorSave based on fArmorCount? (TSW)
		takeDamage = damage;				// for blends (TSW)

		if (save > 0)
			{
			damage -= save;
			takeDamage -= armorSave;		// for blends (TSW)
			takeDamage -= powerArmorSave;	// for blends (TSW)
			fArmorCount -= save;
			fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
			if (fArmorCount == 0) // if armor is depleted we need to reset it
				{
				fArmorProtection = 0.3F;
				fArmorEnergyProtection = 0.0F;
				fArmorMaxCount = 50;
				fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) 0);
				}
			spawnDamage(Engine.TE_SPARKS, point, normal, save);
			}	
		}
	
	if (damage <= 0)
		return;
		
	// damaging a player causes a blood spray
	spawnDamage(Engine.TE_BLOOD, point, normal, damage);

	// also spawn explosive damage
	if ((tempEvent == Engine.TE_ROCKET_EXPLOSION) || (tempEvent == Engine.TE_ROCKET_EXPLOSION_WATER))
		spawnDamage(tempEvent, point, normal, damage);

	
	setHealth(fHealth - damage);
	if (fHealth <= 0)
		die(inflictor, attacker, damage, point, obitKey);
	
	// These are used to determine the blend for this frame. (TSW)
	fDamageBlood += takeDamage;
	fDamageArmor += armorSave;
	fDamagePArmor += powerArmorSave;
	
	// For view angle kicks this frame. (TSW)
	fDamageKnockback += knockback;
	float[] f = {0,0,0};
	point.get(fDamageFrom);	// set fDamageFrom to value of point
	}
/** 
 * Handles color blends and view kicks
 * for this Player's damage this frame.
 * Called during endServerFrame().
 */
protected void damageFeedback()
{
	Color3f		color;
	Vector3f	v;
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
	if (Game.getGameTime() > fPainDebounceTime /* && !(player->flags & FL_GODMODE) && (client->invincible_framenum <= level.framenum)*/)
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
		fEntity.sound(NativeEntity.CHAN_VOICE, getSexedSoundIndex( "pain" + Integer.toString(n) + "_" + Integer.toString((1 + ((int)Math.random() & 1))) ), 1, NativeEntity.ATTN_NORM, 0);
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

		v = new Vector3f();

		v.sub(fDamageFrom, fEntity.getOrigin());
		v.normalize();
		
		side = v.dot(fRight);
		fDamageRoll = kick*side*0.3f;
		
		side = -v.dot(fForward);
		fDamagePitch = kick*side*0.3f;

		fDamageTime = Game.getGameTime() + DAMAGE_TIME;
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
 * This method was created by a SmartGuide.
 */
protected void die(GameObject inflictor, GameObject attacker, int damage, Point3f point, String obitKey)
	{
	Vector3f forward = new Vector3f();
	if (fIsDead)
		return;	// already dead
		
	fKiller = attacker;
	
	fIsDead = true;

	// figure the soonest time the player will be allowed to respawn
	fRespawnTime = (float)(Game.getGameTime() + 1);  
	
	// broadcast a message announcing our death
	broadcastObituary(attacker, obitKey);
	
	// let the attacker know he killed us
	if (attacker != null)
		attacker.registerKill(this);
	else
		// doh! killed by something else
		registerKill(null);
		
	fEntity.setModelIndex2(0); // remove linked weapon model
	fEntity.setSound(0);
	
	Point3f maxs = fEntity.getMaxs();
	fEntity.setMaxs(maxs.x, maxs.y, maxs.z - 8);
	
//	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_DEADMONSTER);

	// drop some things from the inventory onto the ground.
	dropInventory();

	// remove the weapon from the POV
	fEntity.setPlayerGunIndex(0);
	fWeapon = null;
	
	fEntity.setPlayerPMType(NativeEntity.PM_DEAD);
	fKillerYaw = calcAttackerYaw(inflictor, attacker);
	writeDeathmatchScoreboardMessage(attacker);
	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);
	fShowScore = true;
	
	if (fHealth < -40)
		{	// gib
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("misc/udeath.wav"), 1, NativeEntity.ATTN_NORM, 0);
//		for (n= 0; n < 4; n++)
//			ThrowGib (self, "models/objects/gibs/sm_meat/tris.md2", damage, GIB_ORGANIC);
//		ThrowClientHead (self, damage);

//		self->takedamage = DAMAGE_NO;

		// workaround until full gib code is implemented
		setAnimation(ANIMATE_DEATH);
		}
	else
		{	// normal death
		setAnimation(ANIMATE_DEATH);
		fEntity.sound(NativeEntity.CHAN_VOICE, getSexedSoundIndex("death"+((Game.randomInt() & 0x03) + 1)), 1, NativeEntity.ATTN_NORM, 0);
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
	Game.removeLocaleListener(this);
	Game.removeFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END);	
	fEntity.setReference(null);
	fEntity.setPlayerListener(null);
	fEntity = null;
	}
/**
 * Drop the player's inventory onto the ground, usually called on death.
 * A good thing to override if you want to drop more or fewer things
 * in a mod.
 */
protected void dropInventory() 
	{
	// drop the current weapon along with some ammo if possible
	if ((fWeapon != null) && fWeapon.isDroppable())
		{
		try
			{
			// put default amount of ammo into the weapon
			fWeapon.setAmmoCount(fWeapon.getDefaultAmmoCount());

			// toss it
			fWeapon.drop(this, GenericItem.DROP_TIMEOUT);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
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
		fEntity.setEvent(NativeEntity.EV_FOOTSTEP);
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
		fEntity.setEvent(NativeEntity.EV_FALLSHORT);
		return;
		}			

	// land hard enough to damage and make more noise
	if (fHealth > 0)
		{
		if (delta >= 55)
			fEntity.setEvent(NativeEntity.EV_FALLFAR);
		else
			fEntity.setEvent(NativeEntity.EV_FALL);
		}

//	ent->pain_debounce_time = level.time;	// no normal pain sound

	float damage = (delta - 30) / 2;
	if (damage < 1)
		damage = 1;

	if (!GameModule.isDMFlagSet(GameModule.DF_NO_FALLING))
		damage(null, null, new Vector3f(0, 0, 1), fEntity.getOrigin(), new Vector3f(0, 0, 0), (int) damage, 0, 0, Engine.TE_NONE, "falling");
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
 * Get the Player's amount of Armor
 */
public int getArmorCount()
	{
	return fArmorCount;
	}
/**
 * Get the Player's max armor capacity
 */
public int getArmorMaxCount()
	{
	return fArmorMaxCount;
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
public int getHealth() 
	{
	return fHealth;
	}
/**
 * Get the upper limit of the player's health range.
 * @return int
 */
public int getHealthMax() 
	{
	return fHealthMax;
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
 * Returns the mass of the player (normally 200).
 * @return	The player's mass.
 */
public int getMass()
	{
	return fMass;
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param itemname java.lang.String
 */
protected int getMaxAmmoCount(String ammoName) 
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
protected String getPlayerInfo(String key) 
	{
	return getPlayerInfo(key, null);
	}
/**
 * Lookup a value from the userinfo string.
 * @param key name of the value we're looking for.
 * @param defaultValue what to return if key isn't found.
 * @return java.lang.String
 */
protected String getPlayerInfo(String key, String defaultValue) 
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
	return Engine.getSoundIndex((fIsFemale ? "player/female/" : "player/male/") + base + ".wav");
	}
/**
 * Get a suitable Spawnpoint to jump to.  Mods like CTF might
 * want to override this to use team spawnpoints instead.
 * @return baseq2.GenericSpawnpoint
 */
protected GenericSpawnpoint getSpawnpoint() 
	{
	if (GameModule.gIsDeathmatch)
		{
		if (GameModule.isDMFlagSet(GameModule.DF_SPAWN_FARTHEST))
			return MiscUtil.getSpawnpointFarthest();		
		else
			return MiscUtil.getSpawnpointRandom();
		}
		
	return MiscUtil.getSpawnpointSingle();
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 */
public boolean heal(int amount, boolean overrideMax) 
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
 * This method was created by a SmartGuide.
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
			Vector3f	kickVelocity = new Vector3f();
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
			Vector3f v = new Vector3f(fEntity.getVelocity());
			v.add(kickVelocity);
			fEntity.setVelocity(v);
			}
		}
	}
/**
 * React to a localized game message by printing to the player's screen.
 * @param printLevel int
 * @param msg java.lang.String
 */
public void localecast(Locale loc, int printLevel, String msg) 
	{
	fEntity.cprint(printLevel, msg);
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
public void playerBegin(boolean loadgame) 
	{
	Engine.debugLog("Player.begin(" + loadgame + ")");

	fStartTime = (float) Game.getGameTime();	
	
	// restore key entity settings - needs to be done each level change.
	fEntity.setPlayerStat(NativeEntity.STAT_HEALTH_ICON, (short) Engine.getImageIndex("i_health"));	
	fEntity.setPlayerGravity((short)GameModule.gGravity.getFloat());
	Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), getName() + "\\" + getPlayerInfo("skin"));			
		
	// things that need to be reset on map changes
	fCmdAngles  = new Angle3f();		

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
 * This method will look for a method named "cmd_" + Engine.argv(0), 
 * and if found, it will be called with an array of strings passed as 
 * an argument.  The first entry in the array is Engine.argv(0), and the 
 * succeding entries are the succeding values of Engine.argv().
 */
public void playerCommand() 
	{
	String[] sa = new String[Engine.getArgc()];
	for (int i = 0; i < sa.length; i++)
		sa[i] = Engine.getArgv(i);

	Class[] paramTypes = new Class[1];
	paramTypes[0] = sa.getClass();
	
	try
		{
		java.lang.reflect.Method meth = getClass().getMethod("cmd_" + sa[0].toLowerCase(), paramTypes);						
		Object[] params = new Object[1];
		params[0] = sa;
		meth.invoke(this, params);
		}
	catch (NoSuchMethodException e1)
		{
		// treat unrecognized input as a chat
		String msg = getName() + ": " + Engine.getArgv(0) + " " + Engine.getArgs();
		if (msg.length() > 150)
			msg = msg.substring(0, 150);		
		msg += "\n";
			
		Game.bprint(Engine.PRINT_CHAT, msg);
		}
	catch (java.lang.reflect.InvocationTargetException e2)		
		{
		e2.getTargetException().printStackTrace();
		}
	catch (Exception e3)
		{
		e3.printStackTrace();
		}							
	}
/**
 * Called by the DLL when the player is disconnecting. 
 * We should clean things up and say goodbye.
 * Be sure you drop any references to this player object.  
 */
public void playerDisconnect()
	{
	Engine.debugLog("Player.disconnect()");

	Object[] args = {getName()};
	Game.localecast("baseq2.Messages", "disconnect", args, Engine.PRINT_HIGH);
	
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
	Engine.debugLog("baseq2.Player.playerInfoChanged(" + playerInfo + ")");

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
 * All player entities get a chance to think.  When
 * a player entity thinks, it has to handle the 
 * users movement commands by calling pMove().
 * @param cmd commands from the client..indicate movement, jumping, weapon firing.
 */
public void playerThink(PlayerCmd cmd)
	{
	if (fInIntermission)
		return;
		
	PMoveResults pm = fEntity.pMove(cmd, Engine.MASK_PLAYERSOLID);

//  if (pm_passent->health > 0)
//      return gi.trace (start, mins, maxs, end, pm_passent, MASK_PLAYERSOLID);
//  else
//      return gi.trace (start, mins, maxs, end, pm_passent, MASK_DEADSOLID);
 	
 	fCmdAngles = cmd.getCmdAngles();
 	
	if ((fEntity.getGroundEntity() != null) && (pm.fGroundEntity == null) && (cmd.fUpMove >= 10) && (pm.fWaterLevel == 0))
		fEntity.sound(NativeEntity.CHAN_VOICE, getSexedSoundIndex("jump1"), 1, NativeEntity.ATTN_NORM, 0);
			
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
			if (pm.fTouched[i].getReference() instanceof GameObject)
				((GameObject)pm.fTouched[i].getReference()).touch(this);
			}
		}

	fOldButtons = fButtons;	
	fButtons = cmd.fButtons;		
	fLatchedButtons |= fButtons & ~fOldButtons;	
	
	// fire weapon from final position if needed
	if (((fLatchedButtons & PlayerCmd.BUTTON_ATTACK) != 0) && (!fWeaponThunk) && (!fIsDead))
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
	Engine.debugLog("baseq2.Player.playerVariableChanged(" + key + ", " + oldValue + ", " + newValue + ")");

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
		// stop receiving messages for the old locale
		if (fResourceGroup != null)
			fResourceGroup.removeLocaleListener(this);
			
		// sign up to receive messages formatted for the new locale
		fResourceGroup = Game.addLocaleListener(this, newValue);
		return;
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param foo q2java.Vec3
 */
public Point3f projectSource(Vector3f offset, Vector3f forward, Vector3f right) 
	{
	Vector3f distance = new Vector3f(offset);
	
	if (fHand == LEFT_HANDED)
		distance.y *= -1;
	else if (fHand == CENTER_HANDED)
		distance.y = 0;


	Point3f result = new Point3f();
	Point3f point = fEntity.getOrigin();
	
	result.x = point.x + forward.x * distance.x + right.x * distance.y;
	result.y = point.y + forward.y * distance.x + right.y * distance.y;
	result.z = point.z + forward.z * distance.x + right.z * distance.y + distance.z;
	
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
/**
 * This method was created by a SmartGuide.
 * @param name java.lang.String
 */
public void removeInventory(String name) 
	{
	fInventory.remove(name);
	}
/**
 * Put a dead player back into the game
 */
protected void respawn()
	{
	// leave a corpse behind
	GameModule.copyCorpse(fEntity);
	
	clearSettings();
	
	// put a live body back into the game
	spawn();
	
	// add a teleportation effect
	fEntity.setEvent(NativeEntity.EV_PLAYER_TELEPORT);
	
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
 * Send a chat message to all players.
 * @param msg String to send - a newline will be added.
 */
public void say(String msg)
	{
	msg = getName() + ": " + msg;		
	
	// keep the message down to a reasonable length
	if (msg.length() > 150)
		msg = msg.substring(0, 150);	
			
	msg += "\n";
			
	Game.bprint(Engine.PRINT_CHAT, msg);		
	}
/**
 * Send a chat message to teammates (all players in DM mode).
 * @param msg String to send - a newline will be added.
 */
public void sayTeam(String msg)
	{
	say(msg);		
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
		animation += (Game.randomInt() & 0x00ff) % 3;
		
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
 * Set the player's armor.  It can't go below zero or above the max armor count.
 * @param amount amount of armor
 */
public void setArmorCount(int amount)
	{
	setArmorCount(amount, true);
	}
/** 
 * Set the player's armor.  It can't go below zero or above the max armor count.
 * @param amount amount of armor
 * @param isAbsolute Whether the amount is an absolute value, or relative to the current armor.
 */
public void setArmorCount(int amount, boolean isAbsolute)
	{
	int newValue;
	
	if (isAbsolute)
		newValue = amount;
	else
		newValue = fArmorCount + amount;
		
	fArmorCount = Math.max( 0, Math.min(newValue, fArmorMaxCount) );
	fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
	}
/**
 * Set the Player's max possible armor amount
 */
public void setArmorMaxCount( int maxCount )
	{
	fArmorMaxCount = maxCount;
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
 * This method was created by a SmartGuide.
 * @param val int
 */
public void setHealth(int val) 
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
	fEntity.setMins(-16, -16, 24);
	fEntity.setMaxs(16, 16, 32);
	
	// clear entity state values
	fEntity.setEffects(0);
	fEntity.setSkinNum(fEntity.getPlayerNum());
	fEntity.setModelIndex(255);	// will use the skin specified model
	showVWep();
	
	fEntity.linkEntity();			

	closeDisplay();
	}
/**
 * Switch the player into intermission mode.  Their view should be from
 * a specified intermission spot, their movement should be frozen, and the 
 * scoreboard displayed.
 *
 * @param intermissionSpot The spot the player should be moved do.
 */
public void startIntermission(GenericSpawnpoint intermissionSpot) 
	{
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
	// unlink to make sure it can't possibly interfere with KillBox
	fEntity.unlinkEntity();	

	fEntity.setOrigin(origin);

	// clear the velocity and hold them in place briefly
	fEntity.setVelocity(0, 0, 0);
	fEntity.setPlayerPMTime((byte)20);	// hold time 160ms (20 * 8)
	fEntity.setPlayerPMFlags((byte)(fEntity.getPlayerPMFlags() | NativeEntity.PMF_TIME_TELEPORT));

	// draw the teleport splash at source and on the player
	fEntity.setEvent(NativeEntity.EV_PLAYER_TELEPORT);

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

	Object[] args = {getName()};
	Game.localecast("baseq2.Messages", "entered", args, Engine.PRINT_HIGH);
	fEntity.centerprint(WelcomeMessage.getMessage());
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
			fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/lava_in.wav"), 1, NativeEntity.ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_SLIME) != 0)
			fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_in.wav"), 1, NativeEntity.ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_WATER) != 0)
			fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_in.wav"), 1, NativeEntity.ATTN_NORM, 0);			
		}

	//
	// if just completely exited a water volume, play a sound
	//
	if ((fWaterLevel == 0) && (oldWaterLevel != 0))
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_out.wav"), 1, NativeEntity.ATTN_NORM, 0);

	//
	// check for head just going under water
	//
	if ((fWaterLevel == 3) && (oldWaterLevel != 3))
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("player/watr_un.wav"), 1, NativeEntity.ATTN_NORM, 0);

	//
	// check for head just coming out of water
	//
	if ((fWaterLevel != 3) && (oldWaterLevel == 3))
		fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/gasp2.wav"), 1, NativeEntity.ATTN_NORM, 0);

	// FIXME --generate a zero vector for use in damage funcs	
	// not terribly efficient to keep doing this
	Vector3f origin = new Vector3f();

	//
	// check for drowning
	//
	if (fWaterLevel == 3)
		{	
		// breather or envirosuit give air
/*
		if (breather || envirosuit)
			{
			current_player->air_finished = level.time + 10;

			if (((int)(current_client->breather_framenum - level.framenum) % 25) == 0)
				{
				if (!current_client->breather_sound)
					gi.sound (current_player, CHAN_AUTO, gi.soundindex("player/u_breath1.wav"), 1, ATTN_NORM, 0);
				else
					gi.sound (current_player, CHAN_AUTO, gi.soundindex("player/u_breath2.wav"), 1, ATTN_NORM, 0);
				current_client->breather_sound ^= 1;
				PlayerNoise(current_player, current_player->s.origin, PNOISE_SELF);
				//FIXME: release a bubble?
				}
			}
*/
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
					fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/drown1.wav"), 1, NativeEntity.ATTN_NORM, 0);
				else if ((Game.randomInt() & 1) != 0)
					fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("*gurp1.wav"), 1, NativeEntity.ATTN_NORM, 0);
				else
					fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("*gurp2.wav"), 1, NativeEntity.ATTN_NORM, 0);

				fPainDebounceTime = Game.getGameTime();

				damage(GameModule.gWorld, GameModule.gWorld, origin, fEntity.getOrigin(), origin, fDrownDamage, 0, DAMAGE_NO_ARMOR, Engine.TE_NONE, "water");
				}
			}
		}
	else
		{
		// reset breath counter and restore normal drowning damage level
		fAirFinished = Game.getGameTime() + 12;
		fDrownDamage = 2;
		}

		
	//
	// check for sizzle damage
	//
	if ((fWaterLevel != 0) && ((fWaterType & Engine.CONTENTS_LAVA) != 0))
		{
		if (fHealth > 0
			&& Game.getGameTime() > fPainDebounceTime 
//			&& current_client->invincible_framenum < level.framenum
			)
			{
			if ((Game.randomInt() & 1) != 0)
				fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/burn1.wav"), 1, NativeEntity.ATTN_NORM, 0);
			else
				fEntity.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/burn2.wav"), 1, NativeEntity.ATTN_NORM, 0);
			fPainDebounceTime = Game.getGameTime() + 1;
			}

//		if (envirosuit)	// take 1/3 damage with envirosuit
//			T_Damage (current_player, world, world, vec3_origin, current_player->s.origin, vec3_origin, 1*waterlevel, 0, 0, MOD_LAVA);
//		else
			damage(GameModule.gWorld, GameModule.gWorld, origin, fEntity.getOrigin(), origin, 3* fWaterLevel, 0, 0, Engine.TE_NONE, "lava");
		}


	if ((fWaterLevel != 0) && ((fWaterType & Engine.CONTENTS_SLIME) != 0))
		{
//		if (!envirosuit)
			{	// no damage from slime with envirosuit
			damage(GameModule.gWorld, GameModule.gWorld, origin, fEntity.getOrigin(), origin, fWaterLevel, 0, 0, Engine.TE_NONE, "slime");
			}
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param killer q2jgame.Player
 */
protected void writeDeathmatchScoreboardMessage(GameObject killer) 
	{	
	StringBuffer sb = new StringBuffer();
	int i;

	// generate a list of players sorted by score
	Vector players = new Vector();
	Enumeration enum = NativeEntity.enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player) ((NativeEntity)enum.nextElement()).getPlayerListener();
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
		
	for (i = 0; i < playerCount; i++)
		{
		int x = (i >= 6) ? 160 : 0;		// column
		int y = (32 * (i % 6)) + 32;	// row
		Player p = (Player) players.elementAt(i);
		
		// add a dogtag to the player and his killer
		String s = null;
		if (p == this)
			s = "xv " + (x + 32) + " yv " + y + " picn tag1 ";
		if (p == killer)
			s = "xv " + (x + 32) + " yv " + y + " picn tag2 ";
		if ((s != null) && ((sb.length() + s.length()) < 1024))
			sb.append(s);

		// add the layout
		s = "client " + x + " " + y + " " + p.fEntity.getPlayerNum() + " " + p.fScore + " " + p.fEntity.getPlayerPing() + " " + (int)((Game.getGameTime() - p.fStartTime) / 60) + " "; 
		// the last 0 should really be the number of minutes the player's been in the game 		 			
		if ((sb.length() + s.length()) < 1024)
			sb.append(s);		
		}
				
	Engine.writeByte(Engine.SVC_LAYOUT);
	Engine.writeString(sb.toString());
	}
}