package q2java;

/**
 * Methods that -must- be implemented by a Quake II Java game.
 *
 * @author Barry Pederson 
 */
public interface GameListener
	{
	
/**
 * This method was created by a SmartGuide.
 * @param s java.lang.String
 */
public void consoleOutput(String s);
/**
 * Let the DLL know what class (descended from q2java.NativeEntity)
 * to use for new players.
 * @return java.lang.Class
 */
public Class getPlayerClass() throws ClassNotFoundException;
public void init();
/**
 * Called by the DLL when a new player connects. 
 * Throw an exception to reject the connection.
 * @param playerEntity instance of NativeEntity (or a subclass of NativeEntity) that represents the player.
 * @param userinfo Player's basic info
 * @param loadgame boolean
 */
public void playerConnect(NativeEntity playerEntity, boolean loadgame) throws Exception, Throwable;
public void readGame(String filename);
public void readLevel(String filename);
public void runFrame();
/**
 * This method was created by a SmartGuide.
 */
public void serverCommand();
public void shutdown();
public void startLevel(String mapname, String entString, String spawnPoint);
public void writeGame(String filename);
public void writeLevel(String filename);
}