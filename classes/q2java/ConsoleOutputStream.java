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
	
ConsoleOutputStream() 
	{
	}	
/**
 * Write a bunch of bytes, stripping out carriage-returns, 
 * and expanding tabs.
 */
public void write(byte[] ba, int offset, int len) throws IOException
	{
	// figure out how many chars are in the string, after
	// stripping CRs and expanding TABs
	int count = len;
	for (int i = 0; i < len; i++)
		{
		switch ((char)(ba[i + offset]))
			{
			case '\r':
				count--;
				break;
				
			case '\t':
				count += 3;
				break;
			}
		}

	// Create a char array based on the byte array
	char ca[] = new char[count];
	count = 0;
	for (int i = 0; i < len; i++)
		{
		char ch = (char)(ba[i + offset]);
		switch (ch)
			{
			case '\r':
				break;
				
			case '\t':
				ca[count++] = ' ';
				ca[count++] = ' ';
				ca[count++] = ' ';
				ca[count++] = ' ';
				break;
				
			default:
				ca[count++] = ch;
			}
		}

	// create a string and feed it to the Engine		
	Engine.javaConsoleOutput(new String(ca));
	}
/**
 * Write a byte, ignoring carriage-returns, 
 * and expanding tabs.
 */
public void write(int b) throws java.io.IOException
	{
	char ch = (char) b;

	switch (ch)
		{
		case '\r':
			break;

		case '\t':
			Engine.javaConsoleOutput("    ");
			break;

		default:			
			Engine.javaConsoleOutput(String.valueOf(ch));		
		}
	}
}