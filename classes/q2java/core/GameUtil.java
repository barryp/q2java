package q2java.core;

import java.util.*;

/**
 * Handy Static methods.
 */
public class GameUtil 
	{
	// handy random number generator
	private static Random gRandom = new Random();		
	
/**
 * @return A random number between -1.0 and +1.0
 */
public static float cRandom() 
	{
	return (float)((gRandom.nextFloat() - 0.5) * 2.0);
	}
/**
 * Get a Locale object given a name.
 * (no idea why the Locale class didn't do this itself)
 *
 * @return java.util.Locale
 * @param localeName java.lang.String
 */
public static Locale getLocale(String localeName) 
	{
	if ((localeName == null) || localeName.equalsIgnoreCase("default"))
		return Locale.getDefault();

	StringTokenizer st = new StringTokenizer(localeName, "_");
	String lang = st.nextToken();
	String country;
			
	if (st.hasMoreTokens())
		country = st.nextToken();
	else 
		country = "";
			
	if (st.hasMoreTokens())
		return new Locale(lang, country, st.nextToken());
	else
		return new Locale(lang, country);
	}
/**
 * Lookup an float spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found or isn't a valid float
 */
public static float getSpawnArg(String[] args, String keyword, float defaultValue) 
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		{
		if (keyword == args[i])
			{
			try
				{
				float result = Float.valueOf(args[i+1]).floatValue();
				return result;
				}
			catch (NumberFormatException nfe)
				{
				}
			}
		}
		
	return defaultValue;
	}
/**
 * Lookup an integer spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found or isn't a valid integer
 */
public static int getSpawnArg(String[] args, String keyword, int defaultValue) 
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		{
		if (keyword == args[i])
			{
			try
				{
				int result = Integer.parseInt(args[i+1]);
				return result;
				}
			catch (NumberFormatException nfe)
				{
				}
			}
		}

	return defaultValue;
	}
/**
 * Lookup a string spawn argument.
 * @return value found, or defaultValue.
 * @param args String array holding the map's entity arguments, as created by the spawnEntities() method.
 * @param name name of spawn argument.
 * @param defaultValue value to return if "name" is not found.
 */
public static String getSpawnArg(String[] args, String keyword, String defaultValue)
	{
	if (args == null)
		return defaultValue;

	keyword = keyword.intern();
	for (int i = 0; i < args.length; i+=2)
		{
		if (keyword == args[i])
			return args[i+1];
		}
			
	return defaultValue;
	}
/**
 * Return A random float between 0.0 and 1.0.
 */

public static float randomFloat() 
	{
	return gRandom.nextFloat();
	}
/**
 * Get a random integer, values are distributed across 
 * the full range of the signed 32-bit integer type.
 * @return A random integer.
 */
public static int randomInt() 
	{
	return gRandom.nextInt();
	}
}