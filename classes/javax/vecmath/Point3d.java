
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
  * A 3 element point that is represented by double precision
  * floating point x,y,z coordinates.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class Point3d extends Tuple3d implements Serializable {

	/**
	  * Constructs and initializes a Point3d to (0,0,0).
	  */
	public Point3d() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Point3d from the specified array.
	  * @param p the array of length 3 containing xyz in order
	  */
	public Point3d(double p[]) {
	super(p);
	}
/*
 * $Log: Point3d.java,v $
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
	  * Constructs and initializes a Point3d from the specified xyz coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  */
	public Point3d(double x, double y, double z) {
	super(x, y, z);
	}
	/**
	  * Constructs and initializes a Point3d from the specified Point3d.
	  * @param p1 the Point3d containing the initialization x y z data
	  */
	public Point3d(Point3d p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point3d from the specified Point3f.
	  * @param p1 the Point3d containing the initialization x y z data
	  */
	public Point3d(Point3f p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point3d from the specified Tuple3d.
	  * @param t1 the Tuple3d containing the initialization x y z data
	  */
	public Point3d(Tuple3d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Point3d from the specified Tuple3f.
	  * @param t1 the Tuple3f containing the initialization x y z data
	  */
	public Point3d(Tuple3f t1) {
	super(t1);
	}
	/**
	  * Returns the distance between this point and point p1.
	  * @param p1 the other point
	  * @return the distance between these two points as a float
	  */
	public final double distance(Point3d p1) {
	return Math.sqrt(distanceSquared(p1));
	}
	/**
	  * Computes the L-1 (Manhattan) distance between this point and point p1.
	  * The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
	  * @param p1 the other point
	  */
	public final float distanceL1(Point3d p1) {
	// why returns float ?
	return (float)(Math.abs(x-p1.x) + Math.abs(y-p1.y) + Math.abs(z-p1.z));
	}
	/**
	  * Computes the L-infinite distance between this point and point p1.
	  * The L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
	  * @param p1 the other point
	  */
	public final float distanceLinf(Point3d p1) {
	// why returns float ?
	return (float)Math.max(Math.max(Math.abs(x-p1.x), Math.abs(y-p1.y)), Math.abs(z-p1.z));
	}
	/**
	  * Computes the square of the distance between this point and point p1.
	  * @param  p1 the other point
	  * @return the square of distance between these two points as a float
	  */
	public final double distanceSquared(Point3d p1) {
	double dx = x - p1.x;
	double dy = y - p1.y;
	double dz = z - p1.z;
	return dx*dx + dy*dy + dz*dz;
	}
	/**
	  * Multiplies each of the x,y,z components of the Point4f parameter
	  * by 1/w and places the projected values into this point.
	  * @param p1 the source Point4d, which is not modified
	  */
	 public final void project(Point4d p1) {
	 // zero div may occur.
	 x = p1.x/p1.w;
	 y = p1.y/p1.w;
	 z = p1.z/p1.w;
	 } 
}