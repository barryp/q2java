
package q2java;


import java.util.StringTokenizer;

public final class Vec3
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
/**
 * Construct a vector from a String of three float values
 * The values can be separated by spaces and/or commas, 
 * can can be inside parentheses.  Some valid inputs are:
 *   "1 2 3"            -- like we'll get from spawn strings
 *   "(1.0, 2.0, 3.0)"  -- a nice-looking way to print vectors
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
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 * @param x float
 * @param y float
 * @param z float
 */
public Vec3 add(float nx, float ny, float nz) 
	{
	x += nx;
	y += ny;
	z += nz;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 * @param v q2java.Vec3
 */
public Vec3 add(Vec3 v) 
	{
	x += v.x;
	y += v.y;
	z += v.z;
	return this;
	}
/**
 * Given a Vec3 that represents Roll, Pitch, and Yaw angles, 
 * calculate 3 unit vectors that point forward, right, 
 * and up (?)
 */
public void angleVectors(Vec3 forward, Vec3 right, Vec3 up) 
	{
	double		angle;
	double		sr, sp, sy, cr, cp, cy;

	// Pitch
	angle = x * (Math.PI / 180.0);
	sp = Math.sin(angle);
	cp = Math.cos(angle);

	// Yaw
	angle = y * (Math.PI / 180.0);
	sy = Math.sin(angle);
	cy = Math.cos(angle);
	
	// Roll
	angle = z * (Math.PI / 180.0);
	sr = Math.sin(angle);
	cr = Math.cos(angle);

	if (forward != null)
		{
		forward.x = (float) (cp*cy);
		forward.y = (float) (cp*sy);
		forward.z = (float) -sp;
		}
		
	if (right != null)
		{
		right.x = (float) (-1*sr*sp*cy+-1*cr*-sy);
		right.y = (float) (-1*sr*sp*sy+-1*cr*cy);
		right.z = (float) (-1*sr*cp);
		}
		
	if (up != null)
		{
		up.x = (float) (cr*sp*cy+-sr*-sy);
		up.y = (float) (cr*sp*sy+-sr*cy);
		up.z = (float) (cr*cp);
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 */
public Vec3 clear() 
	{
	x = y = z = 0.0F;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @return float
 * @param a q2java.Vec3
 * @param b q2java.Vec3
 */
public static float dotProduct(Vec3 a, Vec3 b) 
	{
	return (a.x*b.x) + (a.y*b.y) + (a.z*b.z);
	}
/**
 * Scale into a unit vector, and return the updated object
 * @return q2java.Vec3
 */
public Vec3 normalize() 
	{
	double length = Math.sqrt((x*x) + (y*y) + (z*z));

	if (length != 0.0)
		{
		double ilength = 1 / length;
		x *= ilength;
		y *= ilength;
		z *= ilength;
		}
		
	return this;
	}
/**
 * This method was created by a SmartGuide.
 */
public static Vec3 projectSource(Vec3 point, Vec3 distance, Vec3 forward, Vec3 right) 
	{
	Vec3 result = new Vec3();

	result.x = point.x + forward.x * distance.x + right.x * distance.y;
	result.y = point.y + forward.y * distance.x + right.y * distance.y;
	result.z = point.z + forward.z * distance.x + right.z * distance.y + distance.z;
	
	return result;
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 * @param val float
 */
public Vec3 scale(float val) 
	{
	x *= val;
	y *= val;
	z *= val;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 */
public Vec3 toAngles() 
	{
	double forward;
	float yaw, pitch;
	
	if (y == 0 && x == 0)
		{
		yaw = 0;
		if (z > 0)
			pitch = 90;
		else
			pitch = 270;
		}
	else
		{
		yaw = (int) (Math.atan2(y, x) * 180 / Math.PI);
		if (yaw < 0)
			yaw += 360;

		forward = Math.sqrt((x*x) + (y*y));
		pitch = (int) (Math.atan2(z, forward) * 180 / Math.PI);
		if (pitch < 0)
			pitch += 360;
		}

	return new Vec3(-pitch, yaw, 0);
	}
public String toString()
	{
	return x + " " + y + " " + z;
	}   
}