package barryp.debugspawn;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import q2java.*;
import q2java.core.*;

/**
 * Extend the DefaultSpawnManager to also print a log of what was or
 * wasn't spawned to the sandbox.
 *
 * @author Barry Pederson
 */ 
public class DebugSpawnManager extends DefaultSpawnManager
	{
	protected PrintWriter fPW;
	
/**
 * Spawn a particular entity and print some stuff.
 * @param classname java.lang.String
 * @param params java.util.Vector
 */
protected Object spawn(String className, String[] params) 
	{
	Object obj = super.spawn(className, params);
	
	// Print some stuff if possible
	if (fPW != null)
		{
		// use a second reference, that we can replace if necessary
		Object obj2 = obj;
		
		if (obj2 instanceof InvocationTargetException)
			obj2 = ((InvocationTargetException)obj2).getTargetException();
			
		if ((obj2 instanceof ClassNotFoundException) || (obj instanceof InhibitedException))
			{
			if (obj instanceof InhibitedException)
				fPW.print("Inhibited " + className + "(");
			else				
				fPW.print("Not Found " + className + "(");
				
			if (params != null)
				{
				String prefix = "";
				for (int i = 0; i < params.length; i+=2)
					{
					fPW.print(prefix + params[i] + "=\"" + params[i+1] + "\"");
					prefix = ", ";
					}
				}
			fPW.println(")");		
			}
		else if (obj2 instanceof Throwable)
			((Throwable)obj).printStackTrace(fPW);
		else
			fPW.print(obj);
		}
		
	return obj;
	}
/**
 * Print what we're spawning.
 *
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */ 
public void spawnEntities(String mapName, String entString, String spawnpoint)
	{
	// setup the output file
	try
		{
		File sandbox = new File(Engine.getGamePath(), "sandbox");
		File entFile = new File(sandbox, "entities.log");
		FileWriter fw = new FileWriter(entFile);
		fPW = new PrintWriter(fw);	
	
		fPW.println(entString);
		fPW.println("--------------------------");
		}
	catch (IOException ioe)
		{
		fPW = null;
		ioe.printStackTrace();
		}

	// actually spawn the entities
	super.spawnEntities(mapName, entString, spawnpoint);

	// finish things up
	if (fPW != null)
		fPW.close();			
	}
}