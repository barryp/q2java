
package baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

public class target_explosion extends GameObject
	{
	private float fDelay;
	private float fDmg;

	private float fNextThink = 0;

	private Player fActivator;
	
public target_explosion(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);

	fDelay = getSpawnArg( "delay", 0f );
	fDmg   = getSpawnArg( "dmg",   0f );
	}
protected void explode()
	{
	float save;
	Point3f origin = fEntity.getOrigin();

	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(Engine.TE_EXPLOSION1);
	Engine.writePosition(origin);
	Engine.multicast(origin, Engine.MULTICAST_PHS );

	MiscUtil.radiusDamage( this, fActivator, fDmg, null, fDmg+40 );

	save = fDelay;
	fDelay = 0;
	useTargets();
	fDelay = save;
	}
public void runFrame() 
	{
	if ((fNextThink > 0) && (Game.getGameTime() >= fNextThink))
		{
		explode();
		fNextThink = 0f;
		}
	}
public void use( Player p ) 
	{
	fActivator = p;

	if ( fDelay == 0)
		{
		explode();
		return;
		}

	fNextThink = Game.getGameTime() + fDelay;
	}
}