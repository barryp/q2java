
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
  * A 3 element texture coordinate that is represented by single precision
  * floating point x,y,z coordinates.
  * @version specification 1.1, implementation $Revision: 1.4 $, $Date: 1998/04/09 08:18:15 $
  * @author Kenji hiranabe
  */
public class TexCoord3f extends Tuple3f implements Serializable {

	/**
	  * Constructs and initializes a TexCoord3f to (0,0,0).
	  */
	public TexCoord3f() {
	super();
	}
	/**
	  * Constructs and initializes a TexCoord3f from the specified array.
	  * @param p the array of length 3 containing xyz in order
	  */
	public TexCoord3f(float v[]) {
	super(v);
	}
/*
 * $Log: TexCoord3f.java,v $
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
	  * Constructs and initializes a TexCoord3f from the specified xy coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  * @param z the z coordinate
	  */
	public TexCoord3f(float x, float y, float z) {
	super(x, y, z);
	}
	/**
	  * Constructs and initializes a TexCoord3f from the specified TexCoord3f.
	  * @param v1 the TexCoord3f containing the initialization x y z data
	  */
	public TexCoord3f(TexCoord3f v1) {
	super(v1);
	}
}