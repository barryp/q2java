
package q2java;

import java.util.StringTokenizer;

/**
 * Vec3 is an equivalent to Quake2's vec3_t structure.
 */

public final class Vec3
	{
	public float x;
	public float y;
	public float z;
	
/**
 * Construct a new vector with values set to (0, 0, 0).
 */
public Vec3()
	{
	}
/**
 * Construct a new vector and set the x, y, and z fields to the specified values.
 * @param newx x value for new vector
 * @param newy y value for new vector
 * @param newz z value for new vector
 */
public Vec3(float newx, float newy, float newz)
	{
	x = newx;
	y = newy;
	z = newz;
	}
/**
 * Construct a vector from a String of three float values.
 * The values can be separated by spaces and/or commas, 
 * can can be inside parentheses.  Some valid inputs are:<br>
 *   "1 2 3"            -- like we'll get from spawn strings.<br>
 *   "(1.0, 2.0, 3.0)"  -- a nice-looking way to print vectors.<br>
 *   "(1    2.0,,,,3"   -- a really ugly string that should still work.
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
 * Construct a new vector, copying values from another vector.
 * @param v a vector to copy values from.
 */
public Vec3(Vec3 v) 
	{
	x = v.x;
	y = v.y;
	z = v.z;
	}
/**
 * Convert all components into absolute values.
 * @return q2java.Vec3
 */
public Vec3 abs() 
	{
	x = Math.abs(x);
	y = Math.abs(y);
	z = Math.abs(z);
	return this;
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
 * This method was created by a SmartGuide.
 * @param point q2java.Vec3
 * @param mins q2java.Vec3
 * @param maxs q2java.Vec3
 */
public static void addPointToBounds(Vec3 point, Vec3 mins, Vec3 maxs) 
	{
	mins.x = Math.min(mins.x, point.x);
	mins.y = Math.min(mins.y, point.y);
	mins.z = Math.min(mins.z, point.z);
	
	maxs.x = Math.max(maxs.x, point.x);
	maxs.y = Math.max(maxs.y, point.y);
	maxs.z = Math.max(maxs.z, point.z);
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
 * Clamp the vector to 1/8 units.  This way positions will
 * be accurate for client side prediction.
 * @return The same vector after clamping.
 */
public Vec3 clampEight() 
	{
	x = Math.round(x * 8) * 0.125F;
	y = Math.round(y * 8) * 0.125F;
	z = Math.round(z * 8) * 0.125F;
	
	return this;
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
 * @return q2java.Vec3
 * @param v q2java.Vec3
 */
public Vec3 copyFrom(Vec3 v) 
	{
	x = v.x;
	y = v.y;
	z = v.z;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @param v q2java.Vec3
 */
public Vec3 copyTo(Vec3 v) 
	{
	v.x = x;
	v.y = y;
	v.z = z;
	return v;
	}
/**
 * Calculate cross product of two vectors.
 * @return q2java.Vec3
 * @param a q2java.Vec3
 * @param b q2java.Vec3
 */
public static Vec3 crossProduct(Vec3 a, Vec3 b) 
	{
	Vec3 result = new Vec3();
	result.x = (a.y * b.z) - (a.z * b.y);
	result.y = (a.z * b.x) - (a.x * b.z);
	result.z = (a.x * b.y) - (a.y * b.x);
	return result;
	}
/**
 * Find dot product of two vectors.
 * @return dot product
 * @param a q2java.Vec3
 * @param b q2java.Vec3
 */
public static float dotProduct(Vec3 a, Vec3 b) 
	{
	return (a.x*b.x) + (a.y*b.y) + (a.z*b.z);
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param v q2java.Vec3
 */
public boolean equals(float nx, float ny, float nz) 
	{
	return (x == nx) && (y == ny) && (z == nz);
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param v q2java.Vec3
 */
public boolean equals(Vec3 v) 
	{
	return (x == v.x) && (y == v.y) && (z == v.z);
	}
/**
 * This method was created by a SmartGuide.
 * @return float
 */
public float length() 
	{
	return  (float) Math.sqrt((x*x) + (y*y) + (z*z));
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
 * Scale into a unit vector, and return the original length.
 * @return q2java.Vec3
 */
public float normalizeLength() 
	{
	double length = Math.sqrt((x*x) + (y*y) + (z*z));

	if (length != 0.0)
		{
		double ilength = 1 / length;
		x *= ilength;
		y *= ilength;
		z *= ilength;
		}
		
	return (float) length;
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
 * @param nx float
 * @param ny float
 * @param nz float
 */
public Vec3 set(double nx, double ny, double nz) 
	{
	x = (float) nx;
	y = (float) ny;
	z = (float) nz;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 * @param x float
 * @param y float
 * @param z float
 */
public Vec3 subtract(float nx, float ny, float nz) 
	{
	x -= nx;
	y -= ny;
	z -= nz;
	return this;
	}
/**
 * This method was created by a SmartGuide.
 * @return q2java.Vec3
 * @param v q2java.Vec3
 */
public Vec3 subtract(Vec3 v) 
	{
	x -= v.x;
	y -= v.y;
	z -= v.z;
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
/**
 * Multiply and accumulate?
 * @return q2java.Vec3
 * @param f float
 * @param v q2java.Vec3
 */
public Vec3 vectorMA(float f, Vec3 v) 
	{
	Vec3 result = new Vec3(this);
	result.x += v.x * f;
	result.y += v.y * f;
	result.z += v.z * f;
	return result;
	}
}