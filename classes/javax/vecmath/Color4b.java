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
  * A four byte colors  (mostly used for colors with alpha).
  * @version specification 1.1, implementation $Revision: 1.6 $, $Date: 1998/10/14 00:49:10 $
  * @author Kenji hiranabe
  */
public class Color4b extends Tuple4b implements Serializable {
	/**
	  * Constructs and initializes a Color4b to (0,0,0,0).
	  */
	public Color4b() {
	// super(); called implicitly.
	}
	/**
	  * Constructs and initializes a Color4b from input array of length 4.
	  * @param c the array of length 4 containing c1 c2 c3 c4 in order
	  */
	public Color4b(byte c[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	super(c);
	}
/*
 * $Log: Color4b.java,v $
 * Revision 1.6  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
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
	  * Constructs and initializes a Color4b from the specified four values.
	  * @param c1 the first value
	  * @param c2 the second value
	  * @param c3 the third value
	  * @param c4 the fourth value
	  */
	public Color4b(byte c1, byte c2, byte c3, byte c4) {
	super(c1, c2, c3, c4);
	}
	/**
	  * Constructs and initializes a Color4b from the specified Color4b.
	  * @param c the Color4b containing the initialization x y z w data
	  */
	public Color4b(Color4b c1) {
	super(c1);
	}
	/**
	  * Constructs and initializes a Color4b from the specified Tuple4b.
	  * @param t1 the Tuple4b containing the initialization x y z w data
	  */
	public Color4b(Tuple4b t1) {
	super(t1);
	}
}