
package q2jgame;

import java.util.*;
import q2java.*;
import q2jgame.spawn.*;
import q2jgame.weapon.*;

/**
 * Player objects are subclassed from GameEntity, but also
 * implement the methods necessary for NativePlayer.  Instances
 * of Player are created by the DLL when a new Player connects,
 * don't try to create them yourself.
 *
 * @author Barry Pederson
 */

public class Player extends GameEntity implements NativePlayer
	{	
	// ---- Instance fields ------------------------
	private String fName;
	private int fScore;
	private float fStartTime;

	private int fHealth;
	private int fHealthMax;
	private boolean fIsFemale;
	private boolean fInIntermission;
		
	private Hashtable fUserInfo;
	private Hashtable fInventory;
	private Vector fWeaponList;
	private Hashtable fAmmoBelt;

	// Armor 
	private int fArmorCount;
	private int fArmorMaxCount;				
	private float fArmorProtection; 		// what fraction of normal damage the player's armor absorbs
	private float fArmorEnergyProtection; 	// fraction of energy damage the player's armor absorbs	

	private boolean fWeaponThunk; // have we given the current weapon a chance to think yet this frame?
	private PlayerWeapon fWeapon;
	private PlayerWeapon fNextWeapon;	
	private AmmoPack fAmmo;
	
	private int fHand;
	private float fBobTime;
	public int fButtons;
	public int fLatchedButtons;
	private int fOldButtons;
	public float fViewHeight;
	private int fWaterType;
	private int fWaterLevel;
	private int fOldWaterLevel;
	private Vec3 fOldVelocity;
	
	private boolean fIsDead;
	private float fKillerYaw;
	private float fRespawnTime;
		
	// animation variables
	private int fAnimationPriority;
	private int fAnimationFrame;
	private int fAnimationEnd;
	private boolean fIsRunning;
	private boolean fIsDucking;
	
	public Vec3 fKickAngles;
	public Vec3 fKickOrigin;
	
	// temp vectors for endFrame()
	private Vec3 fRight;
	private Vec3 fForward;
	private Vec3 fUp;
	private float fXYSpeed;

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


	// ---------------- Private constants ------------------
		
	// animation priority
	private final static int ANIM_BASIC		= 0; // stand / run
	private final static int ANIM_WAVE		= 1;
	private final static int ANIM_JUMP		= 2;
	private final static int ANIM_PAIN		= 3;
	private final static int ANIM_ATTACK		= 4;
	private final static int ANIM_DEATH		= 5;			
	
	// animation table
	// priority, start-frame, endframe, ..........
	private final static int[] fAnims = 
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
 * The DLL will call this constructor when a new player 
 * is connecting. Throw an exception if you want to 
 * reject the connection.
 *
 * @exception GameException if you don't want to let this user connect
 */
public Player(String userinfo, boolean loadgame) throws GameException 
	{
	Engine.debugLog("new Player(\"" + userinfo + "\", " + loadgame + ")");
	parseUserinfo(userinfo);
	
	// create temporary vectors
	fRight = new Vec3();
	fForward = new Vec3();
	fUp = new Vec3();
	fKickAngles = new Vec3();
	fKickOrigin = new Vec3();	
/*	
	// RaV test
	fVelocity = new Vec3();
	fThrust = new Vec3();
*/	
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
		setPlayerStat(STAT_ARMOR, (short)fArmorCount);
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
		setPlayerStat(STAT_ARMOR, (short)fArmorCount);
		setPlayerStat(STAT_ARMOR_ICON, (short) icon);
		return true;
		}		

	// is our armor not up to capacity?		
	if (fArmorCount < fArmorMaxCount)		
		{
		fArmorCount = Math.min(fArmorMaxCount, fArmorCount + amount);
		setPlayerStat(STAT_ARMOR, (short)fArmorCount);
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
public boolean addWeapon(String weaponName, String weaponClassName, String ammoName, int ammoCount) 
	{
	boolean weaponStay = Game.isDMFlagSet(Game.DF_WEAPONS_STAY);

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
			PlayerWeapon w = (PlayerWeapon) Class.forName(weaponClassName).newInstance();
			putInventory(weaponName, w);
			fWeaponList.addElement(w);
			w.setOwner(this);
			cprint(Engine.PRINT_HIGH, "You picked up a " + weaponName + "\n");
			
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
		setPlayerStat(STAT_AMMO, (short) fAmmo.fAmount);
		}
	}
/**
 * Change user settings based on what was in userinfo string.
 */
private void applyUserinfo() 
	{
	fName = getUserInfo("name");
	String s = getUserInfo("skin");

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), fName + "\\" + s);			
	
	// id's C code just checks the first letter of the skin for a 'f' or 'F'
	fIsFemale = (s != null) &&  s.toLowerCase().startsWith("female");
			
	s = getUserInfo("hand");
	if (s != null)
		fHand = Integer.parseInt(s);			
		
	s = getUserInfo("fov");
	if (s != null)
		setPlayerFOV((new Float(s)).floatValue());	
	}
