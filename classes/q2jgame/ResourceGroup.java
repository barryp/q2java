
package q2jgame;

import java.text.MessageFormat;
import java.util.*;

/**
 * Track ResourceBundles using a given locale, along with 
 * BroadcastListeners interested in that locale.
 */
public class ResourceGroup 
	{
	protected final static int HASHTABLE_SIZE = 11;
	
	protected Locale fLocale;
	protected Vector fListeners;
	protected Hashtable fBundles;	
	
/**
 * Create a ResourceGroup handling a given locale.
 * @param localeName java.lang.String
 */
ResourceGroup(Locale loc) 
	{
	fLocale = loc;
	fListeners = new Vector();
	fBundles = new Hashtable(HASHTABLE_SIZE);
	}
/**
 * Add an object that wants to receive broadcasts that are localized.
 * @param obj q2jgame.LocaleListener
 */
void addLocaleListener(LocaleListener obj) 
	{
	if (!fListeners.contains(obj))
		fListeners.addElement(obj);
	}
/**
 * Check whether this group handles the specified locale.
 * @return boolean
 * @param loc java.util.Locale
 */
public boolean equalsLocale(Locale loc) 
	{
	return fLocale.equals(loc);
	}
/**
 * Generate a formatted message for this locale.
 * @return java.lang.String
 * @param baseName java.lang.String
 * @param key java.lang.String
 * @param args java.lang.Object[]
 */
public String format(String baseName, String key, Object[] args) 
	{
	String pattern = getRandomString(baseName, key);
	MessageFormat mf = new MessageFormat(pattern);
	mf.setLocale(fLocale);
	return mf.format(args);
	}
/**
 * Fetch a ResourceBundle that belongs to this locale.
 * @return java.util.ResourceBundle
 * @param baseName java.lang.String
 */
public ResourceBundle getBundle(String baseName) 
	{
	ResourceBundle result = (ResourceBundle) fBundles.get(baseName);
	if (result != null)
		return result;
		
	// bundle wasn't cached, so load it, cache it, and return it.
	result = ResourceBundle.getBundle(baseName, fLocale);
	fBundles.put(baseName, result);
	return result;		
	}
/**
 * Get the locale this ResourceGroup manages.
 * @return java.util.Locale
 */
public Locale getLocale() 
	{
	return fLocale;
	}
/**
 * Get a resource string, if the resource is an array of strings, select one randomly.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 */
public String getRandomString(String basename, String key) 
	{
	ResourceBundle bundle = getBundle(basename);
	Object obj = bundle.getObject(key);
		
	// is the resource a single string
	if (obj instanceof String)
		return (String) obj;
			
	// if it's an array of strings, pick one at random			
	if (obj instanceof String[])
		{
		String[] sa = (String[]) obj;
		int choice = (Game.randomInt() & 0x0fff) % sa.length;
		return sa[choice];
		}
		
	// punt		
	return obj.toString();						
	}
/**
 * Broadcast a localized message to registered listeners.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 * @param args args to pass to java.text.MessageFormat.format().
 * @param printLevel One of the Engine.PRINT_* constants.
 */
void localecast(String basename, String key, Object[] args, int printLevel) 
	{
	try
		{
		String pattern = getRandomString(basename, key);
		MessageFormat mf = new MessageFormat(pattern);
		mf.setLocale(fLocale);
		String msg = mf.format(args) + "\n";
		for (int i = 0; i < fListeners.size(); i++)
			((LocaleListener) fListeners.elementAt(i)).localecast(printLevel, msg);
		}
	catch (Exception e)
		{
		System.out.println(e + " " + basename + " " + key);
		}
	}
/**
 * Broadcast a localized message to registered listeners.
 * @param basename ResourceBundle basename, same as what you'd pass to java.util.ResourceBundle.getBundle().
 * @param key Name of ResourceBundle object, same as what you'd pass to java.util.ResourceBundle.getString().
 * @param printLevel One of the Engine.PRINT_* constants.
 */
void localecast(String basename, String key, int printLevel) 
	{
	try
		{
		String msg = getRandomString(basename, key) + "\n";
		for (int i = 0; i < fListeners.size(); i++)
			((LocaleListener) fListeners.elementAt(i)).localecast(printLevel, msg);
		}
	catch (Exception e)
		{
		System.out.println(e + " " + basename + " " + key);
		}
	}
/**
 * Remove a locale listener from the game.
 * @param obj q2jgame.LocaleListener
 */
void removeLocaleListener(LocaleListener obj) 
	{
	fListeners.removeElement(obj);
	
	// if all the listeners have dropped, free the 
	// ResourceBundles so they can be GC'ed
	if (fListeners.size() == 0)
		fBundles.clear();
	}
}