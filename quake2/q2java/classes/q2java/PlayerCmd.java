
package q2java;

/**
 * Java equivalent to the C usercmd_t type.
 * 
 * @author Barry Pederson 
 */
public class PlayerCmd 
	{
	public final static int BUTTON_ATTACK	= 1;
	public final static int BUTTON_USE		= 2;
	public final static int BUTTON_ANY		= 128;	// any key whatsoever
			
	public byte    fMsec;
	public byte    fButtons;
	public short   fPitch;
	public short   fYaw;
	public short   fRoll;
	public short   fForwardMove, fSideMove, fUpMove;
	public byte    fImpulse;        // remove?
	public byte    fLightLevel;     // light level the player is standing on	
	
/**
 * Convert a float angle to the SHORT format used by PlayerCmd.
 * @return short
 * @param f float
 */
public static short float2Short(float f) 
	{
	return (short) (((int)((f)*65536/360)) & 65535);
	}
/**
 * Used by the DLL to create set the fields 
 * in a static PlayerCmd object.
 *
 * Rather than creating and garbage collecting PlayerCmd objects
 * repeatedly, the NativeEntity class keeps one static
 * PlayerCmd object. The DLL uses this function to set its fields,
 * and that single static object is passed to the Player.playerThink() 
 * method every time that method is called.
 *
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
private void set(byte msec, byte buttons, short pitch, short yaw, short roll, short forward, short side, short up, byte impulse, byte lightlevel) 
	{
	fMsec = msec;
	fButtons = buttons;
	fPitch = pitch;
	fYaw = yaw;
	fRoll = roll;
	fForwardMove = forward;
	fSideMove = side;
	fUpMove = up;
	fImpulse = impulse;
	fLightLevel = lightlevel;
	}
/**
 * Convert a short angle to a float.
 * @return float
 * @param s short
 */
public static float short2float(short s) 
	{
	return (s * (360.0f/65536));
	}
}