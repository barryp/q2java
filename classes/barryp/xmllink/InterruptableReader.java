package barryp.xmllink;

import java.io.*;

/**
 * A FilterReader than can handle being interrupted.
 */
public class InterruptableReader extends FilterReader 
	{
	private boolean fShutdown;
	
/**
 * Attach to the next Reader in the chain.
 * @param in java.io.Reader
 */
protected InterruptableReader(java.io.Reader in) 
	{
	super(in);
	}
/**
 * Override the Reader.read() method to handle interruptions
 *  from below, and ignore them if we're not flagged as shutting down.
 * @return int
 */
public int read() throws IOException
	{
	while (!fShutdown)
		{
		try
			{
			return in.read();		
			}
		catch (InterruptedIOException iie)
			{
			// do nothing..the loop will check if we should keep running.
			}
		}
		
	return -1;
	}
/**
 * Override the Reader.read() method to handle interruptions
 *  from below, and ignore them if we're not flagged as shutting down.
 * @return int
 * @param cbuf char[]
 * @param off int
 * @param len int
 */
public int read(char[] cbuf, int off, int len) throws IOException
	{
	while (!fShutdown)
		{
		try
			{
			return in.read(cbuf, off, len);		
			}
		catch (InterruptedIOException iie)
			{
			// do nothing..the loop will check if we should keep running.
			}
		}
		
	return -1;
	}
/**
 * Called to signal that this reader should shutdown.
 */
void shutdown() 
	{
	fShutdown = true;
	}
}