
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
  * A 3 element color represented by single precision floating point x,y,z
  * coordinates. Color components should be in the range of zero to one.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class Color3f extends Tuple3f implements Serializable {

	/**
	  * Constructs and initializes a Color3f to (0,0,0).
	  */
	public Color3f() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Color3f from input array of length 3.
	  * @param c the array of length 3 containing xyz in order
	  */
	public Color3f(float c[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	super(c);
	}
/*
 * $Log: Color3f.java,v $
 * Revision 1.5  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:04:31  hiranabe
 * *** empty log message ***
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */

	/**
	  * Constructs and initializes a Color3f from the specified xyz
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  */
	public Color3f(float x, float y, float z) {
	super(x, y, z);
	}
	/**
	  * Constructs and initializes a Color3f from the specified Color3f.
	  * @param c the Color3f containing the initialization x y z data
	  */
	public Color3f(Color3f c1) {
	super(c1);
	}
	/**
	  * Constructs and initializes a Color3f from the specified Tuple3d.
	  * @param t1 the Tuple3d containing the initialization x y z data
	  */
	public Color3f(Tuple3d t1) {
	super(t1);
	}
	/**
	  * Constructs and initializes a Color3f from the specified Tuple3f.
	  * @param t1 the Tuple3f containing the initialization x y z data
	  */
	public Color3f(Tuple3f t1) {
	super(t1);
	}
}