package q2java;

import java.lang.reflect.Array;

/**
 * Quickly Recycle objects - nothing in it is particularly tie
 * to Quake or Q2Java other than it being unsynchronized 
 * (so that it's only suitable to use in the main game thread).
 *
 * @author Barry Pederson
 */
 
public class Recycler 
	{
	private Class fClass;
	private Object[] fBin;
	private int fTop;

	// Fields for measuring and tuning behavior
	private int fOverflowCounter;
	private int fUnderflowCounter;
	private boolean fHasOverflowed;

	// Static fields for managing a list of Recyclers
	private static Recycler[] gAllRecyclers = new Recycler[16];
	private static int gAllTop;

	// constants defining how large a recyle bin can grow, and
	// the increment it grows at.
	private final static int MAX_BIN_SIZE = 256;
	private final static int BIN_GROW_SIZE = 16;
	
/**
 * Create a Recycler for a particular class.  Private so that it can't
 * be called by users - they must call getRecycler(), which will prevent
 * more than one Recycler being created for a given class.
 */
private Recycler(Class cls) 
	{
	fClass = cls;

	// create an array with the initial bin size
	createBin(BIN_GROW_SIZE);
	}
/**
 * Create an array to hold objects in.
 * @param size int
 */
private void createBin(int size) 
	{
	// create an array of the appropriate type
	fBin = (Object[]) Array.newInstance(fClass, size);	
	}
/**
 * Get an object of the specified class - it may be brand new or may
 * be recycled, so you can't be sure what state it's in.
 * @return java.lang.Object
 * @param cls java.lang.Class
 */
public static Object get(Class cls) 
	{
	return getRecycler(cls).getObject();
	}
/**
 * Get or create an object of this particular Recycler's class.
 * @return java.lang.Object
 */
public final Object getObject() 
	{
	if (fTop > 0)
		{
		fTop--;
		Object result = fBin[fTop];
		fBin[fTop] = null;
		return result;
		}

	fUnderflowCounter++;
	if (fHasOverflowed && (fBin.length < MAX_BIN_SIZE))
		{
		createBin(fBin.length + BIN_GROW_SIZE);
		fHasOverflowed = false;
		}
		
	try
		{
		return fClass.newInstance();
		}
	catch (Exception e)
		{
		return null;
		}
	}
	
/**
 * Get the count of how many times this recycler discarded objects
 * because the bin was full.
 * @return int
 */
public int getOverflowCounter() 
	{
	return fOverflowCounter;
	}
/**
 * Get a Recycler object for a particular class.
 * @return q2java.Recycler
 * @param cls java.lang.Class
 */
public static Recycler getRecycler(Class cls) 
	{
	for (int i = 0; i < gAllTop; i++)
		{
		if (gAllRecyclers[i].fClass == cls)
			return gAllRecyclers[i];
		}
		
	// wasn't found, so create a new Recycler
	Recycler r = new Recycler(cls);

	// make sure the list of recyclers is big enough
	if (gAllTop == gAllRecyclers.length)
		{
		Recycler[] newArray = new Recycler[gAllTop+16];
		System.arraycopy(gAllRecyclers, 0, newArray, 0, gAllTop);
		gAllRecyclers = newArray;
		}

	// add new Recycler to list
	gAllRecyclers[gAllTop++] = r;

	// return the new Recycler
	return r;
	}
/**
 * Get which class this particular handles.
 * @return java.lang.Class
 */
public Class getRecyclerClass() 
	{
	return fClass;
	}
/**
 * Get all the Recyclers that currently exist.
 * @return q2java.Recycler[]
 */
public static Recycler[] getRecyclers() 
	{
	Recycler[] result = new Recycler[gAllTop];
	System.arraycopy(gAllRecyclers, 0, result, 0, gAllTop);
	return result;
	}
/**
 * Get the count of how many times this recycler had to create
 * new objects because the bin was empty.
 * @return int
 */
public int getUnderflowCounter() 
	{
	return fUnderflowCounter;
	}
/**
 * Put an object in the appropriate Recycle bin.
 * @return java.lang.Object
 * @param cls java.lang.Class
 */
public static void put(Object obj) 
	{
	getRecycler(obj.getClass()).putObject(obj);
	}
/**
 * Put an object into the Recycle bin.
 * @param obj java.lang.Object
 */
public final void putObject(Object obj) 
	{
	if (obj == null)
		return;
		
	if (fTop < fBin.length)
		fBin[fTop++] = obj;
	else
		{
		fOverflowCounter++;
		fHasOverflowed = true;
		}
	}
}