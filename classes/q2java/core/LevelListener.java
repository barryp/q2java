package q2jgame;

/**
 * Interface for objects that need to be called when a new 
 * level is starting.
 * 
 */
public interface LevelListener 
	{
	
/**
 * Called when a new level is starting, after entities have been spawned.
 */
public void levelEntitiesSpawned();
/**
 * Called when a new level is starting, before entities are spawned.
 */
public void startLevel(String mapname, String entString, String spawnPoint);
}