
package q2java;

import java.io.*;
import java.util.*;

/**
 * A subclass of java.io.OutputStream that writes data to the Quake2 
 * console.  Useful for redirecting the System.out and System.err 
 * PrintStreams so that when you calling the printStackTrace() 
 * method on an exception, the output shows up on the Quake2 console.
 * 
 * @author Barry Pederson 
 */

class ConsoleOutputStream extends java.io.OutputStream 
	{
	private static boolean gIsRedirected;
	
/**
 * A private constructor...the DLL will cause one 
 * instance to be created, and we don't want others.
 */
private ConsoleOutputStream() 
	{
	}	
/**
 * Called by the DLL to redirect the System.out and System.err 
 * printstreams to the Quake2 Console.
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

public native void write(byte[] b, int off, int len) throws IOException;


public native void write(int b) throws java.io.IOException;

}