
package q2java;

/**
 * A subclass of java.io.OutputStream that writes data to the Quake2 
 * console.  Useful for calling the printStackTrace() method on exceptions.
 * 
 */

import java.io.*;

class ConsoleOutputStream extends java.io.OutputStream 
	{
	private static boolean fIsRedirected;
	
/**
 * Redirects the System.out and System.err printstreams to 
 * the Quake2 Console.
 */
private static void setConsole( ) 
	{
	if (!fIsRedirected)
		{
		PrintStream ps = new PrintStream(new ConsoleOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		fIsRedirected = true;
		}
	}

/**
 * This method was created by a SmartGuide.
 * @param b byte[]
 * @param off int
 * @param len int
 */
public native void write(byte[] b, int off, int len) throws IOException;


/**
 * write method comment.
 */
public native void write(int b) throws java.io.IOException;

}