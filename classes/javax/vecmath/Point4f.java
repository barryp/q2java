
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
  * A 4 element point that is represented by single precision
  * floating point x,y,z,w coordinates.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class Point4f extends Tuple4f implements Serializable {

	/**
	  * Constructs and initializes a Point4f to (0,0,0,0).
	  */
	public Point4f() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Point4f from the specified array.
	  * @param p the array of length 4 containing xyzw in order
	  */
	public Point4f(float p[]) {
	super(p);
	}
/*
 * $Log: Point4f.java,v $
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
	  * Constructs and initializes a Point4f from the specified xyzw coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  * @param w the w coordinate
	  */
	public Point4f(float x, float y, float z, float w) {
	super(x, y, z, w);
	}
	/**
	  * Constructs and initializes a Point4f from the specified Point4d.
	  * @param p1 the Point4d containing the initialization x y z w data
	  */
	public Point4f(Point4d p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point4f from the specified Point4f.
	  * @param p1 the Point4f containing the initialization x y z w data
	  */
	public Point4f(Point4f p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point4f from the specified Tuple4d.
	  * @param t1 the Tuple4d containing the initialization x y z w data
	  */
	public Point4f(Tuple4d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Point4f from the specified Tuple4f.
	  * @param t1 the Tuple4f containing the initialization x y z w data
	  */
	public Point4f(Tuple4f t1) {
	super(t1);
	}
	/**
	  * Returns the distance between this point and point p1.
	  * @param p1 the other point
	  * @return the distance between these two points
	  */
	public final float distance(Point4f p1) {
	return (float)Math.sqrt(distanceSquared(p1));
	}
	/**
	  * Computes the L-1 (Manhattan) distance between this point and point p1.
	  * The L-1 distance is equal to abs(x1-x2) + abs(y1-y2)
	  * + abs(z1-z2) + abs(w1-w2).
	  * @param p1 the other point
	  * @return L-1 distance
	  */
	public final float distanceL1(Point4f p1) {
	return Math.abs(x-p1.x) + Math.abs(y-p1.y)
	    + Math.abs(z-p1.z) + Math.abs(w-p1.w);
	}
	/**
	  * Computes the L-infinite distance between this point and point p1.
	  * The L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2), abs(w1-w2)].
	  * @param p1 the other point
	  * @return L-infinite distance
	  */
	public final float distanceLinf(Point4f p1) {
	return Math.max(Math.max(Math.abs(x-p1.x), Math.abs(y-p1.y)),
			Math.max(Math.abs(z-p1.z), Math.abs(w-p1.w)));
	}
	/**
	  * Computes the square of the distance between this point and point p1.
	  * @param  p1 the other point
	  * @return the square of distance between these two points as a float
	  */
	public final float distanceSquared(Point4f p1) {
	double dx = x - p1.x;
	double dy = y - p1.y;
	double dz = z - p1.z;
	double dw = z - p1.w;
	return (float)(dx*dx + dy*dy + dz*dz + dw*dw);
	}
	/**
	  * Multiplies each of the x,y,z components of the Point4f parameter by 1/w,
	  * places the projected values into this point, and places a 1 as the w
	  * parameter of this point.
	  * @param p1 the source Point4d, which is not modified
	  */
	 public final void project(Point4f p1) {
	 // zero div may occur.
	 x = p1.x/p1.w;
	 y = p1.y/p1.w;
	 z = p1.z/p1.w;
	 w = 1.0f;
	 } 
}