package q2java.core;

import java.text.MessageFormat;
import java.util.*;

/**
 * Cache ResourceBundles that use a given locale.
 *
 * @author Barry Pederson
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
		int choice = (GameUtil.randomInt() & 0x0fff) % sa.length;
		return sa[choice];
		}
		
	// punt		
	return obj.toString();						
	}
}