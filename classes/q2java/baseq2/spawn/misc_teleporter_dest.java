package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.baseq2.*;

/**
 * Point teleporters at these.
 */
public class misc_teleporter_dest extends GameObject 
	{
	
/**
 * Create a teleport destination pad.
 * @param spawnArgs java.lang.String[]
 */
public misc_teleporter_dest(Element spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);
	
	fEntity.setModel("models/objects/dmspot/tris.md2");
	fEntity.setSolid(NativeEntity.SOLID_BBOX);
	fEntity.setMins(-32, -32, -24);
	fEntity.setMaxs( 32,  32, -16);
	fEntity.linkEntity();
	}
}