package q2java.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.vecmath.*;

import org.w3c.dom.*;

import q2java.*;

/**
 * Class that builds a level document by parsing
 * the "entString" passed up from the Engine.  Other implementations
 * might read from an external file or perhaps over the network.
 *
 * Contains utility methods for converting between the Quake2 style of
 * representing entity data as name/value pairs, and the fancier XML/DOM format.
 * @author Barry Pederson
 */ 
public class DefaultLevelDocumentFactory implements LevelDocumentFactory 
	{
	
/**
 * Utility method to create a DOM element representing 
 * the info for a particular map entity.
 * @return org.w3c.dom.Element
 * @param doc Document that the Element will belong to.
 * @param classname name of entity class (such as "weapon_shotgun")
 * @param params Vector contain keyword/value pairs of strings.
 */
public static Element createEntityElement(Document doc, String classname, Vector v) 
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
 * Create a DOM document representing the info for a map by 
 * parsing the entString passed to it.
 *
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */ 
public Document createLevelDocument(String mapName, String entString, String spawnpoint)
	{
	Document doc = XMLTools.createXMLDocument();

	// creat the root node, which contains the name of the map and the spawnpoint
	Element root = doc.createElement("map");
	root.setAttribute("name", mapName);
	root.setAttribute("spawnpoint", spawnpoint);
	doc.appendChild(root);

	// fill in the document
	parseEntString(doc, root, entString);
	
	return doc;
	}
/**
 * Utility method to convert the data in a map 
 * entity element back to the old-style
 * keyword/value string pairs that Quake2 and older Q2Java code uses.
 * @return java.lang.String[]
 * @param e org.w3c.dom.Element
 */
public static String[] getParamPairs(Element e) 
	{
	// build up an array of parameters like Q2Java expects
	Vector v = Q2Recycler.getVector();

	String s = e.getAttribute("spawnflags");
	if (s != null)
		{
		v.addElement("spawnflags");
		v.addElement(s);
		}
		
	NodeList nl2 = e.getChildNodes();
	for (int j = 0; j < nl2.getLength(); j++)
		{
		Node n = nl2.item(j);
		if (!(n instanceof Element))
			continue;
		Element e2 = (Element) n;

		String keyword = e2.getTagName().intern();
		String keyval = null;
		
		if (keyword.equals("origin"))
			keyval = e2.getAttribute("x") + " " + e2.getAttribute("y") + " " + e2.getAttribute("z");
		else if (keyword.equals("color") || keyword.equals("_color"))
			keyval = e2.getAttribute("r") + " " + e2.getAttribute("g") + " " + e2.getAttribute("b");
		else if (keyword.equals("target") || keyword.equals("targetname") || keyword.equals("team"))
			keyval = e2.getAttribute("id");
		else if (keyword.equals("angles"))
			keyval = e2.getAttribute("pitch") + " " + e2.getAttribute("yaw") + " " + e2.getAttribute("roll");
		else
			keyval = e2.getFirstChild().getNodeValue();
			
		v.addElement(keyword);
		v.addElement(keyval);
		}
		
	String[] params = new String[v.size()];
	v.copyInto(params);
	return params;
	}
/**
 * Actually parse the ent string and create nodes in a supplied document tree.
 *
 * @param doc a DOM document that the map entity nodes will be placed in.
 * @param root Node in the document that map entity nodes will be placed under.
 * @param entString a huge string containing the entity list defined within the map
 */ 
public static void parseEntString(Document doc, Node root, String entString)
	{
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
					root.appendChild(createEntityElement(doc, className, v));

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
}