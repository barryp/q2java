
package q2java;

public interface NativeGame
	{
	
public void init();
public void readGame(String filename);
public void readLevel(String filename);
public void runFrame();
/**
 * This method was created by a SmartGuide.
 */
public void serverCommand();
public void shutdown();
public void spawnEntities(String mapname, String entString, String spawnPoint);
public void writeGame(String filename);
public void writeLevel(String filename);
}