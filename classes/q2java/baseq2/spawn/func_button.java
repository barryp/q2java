package q2java.baseq2.spawn;

import java.util.Enumeration;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * Button pressed by players to cause something to happen
 * 
 * @author Barry Pederson
 */

public class func_button extends GenericPusher
	{	
	// spawn parameters
	private float fWait;
	private int fMaxHealth;

	// private movement parameters
	private Point3f fOffOrigin;
	private Point3f fOnOrigin;

	// track the state of the door
	private int fButtonState;
	private int fHealth;
	
	// door sounds if any
	private int fSoundStart;
		
	// door state constants		
	private final static int STATE_BUTTON_DEACTIVATING = 1;
	private final static int STATE_BUTTON_OFF = 2;
	private final static int STATE_BUTTON_ACTIVATING = 3;
	private final static int STATE_BUTTON_ON = 4;	
	private final static int STATE_BUTTON_ONWAIT = 5;	
	
public func_button(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_BSP);
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);

	fSpeed = getSpawnArg("speed", 40.0F);
	fAccel = getSpawnArg("accel", fSpeed);
	fDecel = getSpawnArg("decel", fSpeed);	
	fWait = getSpawnArg("wait", 4.0F);	
	fHealth = fMaxHealth = getSpawnArg("health", 0);
	float lip = getSpawnArg("lip", 3.0F);
	
	// setup door sounds
	if (getSpawnArg("sounds", 0) != 1)
		fSoundStart = Engine.getSoundIndex("switches/butn2.wav");
							
	// setup for opening and closing
	fOffOrigin = fEntity.getOrigin();
	Vector3f moveDir = MiscUtil.calcMoveDir(fEntity.getAngles());
	fEntity.setAngles(0, 0, 0);

	Vector3f absMoveDir = new Vector3f(moveDir);
	absMoveDir.absolute();
	Tuple3f size = fEntity.getSize();
	
	fMoveDistance = absMoveDir.x * size.x + absMoveDir.y * size.y + absMoveDir.z * size.z - lip;
	fOnOrigin = new Point3f();
	fOnOrigin.scaleAdd(fMoveDistance, moveDir, fOffOrigin);
	fButtonState = STATE_BUTTON_OFF;
		
	fEntity.setEffects(NativeEntity.EF_ANIM01);
			
	fEntity.linkEntity();		
	}
/**
 * This method was created by a SmartGuide.
 */
public void activate() 
	{
	switch (fButtonState)
		{
		case STATE_BUTTON_OFF:
		case STATE_BUTTON_DEACTIVATING:
			fButtonState = STATE_BUTTON_ACTIVATING;
			moveTo(fOnOrigin);
			if ((fSoundStart != 0) && !isGroupSlave())
				Game.getSoundSupport().fireEvent(fEntity, NativeEntity.CHAN_NO_PHS_ADD + NativeEntity.CHAN_VOICE, fSoundStart, 1, NativeEntity.ATTN_STATIC, 0);			
			break;	
			
		case STATE_BUTTON_ONWAIT:
			Game.addServerFrameListener(this, fWait, -1);
			break;				
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param inflictor q2jgame.GameEntity
 * @param attacker q2jgame.GameEntity
 * @param dir q2java.Vec3
 * @param point q2java.Vec3
 * @param normal q2java.Vec3
 * @param damage int
 * @param knockback int
 * @param dflags int
 */
public void damage(DamageEvent de) 
	{
	super.damage(de);
	
	if (fMaxHealth != 0)
		{
		fHealth -= de.getAmount();
		if (fHealth < 0)
			{
			fHealth = fMaxHealth;
			activate();
			}
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void deactivate() 
	{
	switch (fButtonState)
		{
		case STATE_BUTTON_ACTIVATING:
		case STATE_BUTTON_ONWAIT:
		case STATE_BUTTON_ON:
			fButtonState = STATE_BUTTON_DEACTIVATING;
			moveTo(fOffOrigin);
			break;			
		}
	}
/**
 * This method was created by a SmartGuide.
 */
public void moveFinished() 
	{
	switch (fButtonState)
		{
		case STATE_BUTTON_ACTIVATING:
			fEntity.setFrame(1);
			fEntity.setEffects(NativeEntity.EF_ANIM23);			
			useTargets();
			if (fWait == 0)
				fButtonState = STATE_BUTTON_ON;
			else				
				{
				fButtonState = STATE_BUTTON_ONWAIT;
				Game.addServerFrameListener(this, fWait, -1);
				}
			break;			

		case STATE_BUTTON_DEACTIVATING:
			fButtonState = STATE_BUTTON_OFF;
			fEntity.setEffects(NativeEntity.EF_ANIM01);
			break;			
		}		
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame(int phase) 
	{
	switch (fButtonState)
		{
		case STATE_BUTTON_ONWAIT:
			fEntity.setFrame(0);
			deactivate();
			break;
			
		default:
			super.runFrame(phase);
		}
	}
/**
 * Respond to a touch if we're not targeted.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	if (fTargetGroup == null)
		activate();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	activate();
	}
}