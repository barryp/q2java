package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Rotating objects like fans and such.
 * 
 * @author Barry Pederson
 */
public class func_rotating extends GenericPusher
	{
	protected Angle3f fRotateAngle;
	protected int fDmg;
	protected boolean fIsRotating;
	protected boolean fTouchDamages;	
	
/**
 * func_rotating constructor comment.
 * @param spawnArgs Element
 * @exception q2java.GameException The exception description.
 */
public func_rotating(Element spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_BSP);	
	String s = getSpawnArg("model", null);
	if (s != null)
		fEntity.setModel(s);	

	float speed = getSpawnArg("speed", 100.0F);
	fDmg = getSpawnArg("dmg", 2);
				
	// set the axis of rotation
	fRotateAngle = new Angle3f();
	if ((fSpawnFlags & 4) != 0)
		fRotateAngle.z = 1;
	else if ((fSpawnFlags & 8) != 0)
		fRotateAngle.x = 1;
	else // Z_AXIS
		fRotateAngle.y = 1;		

	fRotateAngle.scale(speed);

	// check for reverse rotation
	if ((fSpawnFlags & 2) != 0)
		fRotateAngle.negate();
		
	// does touching this while it rotates damage you?		
	if ((fSpawnFlags & 16) != 0)
		fTouchDamages = true;
					
	int effect = 0;		
	if ((fSpawnFlags & 64) != 0)
		effect |= NativeEntity.EF_ANIM_ALL;
	if ((fSpawnFlags & 128) != 0)
		effect |= NativeEntity.EF_ANIM_ALLFAST;	
	if (effect != 0)
		fEntity.setEffects(effect);
		
	fEntity.linkEntity();	
	
	// start it rotating
	if ((fSpawnFlags & 1) != 0)
		use(null);
	}
/**
 * Called when the GenericPusher is blocked by another object.
 * @param obj The GameObject that's in the way.
 */
public void block(GameObject obj) 
	{
	Vector3f origin = new Vector3f();
	obj.damage(this, this, origin, obj.fEntity.getOrigin(), origin, (int)fDmg, 1, 0, 0, "crush");
	}
/**
 * Needed by GenericPusher.
 */
protected void moveFinished()
	{
	}
/**
 * Possibly damage the player for touching the rotating object.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(Player touchedBy) 
	{
	if (fTouchDamages && fIsRotating)
		{
		Vector3f zero = Q2Recycler.getVector3f();
		zero.set(0, 0, 0);
		touchedBy.damage(this, this, zero, touchedBy.fEntity.getOrigin(), zero, fDmg, 1, 0, Engine.TE_NONE, "crush");
		Q2Recycler.put(zero);
		}
	}
/**
 * Toggle the rotating object on and off.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	// switch states
	fIsRotating = !fIsRotating;
	
	if (fIsRotating)
		startRotating(fRotateAngle);			
	else
		{
		stopMoving();
		fEntity.setSound(0);
		}
	}
}