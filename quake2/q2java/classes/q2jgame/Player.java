
package q2jgame;

/**
 * This class was generated by a SmartGuide.
 * 
 */
import java.util.*;
import q2java.*;
import q2jgame.spawn.*;
import q2jgame.weapon.*;

public class Player extends GenericCharacter implements NativePlayer
	{	
	private Hashtable fUserInfo;
	private Hashtable fInventory;
	private Hashtable fAmmoBelt;

	private PlayerWeapon fWeapon;
	private PlayerWeapon fNextWeapon;	
	private AmmoPack fAmmo;
	
	private int fHand;
	private boolean fIsGrounded;
	private float fBobTime;
	private int fButtons;
	public float fViewHeight;
	private int fWaterType;
	private int fWaterLevel;
	private int fOldWaterLevel;
	private Vec3 fOldVelocity;
	
	// temp vectors for endFrame()
	private Vec3 fRight;
	private Vec3 fForward;
	private Vec3 fUp;
	
	// handedness values
	public final static int RIGHT_HANDED		= 0;
	public final static int LEFT_HANDED		= 1;
	public final static int CENTER_HANDED	= 2;
	
	// button bits
	public final static int BUTTON_ATTACK	= 1;
	public final static int BUTTON_USE		= 2;
	public final static int BUTTON_ANY		= 128; // any key whatsoever	
	
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
	userinfoChanged(userinfo);
	
	// create temporary vectors
	fRight = new Vec3();
	fForward = new Vec3();
	fUp = new Vec3();
	}
/**
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 * @param count int
 * @param icon int
 */
