

import java.util.StringTokenizer;

public class Vec3
	{
	public float x;
	public float y;
	public float z;
	
	public Vec3()
		{
		}
	public Vec3(float newx, float newy, float newz)
		{
		x = newx;
		y = newy;
		z = newz;
		}
	/*
	  * Construct a vector from a String of three float values
	 * The values can be separated by spaces and/or commas, 
	 * can can be inside parentheses.  Some valid inputs are:
	  *   "1 2 3"            -- like we'll get from spawn strings
	  *   "(1.0, 2.0, 3.0)"  -- like the toString() method puts out
	  *   "(1    2.0,,,,3"   -- a really ugly string that should still work
	  * 
	  * I wonder why the java.lang.Float class doesn't have a 
	 * parseFloat(String) method?
	  */
	public Vec3(String s) throws NumberFormatException
		{
		StringTokenizer st = new StringTokenizer(s, "(, )");
		if (st.countTokens() != 3)
			throw new NumberFormatException("Vec3 constructor called with [" + s + "]");

		String t;
		Float f;

		t = st.nextToken();
		f = Float.valueOf(t);
		x = f.floatValue();

		t = st.nextToken();
		f = Float.valueOf(t);
		y = f.floatValue();

		t = st.nextToken();
		f = Float.valueOf(t);
		z = f.floatValue();
		}
/**
 * This method was created by a SmartGuide.
 * @param v Vec3
 */
public Vec3( Vec3 v) 
	{
	x = v.x;
	y = v.y;
	z = v.z;
	}
	public String toString()
		{
		return "(" + x + ", " + y + ", " + z + ")";
		}   
}