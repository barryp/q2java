package q2java;

/**
 * Interface for objects that listen to a player's events.
 *
 * @author Barry Pederson 
 */
public interface PlayerListener 
	{
	
/**
 * Called by the DLL when the player should begin playing in the game.
 * @param loadgame boolean
 */
public abstract void playerBegin();
/**
 * Called by the DLL when the player has typed, or initiated a command.
 */
public abstract void playerCommand();
/**
 * Called by the DLL when the player is disconnecting. 
 */
public abstract void playerDisconnect();
/**
 * Called by the DLL when the player's userinfo has changed.
 * @param userinfo the userinfo string, formatted as: "\keyword\value\keyword\value\....\keyword\value"
 */
public abstract void playerInfoChanged(String userinfo);
/**
 * Called by the DLL when the game should process a client frame.
 * @param cmd commands indicating movement, jumping, firing weapons, etc.
 */
public abstract void playerThink(PlayerCmd cmd);
}