package q2java.core;

import java.util.*;
import javax.vecmath.Point3f;

import org.w3c.dom.*;

import q2java.*;

/**
 * Handy Static methods.
 *
 * @author Barry Pederson
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
 * Get an Angle3f from a given document element.
 * @return Origin of item, or null if it can't be determined or the info in the document is invalid.
 * @param e org.w3c.dom.Element
 */
public static Angle3f getAngle3f(Element e) 
	{
	try
		{
		Angle3f p = new Angle3f();
		
		String s = e.getAttribute("pitch");
		if (s != null)
			p.x = Float.valueOf(s).floatValue();
			
		s = e.getAttribute("yaw");
		if (s != null)
			p.y = Float.valueOf(s).floatValue();
			
		s = e.getAttribute("roll");
		if (s != null)
			p.z = Float.valueOf(s).floatValue();
			
		return p;
		}
	catch (Throwable t)
		{
		// probably one or more of the
		// pitch,yaw,roll attributes was malformed
		return null;
		}	
	}
/**
 * Get an Angle3f from a sub-element of a given document element.
 * @return Origin of item, or null if it can't be determined or the info in the document is invalid.
 * @param e org.w3c.dom.Element
 * @param tagName name of sub-element containing the point information.
 */
public static Angle3f getAngle3f(Element e, String tagName) 
	{
	try
		{
		NodeList nl = e.getElementsByTagName(tagName);
		return getAngle3f((Element) nl.item(0));
		}
	catch (Throwable t)
		{
		// probably no element with the given tagname
		return null;
		}	
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
 * Get a Point3f from a given document element.
 * @return Origin of item, or null if it can't be determined or the info in the document is invalid.
 * @param e org.w3c.dom.Element
 */
public static Point3f getPoint3f(Element e) 
	{
	try
		{
		Point3f p = new Point3f();
		
		String s = e.getAttribute("x");
		if (s != null)
			p.x = Float.valueOf(s).floatValue();
			
		s = e.getAttribute("y");
		if (s != null)
			p.y = Float.valueOf(s).floatValue();
			
		s = e.getAttribute("z");
		if (s != null)
			p.z = Float.valueOf(s).floatValue();
			
		return p;
		}
	catch (Throwable t)
		{
		// either there was no origin element, or one or more of the
		// x,y,z attributes was malformed
		return null;
		}	
	}
/**
 * Get a Point3f from a sub-element of a given document element.
 * @return Origin of item, or null if it can't be determined or the info in the document is invalid.
 * @param e org.w3c.dom.Element
 * @param tagName name of sub-element containing the point information.
 */
public static Point3f getPoint3f(Element e, String tagName) 
	{
	try
		{
		NodeList nl = e.getElementsByTagName(tagName);
		
		return getPoint3f((Element) nl.item(0));
		}
	catch (Throwable t)
		{
		// probably no element with the given tagname
		return null;
		}	
	}
/**
 * Lookup a float spawn argument.
 * @return value found, or defaultValue.
 * @param e DOM element describing a particular map entity.
 * @param tagName name of sub-element we're looking for.
 * @param defaultValue value to return if "tagName" is not found.
 */
public static float getSpawnArg(Element e, String tagName, float defaultValue)
	{
	try
		{
		Element e2 = (Element)(e.getElementsByTagName(tagName).item(0));
		return Float.valueOf(e2.getFirstChild().getNodeValue()).floatValue();
		}
	catch (Throwable t)
		{
		return defaultValue;
		}
	}
/**
 * Lookup an int spawn argument.
 * @return value found, or defaultValue.
 * @param e DOM element describing a particular map entity.
 * @param tagName name of sub-element we're looking for.
 * @param defaultValue value to return if "tagName" is not found.
 */
public static int getSpawnArg(Element e, String tagName, int defaultValue)
	{
	try
		{
		Element e2 = (Element)(e.getElementsByTagName(tagName).item(0));
		return Integer.parseInt(e2.getFirstChild().getNodeValue());
		}
	catch (Throwable t)
		{
		return defaultValue;
		}
	}
/**
 * Lookup a string spawn argument.
 * @return value found, or defaultValue.
 * @param e DOM element describing a particular map entity.
 * @param tagName name of sub-element we're looking for.
 * @param defaultValue value to return if "tagName" is not found.
 */
public static String getSpawnArg(Element e, String tagName, String defaultValue)
	{
	try
		{
		Element e2 = (Element)(e.getElementsByTagName(tagName).item(0));
		return e2.getFirstChild().getNodeValue();
		}
	catch (Throwable t)
		{
		return defaultValue;
		}
	}
/**
 * Lookup a string spawn argument.
 * @return value found, or defaultValue.
 * @param e DOM element describing a particular map entity.
 * @param tagName name of sub-element we're looking for.
 * @param defaultValue value to return if "tagName" is not found.
 */
public static String getSpawnArg(Element e, String tagName, String attributeName, String defaultValue)
	{
	try
		{
		Element e2 = (Element)(e.getElementsByTagName(tagName).item(0));
		String s = e2.getAttribute(attributeName);
		if (s == null)
			return defaultValue;
		else
			return s;
		}
	catch (Throwable t)
		{
		return defaultValue;
		}
	}
/**
 * Get the spawn flags for a map element.
 * @return value found, or 0 if not found.
 * @param e DOM element describing a particular map entity.
 */
public static int getSpawnFlags(Element e)
	{
	try
		{
		return Integer.parseInt(e.getAttribute("spawnflags"));
		}
	catch (Throwable t)
		{
		return 0;
		}
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
/**
 * Get a positive random integer less than a given value. 
 *
 * @return random int >=0 and < max.
 * @param max upper bound for returned values (exclusive).
 */
public static int randomInt(int max) 
	{
	return (gRandom.nextInt() & 0x7fffffff) % max;
	}
/**
 * Get a random integer within a given range. 
 * (Max - min) should be less than (Integer.MAX_VALUE / 2), but
 * that's probably not going to be a problem.
 *
 * @return random int in the range min..max (inclusive).
 * @param min smallest integer you ever want returned.
 * @param max largest integer you ever want returned.
 */
public static int randomInt(int min, int max) 
	{
	return  ((gRandom.nextInt() & 0x7fffffff) % ((max - min) + 1)) + min;
	}
/**
 * Sends a command to the clients console
 * @author Peter Donald 24/1/99  
 */
public static void stuffCommand(NativeEntity ent, String command)
	{
	if (ent.isPlayer() && !ent.isBot())
	    {
	    Engine.writeByte(Engine.SVC_STUFFTEXT);
	    Engine.writeString(command);
	    Engine.unicast(ent, true);	  
	    }	
	}
}