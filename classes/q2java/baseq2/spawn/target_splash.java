package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * target_splash causes a spark effect when it is used.
 * for example: in base1, it causes the sparks coming out 
 * of the wall near the first doorway.
 *
 * @author James Bielby
 */ 

public class target_splash extends GameObject
	{
	protected int fCount;
	protected int fSounds;
	protected float fDmg;
	protected Vector3f fMoveDir;
	
public target_splash(Element spawnArgs) throws GameException
	{
	super(spawnArgs);

	//Make ourselves invisable to clients
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);

	//Grab the spawn args
	fCount = getSpawnArg( "count", 30 );
	fSounds = getSpawnArg( "sounds", 0 );
	fDmg = getSpawnArg( "dmg", 0f );
	//Turn the movedir into a proper angle
	fMoveDir = MiscUtil.calcMoveDir(fEntity.getAngles());
	//Set angles to 0,0,0 to make us look normal
	fEntity.setAngles(0, 0, 0);
	}
public void use( Player p ) 
	{
	Point3f origin = fEntity.getOrigin();

	//Create splash temp entity. This comes straight from the original dll source.
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_SPLASH);
	Engine.writeByte(fCount);
	Engine.writePosition(origin);
	Engine.writeDir(fMoveDir);
	Engine.writeByte(fSounds);
	Engine.multicast(origin, Engine.MULTICAST_PHS );

	//If dmg is set then to radius damage in the area of the splash
	if (fDmg > 0)
		MiscUtil.radiusDamage( this, p, fDmg, null, fDmg+40, "splash" );
	}
}