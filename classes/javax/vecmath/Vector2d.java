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
  * A 2 element vector that is represented by single precision
  * floating point x,y coordinates.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class Vector2d extends Tuple2d implements Serializable {
	/**
	  * Constructs and initializes a Vector2d to (0,0).
	  */
	public Vector2d() {
	super();
	}
	/**
	  * Constructs and initializes a Vector2d from the specified array.
	  * @param v the array of length 2 containing xy in order
	  */
	public Vector2d(double v[]) {
	super(v);
	}
/*
 * $Log: Vector2d.java,v $
 *
 */

	/**
	  * Constructs and initializes a Vector2d from the specified xy coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  */
	public Vector2d(double x, double y) {
	super(x, y);
	}
	/**
	  * Constructs and initializes a Vector2d from the specified Tuple2d.
	  * @param t1 the Tuple2d containing the initialization x y data
	  */
	public Vector2d(Tuple2d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Vector2d from the specified Tuple2f.
	  * @param t1 the Tuple2f containing the initialization x y data
	  */
	public Vector2d(Tuple2f t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Vector2d from the specified Vector2d.
	  * @param v1 the Vector2d containing the initialization x y data
	  */
	public Vector2d(Vector2d v1) {
	super(v1);
	}
	/**
	  * Constructs and initializes a Vector2d from the specified Vector2f.
	  * @param v1 the Vector2f containing the initialization x y data
	  */
	public Vector2d(Vector2f v1) {
	super(v1);
	}
	/**
	  * Returns the angle in radians between this vector and
	  * the vector parameter; the return value is constrained to the
	  * range [0,PI].
	  * @param v1  the other vector
	  * @return the angle in radians in the range [0,PI]
	  */
	public final double angle(Vector2d v1) {
	// stabler than acos
	return Math.abs(Math.atan2(x*v1.y - y*v1.x , dot(v1)));
	}
	/**
	  * Computes the dot product of the this vector and vector v1.
	  * @param  v1 the other vector
	  */
	public final double dot(Vector2d v1) {
	return x*v1.x + y*v1.y;
	}
	/**
	  * Returns the length of this vector.
	  * @return the length of this vector
	  */
	public final double length() {
	return Math.sqrt(x*x + y*y);
	}
	/**
	  * Returns the squared length of this vector.
	  * @return the squared length of this vector
	  */
	public final double lengthSquared() {
	return x*x + y*y;
	}
	/**
	  * Normalizes this vector in place.
	  */
	public final void normalize() {
	double d = length();

	// zero-div may occur.
	x /= d;
	y /= d;
	}
	/**
	  * Sets the value of this vector to the normalization of vector v1.
	  * @param v1 the un-normalized vector
	  */
	public final void normalize(Vector2d v1) {
	set(v1);
	normalize();
	}
}