
package q2jgame.spawn;

import q2java.*;
import q2jgame.*;

public class target_explosion extends GameEntity
	{
	private float fDelay;
	private float fDmg;

	private float fNextThink = 0;

	private Player fActivator;
	
public target_explosion(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	setSVFlags( SVF_NOCLIENT );

	fDelay = getSpawnArg( "delay", 0f );
	fDmg   = getSpawnArg( "dmg",   0f );
	}
private void explode()
	{
	float save;
	Vec3 origin = getOrigin();

	Engine.writeByte( Engine.SVC_TEMP_ENTITY );
	Engine.writeByte( Engine.TE_EXPLOSION1 );
	Engine.writePosition(origin);
	Engine.multicast(origin, Engine.MULTICAST_PHS );

	Game.radiusDamage( this, fActivator, fDmg, null, fDmg+40 );

	save = fDelay;
	fDelay = 0;
	useTargets();
	fDelay = save;
	}
public void runFrame() 
	{
	if ((fNextThink > 0) && (Game.gGameTime >= fNextThink))
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

	fNextThink = Game.gGameTime + fDelay;
	}
}