package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * target_blaster causes a blaster bolt to be fired.
 *
 * @author Peter Donald
 */ 
public class target_blaster extends GameObject
	{
	protected final static int NOEFFECTS = 2;
	protected final static int NOTRAIL = 1;

	protected int fSound;
	protected int fDmg;
	protected int fSpeed;
	protected int fEffect;
	protected Vector3f fMoveDir;
	
 public target_blaster(Element spawnArgs) throws GameException
	{
	super(spawnArgs);

	//Make ourselves invisable to clients
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);

	fDmg = getSpawnArg( "dmg", 15 );
	fSpeed = getSpawnArg( "speed", 1000 );

	//Turn the movedir into a proper angle
	fMoveDir = MiscUtil.calcMoveDir(fEntity.getAngles());

	//Set angles to 0,0,0 to make us look normal
	fEntity.setAngles(0, 0, 0);

	fSound = Engine.getSoundIndex("weapons/laser2.wav");

	//NB this is not done in game dll but it seemed to be an error ?
	if( (fSpawnFlags & NOEFFECTS) != 0 )
		{
		fEffect = 0;
		}
	else if ( (fSpawnFlags & NOTRAIL) != 0 )
		{
		fEffect = NativeEntity.EF_HYPERBLASTER;
		}
	else
		{
		fEffect = NativeEntity.EF_BLASTER;
		}
	}
public void use( Player p ) 
	{
	try
		{
		BlasterBolt bb = new BlasterBolt();
		bb.launch(this,
			    fEntity.getOrigin(), 
			    fMoveDir,
			    fDmg,
			    fSpeed, 
			    fEffect, // NB: in dll this was constant EF_BLASTER
			    "target_blaster" );
		bb.fEntity.setSound( fSound );
		}
	catch (GameException e)
		{
	  	Game.dprint("Can't create BlasterBolt " + e);
		}	
	}
}