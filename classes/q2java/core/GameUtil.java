package q2java.core;

import java.io.*;
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
 * Build a Q2Java Level document.
 * @return org.w3c.dom.Document
 * @param entString huge string defining map entities, usually embedded
 *  inside the .bsp files - if null then a mostly empty document is created.
 */
public static Document buildLevelDocument(String mapname, String entString, String spawnPoint) 
	{
	// create the inital, mostly empty level document
	Document doc = XMLTools.createXMLDocument("map");
	
	Element root = doc.getDocumentElement();
	root.setAttribute("name", mapname);
	root.setAttribute("spawnpoint", spawnPoint);

	if (entString != null)
		parseEntString(root, entString);
		
	return doc;
	}
/**
 * @return A random number between -1.0 and +1.0
 */
public static float cRandom() 
	{
	return (float)((gRandom.nextFloat() - 0.5) * 2.0);
	}
/**
 * Utility method to create a DOM element representing 
 * the info for a particular map entity.
 * @return org.w3c.dom.Element
 * @param doc Document that the Element will belong to.
 * @param classname name of entity class (such as "weapon_shotgun")
 * @param params Vector contain keyword/value pairs of strings.
 */
private static Element createEntityElement(Document doc, String classname, Vector v) 
	{
	Element e = doc.createElement("entity");
	e.setAttribute("class", classname);
	
	// set properties for the element
	Enumeration enum = v.elements();
	while (enum.hasMoreElements())
		{
		String keyword = (String) enum.nextElement();
		
		if (!(enum.hasMoreElements()))
			continue;
			
		String keyval = (String) enum.nextElement();

		// handle special cases
		
		// create tag: <angles pitch="0" yaw="..." roll="0"/>
		if (keyword.equals("angle"))
			{
			Element e2 = doc.createElement("angles");
			e2.setAttribute("pitch", "0");
			e2.setAttribute("yaw", keyval);
			e2.setAttribute("roll", "0");
			e.appendChild(e2);
			continue;
			}

		// create tag: <angles pitch="..." yaw="..." roll="..."/>
		if (keyword.equals("angles"))
			{
			Angle3f ang = GameUtil.parseAngle3f(keyval);
			Element e2 = doc.createElement("angles");
			e2.setAttribute("pitch", Float.toString(ang.x));
			e2.setAttribute("yaw", Float.toString(ang.y));
			e2.setAttribute("roll", Float.toString(ang.z));
			e.appendChild(e2);
			continue;
			}

		// create tag: <origin x="..." y="..." z="..."/>
		if (keyword.equals("origin"))
			{
			Point3f pt = GameUtil.parsePoint3f(keyval);
			Element e2 = doc.createElement("origin");
			e2.setAttribute("x", Float.toString(pt.x));
			e2.setAttribute("y", Float.toString(pt.y));
			e2.setAttribute("z", Float.toString(pt.z));
			e.appendChild(e2);
			continue;			
			}

		// create tag: <[_]color r="..." g="..." b="..."/>
		if (keyword.equals("color") || keyword.equals("_color"))
			{
			Point3f pt = GameUtil.parsePoint3f(keyval);
			Element e2 = doc.createElement(keyword);
			e2.setAttribute("r", Float.toString(pt.x));
			e2.setAttribute("g", Float.toString(pt.y));
			e2.setAttribute("b", Float.toString(pt.z));
			e.appendChild(e2);
			continue;
			}

		// create tag: <[target|targetname|team] id="..."/>
		if (keyword.equals("targetname") 
		|| keyword.equals("target")
		|| keyword.equals("team"))
			{
			Element e2 = doc.createElement(keyword);
			e2.setAttribute("id", keyval);
			e.appendChild(e2);
			continue;
			}

		// add attribute: spawnflags="..." to entity element
		if (keyword.equals("spawnflags"))
			{
			e.setAttribute("spawnflags", keyval);
			continue;
			}

		// handle unknown properties by creating tag
		//    <keyword>keyval</keyword>
		Element e2 = doc.createElement(keyword);
		e2.appendChild(doc.createTextNode(keyval));				
		e.appendChild(e2);
		}

	return e;
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
 * Parse an Angle3f from the standard map format of "<pitch> <yaw> <roll>".
 * @return javax.vecmath.Tuple3f
 * @param s java.lang.String
 */
public static Angle3f parseAngle3f(String s) 
	{
	StringTokenizer st = new StringTokenizer(s, "(, )");
	if (st.countTokens() != 3)
		throw new NumberFormatException("Not a valid format for Angle3f");

	float x = Float.valueOf(st.nextToken()).floatValue();
	float y = Float.valueOf(st.nextToken()).floatValue();
	float z = Float.valueOf(st.nextToken()).floatValue();
	
	return new Angle3f(x, y, z);
	}
/**
 * Parse the entities in a Q2-style entString and place them 
 * inside a DOM document
 *
 * @param dest DOM Element the map entities should be placed under
 * @param entString huge string defining map entities, usually embedded
 *  inside the .bsp files.
 */
public static void parseEntString(Element dest, String entString) 
	{
	Document doc = dest.getOwnerDocument();

	try
		{
		StringReader sr = new StringReader(entString);
		StreamTokenizer st = new StreamTokenizer(sr);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		int token;
		Vector v = new Vector(16);
		boolean foundClassname = false;
		String className = null;
		Object result;

		while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
			{
			switch (token)
				{
				case '"' : 
					if (foundClassname)
						{
						className = st.sval;
						foundClassname = false;
						break;
						}
					if (st.sval.equalsIgnoreCase("classname"))
						{
						foundClassname = true;
						break;
						}
					v.addElement(st.sval.intern()); 
					break;

				case '{' : 
					foundClassname = false; 
					break;

				case '}' :
					// Create a new element and add to document root node
					dest.appendChild(createEntityElement(doc, className, v));

					// get ready for next entity						
					v.removeAllElements();
					foundClassname = false;
					className = null;
					break;

				default  : 
					foundClassname = false;				
				}
			}
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
/**
 * This method was created by a SmartGuide.
 * @return javax.vecmath.Tuple3f
 * @param s java.lang.String
 */
public static Point3f parsePoint3f(String s) 
	{
	StringTokenizer st = new StringTokenizer(s, "(, )");
	if (st.countTokens() != 3)
		throw new NumberFormatException("Not a valid format for Point3f");

	float x = Float.valueOf(st.nextToken()).floatValue();
	float y = Float.valueOf(st.nextToken()).floatValue();
	float z = Float.valueOf(st.nextToken()).floatValue();
	
	return new Point3f(x, y, z);
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