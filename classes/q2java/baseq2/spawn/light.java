package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Lights that can be turned on and off
 *
 * @author Barry Pederson
 */


public class light implements GameTarget
	{
	protected int fState;
	protected int fStyle;

	protected final static int START_OFF = 1;

	protected final static int LIGHT_DISABLED = 0;	
	protected final static int LIGHT_ON = 1;
	protected final static int LIGHT_OFF = 2;
	
public light(Element spawnArgs) throws GameException
	{
	// no targeted lights in deathmatch, because they cause global messages
	if (BaseQ2.gIsDeathmatch)
		return;
		
	String s = GameUtil.getSpawnArg(spawnArgs, "targetname", "id", null);
	if (s == null)
		return;
		
	Game.addLevelRegistry("target-" + s, this);
	
	fStyle = GameUtil.getSpawnArg(spawnArgs, "style", 0);
	if (fStyle >= 32)
		{
		int spawnFlags = GameUtil.getSpawnFlags(spawnArgs);
		
		if ((spawnFlags & START_OFF) == 1)
			{
			fState = LIGHT_OFF;
			Engine.setConfigString(Engine.CS_LIGHTS + fStyle, "a");
			}
		else
			{
			fState = LIGHT_ON;
			Engine.setConfigString(Engine.CS_LIGHTS + fStyle, "m");
			}
		}	
	else
		fState = LIGHT_DISABLED;		
	}
/**
 * Toggle lights off and on if not disabled.
 * @param usedBy baseq2.Player
 */
public void use(Player usedBy) 
	{
	switch (fState)
		{
		case LIGHT_ON:
			Engine.setConfigString(Engine.CS_LIGHTS + fStyle, "a");
			fState = LIGHT_OFF;
			break;
			
		case LIGHT_OFF:
			Engine.setConfigString(Engine.CS_LIGHTS + fStyle, "m");
			fState = LIGHT_ON;
			break;								
		}
	}
}