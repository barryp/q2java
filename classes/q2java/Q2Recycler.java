package q2java;

import java.util.*;
import javax.vecmath.*;

/**
 * Utility class to speed up access to the Recycler for
 * classes that are used quite a bit in Q2Java.
 *
 * @author Barry Pederson
 */
public class Q2Recycler 
	{
	private static Recycler gStringBufferRecycler = Recycler.getRecycler(StringBuffer.class);
	private static Recycler gHashtableRecycler = Recycler.getRecycler(Hashtable.class);	
	private static Recycler gVectorRecycler = Recycler.getRecycler(Vector.class);
	private static Recycler gVector3fRecycler = Recycler.getRecycler(Vector3f.class);
	private static Recycler gPoint3fRecycler = Recycler.getRecycler(Point3f.class);
	private static Recycler gAngle3fRecycler = Recycler.getRecycler(Angle3f.class);
	
/**
 * Private constructor so nobody tries to make one of these.
 */
private Q2Recycler() 
	{
	}
/**
 * Get an Angle3f.
 * @return q2java.Angle3f - values of x, y, and z fields are undefined.
 */
public static Angle3f getAngle3f() 
	{
	return (Angle3f) gAngle3fRecycler.getObject();
	}
/**
 * Get a Hashtable from the Recycler.
 * @return java.util.Hashtable with no entries.
 */
public static Hashtable getHashtable() 
	{
	Hashtable h = (Hashtable) gHashtableRecycler.getObject();
	h.clear();
	return h;
	}
/**
 * Get a Point3f.
 * @return javax.vecmath.Point3f - values of x, y, and z fields are undefined.
 */
public static Point3f getPoint3f() 
	{
	return (Point3f) gPoint3fRecycler.getObject();
	}
/**
 * Get a StringBuffer from the Recycler.
 * @return java.lang.StringBuffer with length set to zero.
 */
public static StringBuffer getStringBuffer() 
	{
	StringBuffer sb = (StringBuffer) gStringBufferRecycler.getObject();
	sb.setLength(0);
	return sb;
	}
/**
 * Get a Vector.
 * @return java.util.Vector all elements removed.
 */
public static Vector getVector() 
	{
	Vector v = (Vector) gVectorRecycler.getObject();
	v.removeAllElements();
	return v;
	}
/**
 * Get a Vector3f.
 * @return javax.vecmath.Vector3f - values of x, y, and z fields are undefined.
 */
public static Vector3f getVector3f() 
	{
	return (Vector3f) gVector3fRecycler.getObject();
	}
/**
 * Put a StringBuffer into the Recycler.  Quicker than using
 * "Recycler.put(sb)" because this class already has a reference
 * to the StringBuffer Recycler (the Recycler class would have to
 * search for it).
 
 * @param sb java.lang.StringBuffer
 */
public static void put(StringBuffer sb) 
	{
	gStringBufferRecycler.putObject(sb);
	}
/**
 * Put a Hashtable into the Recycler.  Quicker than using
 * "Recycler.put(h)" because this class already has a reference
 * to the Hashtable Recycler (the Recycler class would have to
 * search for it).
 
 * @param sb java.util.Hashtable
 */
public static void put(Hashtable h) 
	{
	h.clear();
	gHashtableRecycler.putObject(h);
	}
/**
 * Put a Vector into the Recycler.  Quicker than using
 * "Recycler.put(v)" because this class already has a reference
 * to the Vector Recycler (the Recycler class would have to
 * search for it).
 
 * @param v java.util.Vector - this method will make sure all elements are removed.
 */
public static void put(Vector v) 
	{
	v.removeAllElements();
	gVectorRecycler.putObject(v);
	}
/**
 * Put a Point3f into the Recycler.
 *
 * @param v javax.vecmath.Point3f.
 */
public static void put(Point3f p) 
	{
	gPoint3fRecycler.putObject(p);
	}
/**
 * Put a Vector3f into the Recycler.
 *
 * @param v javax.vecmath.Vector3f.
 */
public static void put(Vector3f v) 
	{
	gVector3fRecycler.putObject(v);
	}
/**
 * Put an Angle3f into the Recycler.
 *
 * @param a q2java.Angle3f.
 */
public static void put(Angle3f a) 
	{
	gAngle3fRecycler.putObject(a);
	}
}