public void addAmmo(String ammoType, int count) 
	{
	AmmoPack pack = (AmmoPack) fAmmoBelt.get(ammoType);
	if (pack == null)
		fAmmoBelt.put(ammoType, new AmmoPack(-1, 0));
	else
		{
		if (pack == fAmmo)
			alterAmmoCount(count);  // will also update HUD
		else			
			pack.fAmount += count;		
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
		setStat(STAT_AMMO, (short) fAmmo.fAmount);
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param loadgame boolean
 */
public void begin(boolean loadgame) 
	{
	Engine.debugLog("Player.begin(" + loadgame + ")");
	
	fInventory = new Hashtable();
	fAmmoBelt = new Hashtable();
	
	GameEntity spawnPoint = null;
	java.util.Enumeration enum = enumerateEntities("q2jgame.spawn.info_player_start");
	while (enum.hasMoreElements())
		{
		spawnPoint = (GameEntity) enum.nextElement();
		if (spawnPoint.getSpawnArg("targetname") == null)
			break;
		}
		
	if (spawnPoint == null)
		Engine.dprint("Didn't find info_player_start\n");
	else
		{							
		Vec3 origin = spawnPoint.getOrigin();
		origin.z += 9;
		setOrigin(origin);
		setAngles(spawnPoint.getAngles());
		}

	fViewHeight = 22;
	setSolid(SOLID_BBOX);
	setClipmask(Engine.MASK_PLAYERSOLID);
	
	// initialize the AmmoBelt
	fAmmoBelt.put("bullets", new AmmoPack(100, Engine.imageIndex("a_bullets")));
	fAmmoBelt.put("cells", new AmmoPack(100, Engine.imageIndex("a_cells")));
	fAmmoBelt.put("grenades", new AmmoPack(100, Engine.imageIndex("a_grenades")));
	fAmmoBelt.put("rockets", new AmmoPack(100, Engine.imageIndex("a_rockets")));
	fAmmoBelt.put("shells", new AmmoPack(100, Engine.imageIndex("a_shells")));
	fAmmoBelt.put("slugs", new AmmoPack(100, Engine.imageIndex("a_slugs")));

	// bring up the initial weapon
	try
		{
		// start off with a plain blaster
		fWeapon = new Blaster();
		fWeapon.use(this);

		// stick it in our inventory too
		putInventory("blaster", fWeapon);
/*		
		// give the user other weapons too, for testing
		putInventory("hyperblaster", new weapon_hyperblaster());
		putInventory("machinegun", new weapon_machinegun());
		putInventory("chaingun", new weapon_chaingun());
		putInventory("shotgun", new weapon_shotgun());
		putInventory("super shotgun", new weapon_supershotgun());
		putInventory("railgun", new weapon_railgun());
*/		
		}
	catch (GameException e)
		{
		Engine.dprint(e + "\n");
		}		
	
	setEffects(0);
	setSkinNum(getPlayerNum());
	setModelIndex(255);	// will use the skin specified model
	setModelIndex2(255);	// custom gun model	
	setFrame(0);
	setMins(-16, -16, 24);
	setMaxs(16, 16, 32);
	setStat(STAT_HEALTH_ICON, (short)worldspawn.fHealthPic);
	linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 */
public void beginFrame() 
	{
	if (fWeapon != null)
		fWeapon.weaponThink();
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
	
	value = Game.fRollAngle.getFloat();

	if (side < Game.fRollSpeed.getFloat())
		side = side * value / Game.fRollSpeed.getFloat();
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
	
	Vec3 velocity = getVelocity();
	float xyspeed = (float)Math.sqrt((velocity.x*velocity.x) + (velocity.y*velocity.y));
	
	if (xyspeed < 5.0)
		{
		bobMove = 0;
		fBobTime = 0;
		}
	else if (fIsGrounded)
		{
		if (xyspeed > 210)
			bobMove = 0.25F;
		else if (xyspeed > 100)
			bobMove = 0.125F;
		else
			bobMove = 0.0625F;		
		}		
		
	fBobTime += bobMove;
	
	float bobfracsin = (float) Math.abs(Math.sin(fBobTime*Math.PI));			
	// add bob height
	float bob = bobfracsin * xyspeed * Game.fBobUp.getFloat(); // *3 added to magnify effect
	if (bob > 6)
		bob = 6.0F;
	v.z += bob;	
	
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
		
	setViewOffset(v);
	}
/**
 * This method was created by a SmartGuide.
 */
public void changeWeapon() 
	{
	if (fNextWeapon == null)
		return;
		
	fWeapon = fNextWeapon;
	fNextWeapon = null;
	fWeapon.use(this);		
	}
/**
 * This method was created by a SmartGuide.
 */
public void command() 
	{
	Engine.debugLog("Player.command()");
	Engine.dprint("Java Player.command()\n");
	Engine.dprint("   Engine.args = [" + Engine.args() + "]\n");
	Engine.dprint("   Engine.argc = " + Engine.argc() + "\n");
	for (int i = 0; i < Engine.argc(); i++)
		Engine.dprint("    " + i + ": [" + Engine.argv(i) + "]\n");
		
		
	if (Engine.argc() > 1)
		{
		if (Engine.argv(0).equals("use"))
			{
			String item = Engine.argv(1);
			for (int i = 2; i < Engine.argc(); i++)
				item = item + " " + Engine.argv(i);
			use(item);
			}
		}		
	}
/**
 * The player is disconnecting, clean things up and say goodbye.
 *
 * Be sure you drop all references to this player object.  
 */
public void disconnect()
	{
	Engine.debugLog("Player.disconnect()");

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), "");	
	}
/**
 * Called for each player after all the entities have 
 * had a chance to runFrame()
 */
public void endFrame() 
	{	
	
	//
	// set model angles from view angles so other things in
	// the world can tell which direction you are looking
	//
	Vec3 newAngles = getViewAngles();
	newAngles.angleVectors(fForward, fRight, fUp);
	if (newAngles.x > 180) 
		newAngles.x = newAngles.x - 360;
	newAngles.x /= 3;		
	newAngles.z = calcRoll(getVelocity());
	setAngles(newAngles);
	
	worldEffects();
	fallingDamage();
	calcViewOffset();	
	}
/**
 * This method was created by a SmartGuide.
 */
private void fallingDamage() 
	{
	// no damage if you're airborne
	if (!fIsGrounded)
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
/*			
	ent->pain_debounce_time = level.time;	// no normal pain sound
	damage = (delta-30)/2;
	if (damage < 1)
		damage = 1;
	VectorSet (dir, 0, 0, 1);

	if (!deathmatch->value || !((int)dmflags->value & DF_NO_FALLING) )
		T_Damage (ent, world, world, dir, ent->s.origin, vec3_origin, damage, 0, 0);
*/			
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param itemname java.lang.String
 */
public int getAmmoCount(String ammoName) 
	{
	if (fAmmo == null)
		return 0;
	else
		return fAmmo.fAmount;				
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @param key java.lang.String
 */
public String getUserInfo(String key) 
	{
	if (fUserInfo == null)
		return null;
	return (String) fUserInfo.get(key);		
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isAttacking() 
	{
	return (fButtons & Player.BUTTON_ATTACK) != 0;
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
 * This method was created by a SmartGuide.
 * @param ammoType java.lang.String
 */
public void setAmmoType(String ammoType) 
	{
	fAmmo = (ammoType == null ? null : (AmmoPack) fAmmoBelt.get(ammoType));	
	if (fAmmo == null)
		{
		setStat(STAT_AMMO, (short) 0);
		setStat(STAT_AMMO_ICON, (short) 0);
		}
	else
		{
		setStat(STAT_AMMO, (short) fAmmo.fAmount);
		setStat(STAT_AMMO_ICON, (short) fAmmo.fIcon);
		}	
	}
/**
 * This method was created by a SmartGuide.
 * @param val int
 */
public void setHealth(int val) 
	{
	super.setHealth(val);
	setStat(STAT_HEALTH, (short)fHealth);
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param base java.lang.String
 */
public int sexedSoundIndex(String base) 
	{
	return Engine.soundIndex((fIsFemale ? "player/female/" : "player/male/") + base + ".wav");
	}
/**
 * All player entities get a chance to think.  When
 * a player entity thinks, it has to handle the 
 * users movement commands by calling pMove();
 */
public void think()
	{
	fOldVelocity = getVelocity();
	
	PMoveResults pm = pMove();
	
	if (fIsGrounded && (pm.fGroundEntity == null) && (pm.fCmdUpMove >= 10) && (pm.fWaterLevel == 0))
		sound(CHAN_VOICE, sexedSoundIndex("jump1"), 1, ATTN_NORM, 0);
		
	fButtons = pm.fCmdButtons;		
	fViewHeight = pm.fViewHeight;	
	fWaterType = pm.fWaterType;
	fWaterLevel = pm.fWaterLevel;	
	fIsGrounded = (pm.fGroundEntity != null);	

	linkEntity();	
	
	// notify all the triggers we're intersecting with
	NativeEntity[] triggers = boxEntity(Engine.AREA_TRIGGERS);
	if (triggers != null)
		{
		for (int i = 0; i < triggers.length; i++)
			((GameEntity)triggers[i]).touch(this);
		}
	
	// notify everything the player has collided with
	if (pm.fTouched != null)
		{
		for (int i = 0; i < pm.fTouched.length; i++)
			((GameEntity)pm.fTouched[i]).touch(this);
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
		cprint(Engine.PRINT_HIGH, "You don't have a " + itemName);
		return;
		}

	// handle weapons a little differently
	if (ent instanceof PlayerWeapon)
		{
		if (ent == fWeapon)
			return; // do nothing if we're already using the weapon
		
		// make a note of what the next weapon will be	
		fNextWeapon = (PlayerWeapon) ent;
		
		// signal the current weapon to deactivate..when it's
		// done deactivating, it will signal back to the player to 
		// switchWeapons() and we'll use() the next weapon		
		fWeapon.deactivate();	

		return;
		}
			
//	ent.use(this);	---FIXME--
	}
public void userinfoChanged(String userinfo)
	{
	Engine.debugLog("Player.userinfoChanged(\"" + userinfo +"\")");
	// Break the userinfo string up and store the info in a hashtable
	// The format of the string is:
	//    \keyword\value\keyword\value\....\keyword\value
	
	fUserInfo = new Hashtable();
	StringTokenizer st = new StringTokenizer(userinfo, "\\");
	while (st.hasMoreTokens())
		{
		String key = st.nextToken();
		if (st.hasMoreTokens())
			fUserInfo.put(key, st.nextToken());
		}
		
		
	// change some settings based on what was in the userinfo string	
	String s = getUserInfo("name");
	String skin = getUserInfo("skin");

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), s + "\\" + skin);			
	
	fIsFemale = ((skin != null) && (skin.length() > 0) && ((skin.charAt(0) == 'F') || (skin.charAt(0) == 'f')));
		
	s = getUserInfo("hand");
	if (s != null)
		fHand = Integer.parseInt(s);			
		
	s = getUserInfo("fov");
	if (s != null)
		setFOV((new Float(s)).floatValue());				
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
}