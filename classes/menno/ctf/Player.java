package menno.ctf;


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

import q2jgame.*;
import q2java.*;
import javax.vecmath.*;
import menno.ctf.spawn.*;
import java.util.*;	// remove..

public class Player extends baseq2.Player implements CameraListener
{
	public static final int STAT_CTF_TECH             = 26;
	public static final int STAT_CTF_ID_VIEW          = 27;

	public static final int CTF_CAPTURE_BONUS		   = 15;	// what you get for capture
	public static final int CTF_TEAM_BONUS			   = 10;	// what your team gets for capture
	public static final int CTF_RECOVERY_BONUS		   =  1;	// what you get for recovery
	public static final int CTF_FLAG_BONUS			   =  0;	// what you get for picking up enemy flag
	public static final int CTF_FRAG_CARRIER_BONUS	   =  2;	// what you get for fragging enemy flag carrier

	public static final int CTF_CARRIER_DANGER_PROTECT_BONUS = 2;	// bonus for fraggin someone who has recently hurt your flag carrier
	public static final int CTF_CARRIER_PROTECT_BONUS        = 1;	// bonus for fraggin someone while either you or your target are near your flag carrier
	public static final int CTF_FLAG_DEFENSE_BONUS           = 1;	// bonus for fraggin someone while either you or your target are near your flag
	public static final int CTF_RETURN_FLAG_ASSIST_BONUS     = 1;	// awarded for returning a flag that causes a capture to happen almost immediately
	public static final int CTF_FRAG_CARRIER_ASSIST_BONUS    = 2;	// award for fragging a flag carrier if a capture happens almost immediately
	
	public static final int CTF_TARGET_PROTECT_RADIUS        = 400;	// the radius around an object being defended where a target will be worth extra frags
	public static final int CTF_ATTACKER_PROTECT_RADIUS      = 400;	// the radius around an object being defended where an attacker will get extra frags when making kills

	public static final int CTF_CARRIER_DANGER_PROTECT_TIMEOUT =  8;
	public static final int CTF_FRAG_CARRIER_ASSIST_TIMEOUT    = 10;
	public static final int CTF_RETURN_FLAG_ASSIST_TIMEOUT     = 10;



	public final static String CTF_STATUSBAR = 
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
		  "xv 246 " +
		  "num 2 10 " +
		  "xv 296 " +
		  "pic 9 " +
		"endif " +

		//  help / weapon icon 
		"if 11 " +
		  "xv 148 " +
		  "pic 11 " +
		"endif " +

		//  frags
		"xr	-50 " +
		"yt 2 " +
		"num 3 14 " +

		//tech
		"yb -129 " +
		"if 26 " +
		  "xr -26 " +
		  "pic 26 " +
		"endif " +

		// red team
		"yb -102 " +
		"if 17 " +
		  "xr -26 " +
		  "pic 17 " +
		"endif " +
		"xr -62 " +
		"num 2 18 " +
		//joined overlay
		"if 22 " +
		  "yb -104 " +
		  "xr -28 " +
		  "pic 22 " +
		"endif " +

		// blue team
		"yb -75 " +
		"if 19 " +
		  "xr -26 " +
		  "pic 19 " +
		"endif " +
		"xr -62 " +
		"num 2 20 " +
		"if 23 " +
		  "yb -77 " +
		  "xr -28 " +
		  "pic 23 " +
		"endif " +

		// have flag graph
		"if 21 " +
		  "yt 26 " +
		  "xr -24 " +
		  "pic 21 " +
		"endif " +

		// id view state
		"if 27 " +
		  "xv 0 " +
		  "yb -58 " +
		  "string \"Viewing\" " +
		  "xv 64 " +
		  "stat_string 27 " +
		"endif ";

	protected Team     fTeam;
	protected float    fLastCarrierHurt;
	public    float    fLastTechMessage;
	public    ChaseCam fChaser;			// The ChaseCam that's following us...
	protected ChaseCam fViewer;			// The ChaseCam where we could be looking trough...

