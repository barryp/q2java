
package javax.vecmath;

/*
   Copyright (C) Kenji Hiranabe 1997,1998
   This program is free software.

   This class was implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification version 1.1
   by Sun Microsystems.

   This program is provided AS IS, with NO WARRANTY.
*/
import java.io.Serializable;

/**
  * A generic 3 element tuple that is represented by
  * double precision floating point x,y and z coordinates.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/09 08:18:15 $
  * @author Kenji hiranabe
  */
public abstract class Tuple3d implements Serializable {
/*
 * $Log: Tuple3d.java,v $
 * Revision 1.5  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.5  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.4  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.3  1998/01/06  00:22:00  hiranabe
 * typo of scale method
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


	/**
	  * The x coordinate.
	  */
	public double x;

	/**
	  * The y coordinate.
	  */
	public double y;

	/**
	  * The z coordinate.
	  */
	public double z;


	/**
	  * Constructs and initializes a Tuple3d to (0,0,0).
	  */
	public Tuple3d() {
	x = 0.0f;
	y = 0.0f;
	z = 0.0f;
	}
	/**
	  * Constructs and initializes a Tuple3d from the specified array.
	  * @param t the array of length 3 containing xyz in order
	  */
	public Tuple3d(double t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	}
	/**
	  * Constructs and initializes a Tuple3d from the specified xyz coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  */
	public Tuple3d(double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
	}
	/**
	  * Constructs and initializes a Tuple3d from the specified Tuple3d.
	  * @param t1 the Tuple3d containing the initialization x y z data
	  */
	public Tuple3d(Tuple3d t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Constructs and initializes a Tuple3d from the specified Tuple3f.
	  * @param t1 the Tuple3f containing the initialization x y z data
	  */
	public Tuple3d(Tuple3f t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Sets each component of this tuple to its absolute value.
	  */
	public final void absolute() {
	if (x < 0.0)
	    x = -x;
	if (y < 0.0)
	    y = -y;
	if (z < 0.0)
	    z = -z;
	}
	/**
	  * Sets each component of the tuple parameter to its absolute value and
	  * places the modified values into this tuple.
	  * @param t the source tuple, which will not be modified
	  */
	public final void absolute(Tuple3d t) {
	set(t);
	absolute();
	}
	/**
	  * Sets the value of this tuple to the vector sum of itself and tuple t1.
	  * @param t1  the other tuple
	  */
	public final void add(Tuple3d t1) {
	x += t1.x;
	y += t1.y;
	z += t1.z;
	}
	/**
	  * Sets the value of this tuple to the vector sum of tuples t1 and t2.
	  * @param t1 the first tuple
	  * @param t2 the second tuple
	  */
	public final void add(Tuple3d t1, Tuple3d t2) {
	x = t1.x + t2.x;
	y = t1.y + t2.y;
	z = t1.z + t2.z;
	}
	/**
	  * Clamps this tuple to the range [low, high].
	  * @param min the lowest value in this tuple after clamping
	  * @param max the highest value in this tuple after clamping
	  */
	public final void clamp(float min, float max) {
	// why float ?
	clampMin(min);
	clampMax(max);
	}
	/**
	  * Clamps the tuple parameter to the range [low, high] and places the values
	  * into this tuple.
	  * @param min the lowest value in the tuple after clamping
	  * @param max the highest value in the tuple after clamping
	  * @param t the source tuple, which will not be modified
	  */
	public final void clamp(float min, float max, Tuple3d t) {
	// why float ?
	set(t);
	clamp(min, max);
	}
	/**
	  * Clamps the maximum value of this tuple to the max parameter.
	  * @param max the highest value in the tuple after clamping
	  */
	public final void clampMax(float max) {
	// why float ?
	if (x > max)
	    x = max;
	if (y > max)
	    y = max;
	if (z > max)
	    z = max;
	}
	/**
	  * Clamps the maximum value of the tuple parameter to the max parameter and
	  * places the values into this tuple.
	  * @param max the highest value in the tuple after clamping
	  * @param t the source tuple, which will not be modified
	  */
	public final void clampMax(float max, Tuple3d t) {
	// why float ?
	set(t);
	clampMax(max);
	}
	/**
	  * Clamps the minimum value of this tuple to the min parameter.
	  * @param min the lowest value in this tuple after clamping
	  */
	public final void clampMin(float min) {
	// why float ?
	if (x < min)
	    x = min;
	if (y < min)
	    y = min;
	if (z < min)
	    z = min;
	}
	/**
	  * Clamps the minimum value of the tuple parameter to the min parameter
	  * and places the values into this tuple.
	  * @param min the lowest value in the tuple after clamping
	  * @parm t the source tuple, which will not be modified
	  */
	public final void clampMin(float min, Tuple3d t) {
	// why float ?
	set(t);
	clampMin(min);
	}
	/**
	  * Returns true if the L-infinite distance between this tuple and tuple t1 is
	  * less than or equal to the epsilon parameter, otherwise returns false. The L-infinite
	  * distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
	  * @param t1 the tuple to be compared to this tuple
	  * @param epsilon the threshold value
	  */
	public boolean epsilonEquals(Tuple3d t1, double epsilon) {
	return (Math.abs(t1.x - this.x) <= epsilon) &&
	    (Math.abs(t1.y - this.y) <= epsilon) &&
	    (Math.abs(t1.z - this.z) <= epsilon);
	}
	/**
	  * Returns true if all of the data members of Tuple3d t1 are equal to the corresponding
	  * data members in this
	  * @param t1 the vector with which the comparison is made.
	  */
	public boolean equals(Tuple3d t1) {
	return x == t1.x && y == t1.y && z == t1.z;
	}
	/**
	  * Copies the value of the elements of this tuple into the array t[]. 
	  * @param t the array that will contain the values of the vector
	  */
	public final void get(double t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	t[0] = x;
	t[1] = y;
	t[2] = z;
	}
	/**
	  * Gets the value of this tuple and copies the values into the Tuple3d.
	  * @param t Tuple3d object into which that values of this object are copied
	  */
	public final void get(Tuple3d t) {
	t.x = x;
	t.y = y;
	t.z = z;
	}
	/**
	  * Returns a hash number based on the data values in this object. 
	  * Two different Tuple3d objects with identical data  values
	  * (ie, returns true for equals(Tuple3d) ) will return the same hash number.
	  * Two vectors with different data members may return the same hash value,
	  * although this is not likely.
	  */
	  public int hashCode() {
	  long xbits = Double.doubleToLongBits(x);
	  long ybits = Double.doubleToLongBits(y);
	  long zbits = Double.doubleToLongBits(z);
	  return (int)(xbits ^ (xbits >> 32) ^
		  ybits ^ (ybits >> 32) ^
		  zbits ^ (zbits >> 32));
	  }  
	/**
	  * Linearly interpolates between this tuple and tuple t1 and places the
	  * result into this tuple: this = alpha*this + (1-alpha)*t1.
	  * @param t1 the first tuple
	  * @param alpha the alpha interpolation parameter
	  *
	  */
	public final void interpolate(Tuple3d t1, float alpha) {
	// why float ?
	x = alpha*x + (1 - alpha)*t1.x;
	y = alpha*y + (1 - alpha)*t1.y;
	z = alpha*z + (1 - alpha)*t1.z;
	}
	/**
	  * Linearly interpolates between tuples t1 and t2 and places the
	  * result into this tuple: this = alpha*t1 + (1-alpha)*t2.
	  * @param t1 the first tuple
	  * @param t2 the second tuple
	  * @param alpha the alpha interpolation parameter
	  */
	public final void interpolate(Tuple3d t1, Tuple3d t2, float alpha) {
	// why float ?
	x = alpha*t1.x + (1 - alpha)*t2.x;
	y = alpha*t1.y + (1 - alpha)*t2.y;
	z = alpha*t1.z + (1 - alpha)*t2.z;
	}
	/**
	  * Negates the value of this vector in place.
	  */
	public final void negate() {
	x = -x;
	y = -y;
	z = -z;
	}
	/**
	  * Sets the value of this tuple to the negation of tuple t1. 
	  * @param t1 the source vector
	  */
	public final void negate(Tuple3d t1) {
	x = -t1.x;
	y = -t1.y;
	z = -t1.z;
	}
	/**
	  * Sets the value of this tuple to the scalar multiplication of itself.
	  * @param s the scalar value
	  */
	public final void scale(double s) {
	x *= s;
	y *= s;
	z *= s;
	}
	/**
	  * Sets the value of this tuple to the scalar multiplication of tuple t1.
	  * @param s the scalar value
	  * @param t1 the source tuple
	  */
	public final void scale(double s, Tuple3d t1) {
	x = s*t1.x;
	y = s*t1.y;
	z = s*t1.z;
	}
	/**
	  * Sets the value of this tuple to the scalar multiplication of itself and then
	  * adds tuple t1 (this = s*this + t1).
	  * @param s the scalar value
	  * @param t1 the tuple to be added
	  */
	public final void scaleAdd(double s, Tuple3d t1) {
	x = s*x + t1.x;
	y = s*y + t1.y;
	z = s*z + t1.z;
	}
	/**
	  * Sets the value of this tuple to the scalar multiplication of tuple t1 and then
	  * adds tuple t2 (this = s*t1 + t2).
	  * @param s the scalar value
	  * @param t1 the tuple to be multipled
	  * @param t2 the tuple to be added
	  */
	public final void scaleAdd(double s, Tuple3d t1, Tuple3d t2) {
	x = s*t1.x + t2.x;
	y = s*t1.y + t2.y;
	z = s*t1.z + t2.z;
	}
	/**
	  * Sets the value of this tuple from the 3 values specified in the array.
	  * @param t the array of length 3 containing xyz in order
	  */
	public final void set(double t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	x = t[0];
	y = t[1];
	z = t[2];
	}
	/**
	  * Sets the value of this tuple to the specified xyz coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  */
	public final void set(double x, double y, double z) {
	this.x = x;
	this.y = y;
	this.z = z;
	}
	/**
	  * Sets the value of this tuple to the value of the Tuple3d argument.
	  * @param t1 the tuple to be copied
	  */
	public final void set(Tuple3d t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Sets the value of this tuple to the value of the Tuple3f argument.
	  * @param t1 the tuple to be copied
	  */
	public final void set(Tuple3f t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Sets the value of this tuple to the vector difference of itself and tuple t1 (this = this - t1).
	  * @param t1 the other tuple
	  */
	public final void sub(Tuple3d t1) {
	x -= t1.x;
	y -= t1.y;
	z -= t1.z;
	}
	/**
	  * Sets the value of this tuple to the vector difference of tuple t1 and t2 (this = t1 - t2).
	  * @param t1 the first tuple
	  * @param t2 the second tuple
	  */
	public final void sub(Tuple3d t1, Tuple3d t2) {
	x = t1.x - t2.x;
	y = t1.y - t2.y;
	z = t1.z - t2.z;
	}
	/**
	  * Returns a string that contains the values of this Tuple3d. The form is (x,y,z).
	  * @return the String representation
	  */
	public String toString() {
	    return "(" + x + ", " + y + ", " + z +")";
	}
}