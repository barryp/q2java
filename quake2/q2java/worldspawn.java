
public class worldspawn extends Entity
	{
	public static int fHealthPic;		
	
public worldspawn(String[] spawnArgs) throws GameException
	{
	super(spawnArgs, true); 	// make sure we get a special spot in the array

	setSolid(SOLID_BSP);
	setModelIndex(1); 			// world model is always index 1


	//
	// deal with spawn args
	//
	
	String s;

	s = getSpawnArg("sky");
	Engine.configString(Engine.CS_SKY, (s == null ? "unit1_" : s));

	s = getSpawnArg("sounds");
	Engine.configString(Engine.CS_CDTRACK, (s == null ? "0" : s));

	s = getSpawnArg("message");
	if (s != null)
		Engine.configString(Engine.CS_NAME, s);

	//
	// cache some images and sounds
	//
		
	fHealthPic = Engine.imageIndex("i_health");		
	
	// setup player status bar
	Engine.configString (Engine.CS_STATUSBAR, player.DM_STATUSBAR);	
	
	//
	// Setup light animation tables. 'a' is total darkness, 'z' is doublebright.
	//

	// 0 normal
	Engine.configString(Engine.CS_LIGHTS+0, "m");
	
	// 1 FLICKER (first variety)
	Engine.configString(Engine.CS_LIGHTS+1, "mmnmmommommnonmmonqnmmo");
	
	// 2 SLOW STRONG PULSE
	Engine.configString(Engine.CS_LIGHTS+2, "abcdefghijklmnopqrstuvwxyzyxwvutsrqponmlkjihgfedcba");
	
	// 3 CANDLE (first variety)
	Engine.configString(Engine.CS_LIGHTS+3, "mmmmmaaaaammmmmaaaaaabcdefgabcdefg");
	
	// 4 FAST STROBE
	Engine.configString(Engine.CS_LIGHTS+4, "mamamamamama");
	
	// 5 GENTLE PULSE 1
	Engine.configString(Engine.CS_LIGHTS+5,"jklmnopqrstuvwxyzyxwvutsrqponmlkj");
	
	// 6 FLICKER (second variety)
	Engine.configString(Engine.CS_LIGHTS+6, "nmonqnmomnmomomno");
	
	// 7 CANDLE (second variety)
	Engine.configString(Engine.CS_LIGHTS+7, "mmmaaaabcdefgmmmmaaaammmaamm");
	
	// 8 CANDLE (third variety)
	Engine.configString(Engine.CS_LIGHTS+8, "mmmaaammmaaammmabcdefaaaammmmabcdefmmmaaaa");
	
	// 9 SLOW STROBE (fourth variety)
	Engine.configString(Engine.CS_LIGHTS+9, "aaaaaaaazzzzzzzz");
	
	// 10 FLUORESCENT FLICKER
	Engine.configString(Engine.CS_LIGHTS+10, "mmamammmmammamamaaamammma");

	// 11 SLOW PULSE NOT FADE TO BLACK
	Engine.configString(Engine.CS_LIGHTS+11, "abcdefghijklmnopqrrqponmlkjihgfedcba");
	
	// styles 32-62 are assigned by the light program for switchable lights

	// 63 testing
	Engine.configString(Engine.CS_LIGHTS+63, "a");		
	}
}