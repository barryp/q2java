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
  * A three byte vector used for colors.
  * @version specification 1.1, implementation $Revision: 1.6 $, $Date: 1998/10/14 00:49:10 $
  * @author Kenji hiranabe
  */
public class Color3b extends Tuple3b implements Serializable {
	/**
	  * Constructs and initializes a Color3b to (0,0,0).
	  */
	public Color3b() {
	// super(); called implicitly
	}
	/**
	  * Constructs and initializes a Color3b from input array of length 3.
	  * @param t the array of length 3 containing c1 c2 c3 in order
	  */
	public Color3b(byte c[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	super(c);
	}
/*
 * $Log: Color3b.java,v $
 * Revision 1.6  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
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
	  * Constructs and initializes a Color3b from the specified three values.
	  * @param c1 the first value
	  * @param c2 the second value
	  * @param c3 the third value
	  */
	public Color3b(byte c1, byte c2, byte c3) {
	super(c1, c2, c3);
	}
	/**
	  * Constructs and initializes a Color3b from the specified Color3b.
	  * @param c the Color3b containing the initialization x y z data
	  */
	public Color3b(Color3b c1) {
	super(c1);
	}
	/**
	  * Constructs and initializes a Color3b from the specified Tuple3b.
	  * @param t1 the Tuple3b containing the initialization x y z data
	  */
	public Color3b(Tuple3b t1) {
	super(t1);
	}
}