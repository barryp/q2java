
/**
 * This class was generated by a SmartGuide.
 * 
 */
import java.util.*;

class player extends NativePlayer 
	{	
	private Hashtable fUserInfo;
	private String fSoundDir;
	private int fHand;
	private boolean fGrounded;
	
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
public player(String userinfo, boolean loadgame) throws GameException 
	{
	Game.debugLog("new player(\"" + userinfo + "\", " + loadgame + ")");
	userinfoChanged(userinfo);
	}
/**
 * This method was created by a SmartGuide.
 * @param loadgame boolean
 */
public void begin(boolean loadgame) 
	{
	Game.debugLog("player.begin(" + loadgame + ")");
	Engine.dprint("Java player.begin(" + loadgame + ")\n");
	
	Entity spawnPoint = null;
	java.util.Enumeration enum = enumerateEntities("info_player_start");
	while (enum.hasMoreElements())
		{
		spawnPoint = (Entity) enum.nextElement();
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

	setMins(new Vec3(-16, -16, 24));
	setMaxs(new Vec3(16, 16, 32));
	setStat(NativePlayer.STAT_HEALTH_ICON, (short)worldspawn.fHealthPic);
	linkEntity();
/*	
	// send effect
	Engine.writeByte(GameConst.SVC_MUZZLEFLASH);
	Engine.writeShort(fEntityIndex);
	Engine.writeByte(GameConst.MZ_LOGIN);
	Engine.multicast(getOrigin(), Engine.MULTICAST_PVS);		
*/	
	}
/**
 * This method was created by a SmartGuide.
 */
public void command() 
	{
	Game.debugLog("Player.command()");
	Engine.dprint("Java Player.command()\n");
	Engine.dprint("   Engine.args = [" + Engine.args() + "]\n");
	Engine.dprint("   Engine.argc = " + Engine.argc() + "\n");
	for (int i = 0; i < Engine.argc(); i++)
		Engine.dprint("    " + i + ": [" + Engine.argv(i) + "]\n");
	}
/**
 * The player is disconnecting, clean things up and say goodbye.
 *
 * Be sure you drop all references to this player object.  
 */
public void disconnect()
	{
	Game.debugLog("Player.disconnect()");
	Engine.dprint("Java Player.disconnect()\n");

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), "");	
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
 * @return int
 * @param base java.lang.String
 */
public int sexedSoundIndex(String base) 
	{
	return Engine.soundIndex(fSoundDir + "/" + base + ".wav");
	}
/**
 * This method was created by a SmartGuide.
 * @param cmd q2java.UserCmd
 */
public void think()
	{
	PMoveResults pm = pMove();
	Game.debugLog("PMove: " + pm);
	
	if (fGrounded && (pm.fGroundEntity == null) && (pm.fCmdUpMove >= 10) && (pm.fWaterLevel == 0))
		sound(CHAN_VOICE, sexedSoundIndex("jump1"), 1, ATTN_NORM, 0);
		
	fGrounded = (pm.fGroundEntity != null);	
	linkEntity();	
	}
public void userinfoChanged(String userinfo)
	{
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
	String skin = getUserInfo("skin");
	String name = getUserInfo("name");

	Engine.configString(Engine.CS_PLAYERSKINS + getPlayerNum(), name + "\\" + skin);			
	
	if ((skin != null) && (skin.length() > 0) && ((skin.charAt(0) == 'F') || (skin.charAt(0) == 'f')))
		fSoundDir = "player/female";
	else
		fSoundDir = "player/male";	
		
	String hand = getUserInfo("hand");
	if (hand != null)
		fHand = Integer.parseInt(hand);			
	}
}