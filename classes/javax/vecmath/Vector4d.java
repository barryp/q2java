
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
  * A 4 element vector that is represented by double precision floating point
  * x,y,z,w coordinates. 
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class Vector4d extends Tuple4d implements Serializable {

	/**
	  * Constructs and initializes a Vector4d to (0,0,0,0).
	  */
	public Vector4d() {
	// super(); called implicitly
	}
	/**
	  * Constructs and initializes a Vector4d from the specified array of length 4.
	  * @param v the array of length 4 containing xyzw in order
	  */
	public Vector4d(double v[]) {
	super(v);
	}
/*
 * $Log: Vector4d.java,v $
 * Revision 1.5  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


	/**
	  * Constructs and initializes a Vector4d from the specified xyzw coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  * @param w the w coordinate
	  */
	public Vector4d(double x, double y, double z, double w) {
	super(x, y, z, w);
	}
	/**
	  * Constructs and initializes a Vector4d from the specified Tuple4d.
	  * @param t1 the Tuple4d containing the initialization x y z w data
	  */
	public Vector4d(Tuple4d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Vector4d from the specified Tuple4f.
	  * @param t1 the Tuple4f containing the initialization x y z w data
	  */
	public Vector4d(Tuple4f t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Vector4d from the specified Vector4d.
	  * @param v1 the Vector4d containing the initialization x y z w data
	  */
	public Vector4d(Vector4d v1) {
	super(v1);
	}
	/**
	  * Constructs and initializes a Vector4d from the specified Vector4d.
	  * @param v1 the Vector4d containing the initialization x y z w data
	  */
	public Vector4d(Vector4f v1) {
	super(v1);
	}
	/**
	  * Returns the (4-space) angle in radians between this vector and
	  * the vector parameter; the return value is constrained to the
	  * range [0,PI].
	  * @param v1  the other vector
	  * @return the angle in radians in the range [0,PI]
	  */
	public final double angle(Vector4d v1) {
	// zero div may occur.
	double d = dot(v1);
	double v1_length = v1.length();
	double v_length = length();

	// numerically, domain error may occur
	return (double)Math.acos(d/v1_length/v_length);
	}
	/**
	  * Computes the dot product of the this vector and vector v1.
	  * @param  v1 the other vector
	  * @return the dot product of this vector and vector v1
	  */
	public final double dot(Vector4d v1) {
	return x*v1.x + y*v1.y + z*v1.z + w*v1.w;
	}
	/**
	  * Returns the length of this vector.
	  * @return the length of this vector
	  */
	  public final double length() {
	  return Math.sqrt(lengthSquared());
	  }  
	/**
	  * Returns the squared length of this vector.
	  * @return the squared length of this vector
	  */
	public final double lengthSquared() {
	return x*x + y*y + z*z + w*w;
	}
	/**
	  * Normalizes this vector in place.
	  */
	public final void normalize() {
	double d = length();

	// zero-div may occur.
	x /= d;
	y /= d;
	z /= d;
	w /= d;
	}
	/**
	  * Sets the value of this vector to the normalization of vector v1.
	  * @param v1 the un-normalized vector
	  */
	public final void normalize(Vector4d v1) {
	set(v1);
	normalize();
	}
}