package q2java.core;

/**
 * Interface for classes that handle spawning entities when new 
 * levels start.
 *
 * @author Barry Pederson
 */
public interface SpawnManager 
	{
	
/**
 * Spawn the entities for a given map.
 * @param mapName name of the map (duh!)
 * @param entString a huge string containing the entity list defined within the map
 * @param spawnpoint name of the entity where a single-player should spawn.
 */
void spawnEntities(String mapName, String entString, String spawnpoint);
}