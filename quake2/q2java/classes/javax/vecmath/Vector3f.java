
package javax.vecmath;

/**
 * A 3 element vector that is represented by single precision floating point
 * x,y,z coordinates. If this value represents a normal, then it should be
 * normalized. 
 * 
 */
public class Vector3f extends Tuple3f 
	{
	
/**
 * Constructs and initializes a Vector3f to (0,0,0). 
 */
public Vector3f() 
	{
	super();
	}
/**
 * Constructs and initializes a Vector3f from the specified xyz
 *  coordinates. 
 * @param nx float
 * @param ny float
 * @param nz float
 */
public Vector3f(float nx, float ny, float nz) 
	{
	super(nx, ny, nz);
	}
/**
 * Constructs and initializes a Vector3f from the specified Tuple3f. 
 * @param t1 javax.vecmath.Tuple3f
 */
public Vector3f(Tuple3f t1) 
	{
	super(t1);
	}
/**
 * Constructs and initializes a Vector3f from the specified Vector3f. 
 * @param v javax.vecmath.Vector3f
 */
public Vector3f (Vector3f v) 
	{
	super(v);
	}
/**
 * Sets this vector to be the vector cross product of vectors v1 and
 *   v2. 
 * @param v1 javax.vecmath.Vector3f
 * @param v2 javax.vecmath.Vector3f
 */
public void cross(Vector3f v1, Vector3f v2) 
	{
	x = (v1.y * v2.z) - (v1.z * v2.y);
	y = (v1.z * v2.x) - (v1.x * v2.z);
	z = (v1.x * v2.y) - (v1.y * v2.x);
	}
/**
 * Computes the dot product of this vector and vector v. 
 * @return float
 * @param v javax.vecmath.Vector3f
 */
public float dot(Vector3f v) 
	{
	return (x * v.x) + (y * v.y) + (z * v.z);
	}
/**
 * Returns the length of this vector. 
 * @return float
 */
public float length() 
	{
	return  (float) Math.sqrt((x*x) + (y*y) + (z*z));
	}
/**
 * Returns the square of the length of this vector. 
 * @return float
 */
public float lengthSquared() 
	{
	return (x*x) + (y*y) + (z*z);
	}
/**
 * Normalizes this vector in place. 
 */
public void normalize() 
	{
	double length = Math.sqrt((x*x) + (y*y) + (z*z));

	if (length != 0.0)
		{
		double ilength = 1 / length;
		x *= ilength;
		y *= ilength;
		z *= ilength;
		}		
	}
/**
 * Sets the value of this vector to the normalization of vector v.  
 */
public void normalize(Vector3f v) 
	{
	float length = (float) Math.sqrt((v.x*v.x) + (v.y*v.y) + (v.z*v.z));

	if (length == 0.0)
		x = y = z = 0f;
	else
		{
		float ilength = 1 / length;
		x = v.x * ilength;
		y = v.y * ilength;
		z = v.z * ilength;
		}		
	}
}