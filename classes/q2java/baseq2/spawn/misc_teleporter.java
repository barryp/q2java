package q2java.baseq2.spawn;

import javax.vecmath.*;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Stepping onto this disc will teleport players to the 
 * targeted misc_teleporter_dest object.
 * 
 */
public class misc_teleporter extends GameObject 
	{
	protected NativeEntity fTriggerEntity;
	
/**
 * misc_teleporter constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public misc_teleporter(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	
	if (fTargets == null)		
		{
		fEntity.freeEntity();
		throw new GameException("teleporter without a target.");
		}

	// setup the first entity to be the pad
	fEntity.setModel("models/objects/dmspot/tris.md2");
	fEntity.setSkinNum(1);
	fEntity.setEffects(NativeEntity.EF_TELEPORTER);
	fEntity.setSound(Engine.getSoundIndex("world/amb10.wav"));
	fEntity.setSolid(NativeEntity.SOLID_BBOX);

	fEntity.setMins(-32, -32, -24);
	fEntity.setMaxs(32, 32, -16);
	fEntity.linkEntity();

	// setup a second entity to be the trigger
	fTriggerEntity = new NativeEntity();
	fTriggerEntity.setReference(this);
	fTriggerEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fTriggerEntity.setOrigin(fEntity.getOrigin());
	fTriggerEntity.setMins( -8, -8, 8);
	fTriggerEntity.setMaxs(8, 8, 24);
	fTriggerEntity.linkEntity();
	}
/**
 * Move the player to the teleport destination.
 * @param touchedBy Player
 */
public void touch(Player touchedBy) 
	{
	if (fTargets.size() < 1)
		{
		Game.dprint("Couldn't find destination\n");
		return;
		}

	// kill anything at the destination
	//	KillBox (other);

	GameObject dest = (GameObject) fTargets.elementAt(0);
	Point3f destOrigin = dest.fEntity.getOrigin();
	destOrigin.z += 10;

	fEntity.setEvent(NativeEntity.EV_PLAYER_TELEPORT);

	touchedBy.teleport(destOrigin, dest.fEntity.getAngles());
	}
}