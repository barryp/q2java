package q2java.core;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import q2java.*;

/**
 * Class that handles spawning entities on map changes by parsing
 * the "entString" passed up from the Engine.  Other implementations
 * might read from an external file or perhaps over the network.
 *
 * @author Barry Pederson
 */ 
public class DefaultSpawnManager implements SpawnManager 
	{
	// fields used repeatedly in constructing entities through reflection
	protected Object[] fParams;
	protected Class[] fParamTypes;
	protected String[] fSA;
	
/**
 * Setup some fields for use with reflection.
 */
public DefaultSpawnManager() 
	{
	fParams = new Object[1];
	fParamTypes = new Class[1];
	fSA = new String[1];
	fParamTypes[0] = fSA.getClass();
	}
/**
 * Spawn a particular entity.
 * @param classname java.lang.String
 * @param params Vector containing pairs of strings - keywords and values.
 * @return Object spawned, or exception caught.
 */
protected Object spawn(String className, String[] params) 
	{
	fParams[0] = params;
	try
		{
		Class entClass = Game.lookupClass(".spawn." + className.toLowerCase());
		Constructor ctor = entClass.getConstructor(fParamTypes);							
		Object obj = ctor.newInstance(fParams);
		return obj;
		}
	catch (Throwable t)
		{
		return t;
		}		
	}
/**
 * Spawn entities into the Quake II environment.
 * This methods parses the entString passed to it, and looks
 * for Java classnames equivalent to the classnames specified 
 * in the entString, and instantiates instances of them, with
 * the entity parameters passed as an array of Strings.
 *
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */ 
public void spawnEntities(String mapName, String entString, String spawnpoint)
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
					// create that sucker
					String[] sa = new String[v.size()];
					v.copyInto(sa);
					result = spawn(className, sa);

					// Print exceptions 
					//   except for ClassNotFoundException 
					//   and InhibitedException
					if ((result instanceof Throwable) 
					&& (!(result instanceof ClassNotFoundException)))
						{
						if (result instanceof InvocationTargetException)
							result = ((InvocationTargetException)result).getTargetException();
							
						if (!(result instanceof InhibitedException))
							((Throwable)result).printStackTrace();							
						}

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