
package javax.vecmath;

/*
   Copyright (C) Kenji Hiranabe 1997,1998
   This program is free software.

   This class was implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification version 1.1
   by Sun Microsystems.

   This program is provided AS IS, with NO WARRANTY.
*/
/**
 * Indicates that inverse of a matrix cannot be computed.
 *
 * @version specification 1.1, implementation $Revision: 1.2 $, $Date: 1998/04/09 08:18:15 $
 * @author Kenji hiranabe
 */
public class SingularMatrixException extends RuntimeException {

	/**
	  * Creates the exception object with default values.
	  */
	public SingularMatrixException() {
	}
	/**
	  * Creates the exception object that outputs a message.
	  * @param str the message string to output
	  */
	public SingularMatrixException(String str) {
		super(str);
	}
}