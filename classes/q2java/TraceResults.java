
package q2java;

import javax.vecmath.*;

/**
 * Java equivalent to the C trace_t, cplane_t, 
 * and csurface_t types.
 *
 * @author Barry Pederson
 */
public class TraceResults 
	{
	public boolean 		fAllSolid;
	public boolean 		fStartSolid;
	public float 			fFraction;
	public Point3f		fEndPos;
	public Vector3f		fPlaneNormal;
	public float 			fPlaneDist;
	public byte 			fPlaneType;
	public byte 			fPlaneSignbits;
	public String 		fSurfaceName;
	public int 			fSurfaceFlags;
	public int 			fSurfaceValue;	
	public int 			fContents;
	public NativeEntity	fEntity;
	
/**
 * Used by the DLL to construct new TraceResults objects.
 */
private TraceResults (boolean allSolid, boolean startSolid, float fraction,
	Point3f endPos, Vector3f planeNormal, float planeDist, byte planeType,
	byte planeSignbits, String surfaceName, int surfaceFlags, 
	int surfaceValue, int contents, NativeEntity ent) 
	{
	fAllSolid = allSolid;
	fStartSolid = startSolid;
	fFraction = fraction;
	fEndPos = endPos;
	fPlaneNormal = planeNormal;
	fPlaneDist = planeDist;
	fPlaneType = planeType;
	fPlaneSignbits = planeSignbits;
	fSurfaceName = surfaceName;
	fSurfaceFlags = surfaceFlags;
	fSurfaceValue = surfaceValue;	
	fContents = contents;
	fEntity = ent;
	}
}