	protected boolean  fIsSpectator;
	protected boolean  fIsChasing;

	//=====================================================
	// Methods for testing things (ignore)
	//=====================================================
	public CTFMenu fMenu = null;
	//========================================================================
	// Constructors
	//========================================================================
	/**
	* The constructor is called every time a player connects to the server.
	* It sets some local vars which are consistent during the entire game.
	**/
	public Player(NativeEntity ent, boolean loadgame) throws GameException
	{
		super( ent, loadgame );
		
		// make sure the grapple VWep skin is cached before we set the player's skin
		baseq2.GenericWeapon.precacheVWep(".spawn.weapon_grapple");
		
		fTeam     = null;
		fViewer   = null;
		fChaser   = new ChaseCam( this );

		// setup the icons
		fEntity.setPlayerStat( Team.STAT_CTF_TEAM1_PIC,  (short)Engine.getImageIndex("i_ctf1") );
		fEntity.setPlayerStat( Team.STAT_CTF_TEAM1_CAPS, (short)Team.TEAM1.getCaptures()       );
		fEntity.setPlayerStat( Team.STAT_CTF_TEAM2_PIC,  (short)Engine.getImageIndex("i_ctf2") );
		fEntity.setPlayerStat( Team.STAT_CTF_TEAM2_CAPS, (short)Team.TEAM2.getCaptures()       );
	}
	//========================================================================
	// Methods
	//========================================================================

	/**
	* Add a flag to the players inventory
	**/
	protected boolean addFlag( GenericFlag flag )
	{
		if (flag.getTeam() == getTeam())
		{
			// player touches their own flag
			if (flag.getState() == GenericFlag.CTF_FLAG_STATE_STANDING)
			{
				// ... while it's standing
				GenericFlag otherFlag = (GenericFlag)getInventory("flag");
				if (otherFlag != null)
				{
					// WE HAVE A CAPTURE !!!!!
					Object[] args = {getName(), otherFlag.fFlagIndex};
					Game.localecast("menno.ctf.CTFMessages", "capture_flag", args, Engine.PRINT_HIGH);	

					flag.playCaptureSound();
		
					// drop the captured flag and send it back to its base
					otherFlag.drop(this, 0);
					otherFlag.reset();
	
					// put the player back to normal				
					fEntity.setEffects( fEntity.getEffects() & ~otherFlag.getFlagEffects() );
					fEntity.setModelIndex3( 0 );
					fEntity.setPlayerStat( GenericFlag.STAT_CTF_FLAG_PIC, (short)0 );
					removeInventory( "flag" );
		
					// Add the bonuses...
					getTeam().addCapture( this );				
				}
			}
			else if (flag.getState() == GenericFlag.CTF_FLAG_STATE_DROPPED)
			{
				// was dropped on the ground
				setScore( Player.CTF_RECOVERY_BONUS, false );
				flag.reset();
				Object[] args = {getName(), flag.fFlagIndex};
				Game.localecast("menno.ctf.CTFMessages", "return_flag", args, Engine.PRINT_HIGH);	
				flag.playReturnSound();
			}
			return false; //you don't actually take your own flag
		}
		
		// Don't add flag if already have one
		if ( isCarrying("flag") )
			return false;

		// add flag to inventory
		fEntity.setPlayerStat( GenericFlag.STAT_CTF_FLAG_PIC, (short)Engine.getImageIndex(flag.getIconName()) );
		fEntity.setEffects( fEntity.getEffects() | flag.getFlagEffects() );
		fEntity.setModelIndex3( Engine.getModelIndex(flag.getModelName()) );
		putInventory( "flag", flag );
		setScore( getScore() + CTF_FLAG_BONUS );
		flag.pickupBy(this);
		
		return true;
	}
	/**
	 * Add item to the player's inventory.
	 * @param item Item we're trying to add.
	 * @return boolean true if the item was taken.
	 */
	public boolean addItem(baseq2.GenericItem item) 
	{
		if (item == null)
			return false;

		if (item instanceof GenericTech)
			return addTech((GenericTech) item);

		if (item instanceof GenericFlag)
			return addFlag((GenericFlag) item);
			
		return super.addItem(item);
	}
	/**
	* Add a tech to the players inventory
	**/
	protected boolean addTech( GenericTech tech )
	{
		// Don't add tech if already have one
		if ( isCarrying("tech") )
		{
//			if ( Game.getGameTime() > (fLastTechMessage+2) )
			{
				fEntity.centerprint(fResourceGroup.getRandomString("menno.ctf.CTFMessages", "have_tech"));
//				fLastTechMessage = Game.getGameTime();
			}
			return false;
		}

		// add tech to inventory
		int icon = Engine.getImageIndex(tech.getIconName());
		fEntity.setPlayerStat( GenericTech.STAT_CTF_TECH, (short)icon );
		putInventory( "tech", tech );
		tech.setOwner(this);

		return true;
	}
	//=====================================================
	// Methods from CameraListener
	//=====================================================

