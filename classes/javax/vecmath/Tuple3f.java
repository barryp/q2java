
package javax.vecmath;

/**
 * A generic 3 element tuple that is represented by 
 * single precision floating point x,y,z coordinates. 
 *
 * Since this is just a subset of the real javax.vecmath.Tuple3f, 
 * serialized instances of this class probably won't be compatible
 * with serialized instances of the "real" Tuple3f class.
 */
public class Tuple3f implements java.io.Serializable
	{
	public float x, y, z;
	
/**
 * Constructs and initializes a Tuple3f to (0,0,0).
 */
public Tuple3f() 
	{	
	}
/**
 * Constructs and initializes a Tuple3f from the specified xyz
 *   coordinates.
 * @param nx the x coordinate
 * @param ny the y coordinate 
 * @param nz the z coordinate
 */
public Tuple3f(float nx, float ny, float nz) 
	{
	x = nx;
	y = ny;
	z = nz;
	}
/**
 * Constructs and initializes a Tuple3f from the specified Tuple3f.
 * @param t1 the Tuple3d containing the initialization x y z data 
 */
public Tuple3f(Tuple3f t1) 
	{
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
/**
 * Sets each component of this tuple to its absolute value. 
 */
public void absolute() 
	{
	x = Math.abs(x);
	y = Math.abs(y);
	z = Math.abs(z);
	}
/**
 * Sets each component of the tuple parameter to its
 *   absolute value and places the modified values into this tuple. 
 */
public void absolute(Tuple3f t) 
	{
	x = Math.abs(t.x);
	y = Math.abs(t.y);
	z = Math.abs(t.z);
	}
/**
 * Sets the value of this tuple to the vector sum of itself
 *  and tuple t1. 
 */
public void add(Tuple3f t1) 
	{
	x += t1.x;
	y += t1.y;
	z += t1.z;
	}
/**
 * Sets the value of this tuple to the vector sum of 
 * tuple t1 and t2. 
 */
public void add(Tuple3f t1, Tuple3f t2) 
	{
	x = t1.x + t2.x;
	y = t1.y + t2.y;
	z = t1.z + t2.z;
	}
/**
 * Returns true if all of the data members of Tuple3f t are
 *    equal to the corresponding data members in this Tuple3f.  
 */
public boolean equals(Tuple3f t1) 
	{
	return (t1 != null) && (x == t1.x) && (y == t1.y) && (z == t1.z);
	}
/**
 * Gets the value of this tuple and copies the values into
 *  the Tuple3f method argument. 
 */
public void get(Tuple3f t) 
	{
	t.x = x;
	t.y = y;
	t.z = z;
	}
/**
 * Negate each component of the tuple. 
 */
public void negate() 
	{
	x = -x;
	y = -y;
	z = -z;
	}
/**
 * Sets the value of this tuple to the negation of tuple t.  
 */
public void negate(Tuple3f t) 
	{
	x = -t.x;
	y = -t.y;
	z = -t.z;
	}
/**
 * Sets the value of this tuple to the scalar multiplication of
 *          the scale factor with this. 
 */
public void scale(float s) 
	{
	x = s * x;
	y = s * y;
	z = s * z;
	}
/**
 * Sets the value of this tuple to the scalar multiplication of
 *  the scale factor with tuple t. 
 */
public void scale(float s, Tuple3f t) 
	{
	x = s * t.x;
	y = s * t.y;
	z = s * t.z;
	}
/**
 * Sets the value of this tuple to the scalar multiplication of
 *          itself and then adds tuple t1 (this = s*this + t1).  
 * @param s float
 * @param t1 javax.vecmath.Tuple3f
 * @param t2 javax.vecmath.Tuple3f
 */
public void scaleAdd(float s, Tuple3f t1) 
	{
	x = (s * x) + t1.x;
	y = (s * y) + t1.y;
	z = (s * z) + t1.z;
	}
/**
 * Sets the value of this tuple to the scalar multiplication of
		  tuple t1 and then adds tuple t2 (this = s*t1 + t2). 
 * @param s float
 * @param t1 javax.vecmath.Tuple3f
 * @param t2 javax.vecmath.Tuple3f
 */
public void scaleAdd(float s, Tuple3f t1, Tuple3f t2) 
	{
	x = (s * t1.x) + t2.x;
	y = (s * t1.y) + t2.y;
	z = (s * t1.z) + t2.z;
	}
/**
 * Sets the value of this tuple to the specified xyz
 *        coordinates. 
 * @param nx float
 * @param ny float
 * @param nz float
 */
public void set(float nx, float ny, float nz) 
	{
	x = nx;
	y = ny;
	z = nz;
	}
/**
 * Sets the value of this tuple to the value of tuple t1.  
 */
public void set(Tuple3f t) 
	{
	x = t.x;
	y = t.y;
	z = t.z;
	}
/**
 * Sets the value of this tuple to the vector difference of
 *          itself and tuple t1 (this = this - t1) .  
 */
public void sub(Tuple3f t1) 
	{
	x -= t1.x;
	y -= t1.y;
	z -= t1.z;
	}
/**
 * Sets the value of this tuple to the vector difference of
 *          tuples t1 and t2 (this = t1 - t2). 
 */
public void sub(Tuple3f t1, Tuple3f t2) 
	{
	x = t1.x - t2.x;
	y = t1.y - t2.y;
	z = t1.z - t2.z;
	}
/**
 * Returns a string that contains the values of this Tuple3f. The form is
 *    (x,y,z).
 * @return The string representation.
 */
public String toString() 
	{
	return "(" + x + "," + y + "," + z + ")";
	}
}