
package q2java;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class UserCmd 
	{
	public final static int BUTTON_ATTACK	= 1;
	public final static int BUTTON_USE		= 2;
	public final static int BUTTON_ANY		= 128;	// any key whatsoever
			
	public byte    fMsec;
	public byte    fButtons;
	public short   fAngles0;
	public short   fAngles1;
	public short   fAngles2;
	public short   fForwardMove, fSideMove, fUpMove;
	public byte    fImpulse;        // remove?
	public byte    fLightLevel;     // light level the player is standing on	
	
/**
 * This method was created by a SmartGuide.
 * @param msec byte
 * @param buttons byte
 * @param angle0 short
 * @param angle1 short
 * @param angle2 short
 * @param forward short
 * @param side short
 * @param up short
 * @param impulse byte
 * @param lightlevel byte
 */
private void set(byte msec, byte buttons, short angle0, short angle1, short angle2, short forward, short side, short up, byte impulse, byte lightlevel) 
	{
	fMsec = msec;
	fButtons = buttons;
	fAngles0 = angle0;
	fAngles1 = angle1;
	fAngles2 = angle2;
	fForwardMove = forward;
	fSideMove = side;
	fUpMove = up;
	fImpulse = impulse;
	fLightLevel = lightlevel;
	}
}