package barryp.debugspawn;

import q2java.core.*;

/**
 * Gamelet that alters the game to dump debuglogs of what gets spawned.
 */
public class DebugSpawn extends Gamelet 
	{
	
/**
 * Install a new SpawnManager.
 * @param gameletName java.lang.String
 */
public DebugSpawn(String gameletName) 
	{
	super(gameletName);

	Game.setSpawnManager(new DebugSpawnManager());
	}
/**
 * Clean up after this Gamelet.
 */
public void unload() 
	{
	// Remove the Game's SpawnManager (if it's still running ours)
	SpawnManager sm = Game.getSpawnManager();
	if (sm instanceof DebugSpawnManager)
		Game.setSpawnManager(null);
	}
}