package q2java.baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

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

public class func_wall extends GameObject
	{
	private String  fModel;
	private boolean fUsed = false;			
	
public func_wall( String[] spawnArgs ) throws GameException
	{
	super(spawnArgs);
		
	//self->movetype = MOVETYPE_PUSH;
	fModel = getSpawnArg( "model", null );
	fEntity.setModel(fModel);

	int effect = fEntity.getEffects();
	if ( (fSpawnFlags & 8) != 0 )
		effect |= NativeEntity.EF_ANIM_ALL;
	if ( (fSpawnFlags & 16) != 0 )
		effect |= NativeEntity.EF_ANIM_ALLFAST;

	fEntity.setEffects(effect);

	// just a wall
	if ( (fSpawnFlags & 7) == 0 )
		{
		fEntity.setSolid(NativeEntity.SOLID_BSP);
		fEntity.linkEntity();
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
			Game.dprint("func_wall START_ON without TOGGLE\n");
			fSpawnFlags |= 2;
			}
		}

	if ( (fSpawnFlags & 4) != 0 )
		{
		fEntity.setSolid(NativeEntity.SOLID_BSP);
		}
	else
		{
		fEntity.setSolid(NativeEntity.SOLID_NOT);
		fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT );
		}
		
	fEntity.linkEntity();
	}
public void use(Player touchedBy) 
	{
	if ( (fSpawnFlags & 2) == 0)
		{
		if ( fUsed )
			return;
		fUsed = true;
		}

	if (fEntity.getSolid() == NativeEntity.SOLID_NOT)
		{
		fEntity.setSolid(NativeEntity.SOLID_BSP);
		fEntity.setSVFlags(fEntity.getSVFlags() & ~NativeEntity.SVF_NOCLIENT );
		MiscUtil.killBox(this.fEntity);
		}
	else
		{
		fEntity.setSolid(NativeEntity.SOLID_NOT);
		fEntity.setSVFlags(fEntity.getSVFlags() | NativeEntity.SVF_NOCLIENT);
		}
		
	fEntity.linkEntity();
	}
}