
package q2java;

/**
 * Java equivalent to the output fields of the C pmove_t type.
 * 
 * @author Barry Pederson
 */
public class PMoveResults 
	{
	public NativeEntity[]	fTouched;
	public float			fViewHeight;
	public NativeEntity	fGroundEntity;
	public int			fWaterType;
	public int 			fWaterLevel;
	
/**
 * Used by the DLL to create new PMoveResults objects.
 */
private PMoveResults(NativeEntity[] touched, float viewHeight,
	NativeEntity groundEntity, int waterType, int waterLevel) 
	{
	fTouched = touched;
	fViewHeight = viewHeight;
	fGroundEntity = groundEntity;
	fWaterType = waterType;
	fWaterLevel = waterLevel;	
	}
/**
 * Utility function to help debugging.
 * @return java.lang.String
 */
public String toString() 
	{
	StringBuffer sb = new StringBuffer();
	if (fTouched == null)		
		sb.append("fTouched: (null)\n");
	else
		{
		sb.append("fTouched:\n");
		for (int i = 0; i < fTouched.length; i++)
			sb.append("    " + fTouched[i] + "\n");
		}		
	sb.append("fViewHeight: " + fViewHeight + "\n");
	sb.append("fGroundEntity: " + fGroundEntity + "\n");
	sb.append("fWaterType: " + fWaterType + " fWaterLevel: " + fWaterLevel + "\n");
	return sb.toString();
	}
}