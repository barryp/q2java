
package q2jgame;

/**
 * Interface for classes that receive localized text messages.
 * 
 */
public interface LocaleListener 
	{
	
/**
 * Called when the listener receives a localized broadcast message.
 * @param printLevel One of the Engine.PRINT_* constants
 * @param msg java.lang.String
 */
public void localecast(int printLevel, String msg);
}