package barryp.map;

import q2java.core.*;

/**
 * Gamelet that reads/writes map entity info as XML files in sandbox directory.
 */
public class XMLMaps extends Gamelet 
	{
	private XMLLevelDocumentFactory fLDF;
	
/**
 * Create the Gamelet.
 * @param gameletName java.lang.String
 */
public XMLMaps(String gameletName) 
	{
	super(gameletName);
	}
/**
 * Actually initialize the Gamelet for action.
 */
public void init() 
	{
	fLDF = new XMLLevelDocumentFactory();
	Game.setLevelDocumentFactory(fLDF);	
	}
/**
 * Clean up after this Gamelet.
 */
public void unload() 
	{
	// Remove the Game's SpawnManager (if it's still running ours)
	if (Game.getLevelDocumentFactory() == fLDF)
		Game.setLevelDocumentFactory(null);

	// fuhgedabout
	fLDF = null;
	}
}