
package q2jgame;

/**
 * Interface for classes that want to be informed of the overall
 * status of the game.
 * 
 */
public interface GameStatusListener 
	{
	
/**
 * Called when the DLL's ReadGame() function is called.
 */
public void readGame(String filename);

/**
 * Called when the DLL's ReadLevel() function is called.
 */
public void readLevel(String filename);
/**
 * Called when the DLL's Shutdown() function is called.
 */
public void shutdown();

/**
 * Called when the DLL's WriteGame() function is called.
 */
public void writeGame(String filename);
/**
 * Called when the DLL's WriteLevel() function is called.
 */
public void writeLevel(String filename);
}