package q2java.core;

import org.w3c.dom.Document;
/**
 * Interface for classes build a DOM Document representing the info 
 * for a given level.
 *
 * @author Barry Pederson
 */
public interface LevelDocumentFactory 
	{
	
/**
 * Build a DOM document representing the initial info for a new map.
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */
Document createLevelDocument(String mapName, String entString, String spawnpoint);
}