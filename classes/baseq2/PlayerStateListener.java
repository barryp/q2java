package baseq2;

/**
 * Interface for classes that want to be notified when Players die (or disconnect).
 */
public interface PlayerStateListener 
	{
	public final static int PLAYER_DIED			= 1;
	public final static int PLAYER_LEVELCHANGE	= 2;
	public final static int PLAYER_DISCONNECT	= 3;
	
/**
 * Called when a player dies or disconnects.
 * @param p - the player we're squealing on.
 * @param changeEvent - what has changed, one of the PLAYER_ constants.
 */
public void playerStateChanged(Player p, int changeEvent);
}