/**
 * This method was created by a SmartGuide.
 */
public void beginServerFrame() 
	{
	if (fInIntermission)
		return;
		
	if (fIsDead && (Game.gGameTime > fRespawnTime) && ((fLatchedButtons != 0) || Game.isDMFlagSet(Game.DF_FORCE_RESPAWN)))
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
private void calcClientFrame() 
	{
/*	
	if (ent->s.modelindex != 255)
		return;		// not in the player model
*/
	boolean isDucking = ((getPlayerPMFlags() & PMF_DUCKED) != 0);
	boolean isRunning = (fXYSpeed != 0);

	// check for stand/duck and stop/go transitions
	//
	// this is a nasty "if" statement, but basically: if we're not making
	// a transition, then run the animations normally
	if (!(	((fIsDucking != isDucking)	&& (fAnimationPriority < ANIM_DEATH))
	||		((fIsRunning != isRunning)	&& (fAnimationPriority == ANIM_BASIC))
	||		((getGroundEntity() == null)			&& (fAnimationPriority <= ANIM_WAVE))
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
			if (getGroundEntity() != null)
				setAnimation(ANIMATE_LAND, true);	
			return;
			}
		}
	
	// at this point, we're either here because we're making
	// a transition, or we didn't return from the normal 
	// animation handling, so reset to a basic state
					
	fIsDucking = isDucking;
	fIsRunning = isRunning;

	if (getGroundEntity() == null)
		setAnimation(ANIMATE_FLAIL, true);
//		if (ent->s.frame != FRAME_jump2)
//			ent->s.frame = FRAME_jump1;
	else
		setAnimation(ANIMATE_NORMAL, true);	
	}
/**
 * This method was created by a SmartGuide.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 */
private void calcKillerYaw(GameEntity inflictor, GameEntity attacker) 
	{
	Vec3	dir;
	
	if ((attacker != null) && (attacker != Game.gWorld) && (attacker != this))
		{
		dir = attacker.getOrigin().subtract(getOrigin());
		}
	else if ((inflictor != null) && (inflictor != Game.gWorld) && (inflictor != this))
		{
		dir = inflictor.getOrigin().subtract(getOrigin());
		}
	else
		{
		dir = getAngles();
		fKillerYaw = dir.y;
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
private float calcRoll(Vec3 velocity) 
	{
	float	sign;
	float	side;
	float	value;
	
	side = Vec3.dotProduct(velocity, fRight);
	sign = side < 0 ? -1 : 1;
	side = Math.abs(side);
	
	value = Game.gRollAngle.getFloat();

	if (side < Game.gRollSpeed.getFloat())
		side = side * value / Game.gRollSpeed.getFloat();
	else
		side = value;
	
	return side*sign*4;	
	}
/**
 * This method was created by a SmartGuide.
 */
private void calcViewOffset() 
	{
	Vec3 v = new Vec3(0, 0, fViewHeight);
	float bobMove = 0.0F;

	setPlayerKickAngles(fKickAngles);	
	
	// add fall height
	// ---FIXME---
	
	// setup bob calculations
	Vec3 velocity = getVelocity();
	fXYSpeed = (float)Math.sqrt((velocity.x*velocity.x) + (velocity.y*velocity.y));
	
	if (fXYSpeed < 5.0)
		{
		bobMove = 0;
		fBobTime = 0;
		}
	else if (getGroundEntity() != null)
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
	float bob = bobfracsin * fXYSpeed * Game.gBobUp.getFloat(); // *3 added to magnify effect
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
		
	setPlayerViewOffset(v);
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
 * Change field-of-view.
 * @param args java.lang.String[]
 */
public void cmd_fov(String[] args) 
	{
	if (args.length < 1)
		{
		Engine.dprint("cmd_fov() called with no arguments\n");
		return;
		}
		
	int i = Integer.parseInt(args[1]);
	
	// limit to reasonable values
	if (i < 1)
		i = 90;
	if (i > 160)
		i = 160;

	setPlayerFOV(i);				
	}
/**
 * Tell the player about the game.
 * @param args java.lang.String[]
 */
public void cmd_gameversion(String[] args) 
	{
	cprint(Engine.PRINT_HIGH, Game.getVersion() + "\n");
	}
/**
 * Suicide.
 * @param args java.lang.String[]
 */
public void cmd_kill(String[] args) 
	{
	if (fIsDead)
		cprint(Engine.PRINT_HIGH, "You're already dead\n");
	else		
		die(this, this, 0, getOrigin());
	}
/**
 * Send a chat message to all players.
 * @param (Ignored, uses the Engine.args() value instead)
 */
public void cmd_say(String[] args) 
	{
	String msg = Engine.args();
	
	// remove any quote marks
	if (msg.charAt(0) == '"')
		msg = msg.substring(1, msg.length()-1);
		
	msg = fName + ": " + msg;		
	
	// keep the message down to a reasonable length
	if (msg.length() > 150)
		msg = msg.substring(0, 150);	
			
	msg += "\n";
			
	Enumeration enum = enumeratePlayers();
	while (enum.hasMoreElements())
		{
		Player p = (Player)enum.nextElement();
		p.cprint(Engine.PRINT_CHAT, msg);
		}		
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
		Engine.dprint("cmd_wave() called with no arguments\n");
		return;
		}
		
	int i = Integer.parseInt(args[1]);
	
	switch(i)
		{
		case 0: setAnimation(ANIMATE_FLIPOFF, false); break;
		case 1: setAnimation(ANIMATE_SALUTE, false); break;
		case 2: setAnimation(ANIMATE_TAUNT, false); break;
		case 3: setAnimation(ANIMATE_WAVE, false); break;
		case 4: setAnimation(ANIMATE_POINT, false); break;
		default: Engine.dprint("Unknown wave: " + i + "\n");
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
			
		fNextWeapon = (PlayerWeapon) fWeaponList.elementAt(i);
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
			
		fNextWeapon = (PlayerWeapon) fWeaponList.elementAt(i);
		if (fNextWeapon.isEnoughAmmo() || (fNextWeapon == fWeapon))
			break;
		}
		
	if (fNextWeapon == fWeapon)
		fNextWeapon = null;
	else					
		fWeapon.deactivate();						
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
public void damage(GameEntity inflictor, GameEntity attacker, 
	Vec3 dir, Vec3 point, Vec3 normal, 
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
			setPlayerStat(STAT_ARMOR, (short)fArmorCount);
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
private void die(GameEntity inflictor, GameEntity attacker, int damage, Vec3 point)
	{
	if (fIsDead)
		return;	// already dead
		
	fIsDead = true;
	fRespawnTime = (float)(Game.gGameTime + 1);  // the player can respawn after this time
	
	obituary(inflictor, attacker);
		
	setModelIndex2(0); // remove linked weapon model
	setPlayerGunIndex(0);
	fWeapon = null;
	setSolid(SOLID_NOT);
	setPlayerPMType(PM_DEAD);
	calcKillerYaw(inflictor, attacker);
	writeDeathmatchScoreboardMessage(attacker);
	Engine.unicast(this, true);
	setPlayerStat(STAT_LAYOUTS, (short)1);
	
	if (fHealth < -40)
		{	// gib
		sound(CHAN_BODY, Engine.soundIndex("misc/udeath.wav"), 1, ATTN_NORM, 0);
//		for (n= 0; n < 4; n++)
//			ThrowGib (self, "models/objects/gibs/sm_meat/tris.md2", damage, GIB_ORGANIC);
//		ThrowClientHead (self, damage);

//		self->takedamage = DAMAGE_NO;
		}
	else
		{	// normal death
		setAnimation(ANIMATE_DEATH, false);
		sound(CHAN_VOICE, sexedSoundIndex("death"+((Game.randomInt() & 0x03) + 1)), 1, ATTN_NORM, 0);
		}
	}
/**
 * Called for each player after all the entities have 
 * had a chance to runFrame()
 */
public void endServerFrame() 
	{	
	if (fInIntermission)
		return;
		
	//
	// set model angles from view angles so other things in
	// the world can tell which direction you are looking
	//
	Vec3 newAngles = getPlayerViewAngles();
	newAngles.angleVectors(fForward, fRight, fUp);
	if (newAngles.x > 180) 
		newAngles.x = newAngles.x - 360;
	newAngles.x /= 3;		
	newAngles.z = calcRoll(getVelocity());
	setAngles(newAngles);
	
	worldEffects();
	fallingDamage();
	calcViewOffset();	
	calcClientFrame();
	
	fKickAngles.clear();
	fKickOrigin.clear();
	}
/**
 * This method was created by a SmartGuide.
 */
private void fallingDamage() 
	{
	// no damage if you're airborne
	if (getGroundEntity() == null)
		return;

	// never take falling damage if completely underwater
	if (fWaterLevel == 3)
		return;

	Vec3 velocity = getVelocity();
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
		setEvent(EV_FOOTSTEP);
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
		setEvent(EV_FALLSHORT);
		return;
		}			

	// land hard enough to damage and make more noise
	if (fHealth > 0)
		{
		if (fIsFemale)
			{
			if (delta >= 55)
				setEvent(EV_FEMALE_FALLFAR);
			else
				setEvent(EV_FEMALE_FALL);
			}
		else
			{
			if (delta >= 55)
				setEvent(EV_MALE_FALLFAR);
			else
				setEvent(EV_MALE_FALL);
			}
		}

//	ent->pain_debounce_time = level.time;	// no normal pain sound

	float damage = (delta - 30) / 2;
	if (damage < 1)
		damage = 1;

	if (!Game.isDMFlagSet(Game.DF_NO_FALLING))
		damage(Game.gWorld, Game.gWorld, new Vec3(0, 0, 1), getOrigin(), new Vec3(0, 0, 0), (int) damage, 0, 0, Engine.TE_NONE);
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
private int getMaxAmmoCount(String ammoName) 
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
private String getUserInfo(String key) 
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
 * This method was created by a SmartGuide.
 */
private void incFrame() 
	{
	super.setFrame(++fAnimationFrame);
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
private PlayerWeapon nextAvailableWeapon() 
	{
	PlayerWeapon w;
	
	w = (PlayerWeapon) fInventory.get("railgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;
		
	w = (PlayerWeapon) fInventory.get("hyperblaster");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (PlayerWeapon) fInventory.get("chaingun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (PlayerWeapon) fInventory.get("machinegun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (PlayerWeapon) fInventory.get("super shotgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	w = (PlayerWeapon) fInventory.get("shotgun");
	if ((w != null) && w.isEnoughAmmo())
		return w;

	return (PlayerWeapon) fInventory.get("blaster");
	}
/**
 * Broadcast a message announcing the player's demise.
 * @param inflictor the thing that killed the player.
 * @param attacker the player responsible.
 */
private void obituary(GameEntity inflictor, GameEntity attacker) 
	{
	if (attacker == this)
		{
		Engine.bprint(Engine.PRINT_MEDIUM, fName + " killed " + (fIsFemale ? "her" : "him") + "self.\n");
		fScore--;
//		self->enemy = NULL;
		return;
		}

//	self->enemy = attacker;
	if (attacker instanceof Player)
		{
		Player p = (Player) attacker;
		Engine.bprint(Engine.PRINT_MEDIUM, fName + " was killed by " + p.fName + "\n");
		p.fScore++;
		return;
		}

	Engine.bprint(Engine.PRINT_MEDIUM, fName + " died.\n");
	fScore--;
	}
/**
 * Parse a userinfo string into a hashtable.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
private void parseUserinfo(String userinfo)
	{
	fUserInfo = new Hashtable();
	StringTokenizer st = new StringTokenizer(userinfo, "\\");
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

	applyUserinfo();
			
	fStartTime = (float) Game.gGameTime;	
	setPlayerStat(STAT_HEALTH_ICON, (short) Engine.imageIndex("i_health"));	
	setPlayerGravity((short)Game.gGravity.getFloat());
	spawn();	
	
	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGIN);
	Engine.multicast(getOrigin(), Engine.MULTICAST_PVS);

	Engine.bprint(Engine.PRINT_HIGH, fName + " entered the game\n");
	centerprint(WelcomeMessage.getMessage());
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
	String[] sa = new String[Engine.argc()];
	for (int i = 0; i < sa.length; i++)
		sa[i] = Engine.argv(i);

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
		String msg = fName + ": " + Engine.argv(0) + " " + Engine.args();
		if (msg.length() > 150)
			msg = msg.substring(0, 150);		
		msg += "\n";
			
		Enumeration enum = enumeratePlayers();
		while (enum.hasMoreElements())
			{
			Player p = (Player)enum.nextElement();
			p.cprint(Engine.PRINT_CHAT, msg);
			}		
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
	Engine.bprint(Engine.PRINT_HIGH, fName + " disconnected\n");

	// send effect
	Engine.writeByte(Engine.SVC_MUZZLEFLASH);
	Engine.writeShort(getEntityIndex());
	Engine.writeByte(Engine.MZ_LOGOUT);
	Engine.multicast(getOrigin(), Engine.MULTICAST_PVS);

	setModelIndex(0);
	setSolid(SOLID_NOT);
	linkEntity();
	freeEntity();

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), "");	
	}
/**
 * Called by the DLL when the player's userinfo has changed.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
public void playerInfoChanged(String userinfo) 
	{
	parseUserinfo(userinfo);
	applyUserinfo();
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
		
	fOldVelocity = getVelocity();

	PMoveResults pm = pMove(cmd);
	
	if ((getGroundEntity() != null) && (pm.fGroundEntity == null) && (cmd.fUpMove >= 10) && (pm.fWaterLevel == 0))
		sound(CHAN_VOICE, sexedSoundIndex("jump1"), 1, ATTN_NORM, 0);
			
	fViewHeight = pm.fViewHeight;	
	fWaterType = pm.fWaterType;
	fWaterLevel = pm.fWaterLevel;	
	setGroundEntity(pm.fGroundEntity);

	if (fIsDead)
		setPlayerViewAngles(-15, fKillerYaw, 40);
		
	linkEntity();	
	
	touchTriggers();
	
	// notify everything the player has collided with
	if (pm.fTouched != null)
		{
		for (int i = 0; i < pm.fTouched.length; i++)
			((GameEntity)pm.fTouched[i]).touch(this);
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
public Vec3 projectSource(Vec3 offset, Vec3 forward, Vec3 right) 
	{
	Vec3 dist = new Vec3(offset);
	
	if (fHand == LEFT_HANDED)
		dist.y *= -1;
	else if (fHand == CENTER_HANDED)
		dist.y = 0;
		
	return Vec3.projectSource(getOrigin(), dist, forward, right);
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
private void respawn() 
	{
	// leave a corpse behind
//	Game.copyToBodyQueue(this);
	
	// put a live body back into the game
	spawn();
	
	// add a teleportation effect
	setEvent(EV_PLAYER_TELEPORT);
	
	// hold in place briefly
	setPlayerTeleportTime((byte)50);
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
		setPlayerStat(STAT_AMMO, (short) 0);
		setPlayerStat(STAT_AMMO_ICON, (short) 0);
		}
	else
		{
		setPlayerStat(STAT_AMMO, (short) fAmmo.fAmount);
		setPlayerStat(STAT_AMMO_ICON, (short) fAmmo.fIcon);
		}	
	}
/**
 * Set which animation cycle the player will run through.
 * @param animation one of the Player.ANIMATE_* constants.
 * @param ignorePriority true if you want to force the player to start this new animation no matter what is already running, otherwise false.
 */
public void setAnimation(int animation, boolean ignorePriority) 
	{
	boolean ducking = ((getPlayerPMFlags() & PMF_DUCKED) != 0);
	
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
	if ((animation > ANIMATE_LAND) && ducking)
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
 * This method was created by a SmartGuide.
 * @param n int
 */
public void setFrame(int n) 
	{
	fAnimationFrame = n;
	super.setFrame(n);
	}
/**
 * This method was created by a SmartGuide.
 * @param val int
 */
public void setHealth(int val) 
	{
	fHealth = val;
	setPlayerStat(STAT_HEALTH, (short)fHealth);
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param base java.lang.String
 */
private int sexedSoundIndex(String base) 
	{
	return Engine.soundIndex((fIsFemale ? "player/female/" : "player/male/") + base + ".wav");
	}
/**
 * spawn the player into the game.
 */
private void spawn() 
	{	
	GameEntity spawnPoint;
	
	if (Game.gIsDeathmatch)
		{
		if (Game.isDMFlagSet(Game.DF_SPAWN_FARTHEST))
			spawnPoint = Game.getSpawnpointFarthest();		
		else
			spawnPoint = Game.getSpawnpointRandom();
		}
	else
		spawnPoint = Game.getSpawnpointSingle();
				
	if (spawnPoint == null)
		Engine.dprint("Couldn't pick spawnpoint\n");
	else
		{							
		Vec3 origin = spawnPoint.getOrigin();
		Vec3 ang = spawnPoint.getAngles();
		origin.z += 9;
		setOrigin(origin);
		setAngles(ang);
		setPlayerViewAngles(ang);
		}
	
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
	fWeapon = new Blaster();
	putInventory("blaster", fWeapon);
	fWeaponList.addElement(fWeapon);
	fWeapon.setOwner(this);
	fWeapon.activate();

	// initialize the armor settings (jacket_armor quality protection)
	fArmorCount = 0;
	fArmorMaxCount =  50;
	fArmorProtection = 0.30F;
	fArmorEnergyProtection = 0.0F;
/*
	// RaV test
	fVelocity.clear();
	fThrust.clear();
	fClientFrameCount = 0;
*/
	fIsDead = false;		
	fInIntermission = false;
	fViewHeight = 22;
	setSolid(SOLID_BBOX);
	setPlayerPMType(PM_NORMAL);	
	setClipmask(Engine.MASK_PLAYERSOLID);	
	setEffects(0);
	setSkinNum(getPlayerNum());
	setModelIndex(255);	// will use the skin specified model
	setModelIndex2(255);	// custom gun model	
	setMins(-16, -16, 24);
	setMaxs(16, 16, 32);
	setHealth(100);
	fHealthMax = 100;
	setAnimation(ANIMATE_NORMAL, true);
	setPlayerStat(STAT_LAYOUTS, (short) 0); // turn off any scoreboards		
	fOldVelocity = new Vec3();
	linkEntity();		
	}
/**
 * Switch the player into intermission mode.  Their view should be from
 * a specified intermission spot, their movement should be frozen, and the 
 * scoreboard displayed.
 *
 * @param intermissionSpot The spot the player should be moved do.
 */
public void startIntermission(GameEntity intermissionSpot) 
	{
	setOrigin(intermissionSpot.getOrigin());
	setPlayerViewAngles(intermissionSpot.getAngles());
	setPlayerPMType(PM_FREEZE);
	setPlayerGunIndex(0);	
	setPlayerBlend(0, 0, 0, 0);
	setPlayerFOV(90);
	writeDeathmatchScoreboardMessage(null);
	Engine.unicast(this, true);
	setPlayerStat(STAT_LAYOUTS, (short)1);	
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
	NativeEntity[] triggers = boxEntity(Engine.AREA_TRIGGERS);
	if (triggers != null)
		{
		for (int i = 0; i < triggers.length; i++)
			((GameEntity)triggers[i]).touch(this);
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
		cprint(Engine.PRINT_HIGH, "You don't have a " + itemName + "\n");
		return;
		}

	// handle weapons a little differently
	if (ent instanceof PlayerWeapon)
		{
		if (ent == fWeapon)
			return; // do nothing if we're already using the weapon
		
		// make a note of what the next weapon will be	
		fNextWeapon = (PlayerWeapon) ent;
		if (!fNextWeapon.isEnoughAmmo())
			{
			cprint(Engine.PRINT_HIGH, "You don't have enough ammo to use a " + itemName + "\n");
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
private void worldEffects() 
	{
	int oldWaterLevel = fOldWaterLevel;
	fOldWaterLevel = fWaterLevel;
	
	//
	// if just entered a water volume, play a sound
	//
	if ((fWaterLevel != 0) && (oldWaterLevel == 0))
		{
		if ((fWaterType & Engine.CONTENTS_LAVA) != 0)
			sound(CHAN_BODY, Engine.soundIndex("player/lava_in.wav"), 1, ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_SLIME) != 0)
			sound(CHAN_BODY, Engine.soundIndex("player/watr_in.wav"), 1, ATTN_NORM, 0);
		else if ((fWaterType & Engine.CONTENTS_WATER) != 0)
			sound(CHAN_BODY, Engine.soundIndex("player/watr_in.wav"), 1, ATTN_NORM, 0);			
		}

	//
	// if just completely exited a water volume, play a sound
	//
	if ((fWaterLevel == 0) && (oldWaterLevel != 0))
		sound(CHAN_BODY, Engine.soundIndex("player/watr_out.wav"), 1, ATTN_NORM, 0);

	//
	// check for head just going under water
	//
	if ((fWaterLevel == 3) && (oldWaterLevel != 3))
		sound(CHAN_BODY, Engine.soundIndex("player/watr_un.wav"), 1, ATTN_NORM, 0);

	//
	// check for head just coming out of water
	//
	if ((fWaterLevel != 3) && (oldWaterLevel == 3))
		sound(CHAN_VOICE, Engine.soundIndex("player/gasp2.wav"), 1, ATTN_NORM, 0);
	}	
/**
 * This method was created by a SmartGuide.
 * @param killer q2jgame.Player
 */
private void writeDeathmatchScoreboardMessage(GameEntity killer) 
	{	
	StringBuffer sb = new StringBuffer();
	int i;

	// generate a list of players sorted by score
	Vector players = new Vector();
	Enumeration enum = enumeratePlayers();
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
		s = "client " + x + " " + y + " " + p.getPlayerNum() + " " + p.fScore + " " + p.getPlayerPing() + " " + (int)((Game.gGameTime - p.fStartTime) / 60) + " "; 
		// the last 0 should really be the number of minutes the player's been in the game 		 			
		if ((sb.length() + s.length()) < 1024)
			sb.append(s);		
		}
				
	Engine.writeByte(Engine.SVC_LAYOUT);
	Engine.writeString(sb.toString());
	}
}