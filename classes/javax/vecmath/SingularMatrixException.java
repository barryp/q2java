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
/**
 * Indicates that inverse of a matrix cannot be computed.
 *
 * @version specification 1.1, implementation $Revision: 1.3 $, $Date: 1998/10/14 00:49:10 $
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