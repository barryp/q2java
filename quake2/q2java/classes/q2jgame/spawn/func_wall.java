
package q2jgame.spawn;


import q2java.*;
import q2jgame.*;

/**
QUAKED func_wall (0 .5 .8) ? TRIGGER_SPAWN TOGGLE START_ON ANIMATED ANIMATED_FAST
This is just a solid wall if not inhibited

TRIGGER_SPAWN	the wall will not be present until triggered
				it will then blink in to existance; it will
				kill anything that was in it's way

TOGGLE			only valid for TRIGGER_SPAWN walls
				this allows the wall to be turned on and off

START_ON		only valid for TRIGGER_SPAWN walls

 *  @author M. van Gangelen" &ltmenno@element.nl&gt				the wall will initially be present
 */

public class func_wall extends GameEntity
	{
	private String  fModel;
	private boolean fUsed = false;			
	
public func_wall( String[] spawnArgs ) throws GameException
	{
	super(spawnArgs);
		
	//self->movetype = MOVETYPE_PUSH;
	fModel = getSpawnArg( "model", null );
	setModel (fModel);

	int effect = getEffects();
	if ( (fSpawnFlags & 8) != 0 )
		effect |= EF_ANIM_ALL;
	if ( (fSpawnFlags & 16) != 0 )
		effect |= EF_ANIM_ALLFAST;

	setEffects(effect);

	// just a wall
	if ( (fSpawnFlags & 7) == 0 )
		{
		setSolid( SOLID_BSP );
		linkEntity();
		return;
		}

	// it must be TRIGGER_SPAWN
	if ( (fSpawnFlags & 1) == 0)
		{
		//Engine.dprint("func_wall missing TRIGGER_SPAWN\n");
		fSpawnFlags |= 1;
		}

	// yell if the spawnflags are odd
	if ( (fSpawnFlags & 4) != 0 )
		{
		if ( (fSpawnFlags & 2) == 0)
			{
			PrintManager.dprint("func_wall START_ON without TOGGLE\n");
			fSpawnFlags |= 2;
			}
		}

	if ( (fSpawnFlags & 4) != 0 )
		{
		setSolid( SOLID_BSP );
		}
	else
		{
		setSolid( SOLID_NOT );
		setSVFlags( getSVFlags() | SVF_NOCLIENT );
		}
		
	linkEntity();
	}
public boolean killBox( GameEntity ent )
	{
	java.util.Enumeration players;
	
	players = enumeratePlayers();
	while( players.hasMoreElements() )
		{
		Player player = (Player)players.nextElement();
		// nail it
		Vec3 origin = new Vec3(0, 0, 0);
		player.damage ( ent, ent, origin, ent.getOrigin(), origin, 100000, 0, DAMAGE_NO_PROTECTION, 0 );//MOD_TELEFRAG);
		// if we didn't kill it, fail
		if ( player.getSolid() != SOLID_NOT )
			return false;
		}
		
	return true;		// all clear
	}

public void use(Player touchedBy) 
	{
	if ( (fSpawnFlags & 2) == 0)
		{
		if ( fUsed )
			return;
		fUsed = true;
		}

	if ( getSolid() == SOLID_NOT )
		{
		setSolid( SOLID_BSP );
		setSVFlags( getSVFlags() & ~SVF_NOCLIENT );
		killBox( this );
		}
	else
		{
		setSolid( SOLID_NOT );
		setSVFlags( getSVFlags() | SVF_NOCLIENT );
		}
		
	linkEntity();
	}
}