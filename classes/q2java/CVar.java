
package q2java;

/**
 * Console variable handling...a little bizarre I think...but that's the way Q2 works
 *
 * @author Barry Pederson
 */
public class CVar
	{
	/**
	 * Flag indicating cvar should be saved to vars.rc
	 */
	public final static int CVAR_ARCHIVE		= 1;		
	
	/**
	 * Flag indicating cvar is added to userinfo when changed.
	 */
	public final static int CVAR_USERINFO	= 2;	
	
	/** 
	 * Flag indicating cvar is added to serverinfo when changed.
	 */
	public final static int CVAR_SERVERINFO	= 4;		
	
	/**
	 * Flag indicating cvar is not allowed to be changed from console,
	 * but can be set from command line.
	 */
	public final static int CVAR_NOSET		= 8;	
	
	/**
	 * Flag indicating changes should be saved until server restart.
	 */
	public final static int CVAR_LATCH		= 16;

	// private constants for native method
	private final static int CALL_CVAR			= 0;
	private final static int CALL_CVAR_SET		= 1;
	private final static int CALL_CVAR_FORCESET	= 2;

	private int fCVarPointer;
	private String fName;
	
/**
 * Construct a CVar object.
 * @param name name of the cvar
 * @param value value to set cvar to if it doesn't already have a value?
 * @param flags flags to create cvar with, OR CVAR_* constants together for this value.
 */
public CVar(String name, String value, int flags)
	{
	fName = name;
	fCVarPointer = cvar0(name, value, flags, CALL_CVAR);
	}

private native static int cvar0(String name, String value, int flags, int callType);

/**
 * Force a cvar to be set to a given value. Equivalent to gi.cvar_forceset(char *name, char *value).
 * @param name name of the cvar.
 * @param value new value for the cvar.
 * @return a reference to the CVar specified by the name parameter.
 */
public void forceSet(String value)
	{
	cvar0(fName, value, 0, CALL_CVAR_FORCESET);
	}
/**
 * Fetch the float value of a CVar.
 * @return the value as a float
 */
public float getFloat()
	{
	return getFloat0(fCVarPointer);
	}

private native static float getFloat0(int ptr);

/**
 * Fetch the String value of a CVar.
 * @return the value as a String
 */
public String getString()
	{
	return getString0(fCVarPointer);
	}

private native static String getString0(int ptr);

/**
 * Set a cvar to a given value. Equivalent to gi.cvar_set(char *name, char *value).
 * @param value new value for the cvar.
 */
public void setValue(String value)
	{
	cvar0(fName, value, 0, CALL_CVAR_SET);
	}
/**
 * Converts the object to a string. The string representation is:<br> 
 * "<code>CVar <i>cvar-name</i> = [<i>cvar-value</i>]</code>".
 *
 * @return a string representation of the CVar object.
 */
public String toString()
	{
	return "CVar " + fName + " = [" + getString() + "]";
	}
}