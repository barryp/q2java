
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

public class Player extends GameObject implements FrameListener, PlayerListener, CrossLevel
	{	
	// ---- Instance fields ------------------------
	protected String fName;
	protected int fScore;
	protected float fStartTime;

	private int fHealth;
	protected int fHealthMax;
	protected boolean fIsFemale;
	protected boolean fInIntermission;
	
	private float fDamageMultiplier;
		
	protected Hashtable fUserInfo;
	protected Hashtable fInventory;
	protected Vector fWeaponList;
	protected Hashtable fAmmoBelt;

	// Armor 
	protected int fArmorCount;
	protected int fArmorMaxCount;				
	protected float fArmorProtection; 		// what fraction of normal damage the player's armor absorbs
	protected float fArmorEnergyProtection; 	// fraction of energy damage the player's armor absorbs	

	protected boolean fWeaponThunk; // have we given the current weapon a chance to think yet this frame?
	protected GenericWeapon fWeapon;
	protected GenericWeapon fNextWeapon;	
	protected AmmoPack fAmmo;
	
	protected int fHand;
	protected float fBobTime;
	public int fButtons;
	public int fLatchedButtons;
	protected int fOldButtons;
	public float fViewHeight;
	protected int fWaterType;
	protected int fWaterLevel;
	protected int fOldWaterLevel;
	protected Vector3f fOldVelocity;
	
	protected boolean fShowScore;
	protected boolean fIsDead;
	protected float fKillerYaw;
	protected float fRespawnTime;
		
	// animation variables
	private int fAnimationFrame;
	protected int fAnimationPriority;
	protected int fAnimationEnd;
	protected boolean fIsRunning;
	protected boolean fIsDucking;
	
	public Angle3f fKickAngles;
	public Point3f fKickOrigin;
	
	// temp vectors for endFrame()
	protected Vector3f fRight;
	protected Vector3f fForward;
	protected Vector3f fUp;
	protected float fXYSpeed;

	// ------- Public constants ---------------------------------	
	
	// Constants for setAnimation()

	// basic animations
	public final static int ANIMATE_NORMAL	= 0; // normal = stand, normal+1 = run
	public final static int ANIMATE_ATTACK	= 2;
	public final static int ANIMATE_PAIN		= 3;
	public final static int ANIMATE_DEATH	= 6;
		
	// gesture animations
	public final static int ANIMATE_FLIPOFF	= 18;
	public final static int ANIMATE_SALUTE	= 19;
	public final static int ANIMATE_TAUNT	= 20;
	public final static int ANIMATE_WAVE		= 21;
	public final static int ANIMATE_POINT	= 22;

	// jumping animations
	public final static int ANIMATE_JUMP		= 23;
	public final static int ANIMATE_FLAIL	= 24;
	public final static int ANIMATE_LAND		= 25;
	

	
	// handedness values
	public final static int RIGHT_HANDED		= 0;
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
	fEntity = ent;
	fEntity.setPlayerListener(this);
	fEntity.setReference(this);
	
	parsePlayerInfo(fEntity.getPlayerInfo());
	
	// create temporary vectors
	fRight = new Vector3f();
	fForward = new Vector3f();
	fUp = new Vector3f();
	fKickAngles = new Angle3f();
	fKickOrigin = new Point3f();	

	// sign up to receive server frame notices at the beginning and end of server frames
	Game.addFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END, 0, 0);
	}
/**
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 * @param count int
 * @param icon int
 */
