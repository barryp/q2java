package javax.vecmath;

/*
   Copyright (C) 1997,1998
   Kenji Hiranabe

   This program is free software.
   Implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification version 1.1
   by Sun Microsystems.

   This program is provided AS IS, with NO WARRANTY.
*/
import java.io.Serializable;

/**
  * A 2 element point that is represented by double precision
  * doubleing point x,y coordinates.
  * @version specification 1.1, implementation $Revision: 1.2 $, $Date: 1998/10/14 00:49:10 $
  * @author Kenji hiranabe
  */
public class Point2d extends Tuple2d implements Serializable {
	/**
	  * Constructs and initializes a Point2d to (0,0).
	  */
	public Point2d() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Point2d from the specified array.
	  * @param p the array of length 2 containing xy in order
	  */
	public Point2d(double p[]) {
	super(p);
	}
/*
 * $Log: Point2d.java,v $
# Revision 1.2  1998/10/14  00:49:10  hiranabe
# API1.1 Beta02
#
# Revision 1.1  1998/07/27  04:28:13  hiranabe
# API1.1Alpha01 ->API1.1Alpha03
#
 *
 */

	/**
	  * Constructs and initializes a Point2d from the specified xy coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  */
	public Point2d(double x, double y) {
	super(x, y);
	}
	/**
	  * Constructs and initializes a Point2d from the specified Point2d.
	  * @param p1 the Point2d containing the initialization x y data
	  */
	public Point2d(Point2d p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point2d from the specified Point2f.
	  * @param p1 the Point2f containing the initialization x y data
	  */
	public Point2d(Point2f p1) {
	super(p1);
	}
	/**
	  * Constructs and initializes a Point2d from the specified Tuple2d.
	  * @param t1 the Tuple2d containing the initialization x y data
	  */
	public Point2d(Tuple2d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Point2d from the specified Tuple2f.
	  * @param t1 the Tuple2f containing the initialization x y data
	  */
	public Point2d(Tuple2f t1) {
	super(t1);
	}
	/**
	  * Computes the distance between this point and point p1.
	  * @param p1 the other point
	  */
	public final double distance(Point2d p1) {
	return Math.sqrt(distanceSquared(p1));
	}
	/**
	  * Computes the L-1 (Manhattan) distance between this point and point p1.
	  * The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
	  * @param p1 the other point
	  */
	public final double distanceL1(Point2d p1) {
	return Math.abs(x-p1.x) + Math.abs(y-p1.y);
	}
	/**
	  * Computes the L-infinite distance between this point and point p1.
	  * The L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
	  * @param p1 the other point
	  */
	public final double distanceLinf(Point2d p1) {
	return Math.max(Math.abs(x-p1.x), Math.abs(y-p1.y));
	}
	/**
	  * Computes the square of the distance between this point and point p1.
	  * @param  p1 the other point
	  */
	public final double distanceSquared(Point2d p1) {
	double dx = x - p1.x;
	double dy = x - p1.y;
	return dx*dx + dy*dy;
	}
}