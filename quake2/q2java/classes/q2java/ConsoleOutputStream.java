
package q2java;

import java.io.*;
import java.util.*;

/**
 * A subclass of java.io.OutputStream that writes data to the Quake2 
 * console.  Useful for redirecting the System.out and System.err 
 * PrintStreams so that when you calling the printStackTrace() 
 * method on an exception, the output shows up on the Quake2 console
 * and whatever other objects that implement the ConsoleListener
 * interface and have been registered as listeners.
 * 
 * @author Barry Pederson 
 */

public class ConsoleOutputStream extends java.io.OutputStream 
	{
	private static boolean gIsRedirected;
	private static Vector gListeners = new Vector();
	private static byte[] gSingleBuffer = new byte[1];
	
/**
 * A private constructor...the DLL will cause one 
 * instance to be created, and we don't want others.
 */
private ConsoleOutputStream ( ) {
}
/**
 * Register a ConsoleListener.
 * @param cl q2java.ConsoleListener
 */
public static void addConsoleListener(ConsoleListener cl) 
	{
	if (!gListeners.contains(cl))
		gListeners.addElement(cl);
	}
/**
 * Distribute the output bytes to the interested listeners.
 * @param b byte[]
 * @param offset int
 * @param length int
 */
private static void distribute(byte[] b, int offset, int length) 
	{
	Enumeration enum = gListeners.elements();
	while (enum.hasMoreElements())
		((ConsoleListener) enum.nextElement()).consoleOutput(b, offset, length);
	}
/**
 * Remove a ConsoleListener.
 * @param cl q2java.ConsoleListener
 */
public static void removeConsoleListener(ConsoleListener cl) 
	{
	gListeners.removeElement(cl);
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
/**
 * Write part of an array of bytes to the Quake2 console and any listeners.
 * @param b byte[]
 * @param offset int
 * @param length int
 */
public void write(byte[] b, int offset, int length) throws IOException
	{
	write0(b, offset, length);
	distribute(b, offset, length);
	}
/**
 * Write a character to the Quake2 console and any listeners.
 * @param n int
 */
public void write(int b) throws IOException
	{
	write0(b);
	gSingleBuffer[0] = (byte)(b & 0x00ff);
	distribute(gSingleBuffer, 0, 1);
	}

private native void write0(byte[] b, int off, int len) throws IOException;


private native void write0(int b) throws java.io.IOException;

}