public boolean addAmmo(String ammoType, int count) 
	{
	AmmoPack pack = (AmmoPack) fAmmoBelt.get(ammoType);
	if (pack == null)
		fAmmoBelt.put(ammoType, new AmmoPack(Integer.MAX_VALUE, null));
	else
		{
		// make sure we don't overfill the ammo pack
		count = Math.min(count, pack.fMaxAmount - pack.fAmount);

		// don't do anything if the player is already maxed out on this ammo
		if (count < 1)
			return false;
		
		if (pack == fAmmo)
			alterAmmoCount(count);  // will also update HUD
		else			
			pack.fAmount += count;		
		}
		
	return true;		
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 * @param maxAmount int
 * @param protection float
 * @param energyProtection float
 */
public boolean addArmor(int amount, int maxAmount, float protection, float energyProtection, int icon) 
	{
	// handle shards differently
	if (maxAmount == 0) 
		{
		fArmorCount += amount;
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		return true;
		}

	// is this an armor upgrade?
	if (protection > fArmorProtection)
		{ 
		int salvage = (int)((fArmorProtection / protection) * fArmorCount);
		fArmorCount = amount + salvage;
		fArmorMaxCount = maxAmount;
		fArmorProtection = protection;
		fArmorEnergyProtection = energyProtection;		
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) icon);
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
 * This method was created by a SmartGuide.
 * @return boolean
 * @param weaponName java.lang.String
 * @param ammoName java.lang.String
 * @param ammoCount int
 */
public boolean addWeapon(Class weaponClass, String weaponName, String ammoName, int ammoCount) 
	{
	boolean weaponStay = GameModule.isDMFlagSet(GameModule.DF_WEAPONS_STAY);

	if (isCarrying(weaponName))
		{
		if (weaponStay)
			return false;
		else			
			return addAmmo(ammoName, ammoCount);
		}
	else
		{		
		try
			{
			GenericWeapon w = (GenericWeapon) weaponClass.newInstance();
			putInventory(weaponName, w);
			fWeaponList.addElement(w);
			w.setOwner(this);
			fEntity.cprint(Engine.PRINT_HIGH, "You picked up a " + weaponName + "\n");
			
			// switch weapons if we're currently using the blaster
			if (fWeapon == getInventory("blaster"))
				{
				fNextWeapon = w;
				fWeapon.deactivate();
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}							
		addAmmo(ammoName, ammoCount);		
		return !weaponStay;
		}		
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 */
public void alterAmmoCount(int amount) 
	{
	if (fAmmo != null)
		{
		fAmmo.fAmount += amount;
		fEntity.setPlayerStat(NativeEntity.STAT_AMMO, (short) fAmmo.fAmount);
		}
	}
/**
 * Change user settings based on what was in userinfo string.
 */
protected void applyPlayerInfo()
	{
	fName = getUserInfo("name");
	String s = getUserInfo("skin");

	Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), fName + "\\" + s);			
	
	// id's C code just checks the first letter of the skin for a 'f' or 'F'
	fIsFemale = (s != null) &&  s.toLowerCase().startsWith("female");
			
	s = getUserInfo("hand");
	if (s != null)
		fHand = Integer.parseInt(s);			
		
	s = getUserInfo("fov");
	if (s != null)
		fEntity.setPlayerFOV((new Float(s)).floatValue());	
	}
/**
 * This method was created by a SmartGuide.
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
		if (fAnimationFrame < fAnimationEnd)
			{
			incFrame();
			return;
			}
		
		if (fAnimationPriority == ANIM_DEATH)
			return;		// stay there
		
		if (fAnimationPriority == ANIM_JUMP)
			{
			if (fEntity.getGroundEntity() != null)
				setAnimation(ANIMATE_LAND, true);	
			return;
			}
		}
	
	// at this point, we're either here because we're making
	// a transition, or we didn't return from the normal 
	// animation handling, so reset to a basic state
					
	fIsDucking = isDucking;
	fIsRunning = isRunning;

	if (fEntity.getGroundEntity() == null)
		setAnimation(ANIMATE_FLAIL, true);
	else
		setAnimation(ANIMATE_NORMAL, true);	
	}
/**
 * This method was created by a SmartGuide.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 */
protected void calcKillerYaw(GameObject inflictor, GameObject attacker) 
	{
	Tuple3f	dir;
	
	if ((attacker != null) && (attacker != this))
		{
		dir = attacker.fEntity.getOrigin();
		dir.sub(fEntity.getOrigin());
		}
	else if ((inflictor != null) && (inflictor != this))
		{
		dir = inflictor.fEntity.getOrigin();
		dir.sub(fEntity.getOrigin());
		}
	else
		{
		fKillerYaw = fEntity.getAngles().y;
		return;
		}

	fKillerYaw = (float) ((180.0 / Math.PI) * Math.atan2(dir.y, dir.x));	
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
	Vector3f v = new Vector3f(0, 0, fViewHeight);
	float bobMove = 0.0F;

	fEntity.setPlayerKickAngles(fKickAngles);	
	
	// add fall height
	// ---FIXME---
	
	// setup bob calculations
	Vector3f velocity = fEntity.getVelocity();
	fXYSpeed = (float)Math.sqrt((velocity.x*velocity.x) + (velocity.y*velocity.y));
	
	if (fXYSpeed < 5.0)
		{
		bobMove = 0;
		fBobTime = 0;
		}
	else if (fEntity.getGroundEntity() != null)
		{
		if (fXYSpeed > 210)
			bobMove = 0.25F;
		else if (fXYSpeed > 100)
			bobMove = 0.125F;
		else
			bobMove = 0.0625F;		
		}		
		
	fBobTime += bobMove;
	
	float bobfracsin = (float) Math.abs(Math.sin(fBobTime*Math.PI));			

	// add bob height
	float bob = bobfracsin * fXYSpeed * GameModule.gBobUp.getFloat(); // *3 added to magnify effect
	if (bob > 6)
		bob = 6.0F;
	v.z += bob;	
		
	// add kick offset
	v.add(fKickOrigin);
	
	// absolutely bound offsets
	// so the view can never be outside the player box		
	if (v.x < -14)
		v.x = -14;
	else if (v.x > 14)
		v.x = 14;
		
	if (v.y < -14)
		v.y = -14;
	else if (v.y > 14)
		v.y = 14;
		
	if (v.z < -22)
		v.z = -22;
	else if (v.z > 30)
		v.z = 30;
		
	fEntity.setPlayerViewOffset(v);
	}
/**
 * This method was created by a SmartGuide.
 */
public void changeWeapon() 
	{
	if (fNextWeapon != null) 
		fWeapon = fNextWeapon;
	else
		fWeapon = nextAvailableWeapon();
						
	fNextWeapon = null;
	fWeapon.activate();		
	}
/**
 * Clear the player's settings so they are a fresh 
 * new Space Marine.
 */
protected void clearSettings( ) 
	{
	setDamageMultiplier(1.0F);
	
	// initialize the AmmoBelt
	fAmmoBelt = new Hashtable();
	fAmmoBelt.put("shells", new AmmoPack(100, "a_shells"));
	fAmmoBelt.put("bullets", new AmmoPack(200, "a_bullets"));
	fAmmoBelt.put("grenades", new AmmoPack(50, "a_grenades"));
	fAmmoBelt.put("rockets", new AmmoPack(50, "a_rockets"));
	fAmmoBelt.put("cells", new AmmoPack(200, "a_cells"));
	fAmmoBelt.put("slugs", new AmmoPack(50, "a_slugs"));

	// initialize the inventory with a Blaster
	fInventory = new Hashtable();
	fWeaponList = new Vector();
	try
		{
		fWeapon = (GenericWeapon) Game.lookupClass(".spawn.weapon_blaster").newInstance();
		putInventory("blaster", fWeapon);
		fWeaponList.addElement(fWeapon);
		fWeapon.setOwner(this);
		fWeapon.activate();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}

	// initialize the armor settings (jacket_armor quality protection)
	fArmorCount = 0;
	fArmorMaxCount =  50;
	fArmorProtection = 0.30F;
	fArmorEnergyProtection = 0.0F;

	fIsDead = false;		
	fInIntermission = false;
	fViewHeight = 22;

	setHealth(100);
	fHealthMax = 100;
	setAnimation(ANIMATE_NORMAL, true);
	fOldVelocity = new Vector3f();
	}
/**
 * Change field-of-view.
 * @param args java.lang.String[]
 */
public void cmd_fov(String[] args) 
	{
	if (args.length < 1)
		{
		Game.dprint("cmd_fov() called with no arguments\n");
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
 * Suicide.
 * @param args java.lang.String[]
 */
public void cmd_kill(String[] args) 
	{
	if (fIsDead)
		fEntity.cprint(Engine.PRINT_HIGH, "You're already dead\n");
	else		
		die(this, this, 0, fEntity.getOrigin());
	}
/**
 * Send a chat message to all players.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say(String[] args) 
	{
	String msg = Engine.getArgs();
	
	// remove any quote marks
	if (msg.charAt(0) == '"')
		msg = msg.substring(1, msg.length()-1);
		
	msg = fName + ": " + msg;		
	
	// keep the message down to a reasonable length
	if (msg.length() > 150)
		msg = msg.substring(0, 150);	
			
	msg += "\n";
			
	Game.bprint(Engine.PRINT_CHAT, msg);		
	}
/**
 * Treat "say_team" the same as "say" for now.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say_team(String[] args) 
	{
	cmd_say(args);
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
	String item = args[1];
	for (int i = 2; i < args.length; i++)
		item = item + " " + args[i];
	use(item);
	}
/**
 * Invoke player gestures.
 * @param args java.lang.String[]
 */
public void cmd_wave(String[] args) 
	{
	if (args.length < 1)
		{
		fEntity.cprint(Engine.PRINT_HIGH, "wave called with no arguments\n");
		return;
		}
		
	int i = Integer.parseInt(args[1]);
			
	switch(i)
		{
		case 0: 
			setAnimation(ANIMATE_FLIPOFF, false);
			fEntity.cprint(Engine.PRINT_HIGH, "flipoff\n");
			break;

  		case 1: 
  			setAnimation(ANIMATE_SALUTE, false);
			fEntity.cprint(Engine.PRINT_HIGH, "salute\n");
			break;

  		case 2: 
  			setAnimation(ANIMATE_TAUNT, false);
			fEntity.cprint(Engine.PRINT_HIGH, "taunt\n");
			break;

  		case 3: 
  			setAnimation(ANIMATE_WAVE, false);
			fEntity.cprint(Engine.PRINT_HIGH, "wave\n");
			break;

	  	default: 
	  		setAnimation(ANIMATE_POINT, false);
			fEntity.cprint(Engine.PRINT_HIGH, "point\n");
			break;		
		}			
	}
/**
 * Switch to the next available weapon.
 * @param args java.lang.String[]
 */
public void cmd_weapnext(String[] args) 
	{
	int i = fWeaponList.indexOf(fWeapon);
	while (true)
		{
		i++;		
		if (i >= fWeaponList.size())
			i = 0;
			
		fNextWeapon = (GenericWeapon) fWeaponList.elementAt(i);
		if (fNextWeapon.isEnoughAmmo() || (fNextWeapon == fWeapon))
			break;
		}
		
	if (fNextWeapon == fWeapon)
		fNextWeapon = null;
	else					
		fWeapon.deactivate();						
	}
/**
 * Switch to the previous available weapon.
 * @param args java.lang.String[]
 */
public void cmd_weapprev(String[] args) 
	{
	int i = fWeaponList.indexOf(fWeapon);
	while (true)
		{
		i--;		
		if (i < 0)
			i = fWeaponList.size() - 1;
			
		fNextWeapon = (GenericWeapon) fWeaponList.elementAt(i);
		if (fNextWeapon.isEnoughAmmo() || (fNextWeapon == fWeapon))
			break;
		}
		
	if (fNextWeapon == fWeapon)
		fNextWeapon = null;
	else					
		fWeapon.deactivate();						
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
	int damage, int knockback, int dflags, int tempEvent) 
	{
	// don't take any more damage if already dead
	if (fIsDead)
		return;
		
	// decrease damage based on armor
	if ((dflags & DAMAGE_NO_ARMOR) == 0)
		{
		int save; // the amount of damage our armor protects us from
		if ((dflags & DAMAGE_ENERGY) != 0)
			save = (int) Math.ceil(damage * fArmorEnergyProtection);
		else
			save = (int) Math.ceil(damage * fArmorProtection);			
			
		save = Math.min(save, fArmorCount);		

		if (save > 0)
			{
			damage -= save;
			fArmorCount -= save;
			fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
			spawnDamage(Engine.TE_SPARKS, point, normal, save);
			}	
		}
	
	if (damage <= 0)
		return;
			
	spawnDamage(Engine.TE_BLOOD, point, normal, damage);
	setHealth(fHealth - damage);
	if (fHealth < 0)
		die(inflictor, attacker, damage, point);
	}
/**
 * This method was created by a SmartGuide.
 */
protected void die(GameObject inflictor, GameObject attacker, int damage, Point3f point)
	{
	if (fIsDead)
		return;	// already dead
		
	fIsDead = true;
	fRespawnTime = (float)(Game.getGameTime() + 1);  // the player can respawn after this time
	
	obituary(inflictor, attacker);
		
	fEntity.setModelIndex2(0); // remove linked weapon model
	fEntity.setPlayerGunIndex(0);
	fWeapon = null;
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setPlayerPMType(NativeEntity.PM_DEAD);
	calcKillerYaw(inflictor, attacker);
	writeDeathmatchScoreboardMessage(attacker);
	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);
	
	if (fHealth < -40)
		{	// gib
		fEntity.sound(NativeEntity.CHAN_BODY, Engine.getSoundIndex("misc/udeath.wav"), 1, NativeEntity.ATTN_NORM, 0);
//		for (n= 0; n < 4; n++)
//			ThrowGib (self, "models/objects/gibs/sm_meat/tris.md2", damage, GIB_ORGANIC);
//		ThrowClientHead (self, damage);

//		self->takedamage = DAMAGE_NO;
		}
	else
		{	// normal death
		setAnimation(ANIMATE_DEATH, false);
		fEntity.sound(NativeEntity.CHAN_VOICE, sexedSoundIndex("death"+((MiscUtil.randomInt() & 0x03) + 1)), 1, NativeEntity.ATTN_NORM, 0);
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
	
	worldEffects();
	fallingDamage();
	calcViewOffset();	
	calcClientFrame();
	
	fKickAngles.set(0,0,0);
	fKickOrigin.set(0,0,0);
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
		if (fIsFemale)
			{
			if (delta >= 55)
				fEntity.setEvent(NativeEntity.EV_FEMALE_FALLFAR);
			else
				fEntity.setEvent(NativeEntity.EV_FEMALE_FALL);
			}
		else
			{
			if (delta >= 55)
				fEntity.setEvent(NativeEntity.EV_MALE_FALLFAR);
			else
				fEntity.setEvent(NativeEntity.EV_MALE_FALL);
			}
		}

//	ent->pain_debounce_time = level.time;	// no normal pain sound

	float damage = (delta - 30) / 2;
	if (damage < 1)
		damage = 1;

	if (!GameModule.isDMFlagSet(GameModule.DF_NO_FALLING))
		damage(null, null, new Vector3f(0, 0, 1), fEntity.getOrigin(), new Vector3f(0, 0, 0), (int) damage, 0, 0, Engine.TE_NONE);
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
		
	AmmoPack p = (AmmoPack) fAmmoBelt.get(ammoName);
	
	if (p == null)
		return 0;		
	else
		return p.fAmount;				
	}
/**
 * How much damage should be scaled for this player. 
 * For example, normally this method would return 1.0, but if a
 * Quad was being used, it would return 4.0.
 *
 * @return float
 */
public float getDamageMultiplier() 
	{
	return fDamageMultiplier;
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int getHealth() 
	{
	return fHealth;
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.Object
 * @param itemName java.lang.String
 */
public Object getInventory(String itemName) 
	{
	return fInventory.get(itemName.toLowerCase());
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
		
	AmmoPack p = (AmmoPack) fAmmoBelt.get(ammoName);
	
	if (p == null)
		return Integer.MAX_VALUE;		
	else
		return p.fMaxAmount;				
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
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param key java.lang.String
 */
protected String getUserInfo(String key) 
	{
	if (fUserInfo == null)
		return null;
	return (String) fUserInfo.get(key);		
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
	return (fInventory.containsKey(itemName));
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
 * Broadcast a message announcing the player's demise.
 * @param inflictor the thing that killed the player.
 * @param attacker the player responsible.
 */
protected void obituary(GameObject inflictor, GameObject attacker) 
	{
	if (attacker == this)
		{
		Game.bprint(Engine.PRINT_MEDIUM, fName + " killed " + (fIsFemale ? "her" : "him") + "self.\n");
		fScore--;
//		self->enemy = NULL;
		return;
		}

//	self->enemy = attacker;
	if (attacker instanceof Player)
		{
		Player p = (Player) attacker;
		Game.bprint(Engine.PRINT_MEDIUM, fName + " was killed by " + p.fName + "\n");
		p.fScore++;
		return;
		}

	Game.bprint(Engine.PRINT_MEDIUM, fName + " died.\n");
	fScore--;
	}
/**
 * Parse a userinfo string into a hashtable.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
protected void parsePlayerInfo(String playerInfo)
	{
	fUserInfo = new Hashtable();
	StringTokenizer st = new StringTokenizer(playerInfo, "\\");
	while (st.hasMoreTokens())
		{
		String key = st.nextToken();
		if (st.hasMoreTokens())
			fUserInfo.put(key, st.nextToken());
		}		
	}
/**
 * Called by the DLL when the player should begin playing in the game.
 * @param loadgame boolean
 */
public void playerBegin(boolean loadgame) 
	{
	Engine.debugLog("Player.begin(" + loadgame + ")");

	applyPlayerInfo();
			
	fStartTime = (float) Game.getGameTime();	
	fEntity.setPlayerStat(NativeEntity.STAT_HEALTH_ICON, (short) Engine.getImageIndex("i_health"));	
	fEntity.setPlayerGravity((short)GameModule.gGravity.getFloat());
	
	clearSettings();
	spawn();	
	
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGIN);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	Game.bprint(Engine.PRINT_HIGH, fName + " entered the game\n");
	fEntity.centerprint(WelcomeMessage.getMessage());
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
		String msg = fName + ": " + Engine.getArgv(0) + " " + Engine.getArgs();
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
	Game.bprint(Engine.PRINT_HIGH, fName + " disconnected\n");

	Game.removeFrameListener(this, Game.FRAME_BEGINNING + Game.FRAME_END);	
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntity.getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGOUT);
	Engine.multicast(fEntity.getOrigin(), Engine.MULTICAST_PVS);

	fEntity.setModelIndex(0);
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.linkEntity();

	Engine.setConfigString(Engine.CS_PLAYERSKINS + fEntity.getPlayerNum(), "");	
	}
/**
 * Called by the DLL when the player's userinfo has changed.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
public void playerInfoChanged(String playerInfo) 
	{
	parsePlayerInfo(playerInfo);
	applyPlayerInfo();
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
		
	fOldVelocity = fEntity.getVelocity();

	PMoveResults pm = fEntity.pMove(cmd);
	
	if ((fEntity.getGroundEntity() != null) && (pm.fGroundEntity == null) && (cmd.fUpMove >= 10) && (pm.fWaterLevel == 0))
		fEntity.sound(NativeEntity.CHAN_VOICE, sexedSoundIndex("jump1"), 1, NativeEntity.ATTN_NORM, 0);
			
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
 * This method was created by a SmartGuide.
 * @param itemName java.lang.String
 * @param ent q2jgame.GameEntity
 */
public void putInventory(String itemName, Object ent) 
	{
	fInventory.put(itemName, ent);
	}
/**
 * Put a dead player back into the game
 */
protected void respawn()
	{
	// leave a corpse behind
//	Game.copyToBodyQueue(this);
	
	clearSettings();
	
	// put a live body back into the game
	spawn();
	
	// add a teleportation effect
	fEntity.setEvent(NativeEntity.EV_PLAYER_TELEPORT);
	
	// hold in place briefly
	fEntity.setPlayerTeleportTime((byte)50);
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
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 */
public void setAmmoType(String ammoType) 
	{
	fAmmo = (ammoType == null ? null : (AmmoPack) fAmmoBelt.get(ammoType));	
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
 * @param ignorePriority true if you want to force the player to start this new animation no matter what is already running, otherwise false.
 */
public void setAnimation(int animation, boolean ignorePriority) 
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
		animation += (MiscUtil.randomInt() & 0x00ff) % 3;
		
	// use different animations when crouching		
	if ((animation < ANIMATE_FLIPOFF) && ducking)
		animation += 9;
		
	int newPriority = fAnims[animation * 3];
	if (ignorePriority || (newPriority >= fAnimationPriority))
		{
		fAnimationPriority = newPriority;
		setFrame(fAnims[(animation*3) + 1]);
		fAnimationEnd = fAnims[(animation*3)+2];
		}
	}
/**
 * Set how much damage should be scaled for this player. 
 * For example, a quad might set this value to 4.0.
 *
 * @return float
 */
public void setDamageMultiplier(float f) 
	{
	fDamageMultiplier = f;
	}
/**
 * This method was created by a SmartGuide.
 * @param n int
 */
public void setFrame(int n) 
	{
	fAnimationFrame = n;
	fEntity.setFrame(n);
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
 * This method was created by a SmartGuide.
 * @return int
 * @param base java.lang.String
 */
protected int sexedSoundIndex(String base) 
	{
	return Engine.getSoundIndex((fIsFemale ? "player/female/" : "player/male/") + base + ".wav");
	}
/**
 * spawn the player into the game.
 */
protected void spawn() 
	{	
	GenericSpawnpoint spawnPoint;
	
	if (GameModule.gIsDeathmatch)
		{
		if (GameModule.isDMFlagSet(GameModule.DF_SPAWN_FARTHEST))
			spawnPoint = MiscUtil.getSpawnpointFarthest();		
		else
			spawnPoint = MiscUtil.getSpawnpointRandom();
		}
	else
		spawnPoint = MiscUtil.getSpawnpointSingle();
				
	if (spawnPoint == null)
		Game.dprint("Couldn't pick spawnpoint\n");
	else
		{							
		Point3f origin = spawnPoint.getOrigin();
		Angle3f ang = spawnPoint.getAngles();
		origin.z += 9;
		fEntity.setOrigin(origin);
		fEntity.setAngles(ang);
		fEntity.setPlayerViewAngles(ang);
		}
	
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setPlayerPMType(NativeEntity.PM_NORMAL);	
	fEntity.setClipmask(Engine.MASK_PLAYERSOLID);	
	fEntity.setEffects(0);
	fEntity.setSkinNum(fEntity.getPlayerNum());
	fEntity.setModelIndex(255);	// will use the skin specified model
	fEntity.setModelIndex2(255);	// custom gun model	
	fEntity.setMins(-16, -16, 24);
	fEntity.setMaxs(16, 16, 32);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short) 0); // turn off any scoreboards		
	fShowScore = false;
	fEntity.linkEntity();			
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
	fEntity.setPlayerGunIndex(0);	
	fEntity.setPlayerBlend(0, 0, 0, 0);
	fEntity.setPlayerFOV(90);
	fEntity.linkEntity();
	
	writeDeathmatchScoreboardMessage(null);
	Engine.unicast(fEntity, true);
	fEntity.setPlayerStat(NativeEntity.STAT_LAYOUTS, (short)1);	
	fInIntermission = true;
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
			if (triggers[i].getReference() instanceof GameObject)	
				((GameObject)triggers[i].getReference()).touch(this);
			}
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param itemName java.lang.String
 */
public void use(String itemName) 
	{
	Object ent = fInventory.get(itemName.toLowerCase());
	if (ent == null)
		{
		fEntity.cprint(Engine.PRINT_HIGH, "You don't have a " + itemName + "\n");
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
			fEntity.cprint(Engine.PRINT_HIGH, "You don't have enough ammo to use a " + itemName + "\n");
			fNextWeapon = null;
			return;
			}
		
		// signal the current weapon to deactivate..when it's
		// done deactivating, it will signal back to the player to 
		// changeWeapon() and we'll use() the next weapon		
		fWeapon.deactivate();	

		return;
		}
			
//	ent.use(this);	---FIXME--
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