package barryp.map;

import java.io.*;
import java.util.Date;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Gamelet that reads/writes map entity info as XML files in sandbox directory.
 */
public class XMLMaps extends Gamelet implements GameStatusListener
	{
	protected String fSuffix = ".xml";
	protected boolean fMakeXML = false;
	
/**
 * Create the Gamelet.
 * @param gameletName java.lang.String
 */
public XMLMaps(Document gameletInfo) 
	{
	super(gameletInfo);
	
	Game.addGameStatusListener(this);
	}
/**
 * Called when the status of the game changes.
 * @param gse q2java.core.event.GameStatusEvent
 */
public void gameStatusChanged(GameStatusEvent gse) 
	{
	// we're only interested in one particular event
	if (gse.getState() != GameStatusEvent.GAME_BUILD_LEVEL_DOCUMENT)
		return;

	String mapName = Game.getCurrentMapName();
	
	try
		{
		// read the XML file
		File mapdir = new File(Engine.getGamePath(), "maps");
		File xmlFile = new File(mapdir, mapName + fSuffix);		
		FileReader fr = new FileReader(xmlFile);
		Document doc = XMLTools.readXMLDocument(fr, "");
		fr.close();

		// merge it into the Game's existing level document
		Element masterRoot = Game.getDocument("q2java.level").getDocumentElement();
		Element fileRoot = doc.getDocumentElement();
		XMLTools.copy(fileRoot, masterRoot, true);
		}
	catch (Exception e)
		{
		// FileNotFound, or invalid XML probably
		}

	// build XML samples if desired
	if (fMakeXML)
		{
		try
			{
			// Build a DOM document based on the info embedded in the BSP file
			Document doc = GameUtil.buildLevelDocument(mapName, gse.getMapEntities(), null);

			// slip a comment into the beginning of the file, so
			// we know a bit about where it came from.
			Date now = new Date();
			Comment cmt = doc.createComment("Created by barryp.map.XMLMaps " + now);
			Node root = doc.getDocumentElement();
			doc.insertBefore(cmt, root);

			// write it out 
			File sandbox = new File(Engine.getGamePath(), "sandbox");
			File xmlFile = new File(sandbox, mapName + fSuffix);
			FileWriter fw = new FileWriter(xmlFile);
			XMLTools.writeXMLDocument(doc, fw);
			fw.close();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}		
	}
/**
 * View/set the suffix used by this gamelet when reading/writing XML files.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint("    sv commands:\n");
	Game.dprint("       makexml [yes|no]\n");
	Game.dprint("       suffix [<new-suffix>]\n");
	}
/**
 * View/set whether or not the gamelet will generate XML files based on the 
 * info embedded in the .bsp files.
 *
 * @param args java.lang.String[]
 */
public void svcmd_makexml(String[] args) 
	{
	if (args.length > 2)
		fMakeXML = args[2].equalsIgnoreCase("yes");

	if (fMakeXML)
		Game.dprint("XML sample files will be created in the sandbox directory\n");
	else
		Game.dprint("XML sample files will not be created\n");
	}
/**
 * View/set the suffix used by this gamelet when reading/writing XML files.
 * @param args java.lang.String[]
 */
public void svcmd_suffix(String[] args) 
	{
	if (args.length > 2)
		fSuffix = args[2];
		
	Game.dprint("Suffix = [" + fSuffix + "]\n");
	}
/**
 * Clean up after this Gamelet.
 */
public void unload() 
	{
	Game.removeGameStatusListener(this);
	}
}