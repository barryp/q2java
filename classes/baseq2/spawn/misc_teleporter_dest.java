
package baseq2.spawn;

import q2java.*;
import baseq2.*;

/**
 * Point teleporters at these.
 */
public class misc_teleporter_dest extends GameObject 
	{
	
/**
 * Create a teleport destination pad.
 * @param spawnArgs java.lang.String[]
 */
public misc_teleporter_dest(java.lang.String[] spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	
	fEntity.setModel("models/objects/dmspot/tris.md2");
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setMins(-32, -32, -24);
	fEntity.setMaxs( 32,  32, -16);
	fEntity.linkEntity();
	}
}