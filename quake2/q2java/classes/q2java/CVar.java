
package q2java;

/**
 * Console variable handline...a little bizarre I think...but that's the way Q2 works
 */
public class CVar
	{
	public final static int CVAR_ARCHIVE		= 1;		// set to cause it to be saved to vars.rc
	public final static int CVAR_USERINFO	= 2;		// added to userinfo  when changed
	public final static int CVAR_SERVERINFO	= 4;		// added to serverinfo when changed
	public final static int CVAR_NOSET		= 8;		// don't allow change from console at all,
												// but can be set from the command line
	public final static int CVAR_LATCH		= 16;	// save changes until server restart

	// private constants for native method
	private final static int CALL_CVAR			= 0;
	private final static int CALL_CVAR_SET		= 1;
	private final static int CALL_CVAR_FORCESET	= 2;

	private int fCVarPointer;
	private String fName;
	
	private CVar(String name, int pointer)
		{
		fName = name;
		fCVarPointer = pointer;
		}
	public CVar(String name, String value, int flags)
		{
		fName = name;
		fCVarPointer = cvar0(name, value, flags, CALL_CVAR);
		}
	// ------- Static methods, similar to the C versions

	public static CVar cvar(String name, String value, int flags)
		{
		return new CVar(name, cvar0(name, value, flags, CALL_CVAR));
		}

	private native static int cvar0(String name, String value, int flags, int callType);

	public static CVar forceSet(String name, String value)
		{
		return new CVar(name, cvar0(name, value, 0, CALL_CVAR_FORCESET));
		}
	public float getFloat()
		{
		return getFloat0(fCVarPointer);
		}

	private native static float getFloat0(int ptr);

	public String getString()
		{
		return getString0(fCVarPointer);
		}

	// -------- Native methods ----------------------

	private native static String getString0(int ptr);

	public static CVar set(String name, String value)
		{
		return new CVar(name, cvar0(name, value, 0, CALL_CVAR_SET));
		}
	public String toString()
		{
		return "CVar " + fName + " = [" + getString() + "]";
		}
}