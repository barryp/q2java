
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
  * A four byte tuple.
  * @version specification 1.1, implementation $Revision: 1.4 $, $Date: 1998/04/09 08:18:15 $
  * @author Kenji hiranabe
  */
public abstract class Tuple4b implements Serializable {
/*
 * $Log: Tuple4b.java,v $
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
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
	  * The first value.
	  */
	public byte x;

	/**
	  * The second value.
	  */
	public byte y;

	/**
	  * The third value.
	  */
	public byte z;

	/**
	  * The fourth value.
	  */
	public byte w;


	/**
	  * Constructs and initializes a Tuple4b to (0,0,0,0).
	  */
	public Tuple4b() {
	x = 0;
	y = 0;
	z = 0;
	w = 0;
	}
	/**
	  * 
	  * Constructs and initializes a Tuple4b from input array of length 4.
	  * @param t the array of length 4 containing b1 b2 b3 b4 in order
	  */
	public Tuple4b(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	this.w = t[3];
	}
	/**
	  * Constructs and initializes a Tuple4b from the specified three values.
	  * @param b1 the first value
	  * @param b2 the second value
	  * @param b3 the third value
	  * @param b4 the fourth value
	  */
	public Tuple4b(byte b1, byte b2, byte b3, byte b4) {
	this.x = b1;
	this.y = b2;
	this.z = b3;
	this.w = b4;
	}
	/**
	  * Constructs and initializes a Tuple4b from the specified Tuple4b.
	  * @param t1 the Tuple4b containing the initialization x y z w data
	  */
	public Tuple4b(Tuple4b t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	w = t1.w;
	}
	/**
	  * Returns true if all of the data members of Tuple4b t1 are equal to the corresponding
	  * data members in this
	  * @param t1 the vector with which the comparison is made.
	  */
	public boolean equals(Tuple4b t1) {
	return x == t1.x && y == t1.y && z == t1.z && w == t1.w;
	}
	/**
	  * Places the value of the x,y,z components of this Tuple4b into the array of length 4.
	  * @param t array of length 4 into which the component values are copied
	  */
	public final void get(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	t[0] = x;
	t[1] = y;
	t[2] = z;
	t[3] = w;
	}
	/**
	  * Places the value of the x,y,z components of this tuple into the tuple t1.
	  * @param t the tuple into which the values are placed
	  */
	public final void get(Tuple4b t) {
	t.x = x;
	t.y = y;
	t.z = z;
	t.w = w;
	}
	/**
	  * Returns a hash number based on the data values in this object. 
	  * Two different Tuple4b objects with identical data  values
	  * (ie, returns true for equals(Tuple4b) ) will return the same hash number.
	  * Two vectors with different data members may return the same hash value,
	  * although this is not likely.
	  */
	  public int hashCode() {
	  return  x | (y << 8) | (z << 16) | (w << 24);
	  }  
	/**
	  * Sets the value of the data members of this tuple to the value of the argument tuple t1.
	  * @param t array of length 4 which is the source for the memberwise copy
	  */
	public final void set(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 4
	x = t[0];
	y = t[1];
	z = t[2];
	w = t[3];
	}
	/**
	  * Sets the value of the data members of this tuple to the value of the argument tuple t1.
	  * @param t1 the source tuple for the memberwise copy
	  */
	public final void set(Tuple4b t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	w = t1.w;
	}
	/**
	  * Returns a string that contains the values of this Tuple4b. The form is (x,y,z,w).
	  * @return the String representation
	  */
	public String toString() {
	    return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}