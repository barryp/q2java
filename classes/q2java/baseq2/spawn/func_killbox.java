package q2java.baseq2.spawn;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Kills everything inside when fired, irrespective of protection.
 *  @author Barry Pederson
 */

public class func_killbox extends GameObject
	{
	
public func_killbox(Element spawnArgs ) throws GameException
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