package barryp.map;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.w3c.dom.*;

import q2java.Engine;
import q2java.core.*;

/**
 * Extend the DefaultSpawnManager to read/write an XML version
 * of the map spawning info in the sandbox, as a file named
 * "&lt;mapname&gt;.xml"
 *
 * @author Barry Pederson
 */ 
public class XMLLevelDocumentFactory implements LevelDocumentFactory
	{
	
/**
 * Print what we're spawning.
 *
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */ 
public Document createLevelDocument(String mapName, String entString, String spawnpoint)
	{	
	Document doc = null;
	
	try
		{
		// read the XML file
		File mapdir = new File(Engine.getGamePath(), "maps");
		File xmlFile = new File(mapdir, mapName + ".xml");		
		FileReader fr = new FileReader(xmlFile);
		doc = XMLTools.readXMLDocument(fr, "");
		fr.close();
		}
	catch (Exception e)
		{
		boolean createFile = false;
		
		if (e instanceof FileNotFoundException)
			createFile = true;
		else
			e.printStackTrace();
			
		// Build the document the old-fashioned way
		LevelDocumentFactory ldf = new DefaultLevelDocumentFactory();		
		doc = ldf.createLevelDocument(mapName, entString, spawnpoint);
		
		// Save the document we built up in the sandbox - if the file 
		// wasn't found in the maps directory
		if (createFile)
			{
			try
				{
				File sandbox = new File(Engine.getGamePath(), "sandbox");
				File xmlFile = new File(sandbox, mapName + ".xml");
				FileWriter fw = new FileWriter(xmlFile);
				XMLTools.writeXMLDocument(doc, fw);
				fw.close();
				}
			catch (IOException ioe)
				{
				ioe.printStackTrace();
				}
			}
		}

	return doc;
	}
}