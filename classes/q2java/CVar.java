package q2java;

/**
 * Console variable handling...a little bizarre I think...but that's the way Q2 works.
 *
 * Now multi-thread safe.
 *
 * @author Barry Pederson
 */
public class CVar
	{
	/**
	 * Flag indicating cvar should be saved to vars.rc
	 */
	public final static int CVAR_ARCHIVE	= 1;		
	
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

/*
 * Code written for a multithread-safe version of CVar, but decided not
 * to use it for now.
 
 
	private static class DeferredCVar implements Runnable
		{
		private String fName;
		private String fValue;
		private int fFlags;
		private int fCallType;
		int fResult;
		
		public DeferredCVar(String name, String value, int flags, int callType)
			{
			fName = name;
			fValue = value;
			fFlags = flags;
			fCallType = callType;
			}

		public void run()
			{
			fResult = CVar.cvar0(fName, fValue, fFlags, fCallType);		
			}
		}

	private static class DeferredGetFloat implements Runnable
		{
		private int fPointer;
		float fResult;

		public DeferredGetFloat(int ptr)
			{
			fPointer = ptr;
			}
			
		public void run()
			{
			fResult = CVar.getFloat0(fPointer);
			}
		}

	private static class DeferredGetString implements Runnable
		{
		private int fPointer;
		String fResult;

		public DeferredGetString(int ptr)
			{
			fPointer = ptr;
			}
			
		public void run()
			{
			fResult = CVar.getString0(fPointer);
			}
		}

*/		
	
/**
 * Construct a CVar object.
 * @param name name of the cvar
 * @param value value to set cvar to if it doesn't already have a value?
 * @param flags flags to create cvar with, OR CVAR_* constants together for this value.
 */
public CVar(String name, String value, int flags)
	{
	fName = name;

	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == Engine.getGameThread())
		fCVarPointer = cvar0(name, value, flags, CALL_CVAR);
/*		
	else
		{
		CVar.DeferredCVar def = new CVar.DeferredCVar(name, value, flags, CALL_CVAR);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		fCVarPointer = def.fResult;
		}
*/		
	}
private native static int cvar0(String name, String value, int flags, int callType);
private static void cvarNoWait(String name, String value, int flags, int callType)
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == Engine.getGameThread())
		cvar0(name, value, flags, callType);
//	else
//		Engine.invokeLater(new CVar.DeferredCVar(name, value, flags, callType));
	}
/**
 * Force a cvar to be set to a given value. Equivalent to gi.cvar_forceset(char *name, char *value).
 * @param name name of the cvar.
 * @param value new value for the cvar.
 * @return a reference to the CVar specified by the name parameter.
 */
public void forceSet(String value)
	{
//	cvarNoWait(fName, value, 0, CALL_CVAR_FORCESET);
	cvar0(fName, value, 0, CALL_CVAR_FORCESET);
	}
/**
 * Fetch the float value of a CVar.
 * @return the value as a float
 */
public float getFloat()
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == Engine.getGameThread())
		return getFloat0(fCVarPointer);
/*		
	else
		{
		CVar.DeferredGetFloat def = new CVar.DeferredGetFloat(fCVarPointer);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static float getFloat0(int ptr);
/**
 * Get the name of this CVar.
 * @return java.lang.String
 */
public String getName() 
	{
	return fName;
	}
/**
 * Fetch the String value of a CVar.
 * @return the value as a String
 */
public String getString()
	{
	// if this call is being made on a thread other than
	// the main game thread, queue it up to be run
	// by the main thread
//	if (Thread.currentThread() == Engine.getGameThread())
		return getString0(fCVarPointer);
/*		
	else
		{
		CVar.DeferredGetString def = new CVar.DeferredGetString(fCVarPointer);
		try
			{
			Engine.invokeAndWait(def);
			}
		catch (Exception e)
			{
			}
		return def.fResult;
		}
*/		
	}
private native static String getString0(int ptr);
/**
 * Set a cvar to a given value. Equivalent to gi.cvar_set(char *name, char *value).
 * @param value new value for the cvar.
 */
public void setValue(String value)
	{
//	cvarNoWait(fName, value, 0, CALL_CVAR_SET);
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