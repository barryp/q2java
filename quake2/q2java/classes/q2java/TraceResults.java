
package q2java;

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
	public Vec3 			fEndPos;
	public Vec3 			fPlaneNormal;
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
	Vec3 endPos, Vec3 planeNormal, float planeDist, byte planeType,
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