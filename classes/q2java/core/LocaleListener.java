package q2java.core;

import java.util.Locale;
/**
 * Interface for classes that receive localized text messages.
 *
 * @author Barry Pederson
 */
public interface LocaleListener 
	{
	
/**
 * Called when the listener receives a localized broadcast message.
 * @param loc Locale this message has been formatted for.
 * @param printLevel One of the Engine.PRINT_* constants
 * @param msg java.lang.String
 */
public void localecast(Locale loc, int printLevel, String msg);
}