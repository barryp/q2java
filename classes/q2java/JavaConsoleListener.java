package q2java;

/**
 * Interface for objects that want to register to receive calls
 * when the JVM sends output to stdout or stderr.
 */
public interface JavaConsoleListener 
	{
	
/**
 * Called when the JVM has output.
 * @param s java.lang.String
 */
void javaConsoleOutput(String s);
}