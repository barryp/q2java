
package baseq2.spawn;

import java.util.Vector;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Trigger that can be activated multiple times.
 * An invisible field is setup that causes something
 * to happen when a player touches it.
 *
 * @author Barry Pederson
 */ 

public class trigger_multiple extends GameObject implements FrameListener, FixedObject
	{
	protected float fDelay;
	protected int fState;
	protected Vector3f fTriggerDir;
	
	protected final static int STATE_DISABLED = 0;
	protected final static int STATE_ENABLED = 1;
	protected final static int STATE_BUSY = 2;
	protected final static int STATE_DISPOSING = 3;
	
/**
 * This method was created by a SmartGuide.
 * @param spawnArgs java.lang.String[]
 */
public trigger_multiple (String[] spawnArgs) throws GameException
	{
	this(spawnArgs, true);
	}
public trigger_multiple(String[] spawnArgs, boolean isMultiple) throws GameException
	{
	super(spawnArgs);
	
	fDelay = (isMultiple ? Game.getSpawnArg(spawnArgs, "delay", 0.2F) : -1);	
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);

	// (trigger_once special handling)
	// make old maps work because I messed up on flag assignments here
	// triggered was on bit 1 when it should have been on bit 4
	if ((fSpawnFlags & (isMultiple ? 4 : 5)) != 0)
		{
		fEntity.setSolid(NativeEntity.SOLID_NOT);
		fState = STATE_DISABLED;
		}
	else
		{
		fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
		fState = STATE_ENABLED;
		}

//	if (!VectorCompare(ent->s.angles, vec3_origin))
//		G_SetMovedir (ent->s.angles, ent->movedir);

	Angle3f angles = fEntity.getAngles();
	if (!MiscUtil.equals(angles, 0, 0, 0))
		{
		fTriggerDir = MiscUtil.calcMoveDir(angles);
		fEntity.setAngles(0, 0, 0);
		}
		
	fEntity.setModel(Game.getSpawnArg(spawnArgs, "model", ""));
	fEntity.linkEntity();	
	}
/**
 * Alter the trigger's state based on some alarm we set.
 */
public void runFrame(int phase) 
	{
	switch (fState)
		{
		case STATE_BUSY:
			fState = STATE_ENABLED;
			break;	
			
		case STATE_DISPOSING:
			dispose();
			break;						
		}

	}
/**
 * React to a player's touch..possibly only if they're facing in
 * the right direction.
 * @param touchedBy Player touching the trigger.
 */
public void touch(Player touchedBy) 
	{
	if (fTriggerDir != null)
		{
		Vector3f forward = new Vector3f();
		touchedBy.fEntity.getAngles().getVectors(forward, null, null);
		if (forward.dot(fTriggerDir) < 0)	
			return;
		}
		
	use(touchedBy);
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	switch (fState)
		{
		case STATE_DISABLED:
			fState = STATE_ENABLED;
			fEntity.setSolid(NativeEntity.SOLID_TRIGGER);
			fEntity.linkEntity();
			break;
			
		case STATE_ENABLED:
			useTargets();			
			if (fDelay > 0)
				{
				// setup to be called back when busy state is finished
				fState = STATE_BUSY;
				Game.addFrameListener(this, fDelay, -1);
				}
			else
				{
				// setup to be called back when busy state is finished
				fState = STATE_DISPOSING;
				Game.addFrameListener(this, 0, -1);
				}
			break;			
		}
	}
}