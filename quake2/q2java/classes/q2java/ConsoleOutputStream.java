
package q2java;

/**
 * A subclass of java.io.OutputStream that writes data to the Quake2 
 * console.  Useful for calling the printStackTrace() method on exceptions.
 * 
 * @author Barry Pederson 
 */

import java.io.*;

class ConsoleOutputStream extends java.io.OutputStream 
	{
	private static boolean gIsRedirected;
	
/**
 * Redirects the System.out and System.err printstreams to 
 * the Quake2 Console.
 */
private static void setConsole( ) 
	{
	if (!gIsRedirected)
		{
		PrintStream ps = new PrintStream(new ConsoleOutputStream());
		System.setOut(ps);
		System.setErr(ps);
		gIsRedirected = true;
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