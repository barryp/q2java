package q2java.baseq2.spawn;

import java.util.Vector;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.baseq2.*;

/**
 * Trigger that can be activated multiple times.
 * An invisible field is setup that causes something
 * to happen when a player touches it.
 *
 * @author Barry Pederson
 */ 

public class trigger_multiple extends GameObject implements ServerFrameListener, FixedObject
	{
	protected final static int MONSTER = 1;
	protected final static int NOT_PLAYER = 2;
	protected final static int TRIGGER = 4;

	protected float fDelay;
	protected int fState;
	protected Vector3f fTriggerDir;
	protected float fWait;

	protected int fSound;
	protected String fMessage;
	protected float fLastFire;

	protected final static int STATE_DISABLED = 0;
	protected final static int STATE_ENABLED = 1;
	protected final static int STATE_DISPOSING = 3;
	
/**
 * This method was created by a SmartGuide.
 * @param spawnArgs java.lang.String[]
 */
public trigger_multiple (Element spawnArgs) throws GameException
	{
	this(spawnArgs, true);
	}
public trigger_multiple(Element spawnArgs, boolean isMultiple) throws GameException
	{
	super(spawnArgs);

	int sounds = GameUtil.getSpawnArg(spawnArgs, "sounds", 0);
	
	if(sounds == 1)
	    fSound = Engine.getSoundIndex("misc/secret.wav");
	else if(sounds == 2)
	    fSound = Engine.getSoundIndex("misc/talk.wav");
	else if(sounds == 3)
	    fSound = Engine.getSoundIndex("misc/trigger1.wav");

	fMessage = GameUtil.getSpawnArg(spawnArgs, "message", null);

	fWait = (isMultiple ? GameUtil.getSpawnArg(spawnArgs, "wait", 0.2F) : -1.0F);	

	if( isMultiple )
	    {
	    fDelay = GameUtil.getSpawnArg(spawnArgs, "delay", 0);
	    }
	
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

	Angle3f angles = fEntity.getAngles();
	if (!MiscUtil.equals(angles, 0, 0, 0))
		{
		fTriggerDir = MiscUtil.calcMoveDir(angles);
		fEntity.setAngles(0, 0, 0);
		}
		
	fEntity.setModel(GameUtil.getSpawnArg(spawnArgs, "model", ""));
	fEntity.linkEntity();	
	}
/**
 * Alter the trigger's state based on some alarm we set.
 */
public void runFrame(int phase) 
	{
	switch (fState)
		{
		case STATE_DISPOSING: dispose(); break;						
		}

	}
/**
 * React to a player's touch..possibly only if they're facing in
 * the right direction.
 * @param touchedBy Player touching the trigger.
 */
public void touch(Player touchedBy) 
	{
	if( touchedBy != null )
		{
		if( (fSpawnFlags & NOT_PLAYER) != 0 ) 
			return; // ie if NOT_PLAYER then ignore on player
		}
	  /*
	else if( touchedBy instanceof Monster )
	  {
	  if( (fSpawnFlags & MONSTER) == 0 )  return; // ie only be touched if is MONSTER
	  }
	  */
	else 
		return; // ie been touched by a non-monster non-player
	  
	if (fTriggerDir != null)
		{
		Vector3f forward = Q2Recycler.getVector3f();
		touchedBy.fEntity.getAngles().getVectors(forward, null, null);
		boolean bail = forward.dot(fTriggerDir) < 0;
		Q2Recycler.put(forward);
		
		if (bail)
			return;
		}
	  
	use(touchedBy);
	}
public void trigger(Object activator) 
	{
	useTargets( activator );

	if( fSound != 0 )
		fEntity.sound( NativeEntity.CHAN_AUTO, fSound, 1.0f, NativeEntity.ATTN_NORM, 0.0f );

	if( fMessage != null && activator != null && activator instanceof GameObject )
		((GameObject)activator).fEntity.cprint( Engine.PRINT_MEDIUM , fMessage + "\n" );

	if (fWait <= 0) // ie is multiple trigger
		{
		// setup to be called back when busy state is finished
		fState = STATE_DISPOSING;
		Game.addServerFrameListener(this, 0, -1);
		}
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
	        if( fWait <= 0 ) // deal with single triggers first
				{
				trigger( touchedBy );
				break;
				}
			
			if( fLastFire + fWait > Game.getGameTime() ) 
				return;
			
			fLastFire = Game.getGameTime();
			
	        if( fDelay > 0 )
				{
				Game.addServerFrameListener( new TriggerDelayer(this, touchedBy), 
						 fDelay,
						 -1);
				}
			else
				{
				trigger( touchedBy );
				}
			break;			
		}
	}
public void useTargets(Object activator) 
	{
	if (fTargets == null)
		return;
		
	for (int i = 0; i < fTargets.size(); i++)
		{
		Object obj = fTargets.elementAt(i);
		if (obj instanceof GameTarget)
	    	((GameTarget) obj).use( (Player)activator );
		else
			System.out.println(obj.getClass().getName() + " doesn't implement GameTarget");
		}
	}
}