	// called when we succesfully connected to the camera...
	public void cameraActivated( ChaseCam cam )
	{
		fViewHeight = 0f;
		fViewer     = cam;

		/*sprintf(s, "xv 0 yb -58 string2 \"Chasing %s\"",
			targ->client->pers.netname);
		gi.WriteByte (svc_layout);
		gi.WriteString (s);
		gi.unicast(ent, false);
		*/
	}
	// called when camara has been disposed...
	public void cameraDeactivated( ChaseCam cam )
	{
		// should clear some menu if I had implemented it...
		cam.removeCameraListener( this );
		fViewer = null;
	}
	// called when camaraposition has been updated...
	public void cameraUpdated( ChaseCam cam )
	{
		fEntity.setOrigin( cam.fPosition );
		fEntity.setPlayerViewAngles( cam.fAngle );
		fEntity.linkEntity();
	}
/*
	private Point3f[] buildBoxPoints( Point3f org, Point3f mins, Point3f maxs)
	{
		Point3f[] p = new Point3f[8];

		p[0] = new Point3f( org );
		p[0].add( mins );
		p[1] = new Point3f( p[0] );
		p[1].x -= mins.x;
		p[2] = new Point3f( p[0] );
		p[2].y -= mins.y;
		p[3] = new Point3f( p[0] );
		p[3].x -= mins.x;
		p[3].y -= mins.y;
		p[4] = new Point3f( org );
		p[4].add( maxs );
		p[5] = new Point3f( p[4] );
		p[5].x -= maxs.x;
		p[6] = new Point3f( p[0] );	// strange: should be p[4] ??
		p[6].y -= maxs.y;
		p[7] = new Point3f( p[0] );	// strange: should be p[4] ??
		p[7].x -= maxs.x;
		p[7].y -= maxs.y;

		return p;
	}


	public boolean canSee( baseq2.GameObject object )
	{
		TraceResults tr;
		Point3f      p[];
		Point3f      viewPoint;

		// bmodels need special checking because their origin is 0,0,0
		//if (targ->movetype == MOVETYPE_PUSH)
		//	return false; // bmodels not supported

		p = buildBoxPoints( object.fEntity.getOrigin(), object.fEntity.getMins(), object.fEntity.getMaxs() );
		
		viewPoint = new Point3f( fEntity.getOrigin() );
		viewPoint.z += fViewHeight;

		for ( int i=0; i<8; i++)
		{
			tr = Engine.trace( viewPoint, p[i], fEntity, Engine.MASK_SOLID );
			if ( tr.fFraction == 1 )
				return true;
		}

		return false;

	}
*/

