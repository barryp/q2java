
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
  * A three byte tuple.
  * @version specification 1.1, implementation $Revision: 1.4 $, $Date: 1998/04/09 08:18:15 $
  * @author Kenji hiranabe
  */
public abstract class Tuple3b implements Serializable {
/*
 * $Log: Tuple3b.java,v $
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
	  * Constructs and initializes a Tuple3b to (0,0,0).
	  */
	public Tuple3b() {
	x = 0;
	y = 0;
	z = 0;
	}
	/**
	  * 
	  * Constructs and initializes a Tuple3b from input array of length 3.
	  * @param t the array of length 3 containing b1 b2 b3 in order
	  */
	public Tuple3b(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	this.x = t[0];
	this.y = t[1];
	this.z = t[2];
	}
	/**
	  * Constructs and initializes a Tuple3b from the specified three values.
	  * @param b1 the first value
	  * @param b2 the second value
	  * @param b3 the third value
	  */
	public Tuple3b(byte b1, byte b2, byte b3) {
	this.x = b1;
	this.y = b2;
	this.z = b3;
	}
	/**
	  * Constructs and initializes a Tuple3b from the specified Tuple3b.
	  * @param t1 the Tuple3b containing the initialization x y z data
	  */
	public Tuple3b(Tuple3b t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Returns true if all of the data members of Tuple3b t1 are equal to the corresponding
	  * data members in this
	  * @param t1 the vector with which the comparison is made.
	  */
	public boolean equals(Tuple3b t1) {
	return x == t1.x && y == t1.y && z == t1.z;
	}
	/**
	  * Places the value of the x,y,z components of this Tuple3b into the array of length 3.
	  * @param t array of length 3 into which the component values are copied
	  */
	public final void get(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	t[0] = x;
	t[1] = y;
	t[2] = z;
	}
	/**
	  * Places the value of the x,y,z components of this tuple into the tuple t1.
	  * @param t the tuple into which the values are placed
	  */
	public final void get(Tuple3b t) {
	t.x = x;
	t.y = y;
	t.z = z;
	}
	/**
	  * Returns a hash number based on the data values in this object. 
	  * Two different Tuple3b objects with identical data  values
	  * (ie, returns true for equals(Tuple3b) ) will return the same hash number.
	  * Two vectors with different data members may return the same hash value,
	  * although this is not likely.
	  */
	  public int hashCode() {
	  return  x | (y << 8) | (z << 16);
	  }  
	/**
	  * Sets the value of the data members of this tuple to the value of the argument tuple t1.
	  * @param t array of length 3 which is the source for the memberwise copy
	  */
	public final void set(byte t[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	x = t[0];
	y = t[1];
	z = t[2];
	}
	/**
	  * Sets the value of the data members of this tuple to the value of the argument tuple t1.
	  * @param t1 the source tuple for the memberwise copy
	  */
	public final void set(Tuple3b t1) {
	x = t1.x;
	y = t1.y;
	z = t1.z;
	}
	/**
	  * Returns a string that contains the values of this Tuple3b. The form is (x,y,z).
	  * @return the String representation
	  */
	public String toString() {
	    return "(" + x + ", " + y + ", " + z +")";
	}
}