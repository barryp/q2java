
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_plat extends GenericPusher implements AreaTriggerUser
	{
	// spawn parameters
	private int fWait;
	private int fDmg;
	private int fLip;
	
	private Vec3 fRaisedOrigin;
	private Vec3 fLoweredOrigin;
	
	// track the state of the plat
	private int fPlatState;
	private float fNextPlatThink;	
	
	// plat sounds
	private int fSoundStart;
	private int fSoundMiddle;
	private int fSoundEnd;	
	
	// door state constants		
	private final static int STATE_PLAT_LOWERING = 1;
	private final static int STATE_PLAT_LOWERED = 2;
	private final static int STATE_PLAT_RAISING = 3;
	private final static int STATE_PLAT_RAISED = 4;	
	private final static int STATE_PLAT_RAISEDWAIT = 5;	
	
	private final static int PLAT_LOW_TRIGGER	= 1;	
	
public func_plat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	setAngles(0, 0, 0);
	setSolid(SOLID_BSP);
	
	String s = getSpawnArg("model", null);
	if (s != null)
		setModel(s);

	fSpeed = getSpawnArg("speed", 200) * 0.1F;
	fAccel = getSpawnArg("accel", 50) * 0.1F;
	fDecel = getSpawnArg("decel", 50) * 0.1F;	
	fDmg = getSpawnArg("dmg", 2);
	fWait = 3;
	fLip = getSpawnArg("lip", 8);
	int height = getSpawnArg("height", 0);

	fRaisedOrigin = getOrigin();
	fLoweredOrigin = new Vec3(fRaisedOrigin);
	Vec3 mins = getMins();
	Vec3 maxs = getMaxs();

	if (height != 0)
		fLoweredOrigin.z -= height;
	else
		fLoweredOrigin.z -= (maxs.z - mins.z) - fLip;
			
	if (fTargetGroup != null)
		fPlatState = STATE_PLAT_RAISED;
	else
		{								
		fPlatState = STATE_PLAT_LOWERED;						
		setOrigin(fLoweredOrigin);
		}
	
	spawnInsideTrigger();	
	linkEntity();	
	
	fSoundStart = Engine.soundIndex("plats/pt1_strt.wav");
	fSoundMiddle = Engine.soundIndex("plats/pt1_mid.wav");
	fSoundEnd = Engine.soundIndex("plats/pt1_end.wav");	
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void areaTrigger(Player touchedBy) 
	{
	raise();
	}
/**
 * This method was created by a SmartGuide.
 */
public void lower() 
	{
	switch (fPlatState)
		{
		case STATE_PLAT_RAISING:
		case STATE_PLAT_RAISEDWAIT:
		case STATE_PLAT_RAISED:
			fPlatState = STATE_PLAT_LOWERING;
			moveTo(fLoweredOrigin);
			if (!isGroupSlave())
				{
				if (fSoundStart != 0)
					sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundStart, 1, ATTN_STATIC, 0);
				setSound(fSoundMiddle);
				}			
			break;			
		}
	}
/**
 * This method was created by a SmartGuide.
 */
protected void moveFinished() 
	{
	switch (fPlatState)
		{
		case STATE_PLAT_RAISING:
			if (fWait == 0)
				fPlatState = STATE_PLAT_RAISED;
			else				
				{
				fNextPlatThink = (float)(Game.gGameTime + fWait);
				fPlatState = STATE_PLAT_RAISEDWAIT;
				}
			break;			

		case STATE_PLAT_LOWERING:
			fPlatState = STATE_PLAT_LOWERED;
			break;			
		}	
		
	if (!isGroupSlave())
		{
		if (fSoundEnd != 0)
			sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundEnd, 1, ATTN_STATIC, 0);
		setSound(0);			
		}			
	}
/**
 * This method was created by a SmartGuide.
 */
public void raise() 
	{
	switch (fPlatState)
		{
		case STATE_PLAT_LOWERED:
		case STATE_PLAT_LOWERING:
			fPlatState = STATE_PLAT_RAISING;
			moveTo(fRaisedOrigin);
			if (!isGroupSlave())
				{
				if (fSoundStart != 0)
					sound(CHAN_NO_PHS_ADD + CHAN_VOICE, fSoundStart, 1, ATTN_STATIC, 0);
				setSound(fSoundMiddle);
				}			
			break;			
		}
	}
/**
 * This method was created by a SmartGuide.
 */
private void spawnInsideTrigger() 
	{
	Vec3 mins = getMins();
	Vec3 maxs = getMaxs();

	Vec3 tmin = new Vec3(mins);
	Vec3 tmax = new Vec3(maxs);
		
	tmin.add(25, 25, 0);
	tmax.add(-25, -25, 8);
	tmin.z = tmax.z - (fRaisedOrigin.z - fLoweredOrigin.z + fLip);	
//	tmin[2] = tmax[2] - (ent->pos1[2] - ent->pos2[2] + st.lip);

	if ((fSpawnFlags & PLAT_LOW_TRIGGER) != 0)
		tmax.z = tmin.z + 8;

	if (tmax.x - tmin.x <= 0)
		{
		tmin.x = (mins.x + maxs.x) * 0.5F;
		tmax.x = tmin.x + 1;
		}

	if ((tmax.y - tmin.y) <= 0)			
		{
		tmin.y = (mins.y + maxs.y) * 0.5F;
		tmax.y = tmin.y + 1;
		}
	
	try
		{
		new AreaTrigger(this, tmin, tmax);
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}		
	}
/**
 * This method was created by a SmartGuide.
 */
public void think() 
	{
	if ((fNextPlatThink > 0) && (Game.gGameTime >= fNextPlatThink))
		{
		switch (fPlatState)
			{
			case STATE_PLAT_RAISEDWAIT:
				fNextPlatThink = 0;
				lower();
				break;
			}
		}

	super.think();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	lower();
	}
}