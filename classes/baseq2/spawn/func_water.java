package baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Water or Lava that rises and falls
 *
 * @author Barry Pederson
 */

public class func_water extends func_door
	{	
	
public func_water(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	// override door default speeds
	fSpeed = fAccel = fDecel = getSpawnArg("speed", 25.0F);
	
	// override door sounds with water sounds
	fSoundMiddle = 0;
	switch (getSpawnArg("sounds", 0))
		{
		case 1: // water
		case 2: // lava
			fSoundStart = Engine.getSoundIndex("world/mov_watr.wav");
			fSoundEnd = Engine.getSoundIndex("world/stp_watr.wav");
			break;
			
		default:
			fSoundStart = 0;
			fSoundEnd = 0;
		}
		
	// clear any effects the door code may have set.		
	fEntity.setEffects(0);		
	}
}