
package q2java;

/**
 * Interface for classes that want to register as being 
 * interested in console output.
 * 
 */
public interface ConsoleListener 
	{
	
/**
 * Method called with the console is being sent some output.
 * @param b byte[]
 * @param offset int
 * @param length int
 */
public void consoleOutput(byte[] b, int offset, int length);
}