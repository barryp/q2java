
package baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2jgame.*;
import baseq2.*;

/**
 * Kills everything inside when fired, irrespective of protection.
 *  @author Barry Pederson
 */

public class func_killbox extends GameObject
	{
	
public func_killbox( String[] spawnArgs ) throws GameException
	{
	super(spawnArgs);
		
	//self->movetype = MOVETYPE_PUSH;
	String model = getSpawnArg( "model", null );
	if (model != null)
		fEntity.setModel(model);

	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);
	}
public void use(Player touchedBy) 
	{
	MiscUtil.killBox(this.fEntity);
	}
}