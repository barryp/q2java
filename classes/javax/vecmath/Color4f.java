package javax.vecmath;

/*
   Copyright (C) 1997,1998,1999
   Kenji Hiranabe, Eiwa System Management, Inc.

   This program is free software.
   Implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification version 1.1 final
   by Sun Microsystems.

   Permission to use, copy, modify, distribute and sell this software
   and its documentation for any purpose is hereby granted without fee,
   provided that the above copyright notice appear in all copies and
   that both that copyright notice and this permission notice appear
   in supporting documentation. Kenji Hiranabe and Eiwa System Management,Inc.
   makes no representations about the suitability of this software for any
   purpose.  It is provided "AS IS" with NO WARRANTY.
*/
import java.io.Serializable;

/**
  * A 4 element color represented by single precision floating point x,y,z,w
  * coordinates. Color and alpha components should be in the range of zero to one.
  * @version specification 1.1, implementation 1.0 $Revision: 1.1 $, $Date: 2000/01/02 02:32:58 $
  * @author Kenji hiranabe
  */
public class Color4f extends Tuple4f implements Serializable {
	/**
	  * Constructs and initializes a Color4f to (0,0,0,0).
	  */
	public Color4f() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Color4f from input array of length 4.
	  * @param c the array of length 4 containing xyzw in order
	  */
	public Color4f(float c[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	super(c);
	}
/*
 * $Log: Color4f.java,v $
 * Revision 1.1  2000/01/02 02:32:58  barryp
 * Initial revision
 *
 * Revision 1.6  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.6  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.5  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.4  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
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
	  * Constructs and initializes a Color4f from the specified xyzw
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  * @param w the w coordinate
	  */
	public Color4f(float x, float y, float z, float w) {
	super(x, y, z, w);
	}
	/**
	  * Constructs and initializes a Color4f from the specified Color4f.
	  * @param c the Color4f containing the initialization x y z w data
	  */
	public Color4f(Color4f c1) {
	super(c1);
	}
	/**
	  * Constructs and initializes a Color4f from the specified Tuple4d.
	  * @param t1 the Tuple4d containing the initialization x y z w data
	  */
	public Color4f(Tuple4d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Color4f from the specified Tuple4f.
	  * @param t1 the Tuple4f containing the initialization x y z w data
	  */
	public Color4f(Tuple4f t1) {
	super(t1);
	}
}