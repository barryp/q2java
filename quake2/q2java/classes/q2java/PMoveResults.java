
package q2java;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class PMoveResults 
	{
	public final static int BUTTON_ATTACK	= 1;
	public final static int BUTTON_USE		= 2;
	public final static int BUTTON_ANY		= 128;	// any key whatsoever
		
	public byte	fCmdButtons;
	public short	fCmdAngle0;
	public short	fCmdAngle1;
	public short	fCmdAngle2;
	public short	fCmdForwardMove;
	public short	fCmdSideMove;
	public short	fCmdUpMove;
	public byte	fCmdLightLevel;
	public NativeEntity[] fTouched;
	public float	fViewHeight;
	public NativeEntity	fGroundEntity;
	public int	fWaterType;
	public int 	fWaterLevel;
	
/**
 * This method was created by a SmartGuide.
 */
private PMoveResults(byte cmdButtons, short cmdAngle0, short cmdAngle1, short cmdAngle2,
	short cmdForwardMove, short cmdSideMove, short cmdUpMove,
	byte cmdLightLevel, NativeEntity[] touched, float viewHeight,
	NativeEntity groundEntity, int waterType, int waterLevel) 
	{
	fCmdButtons = cmdButtons;
	fCmdAngle0 = cmdAngle0;
	fCmdAngle1 = cmdAngle1;
	fCmdAngle2 = cmdAngle2;
	fCmdForwardMove = cmdForwardMove;
	fCmdSideMove = cmdSideMove;
	fCmdUpMove = cmdUpMove;
	fCmdLightLevel = cmdLightLevel;
	fTouched = touched;
	fViewHeight = viewHeight;
	fGroundEntity = groundEntity;
	fWaterType = waterType;
	fWaterLevel = waterLevel;	
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() 
	{
	StringBuffer sb = new StringBuffer();
	sb.append("fCmdButtons: ");
	sb.append(fCmdButtons);
	sb.append('\n');
	sb.append("fCmdAngle(s): (" + fCmdAngle0 + ", " + fCmdAngle1 + ", " + fCmdAngle2 + ")\n");
	sb.append("fCmdForwardMove: " + fCmdForwardMove + " fCmdSideMove:" + fCmdSideMove + " fCmdUpMove:" + fCmdUpMove + "\n");
	sb.append("fCmdLightLevel: " + fCmdLightLevel + "\n");
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