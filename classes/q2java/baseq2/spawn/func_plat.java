package q2java.baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * func_plat - elevator platforms that raise and lower
 * 
 * @author Barry Pederson
 */

public class func_plat extends GenericPusher 
	{
	// spawn parameters
	private float fWait;
	private float fDmg;
	private float fLip;
	
	private Point3f fRaisedOrigin;
	private Point3f fLoweredOrigin;
	
	// track the state of the plat
	private int fPlatState;
	
	// plat sounds
	private int fSoundStart;
	private int fSoundMiddle;
	private int fSoundEnd;	
	
	// door state constants		
	private final static int STATE_PLAT_LOWERING	= 1;
	private final static int STATE_PLAT_LOWERED		= 2;
	private final static int STATE_PLAT_RAISING		= 3;
	private final static int STATE_PLAT_RAISED		= 4;	
	private final static int STATE_PLAT_RAISEDWAIT	= 5;	
	
	private final static int PLAT_LOW_TRIGGER	= 1;

	// Trigger field that causes platforms to move when you 
	// step on/in it
	private class Trigger extends GameObject implements FixedObject
		{
		public Trigger(Tuple3f mins, Tuple3f maxs) throws GameException
			{
			fEntity = new NativeEntity();
			fEntity.setReference(this);
	
			fEntity.setMins(mins);
			fEntity.setMaxs(maxs);
	
			fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
			fEntity.linkEntity();						
			}
			
		public void touch(Player touchedBy) 
			{
			if (isLowered())
				raise();
			}			
		}	
	
public func_plat(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setAngles(0, 0, 0);
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);

	fSpeed = getSpawnArg("speed", 200.0F) * 0.1F;
	fAccel = getSpawnArg("accel", 50.0F) * 0.1F;
	fDecel = getSpawnArg("decel", 50.0F) * 0.1F;	
	fDmg = getSpawnArg("dmg", 2.0F);
	fWait = 3;
	fLip = getSpawnArg("lip", 8.0F);
	float height = getSpawnArg("height", 0.0F);

	fRaisedOrigin = fEntity.getOrigin();
	fLoweredOrigin = new Point3f(fRaisedOrigin);
	Tuple3f mins = fEntity.getMins();
	Tuple3f maxs = fEntity.getMaxs();

	if (height != 0)
		fLoweredOrigin.z -= height;
	else
		fLoweredOrigin.z -= (maxs.z - mins.z) - fLip;
			
	if (fTargetGroup != null)
		fPlatState = STATE_PLAT_RAISED;
	else
		{								
		fPlatState = STATE_PLAT_LOWERED;						
		fEntity.setOrigin(fLoweredOrigin);
		}
	
	spawnInsideTrigger();	
	fEntity.linkEntity();	
	
	fSoundStart = Engine.getSoundIndex("plats/pt1_strt.wav");
	fSoundMiddle = Engine.getSoundIndex("plats/pt1_mid.wav");
	fSoundEnd = Engine.getSoundIndex("plats/pt1_end.wav");	
	}
/**
 * Called when the GenericPusher is blocked by another object.
 * @param obj The GameObject that's in the way.
 */
public void block(GameObject obj) 
	{
	Vector3f origin = new Vector3f();

	if (!(obj instanceof Player))
		{
		// give it a chance to go away on it's own terms (like gibs)
		obj.damage(this, this, origin, obj.fEntity.getOrigin(), origin, 100000, 1, 0, 0, "crush");
		// if it's still there, nuke it
		if (obj.fEntity != null)
			obj.becomeExplosion(Engine.TE_EXPLOSION1);
		return;		
		}
	
	obj.damage(this, this, origin, obj.fEntity.getOrigin(), origin, (int)fDmg, 1, 0, 0, "crush");

	if (fPlatState == STATE_PLAT_RAISING)
		lower();
	else
		raise();
	}
/**
 * Is the plat resting at the bottom of it's shaft.
 * @return boolean
 */
public boolean isLowered() 
	{
	return fPlatState == STATE_PLAT_LOWERED;
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
					fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundStart, 1, NativeEntity.ATTN_STATIC, 0);
				fEntity.setSound(fSoundMiddle);
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
				fPlatState = STATE_PLAT_RAISEDWAIT;
				Game.addServerFrameListener(this, fWait, -1);
				}
			break;			

		case STATE_PLAT_LOWERING:
			fPlatState = STATE_PLAT_LOWERED;
			break;			
		}	
		
	if (!isGroupSlave())
		{
		if (fSoundEnd != 0)
			fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundEnd, 1, NativeEntity.ATTN_STATIC, 0);
		fEntity.setSound(0);			
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
					fEntity.sound(NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundStart, 1, NativeEntity.ATTN_STATIC, 0);
				fEntity.setSound(fSoundMiddle);
				}			
			break;			
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame(int phase) 
	{
	switch (fPlatState)
		{
		case STATE_PLAT_RAISEDWAIT:
			lower();
			break;
			
		default:
			super.runFrame(phase);
		}
	}
/**
 * This method was created by a SmartGuide.
 */
protected void spawnInsideTrigger() 
	{
	Tuple3f mins = fEntity.getMins();
	Tuple3f maxs = fEntity.getMaxs();

	Point3f tmin = new Point3f(mins);
	Point3f tmax = new Point3f(maxs);
		
	tmin.add(new Point3f(25, 25, 0));
	tmax.add(new Point3f(-25, -25, 8));
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
		new Trigger(tmin, tmax);
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}		
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	switch (fPlatState)
		{
		}
		
	lower();
	}
}