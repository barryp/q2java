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
  * A 2 element texture coordinate that is represented by single precision
  * floating point x,y coordinates.
  * @version specification 1.1, implementation $Revision: 1.5 $, $Date: 1998/04/10 04:52:14 $
  * @author Kenji hiranabe
  */
public class TexCoord2f extends Tuple2f implements Serializable {
	/**
	  * Constructs and initializes a TexCoord2f to (0,0).
	  */
	public TexCoord2f() {
	// super(); called implicitly
	}
	/**
	  * Constructs and initializes a TexCoord2f from the specified array.
	  * @param p the array of length 2 containing xy in order
	  */
	public TexCoord2f(float v[]) {
	super(v);
	}
/*
 * $Log: TexCoord2f.java,v $
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
	  * Constructs and initializes a TexCoord2f from the specified xy coordinates.
	  * @param x the x coordinate
	  * @param y the y coordinate
	  */
	public TexCoord2f(float x, float y) {
	super(x, y);
	}
	/**
	  * Constructs and initializes a TexCoord2f from the specified TexCoord2f.
	  * @param v1 the TexCoord2f containing the initialization x y data
	  */
	public TexCoord2f(TexCoord2f v1) {
	super(v1);
	}
	/**
	  * Constructs and initializes a TexCoord2f from the specified Tuple2f.
	  * @param t1 the Tuple2f containing the initialization x y data
	  */
	public TexCoord2f(Tuple2f t1) {
	super(t1);
	}
}