	public boolean canSee( Point3f p )
	{
		TraceResults tr;
		Point3f      viewPoint;
		
		viewPoint = new Point3f( fEntity.getOrigin() );
		viewPoint.z += fViewHeight;

		tr = Engine.trace( viewPoint, p, fEntity, Engine.MASK_SOLID );
		if ( tr.fFraction == 1 )
			return true;
		else
			return false;
	}
	/**
	* Clear the player's settings so they are a fresh new Space Marine.
	* It's called everytime the player (re)spawns.
	**/
	protected void clearSettings()
	{
		baseq2.GenericWeapon weapon;

		// don't add weapons if spectator
		if ( fTeam == null )
		{
			fInventory.clear();
			return;
		}

		super.clearSettings();

		// put the grapple in inventory
		addWeapon( ".spawn.weapon_grapple", false );

		// remove any techs and flags, if we changed to another level
		fInventory.remove( "tech" );
		fInventory.remove( "flag" );

		// set field VERY low...
		fLastCarrierHurt = Float.MIN_VALUE;
		fLastTechMessage = Float.MIN_VALUE;
	}
	/**
	 * ChaseCam
	 * @param (Ignored)
	 */
	public void cmd_chasecam(String[] args) 
	{
		if ( fViewer != null )	//already connected to another chasecam, so disconnect it and return
		{
			fViewer.removeCameraListener( this );
			fViewer      = null;
			fIsSpectator = true;
			fIsChasing   = false;
			return;
		}

		// find a chasecam
		fViewer = fChaser.getNextChaseCam( null );

		if ( fViewer != null )
		{
			// leave any team
			if ( fTeam != null )
			{
				fTeam.removePlayer( this );
				fTeam = null;
			}

			//die( null, null, 0, null );
			//fShowScore = false;
			setScore( 0 );
			clearSettings();
			spawn();

			fViewer.addCameraListener( this );
			fEntity.setPlayerPMType( NativeEntity.PM_FREEZE );
			fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
			fEntity.linkEntity();

			// deactivate our chasecam
			fChaser.setActive( false );
			fIsSpectator = false;
			fIsChasing   = true;
		}
		else
			fEntity.cprint( Engine.PRINT_MEDIUM, "No players to chase..\n" );
	}
	/**
	 * Drop item
	 * @param (Ignored, uses the Engine.args() value instead)
	 */
	public void cmd_drop(String[] args) 
	{
		String itemName = Engine.getArgs().toLowerCase();
		
		if ( itemName.equals("flag") )
			dropFlag();
		else if ( itemName.equals("tech") )
			dropTech();
		else
			super.cmd_drop( args );
	}
	public void cmd_inven(String[] args)
	{
		if ( fMenu == null )
		{
			// show the menu
			fMenu = new CTFMenu( this );
			fMenu.show();
		}
		else
		{
			// close the menu
			fMenu.close();
			fMenu = null;
		}

	}
	public void cmd_invnext( String[] args )
	{
		if ( fMenu != null )
			fMenu.selectNextItem();
		else if ( fViewer != null )
		{
			ChaseCam nextCam = fChaser.getNextChaseCam( fViewer );
			if ( nextCam != null )
			{
				fViewer.removeCameraListener( this );
				fViewer = nextCam;
				fViewer.addCameraListener( this );
			}
		}
		// TODO:
		// else
		//	super.cmd_invuse( args );
	}
	public void cmd_invprev( String[] args )
	{
		if ( fMenu != null )
			fMenu.selectPreviousItem();
		else if ( fViewer != null )
		{
			ChaseCam nextCam = fChaser.getPreviousChaseCam( fViewer );
			if ( nextCam != null )
			{
				fViewer.removeCameraListener( this );
				fViewer = nextCam;
				fViewer.addCameraListener( this );
			}
		}
		// TODO:
		// else
		//	super.cmd_invuse( args );
	}
	/**
	 * Called when player presses Enter key
	 */
	public void cmd_invuse( String[] args )
	{
		if ( fMenu == null )
			super.cmd_invuse(args);
		else		
		{
			fMenu.selectMenuItem();
			fMenu.close();
			fMenu = null;
		}
	}
	/**
	 * Put's away the scorboard
	 * when the scoreboard is displayed and the esc key is hit the cmd is called
	 * when the help computer or inventory display is implemented this needs to shut those off also
	 * @param args java.lang.String[]
	 */
	public void cmd_putaway( String[] args )
	{
		if ( fMenu != null )
			cmd_inven( null );
		
		super.cmd_putaway( args );
	}
	/**
	 * Spectator
	 * @param (Ignored)
	 */
	public void cmd_spectator(String[] args) 
	{
		if ( fIsSpectator )
			return;

		// leave any camera
		if ( fViewer != null )
		{
			fViewer.removeCameraListener( this );
			fViewer = null;
		}

		// leave any team
		if ( fTeam != null )
		{
			fTeam.removePlayer( this );
			fTeam = null;
		}

		// close menu
		if ( fMenu != null )
			cmd_inven( null );

		// deactivate our chasecam
		fChaser.setActive( false );
		fIsSpectator = true;
		fIsChasing   = false;

		//die( null, null, 0, null );
		//fShowScore = false;
		//respawn();
		setScore( 0 );
		clearSettings();
		spawn();
	}
	/**
	 * Change team
	 */
	public void cmd_team(String[] args) 
	{
		Team   oldTeam, newTeam;
		String teamName = "";
		
		if ( args.length > 1 )
			teamName = args[1].toLowerCase();

		if ( teamName.equals("red") )
			newTeam = Team.TEAM1;

		else if ( teamName.equals("blue") )
			newTeam = Team.TEAM2;

		else
		{
			fEntity.cprint(Engine.PRINT_HIGH, "Unknown team: " + teamName + "\n");
			return;
		}
		
		if ( newTeam == fTeam )
		{
			fEntity.cprint(Engine.PRINT_HIGH, "You are already on the " + teamName + " team.\n");
			return;	// Can't change to same team
		}

		// remove from old team if was there, and join new team...
		if ( fTeam != null )
			fTeam.removePlayer( this );

		newTeam.addPlayer( this );
		fTeam = newTeam;

		// drop flag and tech if carrying
		dropFlag();
		dropTech();

		// respawn to new base and set score to zero...
		//die( null, null, 0, null );
		setScore( 0 );
		//respawn();
		clearSettings();
		spawn();
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
	public void damage(	baseq2.GameObject inflictor, baseq2.GameObject attacker, 
						Vector3f dir, Point3f point, Vector3f normal, 
						int damage, int knockback, int dflags, int tempEvent, String obitKey) 
	{
		GenericTech tech;

		// don't take any more damage if already dead
		if (fIsDead)
			return;
			
		if ( ( attacker instanceof Player ) && ( attacker != this ))
		{
			// A CTF Player other than ourselves attacked us
			
			Player p = (Player)attacker;
			if ( p.fTeam == fTeam )
				return;  // Teammates can't hurt you

			// If we have a flag, and attacker is playing on other team,
			// mark the attacker that he was aggressive to the flag-carrier.
			if ( isCarrying("flag") )
				p.fLastCarrierHurt = Game.getGameTime();
		}

		// if we are carrying the Disruptor Shield,
		// apply it to us and get the damage-multiplier (0.0-1.0)
		tech = (GenericTech)getInventory( "tech" );
		if ( tech instanceof item_tech1 )
		{
			damage *= ((item_tech1)tech).getDamageMultiplier();
			((item_tech1)tech).playSound();
		}

		super.damage( inflictor, attacker, dir, point, normal, damage, knockback, dflags, tempEvent, obitKey );
	}
	protected void die( baseq2.GameObject inflictor, baseq2.GameObject attacker, int damage, Point3f point, String obitKey)
	{
		if (fIsDead)
			return;	// already dead

		// reset Grapple if carrying
		if ( fWeapon instanceof weapon_grapple )
			( (weapon_grapple)fWeapon ).reset();

		super.die( inflictor, attacker, damage, point, obitKey );
	}
	/**
	 * Disassociate the CTF player object from the rest 
	 * of the game.
	 */
	public void dispose() 
	{
		// remove from team if was there
		if ( fTeam != null )
			fTeam.removePlayer( this );

		// reset Grapple if carrying
		if ( fWeapon instanceof weapon_grapple )
			( (weapon_grapple)fWeapon ).reset();

		// drop flag and tech if carrying
		dropFlag();
		dropTech();

		// remove from chasecam if viewing
		if ( fViewer != null )
		{
			fViewer.removeCameraListener( this );
			fViewer = null;
		}

		// and remove our chasecam
		fChaser.dispose();
		
		super.dispose();
	}
	private void dropFlag()
	{
		// drop flag if carrying
		GenericFlag flag = (GenericFlag) fInventory.get( "flag" );
		if ( flag != null )
		{
			fInventory.remove( "flag" );
			flag.drop(this);
			fEntity.setEffects( fEntity.getEffects() & ~flag.getFlagEffects() );
			fEntity.setModelIndex3( 0 );
			fEntity.setPlayerStat( GenericFlag.STAT_CTF_FLAG_PIC, (short)0 );
		}
	}
	/**
	 * Drop things on the ground when dead.
	 */
	protected void dropInventory() 
	{
		// handle dropping the basic stuff
		super.dropInventory();
		
		// drop flag and tech if carrying
		dropFlag();
		dropTech();	
	}
	private void dropTech()
	{
		// drop tech if carrying
		GenericTech tech = (GenericTech) fInventory.get( "tech" );
		if ( tech != null )
		{
			fInventory.remove( "tech" );

			tech.setOwner(null);
			tech.drop(this, GenericTech.CTF_TECH_TIMEOUT);
			
			fEntity.setPlayerStat( GenericTech.STAT_CTF_TECH, (short)0 );
		}
	}
	protected void endServerFrame()
	{
		if (fInIntermission)
			Team.blinkDeathmatchScoreboard( this );

		super.endServerFrame();
	}
	/**
	 * Override baseq2.Player.getSpawnpoint() to use team 
	 * spawnpoints if the player belongs to a team.
	 * @return baseq2.GenericSpawnpoint
	 */
	protected baseq2.GenericSpawnpoint getSpawnpoint() 
	{
		if ( fTeam != null )
			return fTeam.getSpawnpoint();
		else
			return super.getSpawnpoint();
	}
	public Team getTeam()
	{
		return fTeam;
	}
	public boolean isChasing()
	{
		return fIsChasing;
	}
	/**
	 * All player entities get a chance to think.  When
	 * a player entity thinks, it has to handle the 
	 * users movement commands by calling pMove().
	 * @param cmd commands from the client..indicate movement, jumping, weapon firing.
	 */
	public void playerThink( PlayerCmd cmd )
	{
		if (fInIntermission)
			return;
		
		if ( fIsChasing )
		{
			fEntity.linkEntity();
			return;
		}

		if ( fIsSpectator )
		{
			PMoveResults pm = fEntity.pMove(cmd, Engine.MASK_PLAYERSOLID);
			fEntity.linkEntity();
			return;
		}

		super.playerThink( cmd );

		if ( fWeapon instanceof weapon_grapple )
		{
			GrappleHook hook = ((weapon_grapple)fWeapon).getHook();
			if ( hook != null )
			{ 
				if ( hook.getState() == GrappleHook.CTF_GRAPPLE_STATE_PULLING 
				  || hook.getState() == GrappleHook.CTF_GRAPPLE_STATE_HANGING )
					 hook.pull();
			}
		}

		// update our chasecam in case we are being watched
		fChaser.update();
	}
	/**
	 * Change user settings based on what was in userinfo string.
	 */
	protected void playerVariableChanged(String key, String newVal, String oldVal)
	{
		super.playerVariableChanged(key, newVal, oldVal);

		// overrule skin if on team
		if (key.equals("skin") && (fTeam != null))
			fTeam.assignSkinTo( this );
	}
	/**
	* Calculate the bonuses for flag defense, flag carrier defense, etc.
	* Note that bonuses are not cumaltive.  You get one, they are in importance
	* order.
	**/
	protected void registerKill( Player victim )
	{
		if ( victim.fTeam == fTeam )
		{
			// only add points to attacker when victim is on other team.
			return;
		}

		// did the victim carry the flag?
		if ( victim.getInventory("flag") != null )
		{
			Object[] args = {new Integer(CTF_FRAG_CARRIER_BONUS)};
			fEntity.centerprint(fResourceGroup.format("menno.ctf.CTFMessages", "bonus_points", args) + "\n");

			// The victim had the flag, clear the hurt carrier field on our team
			Player[] players = fTeam.getPlayers();

			for ( int i=0; i<players.length; i++) 
				players[i].fLastCarrierHurt = 0f;

			setScore(1 + CTF_FRAG_CARRIER_BONUS, false);
		}

		// was the victim aggressive against our flagcarrier lately?
		if ( Game.getGameTime() < (victim.fLastCarrierHurt+CTF_CARRIER_DANGER_PROTECT_TIMEOUT)
				&& getInventory("flag") == null )
		{
			Object[] args = {getName(), fTeam.getTeamIndex()};
			Game.localecast("menno.ctf.CTFMessages", "defend_aggressive", args, Engine.PRINT_MEDIUM);	
		
			setScore(1 + CTF_CARRIER_DANGER_PROTECT_BONUS, false);
		}

		// if our flag is laying around somewhere, we can get extra bonuses
		GenericFlag ourFlag = fTeam.getFlag();

		//if ( ourFlag.getState() == GenericFlag.CTF_FLAG_STATE_CARRIED )
		//	return;

		// check to see if we are defending the base.
		Vector3f v1 = new Vector3f( this.fEntity.getOrigin()   );
		Vector3f v2 = new Vector3f( victim.fEntity.getOrigin() );
		v1.sub( fTeam.getBaseOrigin() );
		v2.sub( fTeam.getBaseOrigin() );
		
		//if ( v1.length() < CTF_TARGET_PROTECT_RADIUS || v2.length() < CTF_TARGET_PROTECT_RADIUS 
		//	|| this.canSee(ourFlag) || victim.canSee(ourFlag) )
		if ( v1.length() < CTF_TARGET_PROTECT_RADIUS || v2.length() < CTF_TARGET_PROTECT_RADIUS 
			|| this.canSee(fTeam.getBaseOrigin()) || victim.canSee(fTeam.getBaseOrigin()) )
		{
			// OK, either we or our victim is in sight of our base.
			// Send message based on if the flag is at base or not...
			Object[] args = {getName(), fTeam.getTeamIndex()};
			
			if ( ourFlag.getState() == GenericFlag.CTF_FLAG_STATE_STANDING )
				Game.localecast("menno.ctf.CTFMessages", "defend_flag", args, Engine.PRINT_MEDIUM);	
			else
				Game.localecast("menno.ctf.CTFMessages", "defend_base", args, Engine.PRINT_MEDIUM);	

			setScore(1 + CTF_FLAG_DEFENSE_BONUS, false);
		}

		// No bonusses left if carrying flag...
		if ( isCarrying("flag") )
			setScore(1, false);

		// check to see if we are defending someone on our team with enemy flag.
		GenericFlag enemyFlag = ( Team.TEAM1.getFlag() == ourFlag ? Team.TEAM2.getFlag() : Team.TEAM1.getFlag() );

		if ( enemyFlag.getState() == GenericFlag.CTF_FLAG_STATE_CARRIED )
		{
			Player carrier = enemyFlag.getCarrier();
			v1.set( carrier.fEntity.getOrigin() );
			v2.set( carrier.fEntity.getOrigin() );
			v1.sub( this.fEntity.getOrigin() );
			v2.sub( victim.fEntity.getOrigin() );

			if ( v1.length() < CTF_ATTACKER_PROTECT_RADIUS || v2.length() < CTF_ATTACKER_PROTECT_RADIUS 
				|| this.canSee(carrier.fEntity.getOrigin()) || victim.canSee(carrier.fEntity.getOrigin()) )
			{
				Object[] args = {getName(), fTeam.getTeamIndex()};
				Game.localecast("menno.ctf.CTFMessages", "defend_carrier", args, Engine.PRINT_MEDIUM);	
			
				setScore(1 + CTF_CARRIER_PROTECT_BONUS, false);
			}
		}
		
		// Hmm, no bonusses left...
		setScore(1, false);
	}
	/**
	 * Say something to the members of our team.
	 * @param (Ignored, uses the Engine.args() value instead)
	 */
	public void sayTeam(String msg) 
	{
		if ( fTeam == null )
			say(msg);
		else
			fTeam.say( this, msg );
	}
	/**
	 * spawn the player into the game.
	 */
	protected void spawn() 
	{
		super.spawn();

		if ( fTeam != null )
		{
			// remove from chasecam if viewing
			if ( fViewer != null )
			{
				fViewer.removeCameraListener( this );
				fViewer = null;
			}

			fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT);
			fEntity.setPlayerPMType( NativeEntity.PM_NORMAL );
			fEntity.linkEntity();

			fChaser.setActive( true );
			fIsSpectator = false;
			fIsChasing   = false;
		}
		else
		{
			// set as spectator
			fEntity.setSolid( NativeEntity.SOLID_NOT );
			fEntity.setPlayerPMType( NativeEntity.PM_SPECTATOR );	
			fEntity.setClipmask( Engine.MASK_PLAYERSOLID );	
			fEntity.setSkinNum( 0 );
			fEntity.setModelIndex( 0 );
			fEntity.setModelIndex2( 0 );
			fEntity.setPlayerGunIndex(0);	
			fWeapon = null;
			fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
			fEntity.linkEntity();

			fChaser.setActive( false );
			fIsSpectator = true;
			fIsChasing   = false;

			// show the menu
			cmd_inven( null );
		}
	}
	/**
	 * Teleport the player to another point in the map.
	 * @param origin javax.vecmath.Point3f
	 * @param angles q2java.Angle3f
	 */
	public void teleport(Point3f origin, Angle3f angles) 
	{
		if ( fWeapon instanceof weapon_grapple )
			( (weapon_grapple)fWeapon ).reset();
		
		super.teleport( origin, angles );
	}
	/**
	 * Welcome the player to the game.
	 * (same as baseq2.Player.welcome() except that it omits
	 * the centerprint call that baseq2 makes)
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
	}
	/**
	 * Send CTF Scoreboard to client.
	 * @param killer the Player who killed this one (if any).
	 */
	protected void writeDeathmatchScoreboardMessage( baseq2.GameObject killer ) 
	{	
		int     x, y;
		String  s;
		boolean needSpec = true;

		// We're going to let the Teams write their own scoreboard..
		s  = Team.TEAM1.getDeathmatchScoreboardMessage( this, killer, fInIntermission );
		s += Team.TEAM2.getDeathmatchScoreboardMessage( this, killer, fInIntermission );

		// put in spectators
		if ( Team.TEAM1.getNumPlayers() > Team.TEAM2.getNumPlayers() )
			y = (Team.TEAM1.getNumPlayers() + 2) * 8 + 42;
		else
			y = (Team.TEAM2.getNumPlayers() + 2) * 8 + 42;

		x = 0;
		
		Enumeration enum = NativeEntity.enumeratePlayers();
		while ( enum.hasMoreElements() )
		{
			Player p = (Player)((NativeEntity)enum.nextElement()).getReference();
			if ( p.fTeam != null )
				continue;

			if ( needSpec )
			{
				s += " xv 0 yv " + y + " string2 \"Spectators\"";
				y += 8;
				needSpec = false;
			}

			s += " ctf " + x + " " + y + " " + p.fEntity.getPlayerNum() + " " + p.getScore() + " " + p.fEntity.getPlayerPing();
			if ( x == 160 )
			{
				x  = 0;
				y += 8;
			}
			else
				x  = 160;
		}
		
		Engine.writeByte(Engine.SVC_LAYOUT);
		Engine.writeString( s );
	}
}