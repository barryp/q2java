package q2java.baseq2.spawn;

import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * target_temp_entity creates a temp entity at origin when used.
 * I don't know if this is ever used but its in the dll code.
 *
 * @author James Bielby
 */ 


public class target_temp_entity extends GameObject
	{
	protected int fStyle;
	
public target_temp_entity(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	//Make ourselves invisable to clients
	fEntity.setSVFlags(NativeEntity.SVF_NOCLIENT);

	//Grab the style arg which is the temp entity number
	fStyle = getSpawnArg( "style", 0 );
	}
public void use( Player p ) 
	{
	Point3f origin = fEntity.getOrigin();

	//Create splash temp entity. This comes straight from the original dll source.
	Engine.writeByte(Engine.SVC_TEMP_ENTITY);
	Engine.writeByte(fStyle);
	Engine.writePosition(origin);
	Engine.multicast(origin, Engine.MULTICAST_PHS );
	}
}